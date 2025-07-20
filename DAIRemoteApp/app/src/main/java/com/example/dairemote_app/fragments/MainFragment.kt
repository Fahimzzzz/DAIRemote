package com.example.dairemote_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dairemote_app.R
import com.example.dairemote_app.databinding.FragmentMainBinding
import com.example.dairemote_app.utils.ConnectionManager
import com.example.dairemote_app.utils.TutorialMediator
import com.example.dairemote_app.viewmodels.ConnectionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConnectionViewModel by activityViewModels()
    private val tutorial by lazy {
        TutorialMediator.GetInstance(AlertDialog.Builder(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackPressHandler()

        // Handle click listeners
        binding.DAIRemoteLogoBtn.setOnClickListener {
            val isConnected = viewModel.connectionState.value ?: false
            if (!isConnected) {
                animateConnectionButton(it)
                attemptConnection()
            } else {
                findNavController().navigate(R.id.action_to_interaction)
            }
        }

        binding.helpButton.setOnClickListener {
            tutorial?.let {
                it.setCurrentStep(0)
                it.showSteps(it.getCurrentStep())
            }
        }
    }

    private fun animateConnectionButton(view: View) {
        view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(150)
            .withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
            }.start()
    }

    private fun attemptConnection() {
        binding.connectionLoading.visibility = View.VISIBLE
        viewModel.searchForHosts().observe(viewLifecycleOwner) { result ->
            binding.connectionLoading.visibility = View.GONE

            when (result) {
                is ConnectionViewModel.HostSearchResult.Success -> {
                    if (result.hosts.isNotEmpty()) {
                        // Take the first host found
                        val selectedHost = result.hosts[0]
                        viewModel.connectionManager = ConnectionManager(selectedHost)

                        viewModel.connectionManager.let { manager ->
                            // Move connection initialization to a background thread
                            CoroutineScope(Dispatchers.IO).launch {
                                val connectionResult = manager?.initializeConnection()

                                // Switch back to main thread for UI updates
                                withContext(Dispatchers.Main) {
                                    if (connectionResult != null) {
                                        viewModel.updateConnectionState(connectionResult)
                                    }
                                    if (connectionResult != null) {
                                        manager.setConnectionEstablished(connectionResult)
                                    }
                                    if (connectionResult == true) {
                                        // Connection successful
                                        notifyUser("Connection approved")
                                        // Ensure fragment is still attached before navigating
                                        if (isAdded && !isDetached) {
                                            findNavController().navigate(R.id.action_to_interaction)
                                        }
                                    } else {
                                        // Connection failed
                                        notifyUser("Denied connection")
                                        manager?.resetConnectionManager()
                                    }
                                }
                            }
                        }
                    } else {
                        notifyUser("No hosts found")
                    }
                }

                is ConnectionViewModel.HostSearchResult.Error -> {
                    notifyUser(result.message)
                }
            }
        }
    }

    private fun notifyUser(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setupBackPressHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)

                when {
                    drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                }
            }
        }

        // Add the callback to the activity's back press dispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
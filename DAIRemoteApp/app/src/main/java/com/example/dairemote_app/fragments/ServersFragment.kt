package com.example.dairemote_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.dairemote_app.HostSearchCallback
import com.example.dairemote_app.R
import com.example.dairemote_app.databinding.FragmentServersBinding
import com.example.dairemote_app.utils.ConnectionManager
import com.example.dairemote_app.viewmodels.ConnectionViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ServersFragment : Fragment() {
    private var _binding: FragmentServersBinding? = null
    private val binding get() = _binding!!

    private val availableHosts = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    lateinit var addServer: FloatingActionButton
    private lateinit var executor: ExecutorService
    private lateinit var connectionProgress: ProgressBar
    private lateinit var viewModel: ConnectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity())[ConnectionViewModel::class.java]

        setupViews()
        setupListeners()
        searchHosts()
    }

    private fun setupViews() {
        adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, availableHosts)
        binding.hostList.adapter = adapter
        executor = Executors.newSingleThreadExecutor()

        connectionProgress = binding.connectionLoading
    }

    private fun setupListeners() {
        binding.hostList.setOnItemClickListener { _, _, position, _ ->
            attemptConnection(availableHosts[position])
        }

        binding.addServerBtn.setOnClickListener {
            showAddServerDialog()
        }
    }

    private fun searchHosts() {
        ConnectionManager.hostSearchInBackground(object : HostSearchCallback {
            override fun onHostFound(hosts: List<String>) {
                availableHosts.clear()
                availableHosts.addAll(hosts)
                requireActivity().runOnUiThread { adapter.notifyDataSetChanged() }
            }

            override fun onError(error: String) {
                notifyUser(error)
            }
        }, "Hello, DAIRemote")
    }

    private fun showAddServerDialog() {
        val inputField = EditText(requireContext()).apply {
            hint = "Enter IP Address here"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add your server host here:")
            .setView(inputField)
            .setPositiveButton("Connect") { _, _ ->
                val host = inputField.text.toString().trim()
                if (host.isNotEmpty()) {
                    attemptConnection(host)
                } else {
                    notifyUser("Server IP cannot be empty")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun notifyUser(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun initiateInteractionPage(message: String) {
        notifyUser(message)
        // Navigate to InteractionPage fragment or activity
        findNavController().navigate(R.id.action_to_interaction)
    }

    private fun priorConnectionEstablishedCheck(host: String): Boolean {
        viewModel.connectionManager?.let { manager ->
            if (manager.getConnectionEstablished()) {
                // Safe to call getServerAddress() only if connection is established
                if (host != ConnectionManager.getServerAddress()) {
                    // Stop the current connection before attempting a new one
                    manager.shutdown()
                } else {
                    initiateInteractionPage("Already connected")
                }
                return true
            }
        }
        return false
    }

    private fun attemptConnection(server: String) {
        requireActivity().runOnUiThread { connectionProgress.visibility = View.VISIBLE }

        if (!priorConnectionEstablishedCheck(server)) {
            viewModel.connectionManager = ConnectionManager(server)
            val manager = viewModel.connectionManager ?: run {
                requireActivity().runOnUiThread {
                    connectionProgress.visibility = View.GONE
                    notifyUser("Connection manager initialization failed")
                }
                return
            }

            if (!executor.isShutdown) {
                executor.execute {
                    if (manager.initializeConnection()) {
                        requireActivity().runOnUiThread {
                            connectionProgress.visibility = View.GONE
                            initiateInteractionPage("Connected to: $server")
                        }
                    } else {
                        requireActivity().runOnUiThread {
                            connectionProgress.visibility = View.GONE
                            notifyUser("Connection failed")
                        }
                        manager.resetConnectionManager()
                    }
                    executor.shutdownNow()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (!executor.isShutdown) {
            executor.shutdownNow()
        }
    }
}
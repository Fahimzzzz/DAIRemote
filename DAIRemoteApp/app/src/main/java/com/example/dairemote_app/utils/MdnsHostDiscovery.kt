package com.example.dairemote_app.utils

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.jmdns.JmDNS
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener

class MdnsHostDiscovery(private val context: Context) {
    private var jmDNS: JmDNS? = null
    private val discoveredHosts = mutableListOf<String>()

    fun startDiscovery(callback: (List<String>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val wifiManager =
                    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val lock = wifiManager.createMulticastLock("mDNSLock")
                lock.acquire()

                jmDNS = JmDNS.create()
                jmDNS?.addServiceListener("_daidesktop._tcp.local.", object : ServiceListener {
                    override fun serviceAdded(event: ServiceEvent) {
                        // Resolve the service to get details
                        jmDNS?.requestServiceInfo(event.type, event.name, true)
                    }

                    override fun serviceRemoved(event: ServiceEvent) {
                        discoveredHosts.remove(event.info.hostAddresses.firstOrNull())
                        callback(discoveredHosts.toList())
                    }

                    override fun serviceResolved(event: ServiceEvent) {
                        val host = event.info.hostAddresses.firstOrNull()
                        if (host != null && !discoveredHosts.contains(host)) {
                            discoveredHosts.add(host)
                            callback(discoveredHosts.toList())
                        }
                    }
                })
            } catch (e: Exception) {
                Log.e("MdnsDiscovery", "Error in mDNS discovery", e)
            }
        }
    }

    fun stopDiscovery() {
        jmDNS?.close()
        jmDNS = null
    }
}
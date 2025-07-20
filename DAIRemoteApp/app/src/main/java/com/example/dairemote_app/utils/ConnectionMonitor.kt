package com.example.dairemote_app.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val HEARTBEAT_INTERVAL_MS = 2000L
private const val HEARTBEAT_TIMEOUT_MS: Int = 3000
private const val MAX_MISSED_HEARTBEATS = 3

class ConnectionMonitor(manager: ConnectionManager) {
    private lateinit var connectionManager: ConnectionManager
    private lateinit var handler: Handler
    private var serviceRunning = false
    private lateinit var heartbeatSocket: SocketManager
    private var missedHeartbeats = 0

    init {
        setConnectionManager(manager)
        initHeartbeat()
    }

    private fun setConnectionManager(manager: ConnectionManager) {
        this.connectionManager = manager
    }

    fun getConnectionManager(): ConnectionManager {
        return this.connectionManager
    }

    private fun setHeartbeatSocket(socket: SocketManager) {
        this.heartbeatSocket = socket
    }

    private fun getHeartbeatSocket(): SocketManager {
        return this.heartbeatSocket
    }

    private val heartbeatService = object : Runnable {
        override fun run() {
            if (!serviceRunning) return

            try {
                if (sendHeartbeat()) {
                    missedHeartbeats = 0
                    handler.postDelayed(this, HEARTBEAT_INTERVAL_MS)
                } else {
                    missedHeartbeats++
                    if (missedHeartbeats >= MAX_MISSED_HEARTBEATS) {
                        handleConnectionLoss()
                    } else {
                        handler.postDelayed(this, HEARTBEAT_INTERVAL_MS / 2)
                    }
                }
            } catch (e: Exception) {
                handleConnectionLoss()
            }
        }
    }

    private fun handleConnectionLoss() {
        setServiceRunning(false)
        connectionManager.setConnectionEstablished(false)
        // Notify view model or UI about connection loss
    }

    private fun initHeartbeat() {
        handler = Handler(Looper.getMainLooper())
        heartbeatExecutorService = Executors.newCachedThreadPool()
    }

    fun isHeartbeatRunning(): Boolean {
        return getServiceRunning()
    }

    private fun setServiceRunning(serviceRunning: Boolean) {
        this.serviceRunning = serviceRunning
    }

    private fun getServiceRunning(): Boolean {
        return this.serviceRunning
    }

    fun startHeartbeat() {
        if (!getServiceRunning()) {
            setHeartbeatSocket(
                SocketManager(
                    ConnectionManager.getInetAddress(),
                    ConnectionManager.getPort()
                )
            )
            initHeartbeat()
            handler.post(heartbeatService)
            setServiceRunning(true)
        }
    }

    fun startHeartbeat(delay: Int) {
        if (!getServiceRunning()) {
            setHeartbeatSocket(
                SocketManager(
                    ConnectionManager.getInetAddress(),
                    ConnectionManager.getPort()
                )
            )
            initHeartbeat()
            handler.postDelayed(heartbeatService, delay.toLong())
            setServiceRunning(true)
        }
    }

    fun sendHeartbeat(): Boolean {
        if (heartbeatExecutorService.isShutdown) {
            return false
        }

        // Retry heartbeat 5 times
        for (attempt in 0..1) {
            val future = CompletableFuture.supplyAsync(
                {
                    try {
                        getHeartbeatSocket().sendData("DroidHeartBeat")
                        setHeartbeatResponse(getHeartbeatSocket().waitForResponse(HEARTBEAT_TIMEOUT_MS))

                        if (getHeartbeatResponse().equals("HeartBeat Ack", ignoreCase = true)) {
                            return@supplyAsync true
                        }
                    } catch (ignored: Exception) {
                    }
                    false
                }, heartbeatExecutorService
            )

            try {
                val result = future.get()
                if (result) {
                    return true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                Thread.sleep(125)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        return false
    }

    private fun stopHeartbeat() {
        handler.removeCallbacks(heartbeatService)
        setServiceRunning(false)
    }

    fun shutDownHeartbeat() {
        stopHeartbeat()
        if (!heartbeatExecutorService.isShutdown) {
            heartbeatExecutorService.shutdownNow()
        }
        getHeartbeatSocket().closeSocket()
    }

    companion object {
        private var connectionMonitorInstance: ConnectionMonitor? = null
        private lateinit var heartbeatExecutorService: ExecutorService
        private var heartbeatResponse: String? = null

        fun getInstance(manager: ConnectionManager): ConnectionMonitor? {
            if (connectionMonitorInstance == null) {
                connectionMonitorInstance = ConnectionMonitor(manager)
            }
            return connectionMonitorInstance
        }

        fun setHeartbeatResponse(response: String?) {
            heartbeatResponse = response
        }

        fun getHeartbeatResponse(): String? {
            return heartbeatResponse
        }
    }
}

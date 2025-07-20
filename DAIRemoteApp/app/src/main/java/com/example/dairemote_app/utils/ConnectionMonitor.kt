package com.example.dairemote_app.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ConnectionMonitor(manager: ConnectionManager) {
    private lateinit var connectionManager: ConnectionManager
    private lateinit var handler: Handler
    private lateinit var heartbeatService: Runnable
    private var serviceRunning = false
    private lateinit var heartbeatSocket: SocketManager

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

    private fun initHeartbeat() {
        handler = Handler(Looper.getMainLooper())
        heartbeatService = object : Runnable {
            override fun run() {
                // Send heartbeat
                if (sendHeartbeat()) {
                    handler.postDelayed(this, 2000) // Repeat every 2 seconds
                } else {
                    setServiceRunning(false)
                    getConnectionManager().setConnectionEstablished(false)
                }
            }
        }

        heartbeatExecutorService = Executors.newCachedThreadPool()
    }

    fun isHeartbeatRunning(): Boolean {
        return getServiceRunning()
    }

    fun setServiceRunning(serviceRunning: Boolean) {
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
                        setHeartbeatResponse(getHeartbeatSocket().waitForResponse(3000))

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

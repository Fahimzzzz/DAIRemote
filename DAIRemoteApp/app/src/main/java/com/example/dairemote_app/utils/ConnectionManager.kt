package com.example.dairemote_app.utils

import android.os.Build
import android.util.Log
import com.example.dairemote_app.HostSearchCallback
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Arrays
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class ConnectionManager(serverAddress: String) {
    private val executorService: ExecutorService
    private var hostName: String? = null
    private var hostAudioList: String? = null
    private var hostDisplayProfileList: String? = null
    private var hostRequesterResponse: String? = null

    init {
        try {
            setServerAddress(InetAddress.getByName(serverAddress))
        } catch (ignored: Exception) {
        }

        this.executorService = Executors.newCachedThreadPool()
    }

    fun setConnectionEstablished(status: Boolean) {
        connectionEstablished.set(status)
    }

    fun getConnectionEstablished(): Boolean {
        return connectionEstablished.get()
    }

    private fun setHostAudioList(list: String?) {
        this.hostAudioList = list
    }

    fun getHostAudioList(): String? {
        return this.hostAudioList
    }

    private fun setHostDisplayProfilesList(list: String?) {
        this.hostDisplayProfileList = list
    }

    fun getHostDisplayProfilesList(): String? {
        return this.hostDisplayProfileList
    }

    private fun setHostRequesterResponse(response: String?) {
        this.hostRequesterResponse = response
    }

    private fun getHostRequesterResponse(): String? {
        return this.hostRequesterResponse
    }

    fun setHostName(name: String?) {
        this.hostName = name
    }

    fun getHostName(): String {
        if (this.hostName != null) {
            return hostName!!.substring("HostName: ".length)
        }
        return ""
    }

    // Wait for server response
    private fun waitForResponse(timeout: Int) {
        try {
            getUDPSocket().soTimeout = timeout
            receiveData()
            getUDPSocket().soTimeout = 75

            setServerResponse(String(receivePacket.data, 0, receivePacket.length))
        } catch (ignored: Exception) {
            setServerResponse("")
        }
    }

    // Initialize connection
    fun initializeConnection(): Boolean {
        setServerResponse("")
        var broadcastCount = 0
        while (getServerResponse().isNullOrEmpty()) {
            try {
                sendData("Connection requested by ${getDeviceName()}", getInetAddress())
                waitForResponse(5000) // Increased timeout

                when (getServerResponse()?.lowercase()) {
                    "wait" -> {
                        // Handle waiting case
                        waitForApproval()
                        return getServerResponse().equals("approved", ignoreCase = true)
                    }
                    "approved" -> {
                        shutdownHostSearchInBackground()
                        ConnectionMonitor.getInstance(this)
                        setConnectionEstablished(true)
                        return true
                    }
                    "declined" -> {
                        resetConnectionManager()
                        return false
                    }
                }

            } catch (e: Exception) {
                Log.e("Connection", "Error during initialization", e)
            }

            if (++broadcastCount > 5) {
                return false
            }
        }
        return false
    }

    private fun waitForApproval() {
        var approvalTimeout = 0
        setServerResponse("")
        while (getServerResponse().isNullOrEmpty() && approvalTimeout <= 5) {
            waitForResponse(10000)
            approvalTimeout++
        }
    }

    private fun stopExecServices(service: ExecutorService?) {
        if (service != null && !service.isShutdown) {
            service.shutdownNow()
        }
    }

    // Send message to the server
    fun sendHostMessage(msg: String): Boolean {
        if (getConnectionEstablished()) {
            executorService.submit { sendMessage(msg) }
            return true
        }
        return false
    }

    fun sendHostMessage(msg: String, inetAddress: InetAddress): Boolean {
        if (getConnectionEstablished()) {
            executorService.submit { sendMessage(msg, inetAddress) }
            return true
        }
        return false
    }

    private fun sendMessage(message: String) {
        try {
            sendData(message, getInetAddress())
        } catch (ignored: Exception) {
        }
    }

    private fun sendMessage(message: String, inetAddress: InetAddress) {
        try {
            sendData(message, inetAddress)
        } catch (ignored: Exception) {
        }
    }

    private fun hostRequester(
        replyCondition: String?,
        sendMessage: String,
        inetAddress: InetAddress?
    ): Boolean {
        setServerResponse("")
        var broadcastCount = 0
        while (!getServerResponse()!!.startsWith(replyCondition!!)) {
            try {
                sendData(sendMessage, inetAddress)
            } catch (ignored: Exception) {
            }
            broadcastCount += 1
            if (broadcastCount > 5) {
                return false
            } else {
                // Updates serverResponse else times out and throws socket exception
                try {
                    waitForResponse(5000)
                } catch (ignored: Exception) {
                }
            }
        }
        setHostRequesterResponse(getServerResponse())
        return true
    }

/*    fun requestHostName(): Boolean {
        if (hostRequester("HostName", "HOST Name", getInetAddress())) {
            setHostName(getHostRequesterResponse())
            return true
        }
        return false
    }*/

    // Retrieve audio devices from host
    fun requestHostAudioDevices(): Boolean {
        if (hostRequester("AudioDevices", "AUDIO Devices", getInetAddress())) {
            setHostAudioList(getHostRequesterResponse())
            return true
        }
        return false
    }

    // Retrieve display profiles from host
    fun requestHostDisplayProfiles(): Boolean {
        if (hostRequester("DisplayProfiles", "DISPLAY Profiles", getInetAddress())) {
            setHostDisplayProfilesList(getHostRequesterResponse())
            return true
        }
        return false
    }

    fun resetConnectionManager() {
        stopExecServices(executorService)
        setConnectionEstablished(false)
        setServerResponse(null)
        (null as String?)?.let { setServerAddress(it) }
        (null as InetAddress?)?.let { setServerAddress(it) }
    }

    // Shutdown the connection
    fun shutdown() {
        ConnectionMonitor.getInstance(this)?.shutDownHeartbeat()
        sendHostMessage("Shutdown requested")
        try {
            Thread.sleep(75)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
        resetConnectionManager()
        closeUDPSocket()
    }

    private fun closeUDPSocket() {
        if (!getUDPSocket().isClosed) {
            getUDPSocket().close()
        }
    }

    fun getServerAddress(): String {
        return serverAddress
    }

    companion object {
        private var hostSearchExecService: ExecutorService? = null
        private val connectionEstablished = AtomicBoolean(false)
        private lateinit var serverAddress: String
        private var serverResponse: String? = null
        private lateinit var inetAddress: InetAddress
        private const val SERVER_PORT = 9416
        private var hostHandler: HostSearchCallback? = null

        private var receiveData: ByteArray = ByteArray(1024)
        private var sendData = ByteArray(200)
        private var sendPacket: DatagramPacket? = null
        var receivePacket: DatagramPacket = DatagramPacket(receiveData, receiveData.size)
        private lateinit var udpSocket: DatagramSocket

        init {
            try {
                setUDPSocket(DatagramSocket())
            } catch (e: SocketException) {
                throw RuntimeException(e)
            }
        }

        private var broadcastAddress: InetAddress? = null

        init {
            try {
                broadcastAddress = InetAddress.getByName("255.255.255.255")
            } catch (e: UnknownHostException) {
                throw RuntimeException(e)
            }
        }

        private fun setUDPSocket(socket: DatagramSocket) {
            udpSocket = socket
        }

        fun getUDPSocket(): DatagramSocket {
            return udpSocket
        }

        fun setServerAddress(address: String) {
            serverAddress = address
        }

        fun setServerAddress(address: InetAddress) {
            inetAddress = address
        }

        fun getServerAddress(): String {
            return serverAddress
        }

        fun getInetAddress(): InetAddress {
            return inetAddress
        }

        fun getPort(): Int {
            return SERVER_PORT
        }

        private fun setServerResponse(response: String?) {
            serverResponse = response
        }

        fun getServerResponse(): String? {
            return serverResponse
        }

        @Throws(IOException::class)
        fun sendData(message: String, address: InetAddress?) {
            if (getUDPSocket().isClosed) {
                setUDPSocket(DatagramSocket())
            }
            sendData = message.toByteArray()
            sendPacket = DatagramPacket(sendData, sendData.size, address, getPort())
            getUDPSocket().send(sendPacket)
        }

        @Throws(IOException::class)
        fun receiveData() {
            // Clear prior data
            Arrays.fill(receiveData, 0.toByte())
            getUDPSocket().receive(receivePacket)
        }

        // Utility to get the device name
        fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                model
            } else {
                "$manufacturer $model"
            }
        }

        // Broadcast to search for hosts in the background
        fun hostSearchInBackground(hostSearchCallback: HostSearchCallback?, message: String) {
            hostHandler = hostSearchCallback
            hostSearchExecService = Executors.newSingleThreadExecutor()
            // Perform the host search
            hostSearchExecService!!.execute { hostSearch(message) }
        }

        fun shutdownHostSearchInBackground() {
            hostSearchExecService!!.shutdownNow()
        }

        // Broadcast to search for hosts
        private fun hostSearch(message: String) {
            val hosts: MutableList<String> = ArrayList()
            try {
                if (getUDPSocket().isClosed) {
                    setUDPSocket(DatagramSocket())
                }
                getUDPSocket().broadcast = true
                sendData(message + " " + getDeviceName(), broadcastAddress)
                getUDPSocket().soTimeout =
                    3000 // Millisecond timeout for responses and to break out of loop
                while (true) {
                    try {
                        receiveData() // Blocks until a response is received

                        setServerResponse(String(receivePacket.data).trim { it <= ' ' })
                        val serverIp = receivePacket.address.hostAddress

                        if (getServerResponse()!!.contains("Hello, I'm")) {
                            if (serverIp != null) {
                                hosts.add(serverIp)
                            }
                            getUDPSocket().soTimeout =
                                75 // Reset timeout on host found, otherwise lingers
                            getUDPSocket().broadcast = false
                        }
                    } catch (e: SocketTimeoutException) {
                        // Stop listening for responses, occurs on timeout
                        break
                    }
                }

                // Reset server response, just being used for broadcast search here
                // Needs to be empty for proceeding functions
                setServerResponse(null)

                if (hosts.isNotEmpty()) {
                    hostHandler!!.onHostFound(hosts)
                } else {
                    hostHandler!!.onError("No hosts found")
                }
            } catch (ignored: SocketException) {
            } catch (e: UnknownHostException) {
                throw RuntimeException(e)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }
}
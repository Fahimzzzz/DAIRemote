package com.example.dairemote_app

import com.example.dairemote_app.utils.ConnectionManager
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

internal class ConnectionManagerTest {
    @Test
    fun test_getHostAddress_Initialization() {
        Assertions.assertEquals(
            ConnectionManager.getServerAddress(),
            "Testing GetServerAddress() after initialization with '192.168.1.1'",
            "192.168.1.1"
        )
    }

    @Test
    fun test_setHostName_PC() {
        connectionManager!!.setHostName("HostName: PC")
        Assertions.assertEquals(
            "PC",
            connectionManager!!.getHostName(),
            "Testing SetHostName() with 'PC'"
        )
    }

    @Test
    fun test_getHostName_PC2() {
        connectionManager!!.setHostName("HostName: PC2")
        Assertions.assertEquals(
            "PC2",
            connectionManager!!.getHostName(),
            "Testing GetHostName() with 'PC2'"
        )
    }

    @Test
    fun test_getHostName_Empty() {
        connectionManager!!.setHostName("HostName: ")
        Assertions.assertEquals(
            "",
            connectionManager!!.getHostName(),
            "Testing GetHostName() with no name"
        )
    }

    companion object {
        private var connectionManager: ConnectionManager? = null

        @JvmStatic
        @BeforeAll
        fun setup(): Unit {
            connectionManager = ConnectionManager("192.168.1.1")
        }

        @JvmStatic
        @AfterAll
        fun cleanup(): Unit {
            connectionManager!!.resetConnectionManager()
        }
    }
}
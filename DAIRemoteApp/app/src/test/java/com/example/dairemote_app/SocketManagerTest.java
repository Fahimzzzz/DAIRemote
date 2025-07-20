package com.example.dairemote_app;
import static org.junit.jupiter.api.Assertions.*;

import com.example.dairemote_app.utils.SocketManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
class SocketManagerTest {

    SocketManager socketManager;
    DatagramPacket packet;

    @BeforeEach
    void setUp() throws UnknownHostException {
        socketManager = new SocketManager(InetAddress.getByName("127.0.0.1"), 9999);
    }
    @Test
    void testSetSocket() {
        socketManager.setSocket();
        assertNotNull(socketManager.getSocket(), "Socket should be created");
    }

    @Test
    void testGetSocket() {
        DatagramSocket socket = socketManager.getSocket();
        assertNotNull(socket, "Socket should not be null");
    }

    @Test
    void closeSocket() {
        socketManager.closeSocket();
        assertTrue(socketManager.getSocket().isClosed(), "Testing SocketManager CloseSocket(), expecting null");
    }

    @Test
    void testGetData() {
        assertNotNull(socketManager.getData(), "Data array should not be null");
    }

    @Test
    void testSetPacket() {
        byte[] data = new byte[100];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        socketManager.setPacket(packet);
        assertSame(packet, socketManager.getPacket(), "Packet should be set correctly");
    }

    @Test
    void testGetPacket() {
        DatagramPacket packet = socketManager.getPacket();
        assertNotNull(packet, "Packet should not be null");
    }
}

package com.patetlex.displayphoenix.system.web;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DeviceConnection {

    private static final int MEGABYTE = 1000000;
    public static ServerSocket SERVER;
    public static final Map<String, Socket> CONNECTED_CLIENTS = new HashMap<>();
    public static final Map<String, Socket> CONNECTED_SERVERS = new HashMap<>();

    public static boolean isConnected(String address) {
        if (CONNECTED_SERVERS.containsKey(address) || CONNECTED_CLIENTS.containsKey(address)) {
            Socket socket = CONNECTED_SERVERS.containsKey(address) ? CONNECTED_SERVERS.get(address) : CONNECTED_CLIENTS.get(address);
            return socket.isConnected();
        }
        return false;
    }

    public static boolean hasServer() {
        return SERVER != null && !SERVER.isClosed();
    }

    public static void startServer(Consumer<byte[]> dataReceived) throws IOException {
        startServer(getDefaultPort(), dataReceived);
    }

    public static void startServer(int port, Consumer<byte[]> dataReceived) throws IOException {
        if (SERVER != null)
            throw new IllegalStateException("Server already started.");
        SERVER = new ServerSocket(port);
        new Thread(() -> {
            while (!SERVER.isClosed()) {
                try {
                    Socket client = SERVER.accept();
                    if (CONNECTED_CLIENTS.containsKey(client.getInetAddress().getHostAddress())) {
                        Socket pC = CONNECTED_CLIENTS.get(client.getInetAddress().getHostAddress());
                        if (!pC.isClosed() && pC.isConnected()) {
                            continue;
                        }
                    }
                    CONNECTED_CLIENTS.put(client.getInetAddress().getHostAddress(), client);
                    dataReceived.accept(client.getInetAddress().getHostAddress().getBytes());
                    new Thread(() -> {
                        try {
                            while (client.isConnected()) {
                                ByteArrayOutputStream os = new ByteArrayOutputStream();
                                byte[] buffer = new byte[MEGABYTE * 1000];
                                os.write(buffer, 0, client.getInputStream().read(buffer));
                                byte[] data = os.toByteArray();
                                os.flush();
                                os.close();
                                dataReceived.accept(data);
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public static String connectTo(String address, int port, Consumer<byte[]> dataReceived) throws IOException {
        Socket socket = new Socket(address, port);
        CONNECTED_SERVERS.put(address, socket);
        String clientHostAddress = socket.getInetAddress().getHostAddress();
        new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    byte[] buffer = new byte[MEGABYTE * 1000];
                    os.write(buffer, 0, socket.getInputStream().read(buffer));
                    byte[] data = os.toByteArray();
                    os.flush();
                    os.close();
                    dataReceived.accept(data);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        return clientHostAddress;
    }

    public static void sendData(String address, byte[] data) {
        if (CONNECTED_CLIENTS.containsKey(address) || CONNECTED_SERVERS.containsKey(address)) {
            Socket device = CONNECTED_CLIENTS.containsKey(address) ? CONNECTED_CLIENTS.get(address) : CONNECTED_SERVERS.get(address);
            if (device.isConnected()) {
                try {
                    device.getOutputStream().write(data);
                    device.getOutputStream().flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void dispose() {
        for (String address : CONNECTED_CLIENTS.keySet()) {
            Socket socket = CONNECTED_CLIENTS.get(address);
            try {
                socket.getInputStream().close();
                socket.getOutputStream().close();
                socket.close();
            } catch (IOException ignored) {
            }
        }
        for (String address : CONNECTED_SERVERS.keySet()) {
            Socket socket = CONNECTED_SERVERS.get(address);
            try {
                socket.getInputStream().close();
                socket.getOutputStream().close();
                socket.close();
            } catch (IOException ignored) {
            }
        }
        if (SERVER != null) {
            try {
                SERVER.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static int getDefaultPort() {
/*        String id = StringHelper.id(StringHelper.condense(Application.getTitle()));
        StringBuilder port = new StringBuilder();
        for (int i = 0; i < id.length(); i++) {
            int c = id.charAt(i) - 97;
            if (c < 26) {
                port.append((int) Math.floor(c / 3F) + 1);
            }
        }
        port.setLength(String.valueOf(Integer.MAX_VALUE).length() - 1);*/
        return 53972;
    }
}

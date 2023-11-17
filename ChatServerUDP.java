import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class ChatServerUDP {
    private static final int PORT = 9876;
    private static List<ClientInfo> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            System.out.println("Server is running on port " + PORT);

            while (true) {
                byte[] receiveData = new byte[8192];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

                // Handle regular message
                String senderAddress = receivePacket.getAddress().getHostAddress();
                int senderPort = receivePacket.getPort();

                System.out.println("Received from " + senderAddress + ":" + senderPort + ": " +
                        receivedMessage);

                // Broadcast the message to all clients
                broadcastMessage(new Message("Server", receivedMessage), senderAddress, senderPort);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void broadcastMessage(Message message, String senderAddress, int senderPort) {
        for (ClientInfo client : clients) {
            if (!(client.getAddress().equals(senderAddress) && client.getPort() == senderPort)) {
                sendToClient(message, client.getAddress(), client.getPort());
            }
        }
    }

    private static void sendToClient(Message message, String address, int port) {
        try {
            DatagramSocket socket = new DatagramSocket();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(message);
            byte[] sendData = outputStream.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                    java.net.InetAddress.getByName(address), port);
            socket.send(sendPacket);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ClientInfo {
        private final String address;
        private final int port;

        public ClientInfo(String address, int port) {
            this.address = address;
            this.port = port;
        }

        public String getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }
    }
}

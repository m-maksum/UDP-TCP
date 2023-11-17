import javax.swing.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Base64;
import java.util.Scanner;

public class ChatClientUDP {
    private static final int PORT = 9876;

    public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter your name: ");
            String name = scanner.nextLine();

            while (true) {
                System.out.print("[" + name + "] Enter message or file path (type 'exit' to quit): ");
                String input = scanner.nextLine();

                if ("exit".equalsIgnoreCase(input)) {
                    break;
                }

                // Send message
                sendMessage(clientSocket, name, input);
            }

            clientSocket.close();
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendMessage(DatagramSocket socket, String sender, String content) {
        try {
            Message message = new Message(sender, content);
            String formattedMessage = message.getSender() + ": " + message.getContent();
            byte[] sendData = formattedMessage.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                    java.net.InetAddress.getByName("localhost"), PORT);
            socket.send(sendPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

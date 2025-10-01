package BTL;

import java.net.*;
import java.io.*;

public class ServerMain {
    public static void main(String[] args) {
        int port = 5000;
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                Socket socket = server.accept();
                System.out.println("Client connected: " + socket.getInetAddress());
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

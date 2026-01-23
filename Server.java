import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {

    private static final int PORT = 12345;
    private static Set<ClientHandler> clientHandlers = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        System.out.println("Server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandlers.add(clientHandler);

                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast message to all clients
    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler ch : clientHandlers) {
            if (ch != sender) {
                ch.sendMessage(message);
            }
        }
    }

    // Remove client
    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        System.out.println("Client disconnected: " + clientHandler.getClientName());
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientName = "Anonymous";

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Enter your name:");
                clientName = in.readLine();
                out.println("Welcome " + clientName + "!");
                broadcast(clientName + " joined the chat.", this);
                System.out.println(clientName + " joined.");
            } catch (IOException e) {
                closeEverything();
            }
        }

        @Override
        public void run() {
            String message;

            try {
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("/quit")) {
                        break; // Client wants to quit
                    }
                    String formattedMsg = clientName + ": " + message;
                    System.out.println(formattedMsg);
                    broadcast(formattedMsg, this);
                }
            } catch (IOException e) {
                // Client disconnected unexpectedly
            } finally {
                closeEverything();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public String getClientName() {
            return clientName;
        }

        private void closeEverything() {
            try {
                broadcast(clientName + " left the chat.", this);
                removeClient(this);

                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
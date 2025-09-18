package BTL;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ServerWorker {
    private final int port;
    private final UserStore store;
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private ExecutorService pool;
    private final AdminLogger logger;

    // 🆕 Listener để báo khi có client kết nối
    public interface ClientConnectionListener {
        void onClientConnected(Socket client);
    }
    private ClientConnectionListener listener;

    public void setClientConnectionListener(ClientConnectionListener listener) {
        this.listener = listener;
    }

    public ServerWorker(int port, UserStore store, AdminLogger logger) {
        this.port = port;
        this.store = store;
        this.logger = logger;
    }

    public void start() {
        if (running) return;
        running = true;
        pool = Executors.newCachedThreadPool();
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                logger.log("Server started on port " + port);
                while (running) {
                    try {
                        Socket client = serverSocket.accept();
                        logger.log("Client connected: " + client.getRemoteSocketAddress());

                        // 🆕 Báo cho ServerMain để tự động refresh
                        if (listener != null) {
                            listener.onClientConnected(client);
                        }

                        pool.submit(new ClientHandler(client, store, logger));
                    } catch (IOException e) {
                        if (running) logger.log("Accept error: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                logger.log("Server error: " + e.getMessage());
            } finally {
                stop();
            }
        }).start();
    }

    public void stop() {
        if (!running) return;
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}
        if (pool != null) pool.shutdownNow();
        logger.log("Server stopped");
    }

    public boolean isRunning() { return running; }
}

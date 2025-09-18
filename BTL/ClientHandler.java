package BTL;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final UserStore store;
    private final AdminLogger logger;
    private User currentUser = null; // lưu user đang login

    public ClientHandler(Socket socket, UserStore store, AdminLogger logger) {
        this.socket = socket;
        this.store = store;
        this.logger = logger;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"))
        ) {
            String request;
            while ((request = in.readLine()) != null) {
                logger.log("REQ from " + socket.getRemoteSocketAddress() + " : " + request);

                // protocol: ACTION|param1|param2|...
                String[] parts = request.split("\\|", 5);
                String action = parts[0];
                String response = "ERR|Unknown";

                if ("LOGIN".equalsIgnoreCase(action)) {
                    String username = parts.length > 1 ? parts[1] : "";
                    String password = parts.length > 2 ? parts[2] : "";
                    User u = store.authenticate(username, password);
                    if (u != null) {
                        currentUser = u;
                        currentUser.setActive(true);          // đánh dấu online
                        store.updateUser(currentUser);        // lưu lại trạng thái
                        response = "OK|LOGIN|" + u.getId() + "|" + u.getUsername() + "|" + u.getRole();
                    } else {
                        response = "ERR|LOGIN_FAILED";
                    }

                } else if ("REGISTER".equalsIgnoreCase(action)) {
                    String username = parts.length > 1 ? parts[1] : "";
                    String password = parts.length > 2 ? parts[2] : "";
                    String email = parts.length > 3 ? parts[3] : "";
                    String fullname = parts.length > 4 ? parts[4] : "";
                    if (store.usernameExists(username)) {
                        response = "ERR|USER_EXISTS";
                    } else {
                        User u = store.register(username, password, email, fullname);
                        if (u != null) response = "OK|REGISTER|" + u.getId();
                        else response = "ERR|REGISTER";
                    }

                } else if ("GET_PROFILE".equalsIgnoreCase(action)) {
                    int id = Integer.parseInt(parts[1]);
                    User u = store.findById(id);
                    if (u != null) {
                        String status = u.isActive() ? "ONL" : "OFF";
                        response = String.format("OK|PROFILE|%d|%s|%s|%s|%s",
                                u.getId(),
                                u.getUsername(),
                                (u.getEmail() == null ? "" : u.getEmail()),
                                (u.getFullname() == null ? "" : u.getFullname()),
                                status);
                    } else {
                        response = "ERR|NO_PROFILE";
                    }

                } else if ("UPDATE_PROFILE".equalsIgnoreCase(action)) {
                    int id = Integer.parseInt(parts[1]);
                    String email = parts.length > 2 ? parts[2] : "";
                    String fullname = parts.length > 3 ? parts[3] : "";
                    boolean ok = store.updateProfile(id, email, fullname);
                    response = ok ? "OK|UPDATE_PROFILE" : "ERR|UPDATE_FAIL";

                } else if ("CHANGE_PASS".equalsIgnoreCase(action)) {
                    int id = Integer.parseInt(parts[1]);
                    String oldPass = parts.length > 2 ? parts[2] : "";
                    String newPass = parts.length > 3 ? parts[3] : "";
                    User u = store.findById(id);
                    if (u != null && UserStore.sha256(oldPass).equals(u.getPasswordHash())) {
                        store.changePassword(id, newPass);
                        response = "OK|CHANGE_PASS";
                    } else {
                        response = "ERR|CHANGE_FAIL";
                    }

                } else if ("CHANGE_ROLE".equalsIgnoreCase(action)) {
                    int id = Integer.parseInt(parts[1]);
                    String newRole = parts.length > 2 ? parts[2] : "user";
                    boolean ok = store.changeRole(id, newRole);
                    response = ok ? "OK|CHANGE_ROLE" : "ERR|CHANGE_ROLE";

                } else if ("RESET_PASS".equalsIgnoreCase(action)) {
                    int id = Integer.parseInt(parts[1]);
                    boolean ok = store.resetPassword(id);
                    response = ok ? "OK|RESET_PASS" : "ERR|RESET_PASS";

                } else if ("LOGOUT".equalsIgnoreCase(action)) {
                    if (currentUser != null) {
                        currentUser.setActive(false);
                        store.updateUser(currentUser);
                    }
                    response = "OK|LOGOUT|OFF";

                } else {
                    response = "ERR|UNKNOWN_ACTION";
                }

                // gửi trả kết quả về client
                out.write(response);
                out.newLine();
                out.flush();
                logger.log("RESP to " + socket.getRemoteSocketAddress() + " : " + response);
            }
        } catch (IOException ex) {
            logger.log("Client disconnected: " + socket.getRemoteSocketAddress());
        } finally {
            // khi client mất kết nối thì auto OFF
            if (currentUser != null) {
                try {
                    currentUser.setActive(false);
                    store.updateUser(currentUser);
                    logger.log("User " + currentUser.getUsername() + " -> OFF");
                } catch (IOException e) {
                    logger.log("Error updating user state: " + e.getMessage());
                }
            }
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}

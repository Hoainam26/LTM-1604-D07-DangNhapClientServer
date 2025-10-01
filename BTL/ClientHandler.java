package BTL;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

            String request;
            while ((request = in.readLine()) != null) {
                // protocol: COMMAND;arg1;arg2;...
                String[] parts = request.split(";", -1);
                String cmd = parts[0];
                System.out.println("REQ: " + request);

                switch (cmd) {
                    case "LOGIN":
                        handleLogin(parts);
                        break;
                    case "REGISTER":
                        handleRegister(parts);
                        break;
                    case "GET_STATS":
                        handleStats();
                        break;
                    case "GET_USERS":
                        handleGetUsers();
                        break;
                    case "TOGGLE_LOCK":
                        handleToggleLock(parts);
                        break;
                    case "CREATE_USER":
                        handleCreateUser(parts);
                        break;
                    case "EDIT_USER":
                        handleEditUser(parts);
                        break;
                    case "CHANGE_PASSWORD":
                        handleChangePassword(parts);
                        break;
                    case "GET_LOGS":
                        handleGetLogs();
                        break;
                    case "DELETE_USER":   // ✅ Thêm xử lý xóa user
                        handleDeleteUser(parts);
                        break;
                    default:
                        out.println("ERROR;Unknown command");
                }
            }
        } catch (IOException e) {
            // e.printStackTrace();
        } finally {
            try { socket.close(); } catch (IOException e) {}
        }
    }

    private void handleLogin(String[] p) {
        if (p.length < 3) { out.println("FAIL;Missing args"); return; }
        String username = p[1], password = p[2];
        List<User> users = UserDatabase.loadUsers();
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                if ("LOCKED".equals(u.getStatus())) {
                    out.println("FAIL;Account locked");
                    LoggerUtil.log(username, "LOGIN_FAIL", "Account locked");
                } else {
                    out.println("SUCCESS;" + u.getRole() + ";" + u.getFullname() + ";" + u.getEmail());
                    LoggerUtil.log(username, "LOGIN_SUCCESS", "Login from " + socket.getInetAddress().getHostAddress());
                }
                return;
            }
        }
        out.println("FAIL;Invalid username or password");
        LoggerUtil.log(username, "LOGIN_FAIL", "Invalid credentials");
    }

    private void handleRegister(String[] p) {
        if (p.length < 5) { out.println("FAIL;Missing args"); return; }
        String username = p[1], password = p[2], fullname = p[3], email = p[4];
        List<User> users = UserDatabase.loadUsers();
        for (User u : users) if (u.getUsername().equals(username)) {
            out.println("FAIL;Username exists"); return;
        }
        int id = UserDatabase.nextId();
        User newUser = new User(id, username, password, fullname, email, "USER", "ACTIVE");
        users.add(newUser);
        UserDatabase.saveUsers(users);
        out.println("SUCCESS;Registered");
        LoggerUtil.log(username, "REGISTER", "Created new user");
    }

    private void handleStats() {
        List<User> users = UserDatabase.loadUsers();
        long total = users.size();
        long locked = users.stream().filter(u -> "LOCKED".equals(u.getStatus())).count();
        out.println("STATS;" + total + ";" + locked);
    }

    private void handleGetUsers() {
        List<User> users = UserDatabase.loadUsers();
        // format: USERS;id|username|fullname|email|role|status;... (each user separated by ;)
        StringBuilder sb = new StringBuilder();
        sb.append("USERS");
        for (User u : users) {
            sb.append(";");
            sb.append(u.getId()).append("|")
              .append(escape(u.getUsername())).append("|")
              .append(escape(u.getFullname())).append("|")
              .append(escape(u.getEmail())).append("|")
              .append(u.getRole()).append("|")
              .append(u.getStatus());
        }
        out.println(sb.toString());
    }

    private void handleToggleLock(String[] p) {
        if (p.length < 2) { out.println("FAIL;Missing id"); return; }
        int id = Integer.parseInt(p[1]);
        List<User> users = UserDatabase.loadUsers();
        boolean ok = false;
        for (User u : users) {
            if (u.getId() == id) {
                if ("LOCKED".equals(u.getStatus())) u.setStatus("ACTIVE");
                else u.setStatus("LOCKED");
                ok = true;
                LoggerUtil.log("admin", "ADMIN_SET_STATUS", "Set status for " + u.getUsername() + " -> " + u.getStatus());
                break;
            }
        }
        if (ok) {
            UserDatabase.saveUsers(users);
            out.println("SUCCESS;Toggled");
        } else out.println("FAIL;Not found");
    }

    private void handleCreateUser(String[] p) {
        if (p.length < 6) { out.println("FAIL;Missing args"); return; }
        String username = p[1], password = p[2], fullname = p[3], email = p[4], role = p[5];
        List<User> users = UserDatabase.loadUsers();
        for (User u : users) if (u.getUsername().equals(username)) { out.println("FAIL;Exists"); return; }
        int id = UserDatabase.nextId();
        User nu = new User(id, username, password, fullname, email, role, "ACTIVE");
        users.add(nu);
        UserDatabase.saveUsers(users);
        out.println("SUCCESS;Created");
        LoggerUtil.log("admin", "ADMIN_CREATE_USER", "Created " + username);
    }

    private void handleEditUser(String[] p) {
        if (p.length < 6) { out.println("FAIL;Missing args"); return; }
        int id = Integer.parseInt(p[1]);
        String username = p[2], fullname = p[3], email = p[4], role = p[5];
        List<User> users = UserDatabase.loadUsers();
        boolean ok = false;
        for (User u : users) {
            if (u.getId() == id) {
                u.setUsername(username);
                u.setFullname(fullname);
                u.setEmail(email);
                u.setRole(role);
                ok = true;
                LoggerUtil.log("admin", "ADMIN_EDIT_USER", "Edit id " + id);
                break;
            }
        }
        if (ok) { UserDatabase.saveUsers(users); out.println("SUCCESS;Edited"); }
        else out.println("FAIL;Not found");
    }

    private void handleChangePassword(String[] p) {
        if (p.length < 3) { out.println("FAIL;Missing args"); return; }
        String username = p[1], newpass = p[2];
        List<User> users = UserDatabase.loadUsers();
        boolean ok = false;
        for (User u : users) {
            if (u.getUsername().equals(username)) {
                u.setPassword(newpass);
                ok = true;
                LoggerUtil.log(username, "CHANGE_PASSWORD", "Changed password");
                break;
            }
        }
        if (ok) { UserDatabase.saveUsers(users); out.println("SUCCESS;Password changed"); }
        else out.println("FAIL;Not found");
    }

    private void handleGetLogs() {
        File f = new File("logs.csv");
        if (!f.exists()) { out.println("LOGS;"); return; }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            br.readLine(); // header
            while ((line = br.readLine()) != null) {
                sb.append(line.replace(",", "&#44;")).append(";;"); // separate records
            }
        } catch (IOException e) { e.printStackTrace(); }
        out.println("LOGS;" + sb.toString());
    }

    private void handleDeleteUser(String[] p) {
        if (p.length < 2) { 
            out.println("FAIL;Missing id"); 
            return; 
        }
        int id = Integer.parseInt(p[1]);
        List<User> users = UserDatabase.loadUsers();
        boolean removed = users.removeIf(u -> u.getId() == id);

        if (removed) {
            UserDatabase.saveUsers(users);
            LoggerUtil.log("admin", "ADMIN_DELETE_USER", "Deleted user id=" + id);
            out.println("SUCCESS;User deleted");
        } else {
            out.println("FAIL;Not found");
        }
    }

    private String escape(String s) {
        return s == null ? "" : s.replace(",", "&#44;");
    }
}

package BTL;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

public class UserStore {
    private final Path path;
    private final Map<Integer, User> users = new LinkedHashMap<>();
    private int nextId = 1;

    public UserStore(String filename) throws IOException {
        this.path = Paths.get(filename);
        if (!Files.exists(path)) Files.createFile(path);
        load();
        ensureAdminExists();
    }

    private synchronized void load() throws IOException {
        users.clear();
        List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            List<String> parts = splitCSV(line);
            if (parts.size() < 9) continue;
            int id = Integer.parseInt(parts.get(0));
            String username = User.unescape(parts.get(1));
            String passwordHash = parts.get(2);
            String email = User.unescape(parts.get(3));
            String fullname = User.unescape(parts.get(4));
            boolean active = Boolean.parseBoolean(parts.get(5));
            boolean enabled = Boolean.parseBoolean(parts.get(6));
            String createdAt = parts.get(7);
            String role = parts.get(8);
            User u = new User(id, username, passwordHash, email, fullname, active, enabled, createdAt, role);
            users.put(id, u);
            if (id >= nextId) nextId = id + 1;
        }
    }

    private List<String> splitCSV(String line) {
        List<String> res = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean escape = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (escape) {
                cur.append(c);
                escape = false;
            } else {
                if (c == '\\') {
                    escape = true;
                } else if (c == ',') {
                    res.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
        }
        res.add(cur.toString());
        return res;
    }

    private synchronized void persist() throws IOException {
        List<String> lines = new ArrayList<>();
        for (User u : users.values()) lines.add(u.toCSVLine());
        Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void ensureAdminExists() throws IOException {
        boolean hasAdmin = users.values().stream().anyMatch(u -> "admin".equals(u.getRole()));
        if (!hasAdmin) {
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String adminPass = sha256("admin123"); // default admin password
            User admin = new User(nextId++, "admin", adminPass,
                    "admin@example.com", "Administrator",
                    false, true, now, "admin");
            users.put(admin.getId(), admin);
            persist();
        }
    }

    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte by : b) sb.append(String.format("%02x", by));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Đăng ký user mới
    public synchronized User register(String username, String password, String email, String fullname) throws IOException {
        if (usernameExists(username)) return null;
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String hashed = sha256(password);
        User u = new User(nextId++, username, hashed, email, fullname,
                false, true, now, "user");
        users.put(u.getId(), u);
        persist();
        return u;
    }

    public synchronized boolean usernameExists(String username) {
        return users.values().stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    // Đăng nhập
    public synchronized User authenticate(String username, String password) {
        String hashed = sha256(password);
        return users.values().stream().filter(u ->
                u.getUsername().equalsIgnoreCase(username) &&
                u.getPasswordHash().equals(hashed) &&
                u.isEnabled()
        ).findFirst().orElse(null);
    }

    public synchronized User findById(int id) {
        return users.get(id);
    }

    public synchronized List<User> listAll() {
        return new ArrayList<>(users.values());
    }

    public synchronized boolean deleteUser(int id) throws IOException {
        if (!users.containsKey(id)) return false;
        users.remove(id);
        persist();
        return true;
    }

    public synchronized boolean updateProfile(int id, String email, String fullname) throws IOException {
        User u = users.get(id);
        if (u == null) return false;
        u.setEmail(email);
        u.setFullname(fullname);
        persist();
        return true;
    }

    public synchronized boolean changePassword(int id, String newPassword) throws IOException {
        User u = users.get(id);
        if (u == null) return false;
        u.setPasswordHash(sha256(newPassword));
        persist();
        return true;
    }

    public synchronized void setActive(int id, boolean active) throws IOException {
        User u = users.get(id);
        if (u != null) {
            u.setActive(active);
            persist();
        }
    }

    // 🔹 Cập nhật toàn bộ user
    public synchronized boolean updateUser(User u) throws IOException {
        if (!users.containsKey(u.getId())) return false;
        users.put(u.getId(), u);
        persist();
        return true;
    }

    // 🔹 Đổi vai trò
    public synchronized boolean changeRole(int id, String newRole) throws IOException {
        User u = users.get(id);
        if (u == null) return false;
        u.setRole(newRole);
        persist();
        return true;
    }

    // 🔹 Reset mật khẩu về mặc định 123456
    public synchronized boolean resetPassword(int id) throws IOException {
        User u = users.get(id);
        if (u == null) return false;
        u.setPasswordHash(sha256("123456"));
        persist();
        return true;
    }

    // 🔹 Bật/tắt user (cấm login)
    public synchronized boolean setEnabled(int id, boolean enabled) throws IOException {
        User u = users.get(id);
        if (u == null) return false;
        u.setEnabled(enabled);
        persist();
        return true;
    }
}

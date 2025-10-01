package BTL;

import java.io.*;
import java.util.*;

public class UserDatabase {
    private static final String FILE = "users.csv";

    public synchronized static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File f = new File(FILE);
        boolean needDefaultAdmin = false;

        // Nếu file chưa tồn tại → tạo file mới + cần admin mặc định
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
                pw.println("id,username,password,fullname,email,role,status");
            } catch (IOException e) {
                e.printStackTrace();
            }
            needDefaultAdmin = true;
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
                String line;
                String header = br.readLine(); // bỏ dòng header
                while ((line = br.readLine()) != null) {
                    String[] parts = splitCSV(line);
                    if (parts.length >= 7) {
                        users.add(new User(
                            Integer.parseInt(parts[0]),
                            User.unescape(parts[1]),
                            User.unescape(parts[2]),
                            User.unescape(parts[3]),
                            User.unescape(parts[4]),
                            parts[5], parts[6]
                        ));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                needDefaultAdmin = true;
            }
        }

        // Nếu chưa có admin thì thêm admin mặc định
     // Nếu file mới tạo hoàn toàn trống thì mới thêm admin mặc định
        if (needDefaultAdmin) {
            User admin = new User(1, "admin", "admin123", "Administrator", "admin@example.com", "ADMIN", "ACTIVE");
            users.add(admin);
            saveUsers(users);
        }


        return users;
    }

    public synchronized static void saveUsers(List<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            pw.println("id,username,password,fullname,email,role,status");
            for (User u : users) {
                pw.println(u.toCSV());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] splitCSV(String line) {
        // simple split that handles our escaped commas &#44;
        return line.split("(?<!\\\\),");
    }

    // helper to find by username
    public synchronized static User findByUsername(String username) {
        for (User u : loadUsers()) {
            if (u.getUsername().equals(username)) return u;
        }
        return null;
    }

    public synchronized static int nextId() {
        List<User> users = loadUsers();
        int max = 0;
        for (User u : users) if (u.getId() > max) max = u.getId();
        return max + 1;
    }
}

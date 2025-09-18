package BTL;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String email;
    private String fullname;
    private boolean active;   // online/offline
    private boolean enabled;  // có được phép đăng nhập hay không
    private String createdAt;
    private String role; // "admin" or "user"

    public User(int id, String username, String passwordHash, String email, String fullname,
                boolean active, boolean enabled, String createdAt, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.fullname = fullname;
        this.active = active;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.role = role;
    }

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getCreatedAt() { return createdAt; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // CSV line
    public String toCSVLine() {
        // id,username,passwordHash,email,fullname,active,enabled,createdAt,role
        return String.format("%d,%s,%s,%s,%s,%b,%b,%s,%s",
                id, escape(username), passwordHash,
                escape(email), escape(fullname),
                active, enabled, createdAt, role);
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", "\\,");
    }

    public static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\,", ",");
    }
}

package BTL;

import java.util.*;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullname;
    private String email;
    private String role;   // ADMIN / USER
    private String status; // ACTIVE / LOCKED

    public User(int id, String username, String password, String fullname, String email, String role, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullname() { return fullname; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getStatus() { return status; }

    public void setPassword(String p) { this.password = p; }
    public void setFullname(String s) { this.fullname = s; }
    public void setEmail(String s) { this.email = s; }
    public void setRole(String s) { this.role = s; }
    public void setStatus(String s) { this.status = s; }

    public String toCSV() {
        return id + "," + escape(username) + "," + escape(password) + "," + escape(fullname) + "," + escape(email) + "," + role + "," + status;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", "&#44;"); // basic escape for comma
    }

    public static String unescape(String s) {
        if (s == null) return "";
        return s.replace("&#44;", ",");
    }

	public void setUsername(String username2) {
		// TODO Auto-generated method stub
		
	}
}

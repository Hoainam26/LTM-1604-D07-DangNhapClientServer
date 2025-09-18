package BTL;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClientApp extends JFrame {
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin, btnRegister, btnExit;

    private final String SERVER_HOST = "localhost";
    private final int SERVER_PORT = 5000;

    public ClientApp() {
        setTitle("Đăng nhập hệ thống");
        setSize(480, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx=0; c.gridy=0; p.add(new JLabel("Tên đăng nhập:"), c);
        c.gridx=1; txtUser = new JTextField(20); c.gridy=0; p.add(txtUser, c);

        c.gridx=0; c.gridy=1; p.add(new JLabel("Mật khẩu:"), c);
        c.gridx=1; txtPass = new JPasswordField(20); c.gridy=1; p.add(txtPass, c);

        btnLogin = new JButton("Đăng nhập");
        btnRegister = new JButton("Đăng ký");
        btnExit = new JButton("Thoát");

        JPanel btns = new JPanel();
        btns.add(btnLogin); btns.add(btnRegister); btns.add(btnExit);
        c.gridx=0; c.gridy=2; c.gridwidth=2; p.add(btns, c);

        getContentPane().add(p);

        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> openRegisterDialog());
        btnExit.addActionListener(e -> System.exit(0));
    }

    private void doLogin() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ");
            return;
        }
        try (Socket s = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"))
        ) {
            String req = String.join("|", "LOGIN", user, pass);
            out.write(req); out.newLine(); out.flush();
            String resp = in.readLine();
            if (resp == null) { JOptionPane.showMessageDialog(this, "No response"); return; }
            String[] parts = resp.split("\\|");
            if ("OK".equals(parts[0]) && "LOGIN".equals(parts[1])) {
                int id = Integer.parseInt(parts[2]);
                String username = parts[3];
                String role = parts[4];

                // 🔹 Nếu là admin thì mở giao diện quản lý
                if ("admin".equalsIgnoreCase(role)) {
                    openAdminPanel();
                } else {
                    openHome(id, username, role);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Đăng nhập thất bại");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Cannot connect to server: " + ex.getMessage());
        }
    }

    // 🔹 Thêm hàm mới
    private void openAdminPanel() {
        SwingUtilities.invokeLater(() -> {
            ServerMain adminPanel = new ServerMain();
            adminPanel.setVisible(true);
        });
        this.dispose(); // đóng cửa sổ đăng nhập
    }


    private void openRegisterDialog() {
        JDialog d = new JDialog(this, "Đăng ký tài khoản", true);
        d.setSize(420, 320);
        d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField fUser = new JTextField(20);
        JPasswordField fPass = new JPasswordField(20);
        JPasswordField fPass2 = new JPasswordField(20);
        JTextField fEmail = new JTextField(20);
        JTextField fFull = new JTextField(20);

        c.gridx=0;c.gridy=0; p.add(new JLabel("Tên đăng nhập:"), c);
        c.gridx=1; p.add(fUser, c);
        c.gridx=0;c.gridy=1; p.add(new JLabel("Mật khẩu:"), c);
        c.gridx=1; p.add(fPass, c);
        c.gridx=0;c.gridy=2; p.add(new JLabel("Xác nhận mật khẩu:"), c);
        c.gridx=1; p.add(fPass2, c);
        c.gridx=0;c.gridy=3; p.add(new JLabel("Email:"), c);
        c.gridx=1; p.add(fEmail, c);
        c.gridx=0;c.gridy=4; p.add(new JLabel("Họ và tên:"), c);
        c.gridx=1; p.add(fFull, c);

        JButton bOK = new JButton("Đăng ký");
        JButton bCancel = new JButton("Hủy");
        JPanel pb = new JPanel(); pb.add(bOK); pb.add(bCancel);
        c.gridx=0; c.gridy=5; c.gridwidth=2; p.add(pb, c);

        d.getContentPane().add(p);

        bCancel.addActionListener(e -> d.dispose());
        bOK.addActionListener(e -> {
            String u = fUser.getText().trim();
            String p1 = new String(fPass.getPassword());
            String p2 = new String(fPass2.getPassword());
            String email = fEmail.getText().trim();
            String full = fFull.getText().trim();
            if (u.isEmpty() || p1.isEmpty() || p2.isEmpty()) { JOptionPane.showMessageDialog(d, "Nhập đủ thông tin"); return; }
            if (!p1.equals(p2)) { JOptionPane.showMessageDialog(d, "Mật khẩu không khớp"); return; }

            try (Socket s = new Socket(SERVER_HOST, SERVER_PORT);
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"))
            ) {
                String req = String.join("|", "REGISTER", u, p1, email, full);
                out.write(req); out.newLine(); out.flush();
                String resp = in.readLine();
                if (resp != null && resp.startsWith("OK|REGISTER")) {
                    JOptionPane.showMessageDialog(d, "Đăng ký thành công");
                    d.dispose();
                } else if (resp != null && resp.startsWith("ERR|USER_EXISTS")) {
                    JOptionPane.showMessageDialog(d, "Username đã tồn tại");
                } else {
                    JOptionPane.showMessageDialog(d, "Đăng ký thất bại");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(d, "Lỗi kết nối: " + ex.getMessage());
            }
        });

        d.setVisible(true);
    }

    private void openHome(int id, String username, String role) {
        JFrame home = new JFrame("Trang chủ - " + username);
        home.setSize(600, 420);
        home.setLocationRelativeTo(this);
        home.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel title = new JLabel("Chào mừng, " + username + "!", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 26));

        JPanel infoPanel = new JPanel(new GridLayout(5,2,10,10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));
        JLabel lblUser = new JLabel("Tên đăng nhập:");
        JLabel valUser = new JLabel(username);
        JLabel lblEmail = new JLabel("Email:");
        JLabel valEmail = new JLabel("");
        JLabel lblFull = new JLabel("Họ và tên:");
        JLabel valFull = new JLabel("");
        JLabel lblStatus = new JLabel("Trạng thái:");
        JLabel valStatus = new JLabel("Offline");
        infoPanel.add(lblUser); infoPanel.add(valUser);
        infoPanel.add(lblEmail); infoPanel.add(valEmail);
        infoPanel.add(lblFull); infoPanel.add(valFull);
        infoPanel.add(lblStatus); infoPanel.add(valStatus);

        JPanel btnPanel = new JPanel();
        JButton btnChangePass = new JButton("Đổi mật khẩu");
        JButton btnUpdate = new JButton("Cập nhật Profile");
        JButton btnLogout = new JButton("Đăng xuất");
        btnPanel.add(btnChangePass); btnPanel.add(btnUpdate); btnPanel.add(btnLogout);

        home.getContentPane().setLayout(new BorderLayout());
        home.getContentPane().add(title, BorderLayout.NORTH);
        home.getContentPane().add(infoPanel, BorderLayout.CENTER);
        home.getContentPane().add(btnPanel, BorderLayout.SOUTH);

        // Load profile from server
        try (Socket s = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"))
        ) {
            out.write("GET_PROFILE|" + id); out.newLine(); out.flush();
            String resp = in.readLine();
            if (resp != null && resp.startsWith("OK|PROFILE")) {
                String[] parts = resp.split("\\|", 6);
                // OK|PROFILE|id|username|email|fullname|active (note: earlier code had 5 parts, be robust)
                // We'll attempt to parse safely
                if (parts.length >= 6) {
                    valEmail.setText(parts[4]);
                    valFull.setText(parts[5]);
                    valStatus.setText("Online");
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(home, "Lỗi tải profile: " + ex.getMessage());
        }

        btnChangePass.addActionListener(e -> {
            JDialog d = new JDialog(home, "Đổi mật khẩu", true);
            d.setSize(380, 260);
            d.setLocationRelativeTo(home);
            JPanel p = new JPanel(new GridBagLayout());
            GridBagConstraints cc = new GridBagConstraints();
            cc.insets = new Insets(6,6,6,6);
            cc.fill = GridBagConstraints.HORIZONTAL;

            JPasswordField cur = new JPasswordField(16);
            JPasswordField np = new JPasswordField(16);
            JPasswordField np2 = new JPasswordField(16);

            cc.gridx=0; cc.gridy=0; p.add(new JLabel("Mật khẩu hiện tại:"), cc);
            cc.gridx=1; p.add(cur, cc);
            cc.gridx=0; cc.gridy=1; p.add(new JLabel("Mật khẩu mới:"), cc);
            cc.gridx=1; p.add(np, cc);
            cc.gridx=0; cc.gridy=2; p.add(new JLabel("Xác nhận mật khẩu:"), cc);
            cc.gridx=1; p.add(np2, cc);

            JButton ok = new JButton("Đổi mật khẩu");
            JButton cancel = new JButton("Hủy");
            JPanel pb = new JPanel(); pb.add(ok); pb.add(cancel);
            cc.gridx=0; cc.gridy=3; cc.gridwidth=2; p.add(pb, cc);

            ok.addActionListener(a -> {
                String curp = new String(cur.getPassword());
                String newp = new String(np.getPassword());
                String newp2 = new String(np2.getPassword());
                if (curp.isEmpty() || newp.isEmpty()) { JOptionPane.showMessageDialog(d, "Nhập đủ thông tin"); return; }
                if (!newp.equals(newp2)) { JOptionPane.showMessageDialog(d, "Mật khẩu mới không khớp"); return; }
                try (Socket s = new Socket(SERVER_HOST, SERVER_PORT);
                     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
                     BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"))
                ) {
                    String req = String.join("|", "CHANGE_PASS", String.valueOf(id), curp, newp);
                    out.write(req); out.newLine(); out.flush();
                    String resp = in.readLine();
                    if (resp != null && resp.startsWith("OK|CHANGE_PASS")) {
                        JOptionPane.showMessageDialog(d, "Đổi mật khẩu thành công");
                        d.dispose();
                    } else {
                        JOptionPane.showMessageDialog(d, "Đổi mật khẩu thất bại");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(d, "Lỗi: " + ex.getMessage());
                }
            });

            cancel.addActionListener(a -> d.dispose());
            d.getContentPane().add(p);
            d.setVisible(true);
        });

        btnUpdate.addActionListener(e -> {
            JDialog d = new JDialog(home, "Cập nhật thông tin", true);
            d.setSize(420, 280);
            d.setLocationRelativeTo(home);
            JPanel p = new JPanel(new GridBagLayout());
            GridBagConstraints cc = new GridBagConstraints();
            cc.insets = new Insets(6,6,6,6);
            cc.fill = GridBagConstraints.HORIZONTAL;

            JTextField fEmail = new JTextField(valEmail.getText(), 20);
            JTextField fFull = new JTextField(valFull.getText(), 20);

            cc.gridx=0; cc.gridy=0; p.add(new JLabel("Email:"), cc);
            cc.gridx=1; p.add(fEmail, cc);
            cc.gridx=0; cc.gridy=1; p.add(new JLabel("Họ và tên:"), cc);
            cc.gridx=1; p.add(fFull, cc);

            JButton ok = new JButton("Cập nhật");
            JButton cancel = new JButton("Hủy");
            JPanel pb = new JPanel(); pb.add(ok); pb.add(cancel);
            cc.gridx=0; cc.gridy=2; cc.gridwidth=2; p.add(pb, cc);

            ok.addActionListener(a -> {
                String email = fEmail.getText().trim();
                String full = fFull.getText().trim();
                try (Socket s = new Socket(SERVER_HOST, SERVER_PORT);
                     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
                     BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"))
                ) {
                    String req = String.join("|", "UPDATE_PROFILE", String.valueOf(id), email, full);
                    out.write(req); out.newLine(); out.flush();
                    String resp = in.readLine();
                    if (resp != null && resp.startsWith("OK|UPDATE_PROFILE")) {
                        JOptionPane.showMessageDialog(d, "Cập nhật thành công");
                        valEmail.setText(email);
                        valFull.setText(full);
                        d.dispose();
                    } else {
                        JOptionPane.showMessageDialog(d, "Cập nhật thất bại");
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(d, "Lỗi: " + ex.getMessage());
                }
            });

            cancel.addActionListener(a -> d.dispose());
            d.getContentPane().add(p);
            d.setVisible(true);
        });

        btnLogout.addActionListener(e -> {
            try (Socket s = new Socket(SERVER_HOST, SERVER_PORT);
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"))
            ) {
                out.write("LOGOUT|" + id); out.newLine(); out.flush();
            } catch (IOException ignored) {}
            home.dispose();
        });

        home.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientApp c = new ClientApp();
            c.setVisible(true);
        });
    }
}

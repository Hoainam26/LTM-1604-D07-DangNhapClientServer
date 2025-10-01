package BTL;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField tfUser;
    private JPasswordField pfPass;

    public LoginFrame() {
        // --- Cài đặt cơ bản của Frame ---
        setTitle("Đăng nhập - Hệ thống quản lý người dùng");
        setSize(450, 480); // Tăng kích thước nhẹ
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // --- Panel Chính (Chứa mọi thứ) ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 20));
        mainPanel.setBackground(new Color(236, 240, 241)); // Màu nền xám nhạt
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // --- Phần Tiêu đề (Header) ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(mainPanel.getBackground());

        JLabel lblTitle = new JLabel(" Đăng nhập hệ thống", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(new Color(44, 62, 80)); // Màu chữ gần đen
        headerPanel.add(lblTitle);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Phần Trung tâm (Form Đăng nhập) ---
        // Đặt Form vào một Panel con để tạo hiệu ứng "Card"
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1), // Viền ngoài
            new EmptyBorder(25, 35, 25, 35) // Padding bên trong
        ));
        
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 0, 10, 0); // Khoảng cách giữa các thành phần
        c.fill = GridBagConstraints.HORIZONTAL;

        // --- 1. Tên đăng nhập ---
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1.0;
        JLabel lblUser = new JLabel("Tên đăng nhập:");
        lblUser.setFont(new Font("Arial", Font.BOLD, 14));
        formContainer.add(lblUser, c);
        
        c.gridy = 1;
        tfUser = new JTextField(15);
        styleTextField(tfUser);
        formContainer.add(tfUser, c);

        // --- 2. Mật khẩu ---
        c.gridy = 2;
        JLabel lblPass = new JLabel("Mật khẩu:");
        lblPass.setFont(new Font("Arial", Font.BOLD, 14));
        formContainer.add(lblPass, c);
        
        c.gridy = 3;
        pfPass = new JPasswordField(15);
        styleTextField(pfPass);
        formContainer.add(pfPass, c);

        // --- 3. Checkbox ---
        c.gridy = 4;
        c.insets = new Insets(5, 0, 5, 0); 
        JCheckBox cbShow = new JCheckBox("Hiển thị mật khẩu");
        cbShow.setFont(new Font("Arial", Font.PLAIN, 13));
        cbShow.setBackground(Color.WHITE);
        cbShow.addActionListener(e -> pfPass.setEchoChar(cbShow.isSelected() ? (char)0 : '•'));
        formContainer.add(cbShow, c);

        mainPanel.add(formContainer, BorderLayout.CENTER);

        // --- Phần Chân (Các nút bấm) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(mainPanel.getBackground());
        
        JButton btnLogin = new JButton("Đăng nhập");
        styleButton(btnLogin, new Color(52, 152, 219), Color.WHITE); // Xanh dương sáng hơn
        
        JButton btnReg = new JButton("Đăng ký");
        styleButton(btnReg, new Color(149, 165, 166), Color.WHITE);

        bottomPanel.add(btnLogin);
        bottomPanel.add(btnReg);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.add(mainPanel);

        // --- Xử lý sự kiện (Giữ nguyên logic) ---
        btnLogin.addActionListener(e -> doLogin());
        btnReg.addActionListener(e -> {
            RegisterFrame rf = new RegisterFrame();
            rf.setVisible(true);
        });
        
        // Thêm sự kiện nhấn Enter để Đăng nhập
        Action loginAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        };
        tfUser.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "login");
        tfUser.getActionMap().put("login", loginAction);
        pfPass.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "login");
        pfPass.getActionMap().put("login", loginAction);

        setVisible(true);
    }
    
    // Phương thức trợ giúp để tạo kiểu cho TextField/PasswordField chuyên nghiệp hơn
    private void styleTextField(JTextComponent field) {
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 38));
        
        // Viền hiện đại: Viền mỏng, padding bên trong
        Border line = BorderFactory.createLineBorder(new Color(189, 195, 199)); // Màu xám nhạt
        Border margin = new EmptyBorder(5, 10, 5, 10); // Padding bên trong text
        field.setBorder(new CompoundBorder(line, margin));
    }

    // Phương thức trợ giúp để tạo kiểu cho nút bấm
    private void styleButton(JButton button, Color background, Color foreground) {
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30)); // Tăng padding nút
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false); // Quan trọng để hiển thị màu nền
    }

    /**
     * Phương thức xử lý logic đăng nhập (Được giữ nguyên theo yêu cầu).
     */
    private void doLogin() {
        String u = tfUser.getText().trim();
        String p = new String(pfPass.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập username & password");
            return;
        }
        String req = "LOGIN;" + u + ";" + p;
        String resp = NetworkUtil.sendRequest(req);
        if (resp.startsWith("SUCCESS")) {
            String[] parts = resp.split(";", -1);
            String role = parts[1];
            String fullname = parts.length>2 ? parts[2] : "";
            String email = parts.length>3 ? parts[3] : "";
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công: " + role);
            if ("ADMIN".equals(role)) {
                AdminDashboard ad = new AdminDashboard(u);
                ad.setVisible(true);
            } else {
                UserDashboard ud = new UserDashboard(u, fullname, email);
                ud.setVisible(true);
            }
            this.dispose();
        } else if (resp.startsWith("FAIL")) {
            String[] parts = resp.split(";", 2);
            JOptionPane.showMessageDialog(this, "Login failed: " + (parts.length>1?parts[1]:""));
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi server: " + resp);
        }
    }
}

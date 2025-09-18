package BTL;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.io.File;


public class ServerMain extends JFrame {
    private final JButton btnStart = new JButton("Khởi động Server");
    private final JButton btnStop = new JButton("Dừng Server");
    private final JButton btnAddUser = new JButton("Thêm User");
    private final JButton btnDelete = new JButton("Xóa User");
    private final JButton btnChangeRole = new JButton("Sửa Vai Trò");
    private final JButton btnResetPass = new JButton("Reset Mật Khẩu");

    private final JTable table = new JTable();
    private final DefaultTableModel model = new DefaultTableModel();
    private final JTextArea taLog = new JTextArea(8, 60);

    private UserStore store;
    private ServerWorker worker;
    private AdminLogger logger;

    public ServerMain() {
        // Áp dụng giao diện Nimbus (nếu có)
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        setTitle("Quản lý Server - Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);

        // --- Thanh nút điều khiển ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        styleButton(btnStart, new Color(72, 201, 176), Color.WHITE);      // xanh ngọc
        styleButton(btnStop, new Color(178, 34, 34), Color.WHITE);        // đỏ
        styleButton(btnAddUser, new Color(144, 238, 144), Color.BLACK);  // xanh lá nhạt
        styleButton(btnDelete, new Color(255, 160, 122), Color.BLACK);   // cam
        styleButton(btnChangeRole, new Color(100, 149, 237), Color.WHITE); // xanh dương
        styleButton(btnResetPass, new Color(255, 215, 0), Color.BLACK);    // vàng

        top.add(btnStart);
        top.add(btnStop);
        top.add(btnAddUser);
        top.add(btnDelete);
        top.add(btnChangeRole);
        top.add(btnResetPass);

        // --- Bảng hiển thị User ---
        model.setColumnIdentifiers(new Object[]{"ID", "Username", "Email", "Họ tên", "Trạng thái", "Ngày tạo", "Vai Trò"});
        table.setModel(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(120, 144, 156));
        header.setForeground(Color.WHITE);

        JScrollPane spTable = new JScrollPane(table);

        // --- Log panel ---
        taLog.setEditable(false);
        taLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        taLog.setBackground(new Color(245, 245, 245));
        taLog.setForeground(new Color(0, 102, 102));
        JScrollPane spLog = new JScrollPane(taLog);
        spLog.setBorder(BorderFactory.createTitledBorder("Server Logs"));

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(spTable, BorderLayout.CENTER);
        center.add(spLog, BorderLayout.SOUTH);

        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(center, BorderLayout.CENTER);

        logger = new AdminLogger(taLog);

        btnStop.setEnabled(false);

        btnStart.addActionListener(e -> startServer());
        btnStop.addActionListener(e -> stopServer());
        btnAddUser.addActionListener(e -> addUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnChangeRole.addActionListener(e -> changeRole());
        btnResetPass.addActionListener(e -> resetPassword());

        try {
            // ✅ Đường dẫn tuyệt đối tới file CSV
            String filePath = "C:/Users/ACER/eclipse-workspace/LTM/src/BTL/users.csv";
            File f = new File(filePath);
            if (!f.exists()) {
                f.createNewFile(); // tạo file trống nếu chưa tồn tại
            }
            store = new UserStore(filePath);
            refreshList();
            logger.log("UserStore loaded -> " + f.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Cannot init user store: " + ex.getMessage());
        }
    }
    

    private void startServer() {
        if (worker != null && worker.isRunning()) {
            JOptionPane.showMessageDialog(this, "Server đã chạy");
            return;
        }
        try {
            worker = new ServerWorker(5000, store, logger);

            // Tự động làm mới khi có client đăng nhập
            worker.setClientConnectionListener(client -> {
                SwingUtilities.invokeLater(() -> {
                    refreshList();
                    logger.log("Auto refresh: client mới " + client.getRemoteSocketAddress());
                });
            });

            worker.start();
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            logger.log("ServerWorker started");
        } catch (Exception ex) {
            logger.log("Start server error: " + ex.getMessage());
        }
    }

    private void stopServer() {
        if (worker != null) {
            worker.stop();
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        }
    }

    private void refreshList() {
        model.setRowCount(0);
        List<User> all = store.listAll();
        for (User u : all) {
            String status = u.isActive() ? "Online" : "Offline"; // ✅ hiển thị chữ thay vì true/false
            model.addRow(new Object[]{
                    u.getId(), u.getUsername(), u.getEmail(),
                    u.getFullname(), status, u.getCreatedAt(), u.getRole()
            });
        }
        logger.log("Refreshed user list");
    }

    private void addUser() {
        JDialog d = new JDialog(this, "Thêm User mới", true);
        d.setSize(400, 350);
        d.setLocationRelativeTo(this);
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField fUser = new JTextField(20);
        JPasswordField fPass = new JPasswordField(20);
        JTextField fEmail = new JTextField(20);
        JTextField fFull = new JTextField(20);
        String[] roles = {"user", "admin"};
        JComboBox<String> cbRole = new JComboBox<>(roles);

        c.gridx=0; c.gridy=0; p.add(new JLabel("Tên đăng nhập:"), c);
        c.gridx=1; p.add(fUser, c);
        c.gridx=0; c.gridy=1; p.add(new JLabel("Mật khẩu:"), c);
        c.gridx=1; p.add(fPass, c);
        c.gridx=0; c.gridy=2; p.add(new JLabel("Email:"), c);
        c.gridx=1; p.add(fEmail, c);
        c.gridx=0; c.gridy=3; p.add(new JLabel("Họ và tên:"), c);
        c.gridx=1; p.add(fFull, c);
        c.gridx=0; c.gridy=4; p.add(new JLabel("Vai trò:"), c);
        c.gridx=1; p.add(cbRole, c);

        JButton bOK = new JButton("Thêm");
        JButton bCancel = new JButton("Hủy");
        JPanel pb = new JPanel(); pb.add(bOK); pb.add(bCancel);
        c.gridx=0; c.gridy=5; c.gridwidth=2; p.add(pb, c);

        d.getContentPane().add(p);

        bCancel.addActionListener(e -> d.dispose());
        bOK.addActionListener(e -> {
            String u = fUser.getText().trim();
            String p1 = new String(fPass.getPassword());
            String email = fEmail.getText().trim();
            String full = fFull.getText().trim();
            String role = (String) cbRole.getSelectedItem();

            if (u.isEmpty() || p1.isEmpty()) {
                JOptionPane.showMessageDialog(d, "Nhập đủ username và mật khẩu");
                return;
            }
            try {
                User newU = store.register(u, p1, email, full);
                if (newU != null) {
                    // chỉnh vai trò
                    store.changeRole(newU.getId(), role);
                    logger.log("Added new user " + u + " với vai trò " + role);
                    refreshList();
                    JOptionPane.showMessageDialog(d, "Thêm user thành công!");
                    d.dispose();
                } else {
                    JOptionPane.showMessageDialog(d, "Username đã tồn tại!");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(d, "Lỗi khi thêm user: " + ex.getMessage());
            }
        });

        d.setVisible(true);
    }

    private void deleteUser() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Chọn user để xóa");
            return;
        }
        int id = (int) model.getValueAt(r, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa user id=" + id + " ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = store.deleteUser(id);
                if (ok) {
                    logger.log("Deleted user id " + id);
                    refreshList();
                } else logger.log("Delete failed for id " + id);
            } catch (IOException ex) {
                logger.log("Delete error: " + ex.getMessage());
            }
        }
    }

    private void changeRole() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Chọn user để đổi vai trò");
            return;
        }
        int id = (int) model.getValueAt(r, 0);
        String[] roles = {"admin", "user"};
        String role = (String) JOptionPane.showInputDialog(this, "Chọn vai trò mới:",
                "Đổi vai trò", JOptionPane.QUESTION_MESSAGE, null, roles, roles[1]);
        if (role != null) {
            try {
                boolean ok = store.changeRole(id, role);
                if (ok) {
                    logger.log("Changed role of user id " + id + " -> " + role);
                    refreshList();
                } else logger.log("Change role failed for id " + id);
            } catch (IOException ex) {
                logger.log("Change role error: " + ex.getMessage());
            }
        }
    }

    private void resetPassword() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Chọn user để reset mật khẩu");
            return;
        }
        int id = (int) model.getValueAt(r, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Reset mật khẩu cho user id=" + id + " về 123456?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = store.resetPassword(id);
                if (ok) {
                    logger.log("Reset password for user id " + id);
                    JOptionPane.showMessageDialog(this, "Mật khẩu đã reset về 123456");
                } else logger.log("Reset password failed for id " + id);
            } catch (IOException ex) {
                logger.log("Reset password error: " + ex.getMessage());
            }
        }
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(150, 35));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerMain m = new ServerMain();
            m.setVisible(true);
        });
    }
}

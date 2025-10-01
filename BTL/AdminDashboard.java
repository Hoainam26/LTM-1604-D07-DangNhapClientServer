package BTL;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

// ************ THÊM DÒNG IMPORT CHO LỚP CHARTFRAME ************
import BTL.ChartFrame; // Đảm bảo ChartFrame.java nằm trong cùng package BTL

public class AdminDashboard extends JFrame {
    private String adminUser;
    private JTable table;
    private DefaultTableModel model;
    private JLabel statusBar;
    private TableRowSorter<DefaultTableModel> sorter; // dùng cho filter/search
    private JTextField searchField;
    private JComboBox<String> roleFilter;
    private JComboBox<String> statusFilter;

    // 🎨 Định nghĩa bảng màu
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private final Color ACCENT_LIGHT = new Color(52, 152, 219);
    private final Color BACKGROUND_GRAY = new Color(244, 246, 249);
    private final Color SIDEBAR_BG = new Color(236, 240, 241);
    private final Color TEXT_DARK = new Color(44, 62, 80);

    public AdminDashboard(String adminUser) {
        this.adminUser = adminUser;
        setTitle("Admin Dashboard - Hệ thống Quản lý Người dùng");
        setSize(1300, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_GRAY);

        // ===== HEADER =====
        JLabel header = new JLabel("QUẢN LÝ NGƯỜI DÙNG", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setBackground(PRIMARY_BLUE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(header, BorderLayout.NORTH);

        // ===== MAIN CONTENT =====
        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainContent.setBackground(BACKGROUND_GRAY);

        // ----- SIDEBAR -----
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        sidebar.setPreferredSize(new Dimension(220, 0));

        JLabel sideTitle = new JLabel("Chức năng");
        sideTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sideTitle.setForeground(TEXT_DARK);
        sideTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(sideTitle);
        sidebar.add(Box.createVerticalStrut(20));

        JButton btnReload = styledSidebarButton("  Tải lại dữ liệu");
        JButton btnCreate = styledSidebarButton("  Tạo người dùng");
        JButton btnEdit   = styledSidebarButton("  Chỉnh sửa User");
        JButton btnLock   = styledSidebarButton("  Khóa / Mở User");
        JButton btnDelete = styledSidebarButton("  Xóa User ❌");
        JButton btnChart  = styledSidebarButton("  Xem Biểu đồ");
        JButton btnLogs   = styledSidebarButton("  Xem Nhật ký");

        sidebar.add(btnReload); sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnCreate); sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnEdit);   sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnLock);   sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnDelete); sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(btnChart);  sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnLogs);   sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(Box.createVerticalGlue());

        mainContent.add(sidebar, BorderLayout.WEST);

        // ----- TABLE + TOOLBAR -----
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(BACKGROUND_GRAY);

        // Toolbar chứa Search + Filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(BACKGROUND_GRAY);

        searchField = new JTextField(20);
        searchField.setToolTipText("Tìm kiếm theo Tên/Email");
        filterPanel.add(new JLabel("Tìm kiếm:"));
        filterPanel.add(searchField);

        roleFilter = new JComboBox<>(new String[]{"Tất cả vai trò", "USER", "ADMIN"});
        filterPanel.add(new JLabel("Vai trò:"));
        filterPanel.add(roleFilter);

        statusFilter = new JComboBox<>(new String[]{"Tất cả trạng thái", "ACTIVE", "LOCKED"});
        filterPanel.add(new JLabel("Trạng thái:"));
        filterPanel.add(statusFilter);

        centerPanel.add(filterPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(
            new Object[]{"ID","Tên đăng nhập","Họ và tên","Email","Vai trò","Trạng thái"},0
        ) { public boolean isCellEditable(int r,int c){ return false; } };

        table = new JTable(model);
        styleTable(table, PRIMARY_BLUE, ACCENT_LIGHT);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(table);
        centerPanel.add(scroll, BorderLayout.CENTER);

        mainContent.add(centerPanel, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // ===== STATUS BAR =====
        statusBar = new JLabel("  Đăng nhập với tư cách: " + adminUser + " (ADMIN)");
        statusBar.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        statusBar.setForeground(TEXT_DARK);
        statusBar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        statusBar.setOpaque(true);
        statusBar.setBackground(SIDEBAR_BG);
        add(statusBar, BorderLayout.SOUTH);

        // ===== SỰ KIỆN =====
        btnReload.addActionListener(e -> loadUsers());
        btnCreate.addActionListener(e -> createUser());
        btnEdit.addActionListener(e -> editUser());
        btnLock.addActionListener(e -> toggleLock());
        btnDelete.addActionListener(e -> deleteUser());
        btnChart.addActionListener(e -> showChart());
        btnLogs.addActionListener(e -> showLogs());

        // Search + Filter
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e){ applyFilters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e){ applyFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e){ applyFilters(); }
        });
        roleFilter.addActionListener(e -> applyFilters());
        statusFilter.addActionListener(e -> applyFilters());

        loadUsers();
    }

    // ================== LOGIC CƠ BẢN ==================

    private void loadUsers() {
        model.setRowCount(0);
        String resp = NetworkUtil.sendRequest("GET_USERS");
        if (resp.startsWith("USERS")) {
            String[] parts = resp.split(";", -1);
            for (int i = 1; i < parts.length; i++) {
                String rec = parts[i];
                String[] fields = rec.split("\\|", -1);
                if (fields.length >= 6) {
                    model.addRow(new Object[]{fields[0], unescape(fields[1]), unescape(fields[2]), unescape(fields[3]), fields[4], fields[5]});
                }
            }
            statusBar.setText("  Đã tải " + (parts.length - 1) + " người dùng. Đăng nhập với tư cách: " + adminUser + " (ADMIN)");
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi load users: " + resp, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ================== CẬP NHẬT: Tạo User (Dùng UserDialog) ==================
    private void createUser() {
        // Tạo dialog mới, truyền null vì là chế độ Tạo mới
        UserDialog dialog = new UserDialog(this, "➕ Tạo người dùng Mới", null);
        dialog.setVisible(true); // Hiển thị dialog và chờ

        if (dialog.isConfirmed()) {
            String username = dialog.getUsername();
            String password = dialog.getPassword();
            String fullname = dialog.getFullname();
            String email = dialog.getEmail();
            String role = dialog.getRole();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập và Mật khẩu không được để trống.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Gửi request tới server
            String req = "CREATE_USER;" + username + ";" + password + ";" + fullname + ";" + email + ";" + role;
            String resp = NetworkUtil.sendRequest(req);
            
            // Hiển thị kết quả và tải lại danh sách
            JOptionPane.showMessageDialog(this, resp, "Kết quả Tạo", resp.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            loadUsers();
        }
    }

    // ================== CẬP NHẬT: Chỉnh sửa User (Dùng UserDialog) ==================
    private void editUser() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { 
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để chỉnh sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); 
            return; 
        }
        
        // Chuyển đổi từ row hiển thị sang row trong model
        int modelRow = table.convertRowIndexToModel(viewRow);
        
        // Lấy dữ liệu user hiện tại
        String[] currentData = new String[6];
        for (int i = 0; i < 6; i++) {
            currentData[i] = model.getValueAt(modelRow, i).toString();
        }

        // Tạo mảng dữ liệu để truyền vào dialog (chỉ cần 5 trường đầu tiên)
        String[] dialogData = {
            currentData[0], // ID
            currentData[1], // Username
            currentData[2], // Fullname
            currentData[3], // Email
            currentData[4]  // Role
        };
        
        // Tạo dialog mới, truyền dữ liệu user vào
        UserDialog dialog = new UserDialog(this, "📝 Chỉnh sửa Thông tin User", dialogData);
        dialog.setVisible(true); // Hiển thị dialog và chờ

        if (dialog.isConfirmed()) {
            int id = dialog.getUserId();
            String username = dialog.getUsername(); // Không đổi
            String newFullname = dialog.getFullname();
            String newEmail = dialog.getEmail();
            String newRole = dialog.getRole();
            
            // server's EDIT_USER expects: EDIT_USER;id;username;fullname;email;role
            String req = "EDIT_USER;" + id + ";" + username + ";" + newFullname + ";" + newEmail + ";" + newRole;
            String resp = NetworkUtil.sendRequest(req);
            
            // Hiển thị kết quả và tải lại danh sách
            JOptionPane.showMessageDialog(this, resp, "Kết quả Chỉnh sửa", resp.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            loadUsers();
        }
    }

    private void toggleLock() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để Khóa/Mở.", "Cảnh báo", JOptionPane.WARNING_MESSAGE); return; }
        
        int modelRow = table.convertRowIndexToModel(row);
        int id = Integer.parseInt(model.getValueAt(modelRow,0).toString());
        String currentStatus = model.getValueAt(modelRow, 5).toString();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn chuyển trạng thái của User ID: " + id + " (" + currentStatus + ")?", 
            "Xác nhận Khóa/Mở", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String resp = NetworkUtil.sendRequest("TOGGLE_LOCK;" + id);
            JOptionPane.showMessageDialog(this, resp, "Kết quả", resp.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            loadUsers();
        }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) { 
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một user để xóa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return; 
        }
        int id = Integer.parseInt(model.getValueAt(table.convertRowIndexToModel(row),0).toString());
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa User ID: " + id + "?", 
            "Xác nhận Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            String resp = NetworkUtil.sendRequest("DELETE_USER;" + id);
            JOptionPane.showMessageDialog(this, resp, "Kết quả", resp.startsWith("SUCCESS") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            loadUsers();
        }
    }

    // ================== CẬP NHẬT: HÀM HIỂN THỊ BIỂU ĐỒ ==================
    private void showChart() {
        String r = NetworkUtil.sendRequest("GET_STATS");
        if (!r.startsWith("STATS")) {
            JOptionPane.showMessageDialog(
                this,
                "Lỗi khi lấy dữ liệu thống kê: " + r,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Tách dữ liệu từ phản hồi
        String[] p = r.split(";", -1);
        int total = Integer.parseInt(p[1]);
        int locked = Integer.parseInt(p[2]);

        // Tạo danh sách lựa chọn kiểu biểu đồ
        String[] options = {
            "Biểu đồ tròn (Pie)",
            "Biểu đồ cột (Bar)",
            "Cả hai"
        };

        String choice = (String) JOptionPane.showInputDialog(
            this,
            "Chọn loại biểu đồ muốn hiển thị:",
            "Chọn kiểu biểu đồ",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[2] // Đặt mặc định là "Cả hai"
        );

        // Nếu người dùng bấm Cancel
        if (choice == null) {
            return;
        }
        
        // 1. Xác định tham số chartType truyền vào ChartFrame
        String chartTypeParam;
        switch (choice) {
            case "Biểu đồ tròn (Pie)":
                chartTypeParam = "PIE";
                break;
            case "Biểu đồ cột (Bar)":
                chartTypeParam = "BAR";
                break;
            case "Cả hai":
                chartTypeParam = "BOTH";
                break;
            default:
                return;
        }

        // 2. Tạo và hiển thị ChartFrame
        // Lớp ChartFrame được giả định là lớp bạn đã phát triển bằng Graphics2D
        new ChartFrame(total, locked, chartTypeParam).setVisible(true); 
    }
    // ====================================================================


    private void showLogs() {
        // Logic showLogs giữ nguyên
        String resp = NetworkUtil.sendRequest("GET_LOGS");
        if (resp.startsWith("LOGS;")) {
            String data = resp.substring(5);
            String[] records = data.split(";;");
            JTextArea ta = new JTextArea(20,80);
            ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); // Dùng font đơn cách cho log
            ta.setBackground(new Color(240, 240, 240));
            ta.setForeground(TEXT_DARK);
            for (String rec : records) {
                if (rec.trim().isEmpty()) continue;
                ta.append(rec.replace("&#44;", ",") + "\n");
            }
            ta.setEditable(false);
            
            // Tăng kích thước hộp thoại log
            JScrollPane scroll = new JScrollPane(ta);
            scroll.setPreferredSize(new Dimension(800, 500)); 
            
            JOptionPane.showMessageDialog(this, scroll, "Nhật ký Hệ thống", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lấy Nhật ký: " + resp, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================== Bộ lọc & Tìm kiếm ==================
    private void applyFilters() {
        RowFilter<DefaultTableModel, Object> rf = new RowFilter<>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String searchText = searchField.getText().trim().toLowerCase();
                String roleSel = roleFilter.getSelectedItem().toString();
                String statusSel = statusFilter.getSelectedItem().toString();

                String username = entry.getStringValue(1).toLowerCase();
                String fullname = entry.getStringValue(2).toLowerCase();
                String email = entry.getStringValue(3).toLowerCase();
                String role = entry.getStringValue(4);
                String status = entry.getStringValue(5);

                boolean matchSearch = searchText.isEmpty() ||
                    username.contains(searchText) ||
                    fullname.contains(searchText) ||
                    email.contains(searchText);

                boolean matchRole = roleSel.equals("Tất cả vai trò") || role.equalsIgnoreCase(roleSel);
                boolean matchStatus = statusSel.equals("Tất cả trạng thái") || status.equalsIgnoreCase(statusSel);

                return matchSearch && matchRole && matchStatus;
            }
        };
        sorter.setRowFilter(rf);
    }

    // ================== STYLE METHODS ==================
    private JButton styledSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(ACCENT_LIGHT);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(PRIMARY_BLUE); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(ACCENT_LIGHT); }
        });
        return btn;
    }

    private void styleTable(JTable table, Color headerBg, Color selectBg) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(headerBg);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionBackground(new Color(171, 183, 183));
        table.setSelectionForeground(Color.WHITE);
    }

    private String unescape(String s) {
        return s.replace("&#44;", ",");
    }
    
    // ================== LỚP NỘI BỘ: UserDialog (Giao diện Tạo/Sửa User mới) ==================
    /**
     * Lớp JDialog tùy chỉnh để tạo và chỉnh sửa người dùng.
     */
    public class UserDialog extends JDialog {
        private JTextField tfUser, tfFull, tfEmail;
        private JPasswordField pfPass;
        private JComboBox<String> cbRole;
        private int userId = -1; // -1 cho Tạo mới, ID > 0 cho Chỉnh sửa
        private boolean confirmed = false;

        public UserDialog(JFrame owner, String title, String[] userData) {
            super(owner, title, true); // true: modal dialog
            setupUI(userData);
            pack();
            setLocationRelativeTo(owner);
            setResizable(false);
        }

        private void setupUI(String[] userData) {
            // --- Cấu hình Panel chính ---
            JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            mainPanel.setBackground(BACKGROUND_GRAY);
            
            // --- Form Panel (sử dụng GridBagLayout) ---
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_BLUE, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;

            // --- Khởi tạo các trường nhập liệu ---
            tfUser = new JTextField(20);
            pfPass = new JPasswordField(20);
            tfFull = new JTextField(20);
            tfEmail = new JTextField(20);
            cbRole = new JComboBox<>(new String[]{"USER", "ADMIN"});

            // Đặt font và màu sắc
            Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
            Font textFont = new Font("Segoe UI", Font.PLAIN, 14);
            
            tfUser.setFont(textFont); tfFull.setFont(textFont);
            tfEmail.setFont(textFont); pfPass.setFont(textFont);
            cbRole.setFont(textFont);

            // --- Điền dữ liệu cho chế độ Chỉnh sửa ---
            boolean isEditMode = userData != null;
            if (isEditMode) {
                userId = Integer.parseInt(userData[0]);
                tfUser.setText(userData[1]);
                tfFull.setText(userData[2]);
                tfEmail.setText(userData[3]);
                cbRole.setSelectedItem(userData[4]);
                
                // Trong chế độ chỉnh sửa:
                tfUser.setEditable(false); // Không cho sửa Username
                pfPass.setVisible(false); // Ẩn trường mật khẩu
                
                // Row 1: ID
                gbc.gridx = 0; gbc.gridy = 0; formPanel.add(styledLabel("ID:", labelFont), gbc);
                gbc.gridx = 1; gbc.gridy = 0; formPanel.add(styledDisplayLabel(String.valueOf(userId), textFont), gbc);
                
                // Row 2: Username
                gbc.gridx = 0; gbc.gridy = 1; formPanel.add(styledLabel("Username (Không sửa):", labelFont), gbc);
                gbc.gridx = 1; gbc.gridy = 1; formPanel.add(tfUser, gbc);
            } else {
                // Row 1: Username
                gbc.gridx = 0; gbc.gridy = 0; formPanel.add(styledLabel("Username:", labelFont), gbc);
                gbc.gridx = 1; gbc.gridy = 0; formPanel.add(tfUser, gbc);
                
                // Row 2: Password (chỉ có trong Tạo mới)
                gbc.gridx = 0; gbc.gridy = 1; formPanel.add(styledLabel("Password:", labelFont), gbc);
                gbc.gridx = 1; gbc.gridy = 1; formPanel.add(pfPass, gbc);
            }
            
            // Căn chỉnh các row tiếp theo
            int startRow = isEditMode ? 2 : 2;

            // Row Fullname
            gbc.gridx = 0; gbc.gridy = startRow; formPanel.add(styledLabel("Họ và tên:", labelFont), gbc);
            gbc.gridx = 1; gbc.gridy = startRow; formPanel.add(tfFull, gbc);

            // Row Email
            gbc.gridx = 0; gbc.gridy = startRow + 1; formPanel.add(styledLabel("Email:", labelFont), gbc);
            gbc.gridx = 1; gbc.gridy = startRow + 1; formPanel.add(tfEmail, gbc);
            
            // Row Role
            gbc.gridx = 0; gbc.gridy = startRow + 2; formPanel.add(styledLabel("Vai trò:", labelFont), gbc);
            gbc.gridx = 1; gbc.gridy = startRow + 2; formPanel.add(cbRole, gbc);
            
            mainPanel.add(formPanel, BorderLayout.CENTER);

            // --- Button Panel ---
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBackground(BACKGROUND_GRAY);
            
            JButton btnOK = styledDialogButton(isEditMode ? "💾 Cập nhật" : "➕ Tạo User");
            JButton btnCancel = styledDialogButton("❌ Hủy bỏ");
            
            btnOK.addActionListener(e -> { confirmed = true; dispose(); });
            btnCancel.addActionListener(e -> dispose());
            
            buttonPanel.add(btnOK);
            buttonPanel.add(btnCancel);
            
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            setContentPane(mainPanel);
        }
        
        // --- Helper Styling Methods (Nằm trong UserDialog) ---
        private JLabel styledLabel(String text, Font font) {
            JLabel label = new JLabel(text);
            label.setFont(font);
            label.setForeground(TEXT_DARK);
            return label;
        }
        
        private JLabel styledDisplayLabel(String text, Font font) {
            JLabel label = new JLabel(text);
            label.setFont(font);
            label.setForeground(PRIMARY_BLUE);
            return label;
        }

        private JButton styledDialogButton(String text) {
            JButton btn = new JButton(text);
            btn.setFocusPainted(false);
            btn.setBackground(PRIMARY_BLUE);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setPreferredSize(new Dimension(130, 35));
            btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(ACCENT_LIGHT); }
                public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(PRIMARY_BLUE); }
            });
            return btn;
        }

        // --- Getters ---
        public boolean isConfirmed() { return confirmed; }
        public int getUserId() { return userId; }
        public String getUsername() { return tfUser.getText().trim(); }
        public String getPassword() { return new String(pfPass.getPassword()); }
        public String getFullname() { return tfFull.getText().trim(); }
        public String getEmail() { return tfEmail.getText().trim(); }
        public String getRole() { return (String) cbRole.getSelectedItem(); }
    }
    // ================== KẾT THÚC LỚP NỘI BỘ UserDialog ==================
}
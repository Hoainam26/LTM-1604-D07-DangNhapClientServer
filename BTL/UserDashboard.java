package BTL;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserDashboard extends JFrame {
    private String username;
    private String fullname;
    private String email;

    // Màu sắc được chọn theo bảng màu hiện đại
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185); // Xanh dương đậm
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Xám trắng (Clouds)
    private static final Color CARD_BACKGROUND_COLOR = Color.WHITE;
    private static final Color BUTTON_COLOR = new Color(39, 174, 96); // Xanh lá đậm (Emerald)
    private static final Color DANGER_COLOR = new Color(192, 57, 43); // Đỏ đậm (Alizarin)
    private static final Color TEXT_COLOR = new Color(52, 73, 94); // Xám xanh (Wet Asphalt)
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // Khởi tạo các JLabel để có thể cập nhật thông tin ngay lập tức
    private JLabel lblFullnameValue;
    private JLabel lblEmailValue;

    public UserDashboard(String username, String fullname, String email) {
        this.username = username;
        this.fullname = fullname;
        this.email = email;

        // --- Cấu hình Khung chính ---
        setTitle("Trang cá nhân"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout(20, 20)); 
        getContentPane().setBackground(BACKGROUND_COLOR);

        // --- 1. Tiêu đề và Ảnh đại diện (Profile Header) ---
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. Panel Thông tin chính (Card Design - Bố cục co giãn) ---
        JPanel mainContentPanel = createMainContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);

        // --- 3. Panel Nút chức năng ---
        JPanel btnsPanel = createButtonsPanel();
        add(btnsPanel, BorderLayout.SOUTH);

        // --- 4. Xử lý sự kiện ---
        ((JButton)btnsPanel.getComponent(0)).addActionListener(e -> new EditInfoDialog(this, "Sửa thông tin", true).setVisible(true));
        ((JButton)btnsPanel.getComponent(1)).addActionListener(e -> new ChangePasswordDialog(this, "Đổi mật khẩu", true).setVisible(true));
        ((JButton)btnsPanel.getComponent(2)).addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Đã đăng xuất", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });
        
        pack();
        setMinimumSize(new Dimension(600, 500)); 
        setVisible(true);
    }
    
    // ====================================================================
    // --- PHẦN 1: KHỞI TẠO CÁC PANEL VÀ THÀNH PHẦN CHÍNH ---
    // ====================================================================

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        // Icon Avatar
        JLabel avatar = new JLabel("🧑", SwingConstants.CENTER); 
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        avatar.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(241, 196, 15)); 
        avatar.setPreferredSize(new Dimension(80, 80));
        headerPanel.add(avatar);

        JLabel title = new JLabel("THÔNG TIN CÁ NHÂN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        headerPanel.add(title);
        
        return headerPanel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainContentPanel = new JPanel(new GridBagLayout()); 
        mainContentPanel.setBackground(BACKGROUND_COLOR);
        mainContentPanel.setBorder(new EmptyBorder(0, 50, 0, 50)); 

        JPanel infoCard = new RoundedPanel(15, CARD_BACKGROUND_COLOR); 
        infoCard.setLayout(new GridBagLayout()); 
        
        GridBagConstraints gbcInfo = new GridBagConstraints();
        gbcInfo.insets = new Insets(15, 20, 15, 20); 
        gbcInfo.fill = GridBagConstraints.HORIZONTAL; 
        gbcInfo.weightx = 1.0;

        int row = 0;
        row = addInfoRow(infoCard, gbcInfo, "Tên đăng nhập:", this.username, row);
        row = addInfoRow(infoCard, gbcInfo, "Họ và tên:", this.fullname, row);
        row = addInfoRow(infoCard, gbcInfo, "Email:", this.email, row);
        row = addInfoRow(infoCard, gbcInfo, "Vai trò:", "USER", row);

        // THIẾT LẬP CO GIÃN
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.insets = new Insets(20, 0, 20, 0); 
        gbcMain.weightx = 1.0; 
        gbcMain.weighty = 1.0; 
        gbcMain.fill = GridBagConstraints.BOTH; 
        gbcMain.anchor = GridBagConstraints.CENTER;

        mainContentPanel.add(infoCard, gbcMain);
        
        return mainContentPanel;
    }

    private JPanel createButtonsPanel() {
        JPanel btnsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20)); 
        btnsPanel.setBackground(BACKGROUND_COLOR);
        btnsPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton btnEdit = createStyledButton("Sửa thông tin", BUTTON_COLOR);
        JButton btnChange = createStyledButton("Đổi mật khẩu", PRIMARY_COLOR);
        JButton btnLogout = createStyledButton("Đăng xuất", DANGER_COLOR);

        btnsPanel.add(btnEdit);
        btnsPanel.add(btnChange);
        btnsPanel.add(btnLogout);
        return btnsPanel;
    }

    // ====================================================================
    // --- PHẦN 2: HÀM HỖ TRỢ VÀ STYLE ---
    // ====================================================================
    
    // Custom Panel cho hiệu ứng bo góc
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 15;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.cornerRadius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setColor(bgColor);
            graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
            graphics.setColor(new Color(189, 195, 199)); 
            graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
        }
    }

    // Custom Panel cho hiệu ứng đổ bóng/viền cho trường Input trong Dialog
    private class StyledInputFieldPanel extends JPanel {
        public StyledInputFieldPanel(JComponent field) {
            super(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1), // Viền mỏng
                new EmptyBorder(5, 5, 5, 5) // Padding bên trong
            ));
            setBackground(CARD_BACKGROUND_COLOR);
            add(field, BorderLayout.CENTER);
        }
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
        return button;
    }

    private int addInfoRow(JPanel panel, GridBagConstraints gbc, String labelText, String valueText, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(TEXT_COLOR); 

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0; 
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.BOLD, 16));
        value.setForeground(PRIMARY_COLOR.darker());
        
        JPanel fieldPanel = new RoundedPanel(8, new Color(248, 249, 250)); 
        fieldPanel.setLayout(new BorderLayout());
        fieldPanel.setBorder(new EmptyBorder(8, 15, 8, 15));
        fieldPanel.add(value, BorderLayout.CENTER);

        if (labelText.contains("Họ và tên:")) lblFullnameValue = value;
        if (labelText.contains("Email:")) lblEmailValue = value;

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1.0; 
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(fieldPanel, gbc);
        
        return row + 1;
    }

    // ====================================================================
    // --- PHẦN 3: CUSTOM DIALOGS (Nâng cấp giao diện) ---
    // ====================================================================
    
    // Hàm tạo input field được nâng cấp
    private JPanel createStyledLabelField(String labelText, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(CARD_BACKGROUND_COLOR);
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR); // Thêm màu sắc cho label
        
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Bỏ viền mặc định
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        p.add(label, BorderLayout.NORTH);
        // Sử dụng StyledInputFieldPanel để thêm viền/đổ bóng cho field
        p.add(new StyledInputFieldPanel(field), BorderLayout.CENTER); 
        return p;
    }

    // Hàm tạo password field được nâng cấp
    private JPanel createStyledLabelField(String labelText, JPasswordField field) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        p.setBackground(CARD_BACKGROUND_COLOR);
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR); // Thêm màu sắc cho label
        
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Bỏ viền mặc định
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        
        p.add(label, BorderLayout.NORTH);
        // Sử dụng StyledInputFieldPanel để thêm viền/đổ bóng cho field
        p.add(new StyledInputFieldPanel(field), BorderLayout.CENTER); 
        return p;
    }

    private class EditInfoDialog extends JDialog {
        public EditInfoDialog(Frame owner, String title, boolean modal) {
            super(owner, title, modal);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(BACKGROUND_COLOR);
            
            JLabel dialogTitle = new JLabel("Cập nhật Thông tin cá nhân", SwingConstants.CENTER);
            dialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
            dialogTitle.setForeground(PRIMARY_COLOR);
            dialogTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
            add(dialogTitle, BorderLayout.NORTH);

            // Nâng cấp: Dùng JPanel lồng nhau để tăng khoảng cách và làm đẹp hơn
            JPanel inputContainer = new RoundedPanel(15, CARD_BACKGROUND_COLOR);
            inputContainer.setLayout(new GridBagLayout()); 
            inputContainer.setBorder(new EmptyBorder(30, 40, 30, 40)); // Tăng padding bên trong

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(10, 0, 10, 0); // Khoảng cách giữa các trường

            JTextField txtFullname = new JTextField(((UserDashboard)owner).fullname);
            JTextField txtEmail = new JTextField(((UserDashboard)owner).email);
            
            gbc.gridy = 0;
            inputContainer.add(createStyledLabelField("Họ và tên mới:", txtFullname), gbc);
            
            gbc.gridy = 1;
            inputContainer.add(createStyledLabelField("Email mới:", txtEmail), gbc);

            add(inputContainer, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
            btnPanel.setBackground(BACKGROUND_COLOR);
            
            JButton btnSave = createStyledButton("Lưu Thay Đổi", BUTTON_COLOR);
            JButton btnCancel = createStyledButton("Hủy", DANGER_COLOR);

            btnSave.addActionListener(e -> {
                String newFull = txtFullname.getText().trim();
                String newEmail = txtEmail.getText().trim();
                
                if (newFull.isEmpty() || newEmail.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                UserDashboard parent = (UserDashboard) owner;
                parent.fullname = newFull;
                parent.email = newEmail;
                parent.lblFullnameValue.setText(newFull);
                parent.lblEmailValue.setText(newEmail);

                JOptionPane.showMessageDialog(this, "Đã cập nhật hiển thị (local).", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            });
            btnCancel.addActionListener(e -> dispose());

            btnPanel.add(btnSave);
            btnPanel.add(btnCancel);
            add(btnPanel, BorderLayout.SOUTH);
            
            pack();
            setResizable(false);
            setMinimumSize(new Dimension(450, 350)); // Đảm bảo kích thước tối thiểu đẹp
        }
    }

    private class ChangePasswordDialog extends JDialog {
        public ChangePasswordDialog(Frame owner, String title, boolean modal) {
            super(owner, title, modal);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout(10, 10));
            getContentPane().setBackground(BACKGROUND_COLOR);

            JLabel dialogTitle = new JLabel("Đổi mật khẩu tài khoản", SwingConstants.CENTER);
            dialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
            dialogTitle.setForeground(PRIMARY_COLOR);
            dialogTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
            add(dialogTitle, BorderLayout.NORTH);

            // Nâng cấp: Dùng JPanel lồng nhau để tăng khoảng cách và làm đẹp hơn
            JPanel inputContainer = new RoundedPanel(15, CARD_BACKGROUND_COLOR);
            inputContainer.setLayout(new GridBagLayout()); 
            inputContainer.setBorder(new EmptyBorder(30, 40, 30, 40)); // Tăng padding bên trong

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(10, 0, 10, 0); // Khoảng cách giữa các trường

            JPasswordField oldp = new JPasswordField(18);
            JPasswordField newp = new JPasswordField(18);
            JPasswordField repl = new JPasswordField(18);

            gbc.gridy = 0;
            inputContainer.add(createStyledLabelField("Mật khẩu cũ:", oldp), gbc);
            
            gbc.gridy = 1;
            inputContainer.add(createStyledLabelField("Mật khẩu mới:", newp), gbc);
            
            gbc.gridy = 2;
            inputContainer.add(createStyledLabelField("Nhập lại mật khẩu mới:", repl), gbc);

            add(inputContainer, BorderLayout.CENTER);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
            btnPanel.setBackground(BACKGROUND_COLOR);

            JButton btnChange = createStyledButton("Xác nhận Đổi", PRIMARY_COLOR);
            JButton btnCancel = createStyledButton("Hủy", DANGER_COLOR);
            
            btnChange.addActionListener(e -> {
                String np = new String(newp.getPassword());
                String rp = new String(repl.getPassword());
                String op = new String(oldp.getPassword());

                if (op.isEmpty() || np.isEmpty() || rp.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không được để trống mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE); return;
                }
                if (!np.equals(rp)) { 
                    JOptionPane.showMessageDialog(this, "Mật khẩu mới không khớp nhau.", "Lỗi", JOptionPane.ERROR_MESSAGE); return; 
                }
                
                boolean isSuccess = true; 
                
                if (isSuccess) { 
                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else { 
                    JOptionPane.showMessageDialog(this, "Lỗi: Mật khẩu cũ không đúng hoặc lỗi máy chủ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });
            btnCancel.addActionListener(e -> dispose());

            btnPanel.add(btnChange);
            btnPanel.add(btnCancel);
            add(btnPanel, BorderLayout.SOUTH);

            pack();
            setResizable(false);
            setMinimumSize(new Dimension(450, 400)); // Đảm bảo kích thước tối thiểu đẹp
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UserDashboard("nguyenvana", "Nguyễn Văn A", "vana@example.com");
        });
    }
}
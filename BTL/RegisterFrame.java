package BTL;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D; // Để bo góc
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFrame extends JFrame {
    private JTextField tfUser, tfFull, tfEmail;
    private JPasswordField pfPass, pfConfirm;

    // --- Custom Border cho bo góc ---
    class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius, this.radius, this.radius, this.radius);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.top = insets.bottom = this.radius;
            return insets;
        }
    }

    // --- Custom JButton với bo góc và hiệu ứng Hover ---
    class StyledButton extends JButton {
        private Color normalColor;
        private Color hoverColor;
        private int borderRadius;

        public StyledButton(String text, Color normalColor, Color hoverColor, int borderRadius) {
            super(text);
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            this.borderRadius = borderRadius;
            setContentAreaFilled(false); // Không vẽ nền mặc định
            setFocusPainted(false); // Bỏ đường viền focus
            setBorderPainted(false); // Bỏ border mặc định
            setForeground(Color.WHITE); // Màu chữ mặc định là trắng
            setFont(new Font("Segoe UI", Font.BOLD, 14)); // Font hiện đại hơn

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(hoverColor);
                    setForeground(Color.WHITE);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(normalColor);
                    setForeground(Color.WHITE);
                }
            });
            setBackground(normalColor); // Đặt màu nền ban đầu
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground()); // Sử dụng màu nền hiện tại (có thể thay đổi khi hover)
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), borderRadius, borderRadius));
            super.paintComponent(g); // Vẽ chữ
            g2.dispose();
        }
    }

    // --- Custom JTextField với bo góc và padding ---
    class RoundedTextField extends JTextField {
        private int radius;
        RoundedTextField(int columns, int radius) {
            super(columns);
            this.radius = radius;
            setOpaque(false); // Để có thể vẽ nền bo góc
            setBorder(new EmptyBorder(5, 10, 5, 10)); // Padding bên trong
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // Không vẽ border mặc định, dùng paintComponent để vẽ border bo góc
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200)); // Màu border nhạt
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            g2.dispose();
        }
    }

    // --- Custom JPasswordField với bo góc và padding ---
    class RoundedPasswordField extends JPasswordField {
        private int radius;
        RoundedPasswordField(int columns, int radius) {
            super(columns);
            this.radius = radius;
            setOpaque(false); // Để có thể vẽ nền bo góc
            setBorder(new EmptyBorder(5, 10, 5, 10)); // Padding bên trong
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200)); // Màu border nhạt
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
            g2.dispose();
        }
    }


    public RegisterFrame() {
        setTitle("Đăng ký tài khoản mới");
        setSize(500, 600); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false); 

        // --- Bảng màu hiện đại ---
        Color primaryBlue = new Color(52, 152, 219);  
        Color darkBlue = new Color(41, 128, 185);    
        Color greyText = new Color(100, 100, 100);    
        Color lightBackground = new Color(236, 240, 241); 
        Color cardBackground = Color.WHITE; 
        
        Font titleFont = new Font("Segoe UI", Font.BOLD, 28);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        // --- Panel chính ---
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBackground(lightBackground);

        // --- Card Panel (Panel chính chứa form) ---
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout(20, 20)); 
        cardPanel.setBackground(cardBackground);
        cardPanel.setBorder(new CompoundBorder(
            new RoundedBorder(20, cardBackground), 
            new EmptyBorder(30, 30, 30, 30) 
        ));

        // --- Tiêu đề "Đăng ký" ---
        JLabel titleLabel = new JLabel("ĐĂNG KÝ", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(primaryBlue);
        cardPanel.add(titleLabel, BorderLayout.NORTH);

        // --- Form Panel (Chứa các trường nhập liệu) ---
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 0, 15)); 
        formPanel.setBackground(cardBackground);

        // Tên đăng nhập
        formPanel.add(createLabelFieldPanel("Tên đăng nhập:", tfUser = new RoundedTextField(25, 10), labelFont, greyText));
        
        // Mật khẩu
        formPanel.add(createLabelFieldPanel("Mật khẩu:", pfPass = new RoundedPasswordField(25, 10), labelFont, greyText));

        // Xác nhận mật khẩu
        formPanel.add(createLabelFieldPanel("Xác nhận mật khẩu:", pfConfirm = new RoundedPasswordField(25, 10), labelFont, greyText));

        // Họ và tên
        formPanel.add(createLabelFieldPanel("Họ và tên:", tfFull = new RoundedTextField(25, 10), labelFont, greyText));

        // Email
        formPanel.add(createLabelFieldPanel("Email:", tfEmail = new RoundedTextField(25, 10), labelFont, greyText));
        
        cardPanel.add(formPanel, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(cardBackground);

        JButton btnCreate = new StyledButton("Đăng ký", primaryBlue, darkBlue, 10);
        btnCreate.setPreferredSize(new Dimension(150, 40));
        
        JButton btnCancel = new StyledButton("Hủy", new Color(189, 195, 199), new Color(149, 165, 166), 10); // Xám
        btnCancel.setPreferredSize(new Dimension(150, 40));

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnCancel);
        
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Thêm cardPanel vào backgroundPanel và căn giữa
        backgroundPanel.add(cardPanel);
        
        this.setContentPane(backgroundPanel);
        this.setVisible(true);

        // Cài đặt Listener
        btnCreate.addActionListener(e -> create());
        btnCancel.addActionListener(e -> this.dispose());
    }

    // Helper method để tạo Panel chứa Label và Field
    private JPanel createLabelFieldPanel(String labelText, JComponent field, Font font, Color textColor) {
        JPanel panel = new JPanel(new BorderLayout(0, 5)); 
        panel.setOpaque(false); 

        JLabel label = new JLabel(labelText);
        label.setFont(font);
        label.setForeground(textColor);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        field.setBackground(new Color(248, 248, 248)); 

        return panel;
    }

    /**
     * Phương thức xử lý logic Đăng ký
     */
    private void create() {
        String username = tfUser.getText().trim();
        String password = new String(pfPass.getPassword());
        String confirmPass = new String(pfConfirm.getPassword());
        String fullname = tfFull.getText().trim();
        String email = tfEmail.getText().trim();

        // 1. Kiểm tra rỗng và độ dài tối thiểu (Giả định mật khẩu tối thiểu 6 ký tự)
        if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty() || fullname.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ tất cả các trường.", "Lỗi Đăng ký", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự.", "Lỗi Đăng ký", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Kiểm tra mật khẩu xác nhận
        if (!password.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu và Xác nhận mật khẩu không khớp.", "Lỗi Đăng ký", JOptionPane.WARNING_MESSAGE);
            pfPass.setText("");
            pfConfirm.setText("");
            return;
        }
        
        // 3. Kiểm tra định dạng Email (Regex cơ bản)
        Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = emailPattern.matcher(email);
        if (!matcher.matches()) {
            JOptionPane.showMessageDialog(this, "Định dạng Email không hợp lệ.", "Lỗi Đăng ký", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 4. Kiểm tra Tên đăng nhập đã tồn tại trong database
        if (UserDatabase.findByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập '" + username + "' đã tồn tại. Vui lòng chọn tên khác.", "Lỗi Đăng ký", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 5. Tạo User mới và lưu vào database
        try {
            // Lấy ID tiếp theo và tạo đối tượng User mới
            int newId = UserDatabase.nextId();
            // Mật khẩu nên được hash, nhưng ở đây dùng password thường theo cấu trúc database
            User newUser = new User(newId, username, password, fullname, email, "USER", "ACTIVE"); 
            
            // Tải danh sách, thêm User, và lưu lại
            List<User> users = UserDatabase.loadUsers();
            users.add(newUser);
            UserDatabase.saveUsers(users);
            
            JOptionPane.showMessageDialog(this, "Đăng ký thành công!\nTài khoản: " + username, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            this.dispose(); // Đóng cửa sổ đăng ký sau khi thành công
            
        } catch (Exception ex) {
            // Bắt các lỗi I/O hoặc lỗi chuyển đổi (ví dụ: nếu file CSV bị hỏng)
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống khi lưu dữ liệu. Vui lòng thử lại.\nChi tiết: " + ex.getMessage(), "Lỗi Hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}
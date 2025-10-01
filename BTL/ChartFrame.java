package BTL;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.text.DecimalFormat;

public class ChartFrame extends JFrame {
    private int total;
    private int locked;
    private String chartType;

    // Thiết lập Design System (Màu sắc và Font chuẩn)
    private final Color PRIMARY_TEXT = new Color(44, 62, 80); // Xanh Navy đậm
    private final Color BACKGROUND_COLOR = new Color(248, 249, 250); // Nền sáng
    private final Color ACTIVE_COLOR = new Color(39, 174, 96); // Xanh lá - Tích cực
    private final Color LOCKED_COLOR = new Color(231, 76, 60); // Đỏ - Cảnh báo
    private final Color BORDER_COLOR = new Color(200, 200, 200); // Viền/Lưới nhạt

    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font LEGEND_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private final Font DATA_LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);

    /**
     * Khởi tạo ChartFrame theo loại biểu đồ.
     */
    public ChartFrame(int total, int locked, String chartType) {
        this.total = total;
        this.locked = locked;
        this.chartType = chartType;

        setTitle("Thống kê tài khoản (" + chartType + ")");
        setSize(1000, 650); // Tăng kích thước cửa sổ
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Tạo container cho biểu đồ
        JPanel chartContainer = new JPanel();
        chartContainer.setBackground(BACKGROUND_COLOR);
        chartContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if ("PIE".equalsIgnoreCase(chartType)) {
            chartContainer.setLayout(new BorderLayout());
            chartContainer.add(new PieChartPanel(), BorderLayout.CENTER);
        } else if ("BAR".equalsIgnoreCase(chartType)) {
            chartContainer.setLayout(new BorderLayout());
            chartContainer.add(new BarChartPanel(), BorderLayout.CENTER);
        } else if ("BOTH".equalsIgnoreCase(chartType)) {
            chartContainer.setLayout(new GridLayout(1, 2, 30, 0)); // Thêm khoảng cách ngang
            chartContainer.add(new PieChartPanel());
            chartContainer.add(new BarChartPanel());
        }

        add(chartContainer, BorderLayout.CENTER);
        setVisible(true);
    }

    /**
     * Panel vẽ biểu đồ tròn.
     */
 // ... (Phần khai báo class và biến của ChartFrame giữ nguyên)

    /**
     * Panel vẽ biểu đồ tròn.
     */
    class PieChartPanel extends JPanel {
        private static final int DIAMETER = 300;
        private final int LEGEND_PADDING = 30; 

        public PieChartPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth(), height = getHeight();
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            String title = "Tỷ Lệ Tài Khoản Đang Hoạt Động & Bị Khóa";
            // Tiêu đề luôn căn giữa Panel
            drawCenteredTitle(g2, title, width, 40);

            if (total == 0) {
                drawCenteredMessage(g2, "Chưa có tài khoản nào được đăng ký.", width, height);
                return;
            }

            int active = total - locked;
            double activeRatio = (double) active / total;
            double lockedRatio = (double) locked / total;

            double activeAngle = activeRatio * 360;
            double lockedAngle = lockedRatio * 360;

            // ***** ĐIỀU CHỈNH CHÍNH *****
            // Tính toán vị trí mới để căn giữa Biểu đồ Tròn và Legend trong Panel
            
            // Tổng chiều rộng cần thiết cho Biểu đồ + Legend
            int totalVisualWidth = DIAMETER + 250; 
            
            // Bắt đầu vẽ Biểu đồ Tròn sao cho tổng thể (Biểu đồ + Legend) được căn giữa Panel
            int chartX = (width / 2) - (totalVisualWidth / 2); 
            int chartY = (height / 2) - (DIAMETER / 2) + 20;

            // Vị trí Legend (Đặt bên phải biểu đồ)
            int legendX = chartX + DIAMETER + LEGEND_PADDING;
            int legendY = chartY + 50;
            // ****************************
            
            DecimalFormat df = new DecimalFormat("0.0%");
            double startAngle = 90;

            // === VẼ BIỂU ĐỒ TRÒN ===
            drawPieSlice(g2, chartX, chartY, DIAMETER, startAngle, -activeAngle, ACTIVE_COLOR, Color.WHITE);
            drawPieSlice(g2, chartX, chartY, DIAMETER, startAngle - activeAngle, -lockedAngle, LOCKED_COLOR, Color.WHITE);
            
            // === VẼ LABEL PHẦN TRĂM TRÊN LÁT CẮT ===
            g2.setFont(DATA_LABEL_FONT);
            
            if (activeRatio > 0.05) {
                drawPieLabel(g2, chartX, chartY, DIAMETER, startAngle - (activeAngle / 2), df.format(activeRatio));
            }

            if (lockedRatio > 0.05) {
                drawPieLabel(g2, chartX, chartY, DIAMETER, startAngle - activeAngle - (lockedAngle / 2), df.format(lockedRatio));
            }

            // === VẼ LEGEND ===
            g2.setFont(LEGEND_FONT);
            
            drawLegendItem(g2, legendX, legendY, ACTIVE_COLOR, String.format("Đang hoạt động: %d (%s)", active, df.format(activeRatio)));
            drawLegendItem(g2, legendX, legendY + 40, LOCKED_COLOR, String.format("Bị khóa: %d (%s)", locked, df.format(lockedRatio)));

            // Label Tổng số
            g2.setFont(LEGEND_FONT.deriveFont(Font.BOLD | Font.ITALIC));
            String totalLabel = "Tổng số tài khoản: " + total;
            FontMetrics fmTotal = g2.getFontMetrics();
            int tw = fmTotal.stringWidth(totalLabel);
            g2.drawString(totalLabel, (width - tw) / 2, height - 20);
        }
        
        // Helper: Vẽ lát cắt (Giữ nguyên)
        private void drawPieSlice(Graphics2D g2, int x, int y, int d, double start, double extent, Color fill, Color border) {
            Arc2D.Double arc = new Arc2D.Double(x, y, d, d, start, extent, Arc2D.PIE);
            g2.setColor(fill);
            g2.fill(arc);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(3)); 
            g2.draw(arc);
        }

        // Helper: Vẽ nhãn % trên lát cắt (Giữ nguyên)
        private void drawPieLabel(Graphics2D g2, int chartX, int chartY, int diameter, double midAngle, String text) {
            int labelX = (int) (chartX + diameter / 2 + Math.cos(Math.toRadians(midAngle)) * (diameter / 2.5));
            int labelY = (int) (chartY + diameter / 2 - Math.sin(Math.toRadians(midAngle)) * (diameter / 2.5));
            
            g2.setColor(Color.WHITE);
            g2.drawString(text, labelX - g2.getFontMetrics().stringWidth(text) / 2, labelY);
        }
    }

// ---

    /**
     * Panel vẽ biểu đồ cột.
     */
    class BarChartPanel extends JPanel {
        public BarChartPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight();
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            String title = "So Sánh Số Lượng Tài Khoản";
            // Tiêu đề luôn căn giữa Panel
            drawCenteredTitle(g2, title, width, 40);

            if (total == 0) {
                drawCenteredMessage(g2, "Chưa có tài khoản nào được đăng ký.", width, height);
                return;
            }

            int active = total - locked;
            int maxVal = Math.max(active, locked);
            
            // Vùng vẽ biểu đồ
            int paddingX = 40; // Giảm padding ngang
            int paddingY = 60; // Padding dọc
            int chartWidth = width - 2 * paddingX;
            int chartHeight = height - 100 - paddingY;

            int activeBarHeight = (int) ((double) active / maxVal * chartHeight);
            int lockedBarHeight = (int) ((double) locked / maxVal * chartHeight);

            int barWidth = 120;
            int chartBase = height - paddingY; // Trục X

            // ***** ĐIỀU CHỈNH CHÍNH *****
            // Căn giữa hai cột trong Panel
            int totalBarSpace = barWidth * 2 + 20; // 2 cột + 20px khoảng cách giữa 2 cột
            int x1 = (width / 2) - (totalBarSpace / 2);
            int x2 = x1 + barWidth + 20;
            // ****************************

            // === VẼ TRỤC X (Đường cơ sở) ===
            g2.setColor(BORDER_COLOR.darker());
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(paddingX, chartBase, width - paddingX, chartBase);
            g2.setStroke(new BasicStroke(1));

            // === VẼ CỘT HOẠT ĐỘNG ===
            g2.setColor(ACTIVE_COLOR);
            g2.fillRect(x1, chartBase - activeBarHeight, barWidth, activeBarHeight);
            
            g2.setColor(PRIMARY_TEXT);
            g2.setFont(DATA_LABEL_FONT);
            // Nhãn giá trị
            g2.drawString(String.valueOf(active), x1 + barWidth/2 - g2.getFontMetrics().stringWidth(String.valueOf(active)) / 2, chartBase - activeBarHeight - 10);
            // Nhãn Category
            g2.setFont(LEGEND_FONT.deriveFont(Font.BOLD));
            g2.drawString("Hoạt động", x1 + barWidth/2 - g2.getFontMetrics().stringWidth("Hoạt động") / 2, chartBase + 20);

            // === VẼ CỘT BỊ KHÓA ===
            g2.setColor(LOCKED_COLOR);
            g2.fillRect(x2, chartBase - lockedBarHeight, barWidth, lockedBarHeight);
            
            g2.setColor(PRIMARY_TEXT);
            g2.setFont(DATA_LABEL_FONT);
            // Nhãn giá trị
            g2.drawString(String.valueOf(locked), x2 + barWidth/2 - g2.getFontMetrics().stringWidth(String.valueOf(locked)) / 2, chartBase - lockedBarHeight - 10);
            // Nhãn Category
            g2.setFont(LEGEND_FONT.deriveFont(Font.BOLD));
            g2.drawString("Bị khóa", x2 + barWidth/2 - g2.getFontMetrics().stringWidth("Bị khóa") / 2, chartBase + 20);

            // Ghi chú trục Y (Tối đa)
            g2.setFont(LEGEND_FONT);
            g2.setColor(BORDER_COLOR.darker());
            g2.drawString("Số lượng", paddingX - 25, height / 2); // Đẩy chữ "Số lượng" vào gần hơn
            g2.drawString(String.valueOf(maxVal), paddingX - 15, chartBase - chartHeight); // Đẩy giá trị tối đa vào gần hơn
        }
    }

// ... (Phần Helper Methods giữ nguyên)
    // === HÀM HỖ TRỢ CHUNG ===

    private void drawCenteredTitle(Graphics2D g2, String title, int width, int y) {
        g2.setFont(TITLE_FONT);
        FontMetrics fmTitle = g2.getFontMetrics();
        int titleWidth = fmTitle.stringWidth(title);
        g2.setColor(PRIMARY_TEXT);
        g2.drawString(title, (width - titleWidth) / 2, y);
    }

    private void drawCenteredMessage(Graphics2D g2, String message, int width, int height) {
        g2.setFont(LEGEND_FONT.deriveFont(Font.ITALIC));
        g2.setColor(PRIMARY_TEXT.darker());
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(message, (width - fm.stringWidth(message)) / 2, height / 2);
    }

    private void drawLegendItem(Graphics2D g2, int x, int y, Color color, String text) {
        final int LEGEND_SQUARE = 18;
        g2.setColor(color);
        g2.fillRect(x, y, LEGEND_SQUARE, LEGEND_SQUARE);
        g2.setColor(PRIMARY_TEXT);
        g2.drawString(text, x + LEGEND_SQUARE + 10, y + 14);
    }
}
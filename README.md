<h2 align="center">
    <a href="https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin">
    🎓 Faculty of Information Technology (DaiNam University)
    </a>
</h2>
<h2 align="center">
   NETWORK PROGRAMMING
</h2>
<div align="center">
    <p align="center">
        <img src="docs/aiotlab_logo.png" alt="AIoTLab Logo" width="170"/>
        <img src="docs/fitdnu_logo.png" alt="AIoTLab Logo" width="180"/>
        <img src="docs/dnu_logo.png" alt="DaiNam University Logo" width="200"/>
    </p>

[![AIoTLab](https://img.shields.io/badge/AIoTLab-green?style=for-the-badge)](https://www.facebook.com/DNUAIoTLab)
[![Faculty of Information Technology](https://img.shields.io/badge/Faculty%20of%20Information%20Technology-blue?style=for-the-badge)](https://dainam.edu.vn/vi/khoa-cong-nghe-thong-tin)
[![DaiNam University](https://img.shields.io/badge/DaiNam%20University-orange?style=for-the-badge)](https://dainam.edu.vn)


</div>
#📖 1. Giới thiệu

Hệ thống đăng nhập Client-Server là một mô hình phần mềm cho phép quản lý và xác thực người dùng thông qua giao thức TCP. Với cơ chế này, hệ thống đảm bảo khả năng truyền thông ổn định, an toàn và đáng tin cậy trong các hoạt động đăng ký, đăng nhập và quản lý tài khoản.

Trong kiến trúc này:

- Server chịu trách nhiệm xử lý logic nghiệp vụ, quản lý cơ sở dữ liệu và duy trì tính bảo mật.

- Client đảm nhiệm việc cung cấp giao diện người dùng trực quan, hỗ trợ thao tác dễ dàng và thuận tiện.

📊 Mục tiêu của đề tài

- Xây dựng hệ thống đăng nhập dựa trên mô hình Client-Server phân tán.

- Ứng dụng giao thức TCP nhằm bảo đảm độ tin cậy trong truyền thông mạng.

- Thiết kế giao diện người dùng bằng Java Swing.

- Tích hợp cơ sở dữ liệu PostgreSQL để lưu trữ và quản lý thông tin người dùng.

- Tăng cường tính bảo mật trong quá trình xác thực.

- Phát triển công cụ hỗ trợ administrator trong việc quản lý và giám sát tài khoản.

#🔧 2. Công nghệ sử dụng

🌐 Ngôn Ngữ Lập Trình

Java SE 17+: Ngôn ngữ lập trình chính

Tính năng: Lập trình hướng đối tượng, hỗ trợ đa luồng, lập trình socket

🎨 Giao Diện Người Dùng

Java Swing: Xây dựng giao diện desktop

Các thành phần chính: JFrame, JPanel, JButton, JTextField, JPasswordField, JTable

Xử lý sự kiện: ActionListener, MouseListener

🌐 Truyền Thông Mạng

Giao thức TCP/IP: Truyền dữ liệu đáng tin cậy

Socket & ServerSocket: Kết nối client-server

Luồng đối tượng: ObjectInputStream & ObjectOutputStream để gửi/nhận dữ liệu

🗄️ Lưu Trữ Dữ Liệu

CSV File

Thay thế cơ sở dữ liệu bằng file users.csv

Chứa thông tin tài khoản (username, password, role, …)

Các thao tác: đọc, ghi, cập nhật, xóa tài khoản bằng Java I/O

🔄 Xử Lý Đa Luồng

Java Multithreading: Cho phép nhiều client kết nối đồng thời

Thread riêng cho từng client để không gây xung đột

Đồng bộ hóa khi ghi/đọc dữ liệu từ file CSV

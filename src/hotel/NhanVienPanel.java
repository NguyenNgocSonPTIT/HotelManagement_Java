package hotel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NhanVienPanel extends JPanel {
    private JTextField txtMaNV, txtHoTen, txtSDT, txtEmail, txtTenDangNhap, txtMatKhau;
    private JComboBox<ComboItem> cboVaiTro;
    private JComboBox<String> cboTrangThai;
    private JTable tblNhanVien;

    public static class ComboItem {
        private int key;
        private String value;
        public ComboItem(int key, String value) { this.key = key; this.value = value; }
        public int getKey() { return key; }
        public String getValue() { return value; }
        @Override public String toString() { return value; }
    }

    public NhanVienPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(241, 245, 249));

        initUI();
        loadVaiTroCombo();
        loadData();
    }

    private void initUI() {
        // Banner
        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setBackground(new Color(26, 54, 93));
        pnlTitle.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lblTitle = new JLabel("QUẢN LÝ NHÂN SỰ, TÀI KHOẢN & PHÂN QUYỀN HỆ THỐNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("* Phân quyền vai trò (Quản lý, Lễ tân, Kế toán) & Kiểm soát trạng thái khóa/mở tài khoản");
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubTitle.setForeground(new Color(203, 213, 225));

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSubTitle);
        add(pnlTitle, BorderLayout.NORTH);

        // Center
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setOpaque(false);

        // Input form
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " HỒ SƠ TÀI KHOẢN NHÂN VIÊN ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtMaNV = new JTextField(10);
        txtHoTen = new JTextField(15);
        cboVaiTro = new JComboBox<>();
        txtSDT = new JTextField(12);
        txtEmail = new JTextField(15);
        txtTenDangNhap = new JTextField(12);
        txtMatKhau = new JTextField("123456", 12);
        cboTrangThai = new JComboBox<>(new String[]{"Đang làm việc", "Đã nghỉ"});

        g.gridx = 0; g.gridy = 0; pnlInput.add(new JLabel("Mã nhân viên (*):"), g);
        g.gridx = 1; pnlInput.add(txtMaNV, g);
        g.gridx = 2; g.gridy = 0; pnlInput.add(new JLabel("Họ và tên (*):"), g);
        g.gridx = 3; pnlInput.add(txtHoTen, g);
        g.gridx = 4; g.gridy = 0; pnlInput.add(new JLabel("Vai trò / Quyền:"), g);
        g.gridx = 5; pnlInput.add(cboVaiTro, g);

        g.gridx = 0; g.gridy = 1; pnlInput.add(new JLabel("Số điện thoại:"), g);
        g.gridx = 1; pnlInput.add(txtSDT, g);
        g.gridx = 2; g.gridy = 1; pnlInput.add(new JLabel("Email liên hệ:"), g);
        g.gridx = 3; pnlInput.add(txtEmail, g);
        g.gridx = 4; g.gridy = 1; pnlInput.add(new JLabel("Trạng thái:"), g);
        g.gridx = 5; pnlInput.add(cboTrangThai, g);

        g.gridx = 0; g.gridy = 2; pnlInput.add(new JLabel("Tên đăng nhập (*):"), g);
        g.gridx = 1; pnlInput.add(txtTenDangNhap, g);
        g.gridx = 2; g.gridy = 2; pnlInput.add(new JLabel("Mật khẩu:"), g);
        g.gridx = 3; pnlInput.add(txtMatKhau, g);

        // Buttons
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlBtns.setOpaque(false);

        JButton btnThem = createBtn("➕ Thêm Nhân Viên", new Color(16, 185, 129));
        JButton btnSua = createBtn("✏️ Cập Nhật", new Color(37, 99, 235));
        JButton btnKhoa = createBtn("🔒 Khóa / Mở Khóa", new Color(245, 158, 11));
        JButton btnXoa = createBtn("🗑️ Xóa", new Color(239, 68, 68));
        JButton btnLamMoi = createBtn("🔄 Làm Mới", new Color(100, 116, 139));

        pnlBtns.add(btnThem);
        pnlBtns.add(btnSua);
        pnlBtns.add(btnKhoa);
        pnlBtns.add(btnXoa);
        pnlBtns.add(btnLamMoi);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        pnlCenter.add(pnlTop, BorderLayout.NORTH);

        // Table
        tblNhanVien = new JTable();
        tblNhanVien.setRowHeight(28);
        tblNhanVien.getTableHeader().setBackground(new Color(30, 58, 138));
        tblNhanVien.getTableHeader().setForeground(Color.WHITE);
        tblNhanVien.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        tblNhanVien.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int r = tblNhanVien.getSelectedRow();
                if (r >= 0) {
                    txtMaNV.setText(tblNhanVien.getValueAt(r, 0).toString());
                    txtHoTen.setText(tblNhanVien.getValueAt(r, 1).toString());
                    txtSDT.setText(tblNhanVien.getValueAt(r, 3) != null ? tblNhanVien.getValueAt(r, 3).toString() : "");
                    txtEmail.setText(tblNhanVien.getValueAt(r, 4) != null ? tblNhanVien.getValueAt(r, 4).toString() : "");
                    txtTenDangNhap.setText(tblNhanVien.getValueAt(r, 5).toString());
                    cboTrangThai.setSelectedItem(tblNhanVien.getValueAt(r, 6).toString());
                }
            }
        });

        pnlCenter.add(new JScrollPane(tblNhanVien), BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        btnThem.addActionListener(e -> themNhanVien());
        btnSua.addActionListener(e -> suaNhanVien());
        btnKhoa.addActionListener(e -> toggleKhoaTaiKhoan());
        btnXoa.addActionListener(e -> xoaNhanVien());
        btnLamMoi.addActionListener(e -> {
            txtMaNV.setText(""); txtHoTen.setText(""); txtSDT.setText(""); txtEmail.setText(""); txtTenDangNhap.setText("");
            loadData();
        });
    }

    private JButton createBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void loadVaiTroCombo() {
        cboVaiTro.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaVaiTro, TenVaiTro FROM VaiTro")) {
            while (rs.next()) {
                cboVaiTro.addItem(new ComboItem(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void loadData() {
        try {
            String sql = "SELECT nv.MaNhanVien AS 'Mã NV', nv.HoTen AS 'Họ Và Tên', " +
                         "vt.TenVaiTro AS 'Vai Trò / Quyền', nv.SoDienThoai AS 'Số ĐT', " +
                         "nv.Email AS 'Email', nv.TenDangNhap AS 'Tài Khoản', nv.TrangThai AS 'Trạng Thái' " +
                         "FROM NhanVien nv JOIN VaiTro vt ON nv.MaVaiTro = vt.MaVaiTro ORDER BY nv.MaNhanVien";
            tblNhanVien.setModel(DatabaseHelper.executeQuery(sql));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void themNhanVien() {
        String ma = txtMaNV.getText().trim();
        String ten = txtHoTen.getText().trim();
        ComboItem vt = (ComboItem) cboVaiTro.getSelectedItem();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String user = txtTenDangNhap.getText().trim();
        String pass = txtMatKhau.getText().trim();
        String tt = (String) cboTrangThai.getSelectedItem();

        if (ma.isEmpty() || ten.isEmpty() || user.isEmpty() || vt == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã NV, Họ tên và Tên đăng nhập!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String sql = "INSERT INTO NhanVien (MaNhanVien, MaVaiTro, HoTen, SoDienThoai, Email, TenDangNhap, MatKhauMaHoa, TrangThai) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            DatabaseHelper.executeUpdate(sql, ma.toUpperCase(), vt.getKey(), ten, sdt, email, user, pass, tt);
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaNhanVien() {
        String ma = txtMaNV.getText().trim();
        String ten = txtHoTen.getText().trim();
        ComboItem vt = (ComboItem) cboVaiTro.getSelectedItem();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String tt = (String) cboTrangThai.getSelectedItem();

        if (ma.isEmpty()) return;

        try {
            String sql = "UPDATE NhanVien SET MaVaiTro=?, HoTen=?, SoDienThoai=?, Email=?, TrangThai=? WHERE MaNhanVien=?";
            DatabaseHelper.executeUpdate(sql, vt.getKey(), ten, sdt, email, tt, ma);
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleKhoaTaiKhoan() {
        int r = tblNhanVien.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần đổi trạng thái tài khoản!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ma = tblNhanVien.getValueAt(r, 0).toString();
        String tt = tblNhanVien.getValueAt(r, 6).toString();
        String newTT = "Đang làm việc".equals(tt) ? "Đã nghỉ" : "Đang làm việc";

        try {
            DatabaseHelper.executeUpdate("UPDATE NhanVien SET TrangThai=? WHERE MaNhanVien=?", newTT, ma);
            JOptionPane.showMessageDialog(this, "Đã đổi trạng thái tài khoản [" + ma + "] sang: " + newTT, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaNhanVien() {
        String ma = txtMaNV.getText().trim();
        if (ma.isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa tài khoản nhân viên [" + ma + "]?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                DatabaseHelper.executeUpdate("DELETE FROM NhanVien WHERE MaNhanVien=?", ma);
                loadData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Không thể xóa do nhân viên này đã tiếp nhận hóa đơn/đặt phòng. Hãy dùng chức năng Khóa tài khoản!", "Ràng buộc", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

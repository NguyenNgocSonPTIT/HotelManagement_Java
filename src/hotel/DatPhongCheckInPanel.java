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
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatPhongCheckInPanel extends JPanel {
    // Tab 1: Đơn đặt
    private JTextField txtMaDP;
    private JComboBox<ComboItem> cboKhachHang;
    private JComboBox<ComboItem> cboNhanVien;
    private JComboBox<String> cboTrangThaiDP;
    private JTable tblDatPhong;

    // Tab 2: Xếp phòng & Check-in
    private JComboBox<ComboItem> cboDonDatXepPhong;
    private JComboBox<ComboItem> cboLoaiPhongXep;
    private JComboBox<ComboItem> cboPhongTrong;
    private JTextField txtGiaThueThucTe, txtNgayNhanDuKien, txtNgayTraDuKien;
    private JTable tblChiTietXepPhong;

    // Khách ở thực tế
    private JComboBox<ComboItem> cboKhachOThucTe;
    private JTable tblKhachOThucTe;

    public static class ComboItem {
        private String key;
        private String value;
        public ComboItem(String key, String value) { this.key = key; this.value = value; }
        public String getKey() { return key; }
        public String getValue() { return value; }
        @Override public String toString() { return value; }
    }

    public DatPhongCheckInPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(241, 245, 249));

        initUI();
        loadAllData();

        KhachHangPanel.addChangeListener(this::loadKhachHangCombos);
    }

    private void initUI() {
        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setBackground(new Color(26, 54, 93));
        pnlTitle.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lblTitle = new JLabel("ĐẶT PHÒNG, PHÂN BỔ PHÒNG & THỦ TỤC CHECK-IN LƯU TRÚ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("* Tạo đơn đặt, hủy đơn, xếp phòng vật lý, chốt giá Price Snapshot & Check-in đăng ký tạm trú");
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubTitle.setForeground(new Color(203, 213, 225));

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSubTitle);
        add(pnlTitle, BorderLayout.NORTH);

        JTabbedPane subTabs = new JTabbedPane();
        subTabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        subTabs.addTab("📑 1. Đơn Đặt Phòng Tổng (Tạo / Hủy)", createDonDatTab());
        subTabs.addTab("🔑 2. Phân Bổ Phòng & Thủ Tục Check-in Tạm Trú", createCheckInTab());

        add(subTabs, BorderLayout.CENTER);
    }

    // ==============================================================
    // TAB 1: ĐƠN ĐẶT PHÒNG TỔNG
    // ==============================================================
    private JPanel createDonDatTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " THÔNG TIN ĐƠN ĐẶT PHÒNG TỔNG ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtMaDP = new JTextField(10);
        cboKhachHang = new JComboBox<>();
        cboNhanVien = new JComboBox<>();
        cboTrangThaiDP = new JComboBox<>(new String[]{"Chờ cọc", "Đã cọc", "Đã nhận phòng", "Hủy", "Hoàn thành"});

        g.gridx = 0; g.gridy = 0; pnlInput.add(new JLabel("Mã đặt phòng (*):"), g);
        g.gridx = 1; pnlInput.add(txtMaDP, g);
        g.gridx = 2; g.gridy = 0; pnlInput.add(new JLabel("Khách hàng đại diện (*):"), g);
        g.gridx = 3; pnlInput.add(cboKhachHang, g);
        g.gridx = 4; g.gridy = 0; pnlInput.add(new JLabel("Nhân viên tiếp nhận:"), g);
        g.gridx = 5; pnlInput.add(cboNhanVien, g);

        g.gridx = 0; g.gridy = 1; pnlInput.add(new JLabel("Trạng thái đơn:"), g);
        g.gridx = 1; pnlInput.add(cboTrangThaiDP, g);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlBtns.setOpaque(false);
        JButton btnThem = createBtn("➕ Tạo Đơn Đặt", new Color(16, 185, 129));
        JButton btnHuy = createBtn("❌ Hủy Đơn Đặt", new Color(239, 68, 68));
        JButton btnLamMoi = createBtn("🔄 Làm Mới", new Color(100, 116, 139));

        pnlBtns.add(btnThem);
        pnlBtns.add(btnHuy);
        pnlBtns.add(btnLamMoi);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        panel.add(pnlTop, BorderLayout.NORTH);

        tblDatPhong = new JTable();
        tblDatPhong.setRowHeight(28);
        tblDatPhong.getTableHeader().setBackground(new Color(30, 58, 138));
        tblDatPhong.getTableHeader().setForeground(Color.WHITE);
        tblDatPhong.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        panel.add(new JScrollPane(tblDatPhong), BorderLayout.CENTER);

        btnThem.addActionListener(e -> taoDonDatPhong());
        btnHuy.addActionListener(e -> huyDonDatPhong());
        btnLamMoi.addActionListener(e -> {
            txtMaDP.setText("");
            loadAllData();
        });

        return panel;
    }

    // ==============================================================
    // TAB 2: XẾP PHÒNG & THỦ TỤC CHECK-IN LƯU TRÚ
    // ==============================================================
    private JPanel createCheckInTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Form phân bổ phòng
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " PHÂN BỔ PHÒNG & CHỐT GIÁ GIAO DỊCH ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        cboDonDatXepPhong = new JComboBox<>();
        cboLoaiPhongXep = new JComboBox<>();
        cboPhongTrong = new JComboBox<>();
        txtGiaThueThucTe = new JTextField("500000", 10);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 14:00:00");
        txtNgayNhanDuKien = new JTextField(sdf.format(new Date()), 15);
        txtNgayTraDuKien = new JTextField(sdf.format(new Date(System.currentTimeMillis() + 86400000L * 2)), 15);

        g.gridx = 0; g.gridy = 0; pnlInput.add(new JLabel("Mã đơn đặt:"), g);
        g.gridx = 1; pnlInput.add(cboDonDatXepPhong, g);
        g.gridx = 2; g.gridy = 0; pnlInput.add(new JLabel("Loại phòng:"), g);
        g.gridx = 3; pnlInput.add(cboLoaiPhongXep, g);
        g.gridx = 4; g.gridy = 0; pnlInput.add(new JLabel("Phòng gán:"), g);
        g.gridx = 5; pnlInput.add(cboPhongTrong, g);

        g.gridx = 0; g.gridy = 1; pnlInput.add(new JLabel("Giá chốt (Price Snapshot):"), g);
        g.gridx = 1; pnlInput.add(txtGiaThueThucTe, g);
        g.gridx = 2; g.gridy = 1; pnlInput.add(new JLabel("Nhận dự kiến:"), g);
        g.gridx = 3; pnlInput.add(txtNgayNhanDuKien, g);
        g.gridx = 4; g.gridy = 1; pnlInput.add(new JLabel("Trả dự kiến:"), g);
        g.gridx = 5; pnlInput.add(txtNgayTraDuKien, g);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlBtns.setOpaque(false);
        JButton btnXepPhong = createBtn("📌 Gán & Xếp Phòng", new Color(37, 99, 235));
        JButton btnCheckIn = createBtn("🛎️ THỰC HIỆN CHECK-IN PHÒNG", new Color(16, 185, 129));
        JButton btnThemKhachO = createBtn("👤 Khai Báo Khách Ở Tạm Trú", new Color(245, 158, 11));

        cboKhachOThucTe = new JComboBox<>();
        cboKhachOThucTe.setPreferredSize(new Dimension(200, 26));

        pnlBtns.add(btnXepPhong);
        pnlBtns.add(btnCheckIn);
        pnlBtns.add(new JLabel("  Chọn khách ở:"));
        pnlBtns.add(cboKhachOThucTe);
        pnlBtns.add(btnThemKhachO);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        panel.add(pnlTop, BorderLayout.NORTH);

        // Split tables: Danh sách xếp phòng & Danh sách khách ở thực tế
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.65);

        tblChiTietXepPhong = new JTable();
        tblChiTietXepPhong.setRowHeight(28);
        tblChiTietXepPhong.getTableHeader().setBackground(new Color(30, 58, 138));
        tblChiTietXepPhong.getTableHeader().setForeground(Color.WHITE);
        tblChiTietXepPhong.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane sp1 = new JScrollPane(tblChiTietXepPhong);
        sp1.setBorder(BorderFactory.createTitledBorder(" CÁC LƯỢT XẾP PHÒNG CỦA ĐƠN ĐẶT "));
        split.setLeftComponent(sp1);

        tblKhachOThucTe = new JTable();
        tblKhachOThucTe.setRowHeight(26);
        tblKhachOThucTe.getTableHeader().setBackground(new Color(15, 23, 42));
        tblKhachOThucTe.getTableHeader().setForeground(Color.WHITE);

        JScrollPane sp2 = new JScrollPane(tblKhachOThucTe);
        sp2.setBorder(BorderFactory.createTitledBorder(" KHÁCH Ở THỰC TẾ (ĐĂNG KÝ TẠM TRÚ) "));
        split.setRightComponent(sp2);

        panel.add(split, BorderLayout.CENTER);

        tblChiTietXepPhong.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int r = tblChiTietXepPhong.getSelectedRow();
                if (r >= 0) {
                    Object maCT = tblChiTietXepPhong.getValueAt(r, 0);
                    loadKhachOTable(maCT);
                }
            }
        });

        btnXepPhong.addActionListener(e -> xepPhongChoDon());
        btnCheckIn.addActionListener(e -> thucHienCheckIn());
        btnThemKhachO.addActionListener(e -> themKhachOThucTe());

        return panel;
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

    public void loadAllData() {
        loadKhachHangCombos();
        loadNhanVienCombos();
        loadLoaiPhongCombos();
        loadPhongTrongCombos();
        loadDonDatCombos();
        loadDatPhongTable();
        loadChiTietXepTable();
    }

    private void loadKhachHangCombos() {
        cboKhachHang.removeAllItems();
        cboKhachOThucTe.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaKhachHang, HoTen, SoDienThoai FROM KhachHang")) {
            while (rs.next()) {
                String id = rs.getString(1);
                String name = rs.getString(2);
                String sdt = rs.getString(3);
                ComboItem item = new ComboItem(id, id + " - " + name + " (" + sdt + ")");
                cboKhachHang.addItem(item);
                cboKhachOThucTe.addItem(item);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadNhanVienCombos() {
        cboNhanVien.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaNhanVien, HoTen FROM NhanVien WHERE TrangThai='Đang làm việc'")) {
            while (rs.next()) {
                cboNhanVien.addItem(new ComboItem(rs.getString(1), rs.getString(1) + " - " + rs.getString(2)));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadLoaiPhongCombos() {
        cboLoaiPhongXep.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaLoaiPhong, TenLoaiPhong FROM LoaiPhong")) {
            while (rs.next()) {
                cboLoaiPhongXep.addItem(new ComboItem(rs.getString(1), rs.getString(1) + " - " + rs.getString(2)));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadPhongTrongCombos() {
        cboPhongTrong.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaPhong, SoPhong, TrangThai FROM Phong WHERE TrangThai = 'Trống'")) {
            while (rs.next()) {
                cboPhongTrong.addItem(new ComboItem(rs.getString(1), rs.getString(2) + " (Phòng trống)"));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadDonDatCombos() {
        cboDonDatXepPhong.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaDatPhong, TrangThai FROM DatPhong WHERE TrangThai NOT IN ('Hủy', 'Hoàn thành')")) {
            while (rs.next()) {
                cboDonDatXepPhong.addItem(new ComboItem(rs.getString(1), rs.getString(1) + " [" + rs.getString(2) + "]"));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadDatPhongTable() {
        try {
            String sql = "SELECT dp.MaDatPhong AS 'Mã Đặt', kh.HoTen AS 'Khách Hàng', " +
                         "COALESCE(nv.HoTen, '---') AS 'Nhân Viên', dp.NgayDat AS 'Ngày Đặt', dp.TrangThai AS 'Trạng Thái' " +
                         "FROM DatPhong dp JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                         "LEFT JOIN NhanVien nv ON dp.MaNhanVien = nv.MaNhanVien ORDER BY dp.NgayDat DESC";
            tblDatPhong.setModel(DatabaseHelper.executeQuery(sql));
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadChiTietXepTable() {
        try {
            String sql = "SELECT ct.MaChiTietDatPhong AS 'Mã CT', ct.MaDatPhong AS 'Mã Đặt', " +
                         "COALESCE(p.SoPhong, 'Chưa gán') AS 'Số Phòng', COALESCE(lp.TenLoaiPhong, '---') AS 'Loại Phòng', " +
                         "ct.GiaThueThucTe AS 'Giá Chốt', ct.NgayNhanDuKien AS 'Nhận DK', ct.NgayTraDuKien AS 'Trả DK', " +
                         "ct.NgayNhanThucTe AS 'Check-in Thực Tế', ct.NgayTraThucTe AS 'Check-out Thực Tế' " +
                         "FROM ChiTietDatPhong ct " +
                         "LEFT JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                         "LEFT JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong " +
                         "ORDER BY ct.MaChiTietDatPhong DESC";
            tblChiTietXepPhong.setModel(DatabaseHelper.executeQuery(sql));
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadKhachOTable(Object maCT) {
        if (maCT == null) return;
        try {
            String sql = "SELECT kh.HoTen AS 'Họ Tên', kh.SoGiayTo AS 'Số CCCD/Hộ Chiếu', " +
                         "kh.QuocTich AS 'Quốc Tịch', ko.ThoiGianCheckIn AS 'Giờ Check-in' " +
                         "FROM KhachOThucTe ko JOIN KhachHang kh ON ko.MaKhachHang = kh.MaKhachHang " +
                         "WHERE ko.MaChiTietDatPhong = ?";
            tblKhachOThucTe.setModel(DatabaseHelper.executeQuery(sql, maCT));
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void taoDonDatPhong() {
        String ma = txtMaDP.getText().trim();
        ComboItem kh = (ComboItem) cboKhachHang.getSelectedItem();
        ComboItem nv = (ComboItem) cboNhanVien.getSelectedItem();
        String tt = (String) cboTrangThaiDP.getSelectedItem();

        if (ma.isEmpty() || kh == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã đặt phòng và chọn khách hàng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DatabaseHelper.executeUpdate("INSERT INTO DatPhong (MaDatPhong, MaKhachHang, MaNhanVien, NgayDat, TrangThai) VALUES (?, ?, ?, NOW(), ?)",
                    ma.toUpperCase(), kh.getKey(), nv != null ? nv.getKey() : null, tt);
            JOptionPane.showMessageDialog(this, "Tạo đơn đặt phòng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadDatPhongTable();
            loadDonDatCombos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void huyDonDatPhong() {
        int r = tblDatPhong.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn đặt phòng cần hủy!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ma = tblDatPhong.getValueAt(r, 0).toString();
        if (JOptionPane.showConfirmDialog(this, "Xác nhận hủy đơn đặt phòng [" + ma + "]?", "Xác nhận hủy", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                DatabaseHelper.executeUpdate("UPDATE DatPhong SET TrangThai='Hủy' WHERE MaDatPhong=?", ma);
                // Giải phóng các phòng đã gán
                DatabaseHelper.executeUpdate("UPDATE Phong p JOIN ChiTietDatPhong ct ON p.MaPhong = ct.MaPhong SET p.TrangThai='Trống' WHERE ct.MaDatPhong=?", ma);
                JOptionPane.showMessageDialog(this, "Đã hủy đơn đặt phòng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadDatPhongTable();
                loadDonDatCombos();
                loadPhongTrongCombos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xepPhongChoDon() {
        ComboItem dp = (ComboItem) cboDonDatXepPhong.getSelectedItem();
        ComboItem lp = (ComboItem) cboLoaiPhongXep.getSelectedItem();
        ComboItem p = (ComboItem) cboPhongTrong.getSelectedItem();
        String gia = txtGiaThueThucTe.getText().trim();
        String nhan = txtNgayNhanDuKien.getText().trim();
        String tra = txtNgayTraDuKien.getText().trim();

        if (dp == null || lp == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn đặt phòng và loại phòng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String sql = "INSERT INTO ChiTietDatPhong (MaDatPhong, MaPhong, NgayNhanDuKien, NgayTraDuKien, GiaThueThucTe) " +
                         "VALUES (?, ?, ?, ?, ?)";
            DatabaseHelper.executeUpdate(sql, dp.getKey(), p != null ? p.getKey() : null, nhan, tra, Double.parseDouble(gia));

            JOptionPane.showMessageDialog(this, "Phân bổ phòng cho đơn đặt thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadChiTietXepTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void thucHienCheckIn() {
        int r = tblChiTietXepPhong.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lượt xếp phòng cần thực hiện Check-in từ bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object maCT = tblChiTietXepPhong.getValueAt(r, 0);
        String maDP = tblChiTietXepPhong.getValueAt(r, 1).toString();
        String soPhong = tblChiTietXepPhong.getValueAt(r, 2).toString();

        if ("Chưa gán".equals(soPhong)) {
            JOptionPane.showMessageDialog(this, "Lượt đặt này chưa gán phòng vật lý! Vui lòng gán phòng trước khi Check-in.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Cập nhật ngày nhận thực tế
            DatabaseHelper.executeUpdate("UPDATE ChiTietDatPhong SET NgayNhanThucTe=NOW() WHERE MaChiTietDatPhong=?", maCT);
            // 2. Chuyển phòng sang 'Đang ở'
            DatabaseHelper.executeUpdate("UPDATE Phong SET TrangThai='Đang ở' WHERE SoPhong=?", soPhong);
            // 3. Chuyển trạng thái đơn sang 'Đã nhận phòng'
            DatabaseHelper.executeUpdate("UPDATE DatPhong SET TrangThai='Đã nhận phòng' WHERE MaDatPhong=?", maDP);

            // 4. Tự động thêm khách đại diện vào danh sách khách ở thực tế nếu chưa có
            String maKH = (String) DatabaseHelper.executeScalar("SELECT MaKhachHang FROM DatPhong WHERE MaDatPhong=?", maDP);
            if (maKH != null) {
                DatabaseHelper.executeUpdate("INSERT IGNORE INTO KhachOThucTe (MaChiTietDatPhong, MaKhachHang, ThoiGianCheckIn) VALUES (?, ?, NOW())", maCT, maKH);
            }

            JOptionPane.showMessageDialog(this, "🎉 CHECK-IN THÀNH CÔNG!\nPhòng [" + soPhong + "] đã chuyển sang trạng thái 'Đang ở'.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadChiTietXepTable();
            loadKhachOTable(maCT);
            loadPhongTrongCombos();
            loadDatPhongTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi Check-in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themKhachOThucTe() {
        int r = tblChiTietXepPhong.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lượt phòng cần khai báo tạm trú từ bảng trước!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object maCT = tblChiTietXepPhong.getValueAt(r, 0);
        ComboItem kh = (ComboItem) cboKhachOThucTe.getSelectedItem();
        if (kh == null) return;

        try {
            DatabaseHelper.executeUpdate("INSERT INTO KhachOThucTe (MaChiTietDatPhong, MaKhachHang, ThoiGianCheckIn) VALUES (?, ?, NOW())",
                    maCT, kh.getKey());
            JOptionPane.showMessageDialog(this, "Đã ghi nhận khách lưu trú vào phòng thành công (Đăng ký tạm trú)!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadKhachOTable(maCT);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Khách này đã được khai báo trong phòng rồi!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
}

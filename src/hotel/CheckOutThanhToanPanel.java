package hotel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CheckOutThanhToanPanel extends JPanel {
    // Check-out & Lập Hóa đơn
    private JComboBox<ComboItem> cboPhongCheckOut;
    private JTextField txtMaHoaDon, txtTienPhong, txtTienDichVu, txtPhuPhi, txtGiamGia, txtVAT;
    private JLabel lblTongThanhToan, lblDaDatCoc, lblConLaiPhaiTra;
    private JComboBox<ComboItem> cboNhanVienThuNgan;
    private JTable tblHoaDon;

    // Lịch sử thanh toán & Đặt cọc
    private JComboBox<ComboItem> cboDonDatThanhToan;
    private JTextField txtSoTienGD, txtGhiChuGD;
    private JComboBox<String> cboPhuongThuc;
    private JTable tblLichSuThanhToan;

    public static class ComboItem {
        private String key;
        private String value;
        private String extra;
        public ComboItem(String key, String value) { this(key, value, ""); }
        public ComboItem(String key, String value, String extra) {
            this.key = key; this.value = value; this.extra = extra;
        }
        public String getKey() { return key; }
        public String getValue() { return value; }
        public String getExtra() { return extra; }
        @Override public String toString() { return value; }
    }

    public CheckOutThanhToanPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(241, 245, 249));

        initUI();
        loadAllData();
    }

    private void initUI() {
        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setBackground(new Color(26, 54, 93));
        pnlTitle.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lblTitle = new JLabel("THỦ TỤC CHECK-OUT, TỔNG HỢP CHI PHÍ & ĐỐI SOÁT DÒNG TIỀN THANH TOÁN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("* Tổng hợp tiền phòng + dịch vụ, trừ cọc, xuất hóa đơn tài chính & quản lý dòng tiền nhiều đợt");
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubTitle.setForeground(new Color(203, 213, 225));

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSubTitle);
        add(pnlTitle, BorderLayout.NORTH);

        JTabbedPane subTabs = new JTabbedPane();
        subTabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        subTabs.addTab("🧾 1. Thực Hiện Check-Out & Xuất Hóa Đơn", createCheckOutTab());
        subTabs.addTab("💳 2. Quản Lý Đặt Cọc & Đối Soát Dòng Tiền", createThanhToanTab());

        add(subTabs, BorderLayout.CENTER);
    }

    // ==============================================================
    // TAB 1: CHECK-OUT & XUẤT HÓA ĐƠN
    // ==============================================================
    private JPanel createCheckOutTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " TÍNH TOÁN & XUẤT HÓA ĐƠN CHECK-OUT ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        cboPhongCheckOut = new JComboBox<>();
        txtMaHoaDon = new JTextField("HD" + System.currentTimeMillis() % 100000, 10);
        cboNhanVienThuNgan = new JComboBox<>();

        txtTienPhong = new JTextField("0", 10);
        txtTienDichVu = new JTextField("0", 10);
        txtPhuPhi = new JTextField("0", 8);
        txtGiamGia = new JTextField("0", 8);
        txtVAT = new JTextField("10", 4);

        lblTongThanhToan = new JLabel("0 VNĐ");
        lblTongThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTongThanhToan.setForeground(new Color(16, 185, 129));

        lblDaDatCoc = new JLabel("0 VNĐ");
        lblDaDatCoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDaDatCoc.setForeground(new Color(37, 99, 235));

        lblConLaiPhaiTra = new JLabel("0 VNĐ");
        lblConLaiPhaiTra.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblConLaiPhaiTra.setForeground(new Color(239, 68, 68));

        g.gridx = 0; g.gridy = 0; pnlInput.add(new JLabel("Phòng Check-out:"), g);
        g.gridx = 1; g.gridwidth = 2; pnlInput.add(cboPhongCheckOut, g);
        g.gridwidth = 1;
        g.gridx = 3; g.gridy = 0; pnlInput.add(new JLabel("Mã hóa đơn:"), g);
        g.gridx = 4; pnlInput.add(txtMaHoaDon, g);
        g.gridx = 5; pnlInput.add(new JLabel("Thu ngân:"), g);
        g.gridx = 6; pnlInput.add(cboNhanVienThuNgan, g);

        g.gridx = 0; g.gridy = 1; pnlInput.add(new JLabel("Tiền phòng:"), g);
        g.gridx = 1; pnlInput.add(txtTienPhong, g);
        g.gridx = 2; pnlInput.add(new JLabel("Tiền dịch vụ:"), g);
        g.gridx = 3; pnlInput.add(txtTienDichVu, g);
        g.gridx = 4; pnlInput.add(new JLabel("Phụ thu:"), g);
        g.gridx = 5; pnlInput.add(txtPhuPhi, g);

        g.gridx = 0; g.gridy = 2; pnlInput.add(new JLabel("Giảm giá / Voucher:"), g);
        g.gridx = 1; pnlInput.add(txtGiamGia, g);
        g.gridx = 2; pnlInput.add(new JLabel("Thuế VAT (%):"), g);
        g.gridx = 3; pnlInput.add(txtVAT, g);
        g.gridx = 4; pnlInput.add(new JLabel("Tổng hóa đơn:"), g);
        g.gridx = 5; pnlInput.add(lblTongThanhToan, g);

        g.gridx = 0; g.gridy = 3; pnlInput.add(new JLabel("Đã đặt cọc:"), g);
        g.gridx = 1; pnlInput.add(lblDaDatCoc, g);
        g.gridx = 2; pnlInput.add(new JLabel("CẦN THANH TOÁN:"), g);
        g.gridx = 3; g.gridwidth = 2; pnlInput.add(lblConLaiPhaiTra, g);
        g.gridwidth = 1;

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        pnlBtns.setOpaque(false);
        JButton btnTinhToan = createBtn("🔄 Tính Lại Chi Phí", new Color(37, 99, 235));
        JButton btnXuatHoaDon = createBtn("✅ HOÀN TẤT CHECK-OUT & XUẤT HÓA ĐƠN", new Color(16, 185, 129));

        pnlBtns.add(btnTinhToan);
        pnlBtns.add(btnXuatHoaDon);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        panel.add(pnlTop, BorderLayout.NORTH);

        tblHoaDon = new JTable();
        tblHoaDon.setRowHeight(28);
        tblHoaDon.getTableHeader().setBackground(new Color(30, 58, 138));
        tblHoaDon.getTableHeader().setForeground(Color.WHITE);

        panel.add(new JScrollPane(tblHoaDon), BorderLayout.CENTER);

        cboPhongCheckOut.addActionListener(e -> tinhToanChiPhiPhong());
        btnTinhToan.addActionListener(e -> capNhatTongTien());
        btnXuatHoaDon.addActionListener(e -> xuatHoaDonCheckOut());

        return panel;
    }

    // ==============================================================
    // TAB 2: ĐẶT CỌC & DÒNG TIỀN
    // ==============================================================
    private JPanel createThanhToanTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " GHI NHẬN GIAO DỊCH DÒNG TIỀN / ĐẶT CỌC ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        cboDonDatThanhToan = new JComboBox<>();
        txtSoTienGD = new JTextField(12);
        cboPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Quẹt thẻ"});
        txtGhiChuGD = new JTextField("Đặt cọc giữ phòng 30%", 25);

        g.gridx = 0; g.gridy = 0; pnlInput.add(new JLabel("Đơn đặt phòng:"), g);
        g.gridx = 1; pnlInput.add(cboDonDatThanhToan, g);
        g.gridx = 2; g.gridy = 0; pnlInput.add(new JLabel("Số tiền giao dịch (VNĐ):"), g);
        g.gridx = 3; pnlInput.add(txtSoTienGD, g);

        g.gridx = 0; g.gridy = 1; pnlInput.add(new JLabel("Phương thức:"), g);
        g.gridx = 1; pnlInput.add(cboPhuongThuc, g);
        g.gridx = 2; g.gridy = 1; pnlInput.add(new JLabel("Ghi chú:"), g);
        g.gridx = 3; pnlInput.add(txtGhiChuGD, g);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlBtns.setOpaque(false);
        JButton btnLuuGD = createBtn("💾 Ghi Nhận Dòng Tiền", new Color(16, 185, 129));
        JButton btnXoaGD = createBtn("🗑️ Xóa Giao Dịch", new Color(239, 68, 68));

        pnlBtns.add(btnLuuGD);
        pnlBtns.add(btnXoaGD);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        panel.add(pnlTop, BorderLayout.NORTH);

        tblLichSuThanhToan = new JTable();
        tblLichSuThanhToan.setRowHeight(28);
        tblLichSuThanhToan.getTableHeader().setBackground(new Color(30, 58, 138));
        tblLichSuThanhToan.getTableHeader().setForeground(Color.WHITE);

        panel.add(new JScrollPane(tblLichSuThanhToan), BorderLayout.CENTER);

        btnLuuGD.addActionListener(e -> ghiNhanGiaoDich());
        btnXoaGD.addActionListener(e -> xoaGiaoDich());

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
        loadPhongDangOCombos();
        loadNhanVienCombos();
        loadDonDatCombos();
        loadHoaDonTable();
        loadLichSuThanhToanTable();
    }

    private void loadPhongDangOCombos() {
        cboPhongCheckOut.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT ct.MaChiTietDatPhong, p.SoPhong, dp.MaDatPhong, kh.HoTen " +
                     "FROM ChiTietDatPhong ct " +
                     "JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                     "JOIN DatPhong dp ON ct.MaDatPhong = dp.MaDatPhong " +
                     "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                     "WHERE ct.NgayNhanThucTe IS NOT NULL AND ct.NgayTraThucTe IS NULL")) {
            while (rs.next()) {
                String id = rs.getString(1);
                String room = rs.getString(2);
                String dp = rs.getString(3);
                String guest = rs.getString(4);
                cboPhongCheckOut.addItem(new ComboItem(id, "Phòng " + room + " (" + dp + " - " + guest + ")", dp));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadNhanVienCombos() {
        cboNhanVienThuNgan.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaNhanVien, HoTen FROM NhanVien WHERE TrangThai='Đang làm việc'")) {
            while (rs.next()) {
                cboNhanVienThuNgan.addItem(new ComboItem(rs.getString(1), rs.getString(1) + " - " + rs.getString(2)));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadDonDatCombos() {
        cboDonDatThanhToan.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT dp.MaDatPhong, kh.HoTen, dp.TrangThai FROM DatPhong dp JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang")) {
            while (rs.next()) {
                cboDonDatThanhToan.addItem(new ComboItem(rs.getString(1), rs.getString(1) + " - " + rs.getString(2) + " [" + rs.getString(3) + "]"));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadHoaDonTable() {
        try {
            String sql = "SELECT hd.MaHoaDon AS 'Mã HĐ', hd.MaDatPhong AS 'Mã Đặt', " +
                         "kh.HoTen AS 'Khách Hàng', nv.HoTen AS 'Thu Ngân', hd.NgayLap AS 'Ngày Lập', " +
                         "hd.TienPhong AS 'Tiền Phòng', hd.TienDichVu AS 'Tiền DV', hd.TongThanhToan AS 'Tổng Tiền', " +
                         "hd.TrangThai AS 'Trạng Thái' " +
                         "FROM HoaDon hd " +
                         "JOIN DatPhong dp ON hd.MaDatPhong = dp.MaDatPhong " +
                         "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                         "JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien " +
                         "ORDER BY hd.NgayLap DESC";
            tblHoaDon.setModel(DatabaseHelper.executeQuery(sql));
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadLichSuThanhToanTable() {
        try {
            String sql = "SELECT ls.MaGiaoDich AS 'Mã GD', ls.MaDatPhong AS 'Mã Đặt', " +
                         "ls.SoTien AS 'Số Tiền', ls.PhuongThucTT AS 'Phương Thức', " +
                         "ls.ThoiGianThanhToan AS 'Thời Gian', ls.GhiChu AS 'Ghi Chú' " +
                         "FROM LichSuThanhToan ls ORDER BY ls.ThoiGianThanhToan DESC";
            tblLichSuThanhToan.setModel(DatabaseHelper.executeQuery(sql));
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void tinhToanChiPhiPhong() {
        ComboItem it = (ComboItem) cboPhongCheckOut.getSelectedItem();
        if (it == null) return;

        String maCT = it.getKey();
        String maDP = it.getExtra();

        try {
            // 1. Tiền phòng (tính số ngày ở tối thiểu 1 ngày * GiaThueThucTe)
            String sqlPhong = "SELECT GREATEST(DATEDIFF(NOW(), NgayNhanThucTe), 1) * GiaThueThucTe FROM ChiTietDatPhong WHERE MaChiTietDatPhong = ?";
            Object tpObj = DatabaseHelper.executeScalar(sqlPhong, maCT);
            double tienPhong = tpObj != null ? ((Number) tpObj).doubleValue() : 0;
            txtTienPhong.setText(String.format("%.0f", tienPhong));

            // 2. Tiền dịch vụ
            String sqlDV = "SELECT COALESCE(SUM(SoLuong * DonGiaBan), 0) FROM SuDungDichVu WHERE MaChiTietDatPhong = ?";
            Object tdvObj = DatabaseHelper.executeScalar(sqlDV, maCT);
            double tienDV = tdvObj != null ? ((Number) tdvObj).doubleValue() : 0;
            txtTienDichVu.setText(String.format("%.0f", tienDV));

            // 3. Đã đặt cọc
            String sqlCoc = "SELECT COALESCE(SUM(SoTien), 0) FROM LichSuThanhToan WHERE MaDatPhong = ?";
            Object cocObj = DatabaseHelper.executeScalar(sqlCoc, maDP);
            double daCoc = cocObj != null ? ((Number) cocObj).doubleValue() : 0;
            lblDaDatCoc.setText(String.format("%,.0f VNĐ", daCoc));

            capNhatTongTien();

        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void capNhatTongTien() {
        try {
            double tp = Double.parseDouble(txtTienPhong.getText().trim());
            double tdv = Double.parseDouble(txtTienDichVu.getText().trim());
            double phuPhi = Double.parseDouble(txtPhuPhi.getText().trim());
            double giamGia = Double.parseDouble(txtGiamGia.getText().trim());
            double vat = Double.parseDouble(txtVAT.getText().trim());

            double subTotal = tp + tdv + phuPhi - giamGia;
            double tong = subTotal * (1.0 + vat / 100.0);
            lblTongThanhToan.setText(String.format("%,.0f VNĐ", tong));

            // Trừ số tiền đã cọc
            String daCocStr = lblDaDatCoc.getText().replaceAll("[^0-9]", "");
            double daCoc = daCocStr.isEmpty() ? 0 : Double.parseDouble(daCocStr);
            double conLai = Math.max(0, tong - daCoc);
            lblConLaiPhaiTra.setText(String.format("%,.0f VNĐ", conLai));

        } catch (Exception ignored) {}
    }

    private void xuatHoaDonCheckOut() {
        ComboItem it = (ComboItem) cboPhongCheckOut.getSelectedItem();
        ComboItem nv = (ComboItem) cboNhanVienThuNgan.getSelectedItem();
        String maHD = txtMaHoaDon.getText().trim();

        if (it == null || nv == null || maHD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng check-out và thu ngân!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maCT = it.getKey();
        String maDP = it.getExtra();

        try {
            double tp = Double.parseDouble(txtTienPhong.getText().trim());
            double tdv = Double.parseDouble(txtTienDichVu.getText().trim());
            double phuPhi = Double.parseDouble(txtPhuPhi.getText().trim());
            double giamGia = Double.parseDouble(txtGiamGia.getText().trim());
            double vat = Double.parseDouble(txtVAT.getText().trim());
            double tong = (tp + tdv + phuPhi - giamGia) * (1.0 + vat / 100.0);

            // 1. Tạo bản ghi Hóa đơn (TongThanhToan la cot GENERATED ALWAYS AS STORED nen MySQL tu tinh)
            String sqlHD = "INSERT INTO HoaDon (MaHoaDon, MaDatPhong, MaNhanVien, NgayLap, TienPhong, TienDichVu, GiamGia, PhuPhi, ThueVAT, TrangThai) " +
                           "VALUES (?, ?, ?, NOW(), ?, ?, ?, ?, ?, 'Hoàn tất') " +
                           "ON DUPLICATE KEY UPDATE TienPhong=VALUES(TienPhong), TienDichVu=VALUES(TienDichVu), GiamGia=VALUES(GiamGia), PhuPhi=VALUES(PhuPhi), ThueVAT=VALUES(ThueVAT), TrangThai='Hoàn tất'";
            DatabaseHelper.executeUpdate(sqlHD, maHD, maDP, nv.getKey(), tp, tdv, giamGia, phuPhi, vat);

            // 2. Ghi nhận giao dịch thanh toán kết thúc (nếu còn tiền phải trả)
            String conLaiStr = lblConLaiPhaiTra.getText().replaceAll("[^0-9]", "");
            double conLai = conLaiStr.isEmpty() ? 0 : Double.parseDouble(conLaiStr);
            if (conLai > 0) {
                DatabaseHelper.executeUpdate("INSERT INTO LichSuThanhToan (MaDatPhong, SoTien, PhuongThucTT, ThoiGianThanhToan, TrangThaiGiaoDich, GhiChu) " +
                        "VALUES (?, ?, 'Tiền mặt', NOW(), 'Thành công', 'Thanh toán hoàn tất check-out')", maDP, conLai);
            }

            // 3. Cập nhật ngày trả thực tế của phòng
            DatabaseHelper.executeUpdate("UPDATE ChiTietDatPhong SET NgayTraThucTe=NOW() WHERE MaChiTietDatPhong=?", maCT);

            // 4. Lấy mã phòng vật lý và chuyển trạng thái phòng sang 'Đang dọn'
            DatabaseHelper.executeUpdate("UPDATE Phong p JOIN ChiTietDatPhong ct ON p.MaPhong = ct.MaPhong SET p.TrangThai='Đang dọn' WHERE ct.MaChiTietDatPhong=?", maCT);

            // 5. Chuyển trạng thái đơn sang 'Hoàn thành'
            DatabaseHelper.executeUpdate("UPDATE DatPhong SET TrangThai='Hoàn thành' WHERE MaDatPhong=?", maDP);

            JOptionPane.showMessageDialog(this,
                    "🎉 CHECK-OUT & XUẤT HÓA ĐƠN THÀNH CÔNG!\n\n" +
                    "- Hóa đơn: " + maHD + "\n" +
                    "- Tổng thanh toán: " + String.format("%,.0f VNĐ", tong) + "\n" +
                    "- Phòng đã tự động chuyển sang trạng thái 'Đang dọn'.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            loadAllData();
            txtMaHoaDon.setText("HD" + System.currentTimeMillis() % 100000);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi Check-out: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ghiNhanGiaoDich() {
        ComboItem dp = (ComboItem) cboDonDatThanhToan.getSelectedItem();
        String soTien = txtSoTienGD.getText().trim();
        String phuongThuc = (String) cboPhuongThuc.getSelectedItem();
        String ghiChu = txtGhiChuGD.getText().trim();

        if (dp == null || soTien.isEmpty()) return;

        try {
            DatabaseHelper.executeUpdate("INSERT INTO LichSuThanhToan (MaDatPhong, SoTien, PhuongThucTT, ThoiGianThanhToan, TrangThaiGiaoDich, GhiChu) " +
                    "VALUES (?, ?, ?, NOW(), 'Thành công', ?)", dp.getKey(), Double.parseDouble(soTien), phuongThuc, ghiChu);

            // Tự động chuyển đơn sang 'Đã cọc' nếu đang ở 'Chờ cọc'
            DatabaseHelper.executeUpdate("UPDATE DatPhong SET TrangThai='Đã cọc' WHERE MaDatPhong=? AND TrangThai='Chờ cọc'", dp.getKey());

            JOptionPane.showMessageDialog(this, "Ghi nhận giao dịch thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadLichSuThanhToanTable();
            txtSoTienGD.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaGiaoDich() {
        int r = tblLichSuThanhToan.getSelectedRow();
        if (r < 0) return;
        Object ma = tblLichSuThanhToan.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Xóa giao dịch này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                DatabaseHelper.executeUpdate("DELETE FROM LichSuThanhToan WHERE MaGiaoDich=?", ma);
                loadLichSuThanhToanTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

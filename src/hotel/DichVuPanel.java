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

public class DichVuPanel extends JPanel {
    // Gọi dịch vụ
    private JComboBox<ComboItem> cboPhongDangO;
    private JComboBox<ComboItem> cboDichVuGoi;
    private JSpinner spnSoLuong;
    private JLabel lblDonGiaHienHanh;
    private JTable tblSuDungDichVu;

    // Danh mục dịch vụ
    private JTextField txtMaDV, txtTenDV, txtDonGiaDV, txtDonViTinh;
    private JTable tblDichVu;

    public static class ComboItem {
        private String key;
        private String value;
        private double price;
        public ComboItem(String key, String value, double price) {
            this.key = key; this.value = value; this.price = price;
        }
        public String getKey() { return key; }
        public String getValue() { return value; }
        public double getPrice() { return price; }
        @Override public String toString() { return value; }
    }

    public DichVuPanel() {
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

        JLabel lblTitle = new JLabel("QUẢN LÝ DỊCH VỤ & GHI NHẬN TIÊU DÙNG CỦA PHÒNG (MINIBAR, GIẶT ỦI, SPA)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("* Khóa giá bán tại thời điểm gọi dịch vụ (Price Snapshot DonGiaBan) để phục vụ kế toán xuất hóa đơn");
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubTitle.setForeground(new Color(203, 213, 225));

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSubTitle);
        add(pnlTitle, BorderLayout.NORTH);

        JTabbedPane subTabs = new JTabbedPane();
        subTabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        subTabs.addTab("🛎️ 1. Ghi Nhận Lượt Phòng Sử Dụng Dịch Vụ", createGoiDichVuTab());
        subTabs.addTab("📋 2. Quản Lý Danh Mục Dịch Vụ & Đơn Giá", createDanhMucDVTab());

        add(subTabs, BorderLayout.CENTER);
    }

    private JPanel createGoiDichVuTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " GỌI DỊCH VỤ CHO PHÒNG ĐANG LƯU TRÚ ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        cboPhongDangO = new JComboBox<>();
        cboDichVuGoi = new JComboBox<>();
        spnSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        lblDonGiaHienHanh = new JLabel("0 VNĐ");
        lblDonGiaHienHanh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDonGiaHienHanh.setForeground(new Color(37, 99, 235));

        g.gridx = 0; g.gridy = 0; pnlInput.add(new JLabel("Phòng đang ở:"), g);
        g.gridx = 1; pnlInput.add(cboPhongDangO, g);
        g.gridx = 2; g.gridy = 0; pnlInput.add(new JLabel("Dịch vụ:"), g);
        g.gridx = 3; pnlInput.add(cboDichVuGoi, g);

        g.gridx = 0; g.gridy = 1; pnlInput.add(new JLabel("Số lượng:"), g);
        g.gridx = 1; pnlInput.add(spnSoLuong, g);
        g.gridx = 2; g.gridy = 1; pnlInput.add(new JLabel("Đơn giá hiện hành:"), g);
        g.gridx = 3; pnlInput.add(lblDonGiaHienHanh, g);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlBtns.setOpaque(false);
        JButton btnGoiDV = createBtn("➕ Ghi Nhận Gọi Dịch Vụ", new Color(16, 185, 129));
        JButton btnXoaLuot = createBtn("🗑️ Xóa Lượt Dùng", new Color(239, 68, 68));
        JButton btnLamMoi = createBtn("🔄 Làm Mới", new Color(100, 116, 139));

        pnlBtns.add(btnGoiDV);
        pnlBtns.add(btnXoaLuot);
        pnlBtns.add(btnLamMoi);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        panel.add(pnlTop, BorderLayout.NORTH);

        tblSuDungDichVu = new JTable();
        tblSuDungDichVu.setRowHeight(28);
        tblSuDungDichVu.getTableHeader().setBackground(new Color(30, 58, 138));
        tblSuDungDichVu.getTableHeader().setForeground(Color.WHITE);
        tblSuDungDichVu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        panel.add(new JScrollPane(tblSuDungDichVu), BorderLayout.CENTER);

        cboDichVuGoi.addActionListener(e -> {
            ComboItem it = (ComboItem) cboDichVuGoi.getSelectedItem();
            if (it != null) {
                lblDonGiaHienHanh.setText(String.format("%,.0f VNĐ", it.getPrice()));
            }
        });

        cboPhongDangO.addActionListener(e -> loadSuDungDichVuTable());

        btnGoiDV.addActionListener(e -> ghiNhanGoiDichVu());
        btnXoaLuot.addActionListener(e -> xoaLuotDung());
        btnLamMoi.addActionListener(e -> loadAllData());

        return panel;
    }

    private JPanel createDanhMucDVTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " DANH MỤC DỊCH VỤ & BẢNG GIÁ ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtMaDV = new JTextField(10);
        txtTenDV = new JTextField(15);
        txtDonGiaDV = new JTextField(10);
        txtDonViTinh = new JTextField("Lượt", 8);

        g.gridx = 0; g.gridy = 0; pnlInput.add(new JLabel("Mã dịch vụ (*):"), g);
        g.gridx = 1; pnlInput.add(txtMaDV, g);
        g.gridx = 2; g.gridy = 0; pnlInput.add(new JLabel("Tên dịch vụ (*):"), g);
        g.gridx = 3; pnlInput.add(txtTenDV, g);

        g.gridx = 0; g.gridy = 1; pnlInput.add(new JLabel("Đơn giá niêm yết (VNĐ):"), g);
        g.gridx = 1; pnlInput.add(txtDonGiaDV, g);
        g.gridx = 2; g.gridy = 1; pnlInput.add(new JLabel("Đơn vị tính:"), g);
        g.gridx = 3; pnlInput.add(txtDonViTinh, g);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlBtns.setOpaque(false);
        JButton btnThemDV = createBtn("➕ Thêm Dịch Vụ", new Color(16, 185, 129));
        JButton btnSuaDV = createBtn("✏️ Cập Nhật", new Color(37, 99, 235));
        JButton btnXoaDV = createBtn("🗑️ Xóa", new Color(239, 68, 68));

        pnlBtns.add(btnThemDV);
        pnlBtns.add(btnSuaDV);
        pnlBtns.add(btnXoaDV);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        panel.add(pnlTop, BorderLayout.NORTH);

        tblDichVu = new JTable();
        tblDichVu.setRowHeight(28);
        tblDichVu.getTableHeader().setBackground(new Color(30, 58, 138));
        tblDichVu.getTableHeader().setForeground(Color.WHITE);

        panel.add(new JScrollPane(tblDichVu), BorderLayout.CENTER);

        btnThemDV.addActionListener(e -> themDichVu());
        btnSuaDV.addActionListener(e -> suaDichVu());
        btnXoaDV.addActionListener(e -> xoaDichVu());

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
        loadDichVuCombos();
        loadDanhMucDichVuTable();
        loadSuDungDichVuTable();
    }

    private void loadPhongDangOCombos() {
        cboPhongDangO.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT ct.MaChiTietDatPhong, p.SoPhong, kh.HoTen " +
                     "FROM ChiTietDatPhong ct " +
                     "JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                     "JOIN DatPhong dp ON ct.MaDatPhong = dp.MaDatPhong " +
                     "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                     "WHERE ct.NgayNhanThucTe IS NOT NULL AND ct.NgayTraThucTe IS NULL")) {
            while (rs.next()) {
                String id = rs.getString(1);
                String room = rs.getString(2);
                String guest = rs.getString(3);
                cboPhongDangO.addItem(new ComboItem(id, "Phòng " + room + " - " + guest, 0));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadDichVuCombos() {
        cboDichVuGoi.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaDichVu, TenDichVu, DonGia, DonViTinh FROM DichVu")) {
            while (rs.next()) {
                String id = rs.getString(1);
                String name = rs.getString(2);
                double price = rs.getDouble(3);
                String unit = rs.getString(4);
                cboDichVuGoi.addItem(new ComboItem(id, name + " (" + String.format("%,.0f", price) + " đ/" + unit + ")", price));
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadDanhMucDichVuTable() {
        try {
            String sql = "SELECT MaDichVu AS 'Mã DV', TenDichVu AS 'Tên Dịch Vụ', " +
                         "DonGia AS 'Đơn Giá Hiện Hành', DonViTinh AS 'Đơn Vị Tính' FROM DichVu";
            tblDichVu.setModel(DatabaseHelper.executeQuery(sql));
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadSuDungDichVuTable() {
        ComboItem phong = (ComboItem) cboPhongDangO.getSelectedItem();
        String filter = "";
        if (phong != null) {
            filter = " WHERE s.MaChiTietDatPhong = " + phong.getKey() + " ";
        }

        try {
            String sql = "SELECT s.MaLuotSuDung AS 'Mã Lượt', p.SoPhong AS 'Phòng', " +
                         "dv.TenDichVu AS 'Dịch Vụ', s.SoLuong AS 'Số Lượng', " +
                         "s.DonGiaBan AS 'Đơn Giá Chốt', (s.SoLuong * s.DonGiaBan) AS 'Thành Tiền', " +
                         "s.ThoiGianSuDung AS 'Thời Gian Dùng' " +
                         "FROM SuDungDichVu s " +
                         "JOIN DichVu dv ON s.MaDichVu = dv.MaDichVu " +
                         "JOIN ChiTietDatPhong ct ON s.MaChiTietDatPhong = ct.MaChiTietDatPhong " +
                         "JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                         filter +
                         "ORDER BY s.ThoiGianSuDung DESC";
            tblSuDungDichVu.setModel(DatabaseHelper.executeQuery(sql));
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void ghiNhanGoiDichVu() {
        ComboItem phong = (ComboItem) cboPhongDangO.getSelectedItem();
        ComboItem dv = (ComboItem) cboDichVuGoi.getSelectedItem();
        int soLuong = (Integer) spnSoLuong.getValue();

        if (phong == null || dv == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng đang ở và dịch vụ gọi!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Price Snapshot: Đóng băng DonGiaBan tại thời điểm gọi
            String sql = "INSERT INTO SuDungDichVu (MaChiTietDatPhong, MaDichVu, SoLuong, DonGiaBan, ThoiGianSuDung) " +
                         "VALUES (?, ?, ?, ?, NOW())";
            DatabaseHelper.executeUpdate(sql, phong.getKey(), dv.getKey(), soLuong, dv.getPrice());

            JOptionPane.showMessageDialog(this, "Đã ghi nhận dịch vụ cho phòng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadSuDungDichVuTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi ghi nhận dịch vụ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaLuotDung() {
        int r = tblSuDungDichVu.getSelectedRow();
        if (r < 0) return;
        Object ma = tblSuDungDichVu.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Xóa lượt gọi dịch vụ này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                DatabaseHelper.executeUpdate("DELETE FROM SuDungDichVu WHERE MaLuotSuDung=?", ma);
                loadSuDungDichVuTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void themDichVu() {
        String ma = txtMaDV.getText().trim();
        String ten = txtTenDV.getText().trim();
        String gia = txtDonGiaDV.getText().trim();
        String dvt = txtDonViTinh.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || gia.isEmpty()) return;

        try {
            DatabaseHelper.executeUpdate("INSERT INTO DichVu (MaDichVu, TenDichVu, DonGia, DonViTinh) VALUES (?, ?, ?, ?)",
                    ma.toUpperCase(), ten, Double.parseDouble(gia), dvt);
            JOptionPane.showMessageDialog(this, "Thêm dịch vụ thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadDanhMucDichVuTable();
            loadDichVuCombos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaDichVu() {
        String ma = txtMaDV.getText().trim();
        String ten = txtTenDV.getText().trim();
        String gia = txtDonGiaDV.getText().trim();
        String dvt = txtDonViTinh.getText().trim();
        if (ma.isEmpty()) return;

        try {
            DatabaseHelper.executeUpdate("UPDATE DichVu SET TenDichVu=?, DonGia=?, DonViTinh=? WHERE MaDichVu=?",
                    ten, Double.parseDouble(gia), dvt, ma);
            JOptionPane.showMessageDialog(this, "Cập nhật dịch vụ thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadDanhMucDichVuTable();
            loadDichVuCombos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaDichVu() {
        String ma = txtMaDV.getText().trim();
        if (ma.isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Xóa dịch vụ [" + ma + "]?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                DatabaseHelper.executeUpdate("DELETE FROM DichVu WHERE MaDichVu=?", ma);
                loadDanhMucDichVuTable();
                loadDichVuCombos();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Không thể xóa do đã có lượt dùng dịch vụ này: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

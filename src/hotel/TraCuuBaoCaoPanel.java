package hotel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class TraCuuBaoCaoPanel extends JPanel {
    // Tab 1: Tra cứu lịch sử
    private JTextField txtTuKhoaKH;
    private JTable tblLichSuLuuTru;
    private JTable tblLichSuDichVu;

    // Tab 2: Báo cáo & Tỷ lệ lấp đầy
    private JComboBox<Integer> cboThang, cboNam;
    private JTable tblDoanhThu;
    private JLabel lblTongDoanhThu, lblSoHoaDon, lblTyLeLapDay, lblTongPhong, lblPhongDangO;
    private JProgressBar prgCongSuat;
    private static final DecimalFormat df = new DecimalFormat("#,##0 VNĐ");

    public TraCuuBaoCaoPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(241, 245, 249));

        initUI();
    }

    private void initUI() {
        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setBackground(new Color(26, 54, 93));
        pnlTitle.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lblTitle = new JLabel("TRA CỨU LỊCH SỬ LƯU TRÚ & THỐNG KÊ CÔNG SUẤT, TỶ LỆ LẤP ĐẦY KHÁCH SẠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("* Theo dõi lịch sử khách hàng, doanh thu thực tế, tỷ lệ lấp đầy phòng (Occupancy Rate) & hiệu suất vận hành");
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubTitle.setForeground(new Color(203, 213, 225));

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSubTitle);
        add(pnlTitle, BorderLayout.NORTH);

        JTabbedPane subTabs = new JTabbedPane();
        subTabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        subTabs.addTab("🔍 1. Tra Cứu Lịch Sử Lưu Trú & Dịch Vụ Khách Hàng", createLichSuTab());
        subTabs.addTab("📈 2. Thống Kê Doanh Thu, Tỷ Lệ Lấp Đầy & Công Suất", createThongKeTab());

        add(subTabs, BorderLayout.CENTER);
    }

    // ==============================================================
    // TAB 1: TRA CỨU LỊCH SỬ KHÁCH HÀNG
    // ==============================================================
    private JPanel createLichSuTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        pnlSearch.setBackground(Color.WHITE);
        pnlSearch.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        txtTuKhoaKH = new JTextField(20);
        JButton btnTraCuu = createBtn("🔍 Tra Cứu Khách", new Color(37, 99, 235));

        pnlSearch.add(new JLabel("Nhập Tên / SĐT / CCCD / Mã KH:"));
        pnlSearch.add(txtTuKhoaKH);
        pnlSearch.add(btnTraCuu);

        panel.add(pnlSearch, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.5);

        tblLichSuLuuTru = new JTable();
        tblLichSuLuuTru.setRowHeight(28);
        tblLichSuLuuTru.getTableHeader().setBackground(new Color(30, 58, 138));
        tblLichSuLuuTru.getTableHeader().setForeground(Color.WHITE);

        JScrollPane sp1 = new JScrollPane(tblLichSuLuuTru);
        sp1.setBorder(BorderFactory.createTitledBorder(" 1. LỊCH SỬ CÁC ĐỢT THUÊ PHÒNG & LƯU TRÚ CỦA KHÁCH "));
        split.setTopComponent(sp1);

        tblLichSuDichVu = new JTable();
        tblLichSuDichVu.setRowHeight(28);
        tblLichSuDichVu.getTableHeader().setBackground(new Color(15, 23, 42));
        tblLichSuDichVu.getTableHeader().setForeground(Color.WHITE);

        JScrollPane sp2 = new JScrollPane(tblLichSuDichVu);
        sp2.setBorder(BorderFactory.createTitledBorder(" 2. LỊCH SỬ DỊCH VỤ KHÁCH ĐÃ TỪNG SỬ DỤNG (MINIBAR, SPA, ĐƯA ĐÓN) "));
        split.setBottomComponent(sp2);

        panel.add(split, BorderLayout.CENTER);

        btnTraCuu.addActionListener(e -> traCuuLichSuKhach());
        txtTuKhoaKH.addActionListener(e -> traCuuLichSuKhach());

        return panel;
    }

    // ==============================================================
    // TAB 2: THỐNG KÊ DOANH THU & TỶ LỆ LẤP ĐẦY PHÒNG
    // ==============================================================
    private JPanel createThongKeTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Dashboard KPI Cards
        JPanel pnlDashboard = new JPanel(new GridLayout(1, 4, 12, 0));
        pnlDashboard.setOpaque(false);

        lblTongDoanhThu = new JLabel("0 VNĐ", SwingConstants.CENTER);
        lblTongDoanhThu.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTongDoanhThu.setForeground(new Color(16, 185, 129));
        pnlDashboard.add(createCard("TỔNG DOANH THU THÁNG", lblTongDoanhThu, new Color(240, 253, 244)));

        lblSoHoaDon = new JLabel("0", SwingConstants.CENTER);
        lblSoHoaDon.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSoHoaDon.setForeground(new Color(37, 99, 235));
        pnlDashboard.add(createCard("SỐ LƯỢT CHECK-OUT", lblSoHoaDon, new Color(239, 246, 255)));

        lblPhongDangO = new JLabel("0 / 0", SwingConstants.CENTER);
        lblPhongDangO.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPhongDangO.setForeground(new Color(217, 119, 6));
        pnlDashboard.add(createCard("PHÒNG ĐANG Ở / TỔNG", lblPhongDangO, new Color(254, 243, 199)));

        JPanel pnlOccupancy = new JPanel(new BorderLayout(4, 4));
        pnlOccupancy.setOpaque(false);
        lblTyLeLapDay = new JLabel("0.0 %", SwingConstants.CENTER);
        lblTyLeLapDay.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTyLeLapDay.setForeground(new Color(220, 38, 38));

        prgCongSuat = new JProgressBar(0, 100);
        prgCongSuat.setStringPainted(true);
        prgCongSuat.setForeground(new Color(16, 185, 129));
        pnlOccupancy.add(lblTyLeLapDay, BorderLayout.CENTER);
        pnlOccupancy.add(prgCongSuat, BorderLayout.SOUTH);

        pnlDashboard.add(createCard("TỶ LỆ LẤP ĐẦY (OCCUPANCY)", pnlOccupancy, new Color(254, 242, 242)));

        // Filter Bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        cboThang = new JComboBox<>();
        for (int i = 1; i <= 12; i++) cboThang.addItem(i);
        cboThang.setSelectedItem(LocalDate.now().getMonthValue());

        cboNam = new JComboBox<>();
        int y = LocalDate.now().getYear();
        for (int yr = y - 2; yr <= y + 2; yr++) cboNam.addItem(yr);
        cboNam.setSelectedItem(y);

        JButton btnXem = createBtn("📊 Xem Thống Kê", new Color(37, 99, 235));
        pnlFilter.add(new JLabel("Tháng:"));
        pnlFilter.add(cboThang);
        pnlFilter.add(new JLabel("Năm:"));
        pnlFilter.add(cboNam);
        pnlFilter.add(btnXem);

        JPanel pnlTopCombined = new JPanel(new BorderLayout(0, 10));
        pnlTopCombined.setOpaque(false);
        pnlTopCombined.add(pnlDashboard, BorderLayout.NORTH);
        pnlTopCombined.add(pnlFilter, BorderLayout.SOUTH);

        panel.add(pnlTopCombined, BorderLayout.NORTH);

        // Table
        tblDoanhThu = new JTable();
        tblDoanhThu.setRowHeight(28);
        tblDoanhThu.getTableHeader().setBackground(new Color(30, 58, 138));
        tblDoanhThu.getTableHeader().setForeground(Color.WHITE);

        panel.add(new JScrollPane(tblDoanhThu), BorderLayout.CENTER);

        btnXem.addActionListener(e -> capNhatThongKe());

        SwingUtilities.invokeLater(this::capNhatThongKe);

        return panel;
    }

    private JPanel createCard(String title, JComponent comp, Color bg) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(8, 10, 8, 10)));

        JLabel lblT = new JLabel(title, SwingConstants.CENTER);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblT.setForeground(new Color(71, 85, 105));

        card.add(lblT, BorderLayout.NORTH);
        card.add(comp, BorderLayout.CENTER);
        return card;
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

    private void traCuuLichSuKhach() {
        String kw = txtTuKhoaKH.getText().trim();
        if (kw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm (Tên, SĐT, CCCD hoặc Mã KH)!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Lịch sử lưu trú
            String sql1 = "SELECT dp.MaDatPhong AS 'Mã Đặt', kh.HoTen AS 'Khách Hàng', " +
                          "COALESCE(p.SoPhong, 'Chưa gán') AS 'Số Phòng', COALESCE(lp.TenLoaiPhong, '---') AS 'Loại Phòng', " +
                          "ct.GiaThueThucTe AS 'Giá Thuê Chốt', ct.NgayNhanDuKien AS 'Nhận DK', " +
                          "ct.NgayTraDuKien AS 'Trả DK', ct.NgayNhanThucTe AS 'Check-in', ct.NgayTraThucTe AS 'Check-out', " +
                          "dp.TrangThai AS 'Trạng Thái Đơn' " +
                          "FROM KhachHang kh " +
                          "JOIN DatPhong dp ON kh.MaKhachHang = dp.MaKhachHang " +
                          "JOIN ChiTietDatPhong ct ON dp.MaDatPhong = ct.MaDatPhong " +
                          "LEFT JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                          "LEFT JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong " +
                          "WHERE kh.HoTen LIKE ? OR kh.SoDienThoai LIKE ? OR kh.SoGiayTo LIKE ? OR kh.MaKhachHang = ? " +
                          "ORDER BY dp.NgayDat DESC";
            tblLichSuLuuTru.setModel(DatabaseHelper.executeQuery(sql1, "%" + kw + "%", "%" + kw + "%", "%" + kw + "%", kw));

            // 2. Lịch sử dịch vụ
            String sql2 = "SELECT kh.HoTen AS 'Khách Hàng', p.SoPhong AS 'Phòng Gọi', " +
                          "dv.TenDichVu AS 'Tên Dịch Vụ', s.SoLuong AS 'Số Lượng', " +
                          "s.DonGiaBan AS 'Đơn Giá Bán', (s.SoLuong * s.DonGiaBan) AS 'Thành Tiền', " +
                          "s.ThoiGianSuDung AS 'Thời Điểm Dùng' " +
                          "FROM KhachHang kh " +
                          "JOIN DatPhong dp ON kh.MaKhachHang = dp.MaKhachHang " +
                          "JOIN ChiTietDatPhong ct ON dp.MaDatPhong = ct.MaDatPhong " +
                          "JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                          "JOIN SuDungDichVu s ON ct.MaChiTietDatPhong = s.MaChiTietDatPhong " +
                          "JOIN DichVu dv ON s.MaDichVu = dv.MaDichVu " +
                          "WHERE kh.HoTen LIKE ? OR kh.SoDienThoai LIKE ? OR kh.SoGiayTo LIKE ? OR kh.MaKhachHang = ? " +
                          "ORDER BY s.ThoiGianSuDung DESC";
            tblLichSuDichVu.setModel(DatabaseHelper.executeQuery(sql2, "%" + kw + "%", "%" + kw + "%", "%" + kw + "%", kw));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tra cứu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatThongKe() {
        try {
            int th = (Integer) cboThang.getSelectedItem();
            int nm = (Integer) cboNam.getSelectedItem();

            // 1. Tải bảng doanh thu
            String sqlDT = "SELECT hd.MaHoaDon AS 'Mã HĐ', dp.MaDatPhong AS 'Mã Đặt', kh.HoTen AS 'Khách Hàng', " +
                           "hd.NgayLap AS 'Ngày Lập HĐ', hd.TienPhong AS 'Tiền Phòng', hd.TienDichVu AS 'Tiền DV', " +
                           "hd.GiamGia AS 'Giảm Giá', hd.PhuPhi AS 'Phụ Phí', hd.ThueVAT AS 'VAT (%)', " +
                           "hd.TongThanhToan AS 'Tổng Doanh Thu' " +
                           "FROM HoaDon hd " +
                           "JOIN DatPhong dp ON hd.MaDatPhong = dp.MaDatPhong " +
                           "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                           "WHERE MONTH(hd.NgayLap) = ? AND YEAR(hd.NgayLap) = ? " +
                           "ORDER BY hd.NgayLap DESC";
            DefaultTableModel dtModel = DatabaseHelper.executeQuery(sqlDT, th, nm);
            tblDoanhThu.setModel(dtModel);

            double tongTien = 0;
            for (int r = 0; r < dtModel.getRowCount(); r++) {
                Object v = dtModel.getValueAt(r, 9);
                if (v instanceof Number) tongTien += ((Number) v).doubleValue();
            }

            lblTongDoanhThu.setText(df.format(tongTien));
            lblSoHoaDon.setText(String.valueOf(dtModel.getRowCount()));

            // 2. Tính Tỷ lệ lấp đầy phòng (Occupancy Rate)
            Object totalRoomsObj = DatabaseHelper.executeScalar("SELECT COUNT(*) FROM Phong");
            Object occupiedRoomsObj = DatabaseHelper.executeScalar("SELECT COUNT(*) FROM Phong WHERE TrangThai = 'Đang ở'");

            long totalRooms = totalRoomsObj != null ? ((Number) totalRoomsObj).longValue() : 0;
            long occupied = occupiedRoomsObj != null ? ((Number) occupiedRoomsObj).longValue() : 0;

            lblPhongDangO.setText(occupied + " / " + totalRooms);

            double rate = totalRooms > 0 ? ((double) occupied / totalRooms) * 100.0 : 0.0;
            lblTyLeLapDay.setText(String.format("%.1f %%", rate));
            prgCongSuat.setValue((int) rate);

            if (rate >= 70.0) {
                prgCongSuat.setForeground(new Color(16, 185, 129)); // Green
            } else if (rate >= 40.0) {
                prgCongSuat.setForeground(new Color(245, 158, 11)); // Orange
            } else {
                prgCongSuat.setForeground(new Color(239, 68, 68)); // Red
            }

        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}

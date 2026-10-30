package hotel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class BaoCaoDoanhThuPanel extends JPanel {
    private JComboBox<Integer> cboThang;
    private JComboBox<Integer> cboNam;
    private JTable tblBaoCao;
    private JLabel lblSoHoaDon, lblTongTienPhong, lblTongTienDV, lblTongDoanhThu;
    private JButton btnXemBaoCao;
    private static final DecimalFormat df = new DecimalFormat("#,##0 VNĐ");

    public BaoCaoDoanhThuPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(241, 245, 249));

        initUI();
        xemBaoCao();
    }

    private void initUI() {
        // 1. Header Banner
        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setBackground(new Color(26, 54, 93));
        pnlTitle.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lblTitle = new JLabel("BÁO CÁO DOANH THU THEO THÁNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("* Tổng hợp dòng tiền tiền phòng, tiền dịch vụ, phụ thu và thuế VAT theo kỳ hóa đơn");
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubTitle.setForeground(new Color(203, 213, 225));

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSubTitle);
        add(pnlTitle, BorderLayout.NORTH);

        // 2. Filter Bar
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlFilter.setBackground(Color.WHITE);
        pnlFilter.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        cboThang = new JComboBox<>();
        for (int i = 1; i <= 12; i++) cboThang.addItem(i);
        cboThang.setSelectedItem(LocalDate.now().getMonthValue());

        cboNam = new JComboBox<>();
        int curYear = LocalDate.now().getYear();
        for (int y = curYear - 2; y <= curYear + 2; y++) cboNam.addItem(y);
        cboNam.setSelectedItem(curYear);

        btnXemBaoCao = new JButton("📊 Xem Báo Cáo");
        btnXemBaoCao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnXemBaoCao.setBackground(new Color(37, 99, 235));
        btnXemBaoCao.setForeground(Color.WHITE);
        btnXemBaoCao.setFocusPainted(false);
        btnXemBaoCao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pnlFilter.add(new JLabel("Chọn Tháng:"));
        pnlFilter.add(cboThang);
        pnlFilter.add(new JLabel("Chọn Năm:"));
        pnlFilter.add(cboNam);
        pnlFilter.add(btnXemBaoCao);

        // 3. Center Table
        tblBaoCao = new JTable();
        tblBaoCao.setRowHeight(28);
        tblBaoCao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblBaoCao.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblBaoCao.getTableHeader().setBackground(new Color(30, 58, 138));
        tblBaoCao.getTableHeader().setForeground(Color.WHITE);
        tblBaoCao.setSelectionBackground(new Color(219, 234, 254));
        tblBaoCao.setSelectionForeground(Color.BLACK);
        tblBaoCao.setGridColor(new Color(226, 232, 240));

        JScrollPane scrollTable = new JScrollPane(tblBaoCao);
        scrollTable.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " BẢNG CHI TIẾT HÓA ĐƠN & DOANH SỐ ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)));

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setOpaque(false);
        pnlCenter.add(pnlFilter, BorderLayout.NORTH);
        pnlCenter.add(scrollTable, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // 4. Footer Summary Panel
        JPanel pnlSummary = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 12));
        pnlSummary.setBackground(Color.WHITE);
        pnlSummary.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        lblSoHoaDon = new JLabel("🧾 Số hóa đơn: 0");
        lblSoHoaDon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSoHoaDon.setForeground(new Color(30, 41, 59));

        lblTongTienPhong = new JLabel("🏨 Tiền phòng: 0 VNĐ");
        lblTongTienPhong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTongTienPhong.setForeground(new Color(37, 99, 235));

        lblTongTienDV = new JLabel("🍹 Tiền dịch vụ: 0 VNĐ");
        lblTongTienDV.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTongTienDV.setForeground(new Color(217, 119, 6));

        lblTongDoanhThu = new JLabel("💰 TỔNG DOANH THU: 0 VNĐ");
        lblTongDoanhThu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTongDoanhThu.setForeground(new Color(16, 185, 129));

        pnlSummary.add(lblSoHoaDon);
        pnlSummary.add(lblTongTienPhong);
        pnlSummary.add(lblTongTienDV);
        pnlSummary.add(lblTongDoanhThu);

        add(pnlSummary, BorderLayout.SOUTH);

        btnXemBaoCao.addActionListener(e -> xemBaoCao());
    }

    private void xemBaoCao() {
        try {
            int thang = (Integer) cboThang.getSelectedItem();
            int nam = (Integer) cboNam.getSelectedItem();

            String sql = "SELECT hd.MaHoaDon AS 'Mã HĐ', " +
                         "kh.HoTen AS 'Khách Hàng', " +
                         "nv.HoTen AS 'Nhân Viên Lập', " +
                         "hd.NgayLap AS 'Ngày Lập HĐ', " +
                         "hd.TienPhong AS 'Tiền Phòng', " +
                         "hd.TienDichVu AS 'Tiền Dịch Vụ', " +
                         "hd.GiamGia AS 'Giảm Giá', " +
                         "hd.PhuPhi AS 'Phụ Phí', " +
                         "hd.ThueVAT AS 'VAT (%)', " +
                         "hd.TongThanhToan AS 'Tổng Thanh Toán', " +
                         "hd.TrangThai AS 'Trạng Thái' " +
                         "FROM HoaDon hd " +
                         "JOIN DatPhong dp ON hd.MaDatPhong = dp.MaDatPhong " +
                         "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                         "JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien " +
                         "WHERE MONTH(hd.NgayLap) = ? AND YEAR(hd.NgayLap) = ? " +
                         "ORDER BY hd.NgayLap ASC";

            DefaultTableModel model = DatabaseHelper.executeQuery(sql, thang, nam);
            tblBaoCao.setModel(model);

            double tongPhong = 0;
            double tongDV = 0;
            double tongTien = 0;

            for (int r = 0; r < model.getRowCount(); r++) {
                Object p = model.getValueAt(r, 4);
                Object dv = model.getValueAt(r, 5);
                Object t = model.getValueAt(r, 9);

                if (p instanceof Number) tongPhong += ((Number) p).doubleValue();
                if (dv instanceof Number) tongDV += ((Number) dv).doubleValue();
                if (t instanceof Number) tongTien += ((Number) t).doubleValue();
            }

            lblSoHoaDon.setText("🧾 Số hóa đơn: " + model.getRowCount());
            lblTongTienPhong.setText("🏨 Tiền phòng: " + df.format(tongPhong));
            lblTongTienDV.setText("🍹 Tiền dịch vụ: " + df.format(tongDV));
            lblTongDoanhThu.setText("💰 TỔNG DOANH THU: " + df.format(tongTien));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lập báo cáo: " + ex.getMessage(), "Lỗi MySQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package hotel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class BaoCaoPhongPanel extends JPanel {
    private JTable tblPhong;
    private JLabel lblTongPhong, lblPhongTrong, lblPhongDangO, lblPhongDangDon, lblPhongBaoTri;
    private JButton btnCapNhat;

    public BaoCaoPhongPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(241, 245, 249));

        initUI();
        loadData();
    }

    private void initUI() {
        // 1. Header Banner
        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setBackground(new Color(26, 54, 93));
        pnlTitle.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lblTitle = new JLabel("BÁO CÁO TÌNH TRẠNG PHÒNG VẬN HÀNH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("* Giám sát trạng thái phòng thực tế và thông tin khách lưu trú theo thời gian thực");
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubTitle.setForeground(new Color(203, 213, 225));

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSubTitle);
        add(pnlTitle, BorderLayout.NORTH);

        // 2. Action Bar
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        btnCapNhat = new JButton("🔄 Làm Mới Trạng Thái");
        btnCapNhat.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCapNhat.setBackground(new Color(16, 185, 129));
        btnCapNhat.setForeground(Color.WHITE);
        btnCapNhat.setFocusPainted(false);
        btnCapNhat.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblGhiChu = new JLabel("💡 Mã màu: 🟢 Xanh lá (Trống) | 🔴 Đỏ (Đang ở) | 🟠 Cam (Đang dọn) | 🟡 Vàng (Bảo trì)");
        lblGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblGhiChu.setForeground(new Color(100, 116, 139));

        pnlHeader.add(btnCapNhat);
        pnlHeader.add(lblGhiChu);

        // 3. Center Table
        tblPhong = new JTable();
        tblPhong.setRowHeight(28);
        tblPhong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblPhong.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblPhong.getTableHeader().setBackground(new Color(30, 58, 138));
        tblPhong.getTableHeader().setForeground(Color.WHITE);
        tblPhong.setSelectionBackground(new Color(219, 234, 254));
        tblPhong.setSelectionForeground(Color.BLACK);
        tblPhong.setGridColor(new Color(226, 232, 240));

        // Thiết lập CellRenderer tô màu trạng thái
        tblPhong.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    Object statusObj = table.getValueAt(row, 5); // Cột 'Trạng Thái'
                    String status = statusObj != null ? statusObj.toString() : "";

                    if ("Trống".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(220, 252, 231)); // Light green
                        c.setForeground(new Color(22, 101, 52));
                    } else if ("Đang ở".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(254, 226, 226)); // Light red
                        c.setForeground(new Color(153, 27, 27));
                    } else if ("Đang dọn".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(254, 237, 213)); // Light orange
                        c.setForeground(new Color(154, 52, 18));
                    } else if ("Bảo trì".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(254, 240, 138)); // Light yellow
                        c.setForeground(new Color(133, 77, 14));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblPhong);
        scrollTable.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " DANH SÁCH PHÒNG VẬT LÝ & TÌNH TRẠNG VẬN HÀNH ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)));

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 10));
        pnlCenter.setOpaque(false);
        pnlCenter.add(pnlHeader, BorderLayout.NORTH);
        pnlCenter.add(scrollTable, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // 4. Footer Summary
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 12));
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));

        lblTongPhong = new JLabel("Tổng số phòng: 0");
        lblTongPhong.setFont(new Font("Segoe UI", Font.BOLD, 13));

        lblPhongTrong = new JLabel("🟢 Phòng trống: 0");
        lblPhongTrong.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPhongTrong.setForeground(new Color(16, 185, 129));

        lblPhongDangO = new JLabel("🔴 Đang ở: 0");
        lblPhongDangO.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPhongDangO.setForeground(new Color(239, 68, 68));

        lblPhongDangDon = new JLabel("🟠 Đang dọn dẹp: 0");
        lblPhongDangDon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPhongDangDon.setForeground(new Color(249, 115, 22));

        lblPhongBaoTri = new JLabel("🟡 Bảo trì: 0");
        lblPhongBaoTri.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPhongBaoTri.setForeground(new Color(202, 138, 4));

        pnlFooter.add(lblTongPhong);
        pnlFooter.add(lblPhongTrong);
        pnlFooter.add(lblPhongDangO);
        pnlFooter.add(lblPhongDangDon);
        pnlFooter.add(lblPhongBaoTri);

        add(pnlFooter, BorderLayout.SOUTH);

        btnCapNhat.addActionListener(e -> loadData());
    }

    private void loadData() {
        try {
            String sql = "SELECT p.MaPhong AS 'Mã Phòng', " +
                         "p.SoPhong AS 'Số Phòng', " +
                         "p.Tang AS 'Tầng', " +
                         "lp.TenLoaiPhong AS 'Loại Phòng', " +
                         "lp.SoNguoiToiDa AS 'Sức Chứa', " +
                         "p.TrangThai AS 'Trạng Thái', " +
                         "COALESCE(kh.HoTen, '---') AS 'Khách Đang Lưu Trú' " +
                         "FROM Phong p " +
                         "JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong " +
                         "LEFT JOIN ChiTietDatPhong ct ON p.MaPhong = ct.MaPhong " +
                         "     AND ct.NgayNhanThucTe IS NOT NULL AND ct.NgayTraThucTe IS NULL " +
                         "LEFT JOIN DatPhong dp ON ct.MaDatPhong = dp.MaDatPhong " +
                         "LEFT JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                         "ORDER BY p.Tang ASC, p.SoPhong ASC";

            DefaultTableModel model = DatabaseHelper.executeQuery(sql);
            tblPhong.setModel(model);

            int trong = 0, dangO = 0, dangDon = 0, baoTri = 0;
            for (int r = 0; r < model.getRowCount(); r++) {
                String st = model.getValueAt(r, 5).toString();
                if ("Trống".equalsIgnoreCase(st)) trong++;
                else if ("Đang ở".equalsIgnoreCase(st)) dangO++;
                else if ("Đang dọn".equalsIgnoreCase(st)) dangDon++;
                else if ("Bảo trì".equalsIgnoreCase(st)) baoTri++;
            }

            lblTongPhong.setText("Tổng số phòng: " + model.getRowCount());
            lblPhongTrong.setText("🟢 Phòng trống: " + trong);
            lblPhongDangO.setText("🔴 Đang ở: " + dangO);
            lblPhongDangDon.setText("🟠 Đang dọn dẹp: " + dangDon);
            lblPhongBaoTri.setText("🟡 Bảo trì: " + baoTri);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải trạng thái phòng: " + ex.getMessage(), "Lỗi MySQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}

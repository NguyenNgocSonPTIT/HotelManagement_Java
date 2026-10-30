package hotel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TimKiemPanel extends JPanel {
    private JTextField txtTenKH, txtSDT, txtSoGiayTo;
    private JComboBox<String> cboTrangThai;
    private JTable tblKetQua;
    private JLabel lblStatus;
    private JButton btnTimKiem, btnLamMoi;

    public TimKiemPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(241, 245, 249));

        initUI();
        thucHienTimKiem();
    }

    private void initUI() {
        // 1. Header Banner
        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setBackground(new Color(26, 54, 93));
        pnlTitle.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lblTitle = new JLabel("TRA CỨU VÀ TÌM KIẾM ĐA ĐIỀU KIỆN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("* Tìm kiếm linh hoạt theo Tên, Số điện thoại, Số CCCD/Passport và Trạng thái đơn");
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubTitle.setForeground(new Color(203, 213, 225));

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSubTitle);
        add(pnlTitle, BorderLayout.NORTH);

        // 2. Center Panel
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setOpaque(false);

        // --- Panel Tiêu chí ---
        JPanel pnlCriteria = new JPanel(new GridBagLayout());
        pnlCriteria.setBackground(Color.WHITE);
        pnlCriteria.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " BỘ LỌC TÌM KIẾM ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(10, 15, 10, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTenKH = new JTextField(15);
        txtSDT = new JTextField(12);
        txtSoGiayTo = new JTextField(12);
        cboTrangThai = new JComboBox<>(new String[]{"-- Tất cả trạng thái --", "Chờ cọc", "Đã cọc", "Đã nhận phòng", "Hủy", "Hoàn thành"});

        gbc.gridx = 0; gbc.gridy = 0; pnlCriteria.add(new JLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 1; pnlCriteria.add(txtTenKH, gbc);
        gbc.gridx = 2; pnlCriteria.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 3; pnlCriteria.add(txtSDT, gbc);
        gbc.gridx = 4; pnlCriteria.add(new JLabel("Số CCCD/Hộ chiếu:"), gbc);
        gbc.gridx = 5; pnlCriteria.add(txtSoGiayTo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlCriteria.add(new JLabel("Trạng thái đơn:"), gbc);
        gbc.gridx = 1; pnlCriteria.add(cboTrangThai, gbc);

        // Panel Nút tìm kiếm
        JPanel pnlActionSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlActionSearch.setOpaque(false);

        btnTimKiem = createStyledButton("🔍 Tìm Kiếm", new Color(16, 185, 129));
        btnLamMoi = createStyledButton("🔄 Đặt Lại", new Color(100, 116, 139));

        pnlActionSearch.add(btnTimKiem);
        pnlActionSearch.add(btnLamMoi);

        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 4;
        pnlCriteria.add(pnlActionSearch, gbc);
        gbc.gridwidth = 1;

        pnlCenter.add(pnlCriteria, BorderLayout.NORTH);

        // --- Danh sách kết quả ---
        tblKetQua = new JTable();
        tblKetQua.setRowHeight(28);
        tblKetQua.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblKetQua.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblKetQua.getTableHeader().setBackground(new Color(30, 58, 138));
        tblKetQua.getTableHeader().setForeground(Color.WHITE);
        tblKetQua.setSelectionBackground(new Color(219, 234, 254));
        tblKetQua.setSelectionForeground(Color.BLACK);
        tblKetQua.setGridColor(new Color(226, 232, 240));

        JScrollPane scrollTable = new JScrollPane(tblKetQua);
        scrollTable.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " KẾT QUẢ TRA CỨU HỒ SƠ & ĐẶT PHÒNG ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)));
        pnlCenter.add(scrollTable, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // 3. Status Bar
        lblStatus = new JLabel(" Sẵn sàng tìm kiếm...");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(new Color(30, 58, 138));
        lblStatus.setBorder(new EmptyBorder(6, 4, 4, 4));
        add(lblStatus, BorderLayout.SOUTH);

        btnTimKiem.addActionListener(e -> thucHienTimKiem());
        btnLamMoi.addActionListener(e -> {
            txtTenKH.setText("");
            txtSDT.setText("");
            txtSoGiayTo.setText("");
            cboTrangThai.setSelectedIndex(0);
            thucHienTimKiem();
        });
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void thucHienTimKiem() {
        try {
            StringBuilder sql = new StringBuilder(
                    "SELECT kh.MaKhachHang AS 'Mã KH', " +
                    "kh.HoTen AS 'Họ Và Tên', " +
                    "kh.SoDienThoai AS 'Số Điện Thoại', " +
                    "kh.SoGiayTo AS 'Số Giấy Tờ', " +
                    "dp.MaDatPhong AS 'Mã Đặt Phòng', " +
                    "dp.NgayDat AS 'Ngày Đặt', " +
                    "dp.TrangThai AS 'Trạng Thái Đơn', " +
                    "p.SoPhong AS 'Số Phòng', " +
                    "lp.TenLoaiPhong AS 'Loại Phòng', " +
                    "ct.NgayNhanDuKien AS 'Nhận Dự Kiến', " +
                    "ct.NgayTraDuKien AS 'Trả Dự Kiến' " +
                    "FROM KhachHang kh " +
                    "LEFT JOIN DatPhong dp ON kh.MaKhachHang = dp.MaKhachHang " +
                    "LEFT JOIN ChiTietDatPhong ct ON dp.MaDatPhong = ct.MaDatPhong " +
                    "LEFT JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                    "LEFT JOIN LoaiPhong lp ON ct.MaLoaiPhong = lp.MaLoaiPhong " +
                    "WHERE 1=1 ");

            List<Object> params = new ArrayList<>();

            if (!txtTenKH.getText().trim().isEmpty()) {
                sql.append(" AND kh.HoTen LIKE ? ");
                params.add("%" + txtTenKH.getText().trim() + "%");
            }
            if (!txtSDT.getText().trim().isEmpty()) {
                sql.append(" AND kh.SoDienThoai LIKE ? ");
                params.add("%" + txtSDT.getText().trim() + "%");
            }
            if (!txtSoGiayTo.getText().trim().isEmpty()) {
                sql.append(" AND kh.SoGiayTo LIKE ? ");
                params.add("%" + txtSoGiayTo.getText().trim() + "%");
            }
            if (cboTrangThai.getSelectedIndex() > 0) {
                sql.append(" AND dp.TrangThai = ? ");
                params.add(cboTrangThai.getSelectedItem());
            }

            sql.append(" ORDER BY dp.NgayDat DESC, kh.MaKhachHang ASC");

            DefaultTableModel model = DatabaseHelper.executeQuery(sql.toString(), params.toArray());
            tblKetQua.setModel(model);
            lblStatus.setText(" Tìm thấy " + model.getRowCount() + " kết quả phù hợp.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + ex.getMessage(), "Lỗi MySQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}

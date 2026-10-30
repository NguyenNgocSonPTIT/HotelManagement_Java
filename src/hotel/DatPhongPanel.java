package hotel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
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
import java.util.Vector;

public class DatPhongPanel extends JPanel {
    private JTextField txtMaDP;
    private JComboBox<ComboItem> cboKhachHang;
    private JComboBox<ComboItem> cboNhanVien;
    private JTextField txtNgayDat;
    private JComboBox<String> cboTrangThai;
    private JTable tblDatPhong;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnReloadKH;

    // Lớp bổ trợ lưu trữ cặp Key (Mã) - Value (Tên hiển thị) cho JComboBox
    public static class ComboItem {
        private String key;
        private String value;

        public ComboItem(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() { return key; }
        public String getValue() { return value; }

        @Override
        public String toString() { return value; }
    }

    public DatPhongPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setBackground(new Color(241, 245, 249));

        initUI();
        loadComboBoxData();
        loadData();

        // Tự động đồng bộ khi bên Khách hàng có thêm/sửa/xóa
        KhachHangPanel.addChangeListener(() -> {
            SwingUtilities.invokeLater(this::loadComboBoxData);
        });
    }

    private void initUI() {
        // 1. Header Banner
        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setBackground(new Color(26, 54, 93));
        pnlTitle.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lblTitle = new JLabel("QUẢN LÝ TIẾP NHẬN ĐẶT PHÒNG TỔNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("* Danh mục khách hàng tự động đồng bộ thời gian thực từ phân hệ Quản lý khách hàng");
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubTitle.setForeground(new Color(203, 213, 225));

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSubTitle);
        add(pnlTitle, BorderLayout.NORTH);

        // 2. Center Panel
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setOpaque(false);

        // --- Panel Nhập liệu ---
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " THÔNG TIN ĐƠN ĐẶT PHÒNG ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(10, 15, 10, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaDP = new JTextField(10);
        cboKhachHang = new JComboBox<>();
        cboKhachHang.setPreferredSize(new Dimension(240, 26));

        // Tự động tải lại khi click mở danh sách
        cboKhachHang.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                loadKhachHangCombo();
            }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });

        btnReloadKH = new JButton("🔄");
        btnReloadKH.setToolTipText("Làm mới danh sách khách hàng từ MySQL");
        btnReloadKH.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReloadKH.addActionListener(e -> {
            loadKhachHangCombo();
            JOptionPane.showMessageDialog(this, "Đã cập nhật danh sách khách hàng mới nhất!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel pnlKHCombine = new JPanel(new BorderLayout(4, 0));
        pnlKHCombine.setOpaque(false);
        pnlKHCombine.add(cboKhachHang, BorderLayout.CENTER);
        pnlKHCombine.add(btnReloadKH, BorderLayout.EAST);

        cboNhanVien = new JComboBox<>();
        cboNhanVien.setPreferredSize(new Dimension(200, 26));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        txtNgayDat = new JTextField(sdf.format(new Date()), 15);

        cboTrangThai = new JComboBox<>(new String[]{"Chờ cọc", "Đã cọc", "Đã nhận phòng", "Hủy", "Hoàn thành"});
        cboTrangThai.setPreferredSize(new Dimension(160, 26));

        // Hàng 0: Mã DP + Khách hàng + Nhân viên
        gbc.gridx = 0; gbc.gridy = 0; pnlInput.add(new JLabel("Mã đặt phòng (*):"), gbc);
        gbc.gridx = 1; pnlInput.add(txtMaDP, gbc);
        gbc.gridx = 2; pnlInput.add(new JLabel("Khách hàng (*):"), gbc);
        gbc.gridx = 3; pnlInput.add(pnlKHCombine, gbc);
        gbc.gridx = 4; pnlInput.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 5; pnlInput.add(cboNhanVien, gbc);

        // Hàng 1: Ngày đặt + Trạng thái
        gbc.gridx = 0; gbc.gridy = 1; pnlInput.add(new JLabel("Ngày đặt:"), gbc);
        gbc.gridx = 1; pnlInput.add(txtNgayDat, gbc);
        gbc.gridx = 2; pnlInput.add(new JLabel("Trạng thái đơn:"), gbc);
        gbc.gridx = 3; pnlInput.add(cboTrangThai, gbc);

        // --- Panel Nút bấm ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        pnlButtons.setBackground(new Color(241, 245, 249));

        btnThem = createStyledButton("➕ Thêm", new Color(16, 185, 129));
        btnSua = createStyledButton("✏️ Sửa", new Color(37, 99, 235));
        btnXoa = createStyledButton("🗑️ Xóa", new Color(239, 68, 68));
        btnLamMoi = createStyledButton("🔄 Làm Mới", new Color(100, 116, 139));

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);

        JPanel pnlTopCombine = new JPanel(new BorderLayout());
        pnlTopCombine.setOpaque(false);
        pnlTopCombine.add(pnlInput, BorderLayout.CENTER);
        pnlTopCombine.add(pnlButtons, BorderLayout.SOUTH);

        pnlCenter.add(pnlTopCombine, BorderLayout.NORTH);

        // --- Danh sách JTable ---
        tblDatPhong = new JTable();
        tblDatPhong.setRowHeight(28);
        tblDatPhong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblDatPhong.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblDatPhong.getTableHeader().setBackground(new Color(30, 58, 138));
        tblDatPhong.getTableHeader().setForeground(Color.WHITE);
        tblDatPhong.setSelectionBackground(new Color(219, 234, 254));
        tblDatPhong.setSelectionForeground(Color.BLACK);
        tblDatPhong.setGridColor(new Color(226, 232, 240));

        tblDatPhong.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblDatPhong.getSelectedRow();
                if (row >= 0) {
                    txtMaDP.setText(getValue(row, 0));
                    selectComboKey(cboKhachHang, getValue(row, 1));
                    selectComboKey(cboNhanVien, getValue(row, 3));
                    txtNgayDat.setText(getValue(row, 5));
                    cboTrangThai.setSelectedItem(getValue(row, 6));
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblDatPhong);
        scrollTable.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " DANH SÁCH ĐƠN ĐẶT PHÒNG TỔNG ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)));
        pnlCenter.add(scrollTable, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // Sự kiện nút bấm
        btnThem.addActionListener(e -> themDatPhong());
        btnSua.addActionListener(e -> suaDatPhong());
        btnXoa.addActionListener(e -> xoaDatPhong());
        btnLamMoi.addActionListener(e -> {
            lamMoi();
            loadComboBoxData();
            loadData();
        });
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String getValue(int row, int col) {
        Object val = tblDatPhong.getValueAt(row, col);
        return val == null ? "" : val.toString();
    }

    private void selectComboKey(JComboBox<ComboItem> combo, String key) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getKey().equalsIgnoreCase(key)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    public void loadComboBoxData() {
        loadKhachHangCombo();
        loadNhanVienCombo();
    }

    public void loadKhachHangCombo() {
        ComboItem current = (ComboItem) cboKhachHang.getSelectedItem();
        String currentKey = current != null ? current.getKey() : null;

        cboKhachHang.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MaKhachHang, HoTen, SoDienThoai FROM KhachHang ORDER BY MaKhachHang DESC")) {
            while (rs.next()) {
                String id = rs.getString("MaKhachHang");
                String name = rs.getString("HoTen");
                String sdt = rs.getString("SoDienThoai");
                cboKhachHang.addItem(new ComboItem(id, id + " - " + name + " (" + sdt + ")"));
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi nạp KhachHang combo: " + ex.getMessage());
        }

        if (currentKey != null) {
            selectComboKey(cboKhachHang, currentKey);
        }
    }

    private void loadNhanVienCombo() {
        cboNhanVien.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MaNhanVien, HoTen FROM NhanVien WHERE TrangThai = 'Đang làm việc'")) {
            while (rs.next()) {
                String id = rs.getString("MaNhanVien");
                String name = rs.getString("HoTen");
                cboNhanVien.addItem(new ComboItem(id, id + " - " + name));
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi nạp NhanVien combo: " + ex.getMessage());
        }
    }

    public void loadData() {
        try {
            String sql = "SELECT dp.MaDatPhong AS 'Mã Đặt Phòng', " +
                         "dp.MaKhachHang AS 'Mã KH', kh.HoTen AS 'Tên Khách Hàng', " +
                         "dp.MaNhanVien AS 'Mã NV', COALESCE(nv.HoTen, '---') AS 'Nhân Viên Tiếp Nhận', " +
                         "dp.NgayDat AS 'Ngày Đặt', dp.TrangThai AS 'Trạng Thái' " +
                         "FROM DatPhong dp " +
                         "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                         "LEFT JOIN NhanVien nv ON dp.MaNhanVien = nv.MaNhanVien " +
                         "ORDER BY dp.NgayDat DESC";
            DefaultTableModel model = DatabaseHelper.executeQuery(sql);
            tblDatPhong.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nạp đơn đặt phòng: " + ex.getMessage(), "Lỗi MySQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void themDatPhong() {
        String maDP = txtMaDP.getText().trim();
        ComboItem kh = (ComboItem) cboKhachHang.getSelectedItem();
        ComboItem nv = (ComboItem) cboNhanVien.getSelectedItem();
        String ngayDat = txtNgayDat.getText().trim();
        String trangThai = (String) cboTrangThai.getSelectedItem();

        if (maDP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã đặt phòng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            txtMaDP.requestFocus();
            return;
        }
        if (kh == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Khách hàng đại diện!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String sql = "INSERT INTO DatPhong (MaDatPhong, MaKhachHang, MaNhanVien, NgayDat, TrangThai) VALUES (?, ?, ?, ?, ?)";
            int rows = DatabaseHelper.executeUpdate(sql,
                    maDP.toUpperCase(),
                    kh.getKey(),
                    nv != null ? nv.getKey() : null,
                    ngayDat.isEmpty() ? new Date() : ngayDat,
                    trangThai);

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Thêm đơn đặt phòng mới thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                lamMoi();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm đơn đặt: " + ex.getMessage(), "Lỗi MySQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaDatPhong() {
        String maDP = txtMaDP.getText().trim();
        ComboItem kh = (ComboItem) cboKhachHang.getSelectedItem();
        ComboItem nv = (ComboItem) cboNhanVien.getSelectedItem();
        String ngayDat = txtNgayDat.getText().trim();
        String trangThai = (String) cboTrangThai.getSelectedItem();

        if (maDP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn đặt phòng cần sửa từ bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String sql = "UPDATE DatPhong SET MaKhachHang=?, MaNhanVien=?, NgayDat=?, TrangThai=? WHERE MaDatPhong=?";
            int rows = DatabaseHelper.executeUpdate(sql,
                    kh != null ? kh.getKey() : null,
                    nv != null ? nv.getKey() : null,
                    ngayDat,
                    trangThai,
                    maDP);

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật đơn đặt phòng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi sửa đơn đặt: " + ex.getMessage(), "Lỗi MySQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaDatPhong() {
        String maDP = txtMaDP.getText().trim();
        if (maDP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn đặt phòng muốn xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa đơn đặt phòng [" + maDP + "] không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM DatPhong WHERE MaDatPhong=?";
                int rows = DatabaseHelper.executeUpdate(sql, maDP);
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Đã xóa đơn đặt phòng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                    lamMoi();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Không thể xóa đơn đặt phòng này: " + ex.getMessage(), "Lỗi ràng buộc", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void lamMoi() {
        txtMaDP.setText("");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        txtNgayDat.setText(sdf.format(new Date()));
        cboTrangThai.setSelectedIndex(0);
        txtMaDP.requestFocus();
    }
}

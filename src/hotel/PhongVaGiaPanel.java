package hotel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PhongVaGiaPanel extends JPanel {
    // Tab 1: Phòng thực tế
    private JTextField txtMaPhong, txtSoPhong, txtTang;
    private JComboBox<ComboItem> cboLoaiPhong;
    private JComboBox<String> cboTrangThai;
    private JTable tblPhong;

    // Tab 2: Loại phòng
    private JTextField txtMaLP, txtTenLP, txtSoNguoiToiDa, txtMoTaLP;
    private JTable tblLoaiPhong;

    // Tab 3: Cấu hình giá
    private JComboBox<ComboItem> cboLoaiPhongGia;
    private JTextField txtGiaTheoDem, txtTuNgay, txtDenNgay;
    private JTable tblGiaPhong;

    public static class ComboItem {
        private String key;
        private String value;
        public ComboItem(String key, String value) { this.key = key; this.value = value; }
        public String getKey() { return key; }
        public String getValue() { return value; }
        @Override public String toString() { return value; }
    }

    public PhongVaGiaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(241, 245, 249));

        initUI();
    }

    private void initUI() {
        // Banner
        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setBackground(new Color(26, 54, 93));
        pnlTitle.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel lblTitle = new JLabel("QUẢN LÝ PHÒNG, LOẠI PHÒNG & CẤU HÌNH BẢNG GIÁ LINH HOẠT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("* Cập nhật trạng thái thời gian thực (Trống, Đang ở, Đang dọn, Bảo trì) & Bảng giá theo mùa");
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubTitle.setForeground(new Color(203, 213, 225));

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSubTitle);
        add(pnlTitle, BorderLayout.NORTH);

        // Sub Tabs
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.setFont(new Font("Segoe UI", Font.BOLD, 13));

        subTabs.addTab("🏨 Danh Sách Phòng Thực Tế & Trạng Thái", createPhongTab());
        subTabs.addTab("🏷️ Danh Mục Loại Phòng", createLoaiPhongTab());
        subTabs.addTab("💲 Cấu Hình Giá Theo Mùa (Dynamic Pricing)", createGiaPhongTab());

        add(subTabs, BorderLayout.CENTER);

        loadAllData();
    }

    // ==============================================================
    // TAB 1: PHÒNG THỰC TẾ
    // ==============================================================
    private JPanel createPhongTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Form nhập
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " CHI TIẾT PHÒNG VẬT LÝ ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtMaPhong = new JTextField(10);
        txtSoPhong = new JTextField(10);
        txtTang = new JTextField("1", 5);
        cboLoaiPhong = new JComboBox<>();
        cboTrangThai = new JComboBox<>(new String[]{"Trống", "Đang ở", "Đang dọn", "Bảo trì"});

        g.gridx = 0; g.gridy = 0; pnlInput.add(new JLabel("Mã phòng (*):"), g);
        g.gridx = 1; pnlInput.add(txtMaPhong, g);
        g.gridx = 2; g.gridy = 0; pnlInput.add(new JLabel("Số phòng (*):"), g);
        g.gridx = 3; pnlInput.add(txtSoPhong, g);
        g.gridx = 4; g.gridy = 0; pnlInput.add(new JLabel("Tầng:"), g);
        g.gridx = 5; pnlInput.add(txtTang, g);

        g.gridx = 0; g.gridy = 1; pnlInput.add(new JLabel("Loại phòng:"), g);
        g.gridx = 1; pnlInput.add(cboLoaiPhong, g);
        g.gridx = 2; g.gridy = 1; pnlInput.add(new JLabel("Trạng thái:"), g);
        g.gridx = 3; pnlInput.add(cboTrangThai, g);

        // Buttons
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlBtns.setOpaque(false);
        JButton btnThem = createBtn("➕ Thêm Phòng", new Color(16, 185, 129));
        JButton btnSua = createBtn("✏️ Cập Nhật", new Color(37, 99, 235));
        JButton btnDoiTT = createBtn("🔄 Đổi Nhanh Trạng Thái", new Color(245, 158, 11));
        JButton btnXoa = createBtn("🗑️ Xóa", new Color(239, 68, 68));
        JButton btnLamMoi = createBtn("Làm Mới", new Color(100, 116, 139));

        pnlBtns.add(btnThem);
        pnlBtns.add(btnSua);
        pnlBtns.add(btnDoiTT);
        pnlBtns.add(btnXoa);
        pnlBtns.add(btnLamMoi);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        panel.add(pnlTop, BorderLayout.NORTH);

        // Table
        tblPhong = new JTable();
        tblPhong.setRowHeight(28);
        tblPhong.getTableHeader().setBackground(new Color(30, 58, 138));
        tblPhong.getTableHeader().setForeground(Color.WHITE);
        tblPhong.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Màu sắc trạng thái
        tblPhong.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    Object st = table.getValueAt(row, 4);
                    if ("Trống".equals(st)) c.setBackground(new Color(220, 252, 231));
                    else if ("Đang ở".equals(st)) c.setBackground(new Color(254, 226, 226));
                    else if ("Đang dọn".equals(st)) c.setBackground(new Color(254, 237, 213));
                    else if ("Bảo trì".equals(st)) c.setBackground(new Color(254, 240, 138));
                    else c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        tblPhong.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int r = tblPhong.getSelectedRow();
                if (r >= 0) {
                    txtMaPhong.setText(tblPhong.getValueAt(r, 0).toString());
                    txtSoPhong.setText(tblPhong.getValueAt(r, 1).toString());
                    txtTang.setText(tblPhong.getValueAt(r, 2).toString());
                    cboTrangThai.setSelectedItem(tblPhong.getValueAt(r, 4).toString());
                }
            }
        });

        panel.add(new JScrollPane(tblPhong), BorderLayout.CENTER);

        btnThem.addActionListener(e -> themPhong());
        btnSua.addActionListener(e -> suaPhong());
        btnDoiTT.addActionListener(e -> doiTrangThaiPhong());
        btnXoa.addActionListener(e -> xoaPhong());
        btnLamMoi.addActionListener(e -> {
            txtMaPhong.setText(""); txtSoPhong.setText(""); txtTang.setText("1");
            loadPhongTable();
        });

        return panel;
    }

    // ==============================================================
    // TAB 2: LOẠI PHÒNG
    // ==============================================================
    private JPanel createLoaiPhongTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " DANH MỤC LOẠI PHÒNG ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtMaLP = new JTextField(10);
        txtTenLP = new JTextField(15);
        txtSoNguoiToiDa = new JTextField("2", 5);
        txtMoTaLP = new JTextField(25);

        g.gridx = 0; g.gridy = 0; pnlInput.add(new JLabel("Mã loại (*):"), g);
        g.gridx = 1; pnlInput.add(txtMaLP, g);
        g.gridx = 2; g.gridy = 0; pnlInput.add(new JLabel("Tên loại phòng (*):"), g);
        g.gridx = 3; pnlInput.add(txtTenLP, g);
        g.gridx = 4; g.gridy = 0; pnlInput.add(new JLabel("Sức chứa tối đa:"), g);
        g.gridx = 5; pnlInput.add(txtSoNguoiToiDa, g);

        g.gridx = 0; g.gridy = 1; pnlInput.add(new JLabel("Mô tả tiện ích:"), g);
        g.gridx = 1; g.gridwidth = 5; pnlInput.add(txtMoTaLP, g);
        g.gridwidth = 1;

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlBtns.setOpaque(false);
        JButton btnThemLP = createBtn("➕ Thêm Loại Phòng", new Color(16, 185, 129));
        JButton btnSuaLP = createBtn("✏️ Cập Nhật", new Color(37, 99, 235));
        JButton btnXoaLP = createBtn("🗑️ Xóa", new Color(239, 68, 68));

        pnlBtns.add(btnThemLP);
        pnlBtns.add(btnSuaLP);
        pnlBtns.add(btnXoaLP);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        panel.add(pnlTop, BorderLayout.NORTH);

        tblLoaiPhong = new JTable();
        tblLoaiPhong.setRowHeight(28);
        tblLoaiPhong.getTableHeader().setBackground(new Color(30, 58, 138));
        tblLoaiPhong.getTableHeader().setForeground(Color.WHITE);
        tblLoaiPhong.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        tblLoaiPhong.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int r = tblLoaiPhong.getSelectedRow();
                if (r >= 0) {
                    txtMaLP.setText(tblLoaiPhong.getValueAt(r, 0).toString());
                    txtTenLP.setText(tblLoaiPhong.getValueAt(r, 1).toString());
                    txtSoNguoiToiDa.setText(tblLoaiPhong.getValueAt(r, 2).toString());
                    txtMoTaLP.setText(tblLoaiPhong.getValueAt(r, 3) != null ? tblLoaiPhong.getValueAt(r, 3).toString() : "");
                }
            }
        });

        panel.add(new JScrollPane(tblLoaiPhong), BorderLayout.CENTER);

        btnThemLP.addActionListener(e -> themLoaiPhong());
        btnSuaLP.addActionListener(e -> suaLoaiPhong());
        btnXoaLP.addActionListener(e -> xoaLoaiPhong());

        return panel;
    }

    // ==============================================================
    // TAB 3: BẢNG GIÁ THEO MÙA (DYNAMIC PRICING)
    // ==============================================================
    private JPanel createGiaPhongTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(241, 245, 249));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " THIẾT LẬP BẢNG GIÁ PHÒNG THEO THỜI GIAN ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(8, 12, 8, 12)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        cboLoaiPhongGia = new JComboBox<>();
        txtGiaTheoDem = new JTextField(12);
        txtTuNgay = new JTextField("2026-01-01", 10);
        txtDenNgay = new JTextField("2026-12-31", 10);

        g.gridx = 0; g.gridy = 0; pnlInput.add(new JLabel("Loại phòng:"), g);
        g.gridx = 1; pnlInput.add(cboLoaiPhongGia, g);
        g.gridx = 2; g.gridy = 0; pnlInput.add(new JLabel("Giá thuê / đêm (VNĐ):"), g);
        g.gridx = 3; pnlInput.add(txtGiaTheoDem, g);

        g.gridx = 0; g.gridy = 1; pnlInput.add(new JLabel("Áp dụng từ ngày:"), g);
        g.gridx = 1; pnlInput.add(txtTuNgay, g);
        g.gridx = 2; g.gridy = 1; pnlInput.add(new JLabel("Đến ngày:"), g);
        g.gridx = 3; pnlInput.add(txtDenNgay, g);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        pnlBtns.setOpaque(false);
        JButton btnThemGia = createBtn("➕ Thiết Lập Mức Giá", new Color(16, 185, 129));
        JButton btnXoaGia = createBtn("🗑️ Xóa Mức Giá", new Color(239, 68, 68));

        pnlBtns.add(btnThemGia);
        pnlBtns.add(btnXoaGia);

        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.setOpaque(false);
        pnlTop.add(pnlInput, BorderLayout.CENTER);
        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        panel.add(pnlTop, BorderLayout.NORTH);

        tblGiaPhong = new JTable();
        tblGiaPhong.setRowHeight(28);
        tblGiaPhong.getTableHeader().setBackground(new Color(30, 58, 138));
        tblGiaPhong.getTableHeader().setForeground(Color.WHITE);
        tblGiaPhong.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        panel.add(new JScrollPane(tblGiaPhong), BorderLayout.CENTER);

        btnThemGia.addActionListener(e -> themGiaPhong());
        btnXoaGia.addActionListener(e -> xoaGiaPhong());

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
        loadLoaiPhongCombo();
        loadPhongTable();
        loadLoaiPhongTable();
        loadGiaPhongTable();
    }

    private void loadLoaiPhongCombo() {
        cboLoaiPhong.removeAllItems();
        cboLoaiPhongGia.removeAllItems();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaLoaiPhong, TenLoaiPhong FROM LoaiPhong")) {
            while (rs.next()) {
                String id = rs.getString(1);
                String name = rs.getString(2);
                cboLoaiPhong.addItem(new ComboItem(id, id + " - " + name));
                cboLoaiPhongGia.addItem(new ComboItem(id, id + " - " + name));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadPhongTable() {
        try {
            String sql = "SELECT p.MaPhong AS 'Mã Phòng', p.SoPhong AS 'Số Phòng', p.Tang AS 'Tầng', " +
                         "lp.TenLoaiPhong AS 'Loại Phòng', p.TrangThai AS 'Trạng Thái' " +
                         "FROM Phong p JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong " +
                         "ORDER BY p.Tang, p.SoPhong";
            tblPhong.setModel(DatabaseHelper.executeQuery(sql));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadLoaiPhongTable() {
        try {
            String sql = "SELECT MaLoaiPhong AS 'Mã Loại', TenLoaiPhong AS 'Tên Loại', " +
                         "SoNguoiToiDa AS 'Sức Chứa', MoTa AS 'Mô Tả Tiện Ích' FROM LoaiPhong";
            tblLoaiPhong.setModel(DatabaseHelper.executeQuery(sql));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadGiaPhongTable() {
        try {
            String sql = "SELECT g.MaGia AS 'Mã Giá', lp.TenLoaiPhong AS 'Loại Phòng', " +
                         "g.GiaTheoDem AS 'Giá / Đêm', g.TuNgay AS 'Từ Ngày', g.DenNgay AS 'Đến Ngày' " +
                         "FROM GiaPhong g JOIN LoaiPhong lp ON g.MaLoaiPhong = lp.MaLoaiPhong " +
                         "ORDER BY g.TuNgay DESC";
            tblGiaPhong.setModel(DatabaseHelper.executeQuery(sql));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void themPhong() {
        String ma = txtMaPhong.getText().trim();
        String so = txtSoPhong.getText().trim();
        String tang = txtTang.getText().trim();
        ComboItem lp = (ComboItem) cboLoaiPhong.getSelectedItem();
        String tt = (String) cboTrangThai.getSelectedItem();

        if (ma.isEmpty() || so.isEmpty() || lp == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã phòng, Số phòng và Loại phòng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String sql = "INSERT INTO Phong (MaPhong, MaLoaiPhong, SoPhong, Tang, TrangThai) VALUES (?, ?, ?, ?, ?)";
            DatabaseHelper.executeUpdate(sql, ma.toUpperCase(), lp.getKey(), so, Integer.parseInt(tang), tt);
            JOptionPane.showMessageDialog(this, "Thêm phòng mới thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadPhongTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm phòng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaPhong() {
        String ma = txtMaPhong.getText().trim();
        String so = txtSoPhong.getText().trim();
        String tang = txtTang.getText().trim();
        ComboItem lp = (ComboItem) cboLoaiPhong.getSelectedItem();
        String tt = (String) cboTrangThai.getSelectedItem();

        if (ma.isEmpty()) return;

        try {
            String sql = "UPDATE Phong SET MaLoaiPhong=?, SoPhong=?, Tang=?, TrangThai=? WHERE MaPhong=?";
            DatabaseHelper.executeUpdate(sql, lp.getKey(), so, Integer.parseInt(tang), tt, ma);
            JOptionPane.showMessageDialog(this, "Cập nhật phòng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadPhongTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa phòng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doiTrangThaiPhong() {
        int r = tblPhong.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng cần đổi trạng thái!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String maPhong = tblPhong.getValueAt(r, 0).toString();
        String[] options = {"Trống", "Đang ở", "Đang dọn", "Bảo trì"};
        String chosen = (String) JOptionPane.showInputDialog(this, "Chọn trạng thái vận hành mới cho phòng [" + maPhong + "]:",
                "Đổi trạng thái phòng", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (chosen != null) {
            try {
                DatabaseHelper.executeUpdate("UPDATE Phong SET TrangThai=? WHERE MaPhong=?", chosen, maPhong);
                loadPhongTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xoaPhong() {
        String ma = txtMaPhong.getText().trim();
        if (ma.isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Xóa phòng [" + ma + "]?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                DatabaseHelper.executeUpdate("DELETE FROM Phong WHERE MaPhong=?", ma);
                loadPhongTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Không thể xóa do phòng đã có lịch sử đặt: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void themLoaiPhong() {
        String ma = txtMaLP.getText().trim();
        String ten = txtTenLP.getText().trim();
        String sc = txtSoNguoiToiDa.getText().trim();
        String mt = txtMoTaLP.getText().trim();

        if (ma.isEmpty() || ten.isEmpty()) return;

        try {
            DatabaseHelper.executeUpdate("INSERT INTO LoaiPhong (MaLoaiPhong, TenLoaiPhong, SoNguoiToiDa, MoTa) VALUES (?, ?, ?, ?)",
                    ma.toUpperCase(), ten, Integer.parseInt(sc), mt);
            JOptionPane.showMessageDialog(this, "Thêm loại phòng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadLoaiPhongTable();
            loadLoaiPhongCombo();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaLoaiPhong() {
        String ma = txtMaLP.getText().trim();
        String ten = txtTenLP.getText().trim();
        String sc = txtSoNguoiToiDa.getText().trim();
        String mt = txtMoTaLP.getText().trim();
        if (ma.isEmpty()) return;

        try {
            DatabaseHelper.executeUpdate("UPDATE LoaiPhong SET TenLoaiPhong=?, SoNguoiToiDa=?, MoTa=? WHERE MaLoaiPhong=?",
                    ten, Integer.parseInt(sc), mt, ma);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadLoaiPhongTable();
            loadLoaiPhongCombo();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaLoaiPhong() {
        String ma = txtMaLP.getText().trim();
        if (ma.isEmpty()) return;
        if (JOptionPane.showConfirmDialog(this, "Xóa loại phòng [" + ma + "]?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                DatabaseHelper.executeUpdate("DELETE FROM LoaiPhong WHERE MaLoaiPhong=?", ma);
                loadLoaiPhongTable();
                loadLoaiPhongCombo();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Không thể xóa do đã có phòng thuộc loại này: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void themGiaPhong() {
        ComboItem lp = (ComboItem) cboLoaiPhongGia.getSelectedItem();
        String gia = txtGiaTheoDem.getText().trim();
        String tu = txtTuNgay.getText().trim();
        String den = txtDenNgay.getText().trim();

        if (lp == null || gia.isEmpty()) return;

        try {
            DatabaseHelper.executeUpdate("INSERT INTO GiaPhong (MaLoaiPhong, GiaTheoDem, TuNgay, DenNgay) VALUES (?, ?, ?, ?)",
                    lp.getKey(), Double.parseDouble(gia), tu, den);
            JOptionPane.showMessageDialog(this, "Thiết lập giá thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadGiaPhongTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thiết lập giá: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaGiaPhong() {
        int r = tblGiaPhong.getSelectedRow();
        if (r < 0) return;
        Object id = tblGiaPhong.getValueAt(r, 0);
        if (JOptionPane.showConfirmDialog(this, "Xóa thiết lập giá này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                DatabaseHelper.executeUpdate("DELETE FROM GiaPhong WHERE MaGia=?", id);
                loadGiaPhongTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

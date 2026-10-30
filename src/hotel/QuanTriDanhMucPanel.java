package hotel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class QuanTriDanhMucPanel extends JPanel {
    private final MainFrame parent;

    // Sidebar items
    private JPanel sidebarPanel;
    private final JButton[] menuButtons = new JButton[5];
    private int currentMenuIndex = 0;

    // Right Content
    private JPanel rightHeader;
    private JLabel lblContentTitle;
    private JLabel lblContentSub;
    private JButton btnThemMoi;
    private JTextField txtTimKiem;
    private JLabel lblCounter;
    private JTable contentTable;
    private DefaultTableModel tableModel;

    public QuanTriDanhMucPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initUI();
        selectMenu(0);
    }

    private void initUI() {
        // 1. LEFT SIDEBAR: MENU QUẢN TRỊ
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(230, 0));
        sidebarPanel.setBackground(new Color(248, 250, 252));
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)));

        JPanel pnlMenuTop = new JPanel(new GridLayout(6, 1, 0, 4));
        pnlMenuTop.setOpaque(false);
        pnlMenuTop.setBorder(new EmptyBorder(16, 12, 12, 12));

        JLabel lblMenuHead = new JLabel("MENU QUẢN TRỊ");
        lblMenuHead.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMenuHead.setForeground(new Color(148, 163, 184));
        pnlMenuTop.add(lblMenuHead);

        String[] menuTitles = {
            "🏨 Quản Lý Phòng & Giá",
            "👤 Hồ Sơ Khách Hàng",
            "👥 Nhân Sự & Phân Quyền",
            "🍹 Danh Mục Dịch Vụ",
            "📈 Báo Cáo & Thống Kê"
        };

        for (int i = 0; i < 5; i++) {
            final int idx = i;
            JButton btn = new JButton(menuTitles[i]);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(new EmptyBorder(10, 12, 10, 12));
            btn.setBackground(new Color(248, 250, 252));
            btn.setForeground(new Color(71, 85, 105));
            btn.addActionListener(e -> selectMenu(idx));
            menuButtons[i] = btn;
            pnlMenuTop.add(btn);
        }

        sidebarPanel.add(pnlMenuTop, BorderLayout.NORTH);

        // Sidebar bottom: Database status badge
        JPanel pnlDbBadge = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlDbBadge.setOpaque(false);
        JLabel lblDb = new JLabel("● Hệ Thống MySQL (Connected)");
        lblDb.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblDb.setForeground(new Color(16, 185, 129));
        pnlDbBadge.add(lblDb);
        sidebarPanel.add(pnlDbBadge, BorderLayout.SOUTH);

        add(sidebarPanel, BorderLayout.WEST);

        // 2. RIGHT CONTENT AREA
        JPanel contentContainer = new JPanel(new BorderLayout(0, 14));
        contentContainer.setBackground(Color.WHITE);
        contentContainer.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Right Header
        rightHeader = new JPanel(new BorderLayout());
        rightHeader.setOpaque(false);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setOpaque(false);
        lblContentTitle = new JLabel("Quản Lý Danh Mục Phòng & Cấu Hình Giá");
        lblContentTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblContentTitle.setForeground(new Color(15, 23, 42));

        lblContentSub = new JLabel("Thêm mới, chỉnh sửa thông tin phòng vật lý và thiết lập loại phòng.");
        lblContentSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblContentSub.setForeground(new Color(100, 116, 139));

        pnlTitle.add(lblContentTitle);
        pnlTitle.add(lblContentSub);
        rightHeader.add(pnlTitle, BorderLayout.WEST);

        btnThemMoi = new JButton("+ Thêm Phòng Mới");
        btnThemMoi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnThemMoi.setBackground(new Color(79, 70, 229));
        btnThemMoi.setForeground(Color.WHITE);
        btnThemMoi.setFocusPainted(false);
        btnThemMoi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThemMoi.setBorder(new EmptyBorder(8, 16, 8, 16));
        btnThemMoi.addActionListener(e -> xuLyThemMoi());
        rightHeader.add(btnThemMoi, BorderLayout.EAST);

        contentContainer.add(rightHeader, BorderLayout.NORTH);

        // Table & Search Filter Wrap
        JPanel pnlTableWrap = new JPanel(new BorderLayout(0, 10));
        pnlTableWrap.setBackground(Color.WHITE);

        // Search & Counter bar
        JPanel pnlSearch = new JPanel(new BorderLayout());
        pnlSearch.setBackground(new Color(248, 250, 252));
        pnlSearch.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JPanel pnlSearchInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlSearchInput.setOpaque(false);
        JLabel lblSearchIcon = new JLabel("🔍");
        txtTimKiem = new JTextField(22);
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtTimKiem.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        txtTimKiem.putClientProperty("JTextField.placeholderText", "Tìm kiếm...");
        pnlSearchInput.add(lblSearchIcon);
        pnlSearchInput.add(txtTimKiem);
        pnlSearch.add(pnlSearchInput, BorderLayout.WEST);

        lblCounter = new JLabel("Hiển thị 0 trên tổng số 0");
        lblCounter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCounter.setForeground(new Color(100, 116, 139));
        pnlSearch.add(lblCounter, BorderLayout.EAST);

        txtTimKiem.addActionListener(e -> taiDuLieuTheoMenu());

        pnlTableWrap.add(pnlSearch, BorderLayout.NORTH);

        // Modern Table
        contentTable = new JTable();
        contentTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        contentTable.setRowHeight(36);
        contentTable.setSelectionBackground(new Color(238, 242, 255));
        contentTable.setSelectionForeground(new Color(30, 41, 59));
        contentTable.setGridColor(new Color(241, 245, 249));
        contentTable.setShowVerticalLines(false);
        contentTable.setShowHorizontalLines(true);
        contentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        contentTable.getTableHeader().setBackground(new Color(248, 250, 252));
        contentTable.getTableHeader().setForeground(new Color(71, 85, 105));
        contentTable.getTableHeader().setPreferredSize(new Dimension(0, 36));

        // Click on row to edit / delete
        contentTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    xuLySuaDong();
                }
            }
        });

        JScrollPane sp = new JScrollPane(contentTable);
        sp.setBorder(new LineBorder(new Color(226, 232, 240), 1, true));
        sp.setBackground(Color.WHITE);
        pnlTableWrap.add(sp, BorderLayout.CENTER);

        // Action Toolbar below table (Sửa, Xóa)
        JPanel pnlBottomActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pnlBottomActions.setOpaque(false);

        JButton btnSua = new JButton("✏️ Sửa dòng chọn");
        btnSua.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSua.setBackground(new Color(241, 245, 249));
        btnSua.setForeground(new Color(30, 41, 59));
        btnSua.setFocusPainted(false);
        btnSua.addActionListener(e -> xuLySuaDong());

        JButton btnXoa = new JButton("🗑️ Xóa dòng chọn");
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXoa.setBackground(new Color(254, 242, 242));
        btnXoa.setForeground(new Color(220, 38, 38));
        btnXoa.setFocusPainted(false);
        btnXoa.addActionListener(e -> xuLyXoaDong());

        pnlBottomActions.add(btnSua);
        pnlBottomActions.add(btnXoa);
        pnlTableWrap.add(pnlBottomActions, BorderLayout.SOUTH);

        contentContainer.add(pnlTableWrap, BorderLayout.CENTER);
        add(contentContainer, BorderLayout.CENTER);
    }

    public void selectMenu(int index) {
        currentMenuIndex = index;
        txtTimKiem.setText("");

        for (int i = 0; i < 5; i++) {
            if (i == index) {
                menuButtons[i].setBackground(new Color(238, 242, 255));
                menuButtons[i].setForeground(new Color(79, 70, 229));
            } else {
                menuButtons[i].setBackground(new Color(248, 250, 252));
                menuButtons[i].setForeground(new Color(71, 85, 105));
            }
        }

        switch (index) {
            case 0:
                lblContentTitle.setText("Quản Lý Danh Mục Phòng & Cấu Hình Giá");
                lblContentSub.setText("Thêm mới, chỉnh sửa thông tin phòng vật lý và thiết lập loại phòng.");
                btnThemMoi.setText("+ Thêm Phòng Mới");
                break;
            case 1:
                lblContentTitle.setText("Hồ Sơ Danh Bạ Khách Hàng");
                lblContentSub.setText("Quản lý thông tin định danh CCCD, Hộ chiếu, số điện thoại và lịch sử lưu trú.");
                btnThemMoi.setText("+ Thêm Khách Hàng");
                break;
            case 2:
                lblContentTitle.setText("Nhân Sự & Phân Quyền Tài Khoản");
                lblContentSub.setText("Quản lý danh sách nhân viên, vai trò (Quản lý, Lễ tân, Kế toán) và khóa tài khoản.");
                btnThemMoi.setText("+ Thêm Nhân Viên");
                break;
            case 3:
                lblContentTitle.setText("Danh Mục Dịch Vụ & Minibar");
                lblContentSub.setText("Cấu hình danh mục dịch vụ, đồ uống minibar và bảng giá niêm yết.");
                btnThemMoi.setText("+ Thêm Dịch Vụ");
                break;
            case 4:
                lblContentTitle.setText("Báo Cáo Doanh Thu & Lịch Sử Hóa Đơn");
                lblContentSub.setText("Tổng hợp doanh thu theo tháng, thống kê hóa đơn thanh toán và công suất.");
                btnThemMoi.setText("📊 Xuất Thống Kê");
                break;
        }

        taiDuLieuTheoMenu();
    }

    private void taiDuLieuTheoMenu() {
        String kw = txtTimKiem.getText().trim();
        try {
            switch (currentMenuIndex) {
                case 0: { // Phòng & Giá
                    String sql = "SELECT p.MaPhong AS 'MÃ PHÒNG', p.SoPhong AS 'SỐ PHÒNG', " +
                                 "CONCAT('Tầng ', p.Tang) AS 'TẦNG', lp.TenLoaiPhong AS 'LOẠI PHÒNG', " +
                                 "p.TrangThai AS 'TRẠNG THÁI' " +
                                 "FROM Phong p JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong " +
                                 (kw.isEmpty() ? "" : "WHERE p.SoPhong LIKE '%" + kw + "%' OR p.MaPhong LIKE '%" + kw + "%' ") +
                                 "ORDER BY p.Tang, p.SoPhong";
                    contentTable.setModel(DatabaseHelper.executeQuery(sql));
                    break;
                }
                case 1: { // Khách Hàng
                    String sql = "SELECT MaKhachHang AS 'MÃ KH', HoTen AS 'HỌ VÀ TÊN', LoaiGiayTo AS 'LOẠI GIẤY TỜ', " +
                                 "SoGiayTo AS 'SỐ ĐỊNH DANH', SoDienThoai AS 'SỐ ĐIỆN THOẠI', QuocTich AS 'QUỐC TỊCH', DiaChi AS 'ĐỊA CHỈ' " +
                                 "FROM KhachHang " +
                                 (kw.isEmpty() ? "" : "WHERE HoTen LIKE '%" + kw + "%' OR SoDienThoai LIKE '%" + kw + "%' OR SoGiayTo LIKE '%" + kw + "%' ") +
                                 "ORDER BY MaKhachHang";
                    contentTable.setModel(DatabaseHelper.executeQuery(sql));
                    break;
                }
                case 2: { // Nhân Viên
                    String sql = "SELECT nv.MaNhanVien AS 'MÃ NV', nv.HoTen AS 'HỌ TÊN', vt.TenVaiTro AS 'VAI TRÒ', " +
                                 "nv.SoDienThoai AS 'SỐ ĐT', nv.Email AS 'EMAIL', nv.TenDangNhap AS 'TÀI KHOẢN', nv.TrangThai AS 'TRẠNG THÁI' " +
                                 "FROM NhanVien nv JOIN VaiTro vt ON nv.MaVaiTro = vt.MaVaiTro " +
                                 (kw.isEmpty() ? "" : "WHERE nv.HoTen LIKE '%" + kw + "%' OR nv.TenDangNhap LIKE '%" + kw + "%' ") +
                                 "ORDER BY nv.MaNhanVien";
                    contentTable.setModel(DatabaseHelper.executeQuery(sql));
                    break;
                }
                case 3: { // Dịch Vụ
                    String sql = "SELECT MaDichVu AS 'MÃ DV', TenDichVu AS 'TÊN DỊCH VỤ', " +
                                 "DonGia AS 'ĐƠN GIÁ (VNĐ)', DonViTinh AS 'ĐƠN VỊ TÍNH' FROM DichVu " +
                                 (kw.isEmpty() ? "" : "WHERE TenDichVu LIKE '%" + kw + "%' OR MaDichVu LIKE '%" + kw + "%' ") +
                                 "ORDER BY MaDichVu";
                    contentTable.setModel(DatabaseHelper.executeQuery(sql));
                    break;
                }
                case 4: { // Báo Cáo
                    String sql = "SELECT hd.MaHoaDon AS 'MÃ HĐ', dp.MaDatPhong AS 'MÃ ĐẶT', kh.HoTen AS 'KHÁCH HÀNG', " +
                                 "hd.NgayLap AS 'NGÀY LẬP', hd.TienPhong AS 'TIỀN PHÒNG', hd.TienDichVu AS 'TIỀN DỊCH VỤ', " +
                                 "hd.TongThanhToan AS 'TỔNG CỘNG', hd.TrangThai AS 'TRẠNG THÁI' " +
                                 "FROM HoaDon hd " +
                                 "JOIN DatPhong dp ON hd.MaDatPhong = dp.MaDatPhong " +
                                 "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                                 "ORDER BY hd.NgayLap DESC";
                    contentTable.setModel(DatabaseHelper.executeQuery(sql));
                    break;
                }
            }

            int count = contentTable.getRowCount();
            lblCounter.setText("Hiển thị " + count + " dòng dữ liệu");

            // Custom renderer for Status Badge
            contentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                    Component c = super.getTableCellRendererComponent(table, val, isSel, hasFocus, row, col);
                    if (!isSel) {
                        String str = val != null ? val.toString() : "";
                        if ("Trống".equals(str)) {
                            c.setForeground(new Color(22, 163, 74));
                        } else if ("Đang ở".equals(str)) {
                            c.setForeground(new Color(220, 38, 38));
                        } else if ("Đang làm việc".equals(str)) {
                            c.setForeground(new Color(22, 163, 74));
                        } else if ("Đã nghỉ".equals(str)) {
                            c.setForeground(new Color(148, 163, 184));
                        } else {
                            c.setForeground(new Color(30, 41, 59));
                        }
                    }
                    return c;
                }
            });

        } catch (Exception ex) {
            System.err.println("Lỗi nạp bảng quản trị: " + ex.getMessage());
        }
    }

    // Modal Popup Thêm Mới
    private void xuLyThemMoi() {
        switch (currentMenuIndex) {
            case 0: moPopupThemPhong(); break;
            case 1: moPopupThemKhachHang(); break;
            case 2: moPopupThemNhanVien(); break;
            case 3: moPopupThemDichVu(); break;
            case 4: taiDuLieuTheoMenu(); break;
        }
    }

    // Modal Popup Sửa Dòng
    private void xuLySuaDong() {
        int r = contentTable.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng từ bảng để chỉnh sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = contentTable.getValueAt(r, 0).toString();

        switch (currentMenuIndex) {
            case 0: moPopupSuaPhong(id); break;
            case 1: moPopupSuaKhachHang(id); break;
            case 2: moPopupSuaNhanVien(id); break;
            case 3: moPopupSuaDichVu(id); break;
            default: break;
        }
    }

    // Modal Popup Xóa Dòng
    private void xuLyXoaDong() {
        int r = contentTable.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = contentTable.getValueAt(r, 0).toString();

        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa bản ghi [" + id + "]?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                switch (currentMenuIndex) {
                    case 0: DatabaseHelper.executeUpdate("DELETE FROM Phong WHERE MaPhong=?", id); break;
                    case 1: DatabaseHelper.executeUpdate("DELETE FROM KhachHang WHERE MaKhachHang=?", id); break;
                    case 2: DatabaseHelper.executeUpdate("DELETE FROM NhanVien WHERE MaNhanVien=?", id); break;
                    case 3: DatabaseHelper.executeUpdate("DELETE FROM DichVu WHERE MaDichVu=?", id); break;
                }
                JOptionPane.showMessageDialog(this, "Đã xóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuTheoMenu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Không thể xóa do ràng buộc dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Popup: Thêm Phòng
    private void moPopupThemPhong() {
        JTextField txtMa = new JTextField("P" + (System.currentTimeMillis() % 1000), 10);
        JTextField txtSo = new JTextField("105", 10);
        JTextField txtTang = new JTextField("1", 4);
        JComboBox<String> cboLP = new JComboBox<>(new String[]{"LP01", "LP02", "LP03", "LP04"});
        JComboBox<String> cboTT = new JComboBox<>(new String[]{"Trống", "Đang ở", "Đang dọn", "Bảo trì"});

        JPanel pnl = new JPanel(new GridLayout(5, 2, 8, 8));
        pnl.add(new JLabel("Mã phòng:")); pnl.add(txtMa);
        pnl.add(new JLabel("Số phòng:")); pnl.add(txtSo);
        pnl.add(new JLabel("Tầng:")); pnl.add(txtTang);
        pnl.add(new JLabel("Mã loại phòng:")); pnl.add(cboLP);
        pnl.add(new JLabel("Trạng thái:")); pnl.add(cboTT);

        int res = JOptionPane.showConfirmDialog(this, pnl, "THÊM PHÒNG THỰC TẾ MỚI", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                DatabaseHelper.executeUpdate("INSERT INTO Phong (MaPhong, SoPhong, Tang, MaLoaiPhong, TrangThai) VALUES (?, ?, ?, ?, ?)",
                        txtMa.getText().trim(), txtSo.getText().trim(), Integer.parseInt(txtTang.getText().trim()), cboLP.getSelectedItem(), cboTT.getSelectedItem());
                JOptionPane.showMessageDialog(this, "Thêm phòng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuTheoMenu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void moPopupSuaPhong(String maPhong) {
        JTextField txtSo = new JTextField("101", 10);
        JTextField txtTang = new JTextField("1", 4);
        JComboBox<String> cboTT = new JComboBox<>(new String[]{"Trống", "Đang ở", "Đang dọn", "Bảo trì"});

        JPanel pnl = new JPanel(new GridLayout(3, 2, 8, 8));
        pnl.add(new JLabel("Số phòng mới:")); pnl.add(txtSo);
        pnl.add(new JLabel("Tầng:")); pnl.add(txtTang);
        pnl.add(new JLabel("Trạng thái:")); pnl.add(cboTT);

        int res = JOptionPane.showConfirmDialog(this, pnl, "CHỈNH SỬA PHÒNG [" + maPhong + "]", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                DatabaseHelper.executeUpdate("UPDATE Phong SET SoPhong=?, Tang=?, TrangThai=? WHERE MaPhong=?",
                        txtSo.getText().trim(), Integer.parseInt(txtTang.getText().trim()), cboTT.getSelectedItem(), maPhong);
                taiDuLieuTheoMenu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Popup: Thêm Khách Hàng (Validation CCCD 12 số, SĐT 10-11 số)
    private void moPopupThemKhachHang() {
        JTextField txtMa = new JTextField("KH" + (System.currentTimeMillis() % 1000), 10);
        JTextField txtTen = new JTextField(15);
        JComboBox<String> cboLoai = new JComboBox<>(new String[]{"CCCD", "PASSPORT"});
        JTextField txtSoGT = new JTextField(12);
        JTextField txtSDT = new JTextField(12);
        JTextField txtEmail = new JTextField(15);
        JTextField txtDiaChi = new JTextField(20);

        JPanel pnl = new JPanel(new GridLayout(7, 2, 8, 8));
        pnl.add(new JLabel("Mã khách hàng (*):")); pnl.add(txtMa);
        pnl.add(new JLabel("Họ và tên (*):")); pnl.add(txtTen);
        pnl.add(new JLabel("Loại giấy tờ:")); pnl.add(cboLoai);
        pnl.add(new JLabel("Số định danh (*):")); pnl.add(txtSoGT);
        pnl.add(new JLabel("Số điện thoại (*):")); pnl.add(txtSDT);
        pnl.add(new JLabel("Email:")); pnl.add(txtEmail);
        pnl.add(new JLabel("Địa chỉ:")); pnl.add(txtDiaChi);

        int res = JOptionPane.showConfirmDialog(this, pnl, "THÊM HỒ SƠ KHÁCH HÀNG MỚI", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            String soGT = txtSoGT.getText().trim();
            String sdt = txtSDT.getText().trim();
            String loai = (String) cboLoai.getSelectedItem();

            // Validate CCCD
            if ("CCCD".equals(loai) && !soGT.matches("^\\d{12}$")) {
                JOptionPane.showMessageDialog(this, "Số CCCD phải có ĐÚNG 12 chữ số!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Validate SĐT
            if (!sdt.matches("^0\\d{9,10}$")) {
                JOptionPane.showMessageDialog(this, "Số điện thoại phải có 10 hoặc 11 số và bắt đầu bằng số 0!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                DatabaseHelper.executeUpdate(
                        "INSERT INTO KhachHang (MaKhachHang, HoTen, LoaiGiayTo, SoGiayTo, SoDienThoai, Email, DiaChi) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                        txtMa.getText().trim(), txtTen.getText().trim(), loai, soGT, sdt, txtEmail.getText().trim(), txtDiaChi.getText().trim());
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuTheoMenu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void moPopupSuaKhachHang(String maKH) {
        JTextField txtTen = new JTextField(15);
        JTextField txtSDT = new JTextField(12);
        JTextField txtEmail = new JTextField(15);
        JTextField txtDiaChi = new JTextField(20);

        JPanel pnl = new JPanel(new GridLayout(4, 2, 8, 8));
        pnl.add(new JLabel("Họ và tên:")); pnl.add(txtTen);
        pnl.add(new JLabel("Số điện thoại:")); pnl.add(txtSDT);
        pnl.add(new JLabel("Email:")); pnl.add(txtEmail);
        pnl.add(new JLabel("Địa chỉ:")); pnl.add(txtDiaChi);

        int res = JOptionPane.showConfirmDialog(this, pnl, "CẬP NHẬT KHÁCH HÀNG [" + maKH + "]", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                DatabaseHelper.executeUpdate("UPDATE KhachHang SET HoTen=?, SoDienThoai=?, Email=?, DiaChi=? WHERE MaKhachHang=?",
                        txtTen.getText().trim(), txtSDT.getText().trim(), txtEmail.getText().trim(), txtDiaChi.getText().trim(), maKH);
                taiDuLieuTheoMenu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Popup: Thêm Nhân Viên
    private void moPopupThemNhanVien() {
        JTextField txtMa = new JTextField("NV" + (System.currentTimeMillis() % 1000), 10);
        JTextField txtTen = new JTextField(15);
        JTextField txtUser = new JTextField(12);
        JTextField txtPass = new JTextField("123456", 12);
        JComboBox<Integer> cboVT = new JComboBox<>(new Integer[]{1, 2, 3});
        JTextField txtSDT = new JTextField(12);

        JPanel pnl = new JPanel(new GridLayout(6, 2, 8, 8));
        pnl.add(new JLabel("Mã nhân viên:")); pnl.add(txtMa);
        pnl.add(new JLabel("Họ và tên:")); pnl.add(txtTen);
        pnl.add(new JLabel("Tài khoản:")); pnl.add(txtUser);
        pnl.add(new JLabel("Mật khẩu:")); pnl.add(txtPass);
        pnl.add(new JLabel("Mã vai trò (1:QL, 2:Lễ tân, 3:Kế toán):")); pnl.add(cboVT);
        pnl.add(new JLabel("Số điện thoại:")); pnl.add(txtSDT);

        int res = JOptionPane.showConfirmDialog(this, pnl, "THÊM TÀI KHOẢN NHÂN VIÊN MỚI", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                DatabaseHelper.executeUpdate(
                        "INSERT INTO NhanVien (MaNhanVien, HoTen, TenDangNhap, MatKhauMaHoa, MaVaiTro, SoDienThoai, TrangThai) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 'Đang làm việc')",
                        txtMa.getText().trim(), txtTen.getText().trim(), txtUser.getText().trim(), txtPass.getText().trim(), cboVT.getSelectedItem(), txtSDT.getText().trim());
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuTheoMenu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void moPopupSuaNhanVien(String maNV) {
        JComboBox<String> cboTT = new JComboBox<>(new String[]{"Đang làm việc", "Đã nghỉ"});
        JPanel pnl = new JPanel(new GridLayout(1, 2, 8, 8));
        pnl.add(new JLabel("Trạng thái tài khoản:")); pnl.add(cboTT);

        int res = JOptionPane.showConfirmDialog(this, pnl, "KHÓA / MỞ KHÓA TÀI KHOẢN [" + maNV + "]", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                DatabaseHelper.executeUpdate("UPDATE NhanVien SET TrangThai=? WHERE MaNhanVien=?", cboTT.getSelectedItem(), maNV);
                taiDuLieuTheoMenu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Popup: Thêm Dịch Vụ
    private void moPopupThemDichVu() {
        JTextField txtMa = new JTextField("DV" + (System.currentTimeMillis() % 100), 10);
        JTextField txtTen = new JTextField(15);
        JTextField txtGia = new JTextField("50000", 10);
        JTextField txtDVT = new JTextField("Lượt", 8);

        JPanel pnl = new JPanel(new GridLayout(4, 2, 8, 8));
        pnl.add(new JLabel("Mã dịch vụ:")); pnl.add(txtMa);
        pnl.add(new JLabel("Tên dịch vụ:")); pnl.add(txtTen);
        pnl.add(new JLabel("Đơn giá niêm yết:")); pnl.add(txtGia);
        pnl.add(new JLabel("Đơn vị tính:")); pnl.add(txtDVT);

        int res = JOptionPane.showConfirmDialog(this, pnl, "THÊM DỊCH VỤ NIÊM YẾT MỚI", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                DatabaseHelper.executeUpdate("INSERT INTO DichVu (MaDichVu, TenDichVu, DonGia, DonViTinh) VALUES (?, ?, ?, ?)",
                        txtMa.getText().trim(), txtTen.getText().trim(), Double.parseDouble(txtGia.getText().trim()), txtDVT.getText().trim());
                taiDuLieuTheoMenu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void moPopupSuaDichVu(String maDV) {
        JTextField txtTen = new JTextField(15);
        JTextField txtGia = new JTextField(10);
        JTextField txtDVT = new JTextField(8);

        JPanel pnl = new JPanel(new GridLayout(3, 2, 8, 8));
        pnl.add(new JLabel("Tên dịch vụ:")); pnl.add(txtTen);
        pnl.add(new JLabel("Đơn giá mới:")); pnl.add(txtGia);
        pnl.add(new JLabel("Đơn vị tính:")); pnl.add(txtDVT);

        int res = JOptionPane.showConfirmDialog(this, pnl, "SỬA DỊCH VỤ [" + maDV + "]", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            try {
                DatabaseHelper.executeUpdate("UPDATE DichVu SET TenDichVu=?, DonGia=?, DonViTinh=? WHERE MaDichVu=?",
                        txtTen.getText().trim(), Double.parseDouble(txtGia.getText().trim()), txtDVT.getText().trim(), maDV);
                taiDuLieuTheoMenu();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

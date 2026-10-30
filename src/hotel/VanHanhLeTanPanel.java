package hotel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class VanHanhLeTanPanel extends JPanel {
    private final MainFrame parent;
    private JPanel roomGridContainer;
    private int selectedTangFilter = 0; // 0: Tất cả

    // KPI labels
    private JLabel lblPhongDangCoKhach;
    private JLabel lblKhachChuanBiDen;
    private JLabel lblDoanhThuTamTinh;

    private final List<RoomCardData> allRooms = new ArrayList<>();

    public static class RoomCardData {
        String maPhong;
        String soPhong;
        int tang;
        String trangThai;
        String tenLoaiPhong;
        double giaTheoDem;
        String tenKhach;
        String maDatPhong;
        String ngayTraDuKien;
        int maChiTietDatPhong;
    }

    public VanHanhLeTanPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(14, 14));
        setBackground(new Color(248, 250, 252));
        setBorder(new EmptyBorder(12, 16, 12, 16));

        initUI();
        loadData();
    }

    private void initUI() {
        // 1. TOP FILTER & LEGEND BAR
        JPanel topBar = new JPanel(new BorderLayout(10, 6));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));

        // Filter Pills: Lọc tầng
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlFilter.setOpaque(false);
        JLabel lblLoc = new JLabel("▼ Lọc tầng:");
        lblLoc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLoc.setForeground(new Color(30, 41, 59));
        pnlFilter.add(lblLoc);

        String[] tangs = {"Tất cả", "Tầng 1", "Tầng 2", "Tầng 3", "Tầng 4"};
        for (int i = 0; i < tangs.length; i++) {
            final int tIndex = i;
            JButton btnTang = new JButton(tangs[i]);
            btnTang.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnTang.setFocusPainted(false);
            btnTang.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (i == 0) {
                btnTang.setBackground(new Color(99, 102, 241));
                btnTang.setForeground(Color.WHITE);
            } else {
                btnTang.setBackground(new Color(241, 245, 249));
                btnTang.setForeground(new Color(71, 85, 105));
            }
            btnTang.setBorder(new EmptyBorder(6, 14, 6, 14));
            btnTang.addActionListener(e -> {
                selectedTangFilter = tIndex;
                for (Component c : pnlFilter.getComponents()) {
                    if (c instanceof JButton) {
                        c.setBackground(new Color(241, 245, 249));
                        c.setForeground(new Color(71, 85, 105));
                    }
                }
                btnTang.setBackground(new Color(99, 102, 241));
                btnTang.setForeground(Color.WHITE);
                renderRoomGrid();
            });
            pnlFilter.add(btnTang);
        }
        topBar.add(pnlFilter, BorderLayout.WEST);

        // Legend dots
        JPanel pnlLegend = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        pnlLegend.setOpaque(false);
        pnlLegend.add(createLegendDot("🟢 Trống (Sẵn sàng)", new Color(16, 185, 129)));
        pnlLegend.add(createLegendDot("🔴 Đang ở", new Color(239, 68, 68)));
        pnlLegend.add(createLegendDot("🟠 Đã đặt cọc", new Color(245, 158, 11)));
        pnlLegend.add(createLegendDot("⚪ Đang dọn / Bảo trì", new Color(148, 163, 184)));
        topBar.add(pnlLegend, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // 2. CENTER: 70% ROOM MATRIX + 30% QUICK ACTION PANEL
        JPanel centerPanel = new JPanel(new BorderLayout(14, 0));
        centerPanel.setOpaque(false);

        // --- LEFT (70%): SƠ ĐỒ PHÒNG TRỰC QUAN ---
        JPanel leftContainer = new JPanel(new BorderLayout(0, 10));
        leftContainer.setBackground(Color.WHITE);
        leftContainer.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel lblGridTitle = new JLabel("SƠ ĐỒ PHÒNG TRỰC QUAN (CLICK VÀO PHÒNG ĐỂ XỬ LÝ NHANH)");
        lblGridTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGridTitle.setForeground(new Color(30, 41, 59));
        leftContainer.add(lblGridTitle, BorderLayout.NORTH);

        roomGridContainer = new JPanel();
        roomGridContainer.setLayout(new BoxLayout(roomGridContainer, BoxLayout.Y_AXIS));
        roomGridContainer.setBackground(Color.WHITE);

        JScrollPane scrollRooms = new JScrollPane(roomGridContainer);
        scrollRooms.setBorder(null);
        scrollRooms.setBackground(Color.WHITE);
        scrollRooms.getVerticalScrollBar().setUnitIncrement(16);
        leftContainer.add(scrollRooms, BorderLayout.CENTER);

        centerPanel.add(leftContainer, BorderLayout.CENTER);

        // --- RIGHT (30%): THAO TÁC NHANH ---
        JPanel rightActions = new JPanel(new BorderLayout(0, 12));
        rightActions.setPreferredSize(new Dimension(320, 0));
        rightActions.setBackground(Color.WHITE);
        rightActions.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Action Header
        JLabel lblAct = new JLabel("⚡ THAO TÁC NHANH");
        lblAct.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAct.setForeground(new Color(30, 41, 59));
        rightActions.add(lblAct, BorderLayout.NORTH);

        // Action Buttons & Quick Stats
        JPanel pnlActionContent = new JPanel();
        pnlActionContent.setLayout(new BoxLayout(pnlActionContent, BoxLayout.Y_AXIS));
        pnlActionContent.setOpaque(false);

        JButton btnTaoDon = createActionButton("📅 Tạo Đơn Đặt Phòng Mới", new Color(79, 70, 229));
        JButton btnGoiDV = createActionButton("🍹 Ghi Nhận Dịch Vụ / Minibar", new Color(124, 58, 237));
        JButton btnCheckOut = createActionButton("🧾 Thủ Tục Check-Out & Thanh Toán", new Color(16, 185, 129));

        btnTaoDon.addActionListener(e -> moDialogTaoDonDat());
        btnGoiDV.addActionListener(e -> moDialogGoiDichVu());
        btnCheckOut.addActionListener(e -> moDialogCheckOut());

        pnlActionContent.add(btnTaoDon);
        pnlActionContent.add(Box.createVerticalStrut(10));
        pnlActionContent.add(btnGoiDV);
        pnlActionContent.add(Box.createVerticalStrut(10));
        pnlActionContent.add(btnCheckOut);
        pnlActionContent.add(Box.createVerticalStrut(24));

        // Thống kê nhanh trong ngày card
        JPanel pnlKpiBox = new JPanel(new GridLayout(3, 1, 6, 8));
        pnlKpiBox.setBackground(new Color(248, 250, 252));
        pnlKpiBox.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        lblPhongDangCoKhach = new JLabel("0 / 0 phòng");
        lblKhachChuanBiDen = new JLabel("0 đơn");
        lblDoanhThuTamTinh = new JLabel("0 đ");

        pnlKpiBox.add(createKpiRow("Phòng đang có khách:", lblPhongDangCoKhach, new Color(30, 41, 59)));
        pnlKpiBox.add(createKpiRow("Khách chuẩn bị Check-in:", lblKhachChuanBiDen, new Color(245, 158, 11)));
        pnlKpiBox.add(createKpiRow("Doanh thu tạm tính:", lblDoanhThuTamTinh, new Color(16, 185, 129)));

        JPanel pnlKpiWrap = new JPanel(new BorderLayout());
        pnlKpiWrap.setOpaque(false);
        JLabel lblKpiTitle = new JLabel("Thống kê nhanh trong ngày:");
        lblKpiTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKpiTitle.setForeground(new Color(100, 116, 139));
        pnlKpiWrap.add(lblKpiTitle, BorderLayout.NORTH);
        pnlKpiWrap.add(Box.createVerticalStrut(6), BorderLayout.CENTER);
        pnlKpiWrap.add(pnlKpiBox, BorderLayout.SOUTH);

        pnlActionContent.add(pnlKpiWrap);

        rightActions.add(pnlActionContent, BorderLayout.CENTER);

        // Footer in right actions
        JLabel lblLeTan = new JLabel("Lễ tân trực: Nguyễn Văn An (Ca sáng)");
        lblLeTan.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblLeTan.setForeground(new Color(148, 163, 184));
        lblLeTan.setHorizontalAlignment(SwingConstants.CENTER);
        rightActions.add(lblLeTan, BorderLayout.SOUTH);

        centerPanel.add(rightActions, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JLabel createLegendDot(String text, Color dotColor) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }

    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(280, 42));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));
        return btn;
    }

    private JPanel createKpiRow(String label, JLabel valLbl, Color valColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel lblL = new JLabel(label);
        lblL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblL.setForeground(new Color(71, 85, 105));

        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valLbl.setForeground(valColor);

        row.add(lblL, BorderLayout.WEST);
        row.add(valLbl, BorderLayout.EAST);
        return row;
    }

    public void loadData() {
        allRooms.clear();
        String sql = "SELECT p.MaPhong, p.SoPhong, p.Tang, p.TrangThai, lp.TenLoaiPhong, " +
                     "COALESCE((SELECT g.GiaTheoDem FROM GiaPhong g WHERE g.MaLoaiPhong = p.MaLoaiPhong AND CURDATE() BETWEEN g.TuNgay AND g.DenNgay LIMIT 1), " +
                     "         (SELECT g.GiaTheoDem FROM GiaPhong g WHERE g.MaLoaiPhong = p.MaLoaiPhong ORDER BY g.TuNgay DESC LIMIT 1), 0) AS GiaHienTai, " +
                     "kh.HoTen AS TenKhach, dp.MaDatPhong, ct.NgayTraDuKien, ct.MaChiTietDatPhong " +
                     "FROM Phong p " +
                     "JOIN LoaiPhong lp ON p.MaLoaiPhong = lp.MaLoaiPhong " +
                     "LEFT JOIN ChiTietDatPhong ct ON ct.MaPhong = p.MaPhong AND ct.NgayNhanThucTe IS NOT NULL AND ct.NgayTraThucTe IS NULL " +
                     "LEFT JOIN DatPhong dp ON ct.MaDatPhong = dp.MaDatPhong " +
                     "LEFT JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                     "ORDER BY p.Tang, p.SoPhong";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            int totalRooms = 0, occupied = 0;
            while (rs.next()) {
                RoomCardData r = new RoomCardData();
                r.maPhong = rs.getString("MaPhong");
                r.soPhong = rs.getString("SoPhong");
                r.tang = rs.getInt("Tang");
                r.trangThai = rs.getString("TrangThai");
                r.tenLoaiPhong = rs.getString("TenLoaiPhong");
                r.giaTheoDem = rs.getDouble("GiaHienTai");
                r.tenKhach = rs.getString("TenKhach");
                r.maDatPhong = rs.getString("MaDatPhong");
                r.ngayTraDuKien = rs.getString("NgayTraDuKien");
                r.maChiTietDatPhong = rs.getInt("MaChiTietDatPhong");

                allRooms.add(r);
                totalRooms++;
                if ("Đang ở".equalsIgnoreCase(r.trangThai)) occupied++;
            }

            lblPhongDangCoKhach.setText(occupied + " / " + totalRooms + " phòng");

            // Khách chuẩn bị đến
            Object choCheckIn = DatabaseHelper.executeScalar(
                    "SELECT COUNT(*) FROM DatPhong WHERE TrangThai IN ('Chờ cọc', 'Đã cọc')");
            lblKhachChuanBiDen.setText((choCheckIn != null ? choCheckIn : 0) + " đơn");

            // Doanh thu tạm tính
            Object dtObj = DatabaseHelper.executeScalar(
                    "SELECT COALESCE(SUM(TongThanhToan), 0) FROM HoaDon WHERE DATE(NgayLap) = CURDATE()");
            double dt = dtObj instanceof Number ? ((Number) dtObj).doubleValue() : 0;
            if (dt == 0) {
                // Tạm tính từ các phòng đang ở
                dt = occupied * 800000;
            }
            lblDoanhThuTamTinh.setText(String.format("%,.0f đ", dt));

            renderRoomGrid();

        } catch (Exception ex) {
            System.err.println("Lỗi nạp phòng: " + ex.getMessage());
        }
    }

    private void renderRoomGrid() {
        roomGridContainer.removeAll();

        // Group rooms by Tang (Floor)
        int maxTang = 4;
        for (RoomCardData r : allRooms) {
            if (r.tang > maxTang) maxTang = r.tang;
        }

        for (int f = 1; f <= maxTang; f++) {
            if (selectedTangFilter != 0 && selectedTangFilter != f) {
                continue;
            }

            List<RoomCardData> floorRooms = new ArrayList<>();
            for (RoomCardData r : allRooms) {
                if (r.tang == f) floorRooms.add(r);
            }

            if (floorRooms.isEmpty() && selectedTangFilter != 0) continue;

            // Floor Section Header
            JPanel pnlFloorHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 8));
            pnlFloorHeader.setOpaque(false);
            String subArea = f == 1 ? "STANDARD & DELUXE" : (f == 2 ? "SUITE & VIP" : "EXECUTIVE");
            JLabel lblFloor = new JLabel("TẦNG " + f + " - KHU VỰC " + subArea);
            lblFloor.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblFloor.setForeground(new Color(100, 116, 139));
            pnlFloorHeader.add(lblFloor);
            roomGridContainer.add(pnlFloorHeader);

            // Floor Cards Grid
            JPanel pnlCards = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
            pnlCards.setOpaque(false);

            for (RoomCardData r : floorRooms) {
                pnlCards.add(buildRoomCard(r));
            }

            roomGridContainer.add(pnlCards);
            roomGridContainer.add(Box.createVerticalStrut(10));
        }

        roomGridContainer.revalidate();
        roomGridContainer.repaint();
    }

    private JPanel buildRoomCard(RoomCardData r) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setPreferredSize(new Dimension(195, 115));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color bg;
        Color border;
        Color pillColor;
        String pillText = r.trangThai;

        if ("Trống".equalsIgnoreCase(r.trangThai)) {
            bg = new Color(240, 253, 244);       // Light green
            border = new Color(187, 247, 208);
            pillColor = new Color(22, 163, 74);
        } else if ("Đang ở".equalsIgnoreCase(r.trangThai)) {
            bg = new Color(254, 242, 242);       // Light red
            border = new Color(254, 202, 202);
            pillColor = new Color(220, 38, 38);
        } else if ("Đang dọn".equalsIgnoreCase(r.trangThai)) {
            bg = new Color(254, 243, 199);       // Light amber
            border = new Color(253, 230, 138);
            pillColor = new Color(217, 119, 6);
        } else { // Bảo trì
            bg = new Color(241, 245, 249);       // Light slate
            border = new Color(203, 213, 225);
            pillColor = new Color(100, 116, 139);
        }

        card.setBackground(bg);
        card.setBorder(new CompoundBorder(
                new LineBorder(border, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        // Header: P101 | Pill Badge
        JPanel pnlHead = new JPanel(new BorderLayout());
        pnlHead.setOpaque(false);

        JLabel lblRoom = new JLabel("P." + r.soPhong);
        lblRoom.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblRoom.setForeground(new Color(15, 23, 42));

        JLabel lblPill = new JLabel(" " + pillText + " ");
        lblPill.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblPill.setForeground(pillColor);
        lblPill.setOpaque(true);
        lblPill.setBackground(Color.WHITE);
        lblPill.setBorder(new LineBorder(border, 1, true));

        pnlHead.add(lblRoom, BorderLayout.WEST);
        pnlHead.add(lblPill, BorderLayout.EAST);
        card.add(pnlHead, BorderLayout.NORTH);

        // Body: Info
        JPanel pnlBody = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlBody.setOpaque(false);

        if ("Trống".equalsIgnoreCase(r.trangThai)) {
            JLabel lblDesc = new JLabel(r.tenLoaiPhong + " • " + String.format("%.0fk/đêm", r.giaTheoDem / 1000));
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblDesc.setForeground(new Color(71, 85, 105));

            JLabel lblHint = new JLabel("● Click để đặt / Check-in");
            lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
            lblHint.setForeground(new Color(22, 163, 74));

            pnlBody.add(lblDesc);
            pnlBody.add(lblHint);
        } else if ("Đang ở".equalsIgnoreCase(r.trangThai)) {
            JLabel lblGuest = new JLabel("KH: " + (r.tenKhach != null ? r.tenKhach : "Chưa cập nhật"));
            lblGuest.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblGuest.setForeground(new Color(185, 28, 28));

            String traStr = r.ngayTraDuKien != null && r.ngayTraDuKien.length() >= 10 ? r.ngayTraDuKien.substring(5, 10) : "---";
            JLabel lblTra = new JLabel("🕒 Dự kiến trả: " + traStr);
            lblTra.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblTra.setForeground(new Color(100, 116, 139));

            pnlBody.add(lblGuest);
            pnlBody.add(lblTra);
        } else {
            JLabel lblSt = new JLabel("Đang dọn / Bảo trì");
            lblSt.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblSt.setForeground(new Color(100, 116, 139));

            JLabel lblAction = new JLabel("● Click đổi trạng thái");
            lblAction.setFont(new Font("Segoe UI", Font.ITALIC, 10));
            lblAction.setForeground(new Color(71, 85, 105));

            pnlBody.add(lblSt);
            pnlBody.add(lblAction);
        }

        card.add(pnlBody, BorderLayout.CENTER);

        // Click Action
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                hienThiMenuXuLyPhong(card, r);
            }
        });

        return card;
    }

    private void hienThiMenuXuLyPhong(Component comp, RoomCardData r) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem miTitle = new JMenuItem("Phòng " + r.soPhong + " (" + r.trangThai + ")");
        miTitle.setEnabled(false);
        miTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menu.add(miTitle);
        menu.addSeparator();

        if ("Trống".equalsIgnoreCase(r.trangThai)) {
            JMenuItem miCheckIn = new JMenuItem("🛎️ Đặt phòng & Check-in ngay");
            miCheckIn.addActionListener(e -> moDialogTaoDonDatChoPhong(r));
            menu.add(miCheckIn);
        } else if ("Đang ở".equalsIgnoreCase(r.trangThai)) {
            JMenuItem miGoiDV = new JMenuItem("🍹 Gọi Dịch vụ / Minibar cho phòng này");
            JMenuItem miCheckOut = new JMenuItem("🧾 Check-out & Xuất hóa đơn");

            miGoiDV.addActionListener(e -> moDialogGoiDichVuChoPhong(r));
            miCheckOut.addActionListener(e -> moDialogCheckOutChoPhong(r));

            menu.add(miGoiDV);
            menu.add(miCheckOut);
        }

        menu.addSeparator();
        JMenuItem miDoiTrangThai = new JMenuItem("🔄 Đổi trạng thái phòng...");
        miDoiTrangThai.addActionListener(e -> {
            String[] opts = {"Trống", "Đang ở", "Đang dọn", "Bảo trì"};
            String chosen = (String) JOptionPane.showInputDialog(this,
                    "Chọn trạng thái mới cho phòng " + r.soPhong + ":",
                    "Đổi trạng thái", JOptionPane.QUESTION_MESSAGE, null, opts, r.trangThai);
            if (chosen != null && !chosen.equals(r.trangThai)) {
                try {
                    DatabaseHelper.executeUpdate("UPDATE Phong SET TrangThai=? WHERE MaPhong=?", chosen, r.maPhong);
                    loadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        menu.add(miDoiTrangThai);

        menu.show(comp, 10, 30);
    }

    // Modal Dialog: Tạo đơn đặt phòng
    private void moDialogTaoDonDat() {
        moDialogTaoDonDatChoPhong(null);
    }

    private void moDialogTaoDonDatChoPhong(RoomCardData r) {
        JTextField txtMa = new JTextField("DP" + (System.currentTimeMillis() % 100000), 10);
        JComboBox<ComboItem> cboKH = new JComboBox<>();
        JComboBox<ComboItem> cboP = new JComboBox<>();
        JTextField txtGia = new JTextField(r != null ? String.valueOf(r.giaTheoDem) : "500000", 10);
        JTextField txtNhan = new JTextField("2026-11-01 14:00:00", 15);
        JTextField txtTra = new JTextField("2026-11-03 12:00:00", 15);

        // Load KH
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaKhachHang, HoTen, SoDienThoai FROM KhachHang")) {
            while (rs.next()) {
                cboKH.addItem(new ComboItem(rs.getString(1), rs.getString(2) + " (" + rs.getString(3) + ")"));
            }
        } catch (Exception ignored) {}

        // Load Phòng trống
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaPhong, SoPhong, TrangThai FROM Phong")) {
            while (rs.next()) {
                String id = rs.getString(1);
                String sp = rs.getString(2);
                String tt = rs.getString(3);
                ComboItem item = new ComboItem(id, "P." + sp + " [" + tt + "]");
                cboP.addItem(item);
                if (r != null && id.equals(r.maPhong)) {
                    cboP.setSelectedItem(item);
                }
            }
        } catch (Exception ignored) {}

        JPanel pnl = new JPanel(new GridLayout(6, 2, 8, 8));
        pnl.add(new JLabel("Mã đặt phòng:")); pnl.add(txtMa);
        pnl.add(new JLabel("Khách hàng:")); pnl.add(cboKH);
        pnl.add(new JLabel("Phòng gán:")); pnl.add(cboP);
        pnl.add(new JLabel("Giá thuê chốt (VNĐ):")); pnl.add(txtGia);
        pnl.add(new JLabel("Ngày nhận dự kiến:")); pnl.add(txtNhan);
        pnl.add(new JLabel("Ngày trả dự kiến:")); pnl.add(txtTra);

        int res = JOptionPane.showConfirmDialog(this, pnl, "TẠO ĐƠN ĐẶT PHÒNG & XẾP PHÒNG", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            ComboItem kh = (ComboItem) cboKH.getSelectedItem();
            ComboItem p = (ComboItem) cboP.getSelectedItem();
            if (kh == null || p == null) return;
            try {
                // 1. Tạo đơn đặt
                DatabaseHelper.executeUpdate("INSERT INTO DatPhong (MaDatPhong, MaKhachHang, NgayDat, TrangThai) VALUES (?, ?, NOW(), 'Đã nhận phòng')",
                        txtMa.getText().trim(), kh.getKey());
                // 2. Chi tiết đặt
                DatabaseHelper.executeUpdate("INSERT INTO ChiTietDatPhong (MaDatPhong, MaPhong, NgayNhanDuKien, NgayTraDuKien, NgayNhanThucTe, GiaThueThucTe) VALUES (?, ?, ?, ?, NOW(), ?)",
                        txtMa.getText().trim(), p.getKey(), txtNhan.getText().trim(), txtTra.getText().trim(), Double.parseDouble(txtGia.getText().trim()));
                // 3. Đổi trạng thái phòng sang Đang ở
                DatabaseHelper.executeUpdate("UPDATE Phong SET TrangThai='Đang ở' WHERE MaPhong=?", p.getKey());

                JOptionPane.showMessageDialog(this, "🎉 Đặt phòng và Check-in thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Modal Dialog: Gọi dịch vụ
    private void moDialogGoiDichVu() {
        moDialogGoiDichVuChoPhong(null);
    }

    private void moDialogGoiDichVuChoPhong(RoomCardData r) {
        JComboBox<ComboItem> cboPhong = new JComboBox<>();
        JComboBox<ComboItem> cboDV = new JComboBox<>();
        JSpinner spnSL = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));

        // Load phòng đang ở
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT ct.MaChiTietDatPhong, p.SoPhong, kh.HoTen " +
                     "FROM ChiTietDatPhong ct JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                     "JOIN DatPhong dp ON ct.MaDatPhong = dp.MaDatPhong " +
                     "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                     "WHERE ct.NgayNhanThucTe IS NOT NULL AND ct.NgayTraThucTe IS NULL")) {
            while (rs.next()) {
                String id = rs.getString(1);
                String sp = rs.getString(2);
                String kh = rs.getString(3);
                ComboItem item = new ComboItem(id, "P." + sp + " - " + kh);
                cboPhong.addItem(item);
                if (r != null && r.maChiTietDatPhong == Integer.parseInt(id)) {
                    cboPhong.setSelectedItem(item);
                }
            }
        } catch (Exception ignored) {}

        // Load danh mục dịch vụ
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT MaDichVu, TenDichVu, DonGia, DonViTinh FROM DichVu")) {
            while (rs.next()) {
                cboDV.addItem(new ComboItem(rs.getString(1), rs.getString(2) + " (" + String.format("%,.0f đ", rs.getDouble(3)) + ")", rs.getDouble(3)));
            }
        } catch (Exception ignored) {}

        if (cboPhong.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Hiện không có phòng nào đang ở để gọi dịch vụ!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel pnl = new JPanel(new GridLayout(3, 2, 8, 8));
        pnl.add(new JLabel("Phòng nhận dịch vụ:")); pnl.add(cboPhong);
        pnl.add(new JLabel("Chọn dịch vụ / Minibar:")); pnl.add(cboDV);
        pnl.add(new JLabel("Số lượng:")); pnl.add(spnSL);

        int res = JOptionPane.showConfirmDialog(this, pnl, "GHI NHẬN LƯỢT GỌI DỊCH VỤ PHÒNG", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            ComboItem p = (ComboItem) cboPhong.getSelectedItem();
            ComboItem dv = (ComboItem) cboDV.getSelectedItem();
            int sl = (Integer) spnSL.getValue();
            if (p == null || dv == null) return;

            try {
                // Price snapshot DonGiaBan
                DatabaseHelper.executeUpdate("INSERT INTO SuDungDichVu (MaChiTietDatPhong, MaDichVu, SoLuong, DonGiaBan, ThoiGianSuDung) VALUES (?, ?, ?, ?, NOW())",
                        p.getKey(), dv.getKey(), sl, dv.getPrice());
                JOptionPane.showMessageDialog(this, "Đã ghi nhận dịch vụ phòng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Modal Dialog: Check-out
    private void moDialogCheckOut() {
        moDialogCheckOutChoPhong(null);
    }

    private void moDialogCheckOutChoPhong(RoomCardData r) {
        JComboBox<ComboItem> cboPhong = new JComboBox<>();
        JTextField txtTienPhong = new JTextField("0", 10);
        JTextField txtTienDV = new JTextField("0", 10);
        JTextField txtVAT = new JTextField("8", 4);
        JLabel lblTongTien = new JLabel("0 đ");
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTongTien.setForeground(new Color(16, 185, 129));

        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT ct.MaChiTietDatPhong, p.SoPhong, kh.HoTen, ct.GiaThueThucTe, dp.MaDatPhong, p.MaPhong " +
                     "FROM ChiTietDatPhong ct JOIN Phong p ON ct.MaPhong = p.MaPhong " +
                     "JOIN DatPhong dp ON ct.MaDatPhong = dp.MaDatPhong " +
                     "JOIN KhachHang kh ON dp.MaKhachHang = kh.MaKhachHang " +
                     "WHERE ct.NgayNhanThucTe IS NOT NULL AND ct.NgayTraThucTe IS NULL")) {
            while (rs.next()) {
                String id = rs.getString(1);
                String sp = rs.getString(2);
                String kh = rs.getString(3);
                double gia = rs.getDouble(4);
                String dp = rs.getString(5);
                String mp = rs.getString(6);
                ComboItem item = new ComboItem(id, "P." + sp + " - " + kh, gia, dp, mp);
                cboPhong.addItem(item);
                if (r != null && r.maChiTietDatPhong == Integer.parseInt(id)) {
                    cboPhong.setSelectedItem(item);
                }
            }
        } catch (Exception ignored) {}

        if (cboPhong.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Hiện không có phòng nào đang ở cần làm thủ tục check-out!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Runnable tinhTien = () -> {
            ComboItem item = (ComboItem) cboPhong.getSelectedItem();
            if (item != null) {
                double tp = item.getPrice() * 2; // ước tính 2 đêm
                double tdv = 0;
                try {
                    Object dvObj = DatabaseHelper.executeScalar(
                            "SELECT COALESCE(SUM(SoLuong * DonGiaBan), 0) FROM SuDungDichVu WHERE MaChiTietDatPhong = ?", item.getKey());
                    if (dvObj instanceof Number) tdv = ((Number) dvObj).doubleValue();
                } catch (Exception ignored) {}

                txtTienPhong.setText(String.format("%.0f", tp));
                txtTienDV.setText(String.format("%.0f", tdv));

                double vat = 8;
                try { vat = Double.parseDouble(txtVAT.getText().trim()); } catch (Exception ignored) {}
                double tong = (tp + tdv) * (1 + vat / 100);
                lblTongTien.setText(String.format("%,.0f VNĐ", tong));
            }
        };

        cboPhong.addActionListener(e -> tinhTien.run());
        tinhTien.run();

        JPanel pnl = new JPanel(new GridLayout(5, 2, 8, 8));
        pnl.add(new JLabel("Phòng Check-out:")); pnl.add(cboPhong);
        pnl.add(new JLabel("Tiền phòng:")); pnl.add(txtTienPhong);
        pnl.add(new JLabel("Tiền dịch vụ:")); pnl.add(txtTienDV);
        pnl.add(new JLabel("VAT (%):")); pnl.add(txtVAT);
        pnl.add(new JLabel("TỔNG THANH TOÁN:")); pnl.add(lblTongTien);

        int res = JOptionPane.showConfirmDialog(this, pnl, "THỦ TỤC CHECK-OUT & XUẤT HÓA ĐƠN", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            ComboItem item = (ComboItem) cboPhong.getSelectedItem();
            if (item == null) return;
            try {
                double tp = Double.parseDouble(txtTienPhong.getText().trim());
                double tdv = Double.parseDouble(txtTienDV.getText().trim());
                double vat = Double.parseDouble(txtVAT.getText().trim());
                String maHD = "HD" + (System.currentTimeMillis() % 100000);

                // 1. Tạo hóa đơn
                DatabaseHelper.executeUpdate(
                        "INSERT INTO HoaDon (MaHoaDon, MaDatPhong, MaNhanVien, NgayLap, TienPhong, TienDichVu, GiamGia, PhuPhi, ThueVAT, TrangThai) " +
                        "VALUES (?, ?, 'NV002', NOW(), ?, ?, 0, 0, ?, 'Hoàn tất')",
                        maHD, item.getExtra(), tp, tdv, vat);

                // 2. Cập nhật ngày trả
                DatabaseHelper.executeUpdate("UPDATE ChiTietDatPhong SET NgayTraThucTe=NOW() WHERE MaChiTietDatPhong=?", item.getKey());

                // 3. Chuyển phòng sang Đang dọn
                DatabaseHelper.executeUpdate("UPDATE Phong SET TrangThai='Đang dọn' WHERE MaPhong=?", item.getExtra2());

                // 4. Hoàn tất đơn đặt
                DatabaseHelper.executeUpdate("UPDATE DatPhong SET TrangThai='Hoàn thành' WHERE MaDatPhong=?", item.getExtra());

                JOptionPane.showMessageDialog(this,
                        "🎉 CHECK-OUT & XUẤT HÓA ĐƠN THÀNH CÔNG!\n" +
                        "Mã HĐ: " + maHD + "\nPhòng đã tự động chuyển sang trạng thái 'Đang dọn'.",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi Check-out: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static class ComboItem {
        private String key;
        private String value;
        private double price;
        private String extra;
        private String extra2;

        public ComboItem(String key, String value) {
            this.key = key; this.value = value;
        }
        public ComboItem(String key, String value, double price) {
            this.key = key; this.value = value; this.price = price;
        }
        public ComboItem(String key, String value, double price, String extra, String extra2) {
            this.key = key; this.value = value; this.price = price; this.extra = extra; this.extra2 = extra2;
        }
        public String getKey() { return key; }
        public String getValue() { return value; }
        public double getPrice() { return price; }
        public String getExtra() { return extra; }
        public String getExtra2() { return extra2; }
        @Override public String toString() { return value; }
    }
}

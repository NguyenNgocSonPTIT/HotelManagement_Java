package hotel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachHangPanel extends JPanel {
    private JTextField txtMaKH, txtHoTen, txtSoGiayTo, txtQuocTich, txtSDT, txtEmail, txtDiaChi;
    private JComboBox<String> cboLoaiGiayTo;
    private JTable tblKhachHang;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;

    // Danh sách các listener lắng nghe khi khách hàng thay đổi
    private static final List<Runnable> khachHangChangeListeners = new ArrayList<>();

    public static void addChangeListener(Runnable r) {
        khachHangChangeListeners.add(r);
    }

    private static void notifyChange() {
        for (Runnable r : khachHangChangeListeners) {
            r.run();
        }
    }

    public KhachHangPanel() {
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

        JLabel lblTitle = new JLabel("QUẢN LÝ DANH BẠ KHÁCH HÀNG (JAVA SWING & MYSQL)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubTitle = new JLabel("* Quy tắc đúng đắn: CCCD đúng 12 chữ số | Số điện thoại đúng 10-11 chữ số bắt đầu bằng số 0");
        lblSubTitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubTitle.setForeground(new Color(203, 213, 225));

        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSubTitle);
        add(pnlTitle, BorderLayout.NORTH);

        // 2. Center Panel (Form nhập + Danh sách)
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setOpaque(false);

        // --- Panel Nhập liệu ---
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        pnlInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                        " THÔNG TIN HỒ SƠ KHÁCH HÀNG ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)),
                new EmptyBorder(10, 15, 10, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaKH = new JTextField(10);
        txtHoTen = new JTextField(15);
        cboLoaiGiayTo = new JComboBox<>(new String[]{"CCCD", "PASSPORT"});
        txtSoGiayTo = new JTextField(12);
        txtQuocTich = new JTextField("Việt Nam", 10);
        txtSDT = new JTextField(12);
        txtEmail = new JTextField(15);
        txtDiaChi = new JTextField(25);

        // Hàng 0: Mã KH + Họ tên + Loại giấy tờ
        gbc.gridx = 0; gbc.gridy = 0; pnlInput.add(new JLabel("Mã KH (*):"), gbc);
        gbc.gridx = 1; pnlInput.add(txtMaKH, gbc);
        gbc.gridx = 2; pnlInput.add(new JLabel("Họ và tên (*):"), gbc);
        gbc.gridx = 3; pnlInput.add(txtHoTen, gbc);
        gbc.gridx = 4; pnlInput.add(new JLabel("Loại giấy tờ:"), gbc);
        gbc.gridx = 5; pnlInput.add(cboLoaiGiayTo, gbc);

        // Hàng 1: Số giấy tờ + Quốc tịch + Số ĐT
        gbc.gridx = 0; gbc.gridy = 1; pnlInput.add(new JLabel("Số định danh (*):"), gbc);
        gbc.gridx = 1; pnlInput.add(txtSoGiayTo, gbc);
        gbc.gridx = 2; pnlInput.add(new JLabel("Quốc tịch:"), gbc);
        gbc.gridx = 3; pnlInput.add(txtQuocTich, gbc);
        gbc.gridx = 4; pnlInput.add(new JLabel("Số ĐT (*):"), gbc);
        gbc.gridx = 5; pnlInput.add(txtSDT, gbc);

        // Hàng 2: Email + Địa chỉ
        gbc.gridx = 0; gbc.gridy = 2; pnlInput.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; pnlInput.add(txtEmail, gbc);
        gbc.gridx = 2; pnlInput.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 3; gbc.gridwidth = 3; pnlInput.add(txtDiaChi, gbc);
        gbc.gridwidth = 1;

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
        tblKhachHang = new JTable();
        tblKhachHang.setRowHeight(28);
        tblKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblKhachHang.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblKhachHang.getTableHeader().setBackground(new Color(30, 58, 138));
        tblKhachHang.getTableHeader().setForeground(Color.WHITE);
        tblKhachHang.setSelectionBackground(new Color(219, 234, 254));
        tblKhachHang.setSelectionForeground(Color.BLACK);
        tblKhachHang.setGridColor(new Color(226, 232, 240));

        tblKhachHang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblKhachHang.getSelectedRow();
                if (row >= 0) {
                    txtMaKH.setText(getValue(row, 0));
                    txtHoTen.setText(getValue(row, 1));
                    cboLoaiGiayTo.setSelectedItem(getValue(row, 2));
                    txtSoGiayTo.setText(getValue(row, 3));
                    txtQuocTich.setText(getValue(row, 4));
                    txtSDT.setText(getValue(row, 5));
                    txtEmail.setText(getValue(row, 6));
                    txtDiaChi.setText(getValue(row, 7));
                }
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblKhachHang);
        scrollTable.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)),
                " DANH SÁCH KHÁCH HÀNG ", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 13), new Color(30, 41, 59)));
        pnlCenter.add(scrollTable, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // Sự kiện nút bấm
        btnThem.addActionListener(e -> themKhachHang());
        btnSua.addActionListener(e -> suaKhachHang());
        btnXoa.addActionListener(e -> xoaKhachHang());
        btnLamMoi.addActionListener(e -> {
            lamMoi();
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
        Object val = tblKhachHang.getValueAt(row, col);
        return val == null ? "" : val.toString();
    }

    public void loadData() {
        try {
            String sql = "SELECT MaKhachHang AS 'Mã KH', HoTen AS 'Họ Và Tên', LoaiGiayTo AS 'Loại Giấy Tờ', " +
                         "SoGiayTo AS 'Số Định Danh', QuocTich AS 'Quốc Tịch', SoDienThoai AS 'Số ĐT', " +
                         "Email AS 'Email', DiaChi AS 'Địa Chỉ' FROM KhachHang ORDER BY MaKhachHang";
            DefaultTableModel model = DatabaseHelper.executeQuery(sql);
            tblKhachHang.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nạp dữ liệu: " + ex.getMessage(), "Lỗi MySQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        String maKH = txtMaKH.getText().trim();
        String hoTen = txtHoTen.getText().trim();
        String soGiayTo = txtSoGiayTo.getText().trim();
        String sdt = txtSDT.getText().trim();
        String loai = (String) cboLoaiGiayTo.getSelectedItem();

        if (maKH.isEmpty() || hoTen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Mã khách hàng và Họ tên!", "Cảnh báo nhập liệu", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 1. Kiểm tra tính đúng đắn CCCD (đúng 12 số)
        if ("CCCD".equals(loai)) {
            if (!soGiayTo.matches("^\\d{12}$")) {
                JOptionPane.showMessageDialog(this,
                        "Số Căn cước công dân (CCCD) không hợp lệ!\n\n" +
                        "- Quy định: Phải bao gồm ĐÚNG 12 chữ số (không chứa chữ cái hay ký tự đặc biệt).\n" +
                        "- Bạn vừa nhập: " + soGiayTo.length() + " ký tự (" + soGiayTo + ").",
                        "Lỗi định dạng CCCD", JOptionPane.ERROR_MESSAGE);
                txtSoGiayTo.requestFocus();
                return false;
            }
        } else {
            if (soGiayTo.length() < 6 || soGiayTo.length() > 15) {
                JOptionPane.showMessageDialog(this, "Số Hộ chiếu (Passport) phải có độ dài từ 6 đến 15 ký tự!", "Lỗi định dạng Hộ chiếu", JOptionPane.ERROR_MESSAGE);
                txtSoGiayTo.requestFocus();
                return false;
            }
        }

        // 2. Kiểm tra tính đúng đắn Số điện thoại (đúng 10 hoặc 11 số, bắt đầu bằng số 0)
        if (!sdt.matches("^0\\d{9,10}$")) {
            JOptionPane.showMessageDialog(this,
                    "Số điện thoại không hợp lệ!\n\n" +
                    "- Quy định: Phải bắt đầu bằng số 0 và có độ dài ĐÚNG 10 hoặc 11 chữ số.\n" +
                    "- Bạn vừa nhập: " + sdt.length() + " ký tự (" + sdt + ").",
                    "Lỗi định dạng Số điện thoại", JOptionPane.ERROR_MESSAGE);
            txtSDT.requestFocus();
            return false;
        }

        return true;
    }

    private void themKhachHang() {
        if (!validateInput()) return;

        try {
            String sql = "INSERT INTO KhachHang (MaKhachHang, HoTen, LoaiGiayTo, SoGiayTo, QuocTich, SoDienThoai, Email, DiaChi) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            int rows = DatabaseHelper.executeUpdate(sql,
                    txtMaKH.getText().trim().toUpperCase(),
                    txtHoTen.getText().trim(),
                    cboLoaiGiayTo.getSelectedItem(),
                    txtSoGiayTo.getText().trim(),
                    txtQuocTich.getText().trim().isEmpty() ? "Việt Nam" : txtQuocTich.getText().trim(),
                    txtSDT.getText().trim(),
                    txtEmail.getText().trim(),
                    txtDiaChi.getText().trim());

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng mới thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                lamMoi();
                notifyChange(); // Đồng bộ sang phân hệ đặt phòng
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm khách hàng: " + ex.getMessage(), "Lỗi MySQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaKhachHang() {
        if (txtMaKH.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa từ bảng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInput()) return;

        try {
            String sql = "UPDATE KhachHang SET HoTen=?, LoaiGiayTo=?, SoGiayTo=?, QuocTich=?, SoDienThoai=?, Email=?, DiaChi=? WHERE MaKhachHang=?";
            int rows = DatabaseHelper.executeUpdate(sql,
                    txtHoTen.getText().trim(),
                    cboLoaiGiayTo.getSelectedItem(),
                    txtSoGiayTo.getText().trim(),
                    txtQuocTich.getText().trim(),
                    txtSDT.getText().trim(),
                    txtEmail.getText().trim(),
                    txtDiaChi.getText().trim(),
                    txtMaKH.getText().trim());

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Cập nhật thông tin khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                notifyChange();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi sửa khách hàng: " + ex.getMessage(), "Lỗi MySQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhachHang() {
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng muốn xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa khách hàng [" + maKH + "] không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM KhachHang WHERE MaKhachHang=?";
                int rows = DatabaseHelper.executeUpdate(sql, maKH);
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Đã xóa khách hàng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                    lamMoi();
                    notifyChange();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Không thể xóa do ràng buộc dữ liệu: " + ex.getMessage(), "Lỗi ràng buộc", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void lamMoi() {
        txtMaKH.setText("");
        txtHoTen.setText("");
        txtSoGiayTo.setText("");
        txtQuocTich.setText("Việt Nam");
        txtSDT.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        cboLoaiGiayTo.setSelectedIndex(0);
        txtMaKH.requestFocus();
    }
}

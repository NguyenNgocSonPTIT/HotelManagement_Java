package hotel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class MainFrame extends JFrame {
    private CardLayout workspaceLayout;
    private JPanel workspaceContainer;

    // 2 Màn hình làm việc chính
    private VanHanhLeTanPanel vanHanhPanel;
    private QuanTriDanhMucPanel quanTriPanel;

    // 2 Nút chuyển đổi chế độ trên Top Navbar
    private JButton btnNavVanHanh;
    private JButton btnNavQuanTri;
    private int currentWorkspaceIndex = 0; // 0: Vận hành, 1: Quản trị

    public MainFrame() {
        setTitle("HỆ THỐNG QUẢN LÝ KHÁCH SẠN (HOTEL MANAGEMENT SYSTEM - JAVA & MYSQL)");
        setSize(1340, 820);
        setMinimumSize(new Dimension(1150, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ==============================================================
        // 1. TOP NAVBAR (THANH ĐIỀU HƯỚNG TỔNG)
        // ==============================================================
        JPanel topNavbar = new JPanel(new BorderLayout(16, 0));
        topNavbar.setPreferredSize(new Dimension(0, 64));
        topNavbar.setBackground(new Color(15, 23, 42)); // Deep Slate Navy
        topNavbar.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Left Branding
        JPanel pnlBrand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        pnlBrand.setOpaque(false);

        JLabel lblLogoIcon = new JLabel("🏨");
        lblLogoIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));

        JPanel pnlBrandText = new JPanel(new GridLayout(2, 1, 0, 1));
        pnlBrandText.setOpaque(false);

        JLabel lblTitle = new JLabel("HOTEL MANAGEMENT SYSTEM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Phiên bản giao diện thân thiện & tối ưu thao tác");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(148, 163, 184));

        pnlBrandText.add(lblTitle);
        pnlBrandText.add(lblSub);

        pnlBrand.add(lblLogoIcon);
        pnlBrand.add(pnlBrandText);
        topNavbar.add(pnlBrand, BorderLayout.WEST);

        // Center: 2 Big Mode Toggle Buttons
        JPanel pnlModeSwitch = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        pnlModeSwitch.setOpaque(false);

        btnNavVanHanh = createModeButton("💻 1. Vận hành & Lễ Tân", true);
        btnNavQuanTri = createModeButton("⚙️ 2. Quản Trị & Danh Mục", false);

        btnNavVanHanh.addActionListener(e -> switchWorkspace(0));
        btnNavQuanTri.addActionListener(e -> switchWorkspace(1));

        pnlModeSwitch.add(btnNavVanHanh);
        pnlModeSwitch.add(btnNavQuanTri);
        topNavbar.add(pnlModeSwitch, BorderLayout.CENTER);

        // Right User Pill & DB Config
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        pnlRight.setOpaque(false);

        JButton btnDb = new JButton("🔌 MySQL");
        btnDb.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDb.setBackground(new Color(30, 41, 59));
        btnDb.setForeground(new Color(203, 213, 225));
        btnDb.setFocusPainted(false);
        btnDb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDb.setBorder(new EmptyBorder(6, 12, 6, 12));
        btnDb.addActionListener(e -> moDialogCauHinh());

        JLabel lblUser = new JLabel("👤 Admin (Quản trị viên)");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUser.setForeground(new Color(226, 232, 240));
        lblUser.setOpaque(true);
        lblUser.setBackground(new Color(30, 41, 59));
        lblUser.setBorder(new CompoundBorder(
                new LineBorder(new Color(51, 65, 85), 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));

        pnlRight.add(btnDb);
        pnlRight.add(lblUser);
        topNavbar.add(pnlRight, BorderLayout.EAST);

        add(topNavbar, BorderLayout.NORTH);

        // ==============================================================
        // 2. MAIN WORKSPACE CONTAINER (CARD LAYOUT)
        // ==============================================================
        workspaceLayout = new CardLayout();
        workspaceContainer = new JPanel(workspaceLayout);

        vanHanhPanel = new VanHanhLeTanPanel(this);
        quanTriPanel = new QuanTriDanhMucPanel(this);

        workspaceContainer.add(vanHanhPanel, "vanHanh");
        workspaceContainer.add(quanTriPanel, "quanTri");

        add(workspaceContainer, BorderLayout.CENTER);

        switchWorkspace(0);
    }

    private JButton createModeButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(active ? new Color(99, 102, 241) : new Color(51, 65, 85), 1, true),
                new EmptyBorder(8, 20, 8, 20)
        ));
        if (active) {
            btn.setBackground(new Color(79, 70, 229)); // Indigo 600
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(30, 41, 59)); // Slate 800
            btn.setForeground(new Color(203, 213, 225));
        }
        return btn;
    }

    public void switchWorkspace(int index) {
        currentWorkspaceIndex = index;
        if (index == 0) {
            btnNavVanHanh.setBackground(new Color(79, 70, 229));
            btnNavVanHanh.setForeground(Color.WHITE);
            btnNavQuanTri.setBackground(new Color(30, 41, 59));
            btnNavQuanTri.setForeground(new Color(203, 213, 225));
            workspaceLayout.show(workspaceContainer, "vanHanh");
            vanHanhPanel.loadData();
        } else {
            btnNavQuanTri.setBackground(new Color(79, 70, 229));
            btnNavQuanTri.setForeground(Color.WHITE);
            btnNavVanHanh.setBackground(new Color(30, 41, 59));
            btnNavVanHanh.setForeground(new Color(203, 213, 225));
            workspaceLayout.show(workspaceContainer, "quanTri");
            quanTriPanel.selectMenu(0);
        }
    }

    public void moDialogCauHinh() {
        JTextField txtHost = new JTextField(DatabaseHelper.getHost(), 15);
        JTextField txtPort = new JTextField(DatabaseHelper.getPort(), 6);
        JTextField txtDb = new JTextField(DatabaseHelper.getDbName(), 15);
        JTextField txtUser = new JTextField(DatabaseHelper.getUser(), 15);
        JPasswordField txtPass = new JPasswordField(DatabaseHelper.getPassword(), 15);

        JPanel pnl = new JPanel(new GridLayout(5, 2, 8, 8));
        pnl.add(new JLabel("Host:")); pnl.add(txtHost);
        pnl.add(new JLabel("Port:")); pnl.add(txtPort);
        pnl.add(new JLabel("Tên Database:")); pnl.add(txtDb);
        pnl.add(new JLabel("User:")); pnl.add(txtUser);
        pnl.add(new JLabel("Password:")); pnl.add(txtPass);

        int res = JOptionPane.showConfirmDialog(this, pnl, "Cấu hình kết nối MySQL", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            String p = new String(txtPass.getPassword()).trim();
            DatabaseHelper.saveConfig(txtHost.getText().trim(), txtPort.getText().trim(), txtDb.getText().trim(), txtUser.getText().trim(), p);
            if (DatabaseHelper.testConnection()) {
                JOptionPane.showMessageDialog(this, "Kết nối MySQL thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                if (currentWorkspaceIndex == 0) vanHanhPanel.loadData();
                else quanTriPanel.selectMenu(0);
            } else {
                JOptionPane.showMessageDialog(this, "Kết nối thất bại: " + DatabaseHelper.getLastError(), "Lỗi kết nối", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}

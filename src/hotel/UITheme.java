package hotel;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class UITheme {
    // Bảng màu giao diện hiện đại (Modern PMS Palette)
    public static final Color SIDEBAR_BG      = new Color(15, 23, 42);      // Slate 900
    public static final Color SIDEBAR_HOVER   = new Color(30, 41, 59);      // Slate 800
    public static final Color SIDEBAR_ACTIVE  = new Color(37, 99, 235);     // Blue 600
    public static final Color CONTENT_BG      = new Color(248, 250, 252);   // Slate 50
    public static final Color CARD_BG         = Color.WHITE;
    public static final Color BORDER_COLOR    = new Color(226, 232, 240);   // Slate 200
    public static final Color TEXT_PRIMARY    = new Color(15, 23, 42);      // Slate 900
    public static final Color TEXT_MUTED      = new Color(100, 116, 139);   // Slate 500
    public static final Color PRIMARY         = new Color(37, 99, 235);     // Blue 600
    public static final Color PRIMARY_DARK    = new Color(29, 78, 216);     // Blue 700

    // Màu trạng thái phòng
    public static final Color STATUS_TRONG       = new Color(16, 185, 129); // Emerald 500
    public static final Color STATUS_TRONG_BG    = new Color(209, 250, 229);
    public static final Color STATUS_DANG_O      = new Color(239, 68, 68);  // Rose 500
    public static final Color STATUS_DANG_O_BG   = new Color(254, 226, 226);
    public static final Color STATUS_DANG_DON    = new Color(245, 158, 11); // Amber 500
    public static final Color STATUS_DANG_DON_BG = new Color(254, 243, 199);
    public static final Color STATUS_BAO_TRI     = new Color(100, 116, 139); // Slate 500
    public static final Color STATUS_BAO_TRI_BG  = new Color(241, 245, 249);

    // Phông chữ Segoe UI
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(bg.darker(), 1, true),
                new EmptyBorder(7, 14, 7, 14)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY, Color.WHITE);
    }

    public static JButton createSuccessButton(String text) {
        return createButton(text, STATUS_TRONG, Color.WHITE);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, STATUS_DANG_O, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createButton(text, new Color(241, 245, 249), TEXT_PRIMARY);
    }

    public static JPanel createCard(String title) {
        JPanel pnl = new JPanel(new BorderLayout(8, 8));
        pnl.setBackground(CARD_BG);
        Border line = new LineBorder(BORDER_COLOR, 1, true);
        Border margin = new EmptyBorder(12, 14, 12, 14);
        if (title != null && !title.isEmpty()) {
            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);
            header.setBorder(new EmptyBorder(0, 0, 8, 0));
            JLabel lbl = new JLabel(title);
            lbl.setFont(FONT_SECTION);
            lbl.setForeground(TEXT_PRIMARY);
            header.add(lbl, BorderLayout.WEST);
            pnl.add(header, BorderLayout.NORTH);
        }
        pnl.setBorder(new CompoundBorder(line, margin));
        return pnl;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(32);
        table.setSelectionBackground(new Color(219, 234, 254)); // Blue 100
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);

        JTableHeader th = table.getTableHeader();
        th.setFont(FONT_BOLD);
        th.setBackground(new Color(241, 245, 249));
        th.setForeground(TEXT_PRIMARY);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Integer.class, center);
        table.setDefaultRenderer(Double.class, center);
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_REGULAR);
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    public static JPanel createStatChip(String label, String value, Color color) {
        JPanel chip = new JPanel(new BorderLayout(4, 2));
        chip.setBackground(CARD_BG);
        chip.setBorder(new CompoundBorder(
                new LineBorder(color.brighter(), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblVal.setForeground(color);

        JLabel lblName = new JLabel(label);
        lblName.setFont(FONT_SMALL);
        lblName.setForeground(TEXT_MUTED);

        chip.add(lblVal, BorderLayout.CENTER);
        chip.add(lblName, BorderLayout.SOUTH);
        return chip;
    }
}

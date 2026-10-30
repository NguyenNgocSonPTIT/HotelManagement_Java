package hotel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.GraphicsEnvironment;

public class DatabaseHelper {
    private static String host = "localhost";
    private static String port = "3306";
    private static String dbName = "HotelManagement";
    private static String user = "root";
    private static String password = "";
    private static boolean isPrompting = false;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        try {
            File configFile = new File("config.properties");
            if (configFile.exists()) {
                Properties prop = new Properties();
                try (InputStream is = new FileInputStream(configFile)) {
                    prop.load(is);
                    host = prop.getProperty("db.host", host).trim();
                    port = prop.getProperty("db.port", port).trim();
                    dbName = prop.getProperty("db.name", dbName).trim();
                    user = prop.getProperty("db.user", user).trim();
                    password = prop.getProperty("db.password", password).trim();
                }
            }
        } catch (Exception e) {
            System.err.println("Không đọc được config.properties, dùng mặc định: " + e.getMessage());
        }
    }

    public static void saveConfig(String newHost, String newPort, String newDbName, String newUser, String newPassword) {
        try {
            host = newHost;
            port = newPort;
            dbName = newDbName;
            user = newUser;
            password = newPassword;
            File configFile = new File("config.properties");
            Properties prop = new Properties();
            prop.setProperty("db.host", host);
            prop.setProperty("db.port", port);
            prop.setProperty("db.name", dbName);
            prop.setProperty("db.user", user);
            prop.setProperty("db.password", password);
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                prop.store(fos, "Cau hinh ket noi MySQL cho HotelManagement");
            }
        } catch (Exception e) {
            System.err.println("Không lưu được config.properties: " + e.getMessage());
        }
    }

    public static String getUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + dbName
                + "?useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    }

    public static synchronized Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Chưa tìm thấy MySQL JDBC Driver: " + e.getMessage());
        }

        try {
            return DriverManager.getConnection(getUrl(), user, password);
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("Access denied for user") && !GraphicsEnvironment.isHeadless() && !isPrompting) {
                isPrompting = true;
                try {
                    JPasswordField pf = new JPasswordField(15);
                    Object[] message = {
                        "Đã kết nối được MySQL nhưng tài khoản '" + user + "' yêu cầu mật khẩu!\n" +
                        "(Lỗi: Access denied for user 'root'@'localhost')\n\n" +
                        "Vui lòng nhập mật khẩu MySQL bạn đã đặt khi cài đặt MySQL:",
                        pf
                    };
                    int option = JOptionPane.showConfirmDialog(null, message, "Nhập Mật Khẩu MySQL Server", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (option == JOptionPane.OK_OPTION) {
                        String enteredPass = new String(pf.getPassword()).trim();
                        password = enteredPass;
                        saveConfig(host, port, dbName, user, password);
                        return DriverManager.getConnection(getUrl(), user, password);
                    }
                } finally {
                    isPrompting = false;
                }
            }
            throw ex;
        }
    }

    public static void setCredentials(String newUser, String newPassword) {
        user = newUser;
        password = newPassword;
    }

    public static String getHost() { return host; }
    public static String getPort() { return port; }
    public static String getDbName() { return dbName; }
    public static String getUser() { return user; }
    public static String getPassword() { return password; }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    public static String getLastError() {
        try (Connection conn = getConnection()) {
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        }
    }

    public static Object executeScalar(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject(1);
                }
            }
        }
        return null;
    }

    public static DefaultTableModel executeQuery(String sql, Object... params) throws SQLException {
        return executeQueryToTableModel(sql, params);
    }

    public static DefaultTableModel executeQueryToTableModel(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                Vector<String> columnNames = new Vector<>();
                for (int col = 1; col <= columnCount; col++) {
                    columnNames.add(metaData.getColumnLabel(col));
                }

                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int col = 1; col <= columnCount; col++) {
                        row.add(rs.getObject(col));
                    }
                    data.add(row);
                }

                return new DefaultTableModel(data, columnNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
            }
        }
    }
}

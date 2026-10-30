package hotel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("==============================================================");
        System.out.println("   KIEM TRA KET NOI MYSQL CHO HOTEL MANAGEMENT SYSTEM");
        System.out.println("==============================================================");
        System.out.println("URL:  " + DatabaseHelper.getUrl());

        try {
            Connection conn = DatabaseHelper.getConnection();
            System.out.println("[OK] KET NOI THANH CONG DEN MYSQL SERVER!");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT DATABASE(), VERSION(), COUNT(*) FROM Phong");
            if (rs.next()) {
                System.out.println("-> CSDL hien tai: " + rs.getString(1));
                System.out.println("-> Phien ban MySQL: " + rs.getString(2));
                System.out.println("-> So phong co trong CSDL: " + rs.getInt(3));
            }
            conn.close();
            System.out.println("==============================================================");
            System.out.println("CSDL DA SAN SANG! BAN CO THE CHAY 'Run_Java_App.bat' NGAY.");
            System.out.println("==============================================================");
        } catch (Exception e) {
            System.err.println("\n[LOI KET NOI]: " + e.getMessage());
            System.err.println("--------------------------------------------------------------");
            if (e.getMessage().contains("Communications link failure") || e.getMessage().contains("Connection refused")) {
                System.err.println("-> NGUYEN NHAN: Dich vu MySQL chua duoc BAT.");
                System.err.println("-> KHAC PHUC: Nhan chuot phai vao 'Bat_Dich_Vu_MySQL.bat' chon 'Run as administrator'");
                System.err.println("   hoac mo 'services.msc', tim 'MySQL80' roi nhan 'Start'.");
            } else if (e.getMessage().contains("Access denied for user")) {
                System.err.println("-> NGUYEN NHAN: Sai mat khau tai khoan root.");
                System.err.println("-> KHAC PHUC: Mo file 'config.properties' va dien mat khau vao dong: db.password=YOUR_PASSWORD");
            } else if (e.getMessage().contains("Unknown database")) {
                System.err.println("-> NGUYEN NHAN: CSDL 'HotelManagement' chua duoc tao trong MySQL.");
                System.err.println("-> KHAC PHUC: Mo MySQL Workbench, mo file 'Database\\db-hotel.sql' va bam bieu tuong tia set (Execute).");
            }
            System.err.println("==============================================================");
        }
    }
}

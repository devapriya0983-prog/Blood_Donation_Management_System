
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Reports {

    static Scanner sc = new Scanner(System.in);

    public static void stockReport() {

        String sql = "SELECT blood_group, available_units " +
                     "FROM blood_stock ORDER BY blood_group";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- BLOOD STOCK REPORT ---");

            while (rs.next()) {
                System.out.println(
                    rs.getString("blood_group") + " : " +
                    rs.getInt("available_units") + " units"
                );
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void donationHistory() {

        String sql =
            "SELECT dn.donation_id, u.name, d.blood_group, " +
            "dn.donation_date, dn.units_donated, dn.hospital_name, dn.status " +
            "FROM donations dn " +
            "JOIN donors d ON dn.donor_id = d.donor_id " +
            "JOIN users u ON d.user_id = u.user_id " +
            "ORDER BY dn.donation_date DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- DONATION HISTORY ---");

            while (rs.next()) {
                System.out.println("--------------------------");
                System.out.println("Donation ID: " +
                    rs.getInt("donation_id"));
                System.out.println("Donor Name: " +
                    rs.getString("name"));
                System.out.println("Blood Group: " +
                    rs.getString("blood_group"));
                System.out.println("Date: " +
                    rs.getDate("donation_date"));
                System.out.println("Units: " +
                    rs.getInt("units_donated"));
                System.out.println("Hospital: " +
                    rs.getString("hospital_name"));
                System.out.println("Status: " +
                    rs.getString("status"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void donationCountReport() {

       String sql =
    "SELECT bs.blood_group, COUNT(dn.donation_id) AS total_donations " +
    "FROM blood_stock bs " +
    "LEFT JOIN donors d ON bs.blood_group = d.blood_group " +
    "LEFT JOIN donations dn ON d.donor_id = dn.donor_id " +
    "AND dn.status = 'Completed' " +
    "GROUP BY bs.blood_group " +
    "ORDER BY bs.blood_group";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- BLOOD GROUP-WISE DONATION COUNT ---");

            while (rs.next()) {
                System.out.println(
                    rs.getString("blood_group") + " : " +
                    rs.getInt("total_donations") + " donations"
                );
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n===== REPORTS MENU =====");
            System.out.println("1. Blood Stock Report");
            System.out.println("2. Donation History");
            System.out.println("3. Blood Group-wise Donation Count");
            System.out.println("4. Exit");

            System.out.print("Enter Choice: ");

            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number!");
                continue;
            }

            switch (choice) {
                case 1:
                    stockReport();
                    break;

                case 2:
                    donationHistory();
                    break;

                case 3:
                    donationCountReport();
                    break;

                case 4:
                    System.out.println("Exiting Reports...");
                    return;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }
}

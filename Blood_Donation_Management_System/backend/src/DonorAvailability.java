
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DonorAvailability {

    public static void showEligibleDonors() {

        String sql =
            "SELECT d.donor_id, u.name, d.blood_group, " +
            "d.city, MAX(dn.donation_date) AS last_donation " +
            "FROM donors d " +
            "JOIN users u ON d.user_id = u.user_id " +
            "LEFT JOIN donations dn ON d.donor_id = dn.donor_id " +
            "AND dn.status = 'Completed' " +
            "WHERE d.availability = 1 " +
            "GROUP BY d.donor_id, u.name, d.blood_group, d.city " +
            "HAVING last_donation IS NULL " +
            "OR last_donation <= DATE_SUB(CURDATE(), INTERVAL 90 DAY)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- ELIGIBLE DONOR LIST ---");

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println("---------------------------");
                System.out.println("Donor ID: " +
                    rs.getInt("donor_id"));

                System.out.println("Name: " +
                    rs.getString("name"));

                System.out.println("Blood Group: " +
                    rs.getString("blood_group"));

                System.out.println("City: " +
                    rs.getString("city"));

                System.out.println("Last Donation: " +
                    rs.getDate("last_donation"));
            }

            if (!found) {
                System.out.println("No eligible donors found.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        showEligibleDonors();
    }
}

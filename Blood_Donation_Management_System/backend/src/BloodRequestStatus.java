
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class BloodRequestStatus {

    static Scanner sc = new Scanner(System.in);

    public static void processRequest() {

        Connection con = null;

        try {
            System.out.println("\n--- BLOOD REQUEST APPROVAL ---");

            System.out.print("Enter Request ID: ");
            int requestId = Integer.parseInt(sc.nextLine());

            con = DBConnection.getConnection();

            if (con == null) {
                System.out.println("Database connection unavailable.");
                return;
            }

            con.setAutoCommit(false);

            String requestSQL =
                "SELECT blood_group, units_required, status " +
                "FROM blood_requests WHERE request_id = ? FOR UPDATE";

            String bloodGroup;
            int units;
            String status;

            try (PreparedStatement ps = con.prepareStatement(requestSQL)) {

                ps.setInt(1, requestId);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new Exception("Request ID not found!");
                    }

                    bloodGroup = rs.getString("blood_group");
                    units = rs.getInt("units_required");
                    status = rs.getString("status");
                }
            }

            if (!status.equalsIgnoreCase("Pending")) {
                throw new Exception("Request is already processed!");
            }

            String stockSQL =
                "SELECT available_units FROM blood_stock " +
                "WHERE blood_group = ? FOR UPDATE";

            int availableUnits;

            try (PreparedStatement ps = con.prepareStatement(stockSQL)) {

                ps.setString(1, bloodGroup);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        throw new Exception("Blood group not found!");
                    }

                    availableUnits = rs.getInt("available_units");
                }
            }

            if (availableUnits < units) {
                throw new Exception("Insufficient blood stock!");
            }

            String updateStock =
                "UPDATE blood_stock " +
                "SET available_units = available_units - ? " +
                "WHERE blood_group = ?";

            try (PreparedStatement ps = con.prepareStatement(updateStock)) {
                ps.setInt(1, units);
                ps.setString(2, bloodGroup);
                ps.executeUpdate();
            }

            String updateRequest =
                "UPDATE blood_requests SET status = 'Approved' " +
                "WHERE request_id = ? AND status = 'Pending'";

            try (PreparedStatement ps = con.prepareStatement(updateRequest)) {
                ps.setInt(1, requestId);

                if (ps.executeUpdate() != 1) {
                    throw new Exception("Request status update failed!");
                }
            }

            con.commit();

            System.out.println("\nRequest Approved Successfully!");
            System.out.println("Blood Stock Updated Successfully!");

        } catch (Exception e) {

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            System.out.println("Request Processing Failed: " + e.getMessage());

        } finally {

            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        processRequest();
    }
}

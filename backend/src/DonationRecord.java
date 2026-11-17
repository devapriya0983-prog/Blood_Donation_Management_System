
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.Scanner;

public class DonationRecord {

    static Scanner sc = new Scanner(System.in);

    public static void recordDonation() {

        Connection con = null;

        try {
            System.out.println("\n--- BLOOD DONATION RECORD ---");

            System.out.print("Enter Donor ID: ");
            int donorId = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Donation Date (YYYY-MM-DD): ");
            Date donationDate = Date.valueOf(sc.nextLine());

            System.out.print("Enter Units Donated: ");
            int units = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Hospital Name: ");
            String hospital = sc.nextLine();

            System.out.print("Enter Status (Completed/Pending): ");
            String status = sc.nextLine();

            if (units <= 0 ||
                !(status.equalsIgnoreCase("Completed") ||
                  status.equalsIgnoreCase("Pending"))) {

                System.out.println("Invalid donation details!");
                return;
            }

            con = DBConnection.getConnection();

            if (con == null) {
                System.out.println("Database connection unavailable.");
                return;
            }

            con.setAutoCommit(false);

            String bloodGroup = null;

            String donorSQL =
                "SELECT blood_group FROM donors WHERE donor_id = ?";

            try (PreparedStatement ps = con.prepareStatement(donorSQL)) {
                ps.setInt(1, donorId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        bloodGroup = rs.getString("blood_group");
                    } else {
                        throw new Exception("Donor ID not found!");
                    }
                }
            }

            String insertSQL =
                "INSERT INTO donations " +
                "(donor_id, donation_date, units_donated, hospital_name, status) " +
                "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement ps = con.prepareStatement(insertSQL)) {
                ps.setInt(1, donorId);
                ps.setDate(2, donationDate);
                ps.setInt(3, units);
                ps.setString(4, hospital);
                ps.setString(5, status);

                ps.executeUpdate();
            }

            if (status.equalsIgnoreCase("Completed")) {

                String stockSQL =
                    "UPDATE blood_stock " +
                    "SET available_units = available_units + ? " +
                    "WHERE blood_group = ?";

                try (PreparedStatement ps = con.prepareStatement(stockSQL)) {
                    ps.setInt(1, units);
                    ps.setString(2, bloodGroup);

                    int updated = ps.executeUpdate();

                    if (updated == 0) {
                        throw new Exception("Blood group not found in stock!");
                    }
                }
            }

            con.commit();

            System.out.println("\nDonation Record Saved Successfully!");

            if (status.equalsIgnoreCase("Completed")) {
                System.out.println("Blood Stock Updated Successfully!");
            } else {
                System.out.println("Stock will update after donation completion.");
            }

        } catch (Exception e) {

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            System.out.println("Error: " + e.getMessage());

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
        recordDonation();
    }
}

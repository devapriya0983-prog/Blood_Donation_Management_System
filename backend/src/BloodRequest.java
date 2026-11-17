
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.Scanner;

public class BloodRequest {

    static Scanner sc = new Scanner(System.in);

    public static void createRequest() {

        Connection con = null;

        try {
            System.out.println("\n--- HOSPITAL BLOOD REQUEST ---");

            System.out.print("Enter Patient Name: ");
            String patientName = sc.nextLine();

            System.out.print("Enter Blood Group: ");
            String bloodGroup = sc.nextLine().toUpperCase();

            System.out.print("Enter Units Required: ");
            int units = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Hospital Name: ");
            String hospital = sc.nextLine();

            System.out.print("Enter City: ");
            String city = sc.nextLine();

            System.out.print("Enter Contact Number: ");
            String contact = sc.nextLine();

            System.out.print("Enter Request Date (YYYY-MM-DD): ");
            Date requestDate = Date.valueOf(sc.nextLine());

            String[] groups = {
                "A+", "A-", "B+", "B-",
                "O+", "O-", "AB+", "AB-"
            };

            boolean validGroup = false;

            for (String group : groups) {
                if (group.equals(bloodGroup)) {
                    validGroup = true;
                    break;
                }
            }

            if (!validGroup || units <= 0 ||
                patientName.trim().isEmpty()) {
                System.out.println("Invalid request details!");
                return;
            }

            con = DBConnection.getConnection();

            if (con == null) {
                System.out.println("Database connection unavailable.");
                return;
            }

            String sql =
                "INSERT INTO blood_requests " +
                "(patient_name, blood_group, units_required, " +
                "hospital_name, city, contact_number, request_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 'Pending')";

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, patientName);
                ps.setString(2, bloodGroup);
                ps.setInt(3, units);
                ps.setString(4, hospital);
                ps.setString(5, city);
                ps.setString(6, contact);
                ps.setDate(7, requestDate);

                ps.executeUpdate();
            }

            System.out.println("\nBlood Request Submitted Successfully!");
            System.out.println("Request Status: Pending");

        } catch (Exception e) {
            System.out.println("Request Failed: " + e.getMessage());

        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        createRequest();
    }
}


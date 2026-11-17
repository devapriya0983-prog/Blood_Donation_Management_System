
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import java.util.Base64;

public class DonorRegistration {

    static Scanner sc = new Scanner(System.in);

    // Secure password hashing
    public static String hashPassword(String password)
            throws Exception {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(), salt, 65536, 256
        );

        byte[] hash = SecretKeyFactory
            .getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .getEncoded();

        spec.clearPassword();

        return Base64.getEncoder().encodeToString(salt)
            + ":" +
            Base64.getEncoder().encodeToString(hash);
    }

    // Verify password during login
    public static boolean verifyPassword(
            String password, String storedPassword) throws Exception {

        String[] parts = storedPassword.split(":");

        if (parts.length != 2) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] storedHash = Base64.getDecoder().decode(parts[1]);

        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(), salt, 65536, 256
        );

        byte[] enteredHash = SecretKeyFactory
            .getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .getEncoded();

        spec.clearPassword();

        return java.security.MessageDigest.isEqual(
            storedHash, enteredHash
        );
    }

    public static void registerDonor() {

        Connection con = null;

        try {
            System.out.println("\n--- DONOR REGISTRATION ---");

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Email: ");
            String email = sc.nextLine();

            System.out.print("Enter Password: ");
            String password = sc.nextLine();

            System.out.print("Enter Phone: ");
            String phone = sc.nextLine();

            System.out.print("Enter Age: ");
            int age = Integer.parseInt(sc.nextLine());

            System.out.print("Enter Gender: ");
            String gender = sc.nextLine();

            System.out.print("Enter Blood Group: ");
            String bloodGroup = sc.nextLine().toUpperCase();

            System.out.print("Enter Address: ");
            String address = sc.nextLine();

            System.out.print("Enter City: ");
            String city = sc.nextLine();

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

            if (!validGroup || age <= 0 || password.isEmpty()) {
                System.out.println("Invalid registration details!");
                return;
            }

            String hashedPassword = hashPassword(password);

            con = DBConnection.getConnection();

            if (con == null) {
                System.out.println("Database connection unavailable.");
                return;
            }

            con.setAutoCommit(false);

            String userSQL =
                "INSERT INTO users (name, email, password, phone, role) " +
                "VALUES (?, ?, ?, ?, ?)";

            int userId;

            try (PreparedStatement ps = con.prepareStatement(
                    userSQL, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, hashedPassword);
                ps.setString(4, phone);
                ps.setString(5, "donor");

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new Exception("User ID not generated.");
                    }
                    userId = rs.getInt(1);
                }
            }

            String donorSQL =
                "INSERT INTO donors " +
                "(user_id, blood_group, age, gender, address, city, availability) " +
                "VALUES (?, ?, ?, ?, ?, ?, 1)";

            try (PreparedStatement ps = con.prepareStatement(donorSQL)) {

                ps.setInt(1, userId);
                ps.setString(2, bloodGroup);
                ps.setInt(3, age);
                ps.setString(4, gender);
                ps.setString(5, address);
                ps.setString(6, city);

                ps.executeUpdate();
            }

            con.commit();

            System.out.println("\nDonor Registered Successfully!");
            System.out.println("Donor User ID: " + userId);

        } catch (Exception e) {

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception rollbackError) {
                rollbackError.printStackTrace();
            }

            System.out.println("Registration Failed: " + e.getMessage());

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

    public static String registerDonorFromWeb(
            String name, String email, String password,
            String phone, int age, String gender,
            String bloodGroup, String address, String city) {

        Connection con = null;

        try {
            name = name.trim();
            email = email.trim();
            phone = phone.trim();
            bloodGroup = bloodGroup.trim().toUpperCase();

            if (name.isEmpty() || email.isEmpty() ||
                phone.isEmpty() || password.isEmpty() ||
                address.isEmpty() || city.isEmpty() ||
                age < 18 || age > 100) {
                return "Invalid registration details!";
            }

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

            if (!validGroup) {
                return "Invalid blood group!";
            }

            String hashedPassword = hashPassword(password);

            con = DBConnection.getConnection();

            if (con == null) {
                return "Database connection unavailable!";
            }

            con.setAutoCommit(false);

            String userSQL =
                "INSERT INTO users (name, email, password, phone, role) " +
                "VALUES (?, ?, ?, ?, ?)";

            int userId;

            try (PreparedStatement ps = con.prepareStatement(
                    userSQL, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, hashedPassword);
                ps.setString(4, phone);
                ps.setString(5, "donor");

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new Exception("User ID not generated.");
                    }
                    userId = rs.getInt(1);
                }
            }

            String donorSQL =
                "INSERT INTO donors " +
                "(user_id, blood_group, age, gender, address, city, availability) " +
                "VALUES (?, ?, ?, ?, ?, ?, 1)";

            try (PreparedStatement ps = con.prepareStatement(donorSQL)) {
                ps.setInt(1, userId);
                ps.setString(2, bloodGroup);
                ps.setInt(3, age);
                ps.setString(4, gender);
                ps.setString(5, address);
                ps.setString(6, city);
                ps.executeUpdate();
            }

            con.commit();

            return "Donor Registered Successfully!";

        } catch (Exception e) {

            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception rollbackError) {
                rollbackError.printStackTrace();
            }

            return "Registration Failed: " + e.getMessage();

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
        registerDonor();
    }
}


import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WebServer {

    // LOGIN SESSION STORAGE
    private static final ConcurrentMap<String, String> sessions =
        new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8081), 0
        );

        // DONOR REGISTRATION API
        server.createContext("/api/register", exchange -> {

            System.out.println("Registration request received!");

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            try {
                Map<String, String> data = readFormData(exchange);

                int age = Integer.parseInt(data.get("age"));

                String result = DonorRegistration.registerDonorFromWeb(
                    data.get("name"),
                    data.get("email"),
                    data.get("password"),
                    data.get("phone"),
                    age,
                    data.get("gender"),
                    data.get("blood_group"),
                    data.get("address"),
                    data.get("city")
                );

                System.out.println("Registration result: " + result);

                int status = result.startsWith(
                    "Donor Registered Successfully"
                ) ? 200 : 400;

                sendResponse(exchange, status, result);

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 400, "Registration Failed");
            }
        }); 
        // LOGIN API WITH SESSION
        server.createContext("/api/login", exchange -> {

            System.out.println("Login request received!");

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            try {
                Map<String, String> data = readFormData(exchange);

                String email = data.get("email");
                String password = data.get("password");

                if (email == null || password == null ||
                    email.trim().isEmpty() || password.isEmpty()) {

                    sendResponse(
                        exchange, 400, "Email and password required"
                    );
                    return;
                }

                String sql =
                    "SELECT user_id, password FROM users WHERE email = ?";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setString(1, email.trim());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {

                            String storedPassword =
                                rs.getString("password");

                            boolean valid =
                                DonorRegistration.verifyPassword(
                                    password, storedPassword
                                );

                            if (valid) {

                                String userId =
                                    rs.getString("user_id");

                                String token =
                                    UUID.randomUUID().toString();

                                sessions.put(token, userId);

                                exchange.getResponseHeaders().add(
                                    "Set-Cookie",
                                    "LIFESAVER_SESSION=" + token +
                                    "; Path=/; HttpOnly; SameSite=Strict"
                                );

                                System.out.println("Login successful!");

                                sendResponse(
                                    exchange, 200, "Login Successful"
                                );

                            } else {
                                sendResponse(
                                    exchange, 401,
                                    "Invalid email or password"
                                );
                            }

                        } else {
                            sendResponse(
                                exchange, 401,
                                "Invalid email or password"
                            );
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(
                    exchange, 500, "Login failed due to server error"
                );
            }
        });

// ADMIN ACCESS CHECK API
server.createContext("/api/admin/check", exchange -> {

    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
        sendResponse(exchange, 405, "Method Not Allowed");
        return;
    }

    try {
        String userId = getLoggedInUser(exchange);

        if (userId == null) {
            sendResponse(exchange, 401, "Please login first");
            return;
        }

        String sql = "SELECT role FROM users WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String role = rs.getString("role");

                    if ("admin".equalsIgnoreCase(role)) {
                        sendResponse(exchange, 200, "Admin Access Granted");
                    } else {
                        sendResponse(exchange, 403, "Access Denied");
                    }

                } else {
                    sendResponse(exchange, 404, "User not found");
                }
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
        sendResponse(exchange, 500, "Admin verification failed");
    }
});


// ADMIN VIEW ALL BLOOD REQUESTS API
server.createContext("/api/admin/requests", exchange -> {

    if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
        sendResponse(exchange, 405, "Method Not Allowed");
        return;
    }

    try {
        String userId = getLoggedInUser(exchange);

        if (userId == null) {
            sendResponse(exchange, 401, "Please login first");
            return;
        }

        // Verify admin role
        String roleSql = "SELECT role FROM users WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement rolePs = con.prepareStatement(roleSql)) {

            rolePs.setString(1, userId);

            try (ResultSet roleRs = rolePs.executeQuery()) {

                if (!roleRs.next() ||
                    !"admin".equalsIgnoreCase(roleRs.getString("role"))) {

                    sendResponse(exchange, 403, "Admin access only");
                    return;
                }
            }

            // Fetch all blood requests
            String sql =
                "SELECT request_id, patient_name, blood_group, " +
                "units_required, hospital_name, city, contact_number, " +
                "request_date, status " +
                "FROM blood_requests ORDER BY request_id DESC";

            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                StringBuilder json = new StringBuilder("[");
                boolean first = true;

                while (rs.next()) {

                    if (!first) {
                        json.append(",");
                    }

                    first = false;

                    java.sql.Date requestDate =
                        rs.getDate("request_date");

                    json.append("{")
                        .append("\"request_id\":")
                        .append(rs.getInt("request_id")).append(",")
                        .append("\"patient_name\":\"")
                        .append(jsonEscape(rs.getString("patient_name")))
                        .append("\",")
                        .append("\"blood_group\":\"")
                        .append(jsonEscape(rs.getString("blood_group")))
                        .append("\",")
                        .append("\"units_required\":")
                        .append(rs.getInt("units_required")).append(",")
                        .append("\"hospital_name\":\"")
                        .append(jsonEscape(rs.getString("hospital_name")))
                        .append("\",")
                        .append("\"city\":\"")
                        .append(jsonEscape(rs.getString("city")))
                        .append("\",")
                        .append("\"contact_number\":\"")
                        .append(jsonEscape(rs.getString("contact_number")))
                        .append("\",")
                        .append("\"request_date\":\"")
                        .append(requestDate == null ? "" : requestDate.toString())
                        .append("\",")
                        .append("\"status\":\"")
                        .append(jsonEscape(rs.getString("status")))
                        .append("\"")
                        .append("}");
                }

                json.append("]");
                sendJsonResponse(exchange, 200, json.toString());
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
        sendResponse(exchange, 500, "Unable to load blood requests");
    }
});

  
        // ADMIN APPROVE / REJECT BLOOD REQUEST API
        server.createContext("/api/admin/request-status", exchange -> {

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            try {
                String userId = getLoggedInUser(exchange);

                if (userId == null) {
                    sendResponse(exchange, 401, "Please login first");
                    return;
                }

                Map<String, String> data = readFormData(exchange);
                String requestIdText = data.get("request_id");
                String status = data.get("status");

                if (requestIdText == null || requestIdText.trim().isEmpty() ||
                    (!"Approved".equals(status) && !"Rejected".equals(status))) {
                    sendResponse(exchange, 400, "Invalid request details");
                    return;
                }

                int requestId = Integer.parseInt(requestIdText.trim());

                if (requestId <= 0) {
                    sendResponse(exchange, 400, "Invalid request ID");
                    return;
                }

                try (Connection con = DBConnection.getConnection()) {

                    // VERIFY ADMIN
                    String roleSql = "SELECT role FROM users WHERE user_id = ?";

                    try (PreparedStatement ps = con.prepareStatement(roleSql)) {
                        ps.setInt(1, Integer.parseInt(userId));

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next() ||
                                !"admin".equalsIgnoreCase(rs.getString("role"))) {
                                sendResponse(exchange, 403, "Admin access only");
                                return;
                            }
                        }
                    }

                    // START TRANSACTION
                    con.setAutoCommit(false);

                    try {
                        // LOCK REQUEST
                        String requestSql =
                            "SELECT blood_group, units_required, status " +
                            "FROM blood_requests WHERE request_id = ? FOR UPDATE";

                        String bloodGroup;
                        int units;
                        String currentStatus;

                        try (PreparedStatement ps = con.prepareStatement(requestSql)) {
                            ps.setInt(1, requestId);

                            try (ResultSet rs = ps.executeQuery()) {
                                if (!rs.next()) {
                                    con.rollback();
                                    sendResponse(exchange, 404, "Blood request not found");
                                    return;
                                }

                                bloodGroup = rs.getString("blood_group");
                                units = rs.getInt("units_required");
                                currentStatus = rs.getString("status");
                            }
                        }

                        if (!"Pending".equalsIgnoreCase(currentStatus)) {
                            con.rollback();
                            sendResponse(exchange, 400, "This request is already processed");
                            return;
                        }

                        // DEDUCT STOCK ONLY FOR APPROVAL
                        if ("Approved".equals(status)) {

                            String stockSql =
                                "SELECT available_units FROM blood_stock " +
                                "WHERE blood_group = ? FOR UPDATE";

                            int availableUnits;

                            try (PreparedStatement ps = con.prepareStatement(stockSql)) {
                                ps.setString(1, bloodGroup);

                                try (ResultSet rs = ps.executeQuery()) {
                                    if (!rs.next()) {
                                        con.rollback();
                                        sendResponse(exchange, 400, "Blood group stock not found");
                                        return;
                                    }

                                    availableUnits = rs.getInt("available_units");
                                }
                            }

                            if (availableUnits < units) {
                                con.rollback();
                                sendResponse(exchange, 400,
                                    "Insufficient blood stock. Available: " + availableUnits);
                                return;
                            }

                            String deductSql =
                                "UPDATE blood_stock " +
                                "SET available_units = available_units - ?, " +
                                "last_updated = CURRENT_TIMESTAMP " +
                                "WHERE blood_group = ?";

                            try (PreparedStatement ps = con.prepareStatement(deductSql)) {
                                ps.setInt(1, units);
                                ps.setString(2, bloodGroup);
                                ps.executeUpdate();
                            }
                        }

                        // UPDATE REQUEST STATUS
                        String updateSql =
                            "UPDATE blood_requests SET status = ? " +
                            "WHERE request_id = ? AND status = 'Pending'";

                        try (PreparedStatement ps = con.prepareStatement(updateSql)) {
                            ps.setString(1, status);
                            ps.setInt(2, requestId);

                            if (ps.executeUpdate() != 1) {
                                throw new Exception("Request status update failed");
                            }
                        }

                        // COMMIT BOTH CHANGES
                        con.commit();

                        sendResponse(exchange, 200, "Request " + status + " successfully");

                    } catch (Exception e) {
                        con.rollback();
                        throw e;
                    }
                }

            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Invalid request ID");
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Unable to update request status");
            }
        }); 

        // ADMIN VIEW BLOOD STOCK API
        server.createContext("/api/admin/stock", exchange -> {

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            try {
                String userId = getLoggedInUser(exchange);

                if (userId == null) {
                    sendResponse(exchange, 401, "Please login first");
                    return;
                }

                String roleSql = "SELECT role FROM users WHERE user_id = ?";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement rolePs = con.prepareStatement(roleSql)) {

                    rolePs.setInt(1, Integer.parseInt(userId));

                    try (ResultSet roleRs = rolePs.executeQuery()) {

                        if (!roleRs.next() ||
                            !"admin".equalsIgnoreCase(roleRs.getString("role"))) {
                            sendResponse(exchange, 403, "Admin access only");
                            return;
                        }
                    }

                    String sql =
                        "SELECT blood_group, available_units " +
                        "FROM blood_stock ORDER BY blood_group";

                    try (PreparedStatement ps = con.prepareStatement(sql);
                         ResultSet rs = ps.executeQuery()) {

                        StringBuilder json = new StringBuilder("[");
                        boolean first = true;

                        while (rs.next()) {

                            if (!first) {
                                json.append(",");
                            }

                            first = false;

                            json.append("{")
                                .append("\"blood_group\":\"")
                                .append(jsonEscape(rs.getString("blood_group")))
                                .append("\",")
                                .append("\"available_units\":")
                                .append(rs.getInt("available_units"))
                                .append("}");
                        }

                        json.append("]");

                        sendJsonResponse(exchange, 200, json.toString());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "Unable to load blood stock");
            }
        });



// ADMIN UPDATE BLOOD STOCK API
server.createContext("/api/admin/update-stock", exchange -> {

    if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
        sendResponse(exchange, 405, "Method Not Allowed");
        return;
    }

    try {
        String userId = getLoggedInUser(exchange);

        if (userId == null) {
            sendResponse(exchange, 401, "Please login first");
            return;
        }

        Map<String, String> data = readFormData(exchange);

        String bloodGroup = data.get("blood_group");
        String unitsText = data.get("available_units");

        int units;

        try {
            units = Integer.parseInt(unitsText);
        } catch (Exception e) {
            sendResponse(exchange, 400, "Invalid units");
            return;
        }

        if (units < 0 || bloodGroup == null) {
            sendResponse(exchange, 400, "Invalid stock details");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            String roleSql = "SELECT role FROM users WHERE user_id = ?";

            try (PreparedStatement rolePs = con.prepareStatement(roleSql)) {
                rolePs.setInt(1, Integer.parseInt(userId));

                try (ResultSet roleRs = rolePs.executeQuery()) {
                    if (!roleRs.next() ||
                        !"admin".equalsIgnoreCase(roleRs.getString("role"))) {
                        sendResponse(exchange, 403, "Admin access only");
                        return;
                    }
                }
            }

            String sql =
                "UPDATE blood_stock SET available_units = ?, " +
                "last_updated = CURRENT_TIMESTAMP WHERE blood_group = ?";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, units);
                ps.setString(2, bloodGroup);

                int updated = ps.executeUpdate();

                if (updated == 0) {
                    sendResponse(exchange, 404, "Blood group not found");
                    return;
                }
            }
        }

        sendResponse(exchange, 200, "Blood stock updated successfully!");

    } catch (Exception e) {
        e.printStackTrace();
        sendResponse(exchange, 500, "Unable to update blood stock");
    }
});
        // DONOR PROFILE API
        server.createContext("/api/profile", exchange -> {

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            try {
                String userId = getLoggedInUser(exchange);
                System.out.println("History User ID: " + userId);
                System.out.println("Blood Request User ID: " + userId);

                if (userId == null) {
                    sendResponse(exchange, 401, "Please login first");
                    return;
                }

                String sql =
                    "SELECT u.name, u.email, u.phone, " +
                    "d.blood_group, d.age, d.gender, " +
                    "d.address, d.city " +
                    "FROM users u " +
                    "JOIN donors d ON u.user_id = d.user_id " +
                    "WHERE u.user_id = ?";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setString(1, userId);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {

                            String json = "{"
                                + "\"name\":\"" +
                                jsonEscape(rs.getString("name")) + "\","
                                + "\"email\":\"" +
                                jsonEscape(rs.getString("email")) + "\","
                                + "\"phone\":\"" +
                                jsonEscape(rs.getString("phone")) + "\","
                                + "\"blood_group\":\"" +
                                jsonEscape(rs.getString("blood_group")) + "\","
                                + "\"age\":" + rs.getInt("age") + ","
                                + "\"gender\":\"" +
                                jsonEscape(rs.getString("gender")) + "\","
                                + "\"address\":\"" +
                                jsonEscape(rs.getString("address")) + "\","
                                + "\"city\":\"" +
                                jsonEscape(rs.getString("city")) + "\""
                                + "}";

                            sendJsonResponse(exchange, 200, json);

                        } else {
                            sendResponse(
                                exchange, 404, "Donor profile not found"
                            );
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(
                    exchange, 500, "Unable to load donor profile"
                );
            }
        });

        // FIND AVAILABLE BLOOD DONORS API
        server.createContext("/api/donors", exchange -> {

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            try {
                String userId = getLoggedInUser(exchange);

                if (userId == null) {
                    sendJsonResponse(
                        exchange, 401,
                        "{\"message\":\"Please login first\"}"
                    );
                    return;
                }

                Map<String, String> filters = new HashMap<>();

                String query = exchange.getRequestURI().getRawQuery();

                if (query != null) {
                    for (String pair : query.split("&")) {

                        String[] parts = pair.split("=", 2);

                        String key = URLDecoder.decode(
                            parts[0], StandardCharsets.UTF_8
                        );

                        String value = parts.length > 1
                            ? URLDecoder.decode(
                                parts[1], StandardCharsets.UTF_8
                            )
                            : "";

                        filters.put(key, value);
                    }
                }

                String bloodGroup = filters.getOrDefault(
                    "blood_group", ""
                ).trim();

                String city = filters.getOrDefault(
                    "city", ""
                ).trim();

                String sql =
                    "SELECT u.name, d.blood_group, d.city " +
                    "FROM donors d " +
                    "JOIN users u ON d.user_id = u.user_id " +
                    "WHERE d.availability = 1 " +
                    "AND (? = '' OR d.blood_group = ?) " +
                    "AND (? = '' OR LOWER(d.city) = LOWER(?)) " +
                    "ORDER BY u.name";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setString(1, bloodGroup);
                    ps.setString(2, bloodGroup);
                    ps.setString(3, city);
                    ps.setString(4, city);

                    try (ResultSet rs = ps.executeQuery()) {

                        StringBuilder json = new StringBuilder("[");
                        boolean first = true;

                        while (rs.next()) {

                            if (!first) {
                                json.append(",");
                            }

                            json.append("{")
                                .append("\"name\":\"")
                                .append(jsonEscape(rs.getString("name")))
                                .append("\",")
                                .append("\"blood_group\":\"")
                                .append(jsonEscape(
                                    rs.getString("blood_group")
                                ))
                                .append("\",")
                                .append("\"city\":\"")
                                .append(jsonEscape(rs.getString("city")))
                                .append("\"}");

                            first = false;
                        }

                        json.append("]");

                        sendJsonResponse(
                            exchange, 200, json.toString()
                        );
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();

                sendJsonResponse(
                    exchange, 500,
                    "{\"message\":\"Unable to search donors\"}"
                );
            }
        });

       
        // BLOOD REQUEST API
        server.createContext("/api/blood-request", exchange -> {

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            try {
                // Check login session
                String userId = getLoggedInUser(exchange);

                if (userId == null) {
                    sendResponse(exchange, 401, "Please login first");
                    return;
                }

                Map<String, String> data = readFormData(exchange);

                String patientName = data.get("patient_name");
                String bloodGroup = data.get("blood_group");
                String hospital = data.get("hospital");
                String city = data.get("city");
                String contact = data.get("contact");
                String unitsText = data.get("units");
                String requiredDate = data.get("required_date");

                // Validate empty fields
                if (patientName == null || bloodGroup == null ||
                    hospital == null || city == null ||
                    contact == null || unitsText == null ||
                    requiredDate == null ||
                    patientName.trim().isEmpty() ||
                    bloodGroup.trim().isEmpty() ||
                    hospital.trim().isEmpty() ||
                    city.trim().isEmpty() ||
                    contact.trim().isEmpty() ||
                    unitsText.trim().isEmpty() ||
                    requiredDate.trim().isEmpty()) {

                    sendResponse(exchange, 400,
                        "Please fill all required fields");
                    return;
                }

                // Validate blood group
                String[] groups = {
                    "A+", "A-", "B+", "B-",
                    "O+", "O-", "AB+", "AB-"
                };

                boolean validGroup = false;

                for (String group : groups) {
                    if (group.equals(bloodGroup.trim())) {
                        validGroup = true;
                        break;
                    }
                }

                if (!validGroup) {
                    sendResponse(exchange, 400, "Invalid blood group");
                    return;
                }

                // Validate units
                int units = Integer.parseInt(unitsText.trim());

                if (units < 1 || units > 20) {
                    sendResponse(exchange, 400,
                        "Units must be between 1 and 20");
                    return;
                }

                // Validate contact number
                if (!contact.trim().matches("[0-9]{10}")) {
                    sendResponse(exchange, 400,
                        "Enter a valid 10-digit contact number");
                    return;
                }

                // Validate request date
                java.time.LocalDate date =
                    java.time.LocalDate.parse(requiredDate);

                if (date.isBefore(java.time.LocalDate.now())) {
                    sendResponse(exchange, 400,
                        "Past date is not allowed");
                    return;
                }

                // INSERT request with logged-in user ID
                String sql =
                    "INSERT INTO blood_requests " +
                    "(user_id, patient_name, blood_group, units_required, " +
                    "hospital_name, city, contact_number, request_date, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Pending')";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, Integer.parseInt(userId));
                    ps.setString(2, patientName.trim());
                    ps.setString(3, bloodGroup.trim());
                    ps.setInt(4, units);
                    ps.setString(5, hospital.trim());
                    ps.setString(6, city.trim());
                    ps.setString(7, contact.trim());
                    ps.setDate(8, java.sql.Date.valueOf(date));

                    int result = ps.executeUpdate();

                    if (result > 0) {
                        System.out.println(
                            "Blood Request Saved Successfully for User ID: "
                            + userId
                        );

                        sendResponse(exchange, 200,
                            "Blood Request Submitted Successfully! Status: Pending");
                    } else {
                        sendResponse(exchange, 500,
                            "Request could not be saved");
                    }
                }

            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Invalid units or user ID");
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500,
                    "Blood request failed. Please try again.");
            }
        });

        // MY BLOOD REQUEST HISTORY API
        server.createContext("/api/my-blood-requests", exchange -> {

            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            try {
                String userId = getLoggedInUser(exchange);

                if (userId == null) {
                    sendJsonResponse(exchange, 401,
                        "{\"message\":\"Please login first\"}");
                    return;
                }

                String sql =
                    "SELECT request_id, patient_name, blood_group, " +
                    "units_required, hospital_name, city, " +
                    "request_date, status " +
                    "FROM blood_requests " +
                    "WHERE user_id = ? " +
                    "ORDER BY request_id DESC";

                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, Integer.parseInt(userId));

                    try (ResultSet rs = ps.executeQuery()) {

                        StringBuilder json = new StringBuilder("[");
                        boolean first = true;

                        while (rs.next()) {

                            if (!first) {
                                json.append(",");
                            }

                            json.append("{")
                                .append("\"request_id\":")
                                .append(rs.getInt("request_id"))
                                .append(",")
                                .append("\"patient_name\":\"")
                                .append(jsonEscape(rs.getString("patient_name")))
                                .append("\",")
                                .append("\"blood_group\":\"")
                                .append(jsonEscape(rs.getString("blood_group")))
                                .append("\",")
                                .append("\"units_required\":")
                                .append(rs.getInt("units_required"))
                                .append(",")
                                .append("\"hospital_name\":\"")
                                .append(jsonEscape(rs.getString("hospital_name")))
                                .append("\",")
                                .append("\"city\":\"")
                                .append(jsonEscape(rs.getString("city")))
                                .append("\",")
                                .append("\"request_date\":\"")
                                .append(rs.getDate("request_date") == null
                                    ? "" : rs.getDate("request_date").toString())
                                .append("\",")
                                .append("\"status\":\"")
                                .append(jsonEscape(rs.getString("status")))
                                .append("\"}");

                            first = false;
                        }

                        json.append("]");

                        sendJsonResponse(exchange, 200, json.toString());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                sendJsonResponse(exchange, 500,
                    "{\"message\":\"Unable to load request history\"}");
            }
        });

        // LOGOUT API
        server.createContext("/api/logout", exchange -> {

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            String token = getSessionToken(exchange);

            if (token != null) {
                sessions.remove(token);
            }

            exchange.getResponseHeaders().add(
                "Set-Cookie",
                "LIFESAVER_SESSION=; Path=/; HttpOnly; " +
                "SameSite=Strict; Max-Age=0"
            );

            sendResponse(exchange, 200, "Logged out successfully");
        });

        // FRONTEND FILE SERVER
        Path frontend = Paths.get(
            "Blood_Donation_Management_System",
            "frontend"
        ).toAbsolutePath().normalize();

        server.createContext("/", exchange -> {

            String requestPath = exchange.getRequestURI().getPath();

            if (requestPath.equals("/")) {
                requestPath = "/index.html";
            }

            Path file = frontend.resolve(
                requestPath.substring(1)
            ).normalize();

            if (!file.startsWith(frontend) ||
                !Files.exists(file) ||
                Files.isDirectory(file)) {

                sendResponse(exchange, 404, "Page Not Found");
                return;
            }

            String contentType = "text/html; charset=UTF-8";

            if (requestPath.endsWith(".css")) {
                contentType = "text/css; charset=UTF-8";
            } else if (requestPath.endsWith(".js")) {
                contentType = "application/javascript; charset=UTF-8";
            } else if (requestPath.endsWith(".png")) {
                contentType = "image/png";
            } else if (requestPath.endsWith(".jpg") ||
                       requestPath.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            }

            byte[] fileData = Files.readAllBytes(file);

            exchange.getResponseHeaders().set(
                "Content-Type", contentType
            );

            exchange.sendResponseHeaders(200, fileData.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileData);
            }
        });

        server.start();

        System.out.println("LifeSaver Website Server Started!");
        System.out.println("Open: http://localhost:8081");
    }

    // GET SESSION TOKEN
    private static String getSessionToken(HttpExchange exchange) {

        String cookie = exchange.getRequestHeaders().getFirst("Cookie");

        if (cookie == null) {
            return null;
        }

        for (String part : cookie.split(";")) {

            String item = part.trim();

            if (item.startsWith("LIFESAVER_SESSION=")) {
                return item.substring(
                    "LIFESAVER_SESSION=".length()
                );
            }
        }

        return null;
    }

    // GET LOGGED-IN USER
    private static String getLoggedInUser(HttpExchange exchange) {

        String token = getSessionToken(exchange);

        if (token == null) {
            return null;
        }

        return sessions.get(token);
    }

    // JSON ESCAPE
    private static String jsonEscape(String value) {

        if (value == null) {
            return "";
        }

        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    // READ FORM DATA
    public static Map<String, String> readFormData(
        HttpExchange exchange
    ) throws IOException {

        String formData = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        Map<String, String> data = new HashMap<>();

        for (String pair : formData.split("&")) {

            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(
                parts[0], StandardCharsets.UTF_8
            );

            String value = parts.length > 1
                ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)
                : "";

            data.put(key, value);
        }

        return data;
    }

    // SEND TEXT RESPONSE
    public static void sendResponse(
        HttpExchange exchange,
        int status,
        String message
    ) throws IOException {

        byte[] data = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
            "Content-Type", "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, data.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(data);
        }
    }

    // SEND JSON RESPONSE
    public static void sendJsonResponse(
        HttpExchange exchange,
        int status,
        String json
    ) throws IOException {

        byte[] data = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
            "Content-Type", "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, data.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(data);
        }
    }
}

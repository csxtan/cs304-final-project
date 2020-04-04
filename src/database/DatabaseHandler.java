package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.LinkedList;
import java.util.Scanner;

public class DatabaseHandler {
    private static final String ORACLE_URL = "jdbc:oracle:thin:@dbhost.students.cs.ubc.ca:1522:stu";
    private static final String USERNAME = "ora_csxtan";
    private static final String PASSWORD = "a57195142";
    private static final String SETUP_FILENAME = "setup.sql";

    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private static final String WARNING_TAG = "[WARNING]";

    private Connection connection = null;

    public DatabaseHandler() {
        try {
            // Load the Oracle JDBC driver
            // Note that the path could change for new drivers
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            login();
            System.out.println("Connected to Oracle.");
            clearDatabase();
            System.out.println("Existing database cleared.");
            loadDatabase();
            System.out.println("Database loaded.");
        } catch (SQLException e) {
            handleException(e);
        }
    }

    private void login() throws SQLException {
        if (connection != null) {
            connection.close();
        }

        connection = DriverManager.getConnection(ORACLE_URL, USERNAME, PASSWORD);
        connection.setAutoCommit(false);
    }

    private void loadDatabase() throws SQLException {
        File f = new File(SETUP_FILENAME);
        Scanner scanner;
        try {
            scanner = new Scanner(f).useDelimiter(";");
            Statement s;
            while (scanner.hasNext()) {
                String sql = scanner.next().replace("\n"," ");
                s = connection.createStatement();
                s.executeUpdate(sql);
                s.close();
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File " + SETUP_FILENAME + " not found. Make sure it is in the current directory.");
        }
    }

    private void clearDatabase() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String response;
        do {
            System.out.print("To load the database, all existing tables with be deleted. Continue? (y/n) ");
            response = scanner.nextLine();
        } while (!response.equals("y") && !response.equals("n"));
        if (response.equals("n")) {
            System.exit(1);
        }

        Statement s = connection.createStatement();
        ResultSet databaseTables = s.executeQuery(
                "SELECT 'DROP TABLE \"' || table_name || '\" CASCADE CONSTRAINTS' FROM user_tables");

        while (databaseTables.next()) {
            s = connection.createStatement();
            s.executeUpdate(databaseTables.getString(1));
        }
    }


    // Returns LinkedList of all table names
    public LinkedList<String> getTables() {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT table_name FROM user_tables"
            );
            ResultSet allTables = ps.executeQuery();

            LinkedList<String> tables = new LinkedList<>();
            while (allTables.next()) {
                tables.add(allTables.getString(1));
            }
            return tables;
        } catch (SQLException e) {
            handleException(e);
            return null;
        }
    }


    // SQL COMMANDS

    public ResultSet show(String table) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM " + table
        );
        return ps.executeQuery();
    }

    public ResultSet insert(String sender_id, String name, String address) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO Sender VALUES (?,?,?)"
        );
        ps.setString(1, sender_id);
        ps.setString(2, name);
        ps.setString(3, address);
        ps.executeUpdate();
        return show("Sender");
    }

    public ResultSet delete(String vehicle_id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM Vehicle WHERE vehicle_id = ?"
        );
        ps.setString(1, vehicle_id);
        ps.executeUpdate();
        return show("Vehicle");
    }

    public ResultSet update(String sender_id, String name, String address) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "UPDATE Sender " +
                        "SET name = ?, address = ? " +
                        "WHERE sender_id = ?"
        );
        ps.setString(3, sender_id);
        ps.setString(1, name);
        ps.setString(2, address);
        ps.executeUpdate();
        return show("Sender");
    }

    public ResultSet select(String weight) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT package_id,weight " +
                        "FROM Package " +
                        "WHERE weight >= ?"
        );
        ps.setString(1, weight);
        return ps.executeQuery();
    }

    public ResultSet project(String column) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT ? FROM TrackingInformation"
        );
        ps.setString(1, column);
        return ps.executeQuery();
    }

    public ResultSet join(String price) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT Package.package_id, PackagePricing.price FROM Package INNER JOIN PackagePricing " +
                        "ON Package.weight = PackagePricing.weight " +
                        "AND Package.size = PackagePricing.size " +
                        "WHERE PackagePricing.price >= ?"
        );
        ps.setString(1, price);
        return ps.executeQuery();
    }

    public ResultSet aggregate(String table) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT count(*) from " + table
        );
        return ps.executeQuery();
    }

    public ResultSet group() throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT employee_id, COUNT(vehicle_id) " +
                        "FROM Drives " +
                        "GROUP BY employee_id"
        );
        return ps.executeQuery();
    }

    public ResultSet divide() throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT employee_id " +
                        "FROM Drives " +
                        "GROUP BY employee_id " +
                        "HAVING COUNT(*) = (SELECT COUNT(*) FROM Vehicle)"
        );
        return ps.executeQuery();
    }


    // Exit on error
    private void handleException(Exception e) {
        System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        System.exit(0);
    }
}

package com.Services;

import com.Model.User;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Created by TJ on 24/4/18.
 */
public class DBStore implements UserStore {

    private static DBStore instance = new DBStore();
    private static final String DB_URL = "jdbc:sqlite:"+ASSETS_FOLDER+ "db.db";
    private static final String SELECT_ALL_USERS = "SELECT name, profile_picture, status, gender, age, state FROM People";
    private static final String SELECT_ALL_RELATIONS = "SELECT user_a, user_b, relation FROM Relations";
    private static final String INSERT_PERSON = "INSERT INTO People(name, profile_picture, status, gender, age, state)" +
            " VALUES (?, ?, ?, ?, ?, ?)";
    public static final String DELETE_PERSON = "DELETE FROM People where name = ?";
    public static final String UPDATE_PERSON = "UPDATE People SET profile_picture = ?, status = ?, state = ? WHERE name = ?";
    private static List<User> users = new ArrayList<>();
    private User selectedUser = null;


    public static DBStore getInstance() throws SQLException {
        if (users.isEmpty()) {
            connect();
        }
        return instance;
    }

    private static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void connect() throws SQLException {

        Connection conn = getConnection();
        Statement stmt = conn.createStatement();

        List<List<String>> userTable = getUserData(stmt);
        List<List<String>> relationTable = getRelationsData(stmt);

        users = StoreUtils.parseUsers(userTable, relationTable);

    }

    private static List<List<String>> getRelationsData(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery(SELECT_ALL_RELATIONS);
        List<List<String>> relationTable = new ArrayList<>();

        while (rs.next()) {
            List<String> row = new ArrayList<>();
            row.add(rs.getString("user_a"));
            row.add(rs.getString("user_b"));
            row.add(rs.getString("relation"));
            relationTable.add(row);
        }
        return relationTable;
    }

    private static List<List<String>> getUserData(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery(SELECT_ALL_USERS);
        List<List<String>> userTable = new ArrayList<>();

        while (rs.next()) {
            List<String> row = new ArrayList<>();
            row.add(rs.getString("name"));
            row.add(rs.getString("profile_picture"));
            row.add(rs.getString("status"));
            row.add(rs.getString("gender"));
            row.add(String.valueOf(rs.getInt("age")));
            row.add(rs.getString("state"));
            userTable.add(row);
        }
        return userTable;
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void addUser(User u) {

        try(Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(INSERT_PERSON)){
            pstmt.setString(1, u.getName());
            pstmt.setString(2, u.getProfilePicture());
            pstmt.setString(3, u.getStatus());
            pstmt.setString(4, u.getGender());
            pstmt.setInt(5, u.getAge());
            pstmt.setString(6, u.getResidentialState());
            pstmt.executeUpdate();
            users.add(u);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(User u) {
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(DELETE_PERSON)){
            pstmt.setString(1, u.getName());
            pstmt.executeUpdate();
            users.remove(u);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePerson(User u){
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(UPDATE_PERSON)){
            pstmt.setString(1, u.getProfilePicture());
            pstmt.setString(2, u.getStatus());
            pstmt.setString(3, u.getResidentialState());
            pstmt.setString(4, u.getName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> getSelectedUser() {
        return Optional.ofNullable(selectedUser);
    }

    @Override
    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    @Override
    public void displayUsers() {
        IntStream.range(0, users.size()).mapToObj(i ->
                (i + 1) + ". " + users.get(i).getName()
        ).forEach(System.out::println);
    }

    @Override
    public Optional<User> getUserWithName(String name) {
        return users.stream().filter(usr -> usr.getName().equalsIgnoreCase(name)).findAny();
    }

    @Override
    public boolean uniqueName(String name) {
        return users.stream().noneMatch(o -> o.getName().equalsIgnoreCase(name));
    }
}

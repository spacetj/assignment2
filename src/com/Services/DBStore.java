package com.Services;

import com.Model.RelationType;
import com.Model.Relationship;
import com.Model.User;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * DBStore is the adapter class that implements the UserStore functionality
 * that interacts with a SQLite database.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class DBStore implements UserStore {

    private static DBStore instance = new DBStore();
    private static final String DB_URL = "jdbc:sqlite:"+ASSETS_FOLDER+ "db.db";
    private static final String SELECT_ALL_USERS = "SELECT name, profile_picture, status, gender, age, state FROM People";
    private static final String SELECT_ALL_RELATIONS = "SELECT user_a, user_b, relation FROM Relations";
    private static final String INSERT_PERSON = "INSERT INTO People(name, profile_picture, status, gender, age, state)" +
            " SELECT ?, ?, ?, ?, ?, ? WHERE NOT EXISTS(SELECT * FROM People WHERE name = ?);";
    private static final String INSERT_RELATION = "INSERT INTO Relations(user_a, user_b, relation) SELECT ?, ?, ? WHERE " +
            "NOT EXISTS (SELECT * FROM Relations WHERE user_a = ? AND user_b = ? AND relation = ?" +
            "UNION SELECT * FROM Relations WHERE user_a = ? AND user_b = ? AND relation = ?);";
    private static final String DELETE_RELATION = "DELETE FROM Relations WHERE (user_a = ? OR user_b = ?) " +
            "and (user_a = ? OR user_b = ?) AND relation = ?";
    private static final String DELETE_PERSON = "DELETE FROM People where name = ?";
    private static final String UPDATE_PERSON = "UPDATE People SET profile_picture = ?, status = ?, state = ?, gender = ? WHERE name = ?";
    private static final String EMPTY_PERSON = "DELETE FROM People";
    private static final String EMPTY_RELATIONS = "DELETE FROM Relations;";
    private static final String PEOPLE_FILE = "people.txt";
    private static final String RELATIONS_FILE = "relations.txt";
    private static List<User> users = new ArrayList<>();
    private Optional<User> selectedUser;
    private static boolean initialCall = true;
    private static boolean isActive = false;

    /**
     * DBStore uses the singleton pattern as it acts as the datastore.
     * getInstance populates the database in the initial call.
     * @return an instance of DBStore
     */
    public static DBStore getInstance() {

        // If get instance called for the first time
        if(initialCall){
            initialCall = false;

            File f = new File(ASSETS_FOLDER+PEOPLE_FILE);

            if(f.exists() && !f.isDirectory()){
                // If people.txt exists, empty table and repopulate with data.
                System.out.println("Logger: people.txt file found.");
                try{
                    repopulateTables();
                    isActive = true;
                } catch (SQLException e) {
                    System.out.println("Error: Could not repopulate table "+ e.getSQLState());
                    isActive = false;
                }
            } else {
                // else get the data from the database.
                try {
                    List<List<String>> people = getUserData(SELECT_ALL_USERS);
                    List<List<String>> relations = getRelationsData(SELECT_ALL_RELATIONS);
                    users =  StoreUtils.parseUsers(people, relations);
                    isActive = true;
                } catch (SQLException e) {
                    isActive = false;
                    System.out.println("Error: Could not get users.");
                }
            }
        }
        return instance;
    }

    /**
     * Check if data was successfully retrieved from the database, thus imply an active connection
     * @return if the connection to database was successful.
     */
    @Override
    public boolean isActiveConnection() {
        return isActive;
    }

    /**
     * During the initial call to getInstance, if a people.txt file is found, the database is empty and
     * repopulated. Information is read from people.txt and relations.txt in order to create the associated
     * Objects and insert them into the database.
     * @throws SQLException
     */
    private static void repopulateTables() throws SQLException {
        emptyTables();
        List<List<String>> relations;

        try {
            relations = StoreUtils.readFile(RELATIONS_FILE);
        } catch (IOException e1) {
            relations = Collections.emptyList();
        }

        List<List<String>> userFile = null;
        try {
            System.out.println("Logger: Repopulating the database with users from people.txt");
            userFile = StoreUtils.readFile(PEOPLE_FILE);
            users = StoreUtils.parseUsers(userFile, relations);
            System.out.println("Logger: Database populated.");
        } catch (IOException e) {
            System.out.println("Error: Could not read the file.");
        }
    }

    /**
     * The query to empty both People and Relation table in the database.
     */
    private static void emptyTables() {
        try(Connection conn = getConnection(); Statement stmt = conn.createStatement()){
            System.out.println("Logger: Emptying the database");
            stmt.executeUpdate(EMPTY_PERSON);
            stmt.executeUpdate(EMPTY_RELATIONS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a connection to the database in order to execute queries and statements.
     * @return a connection
     */
    private static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Could not get a connection to the database. "+e.getMessage());
        }
        return conn;
    }

    /**
     * Gets data from the Relation table in the database and converts it into a list of list of information.
     * @param sql select all or custom sql with where condition can be passed
     * @return a list of list of string
     * @throws SQLException
     */
    private static List<List<String>> getRelationsData(String sql) throws SQLException {

        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

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

    /**
     * Gets data from the People table in the database and converts it into a list of list of information.
     * @param sql select all or custom sql with where condition can be passed
     * @return a list of list of string
     * @throws SQLException
     */
    private static List<List<String>> getUserData(String sql) throws SQLException {

        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

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

    /**
     * Get the list of users.
     * @return
     */
    @Override
    public List<User> getUsers() {
        return users;
    }

    /**
     * Add users to the DB as well as the userlist.
     * @param u user to add
     * @return is add successful
     */
    @Override
    public boolean addUser(User u) {

        try(Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_PERSON)){
            pstmt.setString(1, u.getName());
            pstmt.setString(2, u.getProfilePicture());
            pstmt.setString(3, u.getStatus());
            pstmt.setString(4, u.getGender());
            pstmt.setInt(5, u.getAge());
            pstmt.setString(6, u.getState());
            pstmt.setString(7, u.getName());
            Integer insertId = pstmt.executeUpdate();
            if (insertId > 0) {
                // If row doesnt already exist in the database
                System.out.println("Logger: New user "+u.getName()+" has been inserted.");
                users.add(u);
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error: Could not save new user "+u.getName());
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Delete users from the DB and the user list.
     * @param u user to delete
     * @return is delete successful
     */
    @Override
    public boolean deleteUser(User u) {
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(DELETE_PERSON)){
            pstmt.setString(1, u.getName());
            Integer deleteId = pstmt.executeUpdate();
            if (deleteId > 0) {
                System.out.println("Logger: Deleted user "+u.getName());
                users.remove(u);
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error: Could not delete user "+u.getName());
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Update whitelisted information such as profilepicture, status, state and gender of a user.
     * @param u user to update
     * @return is update successful
     */
    @Override
    public boolean updateUser(User u){
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(UPDATE_PERSON)){
            pstmt.setString(1, u.getProfilePicture());
            pstmt.setString(2, u.getStatus());
            pstmt.setString(3, u.getState());
            pstmt.setString(4, u.getGender());
            pstmt.setString(5, u.getName());
            pstmt.executeUpdate();
            System.out.println("Logger: Updated user "+u.getName());
            return true;
        } catch (SQLException e) {
            System.out.println("Error: Could not update person "+u.getName());
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * When a user profile is selected, it returns the users that is selected.
     * @return
     */
    @Override
    public Optional<User> getSelectedUser() {
        return selectedUser;
    }

    /**
     * When a user profile is viewed, it sets the selected user.
     * @param selectedUser the username of the selected user.
     */
    @Override
    public void setSelectedUser(String selectedUser) {
        this.selectedUser = users.stream().filter(o -> o.getName().equalsIgnoreCase(selectedUser)).findAny();
    }

    /**
     * Gets the user with the input username if one exists.
     * @param name The user with username
     * @return Optional of user if one exists
     */
    @Override
    public Optional<User> getUserWithName(String name) {
        return users.stream().filter(usr -> usr.getName().equalsIgnoreCase(name)).findAny();
    }

    /**
     * Checks to see if the name is unique.
     * @param name input name
     * @return is name unique
     */
    @Override
    public boolean uniqueName(String name) {
        return users.stream().noneMatch(o -> o.getName().equalsIgnoreCase(name));
    }

    /**
     * Add relation to the database.
     * @param u user to add the relation to
     * @param relationship the relationship to add
     * @return if the relationship has been added
     */
    @Override
    public boolean addRelation(User u, Relationship relationship){
        try(Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_RELATION)){
            pstmt.setString(1, u.getName());
            pstmt.setString(2, relationship.getUser().getName());
            pstmt.setString(3, RelationType.getString(relationship.getRelation()));
            pstmt.setString(4, u.getName());
            pstmt.setString(5, relationship.getUser().getName());
            pstmt.setString(6, RelationType.getString(relationship.getRelation()));
            pstmt.setString(7, relationship.getUser().getName());
            pstmt.setString(8, u.getName());
            pstmt.setString(9, RelationType.getString(relationship.getRelation()));
            Integer insertId = pstmt.executeUpdate();
            if (insertId > 0) {
                System.out.println("Logger: "+ u.getName()+ " added "+ relationship.getUser().getName()+" as a "+relationship.getRelation());
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error: Could not add relation "+relationship.getRelation()+ " from "+u.getName()+" to "+relationship.getUser().getName()+" into the db");
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Delete relation from the database.
     * @param user user to delete the relation from.
     * @param toDelete relationship to delete
     * @return if the relationship has been deleted.
     */
    @Override
    public boolean deleteRelation(User user, Relationship toDelete) {
        try(Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(DELETE_RELATION)){
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, toDelete.getUser().getName());
            pstmt.setString(4, toDelete.getUser().getName());
            pstmt.setString(5, RelationType.getString(toDelete.getRelation()));
            Integer deleteId = pstmt.executeUpdate();
            if (deleteId > 0) {
                System.out.println("Logger: Deleted "+toDelete.getUser().getName()+" relation as a "+toDelete.getRelation()+" from "+user.getName());
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Error: Could not delete "+user.getName()+"'s relation to"+ toDelete.getUser().getName()+" as a "+toDelete.getRelation());
            return false;
        }
    }
}

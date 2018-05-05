//package com.Services;
//
//import com.Model.RelationType;
//import com.Model.Relationship;
//import com.Model.User;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.IntStream;
//
///**
// * UserService is a singleton which contains the list of users and sets the selected user.
// *
// * @version 1.0.0 22nd March 2018
// * @author Tejas Cherukara
// */
//public class UserService implements UserStore {
//
//    private static UserService instance = new UserService();
//    private User selectedUser = null;
//    private List<User> users = new ArrayList<>();
//
//    private UserService() {
//        users.addAll(
//                Arrays.asList(
//                        UserFactory.getUser("Alpha", 20, "profile_pic_smilie", "Working at KFC"),
//                        UserFactory.getUser("Bravo", 18, "bravo_pic", "Bravo be brave"),
//                        UserFactory.getUser("Charly", 17, "charlr_pic", "Charly is knarly"),
//                        UserFactory.getUser("Delta", 30, "delta_pic", "Delta Goodrum is life"),
//                        UserFactory.getUser("Echo", 60, "echo_pic", "Echo forever"),
//                        UserFactory.getUser("Foxtror", 50, "fostrot_pic", "What does the foxtrot say?"),
//                        UserFactory.getUser("Golf", 40, "golf_pic", "Volkwagon golf gti tubo charged diesel"),
//                        UserFactory.getUser("Halo", 80, "halo_pic", "I can see your halo, halo, halo"),
//                        UserFactory.getUser("India", 30, "india_pic", "Chak de india"),
//                        UserFactory.getUser("Juliet", 40, "juliet_pic", "Romeo and Juliet"),
//                        UserFactory.getUser("Kilo", 53, "kilo_pic", "Kilo is just a number")
//                )
//        );
//
//        users.get(0).addRelation(new Relationship(RelationType.FRIEND, users.get(4)));
//        users.get(3).addRelation(new Relationship(RelationType.FRIEND, users.get(2)));
//        users.get(5).addRelation(new Relationship(RelationType.FRIEND, users.get(2)));
//        users.get(8).addRelation(new Relationship(RelationType.FRIEND, users.get(3)));
//        users.get(9).addRelation(new Relationship(RelationType.FRIEND, users.get(8)));
//        users.get(6).addRelation(new Relationship(RelationType.FRIEND, users.get(7)));
//        users.addAll(Arrays.asList(
//                UserFactory.getUser("Oscar", 2, "oscar_winner", "Oscario", users.get(1), users.get(2)),
//                UserFactory.getUser("Potter", 15, "pots_pic", "Pots for life", users.get(5), users.get(8)),
//                UserFactory.getUser("Tango", 7, "Tango_mask", "Tango time to boogie", users.get(6), users.get(9))
//        ));
//    }
//
//    /**
//     * Get an instance of the class.
//     * @return the singleton class.
//     */
//    public static UserService getInstance() {
//        return instance;
//    }
//
//    /**
//     * Gets the list of users.
//     * @return
//     */
//    public List<User> getUsers() {
//        return users;
//    }
//
//    /**
//     * Adds user to the user list.
//     * @param u
//     */
//    public void addUser(User u) {
//        users.add(u);
//        System.out.println("\n\nSuccessfully added user: " + u.getName()+"\n\n");
//    }
//
//    /**
//     * Retrieves the selected user.
//     * @return and optional user if it is set.
//     */
//    public Optional<User> getSelectedUser() {
//        return Optional.ofNullable(selectedUser);
//    }
//
//    /**
//     * Sets the selected user.
//     * @param selectedUser
//     */
//    public void setSelectedUser(User selectedUser) {
//        this.selectedUser = selectedUser;
//    }
//
//    /**
//     * Deletes user from the user list.
//     * @param u the user to delete.
//     */
//    public void deleteUser(User u) {
//        users.remove(u);
//        System.out.println("Successfully removed user: " + u.getName());
//    }
//
//    /**
//     * Displays the user in a list.
//     */
//    public void displayUsers() {
//        IntStream.range(0, users.size()).mapToObj(i ->
//                (i + 1) + ". " + users.get(i).getName()
//        ).forEach(System.out::println);
//    }
//
//    /**
//     * Retrieves the user object with the set name.
//     * @param name The name to search the user list for.
//     * @return Optional of an user as it may or may not exist.
//     */
//    public Optional<User> getUserWithName(String name) {
//        return users.stream().filter(o -> o.getName().equalsIgnoreCase(name)).findAny();
//    }
//
//    /**
//     * Check that a users name is unique and hasnt been taken.
//     * @param name
//     * @return
//     */
//    public boolean uniqueName(String name){
//        return users.stream().noneMatch(o -> o.getName().equalsIgnoreCase(name));
//    }
//}

//package com.Services;
//
//import com.Model.User;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
///**
// * Created by TJ on 24/4/18.
// */
//public class FileStore implements UserStore {
//
//
//    private static FileStore instance = new FileStore();
//    private static List<User> users = new ArrayList<>();
//    private User selectedUser = null;
//
//    /**
//     * Get an instance of the class.
//     * @return the singleton class.
//     */
//    public static FileStore getInstance() throws IOException {
//
//        if (users.isEmpty()) {
//            List<List<String>> relations;
//
//            try {
//                relations = readFile(RELATIONS_FILE);
//            } catch (IOException e1) {
//                relations = Collections.emptyList();
//            }
//
//            List<List<String>> userFile = readFile(PEOPLE_FILE);
//
//            users = StoreUtils.parseUsers(userFile, relations);
//        }
//
//        return instance;
//    }
//
//
//
//    @Override
//    public List<User> getUsers() {
//        return users;
//    }
//
//    @Override
//    public void addUser(User u) {
//        users.add(u);
//        System.out.println("\n\nSuccessfully added user: " + u.getName() + "\n\n");
//    }
//
//    @Override
//    public void deleteUser(User u) {
//        users.remove(u);
//        System.out.println("Successfully removed "+u.getName());
//    }
//
//    @Override
//    public Optional<User> getSelectedUser() {
//        return Optional.ofNullable(selectedUser);
//    }
//
//    @Override
//    public void setSelectedUser(User selectedUser) {
//        this.selectedUser = selectedUser;
//    }
//
//    @Override
//    public void displayUsers() {
//        IntStream.range(0, users.size()).mapToObj(i ->
//                (i + 1) + ". " + users.get(i).getName()
//        ).forEach(System.out::println);
//    }
//
//    @Override
//    public Optional<User> getUserWithName(String name) {
//        return users.stream().filter(usr -> usr.getName().equalsIgnoreCase(name)).findAny();
//    }
//
//    @Override
//    public boolean uniqueName(String name) {
//        return users.stream().noneMatch(o -> o.getName().equalsIgnoreCase(name));
//    }
//
//
//}

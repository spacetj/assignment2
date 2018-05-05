package com.Services;

import com.Model.User;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * UserStore allows changing the implementation of getting user data without affecting the MiniNet code.
 *
 * @version 1.0.0 22nd March 2018
 * @author Tejas Cherukara
 */

public interface UserStore {

    String ASSETS_FOLDER = "assets"+File.separator;

    List<User> getUsers();
    void addUser(User u);
    void deleteUser(User u);
    Optional<User> getSelectedUser();
    void setSelectedUser(User selectedUser);
    void displayUsers();
    Optional<User> getUserWithName(String name);
    boolean uniqueName(String name);
}

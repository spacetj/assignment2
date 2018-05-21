package com.Services;

import com.Model.Relationship;
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

    static UserStore getInstance(){
        return DBStore.getInstance();
    }

    boolean isActiveConnection();
    List<User> getUsers();
    boolean addUser(User u);
    boolean deleteUser(User u);
    boolean updateUser(User u);
    Optional<User> getSelectedUser();
    void setSelectedUser(String selectedUserName);
    Optional<User> getUserWithName(String name);
    boolean uniqueName(String name);
    boolean addRelation(User u , Relationship rel);
    boolean deleteRelation(User user, Relationship toDelete);

}

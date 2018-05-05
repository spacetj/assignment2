package com.Model;

import com.Model.Exceptions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * User is the abstract class which Adult, YoungAdult and Infants.
 * Contains a list of generic methods and local variables that apply to all users.
 *
 * @version 1.0.0 22nd March 2018
 * @author Tejas Cherukara
 */
public abstract class User{

    protected List<Relationship> relationships = new ArrayList<>();
    private String name;
    private Integer age;
    private String gender;
    private String profilePicture;
    private String status;
    private String residentialState;
    public static Predicate<Relationship> isDependant = relationship -> relationship.getRelation() == RelationType.DEPENDANT;
    public static Predicate<Relationship> isGuardian = relationship -> relationship.getRelation() == RelationType.GUARDIAN;
    public static Predicate<Relationship> isFriend = relationship -> relationship.getRelation() == RelationType.FRIEND;

    public User(String name, Integer age, String profilePicture, String status, String gender, String state) {
        this.name = name;
        this.age = age;
        this.profilePicture = profilePicture;
        this.status = status;
        this.gender = gender;
        this.residentialState = state;
    }

    /**
     * Gets the relation that may or may not exist with the given user.
     * @param user User to search for.
     * @return the optional relationship that may or may not exist.
     */
    public Optional<Relationship> getUserRelation(User user){
        return relationships.stream().filter(o -> o.getUser() == user).findAny();
    }

    /**
     * Abstract add new relation method which will be overriden in sub classes.
     * @param newFriend friend to add to user.
     */
    public abstract String addRelation(Relationship newFriend) throws TooYoungException, NotToBeFriendsException, NotToBeColleaguesException, NotToBeCoupledException, NotToBeClassmastesException;

    /**
     * Abstract delete relation method which will be overriden in sub classes.
     * @param friend friend to add to user.
     */
    public abstract String deleteRelation(User friend);

    /**
     * Abstract erase relation with user method which will be overriden in sub classes.
     * @param user friend to add to user.
     */
    public abstract String eraseRelationWithUser(User user) throws NoParentException;

    /**
     * Displays information about the friends / relations of a user.
     */
    public void showFriends() {
        System.out.print("\n\nTotal number of relationships: "+ relationships.size()+"\n\n");

        relationships.forEach(relation -> {
            System.out.println("Relation Type: "+relation.getRelation());
            System.out.println("Name: "+relation.getUser().getName());
            System.out.println("Age: "+relation.getUser().getAge());

            if (relation.getUser().getProfilePicture() != null) {
                System.out.println(relation.getUser().getProfilePicture());
            }

            if (relation.getUser().getStatus() != null) {
                System.out.println(relation.getUser().getStatus());
            }

            System.out.println();
        });
    }

    public Optional<User> hasRelation(User user){
        return relationships.stream()
                .filter(o -> o.getUser().getName().equalsIgnoreCase(user.getName()))
                .map(Relationship::getUser)
                .findAny();
    }

    /**
     * Getter for friends
     * @return
     */
    public List<Relationship> getFriends() {
        return relationships;
    }

    /**
     * Getter for user's name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for user's age
     * @return
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Getter for user's profile picture
     * @return
     */
    public String getProfilePicture() {
        return profilePicture;
    }

    public void setResidentialState(String residentialState) {
        this.residentialState = residentialState;
    }

    public String getGender() {
        return gender;

    }

    public String getResidentialState() {
        return residentialState;
    }

    /**

     * setter for users profile picture.
     * @param profilePicture
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Getter for users status.
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setter for users status.
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }
}


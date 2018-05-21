package com.Model;

import com.Model.Exceptions.*;
import com.Services.UserFactory;
import com.Services.UserStore;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * User is the abstract class which Adult, YoungAdult and Infants extends from.
 * Contains a list of generic methods and local variables that apply to all users.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public abstract class User{

    protected List<Relationship> relationships = new ArrayList<>();
    private String name;
    private Integer age;
    private String gender;
    private String profilePicture;
    private String status;
    private String state;
    public UserStore store;
    public static Predicate<Relationship> isDependant = relationship -> relationship.getRelation() == RelationType.DEPENDANT;
    public static Predicate<Relationship> isGuardian = relationship -> relationship.getRelation() == RelationType.GUARDIAN;
    public static Predicate<Relationship> isFriend = relationship -> relationship.getRelation() == RelationType.FRIEND;
    public static Predicate<Relationship> isCoParent = relationship -> relationship.getRelation() == RelationType.COPARENT;
    public static Predicate<Relationship> isClassmates = relationship -> relationship.getRelation() == RelationType.CLASSMATES;
    public static Predicate<Relationship> isColleague = relationship -> relationship.getRelation() == RelationType.COLLEAGUES;


    public User(String name, Integer age, String profilePicture, String status, String gender, String state) {
        this.name = name;
        this.age = age;
        this.profilePicture = profilePicture;
        this.status = status;
        this.gender = gender;
        this.state = state;
        store = UserStore.getInstance();
        store.addUser(this);
    }

    /**
     * Gets the relation that may or may not exist with the given user.
     * @param user User to search for.
     * @return the optional relationship that may or may not exist.
     */
    public Optional<Relationship> getUserRelation(User user, RelationType relationType){
        return relationships.stream().filter(o -> o.getUser() == user && o.getRelation() == relationType).findAny();
    }

    /**
     * Abstract add new relation method which will be overriden in sub classes.
     * @param newFriend friend to add to user.
     */
    public abstract boolean addRelation(Relationship newFriend) throws TooYoungException, NotToBeFriendsException, NotToBeColleaguesException, NotToBeCoupledException, NotToBeClassmastesException, NoAvailableException;

    /**
     * Abstract delete relation method which will be overriden in sub classes.
     * @param deleteRel friend to add to user.
     */
    public abstract boolean deleteRelation(Relationship deleteRel) throws NoParentException;

    /**
     * Abstract erase relation with user method which will be overriden in sub classes.
     * @param user friend to add to user.
     */
    public boolean eraseRelationWithUser(User user) {
        List<Relationship> relationshipsWithUser = relationships.stream().filter(o -> o.getUser().equals(user)).collect(Collectors.toList());

        relationshipsWithUser.forEach(o -> {
            relationships.remove(o);
            store.deleteRelation(this, o);
        });

        return true;
    }


    /**
     * Abstract can delete user relation which contains constraints to check if the user can be delete.
     */
    public abstract boolean canDeleteUser() throws NoParentException;

    /**
     * Gets the siblings of young adults and infants.
     * @return
     */
    public List<Relationship> getSiblings() {
        if (UserFactory.isYoungAdult.test(this) || UserFactory.isInfant.test(this)) {
            return this.getRelationships().stream()
                    .filter(o -> o.getRelation() == RelationType.GUARDIAN)
                    .map(o -> o.getUser().getRelationships())
                    .flatMap(List::stream)
                    .filter(o -> o.getRelation() == RelationType.DEPENDANT)
                    .filter(o -> !o.getUser().getName().equalsIgnoreCase(this.getName()))
                    .map(o -> new Relationship(RelationType.SIBLINGS, o.getUser()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
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

    /**
     * Setter for the users state.
     * @param residentialState
     */
    public void setState(String residentialState) {
        this.state = residentialState;
    }

    /**
     * Setter for the users gender
     * @return
     */
    public String getGender() {
        return gender;

    }

    /**
     * Getter for users gender.
     * @param gender
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Getter for users state.
     * @return
     */
    public String getState() {
        return state;
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

    /**
     * Getter for users relationships
     * @return
     */
    public List<Relationship> getRelationships() {
        return relationships;
    }
}


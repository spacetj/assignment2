package com.Model;

import com.Model.Exceptions.*;
import com.Services.UserFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * YoungAdult extends from User and specifies condition apply to users under 16 years old.
 *
 * @author Tejas Cherukara
 * @version 1.0.0 22nd March 2018
 */
public class YoungAdult extends User {

    public YoungAdult(String name, Integer age, String profilePicture, String status, User parent1, User parent2, String gender, String state) {
        super(name, age, profilePicture, status, gender, state);
        addDependantsTo(parent1, parent2);
    }

    /**
     * Ensures that the guardian / dependant / coparent relationships are added for all appropriate users.
     * @param parent1 guardian number 1
     * @param parent2 guardian number 2
     */
    private void addDependantsTo(User parent1, User parent2) {
        try {
            this.addRelation(new Relationship(RelationType.GUARDIAN, parent1));
            this.addRelation(new Relationship(RelationType.GUARDIAN, parent2));
        } catch (TooYoungException | NotToBeFriendsException | NotToBeColleaguesException |
                NotToBeCoupledException | NotToBeClassmastesException e) {
            this.deleteRelation(parent1);
            this.deleteRelation(parent2);
            parent1.deleteRelation(parent2);
            e.getMessage();
        }
    }

    /**
     * Overrides the User add relation function as for Young adults, new friends have to be less than
     * 3 years in age gap and has to be an young adult from a different family.
     * @param newFriend
     */
    @Override
    public String addRelation(Relationship newFriend) throws TooYoungException, NotToBeFriendsException, NotToBeColleaguesException, NotToBeCoupledException, NotToBeClassmastesException {

        if (!relationships.contains(newFriend) && !newFriend.getUser().getName().equalsIgnoreCase(this.getName())) {
            if (isGuardian.test(newFriend)) {
                relationships.add(newFriend);
                newFriend.getUser().addRelation(new Relationship(RelationType.DEPENDANT, this));
            } else if (isAgeGapLessThan3(newFriend) && isYoungAdultFromDiffFamily(newFriend) && isFriend.test(newFriend)) {
                relationships.add(newFriend);
                System.out.println("\n\n"+newFriend.getUser().getName()+" added as a new friend to "+this.getName()+"\n\n");
                newFriend.getUser().addRelation(new Relationship(RelationType.FRIEND, this));
            } else if (newFriend.getRelation() == RelationType.COLLEAGUES){
                throw new NotToBeColleaguesException("Children cannot be colleagues");
            } else if(newFriend.getRelation() == RelationType.COPARENT) {
                throw new NotToBeCoupledException("Children cannot be a couple");
            } else {
                throw new NotToBeFriendsException("Cannot add relation to friend");
            }
        }
        return "Cannot Delete Friend";
    }

    /**
     * Overrides the delete relation from User class because Guardian relationships cannot be deleted.
     * @param friend User to be deleted.
     */
    @Override
    public String deleteRelation(User friend) {
        if (this.getUserRelation(friend).isPresent()) {
            if(isGuardian.test(this.getUserRelation(friend).get())){
                return "\n\nCannot delete a guardian.\n\n";
            } else {
                relationships.remove(this.getUserRelation(friend).get());
                friend.deleteRelation(this);
                return this.getName()+" delete "+friend.getName()+" as a friend.";
            }
        }
        return "Cannot delete relation";
    }

    /**
     * Checks that the new friends and the current users age gap is less than 5.
     * @param user
     * @return boolean
     */
    private Boolean isAgeGapLessThan3(Relationship user) throws NotToBeFriendsException {

        if (Math.abs(this.getAge() - user.getUser().getAge()) > 3 && !isGuardian.test(user)) {
            throw new NotToBeFriendsException("Age gap is greater than 3");
        }

        return true;
    }

    /**
     * Checks if the new friend is an young adult from a different family
     * @param relation
     * @return boolean
     */
    private Boolean isYoungAdultFromDiffFamily(Relationship relation) throws NotToBeFriendsException {


        if (!UserFactory.isYoungAdult.test(relation.getUser())) {
            throw new NotToBeFriendsException("Adult cannot be friend with child");
        }

        List<User> parents = relationships.stream()
                .filter(o -> isGuardian.test(o))
                .map(Relationship::getUser).collect(Collectors.toList());

        // Check that the new friend doesn't have a relation to current users parents.
        if(parents.stream().noneMatch(o -> o.getUserRelation(relation.getUser()).isPresent())){
            return true;
        } else {
            System.out.println("Young adult is from the same family.");
            return false;
        }
    }

    /**
     * When another user is being deleted from the social network, this method is called to ensure that
     * all exisiting connections between this user and the to be deleted user are erased.
     * @param user that is to be deleted.
     */
    @Override
    public String eraseRelationWithUser(User user) throws NoParentException {
        Optional<Relationship> userRelo = relationships.stream().filter(o -> o.getUser().equals(user)).findFirst();
        if (userRelo.isPresent()) {
            // If the to be deleted user is this users guardian, have to delete this user as well.
            if (isGuardian.test(userRelo.get())) {
                throw new NoParentException("Adult has a dependant, cannot delete");
            } else if(isFriend.test(userRelo.get())){
                relationships.remove(userRelo.get());
            }
        }
        return "Not deleted";
    }
}

package com.Model;

import com.Model.Exceptions.*;
import com.Services.UserFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * YoungAdult extends from User and specifies condition apply to users under 16 years old.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
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
        Relationship coParentRelation = parent1.getUserRelation(parent2, RelationType.COPARENT)
                .orElse(new Relationship(RelationType.COPARENT, parent2));
        Relationship parentA = new Relationship(RelationType.GUARDIAN, parent1);
        Relationship parentB = new Relationship(RelationType.GUARDIAN, parent2);
        boolean hasCoParentRelation = parent1.getUserRelation(parent2, RelationType.COPARENT).isPresent();

        try {
            this.addRelation(parentA);
            this.addRelation(parentB);
            if (!hasCoParentRelation) {
                parent1.addRelation(new Relationship(RelationType.COPARENT, parent2));
            }
        } catch (TooYoungException | NotToBeFriendsException | NotToBeColleaguesException |
                NotToBeCoupledException | NotToBeClassmastesException | NoAvailableException e) {
            try {
                this.deleteRelation(parentA);
                this.deleteRelation(parentB);
                if (!hasCoParentRelation) {
                    parent1.deleteRelation(coParentRelation);
                }
                store.deleteUser(this);
                System.out.println("Error: Couldn't add the guardians of "+this.getName());
            } catch (NoParentException e1) {
                System.out.println("Could not delete "+ e1.getMessage());
            }
        }
    }

    /**
     * Overrides the User add relation function as for Young adults, new friends have to be less than
     * 3 years in age gap and has to be an young adult from a different family.
     * @param newFriend
     */
    @Override
    public boolean addRelation(Relationship newFriend) throws TooYoungException, NotToBeFriendsException, NotToBeColleaguesException, NotToBeCoupledException, NotToBeClassmastesException, NoAvailableException {

        if (!relationships.contains(newFriend) && !newFriend.getUser().getName().equalsIgnoreCase(this.getName())) {
            if (isGuardian.test(newFriend) && this.relationships.stream().filter(o -> isGuardian.test(o)).count() < 2) {
                return addRel(newFriend, RelationType.DEPENDANT);
            } else if (isFriend.test(newFriend) && isAgeGapLessThan3(newFriend) && isYoungAdultFromDiffFamily(newFriend)) {
                return addRel(newFriend, newFriend.getRelation());
            } else if (isClassmates.test(newFriend)) {
                return addRel(newFriend, newFriend.getRelation());
            } else if (newFriend.getRelation() == RelationType.COLLEAGUES){
                throw new NotToBeColleaguesException("Children cannot be colleagues");
            } else if(newFriend.getRelation() == RelationType.COPARENT) {
                throw new NotToBeCoupledException("Children cannot be a couple");
            } else {
                throw new NotToBeFriendsException("Cannot add relation to "+this.getName());
            }
        }
        return true;
    }

    /**
     * Adds the specified relations without any contraints.
     * @param newFriend relation that this user needs to add.
     * @param relation relation that the other user needs to add.
     * @return
     * @throws TooYoungException
     * @throws NotToBeFriendsException
     * @throws NotToBeColleaguesException
     * @throws NotToBeCoupledException
     * @throws NotToBeClassmastesException
     * @throws NoAvailableException
     */
    private boolean addRel(Relationship newFriend, RelationType relation) throws TooYoungException,
            NotToBeFriendsException, NotToBeColleaguesException, NotToBeCoupledException, NotToBeClassmastesException,
            NoAvailableException {
        relationships.add(newFriend);
        try {
            newFriend.getUser().addRelation(new Relationship(relation, this));
            return store.addRelation(this, newFriend);
        } catch (TooYoungException | NotToBeFriendsException | NotToBeColleaguesException | NotToBeClassmastesException
                | NotToBeCoupledException | NoAvailableException e) {
            relationships.remove(newFriend);
            throw e;
        }
    }

    /**
     * Overrides the delete relation from User class because Guardian relationships cannot be deleted.
     * @param deleteRel User to be deleted.
     */
    @Override
    public boolean deleteRelation(Relationship deleteRel) throws NoParentException {
        Optional<Relationship> relationToDelete = this.getUserRelation(deleteRel.getUser(), deleteRel.getRelation());
        if(relationToDelete.isPresent()){
            if(isGuardian.test(deleteRel)){
                throw new NoParentException(deleteRel.getUsername()+" is a guardian of "+this.getName()+". Cannot delete.");
            } else if(isClassmates.test(deleteRel) || isFriend.test(deleteRel)){
                relationships.remove(relationToDelete.get());
                try {
                    deleteRel.getUser().deleteRelation(new Relationship(deleteRel.getRelation(), this));
                    store.deleteRelation(this, relationToDelete.get());
                } catch (NoParentException e) {
                    System.out.println("Error: "+this.getName()+" could not delete relation "+
                            relationToDelete.get().getRelation()+" with "+deleteRel.getUser().getName());
                    relationships.add(relationToDelete.get());
                    throw e;
                }
            }
        }
        return true;
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
            throw new NotToBeFriendsException("Only young Adults can be friends with young adults");
        }

        List<User> parents = relationships.stream()
                .filter(o -> isGuardian.test(o))
                .map(Relationship::getUser).collect(Collectors.toList());

        // Check that the new friend doesn't have a relation to current users parents.
        if(parents.stream().noneMatch(o -> o.getUserRelation(relation.getUser(), RelationType.DEPENDANT).isPresent())){
            return true;
        } else {
            throw new NotToBeFriendsException("Users are from the same family");
        }
    }

    /**
     * When another user is being deleted from the social network, this method is called to ensure that
     * all exisiting connections between this user and the to be deleted user are erased.
     * @param user that is to be deleted.
     */
    @Override
    public boolean eraseRelationWithUser(User user){
        List<Relationship> userRelo = relationships.stream().filter(o -> o.getUser().equals(user)).collect(Collectors.toList());
        // If the to be deleted user is this users guardian, have to delete this user as well.

        if (userRelo.stream().anyMatch(o -> o.getRelation() == RelationType.GUARDIAN)) {
            try {
                throw new NoParentException("Adult has a dependant, cannot delete");
            } catch (NoParentException e) {
                return false;
            }
        }

        return super.eraseRelationWithUser(user);
    }

    @Override
    public boolean canDeleteUser() {
        return true;
    }
}

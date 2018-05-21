package com.Model;

import com.Model.Exceptions.*;

import java.util.Optional;

/**
 * Infant class is instantiated when the user is below 2 years old.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class Infant extends YoungAdult {

    public Infant(String name, Integer age, String profilePicture, String status, User parent1, User parent2, String gender, String state) {
        super(name, age, profilePicture, status, parent1, parent2, gender, state);
    }

    /**
     * Checks the contraints for new relations.
     * @param newFriend
     */
    @Override
    public boolean addRelation(Relationship newFriend) throws TooYoungException, NotToBeFriendsException, NotToBeColleaguesException, NotToBeCoupledException, NotToBeClassmastesException, NoAvailableException {
        if (!relationships.contains(newFriend) && !newFriend.getUser().getName().equalsIgnoreCase(this.getName())) {
            if(isGuardian.test(newFriend) && this.relationships.stream().filter(o -> isGuardian.test(o)).count() < 2){
                relationships.add(newFriend);
                try {
                    newFriend.getUser().addRelation(new Relationship(RelationType.DEPENDANT, this));
                    return store.addRelation(this, newFriend);
                } catch (NoAvailableException e) {
                    relationships.remove(newFriend);
                    throw e;
                }
            } else if(isClassmates.test(newFriend)){
                throw new NotToBeClassmastesException("Young Children cannot have classmates");
            } else if(isCoParent.test(newFriend)){
                throw new NotToBeCoupledException("Young Children cannot be a couple");
            } else if(isColleague.test(newFriend)){
                throw new NotToBeColleaguesException("Young Children cannot be colleagues.");
            } else if(isFriend.test(newFriend)){
                throw new TooYoungException("Young Children cannot make friends");
            }
            return false;
        }
        return true;
    }

    /**
     * Enables user to delete a relation, overriden from User abstract class according to the
     * specific subclass constraints.
     * @param friend User to be deleted.
     */
    @Override
    public boolean deleteRelation(Relationship friend) throws NoParentException {
        throw new NoParentException(friend.getUsername()+" is a guardian of "+this.getName()+". Cannot delete.");
    }

    /**
     * When another user is being deleted from the social network, this method is called to ensure that
     * all exisiting connections between this user and the to be deleted user are erased.
     * @param user that is to be deleted.
     */
    @Override
    public boolean eraseRelationWithUser(User user) {
        Optional<Relationship> userRelo = relationships.stream().filter(o -> o.getUser().equals(user)).findFirst();
        if(userRelo.isPresent() && isGuardian.test(userRelo.get())){
            try {
                throw new NoParentException(this.getName()+" is a dependant of "+user.getName()+", so user cannot be deleted.");
            } catch (NoParentException e) {
                return false;
            }
        }
        return false;
    }
}

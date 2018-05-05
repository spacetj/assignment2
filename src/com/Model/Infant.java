package com.Model;

import com.Model.Exceptions.*;

import java.util.Optional;

/**
 * Infact class is instantiated when the user is below 2 years old.
 *
 * @version 1.0.0 22nd March 2018
 * @author Tejas Cherukara
 */
public class Infant extends YoungAdult {

    public Infant(String name, Integer age, String profilePicture, String status, User parent1, User parent2, String gender, String state) {
        super(name, age, profilePicture, status, parent1, parent2, gender, state);
    }

    /**
     * Overrides add relation function from user as only guardian can be friends with infants.
     * @param newFriend
     */
    @Override
    public String addRelation(Relationship newFriend) throws TooYoungException, NotToBeFriendsException, NotToBeColleaguesException, NotToBeCoupledException, NotToBeClassmastesException {
        if (!relationships.contains(newFriend) && !newFriend.getUser().getName().equalsIgnoreCase(this.getName())) {
            if(isGuardian.test(newFriend)){
                relationships.add(newFriend);
                newFriend.getUser().addRelation(new Relationship(RelationType.DEPENDANT, this));
            } else if(newFriend.getRelation() == RelationType.CLASSMATES){
                throw new NotToBeClassmastesException("Young Children cannot have classmates");
            } else if(newFriend.getRelation() == RelationType.COPARENT){
                throw new NotToBeCoupledException("Young Children cannot be a couple");
            } else {
                throw new TooYoungException("Young Children cannot make friends");
            }
        }
        return "Infant cant add rel";
    }

    /**
     * Enables user to delete a relation, overriden from User abstract class according to the
     * specific subclass constraints.
     * @param friend User to be deleted.
     */
    @Override
    public String deleteRelation(User friend) {
        return "\n\nCannot delete guardian relation\n\n";
    }

    /**
     * When another user is being deleted from the social network, this method is called to ensure that
     * all exisiting connections between this user and the to be deleted user are erased.
     * @param user that is to be deleted.
     */
    @Override
    public String eraseRelationWithUser(User user) throws NoParentException {
        Optional<Relationship> userRelo = relationships.stream().filter(o -> o.getUser().equals(user)).findFirst();
        if(userRelo.isPresent() && isGuardian.test(userRelo.get())){
            throw new NoParentException("Adult has a dependant, cannot delete");
        }
        return "No way hosey";
    }


}

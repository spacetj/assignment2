package com.Model;

import com.Model.Exceptions.*;
import com.Services.UserFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Adult is instantiated when a user is over 15 years old.
 *
 * @author Tejas Cherukara
 * @version 1.0.0 22nd March 2018
 */
public class Adult extends User {

    private Predicate<Relationship> isCoParent = relationship -> relationship.getRelation() == RelationType.COPARENT;

    public Adult(String name, Integer age, String profilePicture, String status, String gender, String state) {
        super(name, age, profilePicture, status, gender, state);
    }

    @Override
    public String addRelation(Relationship newFriend) {
        if (!relationships.contains(newFriend) && !newFriend.getUser().getName().equalsIgnoreCase(this.getName())) {
            if (UserFactory.isYoungAdult.test(newFriend.getUser()) || UserFactory.isInfant.test(newFriend.getUser())) {
                try {
                    return addDependant(newFriend);
                } catch (NotToBeFriendsException | NotToBeColleaguesException e) {
                    return e.getMessage();
                }
            } else if (isCoParent.test(newFriend)) {
                try {
                    return addCoParent(newFriend);
                } catch (NoAvailableException | NotToBeCoupledException e) {
                    return e.getMessage();
                }
            } else if (isFriend.test(newFriend)) {
                return addFriend(newFriend);
            }
        }
        return "";
    }

    private String addDependant(Relationship newFriend) throws NotToBeFriendsException, NotToBeColleaguesException {
        if(isDependant.test(newFriend)){
            relationships.add(newFriend);
            return "Added "+newFriend.getUser().getName()+" as a dependant to "+this.getName();
        } else if(newFriend.getRelation() == RelationType.FRIEND){
            throw new NotToBeFriendsException("Adults cannot be friends with Young Children");
        } else if(newFriend.getRelation() == RelationType.COLLEAGUES){
            throw new NotToBeColleaguesException("Adults cannot be colleagues with children");
        }
        return "Cannot add friend";
    }

    private String addFriend(Relationship newFriend) {
        if (this.getUserRelation(newFriend.getUser()).isPresent()) {
            return newFriend.getUser().getName()+" is already associated to "+ this.getName() +"as " +
                    this.getUserRelation(newFriend.getUser()).get().getRelation();
        } else {
            relationships.add(newFriend);
            try {
                newFriend.getUser().addRelation(new Relationship(RelationType.FRIEND, this));
            } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException | NotToBeClassmastesException e) {
                relationships.remove(newFriend);
                return e.getMessage();
            }
            return "\n\n"+newFriend.getUser().getName()+" added as a new friend to "+this.getName()+"\n\n";
        }
    }

    private String addCoParent(Relationship newFriend) throws NoAvailableException, NotToBeCoupledException {

        if(this.relationships.stream().anyMatch(o -> o.getRelation() == RelationType.COPARENT)){
            throw new NoAvailableException("Already has a coparent");
        }

        if(!UserFactory.isAdult.test(newFriend.getUser())){
            throw new NotToBeCoupledException("Cannot add child / young child as couple");
        }

        Optional<Relationship> currentRelo = this.getUserRelation(newFriend.getUser());
        //Check if existing friendship exists with user and change relation type ro coparent.
        if (currentRelo.isPresent()) {
            RelationType previousRel = this.getUserRelation(newFriend.getUser()).get().getRelation();
            this.getUserRelation(newFriend.getUser()).get().setRelation(RelationType.COPARENT);
            try {
                newFriend.getUser().addRelation(new Relationship(RelationType.COPARENT, this));
            } catch (TooYoungException | NotToBeFriendsException | NotToBeColleaguesException | NotToBeClassmastesException | NotToBeCoupledException e) {
                this.getUserRelation(newFriend.getUser()).get().setRelation(previousRel);
                System.out.println(e.getMessage());
            }
        } else if (!currentRelo.isPresent()) {
            // Else create a new co parent relation for both users
            relationships.add(newFriend);
            try {
                newFriend.getUser().addRelation(new Relationship(RelationType.COPARENT, this));
            } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException | NotToBeClassmastesException e) {
                relationships.remove(newFriend);
                System.out.println(e.getMessage());
            }
        }
        return "No coparent";
    }

    /**
     * Overrides the delete relation because dependant and coparent relationships cannot be deleted from an adult.
     *
     * @param friend to be delete.
     */
    @Override
    public String deleteRelation(User friend) {
        Optional<Relationship> userRelation = this.getUserRelation(friend);
        //Check if dependant or coparent before deleting relation.
        if (userRelation.isPresent() && !isDependant.test(userRelation.get()) && !isCoParent.test(userRelation.get())) {
            relationships.remove(this.getUserRelation(friend).get());
            friend.deleteRelation(this);
            System.out.println(this.getName()+" delete "+friend.getName()+" as a friend.");
        } else {
            System.out.println("User must exist / Dependant and coparent relations cannot be deleted");
        }
        return "cannot delete";
    }

    /**
     * When another user is being deleted from the social network, this method is called to ensure that
     * all exisiting connections between this user and the to be deleted user are erased.
     * @param user that is to be deleted.
     */
    @Override
    public String eraseRelationWithUser(User user) {
        Optional<Relationship> userRelo = relationships.stream().filter(o -> o.getUser().equals(user)).findFirst();
        userRelo.ifPresent(relationship -> relationships.remove(relationship));
        return this.getName()+" removed"+ user.getName()+"'s relation.";
    }

    public boolean isCoupleWith(String user){
        return this.relationships.stream()
                .anyMatch(
                        o -> o.getRelation() == RelationType.COPARENT && Objects.equals(o.getUser().getName(), user)
                );
    }
}

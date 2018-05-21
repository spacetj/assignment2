package com.Model;

import com.Model.Exceptions.*;
import com.Services.UserFactory;

import java.util.Objects;
import java.util.Optional;

/**
 * Adult is instantiated when a user is over 16 years old.
 *
 * @author Tejas Cherukara
 * @version 2.0.0 20th May 2018
 */
public class Adult extends User {

    public Adult(String name, Integer age, String profilePicture, String status, String gender, String state) {
        super(name, age, profilePicture, status, gender, state);
    }

    /**
     * Checks the contraints for new relations.
     * @param newFriend friend to add to user.
     * @return
     * @throws NotToBeColleaguesException
     * @throws NotToBeFriendsException
     * @throws NotToBeCoupledException
     * @throws NoAvailableException
     * @throws NotToBeClassmastesException
     * @throws TooYoungException
     */
    @Override
    public boolean addRelation(Relationship newFriend) throws NotToBeColleaguesException, NotToBeFriendsException,
            NotToBeCoupledException, NoAvailableException, NotToBeClassmastesException, TooYoungException {
        if (!relationships.contains(newFriend) && !newFriend.getUser().getName().equalsIgnoreCase(this.getName())) {
            if (UserFactory.isYoungAdult.test(newFriend.getUser()) || UserFactory.isInfant.test(newFriend.getUser())) {
                return addDependant(newFriend);
            } else if (isCoParent.test(newFriend)) {
                return addCoParent(newFriend);
            } else if (isFriend.test(newFriend) && UserFactory.isAdult.test(newFriend.getUser())) {
                return addRel(newFriend);
            } else if (isColleague.test(newFriend) && UserFactory.isAdult.test(newFriend.getUser())){
                return addRel(newFriend);
            } else if (isClassmates.test(newFriend)){
                return addRel(newFriend);
            }
            return false;
        }
        return true;
    }

    /**
     * Add the given relationship without contraints.
     * @param newFriend
     * @return
     * @throws TooYoungException
     * @throws NotToBeFriendsException
     * @throws NotToBeColleaguesException
     * @throws NotToBeCoupledException
     * @throws NotToBeClassmastesException
     * @throws NoAvailableException
     */
    private boolean addRel(Relationship newFriend) throws TooYoungException, NotToBeFriendsException, NotToBeColleaguesException, NotToBeCoupledException, NotToBeClassmastesException, NoAvailableException {
        relationships.add(newFriend);
        try {
            newFriend.getUser().addRelation(new Relationship(newFriend.getRelation(), this));
            return store.addRelation(this, newFriend);
        } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException | NoAvailableException | NotToBeClassmastesException e) {
            relationships.remove(newFriend);
            throw e;
        }
    }

    /**
     * Checks and adds a DEPENDANT relation if it meets the constraints.
     * @param newFriend
     * @return
     * @throws NotToBeFriendsException
     * @throws NotToBeColleaguesException
     */
    private boolean addDependant(Relationship newFriend) throws NotToBeFriendsException, NotToBeColleaguesException, NotToBeClassmastesException, NotToBeCoupledException, NoAvailableException, TooYoungException {
        if(isDependant.test(newFriend)){
            relationships.add(newFriend);
            return true;
        } else if(isClassmates.test(newFriend)){
            return addRel(newFriend);
        } else if(newFriend.getRelation() == RelationType.FRIEND){
            throw new NotToBeFriendsException("Adults cannot be friends with Young Children");
        } else if(newFriend.getRelation() == RelationType.COLLEAGUES){
            throw new NotToBeColleaguesException("Adults cannot be colleagues with children");
        }
        return false;
    }

    /**
     * Checks and adds a COUPLE relation if it meets the contraints.
     * @param newFriend
     * @return
     * @throws NoAvailableException
     * @throws NotToBeCoupledException
     * @throws NotToBeFriendsException
     * @throws NotToBeClassmastesException
     * @throws NotToBeColleaguesException
     * @throws TooYoungException
     */
    private boolean addCoParent(Relationship newFriend) throws NoAvailableException, NotToBeCoupledException, NotToBeFriendsException, NotToBeClassmastesException, NotToBeColleaguesException, TooYoungException {

        if(this.relationships.stream().anyMatch(o -> o.getRelation() == RelationType.COPARENT)){
            throw new NoAvailableException("Already has a coparent");
        }

        if(!UserFactory.isAdult.test(newFriend.getUser())){
            throw new NotToBeCoupledException("Cannot add child / young child as couple");
        }

        Optional<Relationship> rel = newFriend.getUser().getRelationships().stream().filter(o -> isCoParent.test(o)).findAny();

        if (!rel.isPresent() || (rel.isPresent() && rel.get().getUser().getName().equalsIgnoreCase(this.getName()))) {
            relationships.add(newFriend);
            try {
                newFriend.getUser().addRelation(new Relationship(RelationType.COPARENT, this));
                return store.addRelation(this, newFriend);
            } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException | NotToBeClassmastesException e) {
                relationships.remove(newFriend);
                throw e;
            }
        }
        return false;

    }

    /**
     * Overrides the delete relation because dependant and coparent relationships cannot be deleted from an adult.
     *
     * @param rel to be delete.
     */
    @Override
    public boolean deleteRelation(Relationship rel) throws NoParentException {
        Optional<Relationship> userRelation = this.getUserRelation(rel.getUser(), rel.getRelation());
        //Check if dependant or coparent before deleting relation.
        if (userRelation.isPresent()) {
            if (!isDependant.test(userRelation.get()) && !isCoParent.test(userRelation.get())) {
                relationships.remove(userRelation.get());
                try {
                    rel.getUser().deleteRelation(new Relationship(rel.getRelation(), this));
                    return store.deleteRelation(this, userRelation.get());
                } catch (NoParentException e) {
                    System.out.println("Error: "+this.getName()+" could not delete relation "+
                            userRelation.get().getRelation()+" with "+userRelation.get().getUser().getName());
                    relationships.add(userRelation.get());
                    throw e;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canDeleteUser() throws NoParentException {
        boolean hasDependant = relationships.stream().anyMatch(o -> isDependant.test(o));
        boolean hasCoParent = relationships.stream().anyMatch(o -> isCoParent.test(o));

        if(hasDependant) throw new NoParentException("Cannot delete adult, has a dependent.");
        return !hasCoParent;
    }

    /**
     * Checks if this user is couple with the input user.
     * @param user that could be this users couple.
     * @return boolean if this user is couple with the input user.
     */
    public boolean isCoupleWith(String user){
        return this.relationships.stream()
                .anyMatch(
                        o -> o.getRelation() == RelationType.COPARENT && Objects.equals(o.getUser().getName(), user)
                );
    }
}

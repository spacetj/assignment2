package com.Model;

/**
 * Relationship is the class used to add a friend to a user.
 *
 * @version 1.0.0 22nd March 2018
 * @author Tejas Cherukara
 */
public class Relationship {

    private RelationType relation;
    private User user;

    public Relationship(RelationType relation, User user) {
        this.relation = relation;
        this.user = user;
    }

    /**
     * Getter for relation type
     * @return
     */
    public RelationType getRelation() {
        return relation;
    }

    /**
     * Setter for relation type
     * @param relation
     */
    public void setRelation(RelationType relation) {
        this.relation = relation;
    }

    /**
     * Getter for user
     * @return
     */
    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object obj) {
        Relationship rel = (Relationship) obj;
        return obj instanceof Relationship
                && rel.getUser() == this.getUser()
                && rel.getRelation() == this.getRelation();
    }
}
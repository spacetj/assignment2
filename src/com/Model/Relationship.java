package com.Model;

/**
 * Relationship is the class used to add a friend to a user.
 *
 * @version 2.0.0 20th May 2018
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

    /**
     * Gets the username of the user associated with the relation.
     * @return
     */
    public String getUsername() {
        return user.getName();
    }

    /**
     * Overrides the default equals.
     * 2 Relationships are equals if their usernames and relation types are the same.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        Relationship rel = (Relationship) obj;
        return obj instanceof Relationship
                && rel.getUser().getName().equalsIgnoreCase(this.getUser().getName())
                && rel.getRelation() == this.getRelation();
    }
}
package com.Model;

/**
 * RelationTypes defines the various types of relations that a user can have with another user.
 *
 * @version 1.0.0 22nd March 2018
 * @author Tejas Cherukara
 */
public enum RelationType{
    FRIEND,
    GUARDIAN,
    COPARENT,
    DEPENDANT,
    COLLEAGUES,
    CLASSMATES;

    public static final String PARENT = "parent";

    public static RelationType getRelation(String relation){
        switch (relation){
            case "parent": return GUARDIAN;
            case "couple": return COPARENT;
            case "friends": return FRIEND;
            case "classmates": return CLASSMATES;
            case "colleagues": return COLLEAGUES;
            default:
                System.out.println("Unknown relation");
                return null;
        }
    }
}


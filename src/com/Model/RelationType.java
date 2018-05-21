package com.Model;

/**
 * RelationTypes defines the various types of relations that a user can have with another user.
 *
 * @version 2.0.0 22nd March 2018
 * @author Tejas Cherukara
 */
public enum RelationType{
    FRIEND,
    GUARDIAN,
    COPARENT,
    DEPENDANT,
    COLLEAGUES,
    SIBLINGS,
    CLASSMATES;

    public static final String PARENT = "parent";

    public static final String[] relations = {"parent", "couple", "friends", "classmates", "colleagues"};

    /**
     * Given a string, it returns the RelationType that is related.
     * @param relation
     * @return
     */
    public static RelationType getRelation(String relation){
        switch (relation){
            case "parent": return GUARDIAN;
            case "couple": return COPARENT;
            case "friends": return FRIEND;
            case "classmates": return CLASSMATES;
            case "colleagues": return COLLEAGUES;
            default:
                System.out.println("Unknown relation string");
                return null;
        }
    }

    /**
     * Given a relation type, it returns the string that is related.
     * @param relationType
     * @return
     */
    public static String getString(RelationType relationType){
        switch (relationType){
            case GUARDIAN: return "parent";
            case COPARENT: return "couple";
            case FRIEND: return "friends";
            case CLASSMATES: return "classmates";
            case COLLEAGUES: return "colleagues";
            case DEPENDANT: return "child";
            case SIBLINGS: return "sibling";
            default:
                System.out.println("Unknown relation type");
                return null;
        }
    }
}


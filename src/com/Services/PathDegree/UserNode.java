package com.Services.PathDegree;

import com.Model.Relationship;
import com.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * UserNode is the node of the graph that is used in the Breadth First Search.
 * It stores the paths for easy retrieval.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class UserNode {

    private User user;
    private List<UserNode> path = new ArrayList<>();

    UserNode(User u) {
        this.user = u;
    }

    /**
     * Getter for users name
     * @return
     */
    public String getName(){
        return user.getName();
    }

    /**
     * Getter for the user
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * Gets the users relations and sets the current path
     * @return
     */
    List<UserNode> getChildren() {

        return user.getRelationships().stream()
                .map(Relationship::getUser)
                .map(UserNode::new)
                .peek(o -> o.path.addAll(
                        Stream.concat(this.path.stream(),Stream.of(this)).collect(Collectors.toList())
                )).collect(Collectors.toList());
    }

    /**
     * Returns the final path from the start user to the end user.
     * @return
     */
    String getFinalPath(){
        this.path.add(this);
        return this.path.stream()
                .map(UserNode::getName)
                .collect(
                        Collectors.joining(" -> ", "Degree of Connection: "+
                                String.valueOf(this.path.size()-1)+"\n", "")
                );
    }

    /**
     * Overrides the equals function as the name of the user is unique,
     * only that property needs to be checked.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof UserNode &&
                this.getName().equalsIgnoreCase(((UserNode) obj).getName());
    }
}

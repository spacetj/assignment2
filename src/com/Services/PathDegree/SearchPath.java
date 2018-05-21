package com.Services.PathDegree;

import com.Model.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SearchPath takes 2 users and uses the breadth first search algorithms to
 * find a path from one user to another.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class SearchPath {

    private UserNode start;
    private UserNode end;

    public SearchPath(User start, User goal){
        this.start = new UserNode(start);
        this.end = new UserNode(goal);
    }

    /**
     * Invocation of this method builds the graph and runs the search.
     *
     * @return
     */
    public String searchPath(){

        if(this.start.getName().equalsIgnoreCase(end.getName())){
            return "They are one and the same!";
        }

        if(start.getUser().getRelationships().stream()
                .anyMatch(o -> o.getUser().getName().equalsIgnoreCase(end.getName()))){
            return "They are directly related.";
        }

        Queue<UserNode> queue = new LinkedList<>();
        ArrayList<UserNode> explored = new ArrayList<>();
        queue.add(start);
        explored.add(start);

        while (!queue.isEmpty()) {
            UserNode current = queue.remove();
            if (current.equals(end)) {
                return current.getFinalPath();
            } else if (!current.getChildren().isEmpty()) {
                addChildren(current, queue, explored);
            }
            explored.add(current);
        }
        return "No Path Found.";

    }

    /**
     * Add relationships of the user to the queue if it hasnt already been queued or explored.
     * @param node User
     * @param queue The queue of the users that needs to be explored
     * @param explored ArrayList of explored users.
     */
    private void addChildren(UserNode node, Queue<UserNode> queue, ArrayList<UserNode> explored){
        List<UserNode> exploredAndQueued = Stream.concat(queue.stream(), explored.stream()).collect(Collectors.toList());
        queue.addAll(
                node.getChildren().stream()
                        .filter(o -> !exploredAndQueued.contains(o))
                        .collect(Collectors.toList())
        );
    }

}

package com.Services;

import com.MiniNet;
import com.Model.Adult;
import com.Model.Exceptions.*;
import com.Model.RelationType;
import com.Model.Relationship;
import com.Model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.Services.UserStore.ASSETS_FOLDER;

/**
 * StoreUtils has common functions which cant be used to create a store of user objects.
 *
 * @version 2.0.0 20th May 2018
 * @author Tejas Cherukara
 */
public class StoreUtils {

    /**
     * Takes a list of list of string of users and relations and converts them into a
     * list of users with relationships populated.
     * @param usersString List of list of users
     * @param relations List of list of relations
     * @return List of Users
     */
    public static List<User> parseUsers(List<List<String>> usersString, List<List<String>> relations) {

        List<List<String>> validChildren = new ArrayList<>();

        // Create a list of optional Adults as not all users might be valid.
        List<Optional<User>> optionalUsers = usersString.stream().map(usr -> {

            if(usr.size() < 6){
                System.out.println("Error: Inconsistent data for "+usr.get(0));
                return Optional.<User>empty();
            }

            Integer age = parseInt(usr.get(4));

            if(!UserFactory.isAgeValid.test(age)){
                try {
                    throw new NoSuchAgeException("Error: Age not accepted.");
                } catch (NoSuchAgeException e) {
                    System.out.println("Error: "+usr.get(0)+" has an invalid age.");
                    return Optional.<User>empty();
                }
            }

            if (age <= UserFactory.YOUNG_ADULT) {
                try {
                    return parseChildData(relations, validChildren, usr);
                } catch (NoParentException e) {
                    System.out.println("Error: "+usr.get(0) + " does not have 2 parents");
                    return Optional.<User>empty();
                }
            }

            return Optional.ofNullable(
                    UserFactory.getUser(usr.get(0), Integer.parseInt(usr.get(4)),usr.get(1), usr.get(2),
                            usr.get(3), usr.get(5))
            );
        }).collect(Collectors.toList());

        List<User> users = optionalUsers.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        parseRelations(users, relations);
        parseValidChildren(validChildren, users);

        return users;
    }

    /**
     * Parse valid child records, for each child, ensure there are 2 valid parent.
     * @param validChildren list of valid children
     * @param users list of all adults.
     */
    private static void parseValidChildren(List<List<String>> validChildren, List<User> users) {
        validChildren.forEach(o -> {
            Optional<Adult> parent1 = users.stream().filter(usr -> Objects.equals(usr.getName(), o.get(6))).map(usr -> (Adult)usr).findAny();
            Optional<Adult> parent2 = users.stream().filter(usr -> Objects.equals(usr.getName(), o.get(7))).map(usr -> (Adult)usr).findAny();

            if (parent1.isPresent() && parent2.isPresent() && parent1.get().isCoupleWith(parent2.get().getName())) {
                users.add(
                        UserFactory.getUser(o.get(0), Integer.parseInt(o.get(4)), o.get(1), o.get(2),
                                parent1.get(), parent2.get(), o.get(3), o.get(5))
                );
            }
        });
    }

    /**
     * Takes a list of users and a list of list of string of relations and
     * adds the relations to the user objects.
     * @param users users to add the relation to
     * @param relations string relations between users which need to be added.
     */
    private static void parseRelations(List<User> users, List<List<String>> relations) {
        relations.forEach(o -> {
            if(!Objects.equals(o.get(2), RelationType.PARENT)){
                Optional<User> usr1 = users.stream().filter(usr -> Objects.equals(usr.getName(), o.get(0))).findAny();
                Optional<User> usr2 = users.stream().filter(usr -> Objects.equals(usr.getName(), o.get(1))).findAny();
                Optional<RelationType> relation = Optional.ofNullable(RelationType.getRelation(o.get(2)));

                if(usr1.isPresent() && usr2.isPresent() && relation.isPresent()){
                    Relationship relationship = new Relationship(relation.get(), usr2.get());
                    try {
                        usr1.get().addRelation(relationship);
                    } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException | NotToBeClassmastesException | NoAvailableException e) {
                        System.out.println("Error: "+e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * Takes adult usrs, and relation, checks if the child record is valid and add them to valid children arraylist.
     * @param relations list of all relations
     * @param validChildren list of all children with 2 valid adults
     * @param usr list of adults
     * @return Always returns empty but valid children are added to valid children list so that it be post processed.
     * @throws NoParentException
     */
    private static Optional<User> parseChildData(List<List<String>> relations, List<List<String>> validChildren, List<String> usr) throws NoParentException {
        List<List<String>> relatedParents = relations.stream()
                .filter(rel -> Objects.equals(rel.get(2), RelationType.PARENT))
                .filter(rel -> Objects.equals(rel.get(0), usr.get(0)) || Objects.equals(rel.get(1), usr.get(0)))
                .collect(Collectors.toList());

        if (relatedParents.size() < 2) {
            throw new NoParentException("Only has one parent");
        }

        relatedParents.forEach(o -> {
            if(!Objects.equals(o.get(0), usr.get(0))){
                usr.add(o.get(0));
            } else {
                usr.add(o.get(1));
            }
        });

        validChildren.add(usr);
        return Optional.empty();
    }

    /**
     * Take a file name and returns a list of list of string.
     * First splits the files contents by newline character.
     * Then split the character by space.
     * New lines and a few " are trimed.
     * @param fileName
     * @return
     * @throws IOException
     */
    public static List<List<String>> readFile(String fileName) throws IOException {

        List<String> lines= Files.readAllLines(Paths.get(ASSETS_FOLDER+fileName));

        return lines.stream()
                .map(o -> {
                    StringTokenizer tokenizer = new StringTokenizer(o,",");
                    List<String> data = new ArrayList<>();
                    while(tokenizer.hasMoreTokens()){
                        data.add(tokenizer.nextToken()
                                .replaceAll("[”“\"]", "").trim());
                    }
                    return data;
                }).collect(Collectors.toList());
    }

    /**
     * Checks if the input is an integer.
     * @param value Number string
     * @return
     */
    public static Integer parseInt(String value) {
        try{
            return Integer.parseInt(value);
        } catch(NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }
}

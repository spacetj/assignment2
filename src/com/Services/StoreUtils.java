package com.Services;

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
 * Created by TJ on 28/4/18.
 */
public class StoreUtils {

    public static List<User> parseUsers(List<List<String>> usersString, List<List<String>> relations) {

        List<List<String>> validChildren = new ArrayList<>();

        List<Optional<User>> optionalUsers = usersString.stream().map(usr -> {

            if(usr.size() < 6){
                System.out.println("Inconsistent data for "+usr.get(0));
                return Optional.<User>empty();
            }

            if (Integer.parseInt(usr.get(4)) < UserFactory.YOUNG_ADULT) {
                return parseChildData(relations, validChildren, usr);
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

        return users;

    }

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
                    } catch (TooYoungException | NotToBeFriendsException | NotToBeCoupledException | NotToBeColleaguesException | NotToBeClassmastesException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
    }

    private static Optional<User> parseChildData(List<List<String>> relations, List<List<String>> validChildren, List<String> usr) {
        List<List<String>> relatedParents = relations.stream()
                .filter(rel -> Objects.equals(rel.get(2), RelationType.PARENT))
                .filter(rel -> Objects.equals(rel.get(0), usr.get(0)) || Objects.equals(rel.get(1), usr.get(0)))
                .collect(Collectors.toList());

        if (relatedParents.size() < 2) {
            System.out.println(usr.get(0) + " does not have 2 parents");
            return Optional.<User>empty();
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

    static List<List<String>> readFile(String fileName) throws IOException {

        List<String> lines= Files.readAllLines(Paths.get(ASSETS_FOLDER+fileName));

        return lines.stream()
                .map(o -> {
                    StringTokenizer tokenizer = new StringTokenizer(o,",");
                    List<String> data = new ArrayList<>();
                    while(tokenizer.hasMoreTokens()){
                        data.add(tokenizer.nextToken().trim());
                    }
                    return data;
                }).collect(Collectors.toList());
    }
}

package com.Services;

import com.Model.Adult;
import com.Model.Infant;
import com.Model.User;
import com.Model.YoungAdult;

import java.util.function.Predicate;

/**
 * UserFactory utilizes the factory method to create the appropriate User given the age and other specifications.
 *
 * @version 1.0.0 22nd March 2018
 * @author Tejas Cherukara
 */
public class UserFactory{

    public static Integer INFANT_AGE = 2;
    public static Integer YOUNG_ADULT = 16;
    public static Predicate<User> isYoungAdult = user -> user.getAge() < YOUNG_ADULT && user.getAge() > INFANT_AGE;
    public static Predicate<User> isAdult = user -> user.getAge() >= YOUNG_ADULT;
    public static Predicate<User> isInfant = user -> user.getAge() <= INFANT_AGE;

    /**
     * Overloaded method which can be called when instantiating adults as they dont need parents / guardians
     * to be set.
     * @param name name of the user to be created.
     * @param age  age of the user to be created.
     * @param profilePicture profilePicture of the user to be created.
     * @param status status of the user to be created.
     * @return
     */
    public static User getUser(String name, Integer age, String profilePicture, String status, String gender, String state){
        return getUser(name,age,profilePicture,status,null,null,gender, state);
    }

    /**
     * UserFactory default create user method which takes in the information required and results in the
     * appropriate subclass of User.
     *
     * @param name name of the user to be created.
     * @param age  age of the user to be created.
     * @param profilePicture profilePicture of the user to be created.
     * @param status status of the user to be created.
     * @param parent1 Mandatory for Young adults and infants, guardian 1 of new user
     * @param parent2 Mandatory for Young adults and infants, guardian 2 of new user
     * @return
     */
    public static User getUser(String name, Integer age, String profilePicture, String status, User parent1, User parent2, String gender, String state){

        if(age <= INFANT_AGE){
            return new Infant(name, age, profilePicture, status, parent1, parent2, gender, state);
        } else if (age < YOUNG_ADULT){
            return new YoungAdult(name, age, profilePicture, status, parent1, parent2, gender, state);
        } else {
            return new Adult(name, age, profilePicture, status, gender, state);
        }

    }

}

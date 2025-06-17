/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ice.poe;

/**
 * Represents the details of a user, including their first name, last name, and
 * password. This class is immutable as the fields are final and set only in the
 * constructor.
 *
 * @author Simphiwe Jijana
 */
public class UserDetails {

    public final String firstName;
    public final String lastName;
    public final String password;

    /**
     * Constructs a new UserDetails object with the provided first name, last
     * name, and password.
     *
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param password The password of the user.
     */
    public UserDetails(String firstName, String lastName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    /**
     * Returns the first name of the user.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the user.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the last name of the user.
     *
     * @return The last name.
     */
    public String getPassword() {
        return password;
    }
}

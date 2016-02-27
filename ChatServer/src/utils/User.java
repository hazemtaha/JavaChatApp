/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author kan
 */
public class User implements Serializable {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private int status;
    private ArrayList<User> contactList;

    public User(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.status = user.getStatus();
        this.contactList = user.getContactList();
    }

    public User(int id, String firstName, String lastName, String email, String password, int status, ArrayList<User> contactList) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.status = status;
        this.contactList = contactList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<User> getContactList() {
        return contactList;
    }

    public void setContactList(ArrayList<User> contactList) {
        this.contactList = contactList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof User) {
            if (this.id == ((User) obj).getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

}

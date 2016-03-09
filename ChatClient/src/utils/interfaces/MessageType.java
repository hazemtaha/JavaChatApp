/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.interfaces;

/**
 *
 * @author kan
 */
public interface MessageType {

    public static final String LOGIN = "LOGIN";
    public static final String REGISTER = "REGISTER";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String MESSAGE = "MESSAGE";
    public static final String AUTH_YES = "AUTH_YES";
    public static final String AUTH_NO = "AUTH_NO";
    public static final String CONTACT_LIST = "CONTACT_LIST";
    public static final String UPDATE_CONTACT_LIST = "UPDATE_CONTACT_LIST";
    public static final String STATE_CHANGE = "STATE_CHANGE";
    public static final String VALIDATE_EMAIL = "VALIDATE_EMAIL";
    public static final String EMAIL_VALID = "EMAIL_VALID";
    public static final String EMAIL_INVALID = "EMAIL_INVALID";
    public static final String FILE_REQUEST = "FILE_REQUEST";
    public static final String FILE_RESPONSE = "FILE_RESPONSE";
    public static final String DELETE = "DELETE";
    public static final String VOICE_REQUEST = "VOICE_REQUEST";
    public static final String VOICE_RESPONSE = "VOICE_RESPONSE";
    public static final String VOICE_GRANTED = "VOICE_GRANTED";
    public static final String VOICE_TERMINATE = "VOICE_TERMINATE";
    public static final String ANNOUNCEMENT = "ANNOUNCEMENT";

}

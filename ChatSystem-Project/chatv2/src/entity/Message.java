package entity;

import entity.User;

import javax.swing.*;
import java.io.Serializable;

public class Message implements Serializable {
    private String sender;
    private String reciever;
    private String text;
    private ImageIcon picture;
    private String time;
    private String date;
    private User user;
    private String tagged;  //String som används för taggning, hjälper unRead.
    private User[] receivers;

    //TODO: Måste lösa det med receivers!! vi kan inte skicka ett meddelande som inte är associerad med en mottagare
    //TODO: separat variabel för gruppchatt?
    public Message(User user, String date, String time, String text, String reciever) {
        this.user = user;
        this.text = text;
        this.time = time;
        this.date = date;
        this.reciever = reciever;
        //edvin 4head moment
    }

    public Message(User user, String date, String time, String text, ImageIcon picture, String reciever) {
        this.user = user;
        this.text = text;
        this.time = time;
        this.date = date;
        this.picture = picture;
        this.reciever = reciever;
    }

    public String getTagged() {
        return tagged;
    }

    public User getUser() {
        return user;
    }

    public String getReciever() {
        return reciever;
    }

    public String getText() {
        return text;
    }

    public ImageIcon getPicture() {
        return picture;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        String textOut = String.format(getTime() + ", " + getUser().getUsername() + ": " + getText() + "\n");
        return textOut;
    }
}
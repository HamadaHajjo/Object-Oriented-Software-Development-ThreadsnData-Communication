package entity;

import javax.swing.*;
import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String username;
    private ImageIcon profileImage;
    private String ip;
    private int port;
    private ArrayList<User> friendList = new ArrayList<>();

    public User(String username, ImageIcon icon) {
        this.username = username;
        this.profileImage = icon;
    }

    public String getUsername() {
        return username;
    }


    public ImageIcon getProfileImage() {
        return profileImage;
    }

    public void addFriend(User friend) {
        this.friendList.add(friend);
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setProfileImage(ImageIcon profileImage) {
        this.profileImage = profileImage;
    }

    public int hashCode() {
        return username.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof User) {
            return username.equals(((User) obj).getUsername());
        }
        return false;

    }

    public ArrayList<User> getFriendList() {
        return friendList;
    }
}

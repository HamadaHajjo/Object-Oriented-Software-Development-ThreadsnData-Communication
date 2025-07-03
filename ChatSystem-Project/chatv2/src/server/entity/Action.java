package server.entity;

import entity.Message;
import entity.User;

import java.io.Serializable;
import java.util.Date;

public class Action implements Serializable {
    private ActionType actionType;
    private User user;
    private Message message;
    private Date date;

    public Action(ActionType actionType, User user, Message message, Date date) {
        this.actionType = actionType;
        this.user = user;
        this.message = message;
        this.date = date;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public User getUser() {
        return user;
    }

    public Message getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public String toString() {
        String actionTypeString = actionType.toString().toLowerCase().replace("_", " ");
        actionTypeString = actionTypeString.substring(0, 1).toUpperCase() + actionTypeString.substring(1);
        String userOrMessage = (user != null) ? user.getUsername() : message.getUser().getUsername() + " said: " + message.getText();
        return "[" + date + "] " + actionTypeString + ": " + userOrMessage;
    }
}

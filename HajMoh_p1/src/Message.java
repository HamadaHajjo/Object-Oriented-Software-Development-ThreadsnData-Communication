package src;

import javax.swing.*;

public class Message {
    private String text;
    private Icon icon;

    public Message(String text, Icon icon) {
        this.text = text;
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public Icon getIcon() {
        return icon;
    }
}
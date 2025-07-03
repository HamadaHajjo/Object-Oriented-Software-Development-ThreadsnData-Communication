package client.boundary;

import javax.swing.*;
import java.awt.*;

/**
 * User has the option to display messages in a different window. This could be a solution to show Image on this window instead of the ChatFrame window.
 */
public class MessageWindow {
    private ChatFrame chatFrame;
    private Frame frame;
    private JPanel mainpnl;
    private JPanel northPnl;
    private JPanel southPnl;
    private ImageIcon currentIcon;
    private String msg;
    private JLabel avatar;
    private JTextArea txtmsg;

    public MessageWindow(boolean imageExists) {
        frame = new JFrame();
        frame.setTitle("Display Message");
        mainpnl = new JPanel();
        mainpnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainpnl.setLayout(new BoxLayout(mainpnl, BoxLayout.Y_AXIS ));
        mainpnl.setBackground(Color.white);
        mainpnl.add(setUpTheNorthPanel(), BorderLayout.CENTER); //setting up txt

        if (imageExists) { //if image exists, add this
            mainpnl.add(setUpTheSouthPanel(), BorderLayout.SOUTH); //setting up img
            frame.add(mainpnl);

            frame.pack();
            frame.setSize(350, 500);
            frame.setVisible(true);
            frame.setResizable(false);
            return;
        }

        frame.add(mainpnl);
        frame.pack();
        frame.setSize(350, 300);
        frame.setVisible(true);
        frame.setResizable(false);

    }

    public MessageWindow(String[] friendList, ChatFrame chatFrame) {
        this.chatFrame = chatFrame;
        frame = new JFrame();
        frame.setTitle("Select Friend");
        mainpnl = new JPanel();
        mainpnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainpnl.setLayout(new BoxLayout(mainpnl, BoxLayout.Y_AXIS ));
        mainpnl.setBackground(Color.white);
        mainpnl.add(setUpFriendList(friendList), BorderLayout.CENTER);

        frame.add(mainpnl);
        frame.pack();
        frame.setSize(200, 300);
        frame.setVisible(true);
        frame.setResizable(false);

    }


    public MessageWindow(String[] friendList) {
        this.chatFrame = chatFrame;
        frame = new JFrame();
        frame.setTitle("Select Friend");
        mainpnl = new JPanel();
        mainpnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainpnl.setLayout(new BoxLayout(mainpnl, BoxLayout.Y_AXIS ));
        mainpnl.setBackground(Color.white);
        mainpnl.add(setUpFriendList(friendList), BorderLayout.CENTER);

        frame.add(mainpnl);
        frame.pack();
        frame.setSize(200, 300);
        frame.setVisible(true);
        frame.setResizable(false);

    }

    public MessageWindow(String[] messages, boolean messagesonly) {
        frame = new JFrame();
        frame.setTitle("Unread Messages");
        mainpnl = new JPanel();
        mainpnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainpnl.setLayout(new BoxLayout(mainpnl, BoxLayout.Y_AXIS ));
        mainpnl.setBackground(Color.white);
        mainpnl.add(setUpMessages(messages), BorderLayout.CENTER);

        frame.add(mainpnl);
        frame.pack();
        frame.setSize(340, 400);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    public MessageWindow(ImageIcon picture) {
        frame = new JFrame();
        frame.setTitle("Sent Picture");
        mainpnl = new JPanel();
        mainpnl.setLayout(new BoxLayout(mainpnl, BoxLayout.Y_AXIS ));
        mainpnl.setBackground(Color.white);
        mainpnl.add(setUpImage(picture), BorderLayout.CENTER);

        frame.add(mainpnl);
        frame.pack();
        frame.setSize(200, 220);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    private JPanel setUpImage(ImageIcon picture) {
        JPanel imgPanel = new JPanel();
        avatar = new JLabel(picture);

        imgPanel.add(avatar, Component.CENTER_ALIGNMENT);
        imgPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        return imgPanel;
    }

    private JPanel setUpMessages(String[] messages) {
        JPanel msgPnl = new JPanel();
        msgPnl.setLayout(new BoxLayout(msgPnl, BoxLayout.Y_AXIS));

        JLabel showOffline = new JLabel("Displaying unread messages");

        JList showMessage = new JList();
        showMessage.setLayoutOrientation(JList.VERTICAL);
        JScrollPane scrollFriends = new JScrollPane(showMessage);
        showMessage.setListData(messages);

        msgPnl.add(showOffline);
        msgPnl.add(scrollFriends);

        return msgPnl;
    }

    private JPanel setUpFriendList(String[] friendList) {
        JPanel friendPnl = new JPanel();
        friendPnl.setLayout(new BoxLayout(friendPnl, BoxLayout.Y_AXIS));

        JLabel showOffline = new JLabel("SELECT OFFLINE FRIEND");

        JList showOfflineFriends = new JList();
        showOfflineFriends.setLayoutOrientation(JList.VERTICAL);
        JScrollPane scrollFriends = new JScrollPane(showOfflineFriends);
        showOfflineFriends.setListData(friendList);

        JButton selectUser = new JButton("Select Friend");
        selectUser.addActionListener(e -> showOfflineFriendOnGUI(showOfflineFriends.getSelectedIndex(), friendList));
        selectUser.setBackground(Color.white);

        friendPnl.add(showOffline);
        friendPnl.add(scrollFriends);
        friendPnl.add(selectUser);

        return friendPnl;
    }

    public String showOfflineFriendOnGUI(int selectedIndex, String[] friendList) {
        frame.dispose();
        chatFrame.displayFriendInfoOnGUI(friendList[selectedIndex]);
        return friendList[selectedIndex];
    }

    private JPanel setUpTheNorthPanel() { //display text
        northPnl = new JPanel();
        northPnl.setLayout(new BoxLayout(northPnl, BoxLayout.Y_AXIS));

        txtmsg = new JTextArea(getMsg());
        txtmsg.setLineWrap(true);
        txtmsg.setWrapStyleWord(true);
        JScrollPane scrollMsg = new JScrollPane(txtmsg);
        scrollMsg.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollMsg.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        northPnl.add(scrollMsg);
        return northPnl;
    }


    private JPanel setUpTheSouthPanel() { //display img
        southPnl = new JPanel();
        southPnl.setLayout(new BoxLayout(southPnl, BoxLayout.Y_AXIS));

        avatar = new JLabel(getCurrentIcon());

        southPnl.add(avatar, Component.CENTER_ALIGNMENT);
        southPnl.add(Box.createRigidArea(new Dimension(0, 10)));

        return southPnl;
    }


    public void setCurrentIcon(ImageIcon currentIcon) {
        this.avatar.setIcon(currentIcon);
    }

    public ImageIcon getCurrentIcon() {
        return currentIcon;
    }

    public void setMsg(String msg) {
        this.txtmsg.setText(msg);
    }

    public String getMsg() {
        return msg;
    }

}

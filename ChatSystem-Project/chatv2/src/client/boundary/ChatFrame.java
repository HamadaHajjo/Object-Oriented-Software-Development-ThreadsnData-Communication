package client.boundary;

import client.control.ChatClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ChatFrame { //chat frame
    private JFrame frame;
    private JPanel mainpnl;
    private JPanel WESTpnl;
    private JPanel EASTpnl;
    private String[] chatHistory;
    private static ArrayList<String[]> msgHistoryWPeople = new ArrayList<String[]>();
    private JLabel lblDate;
    private JTextArea chatBox;
    private JPanel CENTERpnl;
    private JList<String> mainChatBox = new JList<>();
    private JList showFriends; //shows list of friends
    private JList showOnline;
    private String[] showFriendsStringArray; //shows list of friends
    private String[] showOnlineStringArray;
    private final int width = 900;
    private final int height = 600;
    private JFileChooser fileChooser;
    private File chosenImage;
    private JLabel chosenPic;
    private ChatClient chatClient;
    private JScrollPane scrollPaneForMainChatBox;
    private JScrollBar scrollBar;
    private JLabel nameOfUser;
    private JLabel userAvatar;
    private boolean UserUploadedNewImage;
    private ArrayList<Icon> storedImage;
    private MessageWindow msgFrame;
    private String name;
    private ChatFrame chatFrame;
    private ArrayList<String[]> msgsWithThisUser;
    private int currentIndex;
    private int tempIndex = 1;
    private JButton sendMsg;
    private JButton viewMsg;
    private JButton msgToFriend;


    public ChatFrame(String name, ChatClient chatClient) {
        this.chatClient = chatClient;
        chatClient.registerListener(chatClient);
        storedImage = new ArrayList<>();
        frame = new JFrame();
        this.name = name;
        msgsWithThisUser = new ArrayList<String[]>();
        frame.setTitle("Chat Window for " + name); //ink. vem fönstret tillhör till, behöver endast string troligtvist
        frame.setPreferredSize(new Dimension(width, height));

        mainpnl = new JPanel();
        mainpnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainpnl.setLayout(new BorderLayout());

        mainpnl.add(setUpTheWestPanel(), BorderLayout.WEST);
        mainpnl.add(setUpTheCenterPanel(), BorderLayout.CENTER);
        mainpnl.add(setUpTheEastPanel(), BorderLayout.EAST);

        frame.add(mainpnl);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        showOnlineStringArray = new String[100];
        showFriendsStringArray = new String[100];
        chatHistory = new String[100];
    }

    private JPanel setUpTheWestPanel() {
        WESTpnl = new JPanel();
        WESTpnl.setLayout(new BoxLayout(WESTpnl, BoxLayout.Y_AXIS));

        //NORTH FRIENDS LIST
        JPanel wNorthPnl = new JPanel();
        wNorthPnl.setLayout(new BoxLayout(wNorthPnl, BoxLayout.Y_AXIS));

        JLabel lblFriendList = new JLabel("Friend list");//TEXT
        lblFriendList.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFriendList.setPreferredSize(new Dimension(lblFriendList.getPreferredSize().width, 25));
        wNorthPnl.add(lblFriendList);

        //JLIST
        showFriends = new JList();
        showFriends.setLayoutOrientation(JList.VERTICAL);
        showFriends.setEnabled(false);
        JScrollPane scrollFriends = new JScrollPane(showFriends);
        scrollFriends.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) (height * 0.5)));
        wNorthPnl.add(scrollFriends);

        // SOUTH ONLINE LIST
        JPanel wSouthPnl = new JPanel();
        wSouthPnl.setLayout(new BoxLayout(wSouthPnl, BoxLayout.Y_AXIS));

        JLabel lblOfflineList = new JLabel("Online list"); //TEXT
        lblOfflineList.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblOfflineList.setPreferredSize(new Dimension(lblOfflineList.getPreferredSize().width, 25));
        wNorthPnl.add(lblOfflineList);

        //JLIST
        showOnline = new JList();
        showOnline.setLayoutOrientation(JList.VERTICAL);
        JScrollPane scrollOnline = new JScrollPane(showOnline);
        scrollOnline.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) (height * 0.4)));
        wNorthPnl.add(scrollOnline);

        JButton viewUser = new JButton("View User");
        viewUser.setBackground(Color.white);
        viewUser.addActionListener(e -> checkIfUserExists(showOnline.getSelectedIndex()));

        WESTpnl.add(wNorthPnl);
        WESTpnl.add(wSouthPnl);
        WESTpnl.add(Box.createRigidArea(new Dimension(0, 5)));
        WESTpnl.add(viewUser, Component.CENTER_ALIGNMENT);

        WESTpnl.setPreferredSize(new Dimension((int) (width * 0.18), (int) (height * 0.25)));

        setCurrentIndex(0);
        return WESTpnl;
    }

    private void checkIfUserExists(int selectedIndex) {
        boolean ok = chatClient.checkIfUserExists(selectedIndex);
        if (ok) {
            setCurrentIndex(selectedIndex);
            chatHistory = msgHistoryWPeople.get(getCurrentIndex());
            mainChatBox.setListData(chatHistory);
            chatClient.retrieveUserInfo(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid input, try again or you attempted to chat with yourself.");
        }
    }

    private JPanel setUpTheCenterPanel() {
        CENTERpnl = new JPanel();
        CENTERpnl.setLayout(new BoxLayout(CENTERpnl, BoxLayout.Y_AXIS));

        //DATE LABEL
        lblDate = new JLabel(getTodaysDate());
        lblDate.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDate.setPreferredSize(new Dimension(lblDate.getPreferredSize().width, 14));
        CENTERpnl.add(lblDate);

        //CHATBOX:EN
        JPanel cSouthPnl = new JPanel();
        cSouthPnl.setBorder(BorderFactory.createEmptyBorder(0, 5, 15, 5));

        mainChatBox.setLayoutOrientation(JList.VERTICAL);
        mainChatBox.setPreferredSize(new Dimension((int) (width * 0.4), (int) (height * 0.4)));
        scrollPaneForMainChatBox = new JScrollPane(mainChatBox);
        scrollPaneForMainChatBox.setPreferredSize(new Dimension((int) (width * 0.4), (int) (height * 0.4)));
        scrollPaneForMainChatBox.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneForMainChatBox.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollBar = scrollPaneForMainChatBox.getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());

        JLabel writeMsg = new JLabel("WRITE A MESSAGE");

        chatBox = new JTextArea();
        chatBox.setPreferredSize(new Dimension((int) (width * 0.40), (int) (height * 0.2)));
        chatBox.setLineWrap(true);
        chatBox.setWrapStyleWord(true);
        chatBox.setEditable(false);
        JScrollPane scrollMsg = new JScrollPane(chatBox);
        scrollMsg.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollMsg.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        cSouthPnl.add(writeMsg);
        cSouthPnl.add(scrollMsg);
        CENTERpnl.add(scrollPaneForMainChatBox);

        //"SEND MESSAGE TO FRIENDS" BUTTON
        msgToFriend = new JButton("Send Message To Offline Friend");
        msgToFriend.addActionListener(e -> displayFriendWindow());
        msgToFriend.setAlignmentX(Component.CENTER_ALIGNMENT);
        msgToFriend.setBackground(Color.white);
        msgToFriend.setEnabled(true);

        sendMsg = new JButton("Send Message");
        sendMsg.addActionListener(e -> sendMessage(showOnline.getSelectedIndex()));
        sendMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        sendMsg.setBackground(Color.white);
        sendMsg.setEnabled(false);

        viewMsg = new JButton("View Selected Message");
        viewMsg.addActionListener(e -> viewMsg(mainChatBox.getSelectedIndex()));
        viewMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewMsg.setBackground(Color.white);
        //viewMsg.setEnabled(false);

        CENTERpnl.add(cSouthPnl);
        CENTERpnl.add(sendMsg);
        CENTERpnl.add(viewMsg);
        CENTERpnl.add(msgToFriend);
        CENTERpnl.setPreferredSize(new Dimension((int) (width * 0.5), (int) (height * 0.5)));
        CENTERpnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return CENTERpnl;
    }


    private JPanel setUpTheEastPanel() {
        EASTpnl = new JPanel();
        EASTpnl.setLayout(new BoxLayout(EASTpnl, BoxLayout.Y_AXIS));

        EASTpnl.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton logOut = new JButton("Log out");
        logOut.setBackground(Color.white);
        logOut.addActionListener(e -> logUserOut());
        logOut.setAlignmentX(Component.CENTER_ALIGNMENT);
        EASTpnl.add(logOut);

        EASTpnl.add(Box.createRigidArea(new Dimension(0, 30)));
        JLabel UserYouAreChattingWith = new JLabel("YOU ARE CHATTING WITH");
        UserYouAreChattingWith.setFont(new Font("Arial", Font.BOLD, 16));
        UserYouAreChattingWith.setAlignmentX(Component.CENTER_ALIGNMENT);
        EASTpnl.add(UserYouAreChattingWith);

        JPanel eCenterPnl = new JPanel();
        eCenterPnl.setLayout(new BoxLayout(eCenterPnl, BoxLayout.Y_AXIS));
        nameOfUser = new JLabel("NULL"); //name of user
        nameOfUser.setFont(new Font("Arial", Font.BOLD, 16));
        nameOfUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        eCenterPnl.add(nameOfUser);

        //IMAGE
        ImageIcon resizedIcon = new ImageIcon(resizeImage("files/images/NoImageAvailable.jpeg", 250, 250).getImage());
        userAvatar = new JLabel(resizedIcon);
        userAvatar.setPreferredSize(new Dimension(resizedIcon.getIconWidth(), resizedIcon.getIconHeight()));
        userAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        eCenterPnl.add(userAvatar, BorderLayout.NORTH);
        eCenterPnl.setPreferredSize(new Dimension(250, 250));
        EASTpnl.add(eCenterPnl, Component.CENTER_ALIGNMENT);

        //SOUTH PANEL OF THE EAST PANEL, should be dealing with uploading image specifically
        JPanel eSouthPnl = new JPanel();
        eSouthPnl.setLayout(new BoxLayout(eSouthPnl, BoxLayout.Y_AXIS));
        ImageIcon uploadedPicture = new ImageIcon(resizeImage("files/images/NoImageAvailable.jpeg", 160, 150).getImage());
        chosenPic = new JLabel(uploadedPicture);
        chosenPic.setPreferredSize(new Dimension(resizedIcon.getIconWidth(), resizedIcon.getIconHeight()));
        chosenPic.setAlignmentX(Component.CENTER_ALIGNMENT);

        //BUTTON FOR UPLOADING IMAGE
        JButton addUserAsFriend = new JButton("ADD FRIEND");
        addUserAsFriend.setBackground(Color.white);
        addUserAsFriend.setAlignmentX(Component.CENTER_ALIGNMENT);
        addUserAsFriend.addActionListener(e -> addFriend(nameOfUser.getText()));
        eSouthPnl.add(addUserAsFriend);

        eSouthPnl.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton choosePictureToUpload = new JButton("UPLOAD IMAGE"); //ska tas bort sen
        choosePictureToUpload.setBackground(Color.white);
        choosePictureToUpload.setAlignmentX(Component.CENTER_ALIGNMENT);
        eSouthPnl.add(choosePictureToUpload);
        choosePictureToUpload.addActionListener(e -> uploadImage());
        eSouthPnl.add(chosenPic);

        EASTpnl.add(Box.createRigidArea(new Dimension(0, 10)));
        EASTpnl.add(eSouthPnl);

        EASTpnl.setPreferredSize(new Dimension((int) (width * 0.33), (int) (height * 0.25)));
        return EASTpnl;
    }

    private void viewMsg(int selectedIndex) {
        if (storedImage.get(selectedIndex) == null) {
            msgFrame = new MessageWindow(false);
            msgFrame.setMsg(chatHistory[selectedIndex]);
        } else {
            msgFrame = new MessageWindow(true);
            msgFrame.setMsg(chatHistory[selectedIndex]);
            msgFrame.setCurrentIcon((ImageIcon) storedImage.get(selectedIndex));
        }
    }

    private void sendMessage(int selectedIndex) { //TODO: Registrera meddelande men vem anropar den? ChatClient eller ChatServer?
        if (selectedIndex != -1) {
            this.chatHistory = msgHistoryWPeople.get(selectedIndex);
            this.mainChatBox.setListData(chatHistory);

            if (UserUploadedNewImage && !chatBox.getText().isEmpty()) { // if picture contains
                for (int i = 0; i < chatHistory.length; i++) {
                    if (chatHistory[i] == null) {
                        this.chatHistory[i] = this.chatClient.sendMessage(getTodaysDate(), getCurrentTime(), chatBox.getText() + " [PICTURE]", (ImageIcon) chosenPic.getIcon(), nameOfUser.getText());
                        this.storedImage.add(chosenPic.getIcon());
                        this.mainChatBox.setListData(chatHistory);
                        this.msgHistoryWPeople.set(selectedIndex, chatHistory);
                        this.chatBox.setText("");
                        this.UserUploadedNewImage = false;
                        ImageIcon uploadedPicture = new ImageIcon(resizeImage("files/images/NoImageAvailable.jpeg", 160, 150).getImage());
                        this.chosenPic.setIcon(uploadedPicture);
                        break;
                    }
                }
            } else if (!UserUploadedNewImage && !chatBox.getText().isEmpty()) { //if no image
                for (int i = 0; i < chatHistory.length; i++) {
                    if (chatHistory[i] == null) {
                        this.chatHistory[i] = this.chatClient.sendMessage(getTodaysDate(), getCurrentTime(), chatBox.getText(), nameOfUser.getText());
                        this.storedImage.add(null);
                        this.mainChatBox.setListData(chatHistory);
                        this.msgHistoryWPeople.set(selectedIndex, chatHistory);
                        this.chatBox.setText("");
                        break;
                    }
                }
            }
        } else {
            //this.chatHistory = msgHistoryWPeople.get(selectedIndex);
            this.mainChatBox.setListData(chatHistory);

            if (UserUploadedNewImage && !chatBox.getText().isEmpty()) { // if picture contains
                for (int i = 0; i < chatHistory.length; i++) {
                    if (chatHistory[i] == null) {
                        this.chatHistory[i] = this.chatClient.sendMessage(getTodaysDate(), getCurrentTime(), chatBox.getText() + " [PICTURE]", (ImageIcon) chosenPic.getIcon(), nameOfUser.getText());
                        this.storedImage.add(chosenPic.getIcon());
                        this.mainChatBox.setListData(chatHistory);
                        //this.msgHistoryWPeople.set(selectedIndex, chatHistory);
                        this.chatBox.setText("");
                        this.UserUploadedNewImage = false;
                        ImageIcon uploadedPicture = new ImageIcon(resizeImage("files/images/NoImageAvailable.jpeg", 160, 150).getImage());
                        this.chosenPic.setIcon(uploadedPicture);
                        break;
                    }
                }
            } else if (!UserUploadedNewImage && !chatBox.getText().isEmpty()) { //if no image
                for (int i = 0; i < chatHistory.length; i++) {
                    if (chatHistory[i] == null) {
                        this.chatHistory[i] = this.chatClient.sendMessage(getTodaysDate(), getCurrentTime(), chatBox.getText(), nameOfUser.getText());
                        this.storedImage.add(null);
                        this.mainChatBox.setListData(chatHistory);
                        //this.msgHistoryWPeople.set(selectedIndex, chatHistory);
                        this.chatBox.setText("");
                        break;
                    }
                }
            }
        }
    }

    public void receiveMessage(String fullmsg, ImageIcon picOfUser, String username) { // if no image has been received
        frame.setTitle("Chat Window for " + name + " --- NEW MESSAGE FROM " + username);

        this.chatHistory = msgHistoryWPeople.get(getCurrentIndex());
        this.mainChatBox.setListData(chatHistory);
        if (getCurrentIndex() == tempIndex) {
            displayUserInfoOnGUI(username, picOfUser);
            for (int i = 0; i < chatHistory.length; i++) {
                if (chatHistory[i] == null) {
                    this.chatHistory[i] = fullmsg;
                    this.storedImage.add(null);
                    this.mainChatBox.setListData(chatHistory);
                    msgHistoryWPeople.set(getCurrentIndex(), chatHistory);
                    scrollBar.setValue(scrollBar.getMaximum());
                    frame.setTitle("Chat Window for " + name);
                    break;
                }
            }
        }
    }

    //receives message that a user has sent to them
    public void receiveMessage(String fullmsg, ImageIcon picSent, ImageIcon picOfUser, String username) {
        displayUserInfoOnGUI(username, picOfUser);
        this.chatHistory = msgHistoryWPeople.get(getCurrentIndex());
        this.mainChatBox.setListData(chatHistory);
        for (int i = 0; i < chatHistory.length; i++) {
            if (chatHistory[i] == null) {
                this.chatHistory[i] = fullmsg;
                this.storedImage.add(picSent);
                this.mainChatBox.setListData(chatHistory);
                this.msgHistoryWPeople.set(getCurrentIndex(), chatHistory);
                break;
            }
        }
    }

    private void scrollToBottom(JScrollPane scrollPaneForMainChatBox) {
        JScrollBar verticalScrollBar = scrollPaneForMainChatBox.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
    }

    private String getTodaysDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        return formattedDate;
    }

    public String getCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
        return currentTime.format(formatter);
    }


    private void logUserOut() {
        this.chatClient.logOut();
    }

    private void addFriend(String nameofUser) {
        boolean ok = chatClient.checkIfFriendIsAlreadyAdded(nameofUser);
        if (ok) { //ifall en vän redan finns
            JOptionPane.showMessageDialog(null, "You've already added this friend");
        } else {
            chatClient.addUsertoFriendlist(nameofUser);
        }
    }


    private ImageIcon resizeImage(String s, int width, int height) {
        ImageIcon imgIcon = new ImageIcon("files/images/NoImageAvailable.jpeg");
        Image imgB4Resize = imgIcon.getImage();
        Image newImage = imgB4Resize.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(newImage);
        return resizedIcon;
    }

    /**
     * shows on the online list who is online
     * @param listToPopulate
     */
    public void populateOnlineList(String[] listToPopulate) {
        tempIndex++;
        String[] empty = new String[1];
        empty[0] = "";
        showOnline.setListData(empty);
        showOnline.setListData(listToPopulate);
        String[] tempChat = new String[100];
        msgHistoryWPeople.add(tempChat);
    }

    public void populateFriendList(String friendname) {
        for (int i = 0; i < showFriendsStringArray.length; i++) {
            if (showFriendsStringArray[i] == null) {
                showFriendsStringArray[i] = friendname;
                break;
            }
        }
        showFriends.setListData(showFriendsStringArray);
    }

    public void populateFriendList(String[] friendlist) {
        showFriends.setListData(friendlist);
    }

    private void uploadImage() {
        fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(null);
        chosenImage = fileChooser.getSelectedFile();
        try {
            ImageIcon imageIcon = new ImageIcon(ImageIO.read(new File(chosenImage.getAbsolutePath())));
            Image image = imageIcon.getImage();
            Image newImage = image.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(newImage);
            chosenPic.setIcon(imageIcon);
            UserUploadedNewImage = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void displayUserInfoOnGUI(String username, ImageIcon profileImage) {
        for (int i = 0; i < showOnlineStringArray.length; i++) {
            if (showOnlineStringArray[i] == username) {
                setCurrentIndex(i);
                break;
            }
        }

        chatHistory = msgHistoryWPeople.get(getCurrentIndex());
        mainChatBox.setListData(chatHistory);

        sendMsg.setEnabled(true);
        nameOfUser.setText(username);
        ImageIcon resizedIcon = new ImageIcon(resizeImage(profileImage.getImage(), 250, 250).getImage());
        userAvatar.setIcon(resizedIcon);
        userAvatar.setPreferredSize(new Dimension(resizedIcon.getIconWidth(), resizedIcon.getIconHeight()));
        chatBox.setEditable(true);
    }

    private ImageIcon resizeImage(Image image, int width, int height) {
        Image updatedImg = new ImageIcon(image).getImage();
        ImageIcon imgIcon = new ImageIcon(updatedImg);
        Image imgB4Resize = imgIcon.getImage();
        Image newImage = imgB4Resize.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(newImage);
        return resizedIcon;
    }

    public void disposeFrame() {
        frame.dispose();
    }

    public ChatFrame getChatFrame() {
        return chatFrame;
    }

    public String getName() {
        return name;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public void populateOfflineFriends(String[] convertArrayListToString) {
        MessageWindow offlineFriends = new MessageWindow(convertArrayListToString, this);
    }

    private void displayFriendWindow() {
        chatClient.displayFriendList();
    }

    public void displayFriendInfoOnGUI(String nameofOfflineFriend) {
        chatClient.displayFriendInfoOnGUI(nameofOfflineFriend);
    }

    public void displayOfflineFriendOnGUI(String username, ImageIcon profileImage) {
        sendMsg.setEnabled(true);
        nameOfUser.setText(username);
        ImageIcon resizedIcon = new ImageIcon(resizeImage(profileImage.getImage(), 250, 250).getImage());
        userAvatar.setIcon(resizedIcon);
        userAvatar.setPreferredSize(new Dimension(resizedIcon.getIconWidth(), resizedIcon.getIconHeight()));
        chatBox.setEditable(true);
    }
}
package client.control;

import client.boundary.*;
import entity.Message;
import entity.User;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatClient {
    //ChatFrame for chat
    private ChatFrame chatFrame;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Socket socket;
    private Message message;
    private User clientUser = new User(null, null);
    private ServerSocket serverSocket;
    private ArrayList<User> friendList;
    private LoginFrame loginFrame;
    private HashMap<String, List<Message>> unreadMessages = new HashMap<>();

    public ArrayList<User> getActiveUsers() {
        return activeUsers;
    }

    private ArrayList<User> activeUsers = new ArrayList<>();
    private ArrayList<User> buffer = new ArrayList<>();
    private ArrayList<ChatClient> callArrayList;
    private User groupchat;

    public static void main(String[] args) throws IOException {
        new ChatClient("localhost", 721);
    }


    public ChatClient(String ip, int port) throws IOException {
        friendList = new ArrayList<>();
        socket = new Socket(ip, port);
        createLoginFrame(ip, port);
        friendList = new ArrayList<>();
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        callArrayList = new ArrayList<>();
        new Listener().start();
        activeUsers.add(groupchat = new User("Group chat", new ImageIcon("files/images/group.jpg"))); //lägger in gruppchatt direkt
    }

    private void createLoginFrame(String ip, int port) throws IOException {
        this.loginFrame = new LoginFrame(this, ip, port);
    }

    public boolean checkIfUserExists(int selectedIndex) {
        if (activeUsers.get(selectedIndex) == null) {
            return false;
        } else if (activeUsers.get(selectedIndex).getUsername().equals(clientUser.getUsername().toString())) {
            return false;
        }
        return true;
    }

    public void retrieveUserInfo(int selectedIndex) {
        chatFrame.displayUserInfoOnGUI(activeUsers.get(selectedIndex).getUsername(), activeUsers.get(selectedIndex).getProfileImage());
    }

    public boolean checkIfFriendIsAlreadyAdded(String nameofUser) {
        for (int i = 0; i < clientUser.getFriendList().size(); i++) {
            if (nameofUser.equals(clientUser.getFriendList().get(i).getUsername()) && !nameofUser.equals(groupchat.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public boolean logOut() { //logs out the current client
        try {
            saveUserInstanceToFile();
            System.out.println("Saving user updates...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        chatFrame.disposeFrame();

        try {
            oos.writeObject(clientUser.getUsername());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public void registerListener(ChatClient chatClient) {
        if (!callArrayList.contains(chatClient)) {
            callArrayList.add(chatClient);
        }
    }

    public void displayFriendList() {
        ArrayList<User> offlineFriends = new ArrayList<>();
        for (User friend : clientUser.getFriendList()) {
            if (!activeUsers.contains(friend)) {
                offlineFriends.add(friend);
            }
        }
        chatFrame.populateOfflineFriends(convertArrayListToString(offlineFriends));
    }

    public void displayFriendInfoOnGUI(String nameofOfflineFriend) {
        for (int i = 0; i < clientUser.getFriendList().size(); i++) {
            if (nameofOfflineFriend.equals(clientUser.getFriendList().get(i).getUsername())) {
                chatFrame.displayOfflineFriendOnGUI(clientUser.getFriendList().get(i).getUsername(), clientUser.getFriendList().get(i).getProfileImage());
                return;
            }
        }
    }

    //Different version of listener that implements updating the arraylist of online users from server to chatframe
    public class Listener extends Thread {
        @Override
        public void run() {
            String offlineUser = null;
            try {
                while (true) {
                    Thread.sleep(200);
                    Object obj = ois.readObject();
                    if (obj instanceof User) {
                        User user = (User) obj;
                        if (!(user.getUsername().equals(clientUser.getUsername()))) {
                            if (!activeUsers.contains(user)) {
                                activeUsers.add(user);
                            }
                            chatFrame.populateOnlineList(convertArrayListToString(activeUsers));
                        }
                    } else if (obj instanceof Message) {
                        Message message = (Message) obj;
                        if (message.getReciever().equals("Group chat")) { //om mottagaren är gruppchatten
                            if (!message.getUser().getUsername().equals(clientUser.getUsername())) {
                                if (message.getPicture() == null) { //om ingen bild finns
                                    chatFrame.receiveMessage(message.getTime() + ": " + message.getUser().getUsername() + ": " + message.getText(), groupchat.getProfileImage(), groupchat.getUsername());
                                } else {
                                    chatFrame.receiveMessage(message.getTime() + ": " + message.getUser().getUsername() + ": " + message.getText(), groupchat.getProfileImage(), message.getPicture(), groupchat.getUsername());
                                }
                            }
                        } else if (!(message.getUser().getUsername().equals(clientUser.getUsername()))) { //anropa denna metod till alla klienter vars meddelande var inte skickad av denna person
                            if (message.getReciever().equals(clientUser.getUsername())) {
                                if (message.getPicture() == null) { //om ingen bild finns
                                    chatFrame.receiveMessage(message.getTime() + ": " + message.getUser().getUsername() + ": " + message.getText(), message.getUser().getProfileImage(), message.getUser().getUsername());
                                } else {
                                    chatFrame.receiveMessage(message.getTime() + ": " + message.getUser().getUsername() + ": " + message.getText(), message.getPicture(), message.getUser().getProfileImage(), message.getUser().getUsername());
                                }
                            }
                        }
                        System.out.println(message.getUser().getUsername() + " is sending a message to " + message.getReciever());
                    } else if (obj instanceof HashMap<?, ?>) {
                        HashMap<String, List<Message>> unreadMessages = (HashMap<String, List<Message>>) obj;
                        List<String> messagesAsString = new ArrayList<>();
                        for (Map.Entry<String, List<Message>> entry : unreadMessages.entrySet()) {
                            List<Message> messages = entry.getValue();
                            for (Message message : messages) {
                                if (message.getPicture() == null) {
                                    String messageAsString = message.toString(); // Assuming Message class has a toString() method
                                    messagesAsString.add(messageAsString);
                                }
                                else {
                                    MessageWindow messageWindow = new MessageWindow(message.getPicture());
                                    String messageAsString = message.toString();
                                    messagesAsString.add(messageAsString);
                                }
                            }
                        }
                        MessageWindow messageWindow = new MessageWindow(convertArrayListToString(messagesAsString), false);
                    } else if (obj instanceof String) {
                        offlineUser = (String) obj;
                        if (!offlineUser.equals(clientUser.getUsername())) {
                            for (int i = 0; i < activeUsers.size(); i++) {
                                if (offlineUser.equals(activeUsers.get(i).getUsername())) {
                                    activeUsers.remove(i);
                                    chatFrame.populateOnlineList(convertArrayListToString(activeUsers));
                                    break;
                                }
                            }
                        }
                    }
                    oos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String[] convertArrayListToString(List<String> messagesAsString) {
        String[] arrString = new String[messagesAsString.size()];
        if (messagesAsString != null) {
            for (int i = 0; i < messagesAsString.size(); i++) {
                arrString[i] = messagesAsString.get(i).toString();
            }
        }
        return arrString;
    }

    public String[] convertArrayListToString(ArrayList<User> onlineUsers) {
        String[] arrString = new String[onlineUsers.size()];
        if (onlineUsers != null) {
            for (int i = 0; i < onlineUsers.size(); i++) {
                arrString[i] = onlineUsers.get(i).getUsername();
            }
        }
        return arrString;
    }

    /**
     * createUser method retrieves input from the chatFrame panel. Adds the variables to create a new User instance and connects it to instance variable clientUser.
     * After the User instance is created then the Connector thread starts
     *
     * @return user instance to send to server
     */
    public void createUser(String name, ImageIcon icon) {
        buffer.add(new User(name, icon));
        //premadeUser(clientUser);

        if (doesthisUserExistAlready()) { //if the user we get right now does match an instance of another user, then instansiate it as such
            loginFrame.setAvatar(loginFrame.resizeAvatar(clientUser.getProfileImage()));
            this.chatFrame = new ChatFrame(name, this); //skapa ett fönster ifall vi kan ansluta oss ?
            chatFrame.populateFriendList(convertArrayListToString(clientUser.getFriendList()));
        } else {
            this.clientUser = new User(name, icon);
            loginFrame.setAvatar(icon);
            chatFrame = new ChatFrame(name, this); //skapa ett fönster ifall vi kan ansluta oss ?
        }

        try {
            oos.writeObject(clientUser);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // loginFrame.closeFrame();
    }

    public void premadeUser() { //ENDAST FÖR ATT SPARA PREMADE USERS, RÖR EJ DENNA
        try (FileOutputStream fos = new FileOutputStream("files/premadeUsers.txt");
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    oos.writeObject(new User("Parfait", new ImageIcon("files/images/parfaitcookie.jpg")));
                    System.out.println("Parfait is being written...");
                } else if (i == 1) {
                    oos.writeObject(new User("Ginger", new ImageIcon("files/images/ginger.png")));
                    System.out.println("Gingerbrave is being written...");

                } else if (i == 2) {
                    oos.writeObject(new User("Eclair", new ImageIcon("files/images/eclair.png")));
                    System.out.println("Eclair is being written...");
                    break;
                }
            }
            oos.flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void registerThesePremadeUsersTo() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream("files/premadeUsers.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (
                FileOutputStream fos = new FileOutputStream("files/userList.txt");
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            while (ois.available() >= 0) {
                User tempUser = (User) ois.readObject();
                oos.writeObject(tempUser); //överför objekten från premade users till userList

            }
            oos.flush();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
        }
    }

    private boolean doesthisUserExistAlready() {
        User userToCheck = buffer.get(0);
        User userReadFromFile = null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("files/userList.txt"))) {
            //userReadFromFile = (User) ois.readObject();
            while ((userReadFromFile = (User) ois.readObject()) != null) {
                if (userToCheck.getUsername().equals(userReadFromFile.getUsername())) {
                    this.clientUser = userReadFromFile;
                    buffer.clear();
                    System.out.println("Found a registered user already");
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void saveUserInstanceToFile() throws IOException, ClassNotFoundException {
        List<User> userList = new ArrayList<>(); // Create a temporary list to hold the updated User objects.
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("files/userList.txt"));
        // Read each User object from the file and add it to the temporary list,
        // updating the existing User object with the client's User object if a match is found.
        boolean found = false;
        while (true) {
            try {
                User existingUser = (User) inputStream.readObject();
                if (existingUser.getUsername().equals(clientUser.getUsername())) {
                    userList.add(clientUser);
                    found = true;
                } else {
                    userList.add(existingUser);
                }
            } catch (EOFException e) {
                break;
            }
        }

        if (!found) { // If the clientUser was not found in the file, add it to the temporary list.
            userList.add(clientUser);
        }

        inputStream.close();

        // Create an object output stream to write User objects to the file.
        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("files/userList.txt"));

        for (User user : userList) { // Write each User object from the temporary list to the file.
            outputStream.writeObject(user);
        }

        outputStream.close();
    }


    /**
     * sendMessage method sends the message instance to the ObjectOutputStream and flushes it out.
     */

    public String sendMessage(String date, String time, String msg, String sender) {
        try {
            Message message = new Message(clientUser, date, time, msg, sender);
            oos.writeObject(message);
            oos.flush();
            return message.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendMessage(String date, String time, String text, ImageIcon icon, String sender) {
        try {
            Message message = new Message(clientUser, date, time, text, icon, sender);
            oos.writeObject(message);
            oos.flush();
            return message.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets user instance as parameter to add in the clientUsers friends list.
     *
     * @param friendname is user instance to show on contact list
     */
    public void addUsertoFriendlist(String friendname) {
        for (int i = 0; i < activeUsers.size(); i++) {
            if (friendname.equals(activeUsers.get(i).getUsername()) && !friendname.equals(groupchat.getUsername())) {
                clientUser.addFriend(activeUsers.get(i));
                chatFrame.populateFriendList(convertArrayListToString(clientUser.getFriendList()));
                //sendContactListToChatFrame(friendList);
            }
        }
    }
}

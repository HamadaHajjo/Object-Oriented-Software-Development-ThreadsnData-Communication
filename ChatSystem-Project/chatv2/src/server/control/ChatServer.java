package server.control;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import entity.Message;
import entity.User;
import server.boundary.ServerFrame;

public class ChatServer {
    private ArrayList<User> onlineUsers;                    //Lista av alla aktiva användare
    private ArrayList<ClientHandler> clientHandlers;        //Lista av alla ClientHandlers
    private HashMap<String, ClientHandler> clientsHashMap;       //HashMap för att associera användare med varsin ClientHandler
    private ServerFrame serverFrame;

    private LogController logController;

    private HashMap<String, List<Message>> unreadMessages;  //SHashMap där första parametern inehåller anvNamn och List av meddelanden som inte skickats
    private String nameOfOfflineUser;

    public static void main(String[] args) {
        new ChatServer(721);
    }

    public ChatServer(int port) {
        serverFrame = new ServerFrame(null);
        onlineUsers = new ArrayList<>();
        clientHandlers = new ArrayList<>();
        clientsHashMap = new HashMap<>();
        unreadMessages = new HashMap<>();
        logController = new LogController("files/log.dat", serverFrame);
        serverFrame.setLogController(logController);
        new Connection(this, port).start();
    }

    public void sendMessageToClients(Message message) {   //Bearbetar meddelandet, ser till att den skickas till rätt person(er)
        logController.logMessageSent(message);

        if (message.getReciever().equals("Group chat")) {
            for (int i = 0; i < onlineUsers.size(); i++) {
                clientsHashMap.get(onlineUsers.get(i).getUsername()).sendMessage(message);
            }
        } else {
            for (int i = 0; i < onlineUsers.size(); i++) {
                if (message.getReciever().equals(onlineUsers.get(i).getUsername())) {
                    clientsHashMap.get(onlineUsers.get(i).getUsername()).sendMessage(message);
                    break;
                } else if (!message.getReciever().equals(onlineUsers.get(i).getUsername())) {  //offline
                    logController.logUnreadMessageStored(message);

                    if (unreadMessages.containsKey(message.getReciever())) {    //if list contains messages already
                        List<Message> userUnread = unreadMessages.get(message.getReciever());
                        userUnread.add(message);
                        unreadMessages.put(message.getReciever(), userUnread);

                    } else {
                        List<Message> userUnread = new ArrayList<>();
                        userUnread.add(message);
                        unreadMessages.put(message.getReciever(), userUnread);
                    }
                }
            }
        }
    }

    public void updateClientHandlersWhoWentOffline(String nameOfOfflineUser) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.updateWhoWentOffline(nameOfOfflineUser);
        }
    }

    public void updateClientHandlers() {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.updateUserOnlineList(onlineUsers);
        }
    }

    private class Connection extends Thread {
        private final ChatServer server;
        private final int port;

        public Connection(ChatServer server, int port) {
            this.server = server;
            this.port = port;
        }

        public void run() {
            Socket socket = null;
            System.out.println("Servern startas upp...");
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    try {
                        socket = serverSocket.accept();
                        new ClientHandler(server, socket);
                    } catch (IOException e) {
                        System.err.println(e);
                        socket.close();
                    }
                }
            } catch (IOException e) {
                System.err.println(e);
            }
            System.out.println("Servern nerstängd...");
        }
    }

    private class ClientHandler extends Thread {
        private ChatServer serverController;    //Servern blir en kontroller som utför arbetet, den kallas på för att skicka etc.
        private Socket clientSocket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private User user;   //User för denna CLientHandler

        public ClientHandler(ChatServer server, Socket socket) {
            serverController = server;
            clientSocket = socket;
            try {
                ois = new ObjectInputStream(clientSocket.getInputStream());
                oos = new ObjectOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            start();
        }

        public void updateUserOnlineList(ArrayList<User> users) {
            for (int i = 0; i < onlineUsers.size(); i++) {
                if (!(onlineUsers.get(i).getUsername().equals(user.getUsername()))) {
                    try {
                        oos.writeObject(onlineUsers.get(i));
                        oos.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }


        public synchronized void run() {
            try {
                while (true) {
                    Object obj = ois.readObject();
                    if (obj instanceof Message) {
                        Message message = (Message) obj;
                        sendMessageToClients(message);
                    } else if (obj instanceof User) {
                        user = (User) obj;
                        onlineUsers.add(user);     //Läggs till i lista av aktiva användare
                        clientHandlers.add(this);  //Läggs till i lista av ClientHandlers
                        clientsHashMap.put(user.getUsername(), this);  //Associerar användare med ClientHandlers
                        updateClientHandlers();
                        logController.logUserConnect(user);
                        processUnread();
                    } else if (obj instanceof String) {
                        nameOfOfflineUser = (String) obj;
                        System.out.println(nameOfOfflineUser + " user that went offline");
                        disconnectClient();
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                oos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void sendMessage(Message message) {  //Sends message
            try {
                oos.writeObject(message);
                oos.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void processUnread() {
            for (Map.Entry<String, List<Message>> entry : serverController.unreadMessages.entrySet()) {
                String recipient = entry.getKey();
                List<Message> messages = entry.getValue();
                if (recipient.equals(user.getUsername())) {
                    System.out.println("Unread messages for " + recipient + ":");
                    for (Message message : messages) {
                        System.out.println("- " + message);
                        logController.logUnreadMessageSent(message);
                    }
                }
            }


            for (int i = 0; i < onlineUsers.size(); i++) {
                if (serverController.unreadMessages.containsKey(user.getUsername())) {
                    try {
                        oos.writeObject(unreadMessages);
                        oos.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
            }
        }

        public void disconnectClient() {
            for (int i = 0; i < onlineUsers.size(); i++) {
                if (nameOfOfflineUser.equals(onlineUsers.get(i).getUsername())) {
                    logController.logUserDisconnect(onlineUsers.get(i));
                    onlineUsers.remove(onlineUsers.get(i));
                    clientHandlers.remove(this); //vrf this ?
                    clientsHashMap.remove(nameOfOfflineUser, this);
                    break;
                }
            }

            serverController.updateClientHandlersWhoWentOffline(nameOfOfflineUser);
            nameOfOfflineUser = null;
            //ois.close(); <-- mst fixas
            //oos.close();
            //clientSocket.close();

        }

        private void updateWhoWentOffline(String nameOfOfflineUser) {

            System.out.println("--------------------------");
            for (int i = 0; i < onlineUsers.size(); i++) {
                try {
                    oos.writeObject(nameOfOfflineUser);
                    oos.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

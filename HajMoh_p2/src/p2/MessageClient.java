package p2;

import p1.Message;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * The MessageClient class is responsible for receiving messages from a server.
 * When a new MessageClient object is created, it connects to a server using a Socket object, 
 * and creates an ObjectInputStream to receive Message objects from the server.
 * The MessageClient also provides a method for adding PropertyChangeListener objects, 
 * which will be notified whenever a new message is received from the server.
 */
public class MessageClient {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private ObjectInputStream in;
    private Socket socket;

    /**
     * Constructs a new MessageClient object that connects to the server at the given IP address and port.
     * The MessageClient creates an ObjectInputStream to receive Message objects from the server.
     * @param ip The ip address of the server. 
     * @param port The port of the server.
     */
    public MessageClient(String ip, int port){
        try{
            socket = new Socket(ip, port);
            in = new ObjectInputStream(socket.getInputStream());
        } catch(IOException e){
            System.err.println(e);
        }
        MessageListener messageListener = new MessageListener();
        new Thread(messageListener).start();
    }

    
    /** 
     * Adds a PropertyChangeListener to this MessageClient object.
     * The PropertyChangeListener will be notified whenever a new message is received from the server.
     * @param pcl The PropertyChangeListener to be added (p2viewer)
     */
    public void addPropertyChangeListener(PropertyChangeListener pcl){
        pcs.addPropertyChangeListener(pcl);
    }

    /**
     * The MessageListener class is a private inner class of MessageClient that is responsible for receiving messages from the server.
     * When a new MessageListener object is created, it creates a new thread to receive messages from the server.
     */
    private class MessageListener implements Runnable{

        @Override
        /**
         * Continuously receives Message objects from the server using the ObjectInputStream.
         * Whenever a new Message object is received, it notifies all registered PropertyChangeListener objects with the name "message".
         */
        public void run() {
            try{
                while (true){
                    Message message = (Message) in.readObject();
                    pcs.firePropertyChange("message", null, message);
                }
            } catch (IOException | ClassNotFoundException e){
                System.err.println(e);
            }
        }
    }
}
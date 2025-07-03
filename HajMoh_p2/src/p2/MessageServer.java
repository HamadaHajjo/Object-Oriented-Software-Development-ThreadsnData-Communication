package p2;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import p1.Buffer;
import p1.Message;
import p1.MessageCallback;
import p1.MessageManager;

/**
 * This class implements the MessageCallback interface. 
 * The MessageServer class listens for incoming client connections on a given port, 
 * and when a client connects, it creates a MessageHandler thread to handle messages received from the connected client.
 * When a message is received, it is stored in a buffer and sent to any connected clients.
 */
public class MessageServer implements MessageCallback{
    private Buffer<Message> buffer = new Buffer<>();
    private MessageManager messageManager;
    private ServerSocket serverSocket;
    private int c;

    /**
     * Constructs a new MessageServer instance with the specified MessageManager instance and port number.
     * @param messagemanager  The MessageManager instance to use for receiving messages
     * @param port The port number on which to listen for incoming client connections
     */
    public MessageServer(MessageManager messagemanager, int port){
        this.messageManager = messagemanager;
        messageManager.addCallback(this);
        try{
            serverSocket = new ServerSocket(port);
        }
        catch(IOException e){
            System.err.println(e);
        }
        new ClientConnection().start();
    }

    
    /** 
     * This method is called by the MessageManager instance when a new Message object is received.
     * It adds the message to the buffer c number of times.
     * @param message  The Message object to add to the buffer
     */
    @Override
    public void setMessage(Message message){
        for(int i = 0; i < c; i++){
            buffer.put(message);
        }
    }

    /**
     * A private inner class that listens for incoming client connections.
     * When a connection is accepted, a new MessageHandler thread is started to handle the connection.
     */
    private class ClientConnection extends Thread{
        
        /**
         * The run() method of the ClientConnection class starts a loop that listens for incoming client connections.
         * When a connection is accepted, a new MessageHandler thread is started to handle the connection.
         */
        @Override
        public void run(){
            Socket socket;
            while(true){
                try{
                    socket = serverSocket.accept();
                    new MessageHandler(socket);
                    c++;
                }
                catch(IOException e){
                    System.err.println(e);
                }
            }
        }
    }

    /**
     * A private inner class that is responsible for sending messages to a client.
     * It retrieves a Message object from the buffer and writes it to the client's output stream.
     */
    private class MessageHandler extends Thread{
        private ObjectOutputStream out;
        private Socket socket;
        
        /**
         * Constructs a new MessageHandler object with the given Socket object representing the client connection.
         * The MessageHandler creates an ObjectOutputStream to send Message objects to the client.
         * @param socket The Socket object representing the client connection
         */
        public MessageHandler(Socket socket)throws IOException{
            this.socket = socket;
            start();
        }
        
        /**
         * The run() method of the MessageHandler class starts a loop that retrieves a Message object from the buffer
         * and writes it to the client's output stream. If the buffer is empty, the thread waits for a new message to be added.
         */
        @Override
        public void run(){
            try{
                out = new ObjectOutputStream(socket.getOutputStream());
                while(true){
                    Message message = buffer.get();
                    out.writeObject(message);
                    out.flush();
                }
            } catch (IOException | InterruptedException e){
                System.err.println(e);
            }
            try{
                socket.close();
            } catch (IOException e){
                System.err.println(e);
            }
        }
    }
}
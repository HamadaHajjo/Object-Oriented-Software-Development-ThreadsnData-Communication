package p2;

import p1.MessageProducer;
import p1.MessageProducerInput;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageProducerServer extends Thread{
    private MessageProducerInput mpi;
    private int port;
    
    public MessageProducerServer(MessageProducerInput mpi, int port){
        this.mpi = mpi;
        this.port = port;
    }

    public void startServer(){
        start();
    }

    public void run(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            while (true){
                System.out.println("Server is running...");
                Socket socket = serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                MessageProducer messageProducer = (MessageProducer) ois.readObject();
                mpi.addMessageProducer(messageProducer);
                socket.close();
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        } catch (ClassNotFoundException e) {
            System.err.println(e.toString());
        }
    }
}
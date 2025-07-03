package p2;

import java.io.ObjectOutputStream;
import java.io.IOException;
import p1.ArrayProducer;
import java.net.Socket;
import p1.Message;
import p1.MessageProducer;

public class MessageProducerClient {
    private String ip;
    private int port;

    public MessageProducerClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void send(MessageProducer messageProducer) {
        int delay = messageProducer.delay();
        int times = messageProducer.times();
        int size = messageProducer.size();
        Message[] messages = new Message[size];
        for (int i = 0; i < size; i++) {
            messages[i] = messageProducer.nextMessage();
        }
        ArrayProducer arrayProducer = new ArrayProducer(messages, times, delay);
        try {
            Socket socket = new Socket(ip, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(arrayProducer);
            oos.flush();
            oos.close();
            socket.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}
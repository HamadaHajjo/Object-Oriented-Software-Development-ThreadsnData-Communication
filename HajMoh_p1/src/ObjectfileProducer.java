package src;

import java.io.*;

public class ObjectfileProducer implements MessageProducer {

    private int size;
    private int delay = 0;
    private int times = 0;
    private Message[] messages;
    private int currentIndex = -1;

    public ObjectfileProducer(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            times = ois.readInt();
            delay = ois.readInt();
            size = ois.readInt();
            messages = new Message[size];
            for (int i = 0; i < size; i++) {
                messages[i] = (Message) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.toString());
        }
    }

    public int delay() {
        return delay;
    }

    public int times() {
        return times;
    }

    public int size() {
        return (messages == null) ? 0 : messages.length;
    }

    public Message nextMessage() {
        if (size() == 0) {
            return null;
        }
        currentIndex = (currentIndex + 1) % messages.length;
        return messages[currentIndex];
    }
}
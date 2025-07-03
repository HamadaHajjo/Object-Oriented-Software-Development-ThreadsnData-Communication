package p1;

/**
 * The Producer class is a class that represents a producer thread which is used to retrieve messages from a MessageProducer  
 * Buffer and adds them to a message buffer.
 */
public class Producer extends Thread {
    private Buffer<MessageProducer> producerBuffer;
    private Buffer<Message> messageBuffer;

    
    /**
     * A constructor that creats a new producer object and with the specified MessageProducer buffer and message buffer. 
     * @param prodBuffer The buffer containing MessageProducer object 
     * @param messageBuffer The buffer to add methods to 
     */
    public Producer(Buffer<MessageProducer> prodBuffer, Buffer<Message> messageBuffer) {
        this.producerBuffer = prodBuffer;
        this.messageBuffer = messageBuffer;
    }


    /**
     * The run method retrieves messages from the MessageProducer buffer and adds them to the Message buffer.
     * The thread will sleep for a specified amount of time after every adding of a message. 
     */
    public void run() {
        while (producerBuffer.size() >= 0) {
            try {
                MessageProducer mp = producerBuffer.get();
                for (int i = 0; i < mp.times(); i++) {
                    for (int j = 0; j < mp.size(); j++) {
                        messageBuffer.put(mp.nextMessage());
                        sleep(mp.delay());
                    }
                }
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
    }
}
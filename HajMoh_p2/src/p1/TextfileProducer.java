package p1;

import java.io.*;
import javax.swing.*;

/**
 * This class is used to produce a messages from a text file. 
 */
public class TextfileProducer implements MessageProducer {
    private int size;
    private int delay = 0;
    private int times = 0;
    private Message[] messages;
    private int currentIndex = -1;
    
    
    /** 
     * Create instans of TextfileProducer object. The filename is the parameter of the file to read data from.
     * Data is being read by the inputstreamreader in the fileinputstream. the variables times, delay and time are being used.
     * Times is the number of time the message should be displayed. delay is the delay of time between the messages, and size is the size of the message.
     * Lastly a pair of text and image are added to the list of messages.
     * @param filename , the specified filename to read data from.
     */
    public TextfileProducer(String filename) {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8" ))){

            String line = br.readLine();
            times = Integer.parseInt(line);

            line = br.readLine();
            delay = Integer.parseInt(line);

            line = br.readLine();
            size = Integer.parseInt(line);
            messages = new Message[size];
            
            for(int i=0; i<size; i++) {
                messages[i] = new Message(br.readLine(), new ImageIcon(br.readLine()));
            }
        }
        catch(IOException e) {
            System.err.println(e.toString());
        }
    }

    
    /** 
     * The delay of time between the messages.
     * @return int , the delay in milliseconds.
     */
    public int delay() {
        return delay;
    }

    /** 
     * The number of time the message should be displayed.
     * @return int , the number of times the message should be displayed.
     */
    public int times() {
        return times;
    }

    
    /** 
     * The size of the message.
     * @return int , the size of the message.
     */
    public int size() {
        return (messages==null) ? 0 : messages.length;
    }
    
    
    /** 
     * Getting the next message using a index that loops through the list of messages.
     * @return Message , the next message in the list. 
     */
    public Message nextMessage() {
        if(size()==0)
            return null;
        currentIndex = (currentIndex+1) % messages.length;
        return messages[currentIndex];
    }
}
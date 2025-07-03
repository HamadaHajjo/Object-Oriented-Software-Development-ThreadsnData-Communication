package src;

import java.util.*;

public class MessageManager extends Thread {
    private Message message;
    private Buffer<Message> messageBuffer;
    private ArrayList<MessageCallback> callback;
    
    public MessageManager(Buffer<Message> messageBuffer) {
        this.messageBuffer = messageBuffer;
        this.callback = new ArrayList<>();
    }

    public Message getMessage() {
        return message;
    }
    
    public void addCallback(MessageCallback callback) {
        this.callback.add(callback);
    }

    public void run(){
        while(!Thread.interrupted()){
            try{
                message = messageBuffer.get();
                if(message != null){
                    for(MessageCallback cb : callback){
                        cb.setMessage(message);
                    }
                }
            }
            catch(InterruptedException e){
                System.err.println(e);
            }
        }
    }
}
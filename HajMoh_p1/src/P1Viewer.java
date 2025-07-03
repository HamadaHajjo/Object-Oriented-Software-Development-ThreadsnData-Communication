package src;

public class P1Viewer extends Viewer implements MessageCallback {
    public P1Viewer(MessageManager messageManager, int width, int height){
        super(width, height);
        messageManager.addCallback(this);
    }

    @Override
    public void setMessage(Message message){
        super.setMessage(message);
    }
}
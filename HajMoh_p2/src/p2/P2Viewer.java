package p2;

import p1.Message;
import p1.Viewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class P2Viewer extends Viewer implements PropertyChangeListener {
    private MessageClient messageClient;

    public P2Viewer(MessageClient messageClient, int width, int height){
        super(width,height);
        this.messageClient = messageClient;
        messageClient.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if("message".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Message){
            super.setMessage((Message) evt.getNewValue());
        }
    }
}
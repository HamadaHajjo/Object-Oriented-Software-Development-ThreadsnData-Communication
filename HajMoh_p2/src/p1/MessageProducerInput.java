package p1;

public class MessageProducerInput {
    
    private Buffer<MessageProducer> producerBuffer;

    public MessageProducerInput(Buffer<MessageProducer> bmp){
        this.producerBuffer = bmp;
    }

    public void addMessageProducer(MessageProducer m){
        producerBuffer.put(m);
    }
}
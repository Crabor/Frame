package platform.communication.pubsub;

public interface Subscriber {
    void onMessage(String channel, String msg);
}

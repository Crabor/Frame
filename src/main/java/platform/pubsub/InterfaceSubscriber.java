package platform.pubsub;

public interface InterfaceSubscriber {
    public void onMessage(String s, String s2);
    public void onSubscribed(String s, long l);
    public void onUnsubscribed(String s, long l);
}

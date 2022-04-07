package platform.pubsub;

public interface Subscriber {
    void onMessage(String channel, String msg);
    void onSubscribed(String channel, long subChannelCount);
    void onUnsubscribed(String channel, long subChannelCount);
}

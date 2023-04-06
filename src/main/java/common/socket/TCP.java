package common.socket;

import java.net.Socket;

public interface TCP {
    Socket getSocket();
    boolean send(String str);
    String recv();
    void close();
    void callback();
    void setLockFlag(boolean lockFlag);
    void unlock();
}

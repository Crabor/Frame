package common.socket;

import java.net.Socket;

public interface TCP {
    Socket getSocket();
    void send(String str);
    String recv();
    void close();
    void callBack();
}

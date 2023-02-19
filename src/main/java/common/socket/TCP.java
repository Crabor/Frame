package common.socket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TCP {
    private final Socket socket;
    private final Lock lock;
    private final boolean lockFlag;
    private DataOutputStream out;
    private BufferedReader in;

    public TCP(Socket socket, boolean lockFlag) {
        this.socket = socket;
        this.lockFlag = lockFlag;
        if (lockFlag) {
            this.lock = new ReentrantLock(false);
        } else {
            this.lock = null;
        }
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }
    }

    public TCP(Socket socket) {
        this(socket, true);
    }

    public void send(String str) {
        try {
            if (lockFlag) {
                lock.lock();
            }
            out.writeBytes(str + '\n');
        } catch (IOException e) {
            e.printStackTrace();
            if (lockFlag) {
                lock.unlock();
            }
            try {
                socket.close();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }
    }

    public String recv() {
        String ret = null;

        try {
            ret = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }
        if (lockFlag) {
            lock.unlock();
        }
        return ret;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}

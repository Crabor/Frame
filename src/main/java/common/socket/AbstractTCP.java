package common.socket;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractTCP implements TCP {
    protected final Socket socket;
    private Lock lock;
    private boolean lockFlag;
    private DataOutputStream out;
    private BufferedReader in;

    public AbstractTCP(Socket socket, boolean lockFlag) {
        this.socket = socket;
        try {
            this.socket.setKeepAlive(true);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
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

    public AbstractTCP(Socket socket) {
        this(socket, true);
    }

    @Override
    public void setLockFlag(boolean lockFlag) {
        this.lockFlag = lockFlag;
        if (lockFlag) {
            this.lock = new ReentrantLock(false);
        } else {
            this.lock = null;
        }
    }

    @Override
    public boolean send(String str) {
        try {
            if (lockFlag) {
                lock.lock();
            }
            out.writeBytes(str + '\n');
        } catch (IOException e) {
            callback();
            if (lockFlag) {
                lock.unlock();
            }
            try {
                socket.close();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
            return false;
        }
        return true;
    }

    @Override
    public String recv() {
        String ret = null;

        try {
            ret = in.readLine();
        } catch (IOException e) {
//            e.printStackTrace();
            callback();
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

//    @Override
//    public void close() {
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public void unlock() {
        if (lockFlag) {
            lock.unlock();
        }
    }
}

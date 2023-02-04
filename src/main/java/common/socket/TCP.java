package common.socket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCP {
    Socket socket;
    private DataOutputStream out;
    private BufferedReader in;

    public TCP(Socket socket) {
        this.socket = socket;
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

    public void send(String str) {
        try {
            out.writeBytes(str + '\n');
        } catch (IOException e) {
//            e.printStackTrace();
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
//            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ee) {
                ee.printStackTrace();
            }
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

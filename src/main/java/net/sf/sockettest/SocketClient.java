package net.sf.sockettest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import net.sf.sockettest.swing.Encoding;
import net.sf.sockettest.swing.SocketTestClient;
/**
 *
 * @author Akshathkumar Shetty
 */
public class SocketClient extends Thread {

    private static SocketClient socketClient = null;
    private Socket socket = null;
    private SocketTestClient parent;
    private BufferedReader reader;
    private boolean disonnected = false;
    private final Encoding selectedEncoding;

    public synchronized void setDisonnected(boolean cr) {
        disonnected = cr;
    }

    private SocketClient(SocketTestClient parent, Socket s, Encoding selectedEncoding) {
        super("SocketClient");
        this.parent = parent;
        this.socket = s;
        this.selectedEncoding = selectedEncoding;
        setDisonnected(false);
        start();
    }

    public static synchronized SocketClient handle(SocketTestClient parent, Socket s, Encoding selectedEncoding) {
        if (socketClient == null)
            socketClient = new SocketClient(parent, s, selectedEncoding);
        else {
            if (socketClient.socket != null) {
                try {
                    socketClient.socket.close();
                } catch (Exception e) {
                    parent.error(e.getMessage());
                }
            }
            socketClient.socket = null;
            socketClient = new SocketClient(parent, s, selectedEncoding);
        }
        return socketClient;
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            this.reader = new BufferedReader(new InputStreamReader(is, selectedEncoding.getCharset()));
            
        } catch(IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch(IOException e2) {
                System.err.println("Socket not closed :"+e2);
            }
            parent.error("Could not open socket : "+e.getMessage());
            parent.disconnect();
            return;
        }

        while (!disonnected) {
            String rec = null;
            try {
                rec = read();
                if (rec == null) {
                    if (!reader.ready()) {
                        parent.disconnect();
                        break;
                    } else {
                        sleep(200);
                        continue;
                    }
                }
                parent.append("R: " + rec);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                if (!disonnected) {
                    parent.error(e.getMessage(), "Connection lost");
                    parent.disconnect();
                }
                break;
            }
        }
        try {
            reader.close();
        } catch (Exception err) {
        }
        socket = null;
    }

    public String read() throws IOException {
        StringBuilder result = new StringBuilder();
        
        do {
            int charVal = reader.read();
            if (charVal == -1) {
                return null;
            }
            
            char z = (char) charVal;
            result.append(z);
        } while (reader.ready());

        System.out.println("Received: " + result);
        return result.toString();
    }
}

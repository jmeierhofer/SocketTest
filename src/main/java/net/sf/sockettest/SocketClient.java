package net.sf.sockettest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import net.sf.sockettest.swing.SocketTestClient;
/**
 *
 * @author Akshathkumar Shetty
 */
public class SocketClient extends Thread {
    
    private static SocketClient socketClient=null;
    private Socket socket=null;
    private SocketTestClient parent;
    private BufferedInputStream in;
    private boolean disonnected=false;
    
    public synchronized void setDisonnected(boolean cr) {
        disonnected=cr;
    }
    
    private SocketClient(SocketTestClient parent, Socket s) {
        super("SocketClient");
        this.parent = parent;
        socket=s;
        setDisonnected(false);
        start();
    }
    
    public static synchronized SocketClient handle(SocketTestClient parent, Socket s) {
        if(socketClient==null)
            socketClient=new SocketClient(parent, s);
        else {
            if(socketClient.socket!=null) {
                try	{
                    socketClient.socket.close();
                } catch (Exception e)	{
                    parent.error(e.getMessage());
                }
            }
            socketClient.socket=null;
            socketClient=new SocketClient(parent,s);
        }
        return socketClient;
    }
    
    @Override
    public void run() {
        InputStream is = null;
        try {
            is = socket.getInputStream();
            in = new BufferedInputStream(is);
        } catch(IOException e) {
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
                rec = readInputStream(in);
                if (rec == null) {
                    parent.disconnect();
                    break;
                }
                parent.append("R: " + rec);

            } catch (IOException e) {
                if (!disonnected) {
                    parent.error(e.getMessage(), "Connection lost");
                    parent.disconnect();
                }
                break;
            }
        }
        try {
            is.close();
            in.close();
            // socket.close();
        } catch (Exception err) {
        }
        socket = null;
    }

    private static String readInputStream(BufferedInputStream _in) throws IOException {
        String data = "";
        int s = _in.read();
        if(s==-1)
            return null;
        data += ""+(char)s;
        int len = _in.available();
        System.out.println("Len got : "+len);
        if(len > 0) {
            byte[] byteData = new byte[len];
            _in.read(byteData);
            data += new String(byteData);
        }
        return data;
    }
    
}

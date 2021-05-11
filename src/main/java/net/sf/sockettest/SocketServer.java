package net.sf.sockettest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import net.sf.sockettest.swing.SocketTestServer;

/**
 *
 * @author Akshathkumar Shetty
 */
public class SocketServer extends Thread {
    
    private static SocketServer socketServer = null;
    private Socket socket = null;
    private ServerSocket server = null;
    private SocketTestServer parent;
    private BufferedInputStream in;
    private boolean disonnected=false;
    private boolean stop = false;
    
    public synchronized void setDisonnected(boolean cr) {
        if (socket != null && cr == true) {
            try {
                socket.close();
            } catch (Exception e) {
                System.err.println("Error closing clinet : setDisonnected : " + e);
            }
        }
        disonnected = cr;
    }

    public synchronized void setStop(boolean cr) {
        stop=cr;
        if(server!=null && cr==true) {
            try	{
                server.close();
            } catch (Exception e) {
                System.err.println("Error closing server : setStop : "+e);
            }
        }
    }
    
    private SocketServer(SocketTestServer parent, ServerSocket s) {
        super("SocketServer");
        this.parent = parent;
        server=s;
        setStop(false);
        setDisonnected(false);
        start();
    }
    
    public static synchronized SocketServer handle(SocketTestServer parent,
            ServerSocket s) {
        if(socketServer==null)
            socketServer=new SocketServer(parent, s);
        else {
            if(socketServer.server!=null) {
                try	{
                    socketServer.setDisonnected(true);
                    socketServer.setStop(true);
                    if(socketServer.socket!=null)
                        socketServer.socket.close();
                    if(socketServer.server!=null)
                        socketServer.server.close();
                } catch (Exception e)	{
                    parent.error(e.getMessage());
                }
            }
            socketServer.server = null;
            socketServer.socket = null;
            socketServer=new SocketServer(parent,s);
        }
        return socketServer;
    }
    
    @Override
    public void run() {
        while(!stop) {
            try	{
                socket = server.accept();
            } catch (Exception e) {
                if(!stop) {
                    parent.error(e.getMessage(),"Error acception connection");
                    stop=true;
                }
                continue;
            }
            startServer();
            if(socket!=null) {
                try	{
                    socket.close();
                } catch (Exception e) {
                    System.err.println("Erro closing client socket : "+e);
                }
                socket=null;
                parent.setClientSocket(socket);
            }
        }
    }
    
    private void startServer() {
        parent.setClientSocket(socket);
        InputStream is = null;
        parent.append("> New Client: "+socket.getInetAddress().getHostAddress());
        try {
            is = socket.getInputStream();
            in = new BufferedInputStream(is);
        } catch(IOException e) {
            parent.append("> Could not open input stream on Client "+e.getMessage());
            setDisonnected(true);
            return;
        }
        
        while (true) {
            String rec = null;
            try {
                rec = readInputStream(in);
            } catch (Exception e) {
                setDisonnected(true);
                if (!disonnected) {
                    parent.error(e.getMessage(), "Lost Client connection");
                    parent.append("> Server lost Client connection.");
                } else
                    parent.append("> Server closed Client connection.");
                break;
            }

            if (rec != null) {
                parent.append("R: " + rec);
            } else {
                setDisonnected(true);
                parent.append("> Client closed connection.");
                break;
            }
        }
    }

    private static String readInputStream(BufferedInputStream _in) throws IOException {
        String data = "";
        int s = _in.read();
        if (s == -1)
            return null;
        data += "" + (char) s;
        int len = _in.available();
        System.out.println("Len got : " + len);
        if (len > 0) {
            byte[] byteData = new byte[len];
            _in.read(byteData);
            data += new String(byteData);
        }
        return data;
    }
}

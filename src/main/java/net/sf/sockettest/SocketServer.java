package net.sf.sockettest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import net.sf.sockettest.swing.Encoding;
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
    private BufferedReader reader;
    private boolean disonnected=false;
    private boolean stop = false;
    private final Encoding selectedEncoding;
    
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
        stop = cr;
        if (server != null && cr == true) {
            try {
                server.close();
            } catch (Exception e) {
                System.err.println("Error closing server : setStop : " + e);
            }
        }
    }

    private SocketServer(SocketTestServer parent, ServerSocket s, Encoding selectedEncoding) {
        super("SocketServer");
        this.parent = parent;
        this.server = s;
        this.selectedEncoding = selectedEncoding;
        setStop(false);
        setDisonnected(false);
        start();
    }

    public static synchronized SocketServer handle(SocketTestServer parent, ServerSocket s, Encoding selectedEncoding) {
        if (socketServer == null)
            socketServer = new SocketServer(parent, s, selectedEncoding);
        else {
            if (socketServer.server != null) {
                try {
                    socketServer.setDisonnected(true);
                    socketServer.setStop(true);
                    if (socketServer.socket != null)
                        socketServer.socket.close();
                    if (socketServer.server != null)
                        socketServer.server.close();
                } catch (Exception e) {
                    parent.error(e.getMessage());
                }
            }
            socketServer.server = null;
            socketServer.socket = null;
            socketServer = new SocketServer(parent, s, selectedEncoding);
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
        parent.append("> New Client: "+socket.getInetAddress().getHostAddress());
        
        try {
            InputStream is = socket.getInputStream();
            this.reader = new BufferedReader(new InputStreamReader(is, selectedEncoding.getCharset()));
            
        } catch(IOException e) {
            parent.append("> Could not open input stream on Client "+e.getMessage());
            setDisonnected(true);
            return;
        }
        
        while (true) {
            try {
                String rec = read();
                if (rec == null) {
                    if (!reader.ready()) {
                        setDisonnected(true);
                        parent.append("> Client closed connection.");
                        break;
                    } else {
                        sleep(200);
                        continue;
                    }
                }
                parent.append("R: " + rec);
                
            } catch (Exception e) {
                setDisonnected(true);
                if (!disonnected) {
                    parent.error(e.getMessage(), "Lost Client connection");
                    parent.append("> Server lost Client connection.");
                } else
                    parent.append("> Server closed Client connection.");
                break;
            }
        }
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

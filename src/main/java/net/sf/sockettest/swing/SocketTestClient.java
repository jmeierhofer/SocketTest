package net.sf.sockettest.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import net.sf.sockettest.MyTrustManager;
import net.sf.sockettest.NetService;
import net.sf.sockettest.SocketClient;
import net.sf.sockettest.Util;
import net.sf.sockettest.Version;

/**
 * @author Akshathkumar Shetty
 */
public class SocketTestClient extends JPanel implements NetService {
    
    private static final long serialVersionUID = 1L;

    private static final String NEW_LINE = System.lineSeparator();
    
    private ClassLoader cl = getClass().getClassLoader();
    private ImageIcon logo = new ImageIcon(cl.getResource("icons/logo.gif"));

    private JPanel topPanel;
    private JPanel toPanel;

    private JPanel centerPanel;
    private JPanel textPanel;
    private JPanel buttonPanel;
    private JPanel sendPanel;

    private JLabel ipLabel = new JLabel("IP Address");
    private JLabel portLabel = new JLabel("Port");
    private JLabel logoLabel = new JLabel(Version.VERSION_LONG, logo,
            JLabel.CENTER);
    private JTextField ipField = new JTextField("127.0.0.1", 20);
    private JTextField portField = new JTextField("21", 10);
    private JComboBox<Encoding> encodingBox = new JComboBox<>(Encoding.values());
    private JButton connectButton = new JButton("Connect");

    private JLabel convLabel = new JLabel("Conversation with host");
    private Border connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Connected To < NONE >");
    private JTextArea messagesField = new JTextArea();

    private JLabel sendLabel = new JLabel("Message");
    private JTextField sendField = new JTextField();

    private JButton sendButton = new JButton("Send");
    private JButton saveButton = new JButton("Save");
    private JButton clearButton = new JButton("Clear");

    private JCheckBox secureButton = new JCheckBox("Secure");
    private boolean isSecure = false;
    private GridBagConstraints gbc = new GridBagConstraints();

    private Socket socket;
    private PrintWriter out;
    private SocketClient socketClient;
    protected final JFrame parent;

    public SocketTestClient(final JFrame parent) {
        //Container cp = getContentPane();
        this.parent = parent;
        Container cp = this;

        topPanel = new JPanel();
        toPanel = new JPanel();
        toPanel.setLayout(new GridBagLayout());
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        toPanel.add(ipLabel, gbc);

        gbc.weightx = 1.0; //streach
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener ipListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                portField.requestFocus();
            }
        };
        ipField.addActionListener(ipListener);
        toPanel.add(ipField, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        toPanel.add(portLabel, gbc);

        gbc.weightx = 1.0;
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener connectListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        };
        portField.addActionListener(connectListener);
        toPanel.add(portField, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        encodingBox.setToolTipText("Define charset to use for raw byte conversion");
        toPanel.add(encodingBox, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        connectButton.setMnemonic('C');
        connectButton.setToolTipText("Start Connection");
        connectButton.addActionListener(connectListener);
        toPanel.add(connectButton, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        secureButton.setToolTipText("Set Has Secure");
        secureButton.addItemListener(
                new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        isSecure = !isSecure;
                    }
                });
        toPanel.add(secureButton, gbc);


        toPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Connect To"));
        topPanel.setLayout(new BorderLayout(10, 0));
        topPanel.add(toPanel);
        logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
        logoLabel.setHorizontalTextPosition(JLabel.CENTER);
        topPanel.add(logoLabel, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));


        textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout(0, 5));
        textPanel.add(convLabel, BorderLayout.NORTH);
        messagesField.setEditable(false);
        JScrollPane jsp = new JScrollPane(messagesField);
        textPanel.add(jsp);
        textPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));

        sendPanel = new JPanel();
        sendPanel.setLayout(new GridBagLayout());
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        sendPanel.add(sendLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        sendField.setEditable(false);
        sendPanel.add(sendField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        sendButton.setEnabled(false);
        sendButton.setToolTipText("Send text to host");
        ActionListener sendListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = sendField.getText();
                if (!msg.equals(""))
                    sendMessage(msg);
                else {
                    int value = JOptionPane.showConfirmDialog(
                            SocketTestClient.this, "Send Blank Line ?",
                            "Send Data To Server",
                            JOptionPane.YES_NO_OPTION);
                    if (value == JOptionPane.YES_OPTION)
                        sendMessage(msg);
                }
            }
        };
        sendButton.addActionListener(sendListener);
        sendField.addActionListener(sendListener);
        sendPanel.add(sendButton, gbc);
        sendPanel.setBorder(
                new CompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 0, 3),
                        BorderFactory.createTitledBorder("Send")));

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        gbc.weighty = 0.0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.BOTH;
        buttonPanel.add(sendPanel, gbc);
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        saveButton.setToolTipText("Save conversation with host to a file");
        saveButton.setMnemonic('S');
        ActionListener saveListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = messagesField.getText();
                if (text.equals("")) {
                    error("Nothing to save", "Save to file");
                    return;
                }
                String fileName = "";
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                int returnVal = chooser.showSaveDialog(SocketTestClient.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fileName = chooser.getSelectedFile().getAbsolutePath();
                    try {
                        Util.writeFile(fileName, text);
                    } catch (Exception ioe) {
                        JOptionPane.showMessageDialog(SocketTestClient.this,
                                "" + ioe.getMessage(),
                                "Error saving to file..",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        saveButton.addActionListener(saveListener);
        buttonPanel.add(saveButton, gbc);
        gbc.gridy = 1;
        clearButton.setToolTipText("Clear conversation with host");
        clearButton.setMnemonic('C');
        ActionListener clearListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messagesField.setText("");
            }
        };
        clearButton.addActionListener(clearListener);
        buttonPanel.add(clearButton, gbc);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 3));

        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(0, 10));
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        centerPanel.add(textPanel, BorderLayout.CENTER);

        CompoundBorder cb = new CompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 10, 10),
                connectedBorder);
        centerPanel.setBorder(cb);

        cp.setLayout(new BorderLayout(10, 0));
        cp.add(topPanel, BorderLayout.NORTH);
        cp.add(centerPanel, BorderLayout.CENTER);
    }
    
        /*
        public static void main(String args[]) {
                SocketTestClient client=new SocketTestClient();
                client.setTitle("SocketTest Client");
                //client.pack();
                client.setSize(500,400);
                Util.centerWindow(client);
                client.setDefaultCloseOperation(EXIT_ON_CLOSE);
                client.setIconImage(client.logo.getImage());
                client.setVisible(true);
        }
         */

    /////////////////
    //action methods
    //////////////////
    private void connect() {
        if (socket != null) {
            disconnect();
            encodingBox.setEnabled(true);
            return;
        }

        String ip = ipField.getText();
        String port = portField.getText();
        if (ip == null || ip.equals("")) {
            JOptionPane.showMessageDialog(SocketTestClient.this,
                    "No IP Address. Please enter IP Address",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            ipField.requestFocus();
            ipField.selectAll();
            return;
        }
        if (port == null || port.equals("")) {
            JOptionPane.showMessageDialog(SocketTestClient.this,
                    "No Port number. Please enter Port number",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            portField.requestFocus();
            portField.selectAll();
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (!Util.checkHost(ip)) {
            JOptionPane.showMessageDialog(SocketTestClient.this,
                    "Bad IP Address",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            ipField.requestFocus();
            ipField.selectAll();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        int portNo = 0;
        try {
            portNo = Integer.parseInt(port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(SocketTestClient.this,
                    "Bad Port number. Please enter Port number",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            portField.requestFocus();
            portField.selectAll();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        try {
            if (isSecure == false) {
                System.out.println("Connecting in normal mode : " + ip + ":" + portNo);
                socket = new Socket(ip, portNo);
            } else {
                System.out.println("Connecting in secure mode : " + ip + ":" + portNo);
                //SocketFactory factory = SSLSocketFactory.getDefault();

                TrustManager[] tm = new TrustManager[]{new MyTrustManager(SocketTestClient.this)};

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(new KeyManager[0], tm, new SecureRandom());

                SSLSocketFactory factory = context.getSocketFactory();
                socket = factory.createSocket(ip, portNo);
            }

            ipField.setEditable(false);
            portField.setEditable(false);
            connectButton.setText("Disconnect");
            connectButton.setMnemonic('D');
            connectButton.setToolTipText("Stop Connection");
            sendButton.setEnabled(true);
            sendField.setEditable(true);
        } catch (Exception e) {
            e.printStackTrace();
            error(e.getMessage(), "Opening connection");
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        changeBorder(" " + socket.getInetAddress().getHostName() +
                " [" + socket.getInetAddress().getHostAddress() + "] ");
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        messagesField.setText("");
        
        Encoding selectedEncoding = (Encoding) encodingBox.getSelectedItem();
        socketClient = SocketClient.handle(this, socket, selectedEncoding);
        
        sendField.requestFocus();
        encodingBox.setEnabled(false);
    }

    public synchronized void disconnect() {
        try {
            socketClient.setDisonnected(true);
            socket.close();
        } catch (Exception e) {
            System.err.println("Error closing client : " + e);
        }
        socket = null;
        out = null;
        changeBorder(null);
        ipField.setEditable(true);
        portField.setEditable(true);
        connectButton.setText("Connect");
        connectButton.setMnemonic('C');
        connectButton.setToolTipText("Start Connection");
        sendButton.setEnabled(false);
        sendField.setEditable(false);
        encodingBox.setEnabled(true);
    }

    public void error(String error) {
        if (error == null || error.equals(""))
            return;
        JOptionPane.showMessageDialog(SocketTestClient.this,
                error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void error(String error, String heading) {
        if (error == null || error.equals(""))
            return;
        JOptionPane.showMessageDialog(SocketTestClient.this,
                error, heading, JOptionPane.ERROR_MESSAGE);
    }

    public void append(String msg) {
        messagesField.append(msg + NEW_LINE);
        messagesField.setCaretPosition(messagesField.getText().length());
    }

    public void appendNoNewLine(String msg) {
        messagesField.append(msg);
        messagesField.setCaretPosition(messagesField.getText().length());
    }

    public void sendMessage(String s) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            if (out == null) {
                Encoding selectedEncoding = (Encoding) encodingBox.getSelectedItem();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(),
                        selectedEncoding.getCharset());
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                out = new PrintWriter(bufferedWriter, true);
            }
            
            append("S: " + s);
            out.print(s);
            out.flush();
            sendField.setText("");
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            
        } catch (Exception e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(SocketTestClient.this,
                    e.getMessage(), "Error Sending Message",
                    JOptionPane.ERROR_MESSAGE);
            disconnect();
        }
    }

    private void changeBorder(String ip) {
        if (ip == null || ip.equals(""))
            connectedBorder = BorderFactory.createTitledBorder(
                    new EtchedBorder(), "Connected To < NONE >");
        else
            connectedBorder = BorderFactory.createTitledBorder(
                    new EtchedBorder(), "Connected To < " + ip + " >");
        CompoundBorder cb = new CompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 10, 10),
                connectedBorder);
        centerPanel.setBorder(cb);
        invalidate();
        repaint();
    }

    @Override
    public void setUpConfiguration(String ip, String port) {
        ipField.setText(ip);
        portField.setText(port);
    }

}

package net.sf.sockettest.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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

import net.sf.sockettest.NetService;
import net.sf.sockettest.UdpServer;
import net.sf.sockettest.Util;
import net.sf.sockettest.Version;

/**
 * @author Akshathkumar Shetty
 */
public class SocketTestUdp extends JPanel implements NetService {

    private static final long serialVersionUID = 1L;

    private static final String NEW_LINE = System.lineSeparator();

    private ClassLoader cl = getClass().getClassLoader();
    private ImageIcon logo = new ImageIcon(cl.getResource("icons/logo.gif"));

    private JPanel northPanel;
    private JPanel serverPanel;

    private JPanel convPanel;

    private JPanel clientPanel;
    private JPanel buttonPanel;

    private JLabel ipLabel1 = new JLabel("IP Address");
    private JLabel portLabel1 = new JLabel("Port");
    private JLabel logoLabel = new JLabel(Version.VERSION_LONG, logo,
            JLabel.CENTER);

    private JTextField ipField1 = new JTextField("0.0.0.0", 20);
    private JTextField portField1 = new JTextField("21", 5);
    private JComboBox<Encoding> encodingBox1 = new JComboBox<>(Encoding.values());
    private JButton connectButton = new JButton("Start Listening");

    private Border convBorder = BorderFactory.createTitledBorder(new EtchedBorder(), "Conversation");
    private JTextArea messagesField = new JTextArea();

    private JLabel ipLabel2 = new JLabel("IP Address");
    private JLabel portLabel2 = new JLabel("Port");
    private JTextField ipField2 = new JTextField("127.0.0.1");
    private JTextField portField2 = new JTextField("21", 5);
    private JComboBox<Encoding> encodingBox2 = new JComboBox<>(Encoding.values());
    private JLabel sendLabel = new JLabel("Message");
    private JTextField sendField = new JTextField();
    private JButton sendButton = new JButton("Send");

    private JButton saveButton = new JButton("Save");
    private JButton clearButton = new JButton("Clear");

    private GridBagConstraints gbc = new GridBagConstraints();

    private DatagramSocket server, client;
    private UdpServer udpServer;
    private DatagramPacket pack;
    private byte buffer[];

    protected final JFrame parent;

    public SocketTestUdp(final JFrame parent) {
        //Container cp = getContentPane();
        this.parent = parent;
        Container cp = this;

        northPanel = new JPanel();
        serverPanel = new JPanel();
        serverPanel.setLayout(new GridBagLayout());
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        serverPanel.add(ipLabel1, gbc);

        gbc.weightx = 1.0; //streach
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener ipListener1 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                portField1.requestFocus();
            }
        };
        ipField1.addActionListener(ipListener1);
        serverPanel.add(ipField1, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        serverPanel.add(portLabel1, gbc);

        gbc.weightx = 1.0;
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener connectListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listen();
            }
        };
        portField1.addActionListener(connectListener);
        serverPanel.add(portField1, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        encodingBox1.setToolTipText("Define charset to use for raw byte conversion");
        serverPanel.add(encodingBox1, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        connectButton.setMnemonic('S');
        connectButton.setToolTipText("Start Listening");
        connectButton.addActionListener(connectListener);
        serverPanel.add(connectButton, gbc);

        serverPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Server"));
        northPanel.setLayout(new BorderLayout(10, 0));
        northPanel.add(serverPanel);
        logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
        logoLabel.setHorizontalTextPosition(JLabel.CENTER);
        northPanel.add(logoLabel, BorderLayout.EAST);
        northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        convPanel = new JPanel();
        convPanel.setLayout(new BorderLayout(0, 5));
        messagesField.setEditable(false);
        JScrollPane jsp = new JScrollPane(messagesField);
        convPanel.add(jsp);
        convPanel.setBorder(new CompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10),
                convBorder));

        clientPanel = new JPanel();
        clientPanel.setLayout(new GridBagLayout());
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        clientPanel.add(ipLabel2, gbc);

        gbc.weightx = 1.0; //streach
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener ipListener2 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                portField2.requestFocus();
            }
        };
        ipField2.addActionListener(ipListener2);
        clientPanel.add(ipField2, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 0;
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        clientPanel.add(portLabel2, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 0;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        ActionListener portListener2 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendField.requestFocus();
            }
        };
        portField2.addActionListener(portListener2);
        clientPanel.add(portField2, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 0;
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        encodingBox2.setToolTipText("Define charset to use for raw byte conversion");
        clientPanel.add(encodingBox2, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        clientPanel.add(sendLabel, gbc);

        gbc.weightx = 1.0;
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener sendListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = sendField.getText();
                if (!msg.equals(""))
                    sendMessage(msg);
                else {
                    int value = JOptionPane.showConfirmDialog(
                            SocketTestUdp.this, "Send Blank Line ?",
                            "Send Data To Server",
                            JOptionPane.YES_NO_OPTION);
                    if (value == JOptionPane.YES_OPTION)
                        sendMessage(msg);
                }
            }
        };
        sendField.addActionListener(sendListener);
        clientPanel.add(sendField, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        sendButton.addActionListener(sendListener);
        clientPanel.add(sendButton, gbc);
        clientPanel.setBorder(
                new CompoundBorder(
                        BorderFactory.createEmptyBorder(0, 0, 0, 3),
                        BorderFactory.createTitledBorder("Client")));

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
        buttonPanel.add(clientPanel, gbc);
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        saveButton.setToolTipText("Save conversation with client to a file");
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
                int returnVal = chooser.showSaveDialog(SocketTestUdp.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fileName = chooser.getSelectedFile().getAbsolutePath();
                    try {
                        Encoding selectedEncoding = (Encoding) encodingBox1.getSelectedItem();
                        Util.writeFile(fileName, text, selectedEncoding.getCharset());
                    } catch (Exception ioe) {
                        JOptionPane.showMessageDialog(SocketTestUdp.this, "" + ioe.getMessage(),
                                "Error saving to file..", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        saveButton.addActionListener(saveListener);
        buttonPanel.add(saveButton, gbc);
        gbc.gridy = 1;
        clearButton.setToolTipText("Clear conversation with client");
        clearButton.setMnemonic('C');
        ActionListener clearListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messagesField.setText("");
            }
        };
        clearButton.addActionListener(clearListener);
        buttonPanel.add(clearButton, gbc);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        cp.setLayout(new BorderLayout(10, 0));
        cp.add(northPanel, BorderLayout.NORTH);
        cp.add(convPanel, BorderLayout.CENTER);
        cp.add(buttonPanel, BorderLayout.SOUTH);

    }

    /////////////////////
    //action & helper methods
    /////////////////////
    private void listen() {
        if (server != null) {
            stop();
            encodingBox1.setEnabled(true);
            return;
        }

        encodingBox1.setEnabled(false);
        String ip = ipField1.getText();
        String port = portField1.getText();
        if (ip == null || ip.equals("")) {
            JOptionPane.showMessageDialog(SocketTestUdp.this,
                    "No IP Address. Please enter IP Address",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            ipField1.requestFocus();
            ipField1.selectAll();
            return;
        }
        if (port == null || port.equals("")) {
            JOptionPane.showMessageDialog(SocketTestUdp.this,
                    "No Port number. Please enter Port number",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            portField1.requestFocus();
            portField1.selectAll();
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (!Util.checkHost(ip)) {
            JOptionPane.showMessageDialog(SocketTestUdp.this,
                    "Bad IP Address",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            ipField1.requestFocus();
            ipField1.selectAll();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        int portNo = 0;
        try {
            portNo = Integer.parseInt(port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(SocketTestUdp.this,
                    "Bad Port number. Please enter Port number",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            portField1.requestFocus();
            portField1.selectAll();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }

        boolean multicase = false;
        try {
            InetAddress bindAddr = null;

            int i = ip.indexOf(".");
            int startBit = 0;
            if (i != -1) {
                startBit = Integer.parseInt(ip.substring(0, i));
            }

            //if 224.x.x.x - 239.x.x.x - multi cast
            if (startBit >= 224 && startBit <= 239) {
                MulticastSocket socket = new MulticastSocket(portNo); // must bind receive side
                socket.joinGroup(InetAddress.getByName(ip));
                server = socket;
                multicase = true;
            } else {
                if (!ip.equals("0.0.0.0")) {
                    bindAddr = InetAddress.getByName(ip);
                    server = new DatagramSocket(portNo, bindAddr);
                } else {
                    bindAddr = null;
                    server = new DatagramSocket(portNo);
                }
            }

            ipField1.setEditable(false);
            portField1.setEditable(false);

            connectButton.setText("Stop Listening");
            connectButton.setMnemonic('S');
            connectButton.setToolTipText("Stop Listening");
        } catch (Exception e) {
            error(e.getMessage(), "Starting Server at " + portNo);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if (multicase) {
            messagesField.setText("> MultiCase Server Joined on Port : " + portNo + NEW_LINE);
        } else {
            messagesField.setText("> Server Started on Port : " + portNo + NEW_LINE);
        }
        append("> ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        Encoding selectedEncoding = (Encoding) encodingBox1.getSelectedItem();
        udpServer = UdpServer.handle(this, server, selectedEncoding);
    }

    public synchronized void stop() {
        try {
            udpServer.setStop(true);
        } catch (Exception e) {
        }
        server = null;

        ipField1.setEditable(true);
        portField1.setEditable(true);

        connectButton.setText("Start Listening");
        connectButton.setMnemonic('S');
        connectButton.setToolTipText("Start Listening");
        append("> Server stopped");
        append("> ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public void sendMessage(String s) {
        try {
            String ip = ipField2.getText();
            String port = portField2.getText();
            if (ip == null || ip.equals("")) {
                JOptionPane.showMessageDialog(SocketTestUdp.this,
                        "No IP Address. Please enter IP Address",
                        "Error connecting", JOptionPane.ERROR_MESSAGE);
                ipField2.requestFocus();
                ipField2.selectAll();
                return;
            }
            if (port == null || port.equals("")) {
                JOptionPane.showMessageDialog(SocketTestUdp.this,
                        "No Port number. Please enter Port number",
                        "Error connecting", JOptionPane.ERROR_MESSAGE);
                portField2.requestFocus();
                portField2.selectAll();
                return;
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (!Util.checkHost(ip)) {
                JOptionPane.showMessageDialog(SocketTestUdp.this,
                        "Bad IP Address",
                        "Error connecting", JOptionPane.ERROR_MESSAGE);
                ipField2.requestFocus();
                ipField2.selectAll();
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            int portNo = 0;
            try {
                portNo = Integer.parseInt(port);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(SocketTestUdp.this,
                        "Bad Port number. Please enter Port number",
                        "Error connecting", JOptionPane.ERROR_MESSAGE);
                portField2.requestFocus();
                portField2.selectAll();
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }

            InetAddress toAddr = null;
            toAddr = InetAddress.getByName(ip);

            Encoding selectedEncoding = (Encoding) encodingBox2.getSelectedItem();
            if (client == null) {
                client = new DatagramSocket();
                UdpServer.handleClient(this, client, selectedEncoding); //listen for its response
            }
            buffer = s.getBytes(selectedEncoding.getCharset());
            pack = new DatagramPacket(buffer, buffer.length, toAddr, portNo);
            append("S[" + toAddr.getHostAddress() + ":" + portNo + "]: " + s);
            client.send(pack);
            sendField.setText("");
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(SocketTestUdp.this,
                    e.getMessage(), "Error Sending Message",
                    JOptionPane.ERROR_MESSAGE);
            client = null;
        }
    }

    public void error(String error) {
        if (error == null || error.equals(""))
            return;
        JOptionPane.showMessageDialog(SocketTestUdp.this,
                error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void error(String error, String heading) {
        if (error == null || error.equals(""))
            return;
        JOptionPane.showMessageDialog(SocketTestUdp.this,
                error, heading, JOptionPane.ERROR_MESSAGE);
    }

    public void append(String msg) {
        messagesField.append(msg + NEW_LINE);
        messagesField.setCaretPosition(messagesField.getText().length());
    }

    @Override
    public void setUpConfiguration(String ip, String port) {
        ipField1.setText(ip);
        portField1.setText(port);
        ipField2.setText(ip);
        portField2.setText(port);
    }
}

package net.sf.sockettest.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sf.sockettest.Util;
import net.sf.sockettest.Version;

/**
 * About Tab
 * @author Akshathkumar Shetty
 */
public class About extends JPanel {

    private static final long serialVersionUID = 1L;

    private ClassLoader cl = getClass().getClassLoader();
    public ImageIcon logo = new ImageIcon(
            cl.getResource("icons/logo.gif"));
    public ImageIcon ball = new ImageIcon(
            cl.getResource("icons/ball.gif"));

    private JPanel centerPanel;
    private JPanel topPanel;

    private JLabel productName = new JLabel("<html><font face=\"verdana\" size=10>"+
            "SocketTest",logo,JLabel.CENTER);
    private JTextArea readme = new JTextArea();
    private JScrollPane jsp;

    String html="<html><font face=\"verdana\" size=\"2\">";

    private JLabel versionText = new JLabel(html+"Version",ball,JLabel.LEFT);
    private JLabel version = new JLabel(html+": " + Version.VERSION_SHORT, JLabel.LEFT);

    private JLabel licenceText = new JLabel(html+"Licence",ball,JLabel.LEFT);
    private JLabel licence = new JLabel(html+": GNU Lesser General Public License", JLabel.LEFT);

    private JLabel origAuthorText = new JLabel(html+"Author 1",ball,JLabel.LEFT);
    private JLabel origAuthor = new JLabel(html+": Akshathkumar Shetty", JLabel.LEFT);

    private JLabel authorText = new JLabel(html+"Author 2",ball,JLabel.LEFT);
    private JLabel author = new JLabel(html+": Jochen Meierhofer", JLabel.LEFT);

    private JLabel copyrightText = new JLabel(html+"Copyright &copy; 2003-2008 Akshathkumar Shetty",ball,JLabel.LEFT);

    private JLabel websiteText = new JLabel(html+"Website",ball,JLabel.LEFT);
    private JLabel website = new JLabel(html+": http://sockettest.sourceforge.net/", JLabel.LEFT);

    private JLabel readmeText = new JLabel(html+"ReadMe",ball,JLabel.LEFT);

    private GridBagConstraints gbc = new GridBagConstraints();

    /** Creates a new instance of About */
    public About() {
        //Container cp = getContentPane();
        Container cp = this;

        topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        gbc.insets = new Insets( 2, 2, 2, 2 );
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        topPanel.add(productName, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        topPanel.add(versionText, gbc);
        gbc.gridx = 1;
        topPanel.add(version, gbc);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(Box.createHorizontalGlue(), gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        topPanel.add(licenceText, gbc);
        gbc.gridx = 1;
        topPanel.add(licence, gbc);
        gbc.gridx = 2;
        gbc.weightx = 1.0;//1.0
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(new JLabel(), gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        topPanel.add(origAuthorText, gbc);
        gbc.gridx = 1;
        topPanel.add(origAuthor, gbc);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(Box.createHorizontalGlue(), gbc);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        topPanel.add(authorText, gbc);
        gbc.gridx = 1;
        topPanel.add(author, gbc);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(Box.createHorizontalGlue(), gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        topPanel.add(websiteText, gbc);
        gbc.gridx = 1;
        topPanel.add(website, gbc);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(Box.createHorizontalGlue(), gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.gridwidth = 2;
        topPanel.add(copyrightText, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(Box.createHorizontalGlue(), gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        topPanel.add(readmeText, gbc);
        gbc.gridx = 1;
        topPanel.add(new JLabel(" "), gbc);
        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(Box.createHorizontalGlue(), gbc);


        centerPanel = new JPanel();
        readme.setText("Loading... readme");
        try {
            String cont = Util.readFile("readme.txt", About.this, Encoding.UTF_8.getCharset());
            readme.setText(cont);
        } catch (IOException e) {
            System.err.println("Error reading readme.txt " + e);
            readme.append(System.lineSeparator() + "Failed : " + e.getMessage());
        }
        readme.setEditable(false);
        readme.setLineWrap(true);
        readme.setWrapStyleWord(true);
        jsp = new JScrollPane(readme);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(jsp);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0,9,0,9));

        cp.setLayout(new BorderLayout(0,10));
        cp.add(topPanel,BorderLayout.NORTH);
        cp.add(centerPanel,BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }
}

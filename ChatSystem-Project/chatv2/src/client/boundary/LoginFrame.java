package client.boundary;


import client.control.ChatClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class LoginFrame { //login window, user inputs IP and port + username. Standard picture of user chooses from JFileChooser
    private JFrame frame;
    private String username;
    private JLabel avatar;
    private JTextField txtUSERNAME;
    private JTextField txtIP;
    private JTextField txtPORT;
    private JLabel lblUSERNAME;
    private JLabel lblIP;
    private JLabel lblPORT;
    private final JPanel mainpnl;
    private static JPanel EASTpnl;
    private static JPanel WESTpnl;
    private ChatClient chatClient;
    private JFileChooser fileChooser;
    private File chosenAvatar;
    private ChatFrame chatframe;
    private boolean userChangedAvatar = false;
    //private static Controller controller <-- läggs in sen, konstruktorn ska ha controller i parametern

    public LoginFrame(ChatClient chatClient, String ip, int port) throws IOException {
        this.chatClient = chatClient;
        frame = new JFrame();
        frame.setTitle("Login Window");
        mainpnl = new JPanel();
        mainpnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainpnl.setLayout(new BorderLayout());

        mainpnl.setBackground(Color.white);

        mainpnl.add(setUpTheEastPanel(), BorderLayout.CENTER);
        mainpnl.add(setUpTheWestPanel(), BorderLayout.WEST);

        frame.add(mainpnl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(350, 260);
        frame.setVisible(true);
        frame.setResizable(false);
    }

    /**
     * Sets up the east (right) side of the panel, that specifically deals with displaying an avatar as well as the selected avatar if User wish to change it.
     * @return the whole panel to the mainpanel
     */

    private JPanel setUpTheEastPanel() {
        EASTpnl = new JPanel();
        EASTpnl.setLayout(new BoxLayout(EASTpnl, BoxLayout.Y_AXIS));

        ImageIcon imgIcon = new ImageIcon("files/images/NoImageAvailable.jpeg"); //change into default image
        Image imgB4Resize = imgIcon.getImage();
        Image newImage = imgB4Resize.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(newImage);
        avatar = new JLabel(resizedIcon);
        avatar.setPreferredSize(new Dimension(resizedIcon.getIconWidth(), resizedIcon.getIconHeight()));

        EASTpnl.add(avatar);
        EASTpnl.add(Box.createRigidArea(new Dimension(0, 10)));
        EASTpnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton choosePicbtn = new JButton("Choose avatar");
        choosePicbtn.setBackground(Color.white);
        choosePicbtn.addActionListener(e -> changeAvatar());

        EASTpnl.add(choosePicbtn);
        EASTpnl.setBackground(Color.white);
        return EASTpnl;
    }


    /**
     * Method for changing the avatar. This event gets triggered if the button choosePicbtn is clicked on. The method
     * will retrieve the selected filepath as well as rescaling the image to fit the LoginFrame GUI.
     */
    private void changeAvatar() {
        fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(null);
        chosenAvatar = fileChooser.getSelectedFile();
        try {
            ImageIcon imageIcon = new ImageIcon(ImageIO.read(new File(chosenAvatar.getAbsolutePath())));
            Image image = imageIcon.getImage();
            Image newImage = image.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(newImage);
            avatar.setIcon(imageIcon);
            userChangedAvatar = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ImageIcon resizeAvatar(ImageIcon icon) {
        ImageIcon imageIcon = null;
        try {
            imageIcon = new ImageIcon(icon.getImage());
            Image image = imageIcon.getImage();
            Image newImage = image.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(newImage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return imageIcon;
    }

    /**
     * Sets up the west (left) side of the panel, that mainly deals with the server side as it retrieves input from the textfield as well as initiating
     * an attempt to connect to the server.
     * @return the whole panel to the mainpanel
     */
    private JPanel setUpTheWestPanel() {
        WESTpnl = new JPanel();
        WESTpnl.setLayout(new BoxLayout(WESTpnl, BoxLayout.Y_AXIS));

        //CREATES THE CONNECT BUTTON
        JButton loginbtn = new JButton("Connect to Server");
        loginbtn.setBackground(Color.white);

        //CREATES THE TEXT FIELD
        txtUSERNAME = new JTextField("");
        txtPORT = new JTextField("721"); //change to the standard port
        txtIP = new JTextField("localhost");

        //CREATES THE LABEL
        lblUSERNAME = new JLabel("Username: ");
        lblIP = new JLabel("IP: ");
        lblPORT = new JLabel("Port: ");

        WESTpnl.add(lblUSERNAME);
        WESTpnl.add(txtUSERNAME);

        WESTpnl.add(lblIP);
        WESTpnl.add(txtIP);

        WESTpnl.add(lblPORT);
        WESTpnl.add(txtPORT);

        WESTpnl.add(Box.createRigidArea(new Dimension(0, 10)));
        WESTpnl.add(loginbtn);
        WESTpnl.setBackground(Color.white);

        loginbtn.addActionListener(e -> registerInfo(txtUSERNAME.getText(), avatar.getIcon()));
        return WESTpnl;
    }

    private void registerInfo(String text, Icon icon) {
        username = txtUSERNAME.getText();
        chatClient.createUser(text, (ImageIcon) icon);
    }

    public void setAvatar(ImageIcon profileImage) {
        avatar.setIcon(profileImage);
        userChangedAvatar = true;
        disableEverything();
    }

    private void disableEverything() {
        txtUSERNAME.setEnabled(false);
        txtPORT.setEnabled(false);
        txtIP.setEnabled(false);
    }

    public boolean isUserChangedAvatar() {
        return userChangedAvatar;
    }

    public void closeFrame(){
        frame.dispose();
    }
}
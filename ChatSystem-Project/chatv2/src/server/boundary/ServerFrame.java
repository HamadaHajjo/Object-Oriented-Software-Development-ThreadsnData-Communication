package server.boundary;

import server.control.LogController;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

public class ServerFrame { //status server logger, keeps track of all interactions between clients
    private final JFrame frame;
    private final JPanel mainpnl;
    private static JPanel WESTpnl;
    private JPanel EASTpnl;
    private JList statusmsgs;
    private String[] showStatusOnServerFrame = new String[10];
    private static JLabel lblDate;
    private JTextField txtDate;
    private static JLabel lblTime1;
    private static JTextField txtTime1;
    private static JLabel lblTime2;
    private static JTextField txtTime2;
    private static JScrollPane scrollPane;
    private static JScrollBar vertical;

    private LogController logController;
    // TODO: Register input from the textfield
    public ServerFrame(LogController logController) {
        this.logController = logController;
        frame = new JFrame();
        frame.setTitle("SERVER STATUS LOG");
        System.out.println("Launching ServerFrame");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); <-- must always be online

        mainpnl = new JPanel();
        mainpnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainpnl.setLayout(new BorderLayout());

        mainpnl.add(setUpTheWestPanel(), BorderLayout.CENTER);
        mainpnl.add(setUpTheEastPanel(), BorderLayout.EAST);

        frame.add(mainpnl);
        frame.pack();
        frame.setSize(800, 400);
        frame.setVisible(true);
        frame.setResizable(false);

    }

    private JPanel setUpTheWestPanel() {
        WESTpnl = new JPanel();
        WESTpnl.setLayout(new BoxLayout(WESTpnl, BoxLayout.Y_AXIS));

        statusmsgs = new JList();
        scrollPane = new JScrollPane(statusmsgs);
        vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum() + 1);

        WESTpnl.add(scrollPane);
        WESTpnl.setBackground(Color.darkGray);
        WESTpnl.setPreferredSize(new Dimension(350, 400));
        //WESTpnl.setLayout(new BoxLayout(EASTpnl, BoxLayout.Y_AXIS));
        return WESTpnl;
    }

    private JPanel setUpTheEastPanel() {
        EASTpnl = new JPanel();
        EASTpnl.setLayout(new BoxLayout(EASTpnl, BoxLayout.Y_AXIS));

        lblDate = new JLabel("Show date");
        txtDate = new JTextField("2023-03-12");
        txtDate.setPreferredSize(new Dimension(30, 5));

        lblTime1 = new JLabel("between...");
        txtTime1 = new JTextField("11:55:00");
        txtTime1.setPreferredSize(new Dimension(30, 5));

        lblTime2 = new JLabel("and");
        txtTime2 = new JTextField("12:20:00");
        txtTime2.setPreferredSize(new Dimension(30, 5));

        JButton showTime = new JButton("Show Time");
        showTime.setBackground(Color.white);

        EASTpnl.add(Box.createRigidArea(new Dimension(0, 60)));
        EASTpnl.add(lblDate);
        EASTpnl.add(txtDate);
        EASTpnl.add(Box.createRigidArea(new Dimension(0, 10)));

        EASTpnl.add(lblTime1);
        EASTpnl.add(txtTime1);
        EASTpnl.add(Box.createRigidArea(new Dimension(0, 10)));

        EASTpnl.add(lblTime2, Component.CENTER_ALIGNMENT);
        EASTpnl.add(txtTime2);
        EASTpnl.add(Box.createRigidArea(new Dimension(0, 30)));

        EASTpnl.add(showTime, Component.CENTER_ALIGNMENT);

        EASTpnl.setPreferredSize(new Dimension(150, 400));
        EASTpnl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        showTime.addActionListener(e -> registerInput(txtDate.getText(), txtTime1.getText(), txtTime2.getText()));


        return EASTpnl;
    }

    private void registerInput(String date, String time1, String time2) {
        try {
            logController.displayMessagesBetween(date, time1, time2);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }


    public void registerStatusMessage(String msg) {
        for (int i = 0; i < showStatusOnServerFrame.length; i++) {
            if (showStatusOnServerFrame[i] == null) {
                showStatusOnServerFrame[i] = msg + "\n";
                break;
            }
        }
    }

    public void setLogController(LogController logController) {
        this.logController = logController;
    }

    public void updateStatusMsgs(String[] msgsInput) {
        for (int i = 0; i < msgsInput.length; i++) {
        }
        this.statusmsgs.setListData(msgsInput);
        vertical.setValue(vertical.getMaximum() + 1);
    }
}

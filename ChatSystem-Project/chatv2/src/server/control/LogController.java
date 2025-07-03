package server.control;

import entity.Message;
import entity.User;
import server.entity.Action;
import server.entity.ActionType;
import server.boundary.ServerFrame;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class LogController {
    private ArrayList<Action> log;
    private String filename;
    private ServerFrame serverFrame;

    public LogController(String logFileName, ServerFrame serverFrame) {
        this.filename = logFileName;
        this.serverFrame = serverFrame;
        log = new ArrayList<>();
        readLog(logFileName);
        updateFrame();
    }

    private void updateFrame() {
        String[] logString = new String[log.size()];
        for (int i = 0; i < logString.length; i++) {
            logString[i] = log.get(i).toString();
        }
        serverFrame.updateStatusMsgs(logString);
    }

    private void updateFrame(ArrayList<Action> displayCurrLog) {
        String[] showMsgsBetween = new String[displayCurrLog.size()];
        for (int i = 0; i < showMsgsBetween.length; i++) {
            showMsgsBetween[i] = displayCurrLog.get(i).toString();
            System.out.println(showMsgsBetween[i] + " this is what its receiving");
        }

        serverFrame.updateStatusMsgs(showMsgsBetween);

        displayCurrLog.clear();
    }

    private void writeLog() {
        updateFrame();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(log);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readLog(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            log = (ArrayList<Action>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void logMessageSent(Message message) {
        log.add(new Action(ActionType.MESSAGE_SENT, null, message, new Date()));
        writeLog();
    }

    public void logUnreadMessageStored(Message message) {
        log.add(new Action(ActionType.UNREADMESSAGE_STORED, null, message, new Date()));
        writeLog();
    }

    public void logUnreadMessageSent(Message message) {
        log.add(new Action(ActionType.UNREADMESSAGE_SENT, null, message, new Date()));
        writeLog();
    }

    public void logUserConnect(User user) {
        log.add(new Action(ActionType.USER_CONNECTED, user, null, new Date()));
        writeLog();
    }

    public void logUserDisconnect(User user) {
        log.add(new Action(ActionType.USER_DISCONNECTED, user, null, new Date()));
        writeLog();
        writeLog();
    }

    public void displayMessagesBetween(String date, String time1, String time2) throws IOException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("CET"));
        Date dateToCheck1 = dateFormat.parse(date + " " + time1);
        Date dateToCheck2 = dateFormat.parse(date + " " + time2);
        System.out.println(time1 + " format");
        System.out.println(time2 + " format");

        ArrayList<Action> displayCurrLog = new ArrayList<>();
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("files/log.dat"));

        for (int i = 0; i < log.size(); i++) {
            Date entryDate = log.get(i).getDate();
            if ((entryDate.after(dateToCheck1) && entryDate.before(dateToCheck2)) || entryDate.equals(dateToCheck1) || entryDate.equals(dateToCheck2)) {
                displayCurrLog.add(log.get(i));
                System.out.println( log.get(i) + " date received");
            }
        }
        updateFrame(displayCurrLog);
    }

}

package co.edu.uptc.controller;

import co.edu.uptc.interfaces.IController;
import co.edu.uptc.net.JSONReader;
import co.edu.uptc.net.Message;
import co.edu.uptc.net.MessageCategory;
import co.edu.uptc.view.MainFrame;

import java.io.*;
import java.net.Socket;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Controller implements IController {
    private static final int PORT = 10087;
    private static final String HOST = "localhost";
    private Socket socket;
    private MainFrame frame;
    private ListenerThread listener;
    private DataOutputStream sender;
    private JSONReader jsonReader;
    private boolean closed;

    public Controller() {
        try {
            this.socket = new Socket(HOST, PORT);
            this.listener = new ListenerThread(this, socket);
            this.sender = new DataOutputStream(socket.getOutputStream());
            this.jsonReader = new JSONReader();
        } catch (IOException e) {
            close("No se pudo conectar con el servidor (" + HOST + ":" + PORT + ").\nVerifica que esté en ejecución.");
        }
    }

    public void init() {
        this.frame = new MainFrame(this);
        listener.start();
    }

    public void close() {
        close("Se perdió la conexión con el servidor");
    }

    public void close(String message) {
        synchronized (this) {
            if (closed) return;
            closed = true;
        }
        if (message != null) {
            showMessage(message);
        }
        System.exit(0);
    }

    private void showMessage(String message) {
        Runnable show = () -> {
            if (frame != null) {
                frame.showGeneralError(message);
            } else {
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                show.run();
            } else {
                SwingUtilities.invokeAndWait(show);
            }
        } catch (Exception e) {
            
        }
    }

    public void processData(String data) {
        Message message = jsonReader.jsonToMessage(data);
        sessionCommands(message.getCategory());
        workCircleCommands(message.getCategory());
        projectCommands(message.getCategory());
        historyCommands(message.getCategory());
        requestsCommands(message.getCategory());
        approveCommands(message.getCategory());
        teamCommands(message.getCategory());
        remainingCommands(message.getCategory());
    }

    private void sessionCommands(MessageCategory category) {
        if (category == MessageCategory.REGISTER || category == MessageCategory.LOGIN || category == MessageCategory.LOGOUT) {
            switch (category) {
                case REGISTER:
                    break;
                case LOGIN:
                    break;
                case LOGOUT:
                    break;
            }
        }
    }

    private void workCircleCommands(MessageCategory category) {
        switch (category) {
            case GET_CIRCLE:
                break;
            case ADD_TO_CIRCLE:
                break;
            case GET_INVITATIONS:
                break;
            case ACCEPT_INVITATION:
                break;
            case REJECT_INVITATION:
                break;
        }
    }

    private void projectCommands(MessageCategory category) {
        switch (category) {
            case CREATE_PROJECT:
                break;
            case GET_PROJECT_INFO:
                break;
        }
    }

    private void historyCommands(MessageCategory category) {
        switch (category) {
            case GET_SUMMARY:
                break;
            case GET_HISTORY:
                break;
        }
    }

    private void requestsCommands(MessageCategory category) {
        switch (category) {
            case CREATE_REQUEST:
                break;
            case GET_MY_REQUESTS:
                break;
        }
    }

    private void approveCommands(MessageCategory category) {
        switch (category) {
            case GET_PENDING:
                break;
            case APPROVE_REQUEST:
                break;
            case REJECT_REQUEST:
                break;
        }
    }

    private void teamCommands(MessageCategory category) {
        switch (category) {
            case GET_TEAM:
                break;
            case INVITE_MEMBER:
                break;
            case REMOVE_MEMBER:
                break;
            case TRANSFER_ADMIN:
                break;
            case CLOSE_PROJECT:
                break;
        }
    }

    private void remainingCommands(MessageCategory category) {
        switch (category) {
            case LEAVE_PROJECT:
                break;
            case NOTIFY:
                break;
            case EXIT:
                break;
        }
    }

    public synchronized void sendData(Message message) {
        if (sender == null || message == null) return;

        try {
            sender.writeUTF(jsonReader.messageToJson(message));
            sender.flush();
        } catch (IOException e) {
            close();
        }
    }

    public void sendExit() {
        sendData(new Message(MessageCategory.EXIT, ""));
    }

    @Override
    public void register(String user, String password) {

    }

    @Override
    public void login(String user, String password) {

    }

    @Override
    public void logout() {

    }

    @Override
    public void addToCircle(String user) {

    }

    @Override
    public void acceptInvitation(String projectName) {

    }

    @Override
    public void rejectInvitation(String projectName) {

    }

    @Override
    public void createProject(String name, double budget, Map<String, Double> categories, String password) {

    }

    @Override
    public void createRequest(String concept, double amount, String category) {

    }

    @Override
    public void getHistory(String category, String status, String from, String to, String order) {

    }

    @Override
    public void approveRequest(int id, String reason) {

    }

    @Override
    public void rejectRequest(int id, String reason) {

    }

    @Override
    public void inviteMember(String user) {

    }

    @Override
    public void removeMember(String user, String password) {

    }

    @Override
    public void transferAdmin(String user, String password) {

    }

    @Override
    public void closeProject(String password) {

    }

    @Override
    public void leaveProject() {

    }

    @Override
    public void getCircle() {

    }
    @Override
    public void getInvitations() {

    }

    @Override
    public void getProjectInfo() {

    }

    @Override
    public void getSummary() {

    }

    @Override
    public void getMyRequests() {

    }

    @Override
    public void getPending() {

    }

    @Override
    public void getTeam() {

    }
}

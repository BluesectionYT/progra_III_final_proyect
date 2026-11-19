package co.edu.uptc.controller;

import co.edu.uptc.interfaces.IProjectManager;
import co.edu.uptc.model.User;
import co.edu.uptc.net.JSONReader;
import co.edu.uptc.net.Message;
import co.edu.uptc.net.MessageCategory;
import co.edu.uptc.net.Observer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class ClientThread extends Thread implements Observer {
    private IProjectManager projectManager;
    private Socket socket;
    private DataOutputStream sender;
    private ListenerThread listener;
    private Controller controller;
    private User user;
    private JSONReader jsonReader;

    public ClientThread(Socket socket, IProjectManager projectManager, Controller controller) {
        this.socket = socket;
        this.projectManager = projectManager;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            this.sender = new DataOutputStream(socket.getOutputStream());
            this.listener = new ListenerThread(this, socket);
            this.jsonReader = new JSONReader();
            listener.start();
            System.out.println("Cliente conectado");
        } catch (IOException e) {
            closeConnection();
        }
    }

    public void sendMessage(Message message) {
        String messageAsString = jsonReader.messageToJson(message);
        if (messageAsString != null) {
            try {
                sender.writeUTF(messageAsString);
            } catch (IOException e) {
                closeConnection();
            }
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
                closeConnection();
                break;
        }
    }

    public void closeConnection() {
        controller.detach(this);
        if (listener != null) {
            listener.interrupt();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(MessageCategory category, String data) {
        Message message = new Message(category, data);
        sendMessage(message);
    }

    public void sendResponse(boolean ok, String errorMsg, MessageCategory category, Object extraData) {
        Map<String, Object> responseContent = new HashMap<>();
        responseContent.put("ok", ok);
        
        if (!ok && errorMsg != null) responseContent.put("error", errorMsg);
        
        if (extraData != null) responseContent.put("data", extraData);

        Gson gson = new Gson();
        String jsonString = gson.toJson(responseContent);

        Message responseMessage = new Message(category, jsonString);
        try {
            sender.writeUTF(gson.toJson(responseMessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

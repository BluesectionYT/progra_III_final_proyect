package co.edu.uptc.controller;

import co.edu.uptc.interfaces.IPasswordHasher;
import co.edu.uptc.interfaces.IProjectManager;
import co.edu.uptc.model.ProjectManager;
import co.edu.uptc.net.*;
import co.edu.uptc.model.User;
import co.edu.uptc.net.Message;
import co.edu.uptc.enums.MessageCategory;
import co.edu.uptc.interfaces.Observer;
import co.edu.uptc.interfaces.Subject;
import co.edu.uptc.persistence.PersistenceJSON;
import co.edu.uptc.persistence.StateMapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Controller implements Subject {
    private static final int PORT = 10087;
    private ServerSocket serverSocket;
    private IProjectManager projectManager;
    private ArrayList<Observer> observers;
    private PersistenceJSON persistence;
    private IPasswordHasher hasher;

    public Controller() {
        try {
            this.serverSocket = new ServerSocket(PORT);
            this.observers = new ArrayList<>();
            this.persistence = new PersistenceJSON();
            this.hasher = new PasswordHasher();
            this.projectManager = new ProjectManager();
            StateMapper mapper = new StateMapper();
            mapper.restore(persistence.readState("data/data.json"), projectManager);
            Runtime.getRuntime().addShutdownHook(new Thread(this::saveData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void init() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(socket, projectManager, this);
                this.attach(clientThread);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyUser(String username, MessageCategory category, Object data) {
        for (Observer obs : observers) {
            if (obs instanceof ClientThread) {
                ClientThread client = (ClientThread) obs;

                if (client.getUser() != null && client.getUser().getUsername().equals(username)) {
                    client.sendResponse(true, null, category, data);
                    break;
                }
            }
        }
    }

    public IPasswordHasher getHasher() {
        return hasher;
    }

    public void saveData() {
        StateMapper stateMapper = new StateMapper();
        persistence.saveState("data/data.json", stateMapper.toSnapshot(this.projectManager));
    }

    public boolean isLoggedIn(String username) {
        for (Observer obs : observers) {
            if (obs instanceof ClientThread) {
                User u = ((ClientThread) obs).getUser();
                if (u != null && u.getUsername().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void attach(Observer observer) {
        observers.add(observer);
        System.out.println("New Client attached\n" + observers.size() + " clients attached\n");
    }

    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyAllObservers(Observer client, Message data) {
        for (int i = 0; i < observers.size(); i++) {
            Observer observer = observers.get(i);
            if (!observer.equals(client)) {
                observer.update(data.getCategory(), data.getText());
            }
        }
    }
}

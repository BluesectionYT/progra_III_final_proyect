package co.edu.uptc.controller;

import co.edu.uptc.enums.MessageCategory;
import co.edu.uptc.interfaces.IPasswordHasher;
import co.edu.uptc.interfaces.IPersistence;
import co.edu.uptc.interfaces.IProjectManager;
import co.edu.uptc.interfaces.Observer;
import co.edu.uptc.interfaces.Subject;
import co.edu.uptc.model.ProjectManager;
import co.edu.uptc.model.User;
import co.edu.uptc.net.Message;
import co.edu.uptc.net.PasswordHasher;
import co.edu.uptc.persistence.PersistenceJSON;
import co.edu.uptc.persistence.StateMapper;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Controller implements Subject {
    private static final int PORT = 10087;
    private static final String DATA_PATH = "data/data.json";
    private ServerSocket serverSocket;
    private IProjectManager projectManager;
    private List<Observer> observers;
    private IPersistence persistence;
    private StateMapper stateMapper;
    private IPasswordHasher hasher;

    public Controller() {
        try {
            this.serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.observers = new ArrayList<>();
        this.persistence = new PersistenceJSON();
        this.stateMapper = new StateMapper();
        this.hasher = new PasswordHasher();
        this.projectManager = new ProjectManager();
        this.observers = new CopyOnWriteArrayList<>();
        loadData();
        registerShutdownHook();
    }

    private void loadData() {
        stateMapper.restore(persistence.readState(DATA_PATH), projectManager);
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveData));
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
        ClientThread client = findClient(username);
        if (client != null) {
            client.sendResponse(true, null, category, data);
        }
    }

    public boolean isLoggedIn(String username) {
        return findClient(username) != null;
    }

    private ClientThread findClient(String username) {
        for (Observer observer : observers) {
            if (observer instanceof ClientThread && belongsTo((ClientThread) observer, username)) {
                return (ClientThread) observer;
            }
        }
        return null;
    }

    private boolean belongsTo(ClientThread client, String username) {
        User user = client.getUser();
        return user != null && user.getUsername().equals(username);
    }

    public IPasswordHasher getHasher() {
        return hasher;
    }

    public void saveData() {
        persistence.saveState(DATA_PATH, stateMapper.toSnapshot(this.projectManager));
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

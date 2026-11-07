package co.edu.uptc.controller;

import co.edu.uptc.model.ProjectManager;
import co.edu.uptc.net.Observer;
import co.edu.uptc.net.Subject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Controller implements Subject {
    private static final int PORT = 10087;
    private ServerSocket serverSocket;
    private Socket socket;
    private ProjectManager projectManager;
    private ArrayList<Observer> observers;

    public Controller() {
        try {
            this.serverSocket = new ServerSocket(PORT);
            this.observers = new ArrayList<>();
            this.projectManager = new ProjectManager();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void init() {
        try {
            while (true) {
                this.socket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(socket, projectManager, this);
                this.attach(clientThread);
                clientThread.start();
            }
        } catch (IOException e) {

        }
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
    public void notifyAllObservers(ClientThread client) {
        for (int i = 0; i < observers.size(); i++) {
            Observer observer = observers.get(i);
            if(!observer.equals(client)) {
                observer.update();
            }
        }
    }
}

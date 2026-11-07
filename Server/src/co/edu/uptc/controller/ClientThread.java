package co.edu.uptc.controller;

import co.edu.uptc.model.Project;
import co.edu.uptc.model.ProjectManager;
import co.edu.uptc.net.JSONReader;
import co.edu.uptc.net.Observer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientThread extends Thread implements Observer {
    private ProjectManager projectManager;
    private Socket socket;
    private DataOutputStream sender;
    private ListenerThread listener;
    private Controller controller;
    private JSONReader jsonReader;

    public ClientThread(Socket socket, ProjectManager projectManager, Controller controller) {
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
            this.interrupt();
        }
    }

    public void sendMessage(String data) {
        String message = jsonReader.messageToJson(data);
        if (message != null) {
            try {
                sender.writeUTF(message);
            } catch (IOException e) {
                this.interrupt();
            }
        }
    }

    public void processData(String data) {
        String line = jsonReader.jsonToMessage(data);
        String result = "0";
        long[] numbers = new long[]{};
        try {

        } catch (NumberFormatException e) {
        }
    }

    @Override
    public void update() {
        sendMessage("Update");
    }
}

package co.edu.uptc.controller;

import co.edu.uptc.net.JSONReader;
import co.edu.uptc.net.Message;
import co.edu.uptc.net.MessageCategory;
import co.edu.uptc.view.MainFrame;

import java.io.*;
import java.net.Socket;

public class Controller {
    private static final int PORT = 10087;
    private static final String HOST = "localhost";
    private Socket socket;
    private MainFrame frame;
    private ListenerThread listener;
    private DataOutputStream sender;
    private JSONReader jsonReader;

    public Controller() {
        try {
            this.socket = new Socket(HOST, PORT);
            this.listener = new ListenerThread(this, socket);
            this.sender = new DataOutputStream(socket.getOutputStream());
            this.jsonReader = new JSONReader();
        } catch (IOException e) {
            System.exit(0);
        }
    }

    public void init() {
        this.frame = new MainFrame(this);
        listener.start();
    }

    public void close() {
        frame.showGeneralError("Servidor desconectado");
        System.exit(0);
    }

    public void processData(String data) {
        Message lineToProcess = jsonReader.jsonToMessage(data);
    }

    public void sendData(MessageCategory category, String data) {
        String lineToSend = jsonReader.messageToJson(category, data);
        try {
            sender.writeUTF(lineToSend);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendExit() {
        String lineToSend = jsonReader.messageToJson(MessageCategory.EXIT, "");
        try {
            sender.writeUTF(lineToSend);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

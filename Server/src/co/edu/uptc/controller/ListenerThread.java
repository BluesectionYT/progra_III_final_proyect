package co.edu.uptc.controller;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ListenerThread extends Thread{
    private DataInputStream input;
    private ClientThread clientThread;

    public ListenerThread(ClientThread clientThread, Socket socket) throws IOException {
        this.clientThread = clientThread;
        this.input = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        boolean connected = true;
        while (connected) {
            try {
                String line = input.readUTF();
                String[] data = line.split(",");
                clientThread.processData(data);
            } catch (IOException e) {
                connected = false;
            }
        }
    }
}

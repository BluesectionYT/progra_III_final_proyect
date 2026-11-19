package co.edu.uptc.controller;

import java.io.*;
import java.net.Socket;

import co.edu.uptc.interfaces.IController;

public class ListenerThread extends Thread {
    private DataInputStream input;
    private IController controller;

    public ListenerThread(IController controller, Socket socket) throws IOException {
        this.controller = controller;
        this.input = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        boolean connected = true;
        while (connected) {
            try {
                String line = input.readUTF();
                controller.processData(line);
            } catch (IOException e) {
                connected = false;
                controller.close("conexion perdida");
            }
        }
    }
}

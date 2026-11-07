package co.edu.uptc.net;

import co.edu.uptc.controller.ClientThread;

public interface Subject {
    public void attach(Observer observer);
    public void detach(Observer observer);
    public void notifyAllObservers(ClientThread client);
}

package co.edu.uptc.interfaces;

import co.edu.uptc.net.Message;

public interface Subject {
    public void attach(Observer observer);
    public void detach(Observer observer);
    public void notifyAllObservers(Observer client, Message data);
}

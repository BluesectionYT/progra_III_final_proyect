package co.edu.uptc.net;

public interface Subject {
    public void attach(Observer observer);
    public void detach(Observer observer);
    public void notifyAllObservers(Observer client, Message data);
}

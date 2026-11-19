package co.edu.uptc.interfaces;

import java.util.Map;

public interface IController {
    void register(String user, String password);
    void login(String user, String password);
    void logout();
    void addToCircle(String user);
    void acceptInvitation(String projectName);
    void rejectInvitation(String projectName);
    void createProject(String name, double budget, Map<String, Double> categories, String password);
    void createRequest(String concept, double amount, String category);
    void getHistory(String category, String status, String from, String to, String order);
    void approveRequest(int id, String reason);
    void rejectRequest(int id, String reason);
    void inviteMember(String user);
    void removeMember(String user, String password);
    void transferAdmin(String user, String password);
    void closeProject(String password);
    void leaveProject();
}

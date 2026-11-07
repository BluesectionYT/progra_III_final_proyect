package co.edu.uptc.model;

import java.util.ArrayList;

public class ProjectManager {
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Project> projects = new ArrayList<>();

    public User registerUser(String username, String passwordHash) {
        if (findUser(username) != null) throw new IllegalArgumentException("Usuario ya existe");
        User user = new User(username, passwordHash);
        users.add(user);
        return user;
    }

    public User findUser(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
    }

    public void addProject(Project projectToAdd) {
        projects.add(projectToAdd);
    }

    public void removeProject(Project projectToAdd) {
        projects.remove(projectToAdd);
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}

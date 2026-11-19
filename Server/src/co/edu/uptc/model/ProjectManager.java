package co.edu.uptc.model;

import co.edu.uptc.interfaces.IProjectManager;

import java.util.ArrayList;

public class ProjectManager implements IProjectManager {
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Project> projects = new ArrayList<>();

    @Override
    public User registerUser(String username, String passwordHash) {
        if (findUser(username) != null) throw new IllegalArgumentException("Usuario ya existe");
        User user = new User(username, passwordHash);
        users.add(user);
        return user;
    }

    @Override
    public User findUser(String username) {
        return users.stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
    }

    @Override
    public void addProject(Project projectToAdd) {
        if (projects.stream().anyMatch(p -> p.getName().equalsIgnoreCase(projectToAdd.getName()))) {
            throw new IllegalArgumentException("Ya existe un proyecto con ese nombre");
        }
        projects.add(projectToAdd);
    }

    @Override
    public Project findProject(String name) {
        return projects.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void removeProject(Project projectToAdd) {
        projects.remove(projectToAdd);
    }

    @Override
    public ArrayList<User> closeProject(Project project) {
        if (!project.isActive()) {
            throw new IllegalStateException("El proyecto ya está cerrado");
        }
        ArrayList<User> affected = new ArrayList<>(project.getMembers());
        affected.add(project.getAdmin());
        project.close();
        projects.remove(project);
        for (User user : users) {
            user.getPendingInvitations().remove(project);
        }
        return affected;
    }

    public ArrayList<Project> getProjects() {
        return projects;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}

package co.edu.uptc.model;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.interfaces.IProjectManager;

import java.util.ArrayList;

public class ProjectManager implements IProjectManager {
    private ArrayList<User> users;
    private ArrayList<Project> projects;

    public ProjectManager() {
        this.users = new ArrayList<>();
        this.projects = new ArrayList<>();
    }

    @Override
    public User registerUser(String username, String passwordHash) {
        if (findUser(username) != null) {
            throw new IllegalArgumentException(ErrorMessages.USER_ALREADY_REGISTERED.getText());
        }
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
            throw new IllegalArgumentException(ErrorMessages.PROJECT_NAME_TAKEN.getText());
        }
        projects.add(projectToAdd);
    }

    @Override
    public Project findProject(String name) {
        return projects.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public void removeProject(Project projectToRemove) {
        projects.remove(projectToRemove);
    }

    @Override
    public ArrayList<User> closeProject(Project project) {
        if (!project.isActive()) {
            throw new IllegalStateException(ErrorMessages.PROJECT_ALREADY_CLOSED.getText());
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

    @Override
    public ArrayList<Project> getProjects() {
        return projects;
    }

    @Override
    public ArrayList<User> getUsers() {
        return users;
    }

    @Override
    public void setProjects(ArrayList<Project> projects) {
        this.projects = projects;
    }

    @Override
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}

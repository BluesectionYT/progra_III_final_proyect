package co.edu.uptc.interfaces;

import co.edu.uptc.model.Project;
import co.edu.uptc.model.User;

import java.util.ArrayList;

public interface IProjectManager {
    User registerUser(String username, String passwordHash);

    User findUser(String username);

    void addProject(Project projectToAdd);

    Project findProject(String name);

    void removeProject(Project projectToRemove);

    ArrayList<User> closeProject(Project project);

    ArrayList<User> findPendingInvitees(Project project);

    ArrayList<Project> getProjects();

    ArrayList<User> getUsers();

    void setProjects(ArrayList<Project> projects);

    void setUsers(ArrayList<User> users);
}

package co.edu.uptc.interfaces;

import co.edu.uptc.model.Project;
import co.edu.uptc.model.User;

import java.util.ArrayList;

public interface IProjectManager {
    User registerUser(String username, String passwordHash);

    User findUser(String username);

    void addProject(Project projectToAdd);

    void removeProject(Project projectToAdd);

    ArrayList<User> closeProject(Project project);
}

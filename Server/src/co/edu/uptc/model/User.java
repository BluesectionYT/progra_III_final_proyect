package co.edu.uptc.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String passwordHash;
    private List<User> workCircle;
    private List<Project> pendingInvitations;
    private Project currenProject;
    private Role role;
    
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        workCircle = new ArrayList<>();
        pendingInvitations = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public List<User> getWorkCircle() {
        return workCircle;
    }

    public List<Project> getPendingInvitations() {
        return pendingInvitations;
    }

    public Project getCurrenProject() {
        return currenProject;
    }

    public Role getRole() {
        return role;
    }

    public void setCurrenProject(Project currenProject) {
        this.currenProject = currenProject;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void addToWorkcircle(User user){
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }

        if (!isInWorkcircle(user)) {
            this.workCircle.add(user);
        }
    }

    public boolean isInWorkcircle(User user){
        if (user == null) return false;
        return this.workCircle.contains(user);
    }

    public void receiveInvitation(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("El proyecto no puede ser nulo");
        }

        if (!this.pendingInvitations.contains(project)) {
            this.pendingInvitations.add(project);
        }
    }

    public void acceptInvitation(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("El proyecto no puede ser nulo");
        }
        if (!this.pendingInvitations.contains(project)) {
            throw new IllegalStateException("No existe una invitación pendiente para este proyecto");
        }

        this.currenProject = project;
        this.pendingInvitations.remove(project);
    }

    public void rejectInvitation(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("El proyecto no puede ser nulo");
        }
        if (!this.pendingInvitations.contains(project)) {
            throw new IllegalStateException("No existe una invitación pendiente para este proyecto");
        }

        this.pendingInvitations.remove(project);
    }

    public void leaveProject() {
        if (!hasProject()) {
            throw new IllegalStateException("El usuario no pertenece a ningún proyecto actualmente");
        }
        
        this.currenProject = null;
    }

    public boolean hasProject() {
        return this.currenProject != null;
    }
}

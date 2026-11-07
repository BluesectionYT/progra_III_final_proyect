package co.edu.uptc.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private String username;
    private String passwordHash;
    private List<User> workCircle;
    private List<Project> pendingInvitations;
    private Project currentProject;
    private Role role;
    
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        workCircle = new ArrayList<>();
        pendingInvitations = new ArrayList<>();
        this.currentProject = null;
        this.role = Role.MEMBER;
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

    public Project getCurrentProject() {
        return currentProject;
    }

    public Role getRole() {
        return role;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void addToWorkCircle(User user){
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (!isInWorkCircle(user)) {
            this.workCircle.add(user);
        }
    }

    public boolean isInWorkCircle(User user){
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
        project.addMember(this);
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
        if (!hasProject()){
            throw new IllegalStateException("El usuario no pertenece a ningún proyecto actualmente");
        }
        if (role == Role.ADMIN) {
            throw new IllegalStateException("El administrador debe transferir el cargo o cerrar el proyecto");
        }
        currentProject.removeMember(this);
    }

    public boolean hasProject() {
        return this.currentProject != null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(passwordHash, user.passwordHash) && Objects.equals(workCircle, user.workCircle) && Objects.equals(pendingInvitations, user.pendingInvitations) && Objects.equals(currentProject, user.currentProject) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, passwordHash, workCircle, pendingInvitations, currentProject, role);
    }
}

package co.edu.uptc.model;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.enums.Role;

import java.util.ArrayList;
import java.util.List;

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
        this.workCircle = new ArrayList<>();
        this.pendingInvitations = new ArrayList<>();
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

    public void addToWorkCircle(User user) {
        if (user == null) {
            throw new IllegalArgumentException(ErrorMessages.USER_NULL.getText());
        }
        if (user.equals(this)) {
            throw new IllegalArgumentException(ErrorMessages.SELF_IN_CIRCLE.getText());
        }
        if (!isInWorkCircle(user)) {
            this.workCircle.add(user);
        }
    }

    public boolean isInWorkCircle(User user) {
        return user != null && this.workCircle.contains(user);
    }

    public void receiveInvitation(Project project) {
        if (project == null) {
            throw new IllegalArgumentException(ErrorMessages.PROJECT_NULL.getText());
        }
        if (!project.isActive()) {
            throw new IllegalStateException(ErrorMessages.PROJECT_IS_INACTIVE.getText());
        }
        if (!this.pendingInvitations.contains(project)) {
            this.pendingInvitations.add(project);
        }
    }

    public void acceptInvitation(Project project) {
        requirePendingInvitation(project);
        project.addMember(this);
        this.pendingInvitations.remove(project);
    }

    public void rejectInvitation(Project project) {
        requirePendingInvitation(project);
        this.pendingInvitations.remove(project);
    }

    private void requirePendingInvitation(Project project) {
        if (project == null) {
            throw new IllegalArgumentException(ErrorMessages.PROJECT_NULL.getText());
        }
        if (!this.pendingInvitations.contains(project)) {
            throw new IllegalStateException(ErrorMessages.NO_PENDING_INVITATION.getText());
        }
    }

    public void leaveProject() {
        if (!hasProject()) {
            throw new IllegalStateException(ErrorMessages.USER_WITHOUT_PROJECT.getText());
        }
        if (role == Role.ADMIN) {
            throw new IllegalStateException(ErrorMessages.ADMIN_CANNOT_LEAVE.getText());
        }
        currentProject.removeMember(this);
    }

    public boolean hasProject() {
        return this.currentProject != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        return username.equals(((User) o).username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}

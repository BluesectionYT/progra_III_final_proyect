package co.edu.uptc.persistence;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.enums.RequestStatus;
import co.edu.uptc.interfaces.IProjectManager;
import co.edu.uptc.model.Category;
import co.edu.uptc.model.ExpenseRequest;
import co.edu.uptc.model.Project;
import co.edu.uptc.model.User;
import co.edu.uptc.persistence.StateSnapshot.CategoryData;
import co.edu.uptc.persistence.StateSnapshot.ProjectData;
import co.edu.uptc.persistence.StateSnapshot.RequestData;
import co.edu.uptc.persistence.StateSnapshot.UserData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StateMapper {
    public StateSnapshot toSnapshot(IProjectManager manager) {
        StateSnapshot snapshot = new StateSnapshot();
        for (User user : manager.getUsers()) {
            snapshot.users.add(toUserData(user));
        }
        for (Project project : manager.getProjects()) {
            if (project.isActive()) {
                snapshot.projects.add(toProjectData(project));
            }
        }
        return snapshot;
    }

    private UserData toUserData(User user) {
        UserData data = new UserData();
        data.username = user.getUsername();
        data.passwordHash = user.getPasswordHash();
        for (User friend : user.getWorkCircle()) {
            data.workCircle.add(friend.getUsername());
        }
        for (Project project : user.getPendingInvitations()) {
            data.pendingInvitations.add(project.getName());
        }
        return data;
    }

    private ProjectData toProjectData(Project project) {
        ProjectData data = new ProjectData();
        data.name = project.getName();
        data.totalBudget = project.getTotalBudget();
        data.admin = project.getAdmin().getUsername();
        project.getMembers().forEach(member -> data.members.add(member.getUsername()));
        project.getCategories().forEach(category -> data.categories.add(toCategoryData(category)));
        project.getRequests().forEach(request -> data.requests.add(toRequestData(request)));
        return data;
    }

    private CategoryData toCategoryData(Category category) {
        CategoryData data = new CategoryData();
        data.name = category.getName();
        data.budget = category.getBudget();
        return data;
    }

    private RequestData toRequestData(ExpenseRequest request) {
        RequestData data = new RequestData();
        data.id = request.getId();
        data.concept = request.getConcept();
        data.amount = request.getAmount();
        data.category = request.getCategory().getName();
        data.requester = request.getRequester().getUsername();
        data.status = request.getStatus();
        data.reason = request.getReason();
        data.createdAt = request.getCreatedAt().toString();
        data.resolvedAt = request.getResolvedAt() == null ? null : request.getResolvedAt().toString();
        return data;
    }

    public void restore(StateSnapshot snapshot, IProjectManager manager) {
        requireEmpty(manager);
        for (UserData data : safe(snapshot.users)) {
            manager.registerUser(data.username, data.passwordHash);
        }
        restoreWorkCircles(snapshot, manager);
        for (ProjectData data : safe(snapshot.projects)) {
            manager.addProject(buildProject(data, manager));
        }
        restoreInvitations(snapshot, manager);
    }

    private void requireEmpty(IProjectManager manager) {
        if (!manager.getUsers().isEmpty() || !manager.getProjects().isEmpty()) {
            throw new IllegalStateException(ErrorMessages.MANAGER_NOT_EMPTY.getText());
        }
    }

    private void restoreWorkCircles(StateSnapshot snapshot, IProjectManager manager) {
        for (UserData data : safe(snapshot.users)) {
            User user = requireUser(manager, data.username);
            for (String friendName : safe(data.workCircle)) {
                user.addToWorkCircle(requireUser(manager, friendName));
            }
        }
    }

    private void restoreInvitations(StateSnapshot snapshot, IProjectManager manager) {
        for (UserData data : safe(snapshot.users)) {
            User user = requireUser(manager, data.username);
            for (String projectName : safe(data.pendingInvitations)) {
                user.receiveInvitation(requireProject(manager, projectName));
            }
        }
    }

    private Project buildProject(ProjectData data, IProjectManager manager) {
        User admin = requireUser(manager, data.admin);
        if (admin.hasProject()) {
            throw new IllegalStateException(ErrorMessages.ALREADY_IN_OTHER_PROJECT.format(admin.getUsername()));
        }
        Project project = new Project(data.name, data.totalBudget, buildCategories(data), admin);
        restoreMembers(project, data, manager);
        restoreRequests(project, data, manager);
        return project;
    }

    private ArrayList<Category> buildCategories(ProjectData data) {
        ArrayList<Category> categories = new ArrayList<>();
        for (CategoryData categoryData : safe(data.categories)) {
            categories.add(new Category(categoryData.name, categoryData.budget));
        }
        return categories;
    }

    private void restoreMembers(Project project, ProjectData data, IProjectManager manager) {
        for (String memberName : safe(data.members)) {
            User member = requireUser(manager, memberName);
            member.receiveInvitation(project);
            member.acceptInvitation(project);
        }
    }

    private void restoreRequests(Project project, ProjectData data, IProjectManager manager) {
        List<RequestData> sorted = new ArrayList<>(safe(data.requests));
        sorted.sort(Comparator.comparingInt(request -> request.id));
        double approvedTotal = 0;
        for (int i = 0; i < sorted.size(); i++) {
            approvedTotal += restoreRequest(project, sorted.get(i), i, manager);
        }
        if (approvedTotal > project.getTotalBudget()) {
            throw new IllegalStateException(ErrorMessages.APPROVED_EXCEEDS_BUDGET.format(project.getName()));
        }
        ModelAccess.restoreBalance(project, project.getTotalBudget() - approvedTotal);
    }

    private double restoreRequest(Project project, RequestData data, int index, IProjectManager manager) {
        validateRequestData(project, data, index);
        Category category = findCategory(project, data.category);
        User requester = requireUser(manager, data.requester);
        ExpenseRequest request = new ExpenseRequest(data.concept, data.amount, category, requester);
        ModelAccess.restoreRequest(request, data.id, data.status, data.reason,
                LocalDateTime.parse(data.createdAt), parseDate(data.resolvedAt));
        project.getRequests().add(request);
        return applyStatus(project, request, index);
    }

    private void validateRequestData(Project project, RequestData data, int index) {
        if (data.id != index) {
            throw new IllegalStateException(ErrorMessages.REQUEST_IDS_WITH_GAPS.format(project.getName()));
        }
        if (data.status == null || data.createdAt == null) {
            throw new IllegalStateException(ErrorMessages.REQUEST_WITHOUT_STATE.format(index));
        }
        if (data.amount <= 0) {
            throw new IllegalStateException(ErrorMessages.REQUEST_INVALID_AMOUNT.format(index));
        }
    }

    private double applyStatus(Project project, ExpenseRequest request, int index) {
        if (request.getStatus() == RequestStatus.PENDING) {
            project.getPendingQueue().push(request);
            return 0;
        }
        if (request.getResolvedAt() == null || request.getReason() == null || request.getReason().isBlank()) {
            throw new IllegalStateException(ErrorMessages.RESOLVED_WITHOUT_DATA.format(index));
        }
        if (request.getStatus() != RequestStatus.APPROVED) {
            return 0;
        }
        request.getCategory().addSpent(request.getAmount());
        return request.getAmount();
    }

    private LocalDateTime parseDate(String text) {
        return text == null ? null : LocalDateTime.parse(text);
    }

    private User requireUser(IProjectManager manager, String username) {
        User user = username == null ? null : manager.findUser(username);
        if (user == null) {
            throw new IllegalStateException(ErrorMessages.UNKNOWN_USER.format(username));
        }
        return user;
    }

    private Project requireProject(IProjectManager manager, String projectName) {
        Project project = manager.findProject(projectName);
        if (project == null) {
            throw new IllegalStateException(ErrorMessages.INVITATION_TO_MISSING_PROJECT.format(projectName));
        }
        return project;
    }

    private Category findCategory(Project project, String name) {
        for (Category category : project.getCategories()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        throw new IllegalStateException(ErrorMessages.UNKNOWN_CATEGORY.format(project.getName(), name));
    }

    private <T> List<T> safe(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }
}

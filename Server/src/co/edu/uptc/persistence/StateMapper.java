package co.edu.uptc.persistence;

import co.edu.uptc.interfaces.IProjectManager;
import co.edu.uptc.model.Category;
import co.edu.uptc.model.ExpenseRequest;
import co.edu.uptc.model.Project;
import co.edu.uptc.enums.RequestStatus;
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
            UserData data = new UserData();
            data.username = user.getUsername();
            data.passwordHash = user.getPasswordHash();
            for (User friend : user.getWorkCircle()) {
                data.workCircle.add(friend.getUsername());
            }
            for (Project project : user.getPendingInvitations()) {
                data.pendingInvitations.add(project.getName());
            }
            snapshot.users.add(data);
        }
        for (Project project : manager.getProjects()) {
            if (project.isActive()) {
                snapshot.projects.add(toProjectData(project));
            }
        }
        return snapshot;
    }

    private ProjectData toProjectData(Project project) {
        ProjectData data = new ProjectData();
        data.name = project.getName();
        data.totalBudget = project.getTotalBudget();
        data.admin = project.getAdmin().getUsername();
        for (User member : project.getMembers()) {
            data.members.add(member.getUsername());
        }
        for (Category category : project.getCategories()) {
            CategoryData categoryData = new CategoryData();
            categoryData.name = category.getName();
            categoryData.budget = category.getBudget();
            data.categories.add(categoryData);
        }
        for (ExpenseRequest request : project.getRequests()) {
            RequestData requestData = new RequestData();
            requestData.id = request.getId();
            requestData.concept = request.getConcept();
            requestData.amount = request.getAmount();
            requestData.category = request.getCategory().getName();
            requestData.requester = request.getRequester().getUsername();
            requestData.status = request.getStatus();
            requestData.reason = request.getReason();
            requestData.createdAt = request.getCreatedAt().toString();
            requestData.resolvedAt = request.getResolvedAt() == null ? null : request.getResolvedAt().toString();
            data.requests.add(requestData);
        }
        return data;
    }

    public void restore(StateSnapshot snapshot, IProjectManager manager) {
        if (!manager.getUsers().isEmpty() || !manager.getProjects().isEmpty()) {
            throw new IllegalStateException("El manager debe estar vacío para cargar el estado");
        }
        for (UserData data : safe(snapshot.users)) {
            manager.registerUser(data.username, data.passwordHash);
        }
        for (UserData data : safe(snapshot.users)) {
            User user = requireUser(manager, data.username);
            for (String friendName : safe(data.workCircle)) {
                user.addToWorkCircle(requireUser(manager, friendName));
            }
        }
        for (ProjectData data : safe(snapshot.projects)) {
            manager.addProject(buildProject(data, manager));
        }
        for (UserData data : safe(snapshot.users)) {
            User user = requireUser(manager, data.username);
            for (String projectName : safe(data.pendingInvitations)) {
                Project project = manager.findProject(projectName);
                if (project == null) {
                    throw new IllegalStateException("Invitación a un proyecto inexistente: " + projectName);
                }
                user.receiveInvitation(project);
            }
        }
    }

    private Project buildProject(ProjectData data, IProjectManager manager) {
        User admin = requireUser(manager, data.admin);
        if (admin.hasProject()) {
            throw new IllegalStateException(admin.getUsername() + " ya pertenece a otro proyecto");
        }
        ArrayList<Category> categories = new ArrayList<>();
        for (CategoryData categoryData : safe(data.categories)) {
            categories.add(new Category(categoryData.name, categoryData.budget));
        }
        Project project = new Project(data.name, data.totalBudget, categories, admin);
        for (String memberName : safe(data.members)) {
            User member = requireUser(manager, memberName);
            member.receiveInvitation(project);
            member.acceptInvitation(project);
        }
        restoreRequests(project, data, manager);
        return project;
    }

    private void restoreRequests(Project project, ProjectData data, IProjectManager manager) {
        List<RequestData> sorted = new ArrayList<>(safe(data.requests));
        sorted.sort(Comparator.comparingInt(request -> request.id));
        double approvedTotal = 0;
        for (int i = 0; i < sorted.size(); i++) {
            RequestData requestData = sorted.get(i);
            if (requestData.id != i) {
                throw new IllegalStateException("Los id de solicitudes de " + project.getName() + " deben ser 0, 1, 2... sin huecos");
            }
            if (requestData.status == null || requestData.createdAt == null) {
                throw new IllegalStateException("Solicitud " + i + " sin estado o sin fecha de creación");
            }
            if (requestData.amount <= 0) {
                throw new IllegalStateException("Solicitud " + i + " con monto inválido");
            }
            Category category = findCategory(project, requestData.category);
            User requester = requireUser(manager, requestData.requester);
            ExpenseRequest request = new ExpenseRequest(requestData.concept, requestData.amount, category, requester);
            LocalDateTime resolvedAt = requestData.resolvedAt == null ? null : LocalDateTime.parse(requestData.resolvedAt);
            ModelAccess.restoreRequest(request, requestData.id, requestData.status, requestData.reason,
                    LocalDateTime.parse(requestData.createdAt), resolvedAt);
            project.getRequests().add(request);
            if (requestData.status == RequestStatus.PENDING) {
                project.getPendingQueue().push(request);
            } else {
                if (resolvedAt == null || requestData.reason == null || requestData.reason.isBlank()) {
                    throw new IllegalStateException("Solicitud " + i + " resuelta sin motivo o sin fecha de resolución");
                }
                if (requestData.status == RequestStatus.APPROVED) {
                    category.addSpent(request.getAmount());
                    approvedTotal += request.getAmount();
                }
            }
        }
        if (approvedTotal > project.getTotalBudget()) {
            throw new IllegalStateException("Lo aprobado supera el presupuesto total de " + project.getName());
        }
        ModelAccess.restoreBalance(project, project.getTotalBudget() - approvedTotal);
    }

    private User requireUser(IProjectManager manager, String username) {
        User user = username == null ? null : manager.findUser(username);
        if (user == null) {
            throw new IllegalStateException("Usuario inexistente: " + username);
        }
        return user;
    }

    private Category findCategory(Project project, String name) {
        for (Category category : project.getCategories()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        throw new IllegalStateException("Categoría inexistente en " + project.getName() + ": " + name);
    }

    private <T> List<T> safe(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }
}

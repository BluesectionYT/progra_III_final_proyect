package co.edu.uptc.controller;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.enums.MessageCategory;
import co.edu.uptc.enums.RequestStatus;
import co.edu.uptc.enums.SortCriteria;
import co.edu.uptc.interfaces.IPasswordHasher;
import co.edu.uptc.interfaces.IProjectManager;
import co.edu.uptc.interfaces.Observer;
import co.edu.uptc.model.Category;
import co.edu.uptc.model.ExpenseRequest;
import co.edu.uptc.model.Project;
import co.edu.uptc.model.User;
import co.edu.uptc.net.JSONReader;
import co.edu.uptc.net.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Supplier;

public class ClientThread extends Thread implements Observer {
    private IProjectManager projectManager;
    private Socket socket;
    private DataOutputStream sender;
    private ListenerThread listener;
    private Controller controller;
    private User user;
    private JSONReader jsonReader;
    private IPasswordHasher hasher;

    public ClientThread(Socket socket, IProjectManager projectManager, Controller controller) {
        this.socket = socket;
        this.projectManager = projectManager;
        this.controller = controller;
        this.hasher = controller.getHasher();
    }

    @Override
    public void run() {
        try {
            this.sender = new DataOutputStream(socket.getOutputStream());
            this.listener = new ListenerThread(this, socket);
            this.jsonReader = new JSONReader();
            listener.start();
            System.out.println("Cliente conectado");
        } catch (IOException e) {
            closeConnection();
        }
    }

    public void processData(String data) {
        Message message = jsonReader.jsonToMessage(data);
        synchronized (projectManager) {
            sessionCommands(message);
            workCircleCommands(message);
            projectCommands(message);
            historyCommands(message);
            requestsCommands(message);
            approveCommands(message);
            teamCommands(message);
            remainingCommands(message);
        }
    }

    private void sessionCommands(Message message) {
        switch (message.getCategory()) {
            case REGISTER:
                register(message);
                break;
            case LOGIN:
                login(message);
                break;
            case LOGOUT:
                logout();
                break;
        }
    }

    private void workCircleCommands(Message message) {
        switch (message.getCategory()) {
            case GET_CIRCLE:
                getCircle();
                break;
            case ADD_TO_CIRCLE:
                addToCircle(message);
                break;
            case GET_INVITATIONS:
                getInvitations();
                break;
            case ACCEPT_INVITATION:
                acceptInvitation(message);
                break;
            case REJECT_INVITATION:
                rejectInvitation(message);
                break;
        }
    }

    private void projectCommands(Message message) {
        switch (message.getCategory()) {
            case CREATE_PROJECT:
                createProject(message);
                break;
            case GET_PROJECT_INFO:
                getProjectInfo();
                break;
        }
    }

    private void historyCommands(Message message) {
        switch (message.getCategory()) {
            case GET_SUMMARY:
                getSummary();
                break;
            case GET_HISTORY:
                getHistory(message);
                break;
        }
    }

    private void requestsCommands(Message message) {
        switch (message.getCategory()) {
            case CREATE_REQUEST:
                createRequest(message);
                break;
            case GET_MY_REQUESTS:
                getMyRequests();
                break;
        }
    }

    private void approveCommands(Message message) {
        switch (message.getCategory()) {
            case GET_PENDING:
                getPending();
                break;
            case APPROVE_REQUEST:
                resolveRequest(message, true);
                break;
            case REJECT_REQUEST:
                resolveRequest(message, false);
                break;
        }
    }

    private void teamCommands(Message message) {
        switch (message.getCategory()) {
            case GET_TEAM:
                getTeam();
                break;
            case INVITE_MEMBER:
                inviteMember(message);
                break;
            case REMOVE_MEMBER:
                removeMember(message);
                break;
            case TRANSFER_ADMIN:
                transferAdmin(message);
                break;
            case CLOSE_PROJECT:
                closeProject(message);
                break;
        }
    }

    private void remainingCommands(Message message) {
        switch (message.getCategory()) {
            case LEAVE_PROJECT:
                leaveProject();
                break;
            case NOTIFY:
                break;
            case EXIT:
                exit();
                break;
        }
    }

    private void execute(MessageCategory category, Supplier<Object> action) {
        try {
            sendResponse(true, null, category, action.get());
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        } catch (UnsupportedOperationException e) {
            sendResponse(false, ErrorMessages.INVALID_REQUEST.getText(), category, null);
        }
    }


    private void register(Message message) {
        execute(MessageCategory.REGISTER, () -> {
            BodyReader body = BodyReader.parse(message.getText());
            String username = body.readField("username");
            String password = body.readField("password");
            if (BodyReader.isBlank(username) || BodyReader.isBlank(password)) {
                throw new IllegalArgumentException(ErrorMessages.CREDENTIALS_REQUIRED.getText());
            }
            projectManager.registerUser(username.trim(), hasher.hash(password));
            return null;
        });
    }

    private void login(Message message) {
        execute(MessageCategory.LOGIN, () -> {
            BodyReader body = BodyReader.parse(message.getText());
            String username = body.readField("username");
            String password = body.readField("password");
            if (user != null) {
                throw new IllegalStateException(ErrorMessages.SESSION_ALREADY_STARTED.getText());
            }
            User found = authenticate(username, password);
            startSession(found);
            return ResponseBuilder.sessionData(found);
        });
    }

    private User authenticate(String username, String password) {
        User found = projectManager.findUser(username);
        if (found == null || password == null || !hasher.verify(password, found.getPasswordHash())) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_CREDENTIALS.getText());
        }
        return found;
    }

    private void startSession(User found) {
        synchronized (controller) {
            if (controller.isLoggedIn(found.getUsername())) {
                throw new IllegalStateException(ErrorMessages.SESSION_ACTIVE_ELSEWHERE.getText());
            }
            setUser(found);
        }
    }

    private void logout() {
        execute(MessageCategory.LOGOUT, () -> {
            setUser(null);
            return null;
        });
    }

    private void requireLogin() {
        if (user == null) {
            throw new IllegalStateException(ErrorMessages.LOGIN_REQUIRED.getText());
        }
    }

    private Project requireProject() {
        requireLogin();
        Project project = user.getCurrentProject();
        if (project == null || !project.isActive()) {
            throw new IllegalStateException(ErrorMessages.NO_PROJECT.getText());
        }
        return project;
    }

    private Project requireAdminProject() {
        Project project = requireProject();
        if (!user.equals(project.getAdmin())) {
            throw new IllegalStateException(ErrorMessages.ADMIN_ONLY.getText());
        }
        return project;
    }

    private void checkPassword(String password) {
        if (password == null || !hasher.verify(password, user.getPasswordHash())) {
            throw new IllegalArgumentException(ErrorMessages.WRONG_PASSWORD.getText());
        }
    }

    private User findUserOrFail(String username) {
        User found = (username == null) ? null : projectManager.findUser(username.trim());
        if (found == null) {
            throw new IllegalArgumentException(ErrorMessages.USER_NOT_FOUND.getText());
        }
        return found;
    }

    private Project findProjectOrFail(String projectName) {
        Project project = projectManager.findProject(projectName);
        if (project == null) {
            throw new IllegalArgumentException(ErrorMessages.PROJECT_NOT_FOUND.getText());
        }
        return project;
    }

    private Category findCategory(Project project, String name) {
        for (Category category : project.getCategories()) {
            if (category.getName().equalsIgnoreCase(name.trim())) {
                return category;
            }
        }
        return null;
    }

    private Category requireCategory(Project project, String name) {
        Category category = findCategory(project, name);
        if (category == null) {
            throw new IllegalArgumentException(ErrorMessages.CATEGORY_NOT_IN_PROJECT.getText());
        }
        return category;
    }


    private void getCircle() {
        execute(MessageCategory.GET_CIRCLE, () -> {
            requireLogin();
            return ResponseBuilder.circleNames(user);
        });
    }

    private void addToCircle(Message message) {
        execute(MessageCategory.ADD_TO_CIRCLE, () -> {
            requireLogin();
            BodyReader body = BodyReader.parse(message.getText());
            user.addToWorkCircle(findUserOrFail(body.readField("username")));
            return ResponseBuilder.circleNames(user);
        });
    }

    private void getInvitations() {
        execute(MessageCategory.GET_INVITATIONS, () -> {
            requireLogin();
            return ResponseBuilder.invitationNames(user);
        });
    }

    private void acceptInvitation(Message message) {
        execute(MessageCategory.ACCEPT_INVITATION, () -> {
            requireLogin();
            BodyReader body = BodyReader.parse(message.getText());
            Project project = findProjectOrFail(body.readField("projectName"));
            user.acceptInvitation(project);
            return ResponseBuilder.membershipData(project.getName(), user);
        });
    }

    private void rejectInvitation(Message message) {
        execute(MessageCategory.REJECT_INVITATION, () -> {
            requireLogin();
            BodyReader body = BodyReader.parse(message.getText());
            user.rejectInvitation(findProjectOrFail(body.readField("projectName")));
            return ResponseBuilder.invitationNames(user);
        });
    }


    private void createProject(Message message) {
        execute(MessageCategory.CREATE_PROJECT, () -> {
            requireLogin();
            return buildProject(BodyReader.parse(message.getText()));
        });
    }

    private Object buildProject(BodyReader body) {
        String name = body.readField("name");
        double budget = body.readAmount("budget");
        validateProjectData(name, budget);
        ArrayList<Category> categories = body.readCategories(budget);
        checkPassword(body.readField("password"));
        return openProject(name.trim(), budget, categories);
    }

    private void validateProjectData(String name, double budget) {
        if (BodyReader.isBlank(name)) {
            throw new IllegalArgumentException(ErrorMessages.PROJECT_NAME_REQUIRED.getText());
        }
        if (BodyReader.isInvalidAmount(budget)) {
            throw new IllegalArgumentException(ErrorMessages.BUDGET_GREATER_THAN_ZERO.getText());
        }
    }

    private Object openProject(String name, double budget, ArrayList<Category> categories) {
        if (user.hasProject()) {
            throw new IllegalStateException(ErrorMessages.ALREADY_HAS_PROJECT.getText());
        }
        if (projectManager.getProjects().stream().anyMatch(p -> p.getName().equalsIgnoreCase(name))) {
            throw new IllegalArgumentException(ErrorMessages.PROJECT_NAME_TAKEN.getText());
        }
        projectManager.addProject(new Project(name, budget, categories, user));
        return ResponseBuilder.membershipData(name, user);
    }

    private void getProjectInfo() {
        execute(MessageCategory.GET_PROJECT_INFO, () -> ResponseBuilder.projectInfo(requireProject(), user));
    }


    private void getSummary() {
        execute(MessageCategory.GET_SUMMARY, () -> ResponseBuilder.summary(requireProject()));
    }

    private void getHistory(Message message) {
        execute(MessageCategory.GET_HISTORY, () -> {
            Project project = requireProject();
            return history(project, BodyReader.parse(message.getText()));
        });
    }

    private Object history(Project project, BodyReader body) {
        RequestStatus status = body.readEnum("status", RequestStatus.class, ErrorMessages.INVALID_STATUS);
        SortCriteria criteria = body.readEnum("order", SortCriteria.class, ErrorMessages.INVALID_ORDER);
        LocalDate from = body.readDate("from");
        LocalDate to = body.readDate("to");
        Category category = findFilterCategory(project, body.readField("category"));
        ArrayList<ExpenseRequest> filtered = project.filterHistory(category, status, from, to);
        SortCriteria order = criteria == null ? SortCriteria.CREATED_DATE : criteria;
        return ResponseBuilder.requestsToList(project.sortHistory(filtered, order));
    }

    private Category findFilterCategory(Project project, String categoryName) {
        return BodyReader.isBlank(categoryName) ? null : requireCategory(project, categoryName);
    }


    private void getMyRequests() {
        execute(MessageCategory.GET_MY_REQUESTS, () -> {
            Project project = requireProject();
            return ResponseBuilder.requestsToList(project.getRequestsOf(user));
        });
    }

    private void createRequest(Message message) {
        execute(MessageCategory.CREATE_REQUEST, () -> {
            Project project = requireProject();
            BodyReader body = BodyReader.parse(message.getText());
            project.submitRequest(buildRequest(project, body));
            return ResponseBuilder.requestsToList(project.getRequestsOf(user));
        });
    }

    private ExpenseRequest buildRequest(Project project, BodyReader body) {
        String concept = body.readField("concept");
        double amount = body.readAmount("amount");
        String categoryName = body.readField("category");
        if (BodyReader.isBlank(concept)) {
            throw new IllegalArgumentException(ErrorMessages.CONCEPT_REQUIRED.getText());
        }
        if (BodyReader.isInvalidAmount(amount)) {
            throw new IllegalArgumentException(ErrorMessages.AMOUNT_GREATER_THAN_ZERO.getText());
        }
        if (BodyReader.isBlank(categoryName)) {
            throw new IllegalArgumentException(ErrorMessages.SELECT_CATEGORY.getText());
        }
        return new ExpenseRequest(concept.trim(), amount, requireCategory(project, categoryName), user);
    }

    private void getPending() {
        execute(MessageCategory.GET_PENDING, () -> ResponseBuilder.pendingData(requireAdminProject()));
    }

    private void resolveRequest(Message message, boolean approve) {
        MessageCategory category = approve ? MessageCategory.APPROVE_REQUEST : MessageCategory.REJECT_REQUEST;
        execute(category, () -> {
            Project project = requireAdminProject();
            BodyReader body = BodyReader.parse(message.getText());
            ExpenseRequest request = findRequest(project, readRequestId(body));
            applyDecision(project, request, readReason(body), approve);
            return ResponseBuilder.pendingData(project);
        });
    }

    private int readRequestId(BodyReader body) {
        double rawId = body.readAmount("id");
        if (Double.isNaN(rawId) || rawId < 0 || rawId != Math.rint(rawId)) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_REQUEST.getText());
        }
        return (int) rawId;
    }

    private String readReason(BodyReader body) {
        String reason = body.readField("reason");
        if (BodyReader.isBlank(reason)) {
            throw new IllegalArgumentException(ErrorMessages.REASON_REQUIRED.getText());
        }
        return reason.trim();
    }

    private ExpenseRequest findRequest(Project project, int id) {
        if (id >= project.getRequests().size()) {
            throw new IllegalArgumentException(ErrorMessages.REQUEST_NOT_FOUND.getText());
        }
        return project.getRequests().get(id);
    }

    private void applyDecision(Project project, ExpenseRequest request, String reason, boolean approve) {
        if (approve) {
            project.approve(request, reason);
        } else {
            project.reject(request, reason);
        }
    }

    private void getTeam() {
        execute(MessageCategory.GET_TEAM, () -> ResponseBuilder.teamData(requireAdminProject(), user));
    }

    private void inviteMember(Message message) {
        execute(MessageCategory.INVITE_MEMBER, () -> {
            Project project = requireAdminProject();
            BodyReader body = BodyReader.parse(message.getText());
            project.inviteMember(findUserOrFail(body.readField("username")));
            return ResponseBuilder.teamData(project, user);
        });
    }

    private void removeMember(Message message) {
        execute(MessageCategory.REMOVE_MEMBER, () -> {
            Project project = requireAdminProject();
            BodyReader body = BodyReader.parse(message.getText());
            String username = body.readField("username");
            checkPassword(body.readField("password"));
            project.removeMember(findUserOrFail(username));
            return ResponseBuilder.teamData(project, user);
        });
    }

    private void transferAdmin(Message message) {
        execute(MessageCategory.TRANSFER_ADMIN, () -> {
            Project project = requireAdminProject();
            BodyReader body = BodyReader.parse(message.getText());
            String username = body.readField("username");
            checkPassword(body.readField("password"));
            project.transferAdmin(findUserOrFail(username));
            return ResponseBuilder.membershipData(project.getName(), user);
        });
    }

    private void closeProject(Message message) {
        execute(MessageCategory.CLOSE_PROJECT, () -> {
            Project project = requireAdminProject();
            BodyReader body = BodyReader.parse(message.getText());
            checkPassword(body.readField("password"));
            projectManager.closeProject(project);
            return null;
        });
    }

    private void leaveProject() {
        execute(MessageCategory.LEAVE_PROJECT, () -> {
            requireProject();
            user.leaveProject();
            return null;
        });
    }


    private void exit() {
        controller.saveData();
        closeConnection();
    }

    public void closeConnection() {
        controller.detach(this);
        if (listener != null) {
            listener.interrupt();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(MessageCategory category, String data) {
        sendMessage(new Message(category, data));
    }

    public synchronized void sendMessage(Message message) {
        String messageAsString = jsonReader.messageToJson(message);
        if (messageAsString != null) {
            try {
                sender.writeUTF(messageAsString);
            } catch (IOException e) {
                closeConnection();
            }
        }
    }

    public synchronized void sendResponse(boolean ok, String errorMsg, MessageCategory category, Object extraData) {
        sendMessage(new Message(category, ResponseBuilder.toJson(ok, errorMsg, extraData)));
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

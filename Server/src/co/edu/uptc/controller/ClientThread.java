package co.edu.uptc.controller;

import co.edu.uptc.interfaces.IPasswordHasher;
import co.edu.uptc.interfaces.IProjectManager;
import co.edu.uptc.model.User;
import co.edu.uptc.model.Project;
import co.edu.uptc.model.Category;
import co.edu.uptc.model.ExpenseRequest;
import co.edu.uptc.model.RequestStatus;
import co.edu.uptc.model.SortCriteria;
import co.edu.uptc.net.JSONReader;
import co.edu.uptc.net.Message;
import co.edu.uptc.net.MessageCategory;
import co.edu.uptc.net.Observer;
import co.edu.uptc.interfaces.IPasswordHasher;
import co.edu.uptc.model.Category;
import co.edu.uptc.model.Project;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import co.edu.uptc.net.PasswordHasher;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.google.gson.Gson;

public class ClientThread extends Thread implements Observer {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    public void sendMessage(Message message) {
        String messageAsString = jsonReader.messageToJson(message);
        if (messageAsString != null) {
            try {
                sender.writeUTF(messageAsString);
            } catch (IOException e) {
                closeConnection();
            }
        }
    }

    public void processData(String data) {
        Message message = jsonReader.jsonToMessage(data);
        sessionCommands(message);
        workCircleCommands(message);
        projectCommands(message);
        historyCommands(message);
        requestsCommands(message);
        approveCommands(message);
        teamCommands(message);
        remainingCommands(message);
    }

    private void sessionCommands(Message message) {
        MessageCategory category = message.getCategory();
        if (category == MessageCategory.REGISTER || category == MessageCategory.LOGIN || category == MessageCategory.LOGOUT) {
            switch (category) {
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
    }

    private void register(Message message) {
        try {
            JsonObject body = JsonParser.parseString(message.getText()).getAsJsonObject();
            String username = readField(body, "username");
            String passwordHash = readField(body, "passwordHash");
            if (username == null || username.isBlank() || passwordHash == null || passwordHash.isBlank()) {
                sendResponse(false, "Usuario y contraseña son obligatorios", MessageCategory.REGISTER, null);
                return;
            }
            synchronized (projectManager) {
                projectManager.registerUser(username.trim(), passwordHash);
            }
            sendResponse(true, null, MessageCategory.REGISTER, null);
        } catch (IllegalArgumentException e) {
            sendResponse(false, e.getMessage(), MessageCategory.REGISTER, null);
        } catch (JsonParseException | IllegalStateException e) {
            sendResponse(false, "Solicitud inválida", MessageCategory.REGISTER, null);
        }
    }

    private void login(Message message) {
        try {
            JsonObject body = JsonParser.parseString(message.getText()).getAsJsonObject();
            String username = readField(body, "username");
            String password = readField(body, "password");
            if (user != null) {
                sendResponse(false, "Ya hay una sesión iniciada", MessageCategory.LOGIN, null);
                return;
            }
            User found;
            synchronized (projectManager) {
                found = projectManager.findUser(username);
            }
            if (found == null || password == null || !passwordHasher.verify(password, found.getPasswordHash())) {
                sendResponse(false, "Usuario o contraseña incorrectos", MessageCategory.LOGIN, null);
                return;
            }
            synchronized (controller) {
                if (controller.isLoggedIn(found.getUsername())) {
                    sendResponse(false, "Este usuario ya tiene una sesión activa", MessageCategory.LOGIN, null);
                    return;
                }
                setUser(found);
            }
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("username", found.getUsername());
            if (found.hasProject()) {
                data.put("projectName", found.getCurrentProject().getName());
                data.put("role", found.getRole().name());
            }
            sendResponse(true, null, MessageCategory.LOGIN, data);
        } catch (JsonParseException | IllegalStateException e) {
            sendResponse(false, "Solicitud inválida", MessageCategory.LOGIN, null);
        }
    }

    private void logout() {
        setUser(null);
        sendResponse(true, null, MessageCategory.LOGOUT, null);
    }

    private String readField(JsonObject body, String key) {
        JsonElement element = body.get(key);
        return (element == null || element.isJsonNull()) ? null : element.getAsString();
    }

    private boolean requireLogin(MessageCategory category) {
        if (user == null) {
            sendResponse(false, "Debes iniciar sesión", category, null);
            return false;
        }
        return true;
    }

    private JsonObject readBody(Message message) {
        try {
            return JsonParser.parseString(message.getText()).getAsJsonObject();
        } catch (JsonParseException | IllegalStateException | NullPointerException e) {
            sendResponse(false, "Solicitud inválida", message.getCategory(), null);
            return null;
        }
    }

    private double readAmount(JsonObject body, String key) {
        try {
            JsonElement element = body.get(key);
            return (element == null || element.isJsonNull()) ? Double.NaN : element.getAsDouble();
        } catch (NumberFormatException | UnsupportedOperationException | IllegalStateException e) {
            return Double.NaN;
        }
    }

    private Project requireProject(MessageCategory category) {
        if (!requireLogin(category)) return null;
        Project project = user.getCurrentProject();
        if (project == null || !project.isActive()) {
            sendResponse(false, "No perteneces a ningún proyecto", category, null);
            return null;
        }
        return project;
    }

    private void checkPassword(String password) {
        if (password == null || !passwordHasher.verify(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }
    }

    private User findUserOrFail(String username) {
        User found = (username == null) ? null : projectManager.findUser(username.trim());
        if (found == null) {
            throw new IllegalArgumentException("El usuario no existe");
        }
        return found;
    }

    private Project requireAdminProject(MessageCategory category) {
        Project project = requireProject(category);
        if (project != null && !user.equals(project.getAdmin())) {
            sendResponse(false, "Solo el administrador puede realizar esta acción", category, null);
            return null;
        }
        return project;
    }

    private Category findCategory(Project project, String name) {
        for (Category c : project.getCategories()) {
            if (c.getName().equalsIgnoreCase(name.trim())) {
                return c;
            }
        }
        return null;
    }

    private <E extends Enum<E>> E readEnum(JsonObject body, String key, Class<E> type, String error) {
        String value = readField(body, key);
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(type, value.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(error);
        }
    }

    private LocalDate readDate(JsonObject body, String key) {
        String value = readField(body, key);
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha inválida: usa el formato AAAA-MM-DD");
        }
    }

    private List<Map<String, Object>> requestsToList(List<ExpenseRequest> requests) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ExpenseRequest request : requests) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", request.getId());
            item.put("createdAt", request.getCreatedAt().format(DATE_TIME));
            item.put("requester", request.getRequester().getUsername());
            item.put("concept", request.getConcept());
            item.put("category", request.getCategory().getName());
            item.put("amount", request.getAmount());
            item.put("status", request.getStatus().name());
            if (request.getReason() != null) {
                item.put("reason", request.getReason());
            }
            list.add(item);
        }
        return list;
    }

    private boolean isInvalidAmount(double amount) {
        return Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0;
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

    private void getCircle() {
        if (!requireLogin(MessageCategory.GET_CIRCLE)) return;
        List<String> circle;
        synchronized (projectManager) {
            circle = circleNames();
        }
        sendResponse(true, null, MessageCategory.GET_CIRCLE, circle);
    }

    private void addToCircle(Message message) {
        MessageCategory category = MessageCategory.ADD_TO_CIRCLE;
        if (!requireLogin(category)) return;
        JsonObject body = readBody(message);
        if (body == null) return;
        try {
            String username = readField(body, "username");
            List<String> circle;
            synchronized (projectManager) {
                User target = username == null ? null : projectManager.findUser(username.trim());
                if (target == null) {
                    throw new IllegalArgumentException("El usuario no existe");
                }
                user.addToWorkCircle(target);
                circle = circleNames();
            }
            sendResponse(true, null, category, circle);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        }
    }

    private void getInvitations() {
        if (!requireLogin(MessageCategory.GET_INVITATIONS)) return;
        List<String> invitations;
        synchronized (projectManager) {
            invitations = invitationNames();
        }
        sendResponse(true, null, MessageCategory.GET_INVITATIONS, invitations);
    }

    private void acceptInvitation(Message message) {
        MessageCategory category = MessageCategory.ACCEPT_INVITATION;
        if (!requireLogin(category)) return;
        JsonObject body = readBody(message);
        if (body == null) return;
        try {
            Project project;
            synchronized (projectManager) {
                project = projectManager.findProject(readField(body, "projectName"));
                if (project == null) {
                    throw new IllegalArgumentException("El proyecto ya no existe");
                }
                user.acceptInvitation(project);
            }
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("projectName", project.getName());
            data.put("role", user.getRole().name());
            sendResponse(true, null, category, data);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        }
    }

    private void rejectInvitation(Message message) {
        MessageCategory category = MessageCategory.REJECT_INVITATION;
        if (!requireLogin(category)) return;
        JsonObject body = readBody(message);
        if (body == null) return;
        try {
            List<String> invitations;
            synchronized (projectManager) {
                Project project = projectManager.findProject(readField(body, "projectName"));
                if (project == null) {
                    throw new IllegalArgumentException("El proyecto ya no existe");
                }
                user.rejectInvitation(project);
                invitations = invitationNames();
            }
            sendResponse(true, null, category, invitations);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        }
    }

    private List<String> circleNames() {
        List<String> names = new ArrayList<>();
        for (User member : user.getWorkCircle()) {
            names.add(member.getUsername());
        }
        return names;
    }

    private List<String> invitationNames() {
        List<String> names = new ArrayList<>();
        for (Project project : user.getPendingInvitations()) {
            names.add(project.getName());
        }
        return names;
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

    private void createProject(Message message) {
        MessageCategory category = MessageCategory.CREATE_PROJECT;
        if (!requireLogin(category)) return;
        JsonObject body = readBody(message);
        if (body == null) return;
        try {
            String name = readField(body, "name");
            String password = readField(body, "password");
            double budget = readAmount(body, "budget");
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("El nombre del proyecto es obligatorio");
            }
            if (isInvalidAmount(budget)) {
                throw new IllegalArgumentException("El presupuesto total debe ser mayor que cero");
            }
            ArrayList<Category> categories = parseCategories(body, budget);
            if (password == null || !passwordHasher.verify(password, user.getPasswordHash())) {
                throw new IllegalArgumentException("Contraseña incorrecta");
            }
            String projectName = name.trim();
            synchronized (projectManager) {
                if (user.hasProject()) {
                    throw new IllegalStateException("Ya perteneces a un proyecto");
                }
                boolean exists = projectManager.getProjects().stream()
                        .anyMatch(p -> p.getName().equalsIgnoreCase(projectName));
                if (exists) {
                    throw new IllegalArgumentException("Ya existe un proyecto con ese nombre");
                }
                projectManager.addProject(new Project(projectName, budget, categories, user));
            }
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("projectName", projectName);
            data.put("role", user.getRole().name());
            sendResponse(true, null, category, data);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        } catch (UnsupportedOperationException e) {
            sendResponse(false, "Solicitud inválida", category, null);
        }
    }

    private ArrayList<Category> parseCategories(JsonObject body, double budget) {
        JsonElement raw = body.get("categories");
        if (raw == null || !raw.isJsonArray() || raw.getAsJsonArray().isEmpty()) {
            throw new IllegalArgumentException("Ingresa al menos una categoría");
        }
        ArrayList<Category> categories = new ArrayList<>();
        Set<String> names = new HashSet<>();
        double total = 0;
        for (JsonElement item : raw.getAsJsonArray()) {
            if (!item.isJsonObject()) {
                throw new IllegalArgumentException("Categoría inválida");
            }
            JsonObject entry = item.getAsJsonObject();
            String categoryName = readField(entry, "name");
            double amount = readAmount(entry, "budget");
            if (categoryName == null || categoryName.isBlank() || isInvalidAmount(amount)) {
                throw new IllegalArgumentException("Cada categoría necesita un nombre y un monto mayor que cero");
            }
            categoryName = categoryName.trim();
            if (!names.add(categoryName.toLowerCase())) {
                throw new IllegalArgumentException("La categoría «" + categoryName + "» está repetida");
            }
            categories.add(new Category(categoryName, amount));
            total += amount;
        }
        if (total > budget) {
            throw new IllegalArgumentException("La suma de las categorías supera el presupuesto total");
        }
        return categories;
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

    private void getProjectInfo() {
        MessageCategory category = MessageCategory.GET_PROJECT_INFO;
        Project project = requireProject(category);
        if (project == null) return;
        Map<String, Object> data = new LinkedHashMap<>();
        synchronized (projectManager) {
            List<String> categoryNames = new ArrayList<>();
            for (Category c : project.getCategories()) {
                categoryNames.add(c.getName());
            }
            data.put("name", project.getName());
            data.put("role", user.getRole().name());
            data.put("admin", project.getAdmin().getUsername());
            data.put("totalBudget", project.getTotalBudget());
            data.put("availableBalance", project.getAvailableBalance());
            data.put("categories", categoryNames);
        }
        sendResponse(true, null, category, data);
    }

    private void getSummary() {
        MessageCategory category = MessageCategory.GET_SUMMARY;
        Project project = requireProject(category);
        if (project == null) return;
        Map<String, Object> data = new LinkedHashMap<>();
        synchronized (projectManager) {
            Map<Category, Double> spentByCategory = project.getSpentByCategory();
            Map<User, Double> spentByMember = project.getSpentByMember();
            List<Map<String, Object>> categories = new ArrayList<>();
            for (Category c : project.getCategories()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("name", c.getName());
                row.put("budget", c.getBudget());
                row.put("spent", spentByCategory.getOrDefault(c, 0.0));
                categories.add(row);
            }
            List<Map<String, Object>> members = new ArrayList<>();
            for (User member : project.getMembers()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("name", member.getUsername());
                row.put("spent", spentByMember.getOrDefault(member, 0.0));
                members.add(row);
            }
            data.put("availableBalance", project.getAvailableBalance());
            data.put("totalSpent", project.getTotalSpent());
            data.put("categories", categories);
            data.put("members", members);
        }
        sendResponse(true, null, category, data);
    }

    private void getHistory(Message message) {
        MessageCategory category = MessageCategory.GET_HISTORY;
        Project project = requireProject(category);
        if (project == null) return;
        JsonObject body = readBody(message);
        if (body == null) return;
        try {
            RequestStatus status = readEnum(body, "status", RequestStatus.class, "Estado inválido");
            SortCriteria criteria = readEnum(body, "order", SortCriteria.class, "Criterio de orden inválido");
            LocalDate from = readDate(body, "from");
            LocalDate to = readDate(body, "to");
            String categoryName = readField(body, "category");
            List<Map<String, Object>> requests;
            synchronized (projectManager) {
                Category filterCategory = null;
                if (categoryName != null && !categoryName.isBlank()) {
                    filterCategory = findCategory(project, categoryName);
                    if (filterCategory == null) {
                        throw new IllegalArgumentException("La categoría no existe en este proyecto");
                    }
                }
                ArrayList<ExpenseRequest> filtered = project.filterHistory(filterCategory, status, from, to);
                SortCriteria order = criteria == null ? SortCriteria.CREATED_DATE : criteria;
                requests = requestsToList(project.sortHistory(filtered, order));
            }
            sendResponse(true, null, category, requests);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        } catch (UnsupportedOperationException e) {
            sendResponse(false, "Solicitud inválida", category, null);
        }
    }

    private void getMyRequests() {
        MessageCategory category = MessageCategory.GET_MY_REQUESTS;
        Project project = requireProject(category);
        if (project == null) return;
        List<Map<String, Object>> requests;
        synchronized (projectManager) {
            requests = requestsToList(project.getRequestsOf(user));
        }
        sendResponse(true, null, category, requests);
    }

    private void createRequest(Message message) {
        MessageCategory category = MessageCategory.CREATE_REQUEST;
        Project project = requireProject(category);
        if (project == null) return;
        JsonObject body = readBody(message);
        if (body == null) return;
        try {
            String concept = readField(body, "concept");
            double amount = readAmount(body, "amount");
            String categoryName = readField(body, "category");
            if (concept == null || concept.isBlank()) {
                throw new IllegalArgumentException("El concepto es obligatorio");
            }
            if (isInvalidAmount(amount)) {
                throw new IllegalArgumentException("El monto debe ser mayor que cero");
            }
            if (categoryName == null || categoryName.isBlank()) {
                throw new IllegalArgumentException("Selecciona una categoría");
            }
            List<Map<String, Object>> requests;
            synchronized (projectManager) {
                Category requestCategory = findCategory(project, categoryName);
                if (requestCategory == null) {
                    throw new IllegalArgumentException("La categoría no existe en este proyecto");
                }
                project.submitRequest(new ExpenseRequest(concept.trim(), amount, requestCategory, user));
                requests = requestsToList(project.getRequestsOf(user));
            }
            sendResponse(true, null, category, requests);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        } catch (UnsupportedOperationException e) {
            sendResponse(false, "Solicitud inválida", category, null);
        }
    }

    private Map<String, Object> pendingData(Project project) {
        List<ExpenseRequest> pending = new ArrayList<>();
        for (ExpenseRequest request : project.getRequests()) {
            if (request.getStatus() == RequestStatus.PENDING) {
                pending.add(request);
            }
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("availableBalance", project.getAvailableBalance());
        data.put("requests", requestsToList(pending));
        return data;
    }

    private void getPending() {
        MessageCategory category = MessageCategory.GET_PENDING;
        Project project = requireAdminProject(category);
        if (project == null) return;
        Map<String, Object> data;
        synchronized (projectManager) {
            data = pendingData(project);
        }
        sendResponse(true, null, category, data);
    }

    private void resolveRequest(Message message, boolean approve) {
        MessageCategory category = approve ? MessageCategory.APPROVE_REQUEST : MessageCategory.REJECT_REQUEST;
        Project project = requireAdminProject(category);
        if (project == null) return;
        JsonObject body = readBody(message);
        if (body == null) return;
        try {
            double rawId = readAmount(body, "id");
            String reason = readField(body, "reason");
            if (Double.isNaN(rawId) || rawId < 0 || rawId != Math.rint(rawId)) {
                throw new IllegalArgumentException("Solicitud inválida");
            }
            if (reason == null || reason.isBlank()) {
                throw new IllegalArgumentException("El motivo es obligatorio");
            }
            Map<String, Object> data;
            synchronized (projectManager) {
                int id = (int) rawId;
                if (id >= project.getRequests().size()) {
                    throw new IllegalArgumentException("La solicitud no existe");
                }
                ExpenseRequest request = project.getRequests().get(id);
                if (approve) {
                    project.approve(request, reason.trim());
                } else {
                    project.reject(request, reason.trim());
                }
                data = pendingData(project);
            }
            sendResponse(true, null, category, data);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        } catch (UnsupportedOperationException e) {
            sendResponse(false, "Solicitud inválida", category, null);
        }
    }

    private Map<String, Object> teamData(Project project) {
        List<String> members = new ArrayList<>();
        for (User member : project.getMembers()) {
            members.add(member.getUsername());
        }
        List<String> candidates = new ArrayList<>();
        for (User friend : user.getWorkCircle()) {
            if (!friend.hasProject() && !project.getMembers().contains(friend)
                    && !friend.getPendingInvitations().contains(project)) {
                candidates.add(friend.getUsername());
            }
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("members", members);
        data.put("candidates", candidates);
        return data;
    }

    private void getTeam() {
        MessageCategory category = MessageCategory.GET_TEAM;
        Project project = requireAdminProject(category);
        if (project == null) return;
        Map<String, Object> data;
        synchronized (projectManager) {
            data = teamData(project);
        }
        sendResponse(true, null, category, data);
    }

    private void inviteMember(Message message) {
        MessageCategory category = MessageCategory.INVITE_MEMBER;
        Project project = requireAdminProject(category);
        if (project == null) return;
        JsonObject body = readBody(message);
        if (body == null) return;
        try {
            String username = readField(body, "username");
            Map<String, Object> data;
            synchronized (projectManager) {
                project.inviteMember(findUserOrFail(username));
                data = teamData(project);
            }
            sendResponse(true, null, category, data);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        } catch (UnsupportedOperationException e) {
            sendResponse(false, "Solicitud inválida", category, null);
        }
    }

    private void removeMember(Message message) {
        MessageCategory category = MessageCategory.REMOVE_MEMBER;
        Project project = requireAdminProject(category);
        if (project == null) return;
        JsonObject body = readBody(message);
        if (body == null) return;
        try {
            String username = readField(body, "username");
            checkPassword(readField(body, "password"));
            Map<String, Object> data;
            synchronized (projectManager) {
                project.removeMember(findUserOrFail(username));
                data = teamData(project);
            }
            sendResponse(true, null, category, data);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        } catch (UnsupportedOperationException e) {
            sendResponse(false, "Solicitud inválida", category, null);
        }
    }

    private void transferAdmin(Message message) {
        MessageCategory category = MessageCategory.TRANSFER_ADMIN;
        Project project = requireAdminProject(category);
        if (project == null) return;
        JsonObject body = readBody(message);
        if (body == null) return;
        try {
            String username = readField(body, "username");
            checkPassword(readField(body, "password"));
            synchronized (projectManager) {
                project.transferAdmin(findUserOrFail(username));
            }
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("projectName", project.getName());
            data.put("role", user.getRole().name());
            sendResponse(true, null, category, data);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        } catch (UnsupportedOperationException e) {
            sendResponse(false, "Solicitud inválida", category, null);
        }
    }

    private void closeProject(Message message) {
        MessageCategory category = MessageCategory.CLOSE_PROJECT;
        Project project = requireAdminProject(category);
        if (project == null) return;
        JsonObject body = readBody(message);
        if (body == null) return;
        try {
            checkPassword(readField(body, "password"));
            synchronized (projectManager) {
                projectManager.closeProject(project);
            }
            sendResponse(true, null, category, null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        } catch (UnsupportedOperationException e) {
            sendResponse(false, "Solicitud inválida", category, null);
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
                controller.saveData();
                closeConnection();
                break;
        }
    }

    private void leaveProject() {
        MessageCategory category = MessageCategory.LEAVE_PROJECT;
        if (requireProject(category) == null) return;
        try {
            synchronized (projectManager) {
                user.leaveProject();
            }
            sendResponse(true, null, category, null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            sendResponse(false, e.getMessage(), category, null);
        }
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
        Message message = new Message(category, data);
        sendMessage(message);
    }

    public void sendResponse(boolean ok, String errorMsg, MessageCategory category, Object extraData) {
        Map<String, Object> responseContent = new HashMap<>();
        responseContent.put("ok", ok);
        
        if (!ok && errorMsg != null) responseContent.put("error", errorMsg);
        
        if (extraData != null) responseContent.put("data", extraData);

        Gson gson = new Gson();
        String jsonString = gson.toJson(responseContent);

        Message responseMessage = new Message(category, jsonString);
        try {
            sender.writeUTF(gson.toJson(responseMessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

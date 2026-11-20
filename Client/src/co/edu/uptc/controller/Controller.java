package co.edu.uptc.controller;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.enums.MessageCategory;
import co.edu.uptc.interfaces.IController;
import co.edu.uptc.net.JSONReader;
import co.edu.uptc.net.Message;
import co.edu.uptc.view.frames.MainFrame;
import co.edu.uptc.view.utills.ViewUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Controller implements IController {
    private static final int PORT = 10087;
    private static final String HOST = "localhost";
    private static final String ALL_CATEGORIES = "Todas";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String NOTIFY_REMOVED = "REMOVED_FROM_PROJECT";
    private static final String NOTIFY_PROJECT_CLOSED = "PROJECT_CLOSED";
    private static final String NOTIFY_INVITATION_CANCELLED = "INVITATION_CANCELLED";
    private Socket socket;
    private MainFrame frame;
    private ListenerThread listener;
    private DataOutputStream sender;
    private JSONReader jsonReader;
    private boolean closed;
    private String currentUser;
    private final Gson gson = new Gson();

    public Controller() {
        connect();
    }

    private void connect() {
        try {
            this.socket = new Socket(HOST, PORT);
            this.listener = new ListenerThread(this, socket);
            this.sender = new DataOutputStream(socket.getOutputStream());
            this.jsonReader = new JSONReader();
        } catch (IOException e) {
            close(ErrorMessages.CONNECTION_FAILED.format(HOST, PORT));
        }
    }

    public void init() {
        this.frame = new MainFrame(this);
        listener.start();
    }

    public void close() {
        close(ErrorMessages.CONNECTION_LOST.getText());
    }

    public void close(String message) {
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
        }
        if (message != null) {
            showMessage(message);
        }
        System.exit(0);
    }

    private void showMessage(String message) {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                displayError(message);
            } else {
                SwingUtilities.invokeAndWait(() -> displayError(message));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void displayError(String message) {
        if (frame != null) {
            frame.showGeneralError(message);
        } else {
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void processData(String data) {
        Message message = jsonReader.jsonToMessage(data);
        MessageCategory category = message.getCategory();
        JsonObject response = parseResponse(message);
        if (isSessionCommand(category)) {
            sessionCommands(category, response);
            return;
        }
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        workCircleCommands(category, response);
        projectCommands(category, response);
        historyCommands(category, response);
        requestsCommands(category, response);
        approveCommands(category, response);
        teamCommands(category, response);
        remainingCommands(category, response);
    }

    private boolean isSessionCommand(MessageCategory category) {
        return category == MessageCategory.REGISTER
                || category == MessageCategory.LOGIN
                || category == MessageCategory.LOGOUT;
    }

    private void sessionCommands(MessageCategory category, JsonObject response) {
        switch (category) {
            case REGISTER:
                onRegister(response);
                break;
            case LOGIN:
                onLogin(response);
                break;
            case LOGOUT:
                onLogout();
                break;
        }
    }

    private void workCircleCommands(MessageCategory category, JsonObject response) {
        switch (category) {
            case GET_CIRCLE:
                onCircle(response);
                break;
            case ADD_TO_CIRCLE:
                onCircle(response);
                break;
            case GET_INVITATIONS:
                onInvitations(response);
                break;
            case ACCEPT_INVITATION:
                onJoinedProject(response);
                break;
            case REJECT_INVITATION:
                onInvitations(response);
                break;
        }
    }

    private void projectCommands(MessageCategory category, JsonObject response) {
        switch (category) {
            case CREATE_PROJECT:
                onCreateProject(response);
                break;
            case GET_PROJECT_INFO:
                onProjectInfo(response);
                break;
        }
    }

    private void historyCommands(MessageCategory category, JsonObject response) {
        switch (category) {
            case GET_SUMMARY:
                onSummary(response);
                break;
            case GET_HISTORY:
                onHistory(response);
                break;
        }
    }

    private void requestsCommands(MessageCategory category, JsonObject response) {
        switch (category) {
            case CREATE_REQUEST:
                onCreateRequest(response);
                break;
            case GET_MY_REQUESTS:
                onMyRequests(response);
                break;
        }
    }

    private void approveCommands(MessageCategory category, JsonObject response) {
        switch (category) {
            case GET_PENDING:
                onPending(response);
                break;
            case APPROVE_REQUEST:
                onRequestResolved(response);
                break;
            case REJECT_REQUEST:
                onRequestResolved(response);
                break;
        }
    }

    private void teamCommands(MessageCategory category, JsonObject response) {
        switch (category) {
            case GET_TEAM:
                onTeam(response);
                break;
            case INVITE_MEMBER:
                onTeam(response);
                break;
            case REMOVE_MEMBER:
                onTeam(response);
                break;
            case TRANSFER_ADMIN:
                onJoinedProject(response);
                break;
            case CLOSE_PROJECT:
                showProject();
                break;
        }
    }

    private void remainingCommands(MessageCategory category, JsonObject response) {
        switch (category) {
            case LEAVE_PROJECT:
                showProject();
                break;
            case NOTIFY:
                onNotify(response);
                break;
            case EXIT:
                break;
        }
    }

    private void showServerError(String message) {
        SwingUtilities.invokeLater(() -> frame.showGeneralError(message));
    }

    private void onNotify(JsonObject response) {
        JsonObject data = dataOf(response);
        String type = data.get("type").getAsString();
        String message = data.get("message").getAsString();
        SwingUtilities.invokeLater(() -> applyNotification(type, message));
    }

    private void applyNotification(String type, String message) {
        if (NOTIFY_REMOVED.equals(type) || NOTIFY_PROJECT_CLOSED.equals(type)) {
            frame.showProject();
        } else if (NOTIFY_INVITATION_CANCELLED.equals(type)) {
            getInvitations();
        }
        frame.addNotification(message);
    }

    private JsonObject parseResponse(Message message) {
        return JsonParser.parseString(message.getText()).getAsJsonObject();
    }

    private boolean isOk(JsonObject response) {
        return response.has("ok") && response.get("ok").getAsBoolean();
    }

    private String getError(JsonObject response) {
        return response.has("error") ? response.get("error").getAsString() : ErrorMessages.UNKNOWN_ERROR.getText();
    }

    private JsonObject dataOf(JsonObject response) {
        return response.getAsJsonObject("data");
    }

    private List<String> toStringList(JsonArray array) {
        List<String> list = new ArrayList<>();
        for (JsonElement element : array) {
            list.add(element.getAsString());
        }
        return list;
    }


    private void onRegister(JsonObject response) {
        if (isOk(response)) {
            SwingUtilities.invokeLater(() -> ViewUtils.showMessage(frame, ErrorMessages.ACCOUNT_CREATED.getText()));
        } else {
            SwingUtilities.invokeLater(() -> frame.showError(getError(response)));
        }
    }

    private void onLogin(JsonObject response) {
        if (!isOk(response)) {
            SwingUtilities.invokeLater(() -> frame.showError(getError(response)));
            return;
        }
        JsonObject data = dataOf(response);
        this.currentUser = data.get("username").getAsString();
        SwingUtilities.invokeLater(() -> showSession(currentUser, data));
    }

    private void showSession(String username, JsonObject data) {
        if (!data.has("projectName")) {
            frame.showProject();
        } else if (ADMIN_ROLE.equals(data.get("role").getAsString())) {
            frame.showAdmin(username, data.get("projectName").getAsString());
        } else {
            frame.showMember(username, data.get("projectName").getAsString());
        }
    }

    private void onLogout() {
        currentUser = null;
        SwingUtilities.invokeLater(frame::showLogin);
    }

    private void showProject() {
        SwingUtilities.invokeLater(frame::showProject);
    }


    private void onCircle(JsonObject response) {
        List<String> members = toStringList(response.getAsJsonArray("data"));
        SwingUtilities.invokeLater(() -> frame.setCircle(members));
    }

    private void onInvitations(JsonObject response) {
        List<String> invitations = toStringList(response.getAsJsonArray("data"));
        SwingUtilities.invokeLater(() -> frame.setInvitations(invitations));
    }

    /** Sirve para aceptar una invitación y para recibir la administración: en ambos casos el usuario queda como integrante. */
    private void onJoinedProject(JsonObject response) {
        String projectName = dataOf(response).get("projectName").getAsString();
        SwingUtilities.invokeLater(() -> frame.showMember(currentUser, projectName));
    }

    private void onCreateProject(JsonObject response) {
        String projectName = dataOf(response).get("projectName").getAsString();
        SwingUtilities.invokeLater(() -> frame.showAdmin(currentUser, projectName));
    }

    private void onProjectInfo(JsonObject response) {
        JsonObject data = dataOf(response);
        List<String> categories = toStringList(data.getAsJsonArray("categories"));
        String balance = money(data.get("availableBalance").getAsDouble());
        SwingUtilities.invokeLater(() -> frame.setProjectInfo(categories, balance));
    }

    private void onTeam(JsonObject response) {
        JsonObject data = dataOf(response);
        List<String> members = toStringList(data.getAsJsonArray("members"));
        List<String> candidates = toStringList(data.getAsJsonArray("candidates"));
        SwingUtilities.invokeLater(() -> frame.setTeam(members, candidates));
    }

    private void onSummary(JsonObject response) {
        JsonObject data = dataOf(response);
        String balance = money(data.get("availableBalance").getAsDouble());
        String totalSpent = money(data.get("totalSpent").getAsDouble());
        List<Object[]> categoryRows = toCategoryRows(data.getAsJsonArray("categories"));
        List<Object[]> memberRows = toMemberRows(data.getAsJsonArray("members"));
        SwingUtilities.invokeLater(() -> frame.setSummary(balance, totalSpent, categoryRows, memberRows));
    }

    private List<Object[]> toCategoryRows(JsonArray categories) {
        List<Object[]> rows = new ArrayList<>();
        for (JsonElement element : categories) {
            JsonObject item = element.getAsJsonObject();
            double budget = item.get("budget").getAsDouble();
            double spent = item.get("spent").getAsDouble();
            double usage = budget > 0 ? spent / budget * 100 : 0;
            rows.add(new Object[]{item.get("name").getAsString(), money(budget), money(spent),
                    String.format("%.1f%%", usage)});
        }
        return rows;
    }

    private List<Object[]> toMemberRows(JsonArray members) {
        List<Object[]> rows = new ArrayList<>();
        for (JsonElement element : members) {
            JsonObject item = element.getAsJsonObject();
            rows.add(new Object[]{item.get("name").getAsString(), money(item.get("spent").getAsDouble())});
        }
        return rows;
    }

    private void onHistory(JsonObject response) {
        List<Object[]> rows = toRequestRows(response.getAsJsonArray("data"), true, true);
        SwingUtilities.invokeLater(() -> frame.setHistory(rows));
    }

    private void onMyRequests(JsonObject response) {
        List<Object[]> rows = toRequestRows(response.getAsJsonArray("data"), false, true);
        SwingUtilities.invokeLater(() -> frame.setMyRequests(rows));
    }

    private void onCreateRequest(JsonObject response) {
        List<Object[]> rows = toRequestRows(response.getAsJsonArray("data"), false, true);
        SwingUtilities.invokeLater(() -> frame.requestCreated(rows));
    }

    private void onPending(JsonObject response) {
        JsonObject data = dataOf(response);
        String balance = money(data.get("availableBalance").getAsDouble());
        List<Object[]> rows = toRequestRows(data.getAsJsonArray("requests"), true, false);
        SwingUtilities.invokeLater(() -> frame.setPending(balance, rows));
    }

    private void onRequestResolved(JsonObject response) {
        JsonObject data = dataOf(response);
        String balance = money(data.get("availableBalance").getAsDouble());
        List<Object[]> rows = toRequestRows(data.getAsJsonArray("requests"), true, false);
        SwingUtilities.invokeLater(() -> frame.requestResolved(balance, rows));
    }

    private List<Object[]> toRequestRows(JsonArray requests, boolean withRequester, boolean withResolution) {
        List<Object[]> rows = new ArrayList<>();
        for (JsonElement element : requests) {
            rows.add(toRequestRow(element.getAsJsonObject(), withRequester, withResolution));
        }
        return rows;
    }

    private Object[] toRequestRow(JsonObject item, boolean withRequester, boolean withResolution) {
        List<Object> row = new ArrayList<>();
        row.add(item.get("id").getAsInt());
        row.add(item.get("createdAt").getAsString());
        if (withRequester) {
            row.add(item.get("requester").getAsString());
        }
        addDetails(row, item);
        if (withResolution) {
            addResolution(row, item);
        }
        return row.toArray();
    }

    private void addDetails(List<Object> row, JsonObject item) {
        row.add(item.get("concept").getAsString());
        row.add(item.get("category").getAsString());
        row.add(money(item.get("amount").getAsDouble()));
    }

    private void addResolution(List<Object> row, JsonObject item) {
        row.add(statusLabel(item.get("status").getAsString()));
        row.add(item.has("reason") ? item.get("reason").getAsString() : "");
    }

    private String money(double amount) {
        return String.format("$%,.2f", amount);
    }

    private String statusLabel(String status) {
        switch (status) {
            case "APPROVED":
                return "Aprobada";
            case "REJECTED":
                return "Rechazada";
            default:
                return "Pendiente";
        }
    }

    public synchronized void sendData(Message message) {
        if (sender == null || message == null) {
            return;
        }
        try {
            sender.writeUTF(jsonReader.messageToJson(message));
            sender.flush();
        } catch (IOException e) {
            close();
        }
    }

    private Map<String, Object> body(Object... pairs) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((String) pairs[i], pairs[i + 1]);
        }
        return map;
    }

    private String statusCode(String label) {
        if ("Pendiente".equals(label)) {
            return "PENDING";
        }
        if ("Aprobada".equals(label)) {
            return "APPROVED";
        }
        return "Rechazada".equals(label) ? "REJECTED" : null;
    }

    private String orderCode(String label) {
        if ("Monto (mayor a menor)".equals(label)) {
            return "AMOUNT_DESCENDING";
        }
        if ("Fecha de aprobación".equals(label)) {
            return "APPROVAL_DATE";
        }
        return "Integrante solicitante".equals(label) ? "REQUESTER" : "CREATED_DATE";
    }

    private void putIfNotBlank(Map<String, Object> filters, String key, String value) {
        if (value != null && !value.isBlank()) {
            filters.put(key, value);
        }
    }

    private void sendRequest(MessageCategory category, Map<String, Object> body) {
        sendData(new Message(category, gson.toJson(body)));
    }

    private void sendRequest(MessageCategory category) {
        sendData(new Message(category, ""));
    }

    public void sendExit() {
        sendData(new Message(MessageCategory.EXIT, ""));
    }

    @Override
    public void register(String user, String password) {
        sendRequest(MessageCategory.REGISTER, body("username", user.trim(), "password", password));
    }

    @Override
    public void login(String user, String password) {
        sendRequest(MessageCategory.LOGIN, body("username", user.trim(), "password", password));
    }

    @Override
    public void logout() {
        sendRequest(MessageCategory.LOGOUT);
    }

    @Override
    public void addToCircle(String user) {
        sendRequest(MessageCategory.ADD_TO_CIRCLE, body("username", user.trim()));
    }

    @Override
    public void acceptInvitation(String projectName) {
        sendRequest(MessageCategory.ACCEPT_INVITATION, body("projectName", projectName));
    }

    @Override
    public void rejectInvitation(String projectName) {
        sendRequest(MessageCategory.REJECT_INVITATION, body("projectName", projectName));
    }

    @Override
    public void createProject(String name, double budget, Map<String, Double> categories, String password) {
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categories.entrySet()) {
            items.add(body("name", entry.getKey(), "budget", entry.getValue()));
        }
        sendRequest(MessageCategory.CREATE_PROJECT,
                body("name", name, "budget", budget, "password", password, "categories", items));
    }

    @Override
    public void createRequest(String concept, double amount, String category) {
        sendRequest(MessageCategory.CREATE_REQUEST, body("concept", concept, "amount", amount, "category", category));
    }

    @Override
    public void getHistory(String category, String status, String from, String to, String order) {
        Map<String, Object> filters = new LinkedHashMap<>();
        if (!ALL_CATEGORIES.equals(category)) {
            putIfNotBlank(filters, "category", category);
        }
        putIfNotBlank(filters, "status", statusCode(status));
        putIfNotBlank(filters, "from", from);
        putIfNotBlank(filters, "to", to);
        filters.put("order", orderCode(order));
        sendRequest(MessageCategory.GET_HISTORY, filters);
    }

    @Override
    public void approveRequest(int id, String reason) {
        sendRequest(MessageCategory.APPROVE_REQUEST, body("id", id, "reason", reason));
    }

    @Override
    public void rejectRequest(int id, String reason) {
        sendRequest(MessageCategory.REJECT_REQUEST, body("id", id, "reason", reason));
    }

    @Override
    public void inviteMember(String user) {
        sendRequest(MessageCategory.INVITE_MEMBER, body("username", user));
    }

    @Override
    public void removeMember(String user, String password) {
        sendRequest(MessageCategory.REMOVE_MEMBER, body("username", user, "password", password));
    }

    @Override
    public void transferAdmin(String user, String password) {
        sendRequest(MessageCategory.TRANSFER_ADMIN, body("username", user, "password", password));
    }

    @Override
    public void closeProject(String password) {
        sendRequest(MessageCategory.CLOSE_PROJECT, body("password", password));
    }

    @Override
    public void leaveProject() {
        sendRequest(MessageCategory.LEAVE_PROJECT);
    }

    @Override
    public void getCircle() {
        sendRequest(MessageCategory.GET_CIRCLE);
    }

    @Override
    public void getInvitations() {
        sendRequest(MessageCategory.GET_INVITATIONS);
    }

    @Override
    public void getProjectInfo() {
        sendRequest(MessageCategory.GET_PROJECT_INFO);
    }

    @Override
    public void getSummary() {
        sendRequest(MessageCategory.GET_SUMMARY);
    }

    @Override
    public void getMyRequests() {
        sendRequest(MessageCategory.GET_MY_REQUESTS);
    }

    @Override
    public void getPending() {
        sendRequest(MessageCategory.GET_PENDING);
    }

    @Override
    public void getTeam() {
        sendRequest(MessageCategory.GET_TEAM);
    }
}

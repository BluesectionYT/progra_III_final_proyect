package co.edu.uptc.controller;

import co.edu.uptc.interfaces.IController;
import co.edu.uptc.net.JSONReader;
import co.edu.uptc.net.Message;
import co.edu.uptc.enums.MessageCategory;
import co.edu.uptc.view.frames.MainFrame;
import co.edu.uptc.view.utills.ViewUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Controller implements IController {
    private static final int PORT = 10087;
    private static final String HOST = "localhost";
    private Socket socket;
    private MainFrame frame;
    private ListenerThread listener;
    private DataOutputStream sender;
    private JSONReader jsonReader;
    private boolean closed;
    private String currentUser;
    private final Gson gson = new Gson();

    public Controller() {
        try {
            this.socket = new Socket(HOST, PORT);
            this.listener = new ListenerThread(this, socket);
            this.sender = new DataOutputStream(socket.getOutputStream());
            this.jsonReader = new JSONReader();
        } catch (IOException e) {
            close("No se pudo conectar con el servidor (" + HOST + ":" + PORT + ").\nVerifica que esté en ejecución.");
        }
    }

    public void init() {
        this.frame = new MainFrame(this);
        listener.start();
    }

    public void close() {
        close("Se perdió la conexión con el servidor");
    }

    public void close(String message) {
        synchronized (this) {
            if (closed) return;
            closed = true;
        }
        if (message != null) {
            showMessage(message);
        }
        System.exit(0);
    }

    private void showMessage(String message) {
        Runnable show = () -> {
            if (frame != null) {
                frame.showGeneralError(message);
            } else {
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                show.run();
            } else {
                SwingUtilities.invokeAndWait(show);
            }
        } catch (Exception e) {
            
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
                    onRegister(message);
                    break;
                case LOGIN:
                    onLogin(message);
                    break;
                case LOGOUT:
                    onLogout();
                    break;
            }
        }
    }

    private void onRegister(Message message) {
        JsonObject response = parseResponse(message);
        if (isOk(response)) {
            SwingUtilities.invokeLater(() -> ViewUtils.showMessage(frame, "Cuenta creada. Ya puedes ingresar."));
        } else {
            String error = getError(response);
            SwingUtilities.invokeLater(() -> frame.showError(error));
        }
    }

    private void onLogin(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            String error = getError(response);
            SwingUtilities.invokeLater(() -> frame.showError(error));
            return;
        }
        JsonObject data = response.getAsJsonObject("data");
        String username = data.get("username").getAsString();
        this.currentUser = username;
        SwingUtilities.invokeLater(() -> {
            if (!data.has("projectName")) {
                frame.showProject();
            } else {
                String projectName = data.get("projectName").getAsString();
                if ("ADMIN".equals(data.get("role").getAsString())) {
                    frame.showAdmin(username, projectName);
                } else {
                    frame.showMember(username, projectName);
                }
            }
        });
    }

    private void onLogout() {
        currentUser = null;
        SwingUtilities.invokeLater(frame::showLogin);
    }

    private JsonObject parseResponse(Message message) {
        return JsonParser.parseString(message.getText()).getAsJsonObject();
    }

    private boolean isOk(JsonObject response) {
        return response.has("ok") && response.get("ok").getAsBoolean();
    }

    private String getError(JsonObject response) {
        return response.has("error") ? response.get("error").getAsString() : "Error desconocido";
    }

    private void workCircleCommands(Message message) {
        switch (message.getCategory()) {
            case GET_CIRCLE:
                onCircle(message);
                break;
            case ADD_TO_CIRCLE:
                onCircle(message);
                break;
            case GET_INVITATIONS:
                onInvitations(message);
                break;
            case ACCEPT_INVITATION:
                onAcceptInvitation(message);
                break;
            case REJECT_INVITATION:
                onInvitations(message);
                break;
        }
    }

    private void onCircle(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        List<String> members = toStringList(response.getAsJsonArray("data"));
        SwingUtilities.invokeLater(() -> frame.setCircle(members));
    }

    private void onInvitations(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        List<String> invitations = toStringList(response.getAsJsonArray("data"));
        SwingUtilities.invokeLater(() -> frame.setInvitations(invitations));
    }

    private void onAcceptInvitation(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        String projectName = response.getAsJsonObject("data").get("projectName").getAsString();
        SwingUtilities.invokeLater(() -> frame.showMember(currentUser, projectName));
    }

    private void showServerError(String error) {
        SwingUtilities.invokeLater(() -> frame.showGeneralError(error));
    }

    private List<String> toStringList(JsonArray array) {
        List<String> list = new ArrayList<>();
        for (JsonElement element : array) {
            list.add(element.getAsString());
        }
        return list;
    }

    private void onProjectInfo(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        JsonObject data = response.getAsJsonObject("data");
        List<String> categories = toStringList(data.getAsJsonArray("categories"));
        String balance = money(data.get("availableBalance").getAsDouble());
        SwingUtilities.invokeLater(() -> frame.setProjectInfo(categories, balance));
    }

    private void onSummary(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        JsonObject data = response.getAsJsonObject("data");
        String balance = money(data.get("availableBalance").getAsDouble());
        String totalSpent = money(data.get("totalSpent").getAsDouble());
        List<Object[]> categoryRows = new ArrayList<>();
        for (JsonElement element : data.getAsJsonArray("categories")) {
            JsonObject item = element.getAsJsonObject();
            double budget = item.get("budget").getAsDouble();
            double spent = item.get("spent").getAsDouble();
            double usage = budget > 0 ? spent / budget * 100 : 0;
            categoryRows.add(new Object[]{item.get("name").getAsString(), money(budget), money(spent),
                    String.format("%.1f%%", usage)});
        }
        List<Object[]> memberRows = new ArrayList<>();
        for (JsonElement element : data.getAsJsonArray("members")) {
            JsonObject item = element.getAsJsonObject();
            memberRows.add(new Object[]{item.get("name").getAsString(), money(item.get("spent").getAsDouble())});
        }
        SwingUtilities.invokeLater(() -> frame.setSummary(balance, totalSpent, categoryRows, memberRows));
    }

    private void onHistory(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        List<Object[]> rows = toRequestRows(response.getAsJsonArray("data"), true, true);
        SwingUtilities.invokeLater(() -> frame.setHistory(rows));
    }

    private void onMyRequests(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        List<Object[]> rows = toRequestRows(response.getAsJsonArray("data"), false, true);
        SwingUtilities.invokeLater(() -> frame.setMyRequests(rows));
    }

    private void onCreateRequest(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        List<Object[]> rows = toRequestRows(response.getAsJsonArray("data"), false, true);
        SwingUtilities.invokeLater(() -> frame.requestCreated(rows));
    }

    private void onPending(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        JsonObject data = response.getAsJsonObject("data");
        String balance = money(data.get("availableBalance").getAsDouble());
        List<Object[]> rows = toRequestRows(data.getAsJsonArray("requests"), true, false);
        SwingUtilities.invokeLater(() -> frame.setPending(balance, rows));
    }

    private void onRequestResolved(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        JsonObject data = response.getAsJsonObject("data");
        String balance = money(data.get("availableBalance").getAsDouble());
        List<Object[]> rows = toRequestRows(data.getAsJsonArray("requests"), true, false);
        SwingUtilities.invokeLater(() -> frame.requestResolved(balance, rows));
    }

    private void onTeam(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        JsonObject data = response.getAsJsonObject("data");
        List<String> members = toStringList(data.getAsJsonArray("members"));
        List<String> candidates = toStringList(data.getAsJsonArray("candidates"));
        SwingUtilities.invokeLater(() -> frame.setTeam(members, candidates));
    }

    private void onTransferAdmin(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        String projectName = response.getAsJsonObject("data").get("projectName").getAsString();
        SwingUtilities.invokeLater(() -> frame.showMember(currentUser, projectName));
    }

    private void onCloseProject(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        SwingUtilities.invokeLater(frame::showProject);
    }

    private List<Object[]> toRequestRows(JsonArray requests, boolean withRequester, boolean withResolution) {
        List<Object[]> rows = new ArrayList<>();
        for (JsonElement element : requests) {
            JsonObject item = element.getAsJsonObject();
            List<Object> row = new ArrayList<>();
            row.add(item.get("id").getAsInt());
            row.add(item.get("createdAt").getAsString());
            if (withRequester) {
                row.add(item.get("requester").getAsString());
            }
            row.add(item.get("concept").getAsString());
            row.add(item.get("category").getAsString());
            row.add(money(item.get("amount").getAsDouble()));
            if (withResolution) {
                row.add(statusLabel(item.get("status").getAsString()));
                row.add(item.has("reason") ? item.get("reason").getAsString() : "");
            }
            rows.add(row.toArray());
        }
        return rows;
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

    private void projectCommands(Message message) {
        switch (message.getCategory()) {
            case CREATE_PROJECT:
                onCreateProject(message);
                break;
            case GET_PROJECT_INFO:
                onProjectInfo(message);
                break;
        }
    }

    private void onCreateProject(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        String projectName = response.getAsJsonObject("data").get("projectName").getAsString();
        SwingUtilities.invokeLater(() -> frame.showAdmin(currentUser, projectName));
    }

    private void historyCommands(Message message) {
        switch (message.getCategory()) {
            case GET_SUMMARY:
                onSummary(message);
                break;
            case GET_HISTORY:
                onHistory(message);
                break;
        }
    }

    private void requestsCommands(Message message) {
        switch (message.getCategory()) {
            case CREATE_REQUEST:
                onCreateRequest(message);
                break;
            case GET_MY_REQUESTS:
                onMyRequests(message);
                break;
        }
    }

    private void approveCommands(Message message) {
        switch (message.getCategory()) {
            case GET_PENDING:
                onPending(message);
                break;
            case APPROVE_REQUEST:
                onRequestResolved(message);
                break;
            case REJECT_REQUEST:
                onRequestResolved(message);
                break;
        }
    }

    private void teamCommands(Message message) {
        switch (message.getCategory()) {
            case GET_TEAM:
                onTeam(message);
                break;
            case INVITE_MEMBER:
                onTeam(message);
                break;
            case REMOVE_MEMBER:
                onTeam(message);
                break;
            case TRANSFER_ADMIN:
                onTransferAdmin(message);
                break;
            case CLOSE_PROJECT:
                onCloseProject(message);
                break;
        }
    }

    private void remainingCommands(Message message) {
        switch (message.getCategory()) {
            case LEAVE_PROJECT:
                onLeaveProject(message);
                break;
            case NOTIFY:
                break;
            case EXIT:
                break;
        }
    }

    private void onLeaveProject(Message message) {
        JsonObject response = parseResponse(message);
        if (!isOk(response)) {
            showServerError(getError(response));
            return;
        }
        SwingUtilities.invokeLater(frame::showProject);
    }

    public synchronized void sendData(Message message) {
        if (sender == null || message == null) return;

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
        if ("Pendiente".equals(label)) return "PENDING";
        if ("Aprobada".equals(label)) return "APPROVED";
        if ("Rechazada".equals(label)) return "REJECTED";
        return null;
    }

    private String orderCode(String label) {
        if ("Monto (mayor a menor)".equals(label)) return "AMOUNT_DESCENDING";
        if ("Fecha de aprobación".equals(label)) return "APPROVAL_DATE";
        if ("Integrante solicitante".equals(label)) return "REQUESTER";
        return "CREATED_DATE";
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
        sendRequest(MessageCategory.REGISTER,
            body("username", user.trim(), "password", password));
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
        sendRequest(MessageCategory.CREATE_REQUEST,
            body("concept", concept, "amount", amount, "category", category));
    }

    @Override
    public void getHistory(String category, String status, String from, String to, String order) {
        Map<String, Object> filters = new LinkedHashMap<>();
        if (category != null && !"Todas".equals(category)) {
            filters.put("category", category);
        }
        String statusCode = statusCode(status);
        if (statusCode != null) {
            filters.put("status", statusCode);
        }
        if (from != null && !from.isBlank()) {
            filters.put("from", from);
        }
        if (to != null && !to.isBlank()) {
            filters.put("to", to);
        }
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

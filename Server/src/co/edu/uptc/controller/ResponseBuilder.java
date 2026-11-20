package co.edu.uptc.controller;

import co.edu.uptc.enums.RequestStatus;
import co.edu.uptc.model.Category;
import co.edu.uptc.model.ExpenseRequest;
import co.edu.uptc.model.Project;
import co.edu.uptc.model.User;
import com.google.gson.Gson;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ResponseBuilder {
    public static final String NOTIFY_REMOVED = "REMOVED_FROM_PROJECT";
    public static final String NOTIFY_PROJECT_CLOSED = "PROJECT_CLOSED";
    public static final String NOTIFY_INVITATION_CANCELLED = "INVITATION_CANCELLED";
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Gson GSON = new Gson();

    private ResponseBuilder() {
    }

    public static String toJson(boolean ok, String errorMsg, Object extraData) {
        Map<String, Object> content = new HashMap<>();
        content.put("ok", ok);
        if (!ok && errorMsg != null) {
            content.put("error", errorMsg);
        }
        if (extraData != null) {
            content.put("data", extraData);
        }
        return GSON.toJson(content);
    }

    public static Map<String, Object> notification(String type, String message) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("type", type);
        data.put("message", message);
        return data;
    }

    public static List<String> circleNames(User user) {
        return user.getWorkCircle().stream().map(User::getUsername).collect(Collectors.toList());
    }

    public static List<String> invitationNames(User user) {
        return user.getPendingInvitations().stream().map(Project::getName).collect(Collectors.toList());
    }

    public static Map<String, Object> sessionData(User user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("username", user.getUsername());
        if (user.hasProject()) {
            data.put("projectName", user.getCurrentProject().getName());
            data.put("role", user.getRole().name());
        }
        return data;
    }

    public static Map<String, Object> membershipData(String projectName, User user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("projectName", projectName);
        data.put("role", user.getRole().name());
        return data;
    }

    public static Map<String, Object> projectInfo(Project project, User user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", project.getName());
        data.put("role", user.getRole().name());
        data.put("admin", project.getAdmin().getUsername());
        data.put("totalBudget", project.getTotalBudget());
        data.put("availableBalance", project.getAvailableBalance());
        data.put("categories", project.getCategories().stream().map(Category::getName).collect(Collectors.toList()));
        return data;
    }

    public static Map<String, Object> summary(Project project) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("availableBalance", project.getAvailableBalance());
        data.put("totalSpent", project.getTotalSpent());
        data.put("categories", categorySummary(project));
        data.put("members", memberSummary(project));
        return data;
    }

    private static List<Map<String, Object>> categorySummary(Project project) {
        Map<Category, Double> spentByCategory = project.getSpentByCategory();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Category category : project.getCategories()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", category.getName());
            row.put("budget", category.getBudget());
            row.put("spent", spentByCategory.getOrDefault(category, 0.0));
            rows.add(row);
        }
        return rows;
    }

    private static List<Map<String, Object>> memberSummary(Project project) {
        Map<User, Double> spentByMember = project.getSpentByMember();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (User member : project.getMembers()) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", member.getUsername());
            row.put("spent", spentByMember.getOrDefault(member, 0.0));
            rows.add(row);
        }
        return rows;
    }

    public static List<Map<String, Object>> requestsToList(List<ExpenseRequest> requests) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ExpenseRequest request : requests) {
            list.add(requestToMap(request));
        }
        return list;
    }

    private static Map<String, Object> requestToMap(ExpenseRequest request) {
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
        return item;
    }

    public static Map<String, Object> pendingData(Project project) {
        List<ExpenseRequest> pending = project.getRequests().stream()
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .collect(Collectors.toList());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("availableBalance", project.getAvailableBalance());
        data.put("requests", requestsToList(pending));
        return data;
    }

    public static Map<String, Object> teamData(Project project, User user) {
        List<String> members = project.getMembers().stream().map(User::getUsername).collect(Collectors.toList());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("members", members);
        data.put("candidates", candidatesFor(project, user));
        return data;
    }

    private static List<String> candidatesFor(Project project, User user) {
        List<String> candidates = new ArrayList<>();
        for (User friend : user.getWorkCircle()) {
            if (canBeInvited(friend, project)) {
                candidates.add(friend.getUsername());
            }
        }
        return candidates;
    }

    private static boolean canBeInvited(User friend, Project project) {
        return !friend.hasProject() && !project.getMembers().contains(friend)
                && !friend.getPendingInvitations().contains(project);
    }
}

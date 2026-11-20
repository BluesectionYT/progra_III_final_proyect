package co.edu.uptc.persistence;

import co.edu.uptc.enums.RequestStatus;

import java.util.ArrayList;
import java.util.List;

public class StateSnapshot {
    public List<UserData> users = new ArrayList<>();
    public List<ProjectData> projects = new ArrayList<>();

    public static class UserData {
        public String username;
        public String passwordHash;
        public List<String> workCircle = new ArrayList<>();
        public List<String> pendingInvitations = new ArrayList<>();
    }

    public static class ProjectData {
        public String name;
        public double totalBudget;
        public String admin;
        public List<String> members = new ArrayList<>();
        public List<CategoryData> categories = new ArrayList<>();
        public List<RequestData> requests = new ArrayList<>();
    }

    public static class CategoryData {
        public String name;
        public double budget;
    }

    public static class RequestData {
        public int id;
        public String concept;
        public double amount;
        public String category;
        public String requester;
        public RequestStatus status;
        public String reason;
        public String createdAt;
        public String resolvedAt;
    }
}

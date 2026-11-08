package co.edu.uptc.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import co.edu.uptc.structures.BinaryTree;
import co.edu.uptc.structures.Queue;

public class Project {
    private String name;
    private double totalBudget;
    private double availableBalance;
    private boolean active;
    private User admin;
    private ArrayList<User> members;
    private ArrayList<Category> categories;
    private ArrayList<ExpenseRequest> requests;
    private Queue<ExpenseRequest> pendingQueue;

    public Project(String name, double totalBudget, ArrayList<Category> categories, User admin) {
        if(name == null || totalBudget <= 0 || categories == null || admin == null) {
            throw new IllegalArgumentException("Project name and total budget and categories are required");
        }
        this.name = name;
        this.active = true;
        this.totalBudget = totalBudget;
        this.availableBalance = this.totalBudget;
        this.categories = categories;
        this.admin = admin;
        this.members = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.pendingQueue = new Queue<>();
        projectAssignation();
    }

    private void projectAssignation() {
        admin.setRole(Role.ADMIN);
        admin.setCurrentProject(this);
    }

    public String getName() {
        return name;
    }

    public double getTotalBudget() {
        return totalBudget;
    }

    public double getAvailableBalance() {
        return availableBalance;
    }

    public boolean isActive() {
        return active;
    }

    public User getAdmin() {
        return admin;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public ArrayList<ExpenseRequest> getRequests() {
        return requests;
    }

    public Queue<ExpenseRequest> getPendingQueue() {
        return pendingQueue;
    }

    public void addMember(User user) {
        if (!active) throw new IllegalStateException("Proyecto inactivo");
        if (user.equals(admin) || members.contains(user))
            throw new IllegalArgumentException("Usuario ya existente en este proyecto");
        if (user.hasProject())
            throw new IllegalArgumentException("Usuario ya existente en otro proyecto");
        if (!user.getPendingInvitations().contains(this))
            throw new IllegalStateException("El usuario no tiene invitación pendiente");
        members.add(user);
        user.setRole(Role.MEMBER);
        user.setCurrentProject(this);
    }

    public void removeMember(User user) {
        if (!members.contains(user)) {
            throw new IllegalArgumentException("El usuario no es miembro de este proyecto");
        }
        members.remove(user);
        user.setRole(Role.MEMBER);
        user.setCurrentProject(null);
    }

    public void transferAdmin(User newAdmin) {
        if (!members.contains(newAdmin)) {
            throw new IllegalArgumentException("Usuario no existente en este proyecto");
        }
        if (!newAdmin.getRole().equals(Role.MEMBER)) {
            throw new IllegalArgumentException("El nuevo administrador no puede ser ya administrador");
        }
        members.add(admin);
        admin.setRole(Role.MEMBER);
        newAdmin.setRole(Role.ADMIN);
        members.remove(newAdmin);
        this.admin = newAdmin;
    }

    public void inviteMember(User invitee) {
        if (invitee.hasProject()) {
            throw new IllegalArgumentException("Usuario existente en otro proyecto");
        }
        if (members.contains(invitee)) {
            throw new IllegalArgumentException("Usuario existente en este proyecto");
        }
        if (!admin.isInWorkCircle(invitee)) {
            throw new IllegalArgumentException("Solo puede invitar a personas en su circulo de trabajo");
        }
        invitee.receiveInvitation(this);
    }

    public void close() {
        if (this.isActive()) {
            for (User user : members) {
                user.setRole(Role.MEMBER);
                user.setCurrentProject(null);
            }
            members.clear();
            this.categories.clear();
            this.requests.clear();
            while (!pendingQueue.isEmpty()) {
                pendingQueue.pull();
            }
            this.admin.setRole(Role.MEMBER);
            this.admin.setCurrentProject(null);
            this.admin = null;
            this.active = false;
        }
    }

    public void submitRequest(ExpenseRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if (!active) {
            throw new IllegalStateException("Proyecto inactivo");
        }
        if (!members.contains(request.getRequester())) {
            throw new IllegalArgumentException("Solo los integrantes pueden enviar solicitudes");
        }
        if (!categories.contains(request.getCategory())) {
            throw new IllegalArgumentException("La categoría no pertenece a este proyecto");
        }
        if (request.getStatus() != RequestStatus.PENDING || requests.contains(request)) {
            throw new IllegalArgumentException("La solicitud no es válida para enviarse");
        }
        request.setId(requests.size());
        pendingQueue.push(request);
        requests.add(request);
    }

    public void approve(ExpenseRequest request, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("La razon no puede ser nula");
        }
        if (request == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if (pendingQueue.peek() != request) {
            throw new IllegalStateException("Debe resolver primero la solicitud más antigua");
        }
        if (!active) {
            throw new IllegalStateException("Proyecto inactivo");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("La solicitud ya fue resuelta: " + request.getStatus());
        }
        if (request.getAmount() > availableBalance) {
            throw new IllegalStateException("Saldo disponible insuficiente");
        }
        if (!request.getCategory().canAfford(request.getAmount())) {
            throw new IllegalStateException("La categoría no tiene presupuesto suficiente");
        }
        request.approve(reason);
        availableBalance -= request.getAmount();
        request.getCategory().addSpent(request.getAmount());
        pendingQueue.pull();
    }

    public void reject(ExpenseRequest request, String reason) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("La razon no puede ser nula");
        }
        if (pendingQueue.peek() != request) {
            throw new IllegalStateException("Debe resolver primero la solicitud más antigua");
        }
        request.reject(reason);
        pendingQueue.pull();
    }

    public double getTotalSpent() {
        return totalBudget - availableBalance;
    }

    public HashMap<Category, Double> getSpentByCategory() {
        HashMap<Category, Double> result = new HashMap<>();
        for (Category category : categories) {
            result.put(category, 0.0);
        }
        for (ExpenseRequest request : requests) {
            if (request.getStatus().equals(RequestStatus.APPROVED)) {
                Category category = request.getCategory();
                result.put(category, result.getOrDefault(category, 0.0) + request.getAmount());
            }
        }
        return result;
    }

    public HashMap<User, Double> getSpentByMember() {
        HashMap<User, Double> result = new HashMap<>();
        for (User member : members) {
            result.put(member, 0.0);
        }
        for (ExpenseRequest request : requests) {
            if (request.getStatus().equals(RequestStatus.APPROVED)) {
                User requester = request.getRequester();
                result.put(requester, result.getOrDefault(requester, 0.0) + request.getAmount());
            }
        }
        return result;
    }

    public ArrayList<ExpenseRequest> filterHistory(Category category, RequestStatus status, LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final");
        }
        ArrayList<ExpenseRequest> result = new ArrayList<>();
        for (ExpenseRequest request : requests) {
            LocalDate createdDate = request.getCreatedAt().toLocalDate();
            boolean matches = true;
            if (category != null && !category.equals(request.getCategory())) {
                matches = false;
            }
            if (status != null && !status.equals(request.getStatus())) {
                matches = false;
            }
            if (from != null && createdDate.isBefore(from)) {
                matches = false;
            }
            if (to != null && createdDate.isAfter(to)) {
                matches = false;
            }
            if (matches) {
                result.add(request);
            }
        }
        return result;
    }

    public ArrayList<ExpenseRequest> sortHistory(ArrayList<ExpenseRequest> requests, SortCriteria criteria) {
        if (requests == null || criteria == null) {
            throw new IllegalArgumentException("Las solicitudes y el criterio no pueden ser nulos");
        }
        Comparator<ExpenseRequest> comparator;
        if (criteria.equals(SortCriteria.AMOUNT_DESCENDING)) {
            comparator = Comparator.comparingDouble(ExpenseRequest::getAmount).reversed();
        } else if (criteria.equals(SortCriteria.CREATED_DATE)) {
            comparator = Comparator.comparing(ExpenseRequest::getCreatedAt);
        } else if (criteria.equals(SortCriteria.APPROVAL_DATE)) {
            comparator = Comparator.comparing(ExpenseRequest::getResolvedAt, Comparator.nullsLast(Comparator.naturalOrder()));
        } else {
            comparator = Comparator.comparing(request -> request.getRequester().getUsername());
        }
        comparator = comparator.thenComparingInt(ExpenseRequest::getId);
        BinaryTree<ExpenseRequest> tree = new BinaryTree<>(comparator);
        for (ExpenseRequest request : requests) {
            tree.add(request);
        }
        return tree.inOrderData();
    }

    public ArrayList<ExpenseRequest> getRequestsOf(User user) {
        ArrayList<ExpenseRequest> result = new ArrayList<>();
        for (ExpenseRequest request : requests) {
            if (request.getRequester().equals(user)) {
                result.add(request);
            }
        }
        return result;
    }
}

package co.edu.uptc.model;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.enums.RequestStatus;
import co.edu.uptc.enums.Role;
import co.edu.uptc.enums.SortCriteria;
import co.edu.uptc.structures.BinaryTree;
import co.edu.uptc.structures.Queue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

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
        if (name == null || totalBudget <= 0 || categories == null || admin == null) {
            throw new IllegalArgumentException(ErrorMessages.PROJECT_DATA_REQUIRED.getText());
        }
        this.name = name;
        this.active = true;
        this.totalBudget = totalBudget;
        this.availableBalance = this.totalBudget;
        this.categories = new ArrayList<>(categories);
        this.admin = admin;
        this.members = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.pendingQueue = new Queue<>();
        assignAdmin();
    }

    private void assignAdmin() {
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

    public double getTotalSpent() {
        return totalBudget - availableBalance;
    }

    public void addMember(User user) {
        validateNewMember(user);
        members.add(user);
        user.setRole(Role.MEMBER);
        user.setCurrentProject(this);
    }

    private void validateNewMember(User user) {
        if (!active) {
            throw new IllegalStateException(ErrorMessages.PROJECT_INACTIVE.getText());
        }
        if (user.equals(admin) || members.contains(user)) {
            throw new IllegalArgumentException(ErrorMessages.USER_ALREADY_IN_PROJECT.getText());
        }
        if (user.hasProject()) {
            throw new IllegalArgumentException(ErrorMessages.USER_IN_OTHER_PROJECT.getText());
        }
        if (!user.getPendingInvitations().contains(this)) {
            throw new IllegalStateException(ErrorMessages.NO_INVITATION_FOR_USER.getText());
        }
    }

    public void removeMember(User user) {
        if (!members.contains(user)) {
            throw new IllegalArgumentException(ErrorMessages.USER_NOT_MEMBER.getText());
        }
        members.remove(user);
        releaseUser(user);
    }

    public void transferAdmin(User newAdmin) {
        if (!members.contains(newAdmin)) {
            throw new IllegalArgumentException(ErrorMessages.USER_NOT_IN_PROJECT.getText());
        }
        if (!newAdmin.getRole().equals(Role.MEMBER)) {
            throw new IllegalArgumentException(ErrorMessages.ADMIN_ALREADY.getText());
        }
        members.add(admin);
        admin.setRole(Role.MEMBER);
        newAdmin.setRole(Role.ADMIN);
        members.remove(newAdmin);
        this.admin = newAdmin;
    }

    public void inviteMember(User invitee) {
        validateInvitee(invitee);
        invitee.receiveInvitation(this);
    }

    private void validateInvitee(User invitee) {
        if (!active) {
            throw new IllegalStateException(ErrorMessages.PROJECT_INACTIVE.getText());
        }
        if (invitee.hasProject()) {
            throw new IllegalArgumentException(ErrorMessages.INVITEE_IN_OTHER_PROJECT.getText());
        }
        if (members.contains(invitee)) {
            throw new IllegalArgumentException(ErrorMessages.INVITEE_IN_THIS_PROJECT.getText());
        }
        if (!admin.isInWorkCircle(invitee)) {
            throw new IllegalArgumentException(ErrorMessages.INVITEE_NOT_IN_CIRCLE.getText());
        }
    }

    public void close() {
        if (!active) {
            return;
        }
        members.forEach(this::releaseUser);
        members.clear();
        categories.clear();
        requests.clear();
        clearPendingQueue();
        releaseUser(admin);
        this.admin = null;
        this.active = false;
    }

    private void releaseUser(User user) {
        user.setRole(Role.MEMBER);
        user.setCurrentProject(null);
    }

    private void clearPendingQueue() {
        while (!pendingQueue.isEmpty()) {
            pendingQueue.pull();
        }
    }

    public void submitRequest(ExpenseRequest request) {
        validateSubmission(request);
        validateRequestData(request);
        request.setId(requests.size());
        pendingQueue.push(request);
        requests.add(request);
    }

    private void validateSubmission(ExpenseRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(ErrorMessages.REQUEST_NULL.getText());
        }
        if (!active) {
            throw new IllegalStateException(ErrorMessages.PROJECT_INACTIVE.getText());
        }
    }

    private void validateRequestData(ExpenseRequest request) {
        if (!members.contains(request.getRequester())) {
            throw new IllegalArgumentException(ErrorMessages.REQUESTER_NOT_MEMBER.getText());
        }
        if (!categories.contains(request.getCategory())) {
            throw new IllegalArgumentException(ErrorMessages.CATEGORY_NOT_IN_THIS_PROJECT.getText());
        }
        if (request.getStatus() != RequestStatus.PENDING || requests.contains(request)) {
            throw new IllegalArgumentException(ErrorMessages.REQUEST_NOT_SUBMITTABLE.getText());
        }
    }

    public void approve(ExpenseRequest request, String reason) {
        validateReason(reason);
        requireRequest(request);
        validatePending(request);
        validateFunds(request);
        request.approve(reason);
        availableBalance -= request.getAmount();
        request.getCategory().addSpent(request.getAmount());
        pendingQueue.pull();
    }

    public void reject(ExpenseRequest request, String reason) {
        requireRequest(request);
        validateReason(reason);
        validatePending(request);
        request.reject(reason);
        pendingQueue.pull();
    }

    private void validateReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(ErrorMessages.REASON_NULL.getText());
        }
    }

    private void requireRequest(ExpenseRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(ErrorMessages.REQUEST_NULL.getText());
        }
    }

    private void validatePending(ExpenseRequest request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException(ErrorMessages.REQUEST_ALREADY_RESOLVED.format(request.getStatus()));
        }
        if (pendingQueue.peek() != request) {
            throw new IllegalStateException(ErrorMessages.OLDEST_REQUEST_FIRST.getText());
        }
    }

    private void validateFunds(ExpenseRequest request) {
        if (!active) {
            throw new IllegalStateException(ErrorMessages.PROJECT_INACTIVE.getText());
        }
        if (request.getAmount() > availableBalance) {
            throw new IllegalStateException(ErrorMessages.INSUFFICIENT_BALANCE.getText());
        }
        if (!request.getCategory().canAfford(request.getAmount())) {
            throw new IllegalStateException(ErrorMessages.CATEGORY_WITHOUT_BUDGET.getText());
        }
    }

    public HashMap<Category, Double> getSpentByCategory() {
        return sumApprovedBy(categories, ExpenseRequest::getCategory);
    }

    public HashMap<User, Double> getSpentByMember() {
        return sumApprovedBy(members, ExpenseRequest::getRequester);
    }

    private <K> HashMap<K, Double> sumApprovedBy(List<K> keys, Function<ExpenseRequest, K> keyOf) {
        HashMap<K, Double> result = new HashMap<>();
        keys.forEach(key -> result.put(key, 0.0));
        for (ExpenseRequest request : requests) {
            if (request.getStatus().equals(RequestStatus.APPROVED)) {
                result.merge(keyOf.apply(request), request.getAmount(), Double::sum);
            }
        }
        return result;
    }

    public ArrayList<ExpenseRequest> filterHistory(Category category, RequestStatus status, LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException(ErrorMessages.INVALID_DATE_RANGE.getText());
        }
        ArrayList<ExpenseRequest> result = new ArrayList<>();
        for (ExpenseRequest request : requests) {
            if (matchesAttributes(request, category, status) && matchesDates(request, from, to)) {
                result.add(request);
            }
        }
        return result;
    }

    private boolean matchesAttributes(ExpenseRequest request, Category category, RequestStatus status) {
        return (category == null || category.equals(request.getCategory()))
                && (status == null || status.equals(request.getStatus()));
    }

    private boolean matchesDates(ExpenseRequest request, LocalDate from, LocalDate to) {
        LocalDate createdDate = request.getCreatedAt().toLocalDate();
        return (from == null || !createdDate.isBefore(from)) && (to == null || !createdDate.isAfter(to));
    }

    public ArrayList<ExpenseRequest> sortHistory(ArrayList<ExpenseRequest> toSort, SortCriteria criteria) {
        if (toSort == null || criteria == null) {
            throw new IllegalArgumentException(ErrorMessages.SORT_ARGUMENTS_NULL.getText());
        }
        BinaryTree<ExpenseRequest> tree = new BinaryTree<>(buildComparator(criteria).thenComparingInt(ExpenseRequest::getId));
        for (ExpenseRequest request : toSort) {
            tree.add(request);
        }
        return tree.inOrderData();
    }

    private Comparator<ExpenseRequest> buildComparator(SortCriteria criteria) {
        switch (criteria) {
            case AMOUNT_DESCENDING:
                return Comparator.comparingDouble(ExpenseRequest::getAmount).reversed();
            case CREATED_DATE:
                return Comparator.comparing(ExpenseRequest::getCreatedAt);
            case APPROVAL_DATE:
                return Comparator.comparing(ExpenseRequest::getResolvedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default:
                return Comparator.comparing(request -> request.getRequester().getUsername());
        }
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

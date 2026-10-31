package co.edu.uptc.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
        this.name = name;
        this.totalBudget = totalBudget;
        this.categories = categories;
        this.admin = admin;
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
        if(members.contains(user)){
            throw new IllegalArgumentException("Usuario ya existente en este proyecto");
        }
        if(user.hasProject()){
            throw new IllegalArgumentException("Usuario ya existente en otro proyecto");
        }
        members.add(user);
        user.setCurrenProject(this);
        user.setRole(Role.MEMBER);
    }

    public void removeMember(User user) {
        if(members.contains(user)){
            members.remove(user);
            user.setCurrenProject(null);
            user.setRole(null);
        }
    }

    public void transferAdmin(User newAdmin){
        if(!members.contains(newAdmin)){
            throw new IllegalArgumentException("Usuario no existente en este proyecto");
        }
        if(!newAdmin.getRole().equals(Role.MEMBER)){
            throw new IllegalArgumentException("El nuevo administrador no puede ser ya administrador");
        }
        newAdmin.setRole(Role.ADMIN);
        members.remove(newAdmin);
        this.admin = newAdmin;
    }

    public void inviteMember(User invitee){
        if(invitee.hasProject()){
            throw new IllegalArgumentException("Usuario existente en otro proyecto");
        }
        if(!members.contains(invitee)){
            throw new IllegalArgumentException("Usuario existente en este proyecto");
        }
        if(!admin.isInWorkcircle(invitee)){
            throw new IllegalArgumentException("Solo puede invitar a personas en su circulo de trabajo");
        }
        invitee.receiveInvitation(this);
    }

    public void close(){
        for(User user : members){
            user.setCurrenProject(null);
            user.setRole(null);
        }
        this.members.clear();
        this.categories.clear();
        this.requests.clear();
        this.pendingQueue = null;
        this.admin.setRole(null);
        this.admin.setCurrenProject(null);
        this.admin = null;
    }

    public void submitRequest(ExpenseRequest request){
        if(request == null){
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if(requests.contains(request)){
            throw new IllegalArgumentException("La solicitud ya fue enviada");
        }
        requests.add(request);
    }

    public void approve(ExpenseRequest request, String reason){

    }

    public void reject(ExpenseRequest request, String reason){

    }

    public double getTotalSpend(){
        return totalBudget -  availableBalance;
    }

    public HashMap<Category, Double> getSpentByCategory(){
        return null;
    }

    public HashMap<User, Double> getSpentByUser(){
        return null;
    }

    public ArrayList<ExpenseRequest> filterHistory(Category category, RequestStatus status, LocalDate from, LocalDate to){
        return null;
    }

    public ArrayList<ExpenseRequest> filterHistory(ArrayList<ExpenseRequest> requests, SortCriteria criteria){
        return null;
    }
}

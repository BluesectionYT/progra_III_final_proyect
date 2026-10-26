package co.edu.uptc.model;

import java.time.LocalDateTime;

public class ExpenseRequest {
    private int id;
    private String concept;
    private double amount;
    private Category category;
    private User requester;
    private RequestStatus status;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    
    public ExpenseRequest(String concept, double amount, Category category, User requester) {
        this.concept = concept;
        this.amount = amount;
        this.category = category;
        this.requester = requester;
    }

    public int getId() {
        return id;
    }

    public String getConcept() {
        return concept;
    }

    public double getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public User getRequester() {
        return requester;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void approve(String reason){
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio");
        }
        if (status != RequestStatus.PENDING) {  
            throw new IllegalStateException("solo se pueden aprovar solicitudes en estado PENDING. Estado actual: " + this.status);
        }

        this.status = RequestStatus.APPROVED;
        this.reason = reason;
        this.resolvedAt = LocalDateTime.now();
    }

    public void reject(String reason){
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio");
        }
        if (status != RequestStatus.PENDING) {  
            throw new IllegalStateException("solo se pueden aprovar solicitudes en estado PENDING. Estado actual: " + this.status);
        }

        this.status = RequestStatus.REJECTED;
        this.reason = reason;
        this.resolvedAt = LocalDateTime.now();
    }
}

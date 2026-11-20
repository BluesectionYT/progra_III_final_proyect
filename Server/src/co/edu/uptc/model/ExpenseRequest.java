package co.edu.uptc.model;

import co.edu.uptc.enums.ErrorMessages;
import co.edu.uptc.enums.RequestStatus;

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
        this.id = 0;
        this.concept = concept;
        this.amount = amount;
        this.category = category;
        this.requester = requester;
        this.createdAt = LocalDateTime.now();
        this.status = RequestStatus.PENDING;
        this.resolvedAt = null;
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

    public void setId(int id) {
        this.id = id;
    }

    public void approve(String reason) {
        resolve(RequestStatus.APPROVED, reason, ErrorMessages.ONLY_APPROVE_PENDING);
    }

    public void reject(String reason) {
        resolve(RequestStatus.REJECTED, reason, ErrorMessages.ONLY_REJECT_PENDING);
    }

    private void resolve(RequestStatus newStatus, String reason, ErrorMessages notPendingMessage) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(ErrorMessages.REASON_REQUIRED.getText());
        }
        if (status != RequestStatus.PENDING) {
            throw new IllegalStateException(notPendingMessage.format(this.status));
        }
        this.status = newStatus;
        this.reason = reason;
        this.resolvedAt = LocalDateTime.now();
    }
}

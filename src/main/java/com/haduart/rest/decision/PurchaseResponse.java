package com.haduart.rest.decision;


public class PurchaseResponse {
    private boolean accepted;
    private String reason;

    public PurchaseResponse() {
    }

    public PurchaseResponse(boolean accepted, String reason) {
        this.accepted = accepted;
        this.reason = reason;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PurchaseResponse that = (PurchaseResponse) o;

        if (accepted != that.accepted) return false;
        if (!reason.equals(that.reason)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (accepted ? 1 : 0);
        result = 31 * result + reason.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PurchaseResponse{" +
                "accepted=" + accepted +
                ", reason='" + reason + '\'' +
                '}';
    }
}

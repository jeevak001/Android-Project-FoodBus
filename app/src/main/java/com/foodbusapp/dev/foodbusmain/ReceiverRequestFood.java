package com.foodbusapp.dev.foodbusmain;

/**
 * Created by Jeeva on 3/11/2016.
 */
public class ReceiverRequestFood {


    String name;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    String donorName;

    public String getAmountLeft() {
        return amountLeft;
    }

    public void setAmountLeft(String amountLeft) {
        this.amountLeft = amountLeft;
    }

    String amountLeft;

    public String getDeclined() {
        return declined;
    }

    public void setDeclined(String declined) {
        this.declined = declined;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getDonorId() {
        return donorId;
    }

    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmountClaimed() {
        return amountClaimed;
    }

    public void setAmountClaimed(String amountClaimed) {
        this.amountClaimed = amountClaimed;
    }

    public String getAmountGot() {
        return amountGot;
    }

    public void setAmountGot(String amountGot) {
        this.amountGot = amountGot;
    }

    public String getClaimRequest() {
        return claimRequest;
    }

    public void setClaimRequest(String claimRequest) {
        this.claimRequest = claimRequest;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    String foodId;
    String donorId;
    String requestId;
    String time;
    String date;
    String amountClaimed;
    String amountGot;
    String claimRequest;
    String approved;
    String declined;
}

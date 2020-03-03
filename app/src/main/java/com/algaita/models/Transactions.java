package com.algaita.models;

public class Transactions {
    String ref, type, title, amount, status, ondate;

    public String getRef() {
        return ref;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getOndate() {
        return ondate;
    }

    public void setOndate(String ondate) {
        this.ondate = ondate;
    }

    public String getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public void setType(String type) {
        this.type = type;
    }

}

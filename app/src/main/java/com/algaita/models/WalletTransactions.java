package com.algaita.models;

public class WalletTransactions {
    String ref, type, title, amount, current_balance, status, ondate;

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


    public String getCurrent_balance() {
        return current_balance;
    }

    public void setCurrent_balance(String current_balance) {
        this.current_balance = current_balance;
    }
}

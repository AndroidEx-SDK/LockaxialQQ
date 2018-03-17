package com.androidex.service;

import java.util.Date;

class CardRecord {
    public String card = null;
    public Date creDate = null;

    public CardRecord() {
        this.card = "";
        this.creDate = new Date();
    }

    public boolean checkLastCard(String card) {
        boolean result = false;
        if (this.card.equals(card)) {
            long offset = new Date().getTime() - this.creDate.getTime();
            if (offset > 1000) {
                this.card = card;
                this.creDate = new Date();
            } else {
                result = true;
            }
        } else {
            this.card = card;
            this.creDate = new Date();
        }
        return result;
    }
}

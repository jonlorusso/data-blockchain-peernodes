package com.swatt.internal.rest;

import java.util.ArrayList;

public class TradeMessages {
    public String message;
    public ArrayList<TradeMessage> messages;

    public TradeMessages(String message) {
        this.message = message;

        this.messages = new ArrayList<TradeMessage>();
    }
}

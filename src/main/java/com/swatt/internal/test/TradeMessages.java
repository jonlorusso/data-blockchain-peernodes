package com.swatt.internal.test;

import java.util.ArrayList;

public class TradeMessages {
    public String message;
    public ArrayList<TradeMessage> messages;

    public TradeMessages(String message) {
        this.message = message;

        this.messages = new ArrayList<TradeMessage>();
    }
}

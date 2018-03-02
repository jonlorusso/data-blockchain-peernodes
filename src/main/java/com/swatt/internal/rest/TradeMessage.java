package com.swatt.internal.rest;

public class TradeMessage {
    public String type;
    public String symbol;
    public Long quantity;
    public Double price;

    public TradeMessage() {
        super();
    }

    public TradeMessage(String type, String symbol, Long quantity, Double price) {
        super();

        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
    }
}
package com.swatt.internal.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Toy {
    public Toy() {
    }

    public String getMessages() {
        TradeMessages tradesMessage = new TradeMessages("test message");

        tradesMessage.messages.add(new TradeMessage("SELL", "BTC", new Long(10), new Double(124)));
        tradesMessage.messages.add(new TradeMessage("BUY", "BTC", new Long(12), new Double(120)));
        tradesMessage.messages.add(new TradeMessage("SELL", "ETH", new Long(8), new Double(19)));
        tradesMessage.messages.add(new TradeMessage("BUY", "ETH", new Long(8), new Double(14)));
        tradesMessage.messages.add(new TradeMessage("CNCL", "BTC", new Long(0), new Double(129)));

        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tradesMessage);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }

        return json;
    }
}

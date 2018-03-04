package com.swatt.internal.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;

public class Toy {
    public static void main(String[] args) {
        Javalin app = Javalin.create().port(7000).enableCorsForOrigin("*").start();

        app.get("/buy/:symbol/:quantity/:price", ctx -> {
            ctx.result("Buy received " + ctx.param("symbol") + ':' + ctx.param("quantity") + ':' + ctx.param("price"));
        });

        app.get("/sell/:symbol/:quantity/:price", ctx -> {
            ctx.result("Sell received " + ctx.param("symbol") + ':' + ctx.param("quantity") + ':' + ctx.param("price"));
        });

        app.get("/cancel/:symbol", ctx -> {
            ctx.result("Cancel received " + ctx.param("symbol"));
        });

        app.get("/getAll", ctx -> {
            Toy tradesToy = new Toy();
            String trades = tradesToy.getMessages();
            ctx.result(trades);
        });
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

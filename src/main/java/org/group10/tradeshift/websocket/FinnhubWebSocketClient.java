package org.group10.tradeshift.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.group10.tradeshift.services.PortfolioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;



@Component
@RequiredArgsConstructor
public class FinnhubWebSocketClient extends TextWebSocketHandler {

    private final PortfolioService portfolioService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private WebSocketSession session;

    @Value("${finnhub.api-key}")
    private String finnhubApiKey;

    @PostConstruct
    public void connect() {
        String uriString = "wss://ws.finnhub.io?token=" + finnhubApiKey;
        StandardWebSocketClient client = new StandardWebSocketClient();
        client.execute(this, null, java.net.URI.create(uriString))
                .thenAccept(sess -> this.session = sess)
                .exceptionally(ex -> {
                    System.err.println("WebSocket error: " + ex.getMessage());
                    return null;
                });
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.session = session;
        sendMessage("{\"type\":\"subscribe\",\"symbol\":\"AAPL\"}");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        JsonNode root = objectMapper.readTree(payload);
        String type = root.path("type").asText();

        if ("trade".equals(type)) {
            List<Map<String, Object>> data = objectMapper.readValue(
                    root.path("data").traverse(),
                    new TypeReference<>() {}
            );
            for (Map<String, Object> trade : data) {
                String symbol = (String) trade.get("s");
                double price = ((Number) trade.get("p")).doubleValue();

                portfolioService.updateStockPrice(symbol, price);
            }
        }
    }

    private void sendMessage(String msg) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(msg));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        this.session = null;
    }


    public void subscribe(String symbol) {
        sendMessage("{\"type\":\"subscribe\",\"symbol\":\"" + symbol + "\"}");
    }
}
//package org.group10.tradeshift.websocket;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//import org.springframework.web.socket.WebSocketSession;
//
//import javax.annotation.PostConstruct;
//import java.util.HashSet;
//import java.util.Set;
//
//@Component
//public class FinnhubWebSocketClient extends TextWebSocketHandler {
//    @Autowired private SimpMessagingTemplate template;  // Our broadcaster
//    @Value("${finnhub.ws-url}") private String wsUrl;
//    @Value("${finnhub.api-key}") private String apiKey;
//
//    private WebSocketSession session;
//    private final Set<String> subscribedSymbols = new HashSet<>();  // Track active subs
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @PostConstruct
//    public void connect() {
//        StandardWebSocketClient client = new StandardWebSocketClient();
//        client.doHandshake(this, wsUrl + "?token=" + apiKey).addCallback(
//                result -> { session = result.getResponse().getSession(); /* Subscribe to defaults */ subscribe("AAPL"); },
//                ex -> { /* Reconnect logic */ ex.printStackTrace(); }
//        );
//    }
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, org.springframework.web.socket.TextMessage message) throws Exception {
//        // Parse Finnhub message: e.g., {"type":"trade","data":[{"s":"AAPL","p":150.25,"t":1730300000,"v":100}]}
//        Map<String, Object> payload = mapper.readValue(message.getPayload(), Map.class);
//        if ("trade".equals(payload.get("type"))) {  // Or "quote" for bid/ask
//            List<Map<String, Object>> trades = (List) payload.get("data");
//            trades.forEach(trade -> {
//                String symbol = (String) trade.get("s");
//                Double price = (Double) trade.get("p");
//                // Broadcast: {symbol: "AAPL", price: 150.25, timestamp: ...}
//                template.convertAndSend("/topic/prices", Map.of("symbol", symbol, "price", price));
//            });
//            @Autowired private PortfolioService portfolioService;
//            portfolioService.recalculateOnPriceChange(symbol, price);
//        }
//    }
//
//    // Called by TradingService or on portfolio load
//    public void subscribe(String symbol) {
//        if (!subscribedSymbols.contains(symbol) && session != null && session.isOpen()) {
//            Map<String, Object> subMsg = Map.of("type", "subscribe", "symbol", symbol);
//            session.sendMessage(new org.springframework.web.socket.TextMessage(mapper.writeValueAsString(subMsg)));
//            subscribedSymbols.add(symbol);
//        }
//    }
//
//    public void unsubscribe(String symbol) {
//        // Similar: {"type":"unsubscribe","symbol":"AAPL"}
//        // ...
//    }
//}

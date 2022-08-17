package com.structure.fi.cryptoexchange.util.websocketutil;

import com.google.common.reflect.TypeToken;
import com.structure.fi.cryptoexchange.model.response.SymbolStats;
import com.structure.fi.cryptoexchange.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WebSocketUtil {
    private final Logger logger = LoggerFactory.getLogger(WebSocketUtil.class);

    @Value("${webStream.pollInterval}")
    private int pollInterval;
    private String requestUrl;

    private String symbol;
    private volatile Map<String, Object> messageMap = new HashMap<>();

    public void setPollInterval(int pollInterval) {
        this.pollInterval = pollInterval;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public WebSocketUtil() {
    }

    public WebSocketUtil(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public WebSocketUtil(String requestUrl, int pollInterval) {
        this.requestUrl = requestUrl;
        this.pollInterval = pollInterval;
    }

    public WebSocketUtil(String symbol, String requestUrl) {
        this.requestUrl = requestUrl;
    }

    private volatile Map<String, Integer> countMap = new HashMap<>();
    private volatile Map<String, Double> priceMap = new HashMap<>();
    private volatile Map<String, List<Double>> priceListMap = new HashMap<>();

    private void setSymbolMap(Map<String, Object> map)
    {
        Map<String, Object> stMap = (Map<String, Object>) map.get("data");
        String symbol = (String) stMap.get("s");
        Double price = Double.valueOf((String) stMap.get("p"));
        priceMap.put(symbol, price);
        countMap.put(symbol, countMap.getOrDefault(symbol, 0) + 1);
        List<Double> priceList = priceListMap.getOrDefault(symbol, new ArrayList<>());
        priceList.add(price);
        priceListMap.put(symbol, priceList);
    }

    public SymbolStats getSymbolStats() {
        SymbolStats symbolStats = new SymbolStats();
        symbolStats.setSymbol(symbol);
        if (countMap.get(symbol) != null)
        {
            symbolStats.setFrequency(countMap.get(symbol));
            symbolStats.setMedian(Util.findMedian(priceListMap.get(symbol)));
            symbolStats.setMostRecentPrice(priceMap.get(symbol));
        }
        return symbolStats;
    }

    public void retrieveWebSocketStream() {
        try
        {
            // open websocket
            WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI(requestUrl));

            // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    Type listType = new TypeToken<HashMap<String, Object>>() {
                    }.getType();
                    messageMap = (Map<String, Object>) Util.deserializeXMLtoObject(message, listType);
                    setSymbolMap(messageMap);
                    logger.info(message);
                }
            });

            while (true)
            {
                // wait 10 seconds for messages from websocket
                Thread.sleep(pollInterval * 1000);
                priceListMap.forEach((symbol, priceList) -> {
                    double medianPrice = Util.findMedian(priceListMap.get(symbol));
                    priceList.clear();
                    priceList.add(medianPrice);
                });
            }
//            clientEndPoint.closeSession();
        } catch (InterruptedException ex)
        {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex)
        {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }
}

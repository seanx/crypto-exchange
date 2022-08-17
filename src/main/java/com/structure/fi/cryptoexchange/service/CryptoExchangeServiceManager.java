package com.structure.fi.cryptoexchange.service;

import com.structure.fi.cryptoexchange.model.response.SymbolStats;
import com.structure.fi.cryptoexchange.model.response.Symbols;
import com.structure.fi.cryptoexchange.util.RestUtil;
import com.structure.fi.cryptoexchange.util.websocketutil.WebSocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CryptoExchangeServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(CryptoExchangeServiceManager.class);

    @Value("${binance.com.url}")
    private String exchangeInfoURL;
    @Value("${binance.com.readtimeout}")
    private String readTimeOut;
    @Value("${binance.com.connecttimeout}")
    private String connectTimeOut;
    private Symbols symbols = new Symbols();
    @Value("${marketStream.url}")
    private String marketStreamURL;
    @Value("${maximum.number.combined.symbols}")
    private String maxSymbolNo;
    @Value("${webStream.pollInterval}")
    private int pollInterval;

    WebSocketUtil webSocketUtil;

    public void setPollInterval(int pollInterval) {
        this.pollInterval = pollInterval;
    }

    public void setExchangeInfoURL(String exchangeInfoURL) {
        this.exchangeInfoURL = exchangeInfoURL;
    }

    public void setReadTimeOut(String readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public void setConnectTimeOut(String connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public void setMarketStreamURL(String marketStreamURL) {
        this.marketStreamURL = marketStreamURL;
    }

    public void setMaxSymbolNo(String maxSymbolNo) {
        this.maxSymbolNo = maxSymbolNo;
    }


    public Symbols retrieveExchangeSymbolList() {
        if (symbols.getSymbols() == null || symbols.getSymbols().size() == 0)
        {
            RestTemplate restTemplate = RestUtil.getRestTemplate(Integer.parseInt(connectTimeOut) * 1000, Integer.parseInt(readTimeOut) * 1000);
            Map<String, Object> exchangeInfo = RestUtil.readMapFromURL(restTemplate, exchangeInfoURL);
            List<Map<String, Object>> symbolObjectList = (List) exchangeInfo.get("symbols");
            List<String> symbolList = new ArrayList<>();
            symbolObjectList.forEach(symbolObject -> symbolList.add((String) symbolObject.get("symbol")));
            symbols.setSymbols(symbolList);
        }
        return symbols;
    }

    @Async("startAsynProcessExecutor")
    public void retrieveMarketStreams() {
        String combinedStreamUrl = buildCombinedUrl();
        if (webSocketUtil == null)
        {
            webSocketUtil = new WebSocketUtil(combinedStreamUrl, pollInterval);
            webSocketUtil.retrieveWebSocketStream();
        }
    }

    public SymbolStats getSymbolStats(String symbol)
    {
        if (webSocketUtil == null)
        {
//            String combinedStreamUrl = buildCombinedUrl();
            webSocketUtil = new WebSocketUtil();
        }
        webSocketUtil.setSymbol(symbol);
        return webSocketUtil.getSymbolStats();
    }

    public String buildRawStreamUrl(String symbol) {
        return marketStreamURL + "ws/" + symbol.toLowerCase() + "@trade";
    }

    public String buildCombinedUrl()
    {
        if (symbols.getSymbols() == null || symbols.getSymbols().size() == 0)
        {
            symbols = retrieveExchangeSymbolList();
        }
        List<String> symbolList = symbols.getSymbols();
        String combinedStreamsUrl = marketStreamURL + "stream?streams=";
        int maxCombinedNo = Integer.parseInt(maxSymbolNo);
        for (int i = 0; i < maxCombinedNo; i++)
        {
            combinedStreamsUrl += symbolList.get(i).toLowerCase() + "@trade/";
        }
        return combinedStreamsUrl.substring(0, combinedStreamsUrl.length() - 1);
    }
}

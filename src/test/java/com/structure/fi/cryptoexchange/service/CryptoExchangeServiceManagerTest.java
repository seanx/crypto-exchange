package com.structure.fi.cryptoexchange.service;

import org.junit.jupiter.api.Test;

class CryptoExchangeServiceManagerTest {
    private final CryptoExchangeServiceManager serviceManager = new CryptoExchangeServiceManager();

    @Test
    public void testRetrieveExchangeSymbolList(){
        serviceManager.setExchangeInfoURL("https://api.binance.com/api/v3/exchangeInfo");
        serviceManager.setReadTimeOut("15");
        serviceManager.setConnectTimeOut("15");
        serviceManager.retrieveExchangeSymbolList();
    }

    @Test
    public void testBuildCombinedUrl(){
        serviceManager.setExchangeInfoURL("https://api.binance.com/api/v3/exchangeInfo");
        serviceManager.setReadTimeOut("15");
        serviceManager.setConnectTimeOut("15");
        serviceManager.setMaxSymbolNo("2");
        serviceManager.setMarketStreamURL("wss://stream.binance.com:9443/");
        serviceManager.buildCombinedUrl();
    }
}
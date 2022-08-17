package com.structure.fi.cryptoexchange;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import io.swagger.annotation.Api;


@Configuration
public class CryptoExchangeConfig {

    @Bean
    public String getBinanceURL() {
        return binanceURL;
    }

    @Bean
    public String getBinanceReadTimeOut() {
        return binanceReadTimeOut;
    }

    @Bean
    public String getBinanceConnectTImeOut() {
        return binanceConnectTImeOut;
    }

    @Value("${binance.com.url}")
    private String binanceURL;

    @Value("${binance.com.readtimeout}")
    private String binanceReadTimeOut;

    @Value("${binance.com.connecttimeout}")
    private String binanceConnectTImeOut;
}

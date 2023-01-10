package com.structure.fi.cryptoexchange.util;

import com.google.common.reflect.TypeToken;
import com.structure.fi.cryptoexchange.exception.CryptExchangeException;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

import static com.structure.fi.cryptoexchange.util.Util.deserializeXMLtoObject;

public class RestUtil {
    private static final Logger logger = LoggerFactory.getLogger(RestUtil.class);

    public static RestTemplate getRestTemplate(int connectTimeout, int readTimeout)
    {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectionRequestTimeout(connectTimeout);
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return new RestTemplate(requestFactory);
    }

    public static Map<String, Object> readMapFromURL(RestTemplate restTemplate, String requestUrl)
    {
        Type listType = new TypeToken<HashMap<String, Object>>(){}.getType();
        return (Map<String, Object>)deserializeXMLtoObject(readTextFromURL(restTemplate, requestUrl),listType);
    }

    public static HttpEntity buildHttpEntity()
    {
        // create headers
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(new MediaType("application", "json"));
        mediaTypeList.add(new MediaType("text", "plain"));
        headers.setAccept(mediaTypeList);

        return new HttpEntity(headers);
    }

    public static RestTemplate getRestTemplate(String username, String password, final HttpHost httpHost, int connectionTimeout, int readTimeout)
    {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
        SSLContext sslContext;
        try{
            sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        }catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e)
        {
            throw new CryptExchangeException("Unable to create RestTemplate", e);
        }

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();

        HttpComponentsClientHttpRequestFactory customRequestFactory  = new HttpComponentsClientHttpRequestFactory();
        customRequestFactory.setHttpClient(httpClient);
        customRequestFactory.setConnectTimeout(connectionTimeout * 1000);
        customRequestFactory.setReadTimeout(readTimeout * 1000);
        return new RestTemplate(customRequestFactory);
    }

    public static HttpEntity buildHttpEntity(String username, String password, Object reqObj)
    {
        HttpHeaders headers = getBasicAuthHeader(username,password);
        return new HttpEntity(reqObj, headers);
    }

    public static Map<String, Object> readMapFromURLWithRetry(String username, String password, String requestUrl, RestTemplate restTemplate, int numberOfRetry, int timeTowait)
    {
        HttpEntity entity = buildHttpEntity(username,password,null);
        Type listType = new TypeToken<HashMap<String, Object>>(){}.getType();
        return (Map<String, Object>)deserializeXMLtoObject(readTxtFromURLWithRetry(entity, requestUrl, restTemplate, numberOfRetry, timeTowait), listType);
    }

    public static String readTxtFromURLWithRetry(HttpEntity entity, String requestUrl, RestTemplate restTemplate, int numberOfRetry, int timeToWait)
    {
        int totalRetry = numberOfRetry;
        ResponseEntity<String> response = null;

        //make an http get request with headers
        while(numberOfRetry >= 0)
        {
            numberOfRetry--;
            try
            {
                response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);
            }catch (ResourceAccessException | HttpServerErrorException e)
            {
                if(numberOfRetry == 0)
                {
                    String errorMsg = "Failed to retrieve data from " + requestUrl + " after " + totalRetry + " times retry.";
                    throw new CryptExchangeException(errorMsg, e);
                }else{
                    logger.error("Connection time out while retrieving data from " + requestUrl + ", retry " + numberOfRetry + " time in " + timeToWait + " second",e);
                }

                try
                {
                    Thread.sleep(timeToWait * 1000);
                }catch (InterruptedException ignored)
                {}
                // if error occurs, go to next loop
                continue;
            }
            //if no error, break retry loop
            break;
        }

        //check response
        if(response.getStatusCode() == HttpStatus.OK)
        {
            logger.debug("Successfully retrieved data from: " + requestUrl);
            logger.debug(response.getBody());
        }else {
            logger.error("Failed retrieving data from " + requestUrl);
            logger.error(String.valueOf(response.getStatusCode()));
            throw new CryptExchangeException("Error occur while accessing data from " + requestUrl +". Error code: " + response.getStatusCode());
        }
        return response.getBody();
    }



    private static HttpHeaders getBasicAuthHeader(String username, String password)
    {
        //create headers
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(new MediaType("application", "json"));
        mediaTypes.add(new MediaType("text", "plain"));
        headers.setAccept(mediaTypes);

        //basic auth
        if(username != null && password != null)
        {
            char[] caPassword = password.toCharArray();
            byte[] passwordBytes = (username + ":" + new String(caPassword)).getBytes();
            byte[] base64CredsBytes = Base64.getEncoder().encode(passwordBytes);
            String base64Creds = new String(base64CredsBytes);
            headers.add("Authorization", "Basic " + base64Creds);
        }
        return headers;
    }

    public static String readTextFromURL(RestTemplate restTemplate, String requestUrl)
    {
        HttpEntity entity = buildHttpEntity();
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);

        // check response
        if (response.getStatusCode() == HttpStatus.OK)
        {
            logger.debug("Successfully retrieving data from: " + requestUrl);
            logger.debug(response.getBody());
        } else
        {
            logger.error("Failed retrieving data from: " + requestUrl);
            logger.error(String.valueOf(response.getStatusCode()));
        }
        return response.getBody();
    }
}

package com.structure.fi.cryptoexchange.util;

import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

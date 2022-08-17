package com.structure.fi.cryptoexchange;

import com.structure.fi.cryptoexchange.model.response.SymbolStats;
import com.structure.fi.cryptoexchange.model.response.Symbols;
import com.structure.fi.cryptoexchange.service.CryptoExchangeServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;


@RestController
public class CryptoExchangeController {
    private static final Logger logger = LoggerFactory.getLogger(CryptoExchangeController.class);

    @Autowired
    private CryptoExchangeServiceManager serviceManager;

    @ApiOperation(value = "Get the list of symbols", response = Symbols.class)
    @RequestMapping(value = "/symbols", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Symbols retrieveExchangeSymbolList() {
        logger.info("Client request the list of exchange symbols");
        if (serviceManager == null) serviceManager = new CryptoExchangeServiceManager();
        return serviceManager.retrieveExchangeSymbolList();
    }

    @ApiOperation(value = "Get the list of symbos statistics", response = SymbolStats.class)
    @RequestMapping(value = "/{symbol}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public SymbolStats retrieveMarketStreamStatistics(@PathVariable String symbol){
        logger.info("Client request the snapshot of current market stream");
        if (serviceManager == null)
            serviceManager = new CryptoExchangeServiceManager();
//       return serviceManager.retrieveMarketStreams(symbol);
//        serviceManager.retrieveMarketStreams();
        return serviceManager.getSymbolStats(symbol);
    }

    @ApiOperation(value = "Get the list of symbos statistics", response = HttpStatus.class)
    @RequestMapping(value = "/execute", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public HttpStatus executeMarketStream(){
        logger.info("Client request the snapshot of current market stream");
        if (serviceManager == null)
            serviceManager = new CryptoExchangeServiceManager();
//       return serviceManager.retrieveMarketStreams(symbol);
        serviceManager.retrieveMarketStreams();
        return HttpStatus.OK;
    }
}

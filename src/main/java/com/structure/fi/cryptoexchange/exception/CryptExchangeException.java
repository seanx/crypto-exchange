package com.structure.fi.cryptoexchange.exception;

import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.sql.SQLException;

public class CryptExchangeException extends RuntimeException {
    public CryptExchangeException(String message)
    {
        super(message);
    }

    public CryptExchangeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CryptExchangeException(Throwable e)
    {
        super(e);
    }

    public ExceptionEnum getExceptionEnum()
    {
        Throwable cause = this.getCause();
        if (cause != null)
        {
            if (cause instanceof SQLException)
            {
                return ExceptionEnum.CEDBException;
            } else if (cause instanceof IOException)
            {
                return ExceptionEnum.CEIOException;
            } else if (cause instanceof ClassNotFoundException)
            {
                return ExceptionEnum.CECLASSNOTFOUNDException;
            } else if (cause instanceof RestClientException)
            {
                return ExceptionEnum.CECONNECTException;
            } else
            {
                return ExceptionEnum.CEException;
            }
        }
        return null;
    }
}

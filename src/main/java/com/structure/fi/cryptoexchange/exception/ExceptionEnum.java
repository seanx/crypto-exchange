package com.structure.fi.cryptoexchange.exception;

import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.sql.SQLException;

public enum ExceptionEnum {
    CEDBException(SQLException.class, ErrorCode.Code_001),
    CEIOException(IOException.class, ErrorCode.Code_002),
    CECONNECTException(RestClientException.class, ErrorCode.Code_003),
    CECLASSNOTFOUNDException(ClassNotFoundException.class, ErrorCode.Code_004),
    CEException(CryptExchangeException.class, ErrorCode.Code_005);

    private Class<?> excClass;
    private ErrorCode errorCode;

    public ErrorCode getErrorCodeEnum() {
        return errorCode;
    }

    public String getErrorCode()
    {
        return errorCode.getErrorCode();
    }

    public String getErrorMsg() {
        return errorCode.getErrorMsg();
    }

    ExceptionEnum(Class excClass, ErrorCode errorCode)
    {
        this.excClass = excClass;
        this.errorCode = errorCode;
    }


    private enum ErrorCode {
        Code_001("001", "Error occurred while retrieving data from database"),
        Code_002("002", "Error occurred while reading data from file system, potential NAS mounting issue"),
        Code_003("003", "Error occurred while connecting to external web service"),
        Code_004("004", "Unable to load dependency library"),
        Code_005("005", "Unknown application error occurred");

        private String errorCode;
        private String errorMsg;

        private String getErrorCode() {
            return errorCode;
        }

        private String getErrorMsg() {
            return errorMsg;
        }

        ErrorCode(String errorCode, String errorMsg)
        {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }
    }
}

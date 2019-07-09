package org.wisdom.ApiResult;

import java.io.Serializable;

public class ResultSupport implements Serializable{
    private String message;
    private int StatusCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
    }
}

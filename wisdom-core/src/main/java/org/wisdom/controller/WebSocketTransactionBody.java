package org.wisdom.controller;


import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WebSocketTransactionBody {
    private byte[] hash;
    private int status;
    private Object data;
}

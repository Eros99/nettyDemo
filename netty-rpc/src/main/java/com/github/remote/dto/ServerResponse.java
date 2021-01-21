package com.github.remote.dto;

import lombok.Data;

@Data
public class ServerResponse {

    private Long id;
    private String code;
    private String msg;
    private Object result;

    public static ServerResponse success(Object result) {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.result = result;
        serverResponse.code = "200";
        serverResponse.msg = "success";
        return serverResponse;
    }

    public static ServerResponse fail(String code, String msg) {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.code = code;
        serverResponse.msg = msg;
        return serverResponse;
    }


}

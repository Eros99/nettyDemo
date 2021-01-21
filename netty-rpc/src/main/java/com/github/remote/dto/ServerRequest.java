package com.github.remote.dto;


import lombok.Data;

@Data
public class ServerRequest {

    private Long id;
    private Object content;
    private String methodName;
}

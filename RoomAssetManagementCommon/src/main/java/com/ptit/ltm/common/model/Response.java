package com.ptit.ltm.common.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean success;
    private Object data;
    private String message;
}
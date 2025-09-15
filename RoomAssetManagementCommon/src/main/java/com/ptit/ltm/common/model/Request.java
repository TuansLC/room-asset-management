package com.ptit.ltm.common.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private RequestType type;
    private Object data;
}
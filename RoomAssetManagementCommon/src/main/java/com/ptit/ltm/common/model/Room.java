package com.ptit.ltm.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private String code;
    private String name;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
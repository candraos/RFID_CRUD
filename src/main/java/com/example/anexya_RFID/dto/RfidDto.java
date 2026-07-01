package com.example.anexya_RFID.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfidDto {

    
    private String siteName;
    
    private String epc;

    private String location;

    private int rssi;
}

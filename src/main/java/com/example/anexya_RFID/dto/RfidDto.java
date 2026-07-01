package com.example.anexya_RFID.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RfidDto {

    @NotBlank(message = "Site name is required")
    private String siteName;

    @NotBlank(message = "EPC is required")
    private String epc;

    @NotBlank(message = "Location is required")
    private String location;

    @Min(value = 0, message = "RSSI cannot be less than 0")
    private int rssi;
}

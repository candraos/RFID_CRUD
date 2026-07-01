package com.example.anexya_RFID.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rfid_tags")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RFIDTag {

   
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tid")
    private UUID TID;

    @NotBlank(message = "Site name is required")
    @Column(name = "site_name", nullable = false)
    private String siteName;

    
    @NotBlank(message = "EPC is required")
    @NotNull(message = "EPC is required")
    private String epc;

    @NotBlank(message = "Location is required")
    @NotNull(message = "Location is required")
    private String location;

    
    @Min(value = 0, message = "RSSI cannot be less than 0")
    @NotNull(message = "RSSI is required")
    private int rssi;

    @NotNull(message = "Date is required")
    private LocalDateTime date;
}

package com.example.anexya_RFID.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Payload for creating or updating an RFID tag read")
public class RfidDto {

    @Schema(description = "Name of the site where the tag was read", example = "Warehouse A", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Site name is required")
    private String siteName;

    @Schema(description = "Electronic Product Code, must be unique across tags", example = "E28011606000021D3A2A1B2C", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "EPC is required")
    private String epc;

    @Schema(description = "Physical location of the reader/tag within the site", example = "Dock Door 3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Location is required")
    private String location;

    @Schema(description = "Received Signal Strength Indicator of the tag read, must be >= 0", example = "45")
    @Min(value = 0, message = "RSSI cannot be less than 0")
    private int rssi;
}

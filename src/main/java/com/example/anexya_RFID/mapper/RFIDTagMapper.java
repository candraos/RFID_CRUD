package com.example.anexya_RFID.mapper;

import com.example.anexya_RFID.dto.RfidDto;
import com.example.anexya_RFID.model.RFIDTag;
import org.springframework.stereotype.Component;

@Component
public class RFIDTagMapper {

    

    public RFIDTag toEntity(RfidDto dto) {
        RFIDTag tag = RFIDTag.builder()
            .siteName(dto.getSiteName())
            .epc(dto.getEpc())
            .location(dto.getLocation())
            .rssi(dto.getRssi())
            .date(java.time.LocalDateTime.now())
            .build();
        return tag;
    }
}

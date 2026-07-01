package com.example.anexya_RFID.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.example.anexya_RFID.model.RFIDTag;
import com.example.anexya_RFID.repository.RFIDTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RFIDTagService {

    private final RFIDTagRepository rfidTagRepository;

   

    public Optional<RFIDTag> findByTid(UUID tid) {
        return rfidTagRepository.findById(tid);
    }

    public RFIDTag create(RFIDTag rfidTag) {
        return rfidTagRepository.save(rfidTag);
    }

    public RFIDTag updateByTid(UUID tid, RFIDTag updated) throws NotFoundException {
        RFIDTag existing = rfidTagRepository.findById(tid).orElseThrow(NotFoundException::new);
        RFIDTag updatedTag = RFIDTag.builder()
                .TID(existing.getTID())
                .siteName(updated.getSiteName())
                .epc(updated.getEpc())
                .location(updated.getLocation())
                .rssi(updated.getRssi())
                .date(existing.getDate())
                .build();
        return rfidTagRepository.save(updatedTag);
    }

    public boolean deleteByTid(UUID tid) {
        if (!rfidTagRepository.existsById(tid)) {
            return false;
        }
        rfidTagRepository.deleteById(tid);
        return true;
    }
}

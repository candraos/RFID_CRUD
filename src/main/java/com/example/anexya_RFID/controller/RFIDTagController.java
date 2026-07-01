package com.example.anexya_RFID.controller;

import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.anexya_RFID.dto.RfidDto;
import com.example.anexya_RFID.mapper.RFIDTagMapper;
import com.example.anexya_RFID.model.RFIDTag;
import com.example.anexya_RFID.service.RFIDTagService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rfidtagcontroller")
@RequiredArgsConstructor
public class RFIDTagController {

    private final RFIDTagService rfidTagService;
    private final RFIDTagMapper rfidTagMapper;

    @GetMapping("/get/{tid}")
    public ResponseEntity<RFIDTag> getByTid(@PathVariable UUID tid) {
        RFIDTag tag = rfidTagService.findByTid(tid).orElse(null);
        if (tag != null) {
            return new ResponseEntity<>(tag, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<RFIDTag> createRFID(@Valid @RequestBody RfidDto rfidDto) {
        RFIDTag entity = rfidTagService.create(rfidTagMapper.toEntity(rfidDto));
        return new ResponseEntity<>(entity, HttpStatus.CREATED);
    }

    @PutMapping("/update/{tid}")
    public ResponseEntity<RFIDTag> updateByTid(@PathVariable UUID tid, @Valid @RequestBody RfidDto rfidDto) throws Exception {
        try {
            RFIDTag updatedEntity = rfidTagService.updateByTid(tid, rfidTagMapper.toEntity(rfidDto));
            return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{tid}")
    public ResponseEntity<Void> deleteByTid(@PathVariable UUID tid) {
        try{
            
            return rfidTagService.deleteByTid(tid)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }
}

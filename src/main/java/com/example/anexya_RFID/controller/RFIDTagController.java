package com.example.anexya_RFID.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.anexya_RFID.dto.RfidDto;
import com.example.anexya_RFID.mapper.RFIDTagMapper;
import com.example.anexya_RFID.model.RFIDTag;
import com.example.anexya_RFID.service.RFIDTagService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "RFID Tags", description = "Endpoints for creating, retrieving, updating and deleting RFID tag reads")
@RestController
@RequestMapping("/rfidtag")
@RequiredArgsConstructor
public class RFIDTagController {

    private final RFIDTagService rfidTagService;
    private final RFIDTagMapper rfidTagMapper;

    @Operation(
        summary = "Get an RFID tag by TID",
        description = "Retrieves a single RFID tag read by its unique tag identifier (TID). "
            
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tag found",
            content = @Content(schema = @Schema(implementation = RFIDTag.class))),
        @ApiResponse(responseCode = "404", description = "No tag exists with the given TID", content = @Content)
    })
    @GetMapping("/get/{tid}")
    public ResponseEntity<RFIDTag> getByTid(
        @Parameter(description = "UUID of the RFID tag (TID)", required = true)
        @PathVariable UUID tid
    ) {
        RFIDTag tag = rfidTagService.findByTid(tid).orElse(null);
        if (tag != null) {
            return new ResponseEntity<>(tag, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Create a new RFID tag read",
        description = "Registers a new RFID tag scan with site name, EPC, location and RSSI. "
            + "Typical use case: an RFID reader posting a new tag detection event. "
            + "The EPC must be unique; attempting to create a tag with a duplicate EPC returns 409 Conflict."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Tag created successfully",
            content = @Content(schema = @Schema(implementation = RFIDTag.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed (e.g. missing site name/EPC/location, negative RSSI)",
            content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "409", description = "A tag with the same EPC already exists",
            content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<?> createRFID(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "RFID tag data to create", required = true)
        @Valid @RequestBody RfidDto rfidDto
    ) {
        try {
            RFIDTag entity = rfidTagService.create(rfidTagMapper.toEntity(rfidDto));
            return new ResponseEntity<>(entity, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(List.of("epc: " + e.getMessage()), HttpStatus.CONFLICT);
        }
    }

    @Operation(
        summary = "Update an existing RFID tag by TID",
        description = "Updates the site name, EPC, location and RSSI of an existing RFID tag identified by its TID. "
            + "Typical use case: correcting or refreshing a previously recorded tag read."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tag updated successfully",
            content = @Content(schema = @Schema(implementation = RFIDTag.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "No tag exists with the given TID", content = @Content),
        @ApiResponse(responseCode = "409", description = "The updated EPC conflicts with an existing tag",
            content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PutMapping("/update/{tid}")
    public ResponseEntity<?> updateByTid(
        @Parameter(description = "UUID of the RFID tag (TID) to update", required = true)
        @PathVariable UUID tid,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated RFID tag data", required = true)
        @Valid @RequestBody RfidDto rfidDto
    ) {
        try {
            RFIDTag updatedEntity = rfidTagService.updateByTid(tid, rfidTagMapper.toEntity(rfidDto));
            return new ResponseEntity<>(updatedEntity, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(List.of("epc: " + e.getMessage()), HttpStatus.CONFLICT);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
        summary = "Delete an RFID tag by TID",
        description = "Deletes the RFID tag identified by its TID. "
            + "Typical use case: removing a stale or erroneous tag read."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tag deleted successfully", content = @Content),
        @ApiResponse(responseCode = "404", description = "No tag exists with the given TID", content = @Content),
        @ApiResponse(responseCode = "500", description = "Unexpected error while deleting the tag", content = @Content)
    })
    @DeleteMapping("/delete/{tid}")
    public ResponseEntity<Void> deleteByTid(
        @Parameter(description = "UUID of the RFID tag (TID) to delete", required = true)
        @PathVariable UUID tid
    ) {
        try{

            return rfidTagService.deleteByTid(tid)
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

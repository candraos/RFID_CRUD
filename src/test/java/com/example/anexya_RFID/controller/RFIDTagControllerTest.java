package com.example.anexya_RFID.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.anexya_RFID.dto.RfidDto;
import com.example.anexya_RFID.mapper.RFIDTagMapper;
import com.example.anexya_RFID.model.RFIDTag;
import com.example.anexya_RFID.service.RFIDTagService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(RFIDTagController.class)
public class RFIDTagControllerTest {

    private RFIDTag rfidTag;

    @MockitoBean
    private RFIDTagService rfidTagService;

    @MockitoBean
    private RFIDTagMapper rfidTagMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        // Initialize mocks and any required setup here
        rfidTag = new RFIDTag();
        rfidTag.setTID(java.util.UUID.randomUUID());
        rfidTag.setDate(java.time.LocalDateTime.now());
        rfidTag.setEpc("EPC123");
        rfidTag.setLocation("Location1");
        rfidTag.setRssi(10);
        rfidTag.setSiteName("Site1");
    }

    @Test
    public void testGetRFIDTagById_shouldReturnObject_ifFound() throws Exception {
        when(rfidTagService.findByTid(any())).thenReturn(Optional.of(rfidTag));

        mockMvc.perform(get("/rfidtag/get/" + rfidTag.getTID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tid").value(rfidTag.getTID().toString()))
                .andExpect(jsonPath("$.epc").value(rfidTag.getEpc()))
                .andExpect(jsonPath("$.location").value(rfidTag.getLocation()))
                .andExpect(jsonPath("$.rssi").value(rfidTag.getRssi()))
                .andExpect(jsonPath("$.siteName").value(rfidTag.getSiteName()));
    }

    @Test
    public void testGetRFIDTagById_shouldReturnNotFound_ifNotFound() throws Exception {
        when(rfidTagService.findByTid(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/rfidtag/get/" + rfidTag.getTID()))
                .andExpect(status().isNotFound());

    }

    private RfidDto validDto() {
        return RfidDto.builder()
                .siteName("Site1")
                .epc("EPC123")
                .location("Location1")
                .rssi(10)
                .build();
    }

    @Test
    public void testUpdateRFIDTagById_shouldReturnUpdatedObject_ifFound() throws Exception {
        UUID tid = rfidTag.getTID();
        RfidDto dto = validDto();

        when(rfidTagMapper.toEntity(any())).thenReturn(rfidTag);
        when(rfidTagService.updateByTid(eq(tid), any())).thenReturn(rfidTag);

        mockMvc.perform(put("/rfidtag/update/" + tid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tid").value(rfidTag.getTID().toString()))
                .andExpect(jsonPath("$.epc").value(rfidTag.getEpc()))
                .andExpect(jsonPath("$.location").value(rfidTag.getLocation()))
                .andExpect(jsonPath("$.rssi").value(rfidTag.getRssi()))
                .andExpect(jsonPath("$.siteName").value(rfidTag.getSiteName()));
    }

    @Test
    public void testUpdateRFIDTagById_shouldReturnNotFound_ifNotFound() throws Exception {
        UUID tid = rfidTag.getTID();
        RfidDto dto = validDto();

        when(rfidTagMapper.toEntity(any())).thenReturn(rfidTag);
        when(rfidTagService.updateByTid(eq(tid), any())).thenThrow(new NotFoundException());

        mockMvc.perform(put("/rfidtag/update/" + tid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateRFIDTagById_shouldReturnConflict_ifEpcAlreadyExists() throws Exception {
        UUID tid = rfidTag.getTID();
        RfidDto dto = validDto();

        when(rfidTagMapper.toEntity(any())).thenReturn(rfidTag);
        when(rfidTagService.updateByTid(eq(tid), any()))
                .thenThrow(new DataIntegrityViolationException("EPC already exists"));

        mockMvc.perform(put("/rfidtag/update/" + tid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$[0]").value("epc: EPC already exists"));
    }

    @Test
    public void testUpdateRFIDTagById_shouldReturnBadRequest_ifBodyInvalid() throws Exception {
        UUID tid = rfidTag.getTID();
        RfidDto invalidDto = RfidDto.builder()
                .siteName("")
                .epc("")
                .location("")
                .rssi(-1)
                .build();

        mockMvc.perform(put("/rfidtag/update/" + tid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void testDeleteRFIDTagById_shouldReturnOk_ifDeleted() throws Exception {
        UUID tid = rfidTag.getTID();
        when(rfidTagService.deleteByTid(tid)).thenReturn(true);

        mockMvc.perform(delete("/rfidtag/delete/" + tid))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteRFIDTagById_shouldReturnNotFound_ifNotFound() throws Exception {
        UUID tid = rfidTag.getTID();
        when(rfidTagService.deleteByTid(tid)).thenReturn(false);

        mockMvc.perform(delete("/rfidtag/delete/" + tid))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteRFIDTagById_shouldReturnInternalServerError_ifExceptionThrown() throws Exception {
        UUID tid = rfidTag.getTID();
        when(rfidTagService.deleteByTid(tid)).thenThrow(new RuntimeException("unexpected error"));

        mockMvc.perform(delete("/rfidtag/delete/" + tid))
                .andExpect(status().isInternalServerError());
    }

}

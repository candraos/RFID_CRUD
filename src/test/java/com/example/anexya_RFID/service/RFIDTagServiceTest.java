package com.example.anexya_RFID.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import com.example.anexya_RFID.model.RFIDTag;
import com.example.anexya_RFID.repository.RFIDTagRepository;

@ExtendWith(MockitoExtension.class)
public class RFIDTagServiceTest {
    
    @Mock
    private RFIDTagRepository rfidTagRepository;

    @InjectMocks
    private RFIDTagService rfidService;

    private RFIDTag rfidTag;

    @BeforeEach
    public void setUp() {
        // Initialize mocks and any required setup here
        rfidTag = new RFIDTag();
        rfidTag.setTID(UUID.randomUUID());
        rfidTag.setDate(LocalDateTime.now());
        rfidTag.setEpc("EPC123");
        rfidTag.setLocation("Location1");
        rfidTag.setRssi(10);
        rfidTag.setSiteName("Site1");
    }

    @Test
    public void testCreateRFIDTag() {
        when(rfidTagRepository.save(any())).thenReturn(rfidTag);

        RFIDTag createdTag = rfidService.create(rfidTag);

        assertNotNull(createdTag);
        assertNotNull(createdTag.getTID());

    }

    @Test
    public void testGetRFIDTagById() {
        when(rfidTagRepository.findById(any())).thenReturn(Optional.of(rfidTag));

        RFIDTag foundTag = rfidService.findByTid(rfidTag.getTID()).get();

        assertNotNull(foundTag);
        assertEquals(foundTag.getTID(),rfidTag.getTID());
    }

    @Test
    public void updateByTid_shouldUpdateAndReturnTag_whenTagExistsAndEpcIsUnchanged() throws NotFoundException {
        RFIDTag updates = RFIDTag.builder()
                .siteName("Site2")
                .epc(rfidTag.getEpc())
                .location("Location2")
                .rssi(20)
                .build();
        when(rfidTagRepository.findById(rfidTag.getTID())).thenReturn(Optional.of(rfidTag));
        when(rfidTagRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RFIDTag result = rfidService.updateByTid(rfidTag.getTID(), updates);

        assertNotNull(result);
        assertEquals(rfidTag.getTID(), result.getTID());
        assertEquals("Site2", result.getSiteName());
        assertEquals("Location2", result.getLocation());
        assertEquals(20, result.getRssi());
        verify(rfidTagRepository, never()).existsByEpcAndTIDNot(anyString(), any());
    }

    @Test
    public void updateByTid_shouldUpdateAndReturnTag_whenEpcChangedToAnUnusedValue() throws NotFoundException {
        RFIDTag updates = RFIDTag.builder()
                .siteName(rfidTag.getSiteName())
                .epc("EPC999")
                .location(rfidTag.getLocation())
                .rssi(rfidTag.getRssi())
                .build();
        when(rfidTagRepository.findById(rfidTag.getTID())).thenReturn(Optional.of(rfidTag));
        when(rfidTagRepository.existsByEpcAndTIDNot("EPC999", rfidTag.getTID())).thenReturn(false);
        when(rfidTagRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RFIDTag result = rfidService.updateByTid(rfidTag.getTID(), updates);

        assertNotNull(result);
        assertEquals("EPC999", result.getEpc());
    }

    @Test
    public void updateByTid_shouldThrowNotFoundException_whenTagDoesNotExist() {
        UUID missingTid = UUID.randomUUID();
        when(rfidTagRepository.findById(missingTid)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> rfidService.updateByTid(missingTid, rfidTag));
        verify(rfidTagRepository, never()).save(any());
    }

    @Test
    public void updateByTid_shouldThrowDataIntegrityViolationException_whenEpcChangedToAnExistingValue() {
        RFIDTag updates = RFIDTag.builder()
                .siteName(rfidTag.getSiteName())
                .epc("EPC999")
                .location(rfidTag.getLocation())
                .rssi(rfidTag.getRssi())
                .build();
        when(rfidTagRepository.findById(rfidTag.getTID())).thenReturn(Optional.of(rfidTag));
        when(rfidTagRepository.existsByEpcAndTIDNot("EPC999", rfidTag.getTID())).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class,
                () -> rfidService.updateByTid(rfidTag.getTID(), updates));
        verify(rfidTagRepository, never()).save(any());
    }

    @Test
    public void deleteByTid_shouldReturnTrueAndDeleteTag_whenTagExists() {
        when(rfidTagRepository.existsById(rfidTag.getTID())).thenReturn(true);

        boolean result = rfidService.deleteByTid(rfidTag.getTID());

        assertTrue(result);
        verify(rfidTagRepository, times(1)).deleteById(rfidTag.getTID());
    }

    @Test
    public void deleteByTid_shouldReturnFalseAndNotDeleteTag_whenTagDoesNotExist() {
        UUID missingTid = UUID.randomUUID();
        when(rfidTagRepository.existsById(missingTid)).thenReturn(false);

        boolean result = rfidService.deleteByTid(missingTid);

        assertFalse(result);
        verify(rfidTagRepository, never()).deleteById(any());
    }
}

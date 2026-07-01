package com.example.anexya_RFID.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.anexya_RFID.model.RFIDTag;

@Repository
public interface RFIDTagRepository extends JpaRepository<RFIDTag, UUID> {

}

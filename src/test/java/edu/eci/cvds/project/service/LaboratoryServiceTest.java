package edu.eci.cvds.project.service;

import edu.eci.cvds.project.model.DTO.LaboratoryDTO;
import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.model.Reservation;
import edu.eci.cvds.project.repository.LaboratoryMongoRepository;
import edu.eci.cvds.project.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LaboratoryServiceTest {

    @InjectMocks
    private LaboratoryService laboratoryService;

    @Mock
    private LaboratoryMongoRepository laboratoryRepository;

    @Mock
    private JwtUtil jwtUtilservice;

    private Laboratory laboratory;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String validToken = "valid-token";
    private String invalidToken = "invalid-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        laboratory = new Laboratory();
        laboratory.setName("Lab A");
        laboratory.setReservations(new ArrayList<>());

        startTime = LocalDateTime.of(2025, 4, 10, 10, 0);
        endTime = LocalDateTime.of(2025, 4, 10, 12, 0);
    }

    @Test
    void testGetAllLaboratories() {
        List<Laboratory> labs = List.of(new Laboratory(), new Laboratory());
        when(laboratoryRepository.findAll()).thenReturn(labs);

        List<Laboratory> result = laboratoryService.getAllLaboratories();
        assertEquals(2, result.size());
    }

    @Test
    void testGetLaboratoryById_Found() {
        when(laboratoryRepository.findById("123")).thenReturn(Optional.of(laboratory));

        Optional<Laboratory> result = laboratoryService.getLaboratoryById("123");
        assertTrue(result.isPresent());
        assertEquals("Lab A", result.get().getName());
    }

    @Test
    void testGetLaboratoryById_NotFound() {
        when(laboratoryRepository.findById("999")).thenReturn(Optional.empty());

        Optional<Laboratory> result = laboratoryService.getLaboratoryById("999");
        assertFalse(result.isPresent());
    }

    @Test
    void testGetLaboratoryByName_Found() {
        when(laboratoryRepository.findLaboratoriesByName("Lab A")).thenReturn(laboratory);

        Laboratory result = laboratoryService.getLaboratoryByName("Lab A");
        assertNotNull(result);
        assertEquals("Lab A", result.getName());
    }

    @Test
    void testGetLaboratoryByName_NotFound() {
        when(laboratoryRepository.findLaboratoriesByName("Lab X")).thenReturn(null);

        Laboratory result = laboratoryService.getLaboratoryByName("Lab X");
        assertNull(result);
    }

    @Test
    void testSaveLaboratory_Success() {
        when(jwtUtilservice.validateAdmin(validToken)).thenReturn(true);
        when(laboratoryRepository.findLaboratoriesByName("Lab B")).thenReturn(null);
        when(laboratoryRepository.saveLaboratory(any(Laboratory.class))).thenReturn(laboratory);

        LaboratoryDTO dto = new LaboratoryDTO();
        dto.setName("Lab B");

        Laboratory result = laboratoryService.saveLaboratory(dto, validToken);
        assertNotNull(result);
        assertEquals("Lab A", result.getName());
    }

    @Test
    void testSaveLaboratory_InvalidToken() {
        when(jwtUtilservice.validateAdmin(invalidToken)).thenReturn(false);

        LaboratoryDTO dto = new LaboratoryDTO();
        dto.setName("Lab B");

        assertThrows(IllegalArgumentException.class, () -> laboratoryService.saveLaboratory(dto, invalidToken));
    }

    @Test
    void testSaveLaboratory_LaboratoryAlreadyExists() {
        when(jwtUtilservice.validateAdmin(validToken)).thenReturn(true);
        when(laboratoryRepository.findLaboratoriesByName("Lab A")).thenReturn(laboratory);

        LaboratoryDTO dto = new LaboratoryDTO();
        dto.setName("Lab A");

        assertThrows(IllegalArgumentException.class, () -> laboratoryService.saveLaboratory(dto, validToken));
    }

    @Test
    void testIsLaboratoryAvailable_NoReservations() {
        assertTrue(laboratoryService.isLaboratoryAvailable(laboratory, startTime));
    }

    @Test
    void testIsLaboratoryAvailable_ReservationExists() {
        addReservation(startTime, endTime);
        assertFalse(laboratoryService.isLaboratoryAvailable(laboratory, startTime.plusMinutes(30)));
    }

    @Test
    void testIsLaboratoryAvailable_OutsideReservation() {
        addReservation(startTime, endTime);
        assertTrue(laboratoryService.isLaboratoryAvailable(laboratory, startTime.minusHours(2)));
    }

    @Test
    void testIsLaboratoriesAvailable_ReservationExists_False() {
        addReservation(startTime, endTime);
        assertFalse(laboratoryService.isLaboratoriesAvailable(laboratory, startTime, endTime));
    }

    @Test
    void testIsLaboratoriesAvailable_CompletelyBeforeOrAfter_True() {
        addReservation(startTime, endTime);
        assertTrue(laboratoryService.isLaboratoriesAvailable(laboratory,
                LocalDateTime.of(2025, 4, 10, 7, 0),
                LocalDateTime.of(2025, 4, 10, 8, 0)));

        assertTrue(laboratoryService.isLaboratoriesAvailable(laboratory,
                LocalDateTime.of(2025, 4, 10, 13, 0),
                LocalDateTime.of(2025, 4, 10, 14, 0)));
    }

    @Test
    void testDeleteLaboratory() {
        doNothing().when(laboratoryRepository).deleteLaboratoryById("123");

        assertDoesNotThrow(() -> laboratoryService.deleteLaboratory("123"));
        verify(laboratoryRepository, times(1)).deleteLaboratoryById("123");
    }

    private void addReservation(LocalDateTime start, LocalDateTime end) {
        Reservation reservation = new Reservation();
        reservation.setStartDateTime(start);
        reservation.setEndDateTime(end);
        laboratory.getReservations().add(reservation);
    }
}

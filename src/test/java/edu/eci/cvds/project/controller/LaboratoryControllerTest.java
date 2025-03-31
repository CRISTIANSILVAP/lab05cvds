package edu.eci.cvds.project.controller;

import edu.eci.cvds.project.model.DTO.LaboratoryDTO;
import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.service.ServicesLab;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LaboratoryControllerTest {

    @Mock
    private ServicesLab laboratoryService;

    @InjectMocks
    private LaboratoryController laboratoryController;

    private Laboratory laboratory;
    private LaboratoryDTO laboratoryDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        laboratory = new Laboratory("LAB-001", "Lab 1", new ArrayList<>());
        laboratoryDTO = new LaboratoryDTO();
        laboratoryDTO.setName("Lab 1");
    }

    @Test
    public void testGetAllLaboratories() {
        List<Laboratory> laboratories = new ArrayList<>();
        laboratories.add(laboratory);
        when(laboratoryService.getAllLaboratories()).thenReturn(laboratories);

        List<Laboratory> result = laboratoryController.getAllLaboratories();

        assertEquals(laboratories, result);
        verify(laboratoryService, times(1)).getAllLaboratories();
    }

    @Test
    public void testGetLaboratoryById_ExistingLab() {
        when(laboratoryService.getLaboratoryById("LAB-001")).thenReturn(Optional.of(laboratory));

        ResponseEntity<Laboratory> response = laboratoryController.getLaboratoryById("LAB-001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(laboratory, response.getBody());
        verify(laboratoryService, times(1)).getLaboratoryById("LAB-001");
    }

    @Test
    public void testGetLaboratoryById_NonExistingLab() {
        when(laboratoryService.getLaboratoryById("LAB-002")).thenReturn(Optional.empty());

        ResponseEntity<Laboratory> response = laboratoryController.getLaboratoryById("LAB-002");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(laboratoryService, times(1)).getLaboratoryById("LAB-002");
    }

    @Test
    public void testCheckLaboratoryAvailability_LabAvailable() {
        LocalDateTime dateTime = LocalDateTime.now();
        when(laboratoryService.getLaboratoryById("LAB-001")).thenReturn(Optional.of(laboratory));
        when(laboratoryService.isLaboratoryAvailable(laboratory, dateTime)).thenReturn(true);

        ResponseEntity<String> response = laboratoryController.checkLaboratoryAvailability("LAB-001", dateTime.toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Laboratory is available", response.getBody());
        verify(laboratoryService, times(1)).getLaboratoryById("LAB-001");
        verify(laboratoryService, times(1)).isLaboratoryAvailable(laboratory, dateTime);
    }

    @Test
    public void testCheckLaboratoryAvailability_LabNotAvailable() {
        LocalDateTime dateTime = LocalDateTime.now();
        when(laboratoryService.getLaboratoryById("LAB-001")).thenReturn(Optional.of(laboratory));
        when(laboratoryService.isLaboratoryAvailable(laboratory, dateTime)).thenReturn(false);

        ResponseEntity<String> response = laboratoryController.checkLaboratoryAvailability("LAB-001", dateTime.toString());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Laboratory is not available", response.getBody());
        verify(laboratoryService, times(1)).getLaboratoryById("LAB-001");
        verify(laboratoryService, times(1)).isLaboratoryAvailable(laboratory, dateTime);
    }

    @Test
    public void testCheckLaboratoryAvailability_LabNotFound() {
        LocalDateTime dateTime = LocalDateTime.now();
        when(laboratoryService.getLaboratoryById("LAB-002")).thenReturn(Optional.empty());

        ResponseEntity<String> response = laboratoryController.checkLaboratoryAvailability("LAB-002", dateTime.toString());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Laboratory not found", response.getBody());
        verify(laboratoryService, times(1)).getLaboratoryById("LAB-002");
        verify(laboratoryService, never()).isLaboratoryAvailable(any(), any());
    }

    @Test
    public void testGetLaboratoryByName_ExistingLab() {
        when(laboratoryService.getLaboratoryByName("Lab 1")).thenReturn(laboratory);

        ResponseEntity<Laboratory> response = laboratoryController.getLaboratoryByName("Lab 1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(laboratory, response.getBody());
        verify(laboratoryService, times(1)).getLaboratoryByName("Lab 1");
    }

    @Test
    public void testGetLaboratoryByName_NonExistingLab() {
        when(laboratoryService.getLaboratoryByName("Lab X"))
                .thenReturn(null);

        ResponseEntity<Laboratory> response = laboratoryController.getLaboratoryByName("Lab X");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(laboratoryService, times(1)).getLaboratoryByName("Lab X");
    }

    @Test
    public void testCreateLaboratory_Success() {
        String token = "Bearer valid-token";
        when(laboratoryService.saveLaboratory(laboratoryDTO, token)).thenReturn(laboratory);

        ResponseEntity<Laboratory> response = laboratoryController.createLaboratory(laboratoryDTO, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(laboratory, response.getBody());
        verify(laboratoryService, times(1)).saveLaboratory(laboratoryDTO, token);
    }

    @Test
    public void testCheckLaboratoriesAvailability_Success() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        List<Laboratory> laboratories = List.of(laboratory);
        when(laboratoryService.getAllLaboratories()).thenReturn(laboratories);
        when(laboratoryService.isLaboratoriesAvailable(laboratory, start, end)).thenReturn(true);

        ResponseEntity<List<String>> response = laboratoryController.checkLaboratoriesAvailability(start.toString(), end.toString());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(laboratoryService, times(1)).getAllLaboratories();
        verify(laboratoryService, times(1)).isLaboratoriesAvailable(laboratory, start, end);
    }

    @Test
    public void testCheckLaboratoriesAvailability_NoAvailableLabs() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);
        List<Laboratory> laboratories = List.of(laboratory);
        when(laboratoryService.getAllLaboratories()).thenReturn(laboratories);
        when(laboratoryService.isLaboratoriesAvailable(laboratory, start, end)).thenReturn(false);

        ResponseEntity<List<String>> response = laboratoryController.checkLaboratoriesAvailability(start.toString(), end.toString());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(laboratoryService, times(1)).getAllLaboratories();
        verify(laboratoryService, times(1)).isLaboratoriesAvailable(laboratory, start, end);
    }

}

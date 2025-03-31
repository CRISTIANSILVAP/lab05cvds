package edu.eci.cvds.project.controller;

import edu.eci.cvds.project.model.DTO.ReservationDTO;
import edu.eci.cvds.project.model.Reservation;
import edu.eci.cvds.project.service.ServicesReservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationControllerTest {

    @Mock
    private ServicesReservation reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReservation() {
        ReservationDTO reservationDTO = new ReservationDTO();
        Reservation reservation = new Reservation();
        when(reservationService.createReservation(any())).thenReturn(reservation);

        ResponseEntity<?> response = reservationController.createReservation(reservationDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetAllReservations() {
        List<Reservation> reservations = Arrays.asList(new Reservation(), new Reservation());
        when(reservationService.getAllReservations()).thenReturn(reservations);

        List<Reservation> response = reservationController.getAllReservations();

        assertEquals(2, response.size());
    }

    @Test
    void testCancelReservation() {
        when(reservationService.cancelReservation("1")).thenReturn(true);
        ResponseEntity<Void> response = reservationController.cancelReservation("1");
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testGenerateReservations() {
        doNothing().when(reservationService).generateRandomReservations(anyInt(), anyInt());
        ResponseEntity<String> response = reservationController.generateReservations(10, 50);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Reservas generadas exitosamente.", response.getBody());
    }

    @Test
    void testDeleteAllReservations() {
        doNothing().when(reservationService).deleteAllReservations();
        ResponseEntity<String> response = reservationController.deleteAllReservations();
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetReservationsByDate() {
        Reservation res1 = new Reservation();
        res1.setStartDateTime(LocalDate.of(2024, 3, 30).atStartOfDay());

        when(reservationService.getAllReservations()).thenReturn(Collections.singletonList(res1));
        ResponseEntity<Map<LocalDate, Long>> response = reservationController.getReservationsByDate();

        assertTrue(response.getBody().containsKey(LocalDate.of(2024, 3, 30)));
    }

    @Test
    void testGetReservationsByLabAndDate() {
        Reservation res1 = new Reservation();
        res1.setStartDateTime(LocalDate.of(2024, 3, 30).atStartOfDay());
        res1.setLaboratoryname("Lab A");

        when(reservationService.getAllReservations()).thenReturn(Collections.singletonList(res1));
        ResponseEntity<Map<String, Long>> response = reservationController.getReservationsByLabAndDate(LocalDate.of(2024, 3, 29), LocalDate.of(2024, 3, 31));

        assertTrue(response.getBody().containsKey("Lab A"));
    }

    @Test
    void testGetAverageReservationsByPriority() {
        Reservation res1 = new Reservation();
        res1.setPriority(1);

        when(reservationService.getAllReservations()).thenReturn(Collections.singletonList(res1));
        ResponseEntity<Map<Integer, Double>> response = reservationController.getAverageReservationsByPriority();

        assertTrue(response.getBody().containsKey(1));
        assertEquals(1.0, response.getBody().get(1));
    }

    @Test
    void testGetReservationsByLab() {
        Reservation res1 = new Reservation();
        res1.setLaboratoryname("Lab A");

        when(reservationService.getAllReservations()).thenReturn(Collections.singletonList(res1));
        ResponseEntity<Map<String, Long>> response = reservationController.getReservationsByLab();

        assertTrue(response.getBody().containsKey("Lab A"));
    }
}
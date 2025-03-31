package edu.eci.cvds.project.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationModelTest {

    @Test
    void testConstructorAndGettersAndSetters() {
        String id = "RES-001";
        String laboratoryname = "LAB-001";
        String username = "willy";
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        String purpose = "Project meeting";
        boolean status = true;
        Integer priority=5;

        Reservation reservation = new Reservation(id, laboratoryname, username, startDateTime, endDateTime, purpose, status, priority);

        assertEquals(id, reservation.getId());
        assertEquals(laboratoryname, reservation.getLaboratoryname());
        assertEquals(username, reservation.getUsername());
        assertEquals(startDateTime, reservation.getStartDateTime());
        assertEquals(endDateTime, reservation.getEndDateTime());
        assertEquals(purpose, reservation.getPurpose());
        assertTrue(reservation.getStatus());

        String newPurpose = "Study session";
        boolean newStatus = false;

        reservation.setPurpose(newPurpose);
        reservation.setStatus(newStatus);

        assertEquals(newPurpose, reservation.getPurpose());
        assertFalse(reservation.getStatus());
    }

    @Test
    void testToString() {
        String id = "RES-001";
        String laboratoryname = "LAB-001";
        String username="carl";
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        String purpose = "Project meeting";
        boolean status = true;
        Integer priority=5;

        Reservation reservation = new Reservation(id, laboratoryname, username, startDateTime, endDateTime, purpose, status,priority);

        String toStringResult = reservation.toString();
        assertTrue(toStringResult.contains("Reservation(id=RES-001"));
        assertTrue(toStringResult.contains("laboratoryname=LAB-001"));
        assertTrue(toStringResult.contains("purpose=Project meeting"));
    }

    @Test
    void testEqualsAndHashCode() {
        Reservation res1 = new Reservation("RES-001", "LAB-001", "fabian", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Meeting", true, 3);
        Reservation res2 = new Reservation("RES-001", "LAB-001", "fabian" , LocalDateTime.now(), LocalDateTime.now().plusHours(3), "Study", false, 5);
        Reservation res3 = new Reservation("RES-002", "LAB-001", "charles", LocalDateTime.now(), LocalDateTime.now().plusHours(2), "Meeting", true, 2);

        assertEquals(res1, res2);
        assertNotEquals(res1, res3);
        assertEquals(res1.hashCode(), res2.hashCode());
        assertNotEquals(res1.hashCode(), res3.hashCode());

        assertNotEquals(res1, null);
        assertNotEquals(res1, "Not a Reservation");
    }


    @Test
    void testGetStatusBoolean() {
        Reservation reservation = new Reservation();
        reservation.setStatus(true);
        assertTrue(reservation.getStatus());

        reservation.setStatus(false);
        assertFalse(reservation.getStatus());
    }
}
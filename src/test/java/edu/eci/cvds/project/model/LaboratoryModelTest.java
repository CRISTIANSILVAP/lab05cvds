package edu.eci.cvds.project.model;

import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.model.Reservation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LaboratoryModelTest {

    @Test
    void testConstructorAndGettersAndSetters() {
        String id = "LAB-001";
        String name = "Plataformas";
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation());

        Laboratory laboratory = new Laboratory(id, name, reservations);

        assertEquals(id, laboratory.getId());
        assertEquals(name, laboratory.getName());
        assertEquals(reservations, laboratory.getReservations());

        String newName = "Redes";
        List<Reservation> newReservations = new ArrayList<>();
        newReservations.add(new Reservation());
        newReservations.add(new Reservation());

        laboratory.setName(newName);
        laboratory.setReservations(newReservations);

        assertEquals(newName, laboratory.getName());
        assertEquals(newReservations, laboratory.getReservations());
    }

    @Test
    void testToString() {
        String id = "LAB-001";
        String name = "Software";
        List<Reservation> reservations = new ArrayList<>();

        Laboratory laboratory = new Laboratory(id, name, reservations);

        String expectedString = "Laboratory(id=LAB-001, name=Software, reservations=[])";
        assertEquals(expectedString, laboratory.toString());
    }

    @Test
    void testEquals() {
        String id = "LAB-001";
        String name = "Plataformas";
        List<Reservation> reservations = new ArrayList<>();

        Laboratory laboratory1 = new Laboratory(id, name, reservations);
        Laboratory laboratory2 = new Laboratory(id, name, reservations);
        Laboratory laboratory3 = new Laboratory("LAB-002", name, reservations);

        assertEquals(laboratory1, laboratory2);
        assertNotEquals(laboratory1, laboratory3);
        assertNotEquals(laboratory1, null);
        assertNotEquals(laboratory1, new Object());
    }

    @Test
    void testHashCode() {
        String id = "LAB-001";
        String name = "Plataformas";
        List<Reservation> reservations = new ArrayList<>();

        Laboratory laboratory1 = new Laboratory(id, name, reservations);
        Laboratory laboratory2 = new Laboratory(id, name, reservations);

        assertEquals(laboratory1.hashCode(), laboratory2.hashCode());
    }
}
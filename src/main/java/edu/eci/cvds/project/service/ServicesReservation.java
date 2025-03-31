package edu.eci.cvds.project.service;

import edu.eci.cvds.project.model.DTO.ReservationDTO;
import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ServicesReservation {
    List<Reservation> getAllReservations();
    Reservation createReservation(ReservationDTO reservationDTO);
    boolean cancelReservation(String id);
    List<Reservation> getReservationsInRange(LocalDateTime start, LocalDateTime end);
    boolean isLaboratoryAvilable(Laboratory laboratory, LocalDateTime start, LocalDateTime end);
    boolean isReservationAvailable(Reservation reservation);
    String generateUniqueId();
    Reservation updateReservation(Reservation reservation);
    void generateRandomReservations(int min, int max);
    void deleteAllReservations();
}

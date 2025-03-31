package edu.eci.cvds.project.controller;

import edu.eci.cvds.project.model.DTO.ReservationDTO;
import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.service.UserDetailsServiceImpl;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import edu.eci.cvds.project.model.Reservation;
import edu.eci.cvds.project.service.ServicesReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestionar reservas.
 */
@RestController
@RequestMapping("/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ServicesReservation reservationService;

    /**
     * Crea una nueva reserva.
     * @param reservationDTO Objeto Reservation recibido en la solicitud.
     * @return La reserva creada.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createReservation(@RequestBody ReservationDTO reservationDTO) {
        try {
            Reservation reservation = reservationService.createReservation(reservationDTO);
            return ResponseEntity.ok(reservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    /**
     * this method is in charge of update a task in the application
     * calling the service
     * @param reservation
     * @return the updated task
     */
    @PatchMapping("/update")
    public ResponseEntity<?> updateReservation(@RequestBody Reservation reservation) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(reservationService.updateReservation(reservation));
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }



    /**
     * Cancela una reserva existente.
     * @param id Identificador de la reserva a cancelar.
     * @return ResponseEntity con estado 204 si se cancela correctamente o 404 si no se encuentra.
     */
    @DeleteMapping("cancel/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable String id) {
        boolean cancelled = reservationService.cancelReservation(id);
        if (cancelled) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Obtiene la lista de todas las reservas.
     * @return Lista de reservas.
     */
    @GetMapping("/all")
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

        /**
         * Genera reservas aleatorias dentro de un rango dado.
         *
         * @param min Cantidad mínima de reservas a generar (valor por defecto: 100).
         * @param max Cantidad máxima de reservas a generar (valor por defecto: 1000).
         * @return Mensaje de confirmación de la generación de reservas.
         */
        @PostMapping("/generate")
        public ResponseEntity<String> generateReservations(@RequestParam(defaultValue = "100") int min, @RequestParam(defaultValue = "1000") int max) {
            reservationService.generateRandomReservations(min, max);
            return ResponseEntity.ok("Reservas generadas exitosamente.");
        }

    /**
     * Obtiene la cantidad de reservas agrupadas por fecha.
     *
     * @return Un mapa donde la clave es la fecha y el valor es el número de reservas en esa fecha.
     */
    @GetMapping("/by-date")
    public ResponseEntity<Map<LocalDate, Long>> getReservationsByDate() {
        List<Reservation> reservations = getAllReservations();
        Map<LocalDate, Long> reservationsByDate = new HashMap<>();

        for (Reservation r : reservations) {
            LocalDate date = r.getStartDateTime().toLocalDate();
            reservationsByDate.put(date, reservationsByDate.getOrDefault(date, 0L) + 1);
        }

        return ResponseEntity.ok(reservationsByDate);
    }

    /**
     * Obtiene la cantidad de reservas agrupadas por laboratorio en un rango de fechas.
     *
     * @param startDate Fecha de inicio del rango.
     * @param endDate   Fecha de fin del rango.
     * @return Un mapa donde la clave es el nombre del laboratorio y el valor es el número de reservas dentro del rango.
     */
    @GetMapping("/by-lab-and-date")
    public ResponseEntity<Map<String, Long>> getReservationsByLabAndDate(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        List<Reservation> reservations = getAllReservations();
        Map<String, Long> reservationsByLab = new HashMap<>();

        for (Reservation r : reservations) {
            LocalDate date = r.getStartDateTime().toLocalDate();
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                String labName = r.getLaboratoryname();
                reservationsByLab.put(labName, reservationsByLab.getOrDefault(labName, 0L) + 1);
            }
        }

        return ResponseEntity.ok(reservationsByLab);
    }

    /**
     * Calcula el promedio de reservas por prioridad.
     *
     * @return Un mapa donde la clave es el nivel de prioridad y el valor es el promedio de reservas con esa prioridad.
     */
    @GetMapping("/average-by-priority")
    public ResponseEntity<Map<Integer, Double>> getAverageReservationsByPriority() {
        List<Reservation> reservations = getAllReservations();
        Map<Integer, Long> countByPriority = new HashMap<>();

        for (Reservation r : reservations) {
            int priority = r.getPriority();
            countByPriority.put(priority, countByPriority.getOrDefault(priority, 0L) + 1);
        }

        int totalReservations = reservations.size();
        Map<Integer, Double> averageByPriority = new HashMap<>();

        for (Map.Entry<Integer, Long> entry : countByPriority.entrySet()) {
            int priority = entry.getKey();
            double average = (double) entry.getValue() / totalReservations;
            averageByPriority.put(priority, average);
        }

        return ResponseEntity.ok(averageByPriority);
    }

    /**
     * Obtiene la cantidad de reservas agrupadas por laboratorio.
     *
     * @return Un mapa donde la clave es el nombre del laboratorio y el valor es el número de reservas en ese laboratorio.
     */
    @GetMapping("/by-lab")
    public ResponseEntity<Map<String, Long>> getReservationsByLab() {
        List<Reservation> reservations = getAllReservations();
        Map<String, Long> reservationsByLab = new HashMap<>();

        for (Reservation r : reservations) {
            String labName = r.getLaboratoryname();
            reservationsByLab.put(labName, reservationsByLab.getOrDefault(labName, 0L) + 1);
        }

        return ResponseEntity.ok(reservationsByLab);
    }

    /**
     * Elimina todas las reservas del sistema.
     * @return ResponseEntity con un mensaje de éxito.
     */
    @DeleteMapping("/delete-all")
    public ResponseEntity<String> deleteAllReservations() {
        reservationService.deleteAllReservations();
        return ResponseEntity.ok("Todas las reservas han sido eliminadas exitosamente.");
    }
}

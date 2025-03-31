package edu.eci.cvds.project.service;

import edu.eci.cvds.project.model.DTO.ReservationDTO;
import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.model.Reservation;
import edu.eci.cvds.project.model.Role;
import edu.eci.cvds.project.model.User;
import edu.eci.cvds.project.repository.LaboratoryMongoRepository;
import edu.eci.cvds.project.repository.UserMongoRepository;
import edu.eci.cvds.project.repository.ReservationMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class ReservationService implements ServicesReservation {

    @Autowired
    private ReservationMongoRepository reservationRepository;
    @Autowired
    private UserMongoRepository userRepository;
    @Autowired
    private LaboratoryMongoRepository laboratoryRepository;


    private static final AtomicLong idCounter = new AtomicLong(0);
    @Autowired
    private UserService userService;

    /**
     * Obtiene todas las reservas registradas.
     *
     * @return Lista de reservas.
     */
    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Crea una nueva reserva basándose en los datos proporcionados en el DTO.
     *
     * @param dto Objeto DTO que contiene la información de la reserva.
     * @return La reserva creada.
     * @throws IllegalArgumentException Si el laboratorio o el usuario no existen,
     *                                  o si la reserva no es válida.
     */
    @Transactional
    @Override
    public Reservation createReservation(ReservationDTO dto) {
        Laboratory lab = laboratoryRepository.findLaboratoriesByName(dto.getLabName());
        User user = userRepository.findUserByUsername(dto.getUsername());

        if (lab == null || user == null) {
            throw new IllegalArgumentException("User or Lab not found");
        }

        LocalDateTime startTime = dto.getStartDateTime();
        LocalDateTime endTime = dto.getEndDateTime();

        while (!isLaboratoryAvilable(lab, startTime, endTime)) {
            startTime = startTime.plusDays(1);
            endTime = endTime.plusDays(1);
            if (startTime.isAfter(dto.getStartDateTime().plusDays(365))) {
                throw new IllegalStateException("No available slots within 365 days");
            }
        }

        Reservation reservation = new Reservation();
        reservation.setLaboratoryname(dto.getLabName());
        reservation.setUsername(dto.getUsername());
        reservation.setStartDateTime(startTime);
        reservation.setEndDateTime(endTime);
        reservation.setPurpose(dto.getPurpose());
        reservation.setStatus(true);
        reservation.setPriority(dto.getPriority());

        reservationRepository.save(reservation);
        return updateReservation(reservation);
    }


    /**
     * Cancela una reserva dado su ID.
     * @param id Identificador de la reserva.
     * @return true si la reserva fue cancelada, false si no se encontró.
     */
    @Override
    public boolean cancelReservation(String id) {
        if (id == null) {
            throw new IllegalArgumentException("ID de la reserva no puede ser null");
        }

        Reservation reservation = reservationRepository.findReservationById(id);
        if (reservation == null) {
            throw new DataIntegrityViolationException("Reservation not found: " + id);
        }

        Laboratory lab = laboratoryRepository.findLaboratoriesByName(reservation.getLaboratoryname());
        if (lab == null) {
            throw new DataIntegrityViolationException("Laboratory not found: " + reservation.getLaboratoryname());
        }

        User user = userRepository.findUserByUsername(reservation.getUsername());

        boolean removedFromLab = lab.getReservations().removeIf(r -> r.getId().equals(id));
        boolean removedFromUser = user.getReservations().removeIf(r -> r.getId().equals(id));

        reservationRepository.delete(reservation);

        boolean existsAfter = reservationRepository.findReservationById(id) != null;

        laboratoryRepository.save(lab);
        userRepository.save(user);

        return !existsAfter;
    }



    /**
     * Obtiene una lista de reservas dentro de un rango de fechas especificado.
     * @param start Fecha de inicio del rango.
     * @param end Fecha de fin del rango.
     * @return Lista de reservas dentro del rango de fechas.
     */
    @Override
    public List<Reservation> getReservationsInRange(LocalDateTime start, LocalDateTime end) {
        return reservationRepository.findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(start, end);
    }

    /**
     * Verifica si un laboratorio está disponible dentro de un rango de fechas.
     * @param laboratory Laboratorio a verificar.
     * @param start Fecha y hora de inicio.
     * @param end Fecha y hora de fin.
     * @return true si el laboratorio está disponible, false si está ocupado.
     */
    @Override
    public boolean isLaboratoryAvilable(Laboratory laboratory, LocalDateTime start, LocalDateTime end) {
        List<Reservation> reservations = reservationRepository.findByLaboratoryname(laboratory.getName());

        for (Reservation reservation : reservations) {
            LocalDateTime existingStart = reservation.getStartDateTime();
            LocalDateTime existingEnd = reservation.getEndDateTime();
            if (start.isBefore(existingEnd) && end.isAfter(existingStart)) {
                return false;
            }
        }
        return true;
    }
    /**
     * Verifica si una reserva es válida con respecto al tiempo actual.
     * @param reservation Reserva a validar.
     * @return true si la reserva aún está dentro de su tiempo válido, false si ya ha pasado.
     */
    @Override
    public boolean isReservationAvailable(Reservation reservation) {
        LocalDateTime actualTime =LocalDateTime.now();
        LocalDateTime endTime = reservation.getEndDateTime();
        if(actualTime.isBefore(endTime)){
            return true;
        }else{
            return false;
        }

    }
    // Metodo para generar un ID único secuencial
    @Override
    public String generateUniqueId() {
        return String.valueOf(idCounter.incrementAndGet());  // Genera un ID secuencial único
    }

    /**
     * Actualiza una reservación existente en la base de datos.
     *
     * @param reservation La reservación con los nuevos datos a actualizar.
     * @return La reservación actualizada.
     * @throws DataIntegrityViolationException Si la reservación no existe en la base de datos.
     * @throws RuntimeException Si el usuario asociado a la reservación no se encuentra.
     * @throws TransactionSystemException Si ocurre un error durante la transacción.
     */
    @Transactional
    @Override
    public Reservation updateReservation(Reservation reservation) {
        try {
            if (!reservationRepository.existsById(reservation.getId())) {
                throw new DataIntegrityViolationException("Reservation not found: ");
            }
            Laboratory laboratory =laboratoryRepository.findLaboratoriesByName(reservation.getLaboratoryname());
            laboratory.reservations.add(reservation);
            laboratoryRepository.updateLaboratory(laboratory);
            User user = userRepository.findUserByUsername(reservation.getUsername());
            user.reservations.add(reservation);
            userRepository.updateUser(user);
            if(user == null){
                throw new RuntimeException("User not found");
            }
            return reservationRepository.updateReservation(reservation);
        } catch (TransactionSystemException e) {
            throw new TransactionSystemException("Error creating reservation");
        }
    }
    /**
     * Genera un número aleatorio de reservaciones dentro del rango especificado.
     *
     * @param min Cantidad mínima de reservaciones a generar (no menor de 100).
     * @param max Cantidad máxima de reservaciones a generar (no mayor de 1000).
     */
    public void generateRandomReservations(int min, int max) {
        List<Laboratory> laboratories = laboratoryRepository.findAll();
        if (laboratories.isEmpty()) {
            throw new IllegalStateException("No laboratories found for generating reservations");
        }

        List<User> users = userRepository.findAllUsers();
        if (users.isEmpty()) {
            throw new IllegalStateException("No users found for generating reservations");
        }

        int numReservations = Math.max(100, Math.min(max, 1000));
        Random random = new Random();
        int generatedReservations = 0;

        while (generatedReservations < numReservations) {
            Laboratory lab = laboratories.get(random.nextInt(laboratories.size()));
            User user = users.get(random.nextInt(users.size()));

            LocalDateTime startDate = LocalDateTime.now().plusDays(random.nextInt(30));
            LocalDateTime endDate = startDate.plusHours(2 + random.nextInt(3));

            if (!isLaboratoryAvilable(lab, startDate, endDate)) {
                continue;
            }

            ReservationDTO dto = new ReservationDTO(
                    lab.getName(),
                    user.getUsername(),
                    startDate,
                    endDate,
                    "Random reservation",
                    random.nextInt(5) + 1
            );

            createReservation(dto);
            generatedReservations++;
        }
    }


    /**
     * Obtiene un laboratorio aleatorio de la base de datos.
     *
     * @return Un laboratorio aleatorio si existen laboratorios en la base de datos;
     *         de lo contrario, retorna un laboratorio por defecto.
     */
    private Laboratory getRandomLaboratory() {
        List<Laboratory> laboratories = laboratoryRepository.findAll();

        if (laboratories.isEmpty()) {
            return new Laboratory("1", "Default Lab", new ArrayList<>());
        }

        Random random = new Random();
        int randomIndex = random.nextInt(laboratories.size());
        return laboratories.get(randomIndex);
    }

    /**
     * Obtiene un usuario aleatorio de la base de datos.
     *
     * @return Un usuario aleatorio si existen usuarios en la base de datos;
     *         de lo contrario, retorna un usuario predeterminado.
     */
    private User getRandomUser() {
        List<User> users = userRepository.findAllUsers();
        if (!users.isEmpty()) {
            return users.get(new Random().nextInt(users.size()));
        }
        return new User("User1", "ID1", "password123",new ArrayList<>(), Role.USER);
    }

    /**
     * Elimina todas las reservas almacenadas en la base de datos.
     */
    @Override
    public void deleteAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        Map<String, Laboratory> laboratories = new HashMap<>();
        Map<String, User> users = new HashMap<>();

        for (Reservation reservation : reservations) {
            String labName = reservation.getLaboratoryname();
            String userName = reservation.getUsername();

            laboratories.computeIfAbsent(labName, laboratoryRepository::findLaboratoriesByName);
            users.computeIfAbsent(userName, userRepository::findUserById);

            if (laboratories.get(labName) != null) {
                laboratories.get(labName).getReservations().removeIf(r -> r.getId().equals(reservation.getId()));
            }
            if (users.get(userName) != null) {
                users.get(userName).getReservations().removeIf(r -> r.getId().equals(reservation.getId()));
            }
        }

        reservationRepository.deleteAll();

        laboratories.values().forEach(laboratoryRepository::save);
        users.values().forEach(userRepository::save);
    }
}

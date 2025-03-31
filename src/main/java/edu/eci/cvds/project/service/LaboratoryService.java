package edu.eci.cvds.project.service;

import edu.eci.cvds.project.model.DTO.LaboratoryDTO;
import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.model.Reservation;
import edu.eci.cvds.project.repository.LaboratoryMongoRepository;
import edu.eci.cvds.project.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LaboratoryService implements ServicesLab {

    @Autowired
    private LaboratoryMongoRepository laboratoryRepository;
    @Autowired
    private JwtUtil jwtUtilservice;

    /**
     * Obtiene todos los laboratorios almacenados en la base de datos.
     * @return Lista de todos los laboratorios.
     */
    @Override
    public List<Laboratory> getAllLaboratories() {
        return laboratoryRepository.findAll();
    }

    /**
     * Busca un laboratorio por su ID.
     * @param id Identificador del laboratorio.
     * @return Un Optional que contiene el laboratorio si se encuentra.
     */
    @Override
    public Optional<Laboratory> getLaboratoryById(String id) {
        return laboratoryRepository.findById(id);
    }

    @Override
    public Laboratory getLaboratoryByName(String name) {
        return laboratoryRepository.findLaboratoriesByName(name);
    }

    /**
     * Guarda un nuevo laboratorio en la base de datos a partir de un DTO.
     * @param laboratoryDTO DTO que contiene los datos del laboratorio.
     * @return El laboratorio guardado.
     */
    @Override
    public Laboratory saveLaboratory(LaboratoryDTO laboratoryDTO,String token) {
        if (!jwtUtilservice.validateAdmin(token)) {
            throw new IllegalArgumentException("Invalid token");
        }
        if (laboratoryRepository.findLaboratoriesByName(laboratoryDTO.getName()) != null) {
            throw new IllegalArgumentException("Laboratory already exists");
        }
        Laboratory laboratory = new Laboratory();
        laboratory.setName(laboratoryDTO.getName());
        laboratory.setReservations(new ArrayList<Reservation>());

        return laboratoryRepository.saveLaboratory(laboratory);
    }

    /**
     * Verifica si un laboratorio está disponible en una fecha y hora específicas.
     * @param laboratory Laboratorio a verificar.
     * @param localDateTime Fecha y hora de la reserva.
     * @return true si el laboratorio está disponible, false en caso contrario.
     */
    @Override
    public boolean isLaboratoryAvailable(Laboratory laboratory, LocalDateTime localDateTime) {
        for (Reservation reservation : laboratory.getReservations()) {
            LocalDateTime start = reservation.getStartDateTime();
            LocalDateTime end = reservation.getEndDateTime();
            if (!localDateTime.isBefore(start) && !localDateTime.isAfter(end)) {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean isLaboratoriesAvailable(Laboratory laboratory,LocalDateTime dateStartTime,LocalDateTime dateEndTime) {
        for (Reservation reservation : laboratory.getReservations()) {
            LocalDateTime start=reservation.getStartDateTime();
            LocalDateTime end=reservation.getEndDateTime();
            if (!(dateEndTime.isBefore(start) || dateStartTime.isAfter(end))) {
                return false;
            }
            if(start.isEqual(dateStartTime) && end.isEqual(dateEndTime)||dateStartTime.isAfter(start)&&dateEndTime.isBefore(end)) {
                return false;
            }

            if(dateStartTime.isBefore(start) && dateEndTime.isBefore(start)||dateStartTime.isAfter(end) && dateEndTime.isAfter(end)) {
                return true;
            }
        }
        return true;
    }
    @Override
    public void deleteLaboratory(String id) {
        laboratoryRepository.deleteLaboratoryById(id);
    }
}


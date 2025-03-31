package edu.eci.cvds.project.service;

import edu.eci.cvds.project.model.DTO.LaboratoryDTO;
import edu.eci.cvds.project.model.Laboratory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ServicesLab {
    List<Laboratory> getAllLaboratories();
    Optional<Laboratory> getLaboratoryById(String id);
    Laboratory saveLaboratory(LaboratoryDTO laboratoryDTO,String token);
    boolean isLaboratoryAvailable(Laboratory laboratory, LocalDateTime localDateTime);
    void deleteLaboratory(String id);
    Laboratory getLaboratoryByName(String name);
    boolean isLaboratoriesAvailable(Laboratory laboratory,LocalDateTime dateStartTime,LocalDateTime dateEndTime);
}

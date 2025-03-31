package edu.eci.cvds.project.controller;

import edu.eci.cvds.project.model.DTO.LaboratoryDTO;
import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.model.Reservation;
import edu.eci.cvds.project.service.ServicesLab;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para gestionar laboratorios.
 */
@RestController
@RequestMapping("/laboratories")
@CrossOrigin(origins = "*")
public class LaboratoryController {

    @Autowired
    private ServicesLab laboratoryService;

    /**
     * Obtiene la lista de todos los laboratorios.
     * @return Lista de laboratorios.
     */
    @GetMapping("/all")
    public List<Laboratory> getAllLaboratories() {
        return laboratoryService.getAllLaboratories();
    }

    /**
     * Obtiene un laboratorio por su ID.
     * @param id Identificador del laboratorio.
     * @return ResponseEntity con el laboratorio encontrado o un estado 404 si no existe.
     */
    @GetMapping("id/{id}")
    public ResponseEntity<Laboratory> getLaboratoryById(@PathVariable String id) {
        Optional<Laboratory> laboratory = laboratoryService.getLaboratoryById(id);
        if (laboratory.isPresent()) {
            return ResponseEntity.ok(laboratory.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("name/{name}")
    public ResponseEntity<Laboratory> getLaboratoryByName(@PathVariable String name) {
        Laboratory laboratory = laboratoryService.getLaboratoryByName(name);
        if (laboratory != null) {
            return ResponseEntity.ok(laboratory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo laboratorio.
     * @param laboratoryDTO Objeto Laboratory recibido en la solicitud.
     * @return ResponseEntity con el laboratorio creado.
     */
    @PostMapping("/create")
    public ResponseEntity<Laboratory> createLaboratory(@RequestBody LaboratoryDTO laboratoryDTO,@RequestHeader("Authorization") String token ) {
        Laboratory created = laboratoryService.saveLaboratory(laboratoryDTO,token);
        return ResponseEntity.ok(created);
    }
    /**
     * Verifica la disponibilidad de un laboratorio en una fecha y hora específica.
     * @param id Identificador del laboratorio.
     * @param dateTimeString Fecha y hora en formato de cadena.
     * @return ResponseEntity con un mensaje indicando si está disponible o no.
     */
    @GetMapping("avaiable/{id}")
    public ResponseEntity<String> checkLaboratoryAvailability(@PathVariable String id, @RequestParam("dateTime") String dateTimeString) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString);
        Optional<Laboratory> laboratoryOpt = laboratoryService.getLaboratoryById(id);

        if (laboratoryOpt.isEmpty()) {
            return new ResponseEntity<>("Laboratory not found", HttpStatus.NOT_FOUND);
        }

        Laboratory laboratory = laboratoryOpt.get();
        boolean available = laboratoryService.isLaboratoryAvailable(laboratory, dateTime);

        if (available) {
            return new ResponseEntity<>("Laboratory is available", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Laboratory is not available", HttpStatus.CONFLICT);
        }
    }
    /**
     * Verifica la disponibilidad de un laboratorio en una fecha y hora específica.
     * @return ResponseEntity con la lista de laboratorios disponibles en ese fecha y hora especificas.
     */
    @GetMapping("avaiable")
    public ResponseEntity<List<String>> checkLaboratoriesAvailability( @RequestParam("startDateTime") String dateTimeStartString,
                                                                           @RequestParam("endDateTime") String dateTimeEndString)  {
        LocalDateTime dateStartTime = LocalDateTime.parse(dateTimeStartString);
        LocalDateTime dateEndTime = LocalDateTime.parse(dateTimeEndString);
        List<Laboratory> laboratories = laboratoryService.getAllLaboratories();
        List<String> oklaboratories = new ArrayList<>();


        if (laboratories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        for(Laboratory laboratory : laboratories) {
            boolean available = laboratoryService.isLaboratoriesAvailable(laboratory,dateStartTime,dateEndTime);

            if (available) {
                oklaboratories.add(laboratory.getName());
            }
        }
        if (oklaboratories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(oklaboratories,HttpStatus.OK);
    }
}

package edu.eci.cvds.project.repository;

import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.model.Reservation;
import edu.eci.cvds.project.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface LaboratoryMongoRepository extends MongoRepository<Laboratory, String> {
    /**
     * Busca los laboratorios por nombre exacto.
     * @param name el nombre exacto del laboratorio.
     * @return una lista de laboratorios que coinciden con el nombre proporcionado.
     */
    @Query("{ 'name' : ?0 }")
    Laboratory findLaboratoriesByName(String name);

    default Laboratory saveLaboratory(Laboratory laboratory) {
        if(laboratory.getId() == null){
            laboratory.setId(generateId());
        }
        save(laboratory);
        return laboratory;
    }

    /**
     * Método privado para generar un identificador único utilizando {@link UUID}.
     * Este método crea un identificador único al azar para ser usado en la creación de nuevas instancias
     * de laboratorio. El valor generado es una cadena de caracteres representando un UUID.
     * @return un identificador único generado de forma aleatoria como una cadena de texto.
     */
    default String generateId() {
        return UUID.randomUUID().toString();
    }
    default Laboratory findLaboratoriesById(String id) {
        return findById(id).orElse(null);
    }

    default Laboratory updateLaboratory(Laboratory lab) {
        if (!existsById(lab.getId())) {
            throw new RuntimeException("Lab not found");
        }
        lab.setReservations(lab.getReservations());
        save(lab);
        return lab;
    }
    @Override
    default boolean existsById(String id) {
        Laboratory lab = findLaboratoriesById(id);
        return lab != null;
    }

    default void deleteLaboratoryById(String id) {
        if (!existsById(id)) {
            throw new RuntimeException("lab not found");
        }
        deleteById(id);
    }


}

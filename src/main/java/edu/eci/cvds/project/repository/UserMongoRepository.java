package edu.eci.cvds.project.repository;
import edu.eci.cvds.project.model.Laboratory;
import edu.eci.cvds.project.model.Reservation;
import edu.eci.cvds.project.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserMongoRepository extends MongoRepository<User, String> {
    /**
     * Verifica si un usuario existe en la base de datos por su ID.
     * @param id Identificador del usuario.
     * @return true si el usuario existe, false en caso contrario.
     */
    @Override
    default boolean existsById(String id) {
        User user = findUserById(id);
        return user != null;
    }

    /**
     * Genera un identificador único para un usuario utilizando UUID.
     * @return Un identificador único como cadena de texto.
     */
    private String generateId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Guarda un nuevo usuario en la base de datos, asignándole un ID único si no tiene uno.
     * @param user El usuario a guardar.
     * @return El usuario guardado con los datos actualizados.
     */
    default User saveUser(User user) {
        if (user.getId() == null) {
            user.setId(generateId());
        }
        save(user);
        return user;
    }

    /**
     * Busca un usuario por su ID.
     * @param id Identificador del usuario.
     * @return El usuario encontrado o null si no existe.
     */
    default User findUserById(String id) {
        return findById(id).orElse(null);
    }

    /**
     * Obtiene todos los usuarios almacenados en la base de datos.
     * @return Lista de todos los usuarios.
     */
    default List<User> findAllUsers() {
        return findAll();
    }

    /**
     * Elimina un usuario por su ID.
     * @param id Identificador del usuario a eliminar.
     * @throws RuntimeException Si el usuario no se encuentra.
     */
    default void deleteUserById(String id) {
        if (!existsById(id)) {
            throw new RuntimeException("User not found");
        }
        deleteById(id);
    }

    /**
     * Actualiza un usuario existente en la base de datos.
     * @param user El usuario con los nuevos datos.
     * @return El usuario actualizado.
     * @throws RuntimeException Si el usuario no se encuentra en la base de datos.
     */
    default User updateUser(User user) {
        if (!existsById(user.getId())) {
            throw new RuntimeException("User not found");
        }
        user.setReservations(user.getReservations());
        save(user);
        return user;
    }

    /**
     * Busca un usuario por su nombre de usuario.
     * @param username Nombre de usuario.
     * @return El usuario encontrado.
     */
    @Query("{ 'username' : ?0 }")
    User findUserByUsername(String username);

    /**
     * Verifica si existe un usuario con un nombre de usuario específico.
     * @param username Nombre de usuario.
     * @return true si el usuario existe, false en caso contrario.
     */
    boolean existsByUsername(String username);

}

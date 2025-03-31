package edu.eci.cvds.project.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "User")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    public List<Reservation> reservations = new ArrayList<>();
    private Role role;
}




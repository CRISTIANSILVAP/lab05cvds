package edu.eci.cvds.project.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.ArrayList;
import java.util.List;
@EqualsAndHashCode(exclude = "reservations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "Laboratory")
public class Laboratory {
    @Id
    private String id;
    private String name;
    public List<Reservation> reservations = new ArrayList<>();
}

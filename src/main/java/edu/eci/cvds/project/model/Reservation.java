package edu.eci.cvds.project.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Document(collection = "Reservation")
public class Reservation {
    @Id
    private String id;
    private String laboratoryname;
    private String username;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String purpose;
    private boolean Status;
    private Integer priority;

    public boolean getStatus() {
        return Status;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Reservation that = (Reservation) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

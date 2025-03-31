package edu.eci.cvds.project.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReservationDTO {
    private String labName;
    private String username;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String purpose;
    private Integer priority;
}


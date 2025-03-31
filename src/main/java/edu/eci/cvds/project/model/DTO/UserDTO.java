package edu.eci.cvds.project.model.DTO;

import edu.eci.cvds.project.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class UserDTO {
    private String username;
    private Role role;
    private String password;
}

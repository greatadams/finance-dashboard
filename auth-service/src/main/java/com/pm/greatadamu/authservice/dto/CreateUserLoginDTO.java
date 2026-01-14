package com.pm.greatadamu.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
/// USER LOGIN
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserLoginDTO {
    @Email
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8 , max = 25, message ="Password have to have minimum of 8 character and maximum of 25")
    private String password;

}

package com.pm.greatadamu.customerservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {
    @NotBlank(message = "Firstname is required")
    @Size(max = 50, message = "First name must be at most 50 character")
    private String firstName;

    @NotBlank(message = "Lastname is required")
    @Size(max = 50, message = "Last name must be at most 50 character")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Size(max = 50, message = "Email must be at most 50 character")
    @Email
    private String email;

    @NotBlank(message = "Phone-number is required")
    @Size(max = 25, message = "Phone number must be at most 25 character")
    private String phoneNumber;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be at most 255 character")
    private String address;

}

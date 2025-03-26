package com.natixis.commerce.controller.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class UserRequest {
    @NotNull(message = "Name cannot be null")
    private String name;
    @NotNull(message = "LastName cannot be null")
    private String lastName;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid!")
    private String email;
    @NotNull(message = "Password cannot be null")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}

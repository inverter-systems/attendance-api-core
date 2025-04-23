package com.inverter.auth.dto;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.inverter.auth.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
public class UserDTO {

	private Long id;

	@Length(min = 3, max = 50, message = "O username deve conter entre 3 e 50 caracteres")
	@NotNull(message = "O campo 'username' não deve ser nulo")
	private String username;
	
	@Email(message = "Email inválido")
	@NotNull(message = "O campo 'email' não deve ser nulo")
	private String email;
	
	@Length(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
	@NotNull(message = "O campo 'password' não deve ser nulo")
	private String password;
	
	public User buildUser() {
		return User.builder()
				.id(this.id)
				.email(this.email)
				.password(this.password)
				.username(this.username)
				.build();
	}
	
	public UserDTO buildUserDTO(User user) {
		return UserDTO.builder()
				.id(user.getId())
				.email(user.getEmail())
				.username(user.getUsername())
				.build();
	}
}

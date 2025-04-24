package com.inverter;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.inverter.auth.entity.Role;
import com.inverter.auth.entity.User;
import com.inverter.auth.repository.RoleRepository;
import com.inverter.auth.repository.UserRepository;
import com.inverter.auth.util.Bcrypt;

import jakarta.transaction.Transactional;

@Component 
public class InitializationData implements CommandLineRunner {

	private UserRepository repo;
	private RoleRepository repoRole;
	
	public InitializationData(UserRepository repo, RoleRepository repoRole) {
		this.repo = repo;
		this.repoRole = repoRole;
	}

	@Transactional
	public void run(String... args) throws Exception {
		var roles = new ArrayList<Role>();
		Role role = null;
		
		if (repoRole.findByName("ADMINISTRATOR_ROLE").isEmpty()) {
			role = Role.builder()
					   .name("ADMINISTRATOR_ROLE")
					   .description("System administrator, unrestricted access")
					   .build();
			role = repoRole.save(role);
			roles.add(role);
		}
		
		if (repoRole.findByName("CUSTOMER_ROLE").isEmpty()) {
			role = Role.builder()
					   .name("CUSTOMER_ROLE")
					   .description("Customer administrator, unrestricted access to customer functions")
					   .build();
			repoRole.save(role);
		}
		
		if (repoRole.findByName("USER_ROLE").isEmpty()) {
			role = Role.builder()
					   .name("USER_ROLE")
					   .description("System user, access to attendance records and queries")
					   .build();
			repoRole.save(role);
		}
		
		if (repo.findByUsername("Admin").isEmpty()) {
			repo.save(User.builder()
						  .username("Admin")
						  .email("admin@gmail.com.br")						 
						  .password(Bcrypt.getHash("attendance"))
						  .roles(roles)
						  .active(true)
						  .build());
			
		}
	}
}

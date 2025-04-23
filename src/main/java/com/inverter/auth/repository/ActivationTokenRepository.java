package com.inverter.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inverter.auth.entity.ActivationToken;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, String> {

	Optional<ActivationToken> findByToken(String token);
}

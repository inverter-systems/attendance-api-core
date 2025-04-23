package com.inverter.auth.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ACTIVATION_TOKEN")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivationToken implements Serializable {

	private static final long serialVersionUID = 7308726144863081463L;
	
	@Id
	@NotNull
	private String token;
	
	@Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
	
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private User user;
}

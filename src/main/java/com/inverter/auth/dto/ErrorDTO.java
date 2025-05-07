package com.inverter.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorDTO {	
	private String error;
	
	public static ErrorDTO buildError(String error) {
		return ErrorDTO.builder()
				.error(error)
				.build();
	}
}

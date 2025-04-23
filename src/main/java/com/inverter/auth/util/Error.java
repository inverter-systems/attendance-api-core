package com.inverter.auth.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Error {	
	private Integer status;
	private String erro;
	private String timestamp;
	private String message;
	private String path;	
}

package com.inverter.auth.util;

import java.util.ArrayList;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Response<T> {
	private T data;
	private List<String> errors = new ArrayList<>();
}

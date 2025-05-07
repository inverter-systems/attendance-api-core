package com.inverter.auth.enums;

public enum IssueEnum {
	
	ISSUE("inverterApi"),
	ISSUE_ACTIVATION("inverterApiActivationToken"),
	ISSUE_RESET_PASSWORD("inverterApiResetPasswordToken");

	private final String description;
	
	IssueEnum(String description) {
		this.description = description;
	}
	
	public String getDesc() {
		return description;
	}
}

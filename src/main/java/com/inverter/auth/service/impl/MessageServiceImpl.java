package com.inverter.auth.service.impl;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.inverter.auth.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {
	
	private MessageSource messageSource;
	    
	public MessageServiceImpl(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public String get(String msg) {
		return messageSource.getMessage(msg, null, LocaleContextHolder.getLocale());
	}


	public String get(String msg, Object[] obj) {
		return messageSource.getMessage(msg, obj, LocaleContextHolder.getLocale());
	}
}

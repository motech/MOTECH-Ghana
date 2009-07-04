package org.motech.test;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;

@WebService
public class SampleWS {

	@Autowired
	SampleService service;

	@WebMethod
	public void doSomething() {
		service.doSomething();
	}

}

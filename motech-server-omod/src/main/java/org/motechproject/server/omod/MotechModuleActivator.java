/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.motechproject.server.omod;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.advice.AuthenticateAdvice;
import org.motechproject.server.omod.advice.ContextSessionAdvice;
import org.motechproject.server.omod.advice.ProxyPrivilegesAdvice;
import org.motechproject.server.omod.impl.ContextServiceImpl;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.svc.impl.RegistrarBeanImpl;
import org.openmrs.module.Activator;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.aop.framework.ProxyFactory;

/**
 * This class contains the logic that is run every time this module is either
 * started or shutdown. It initializes an OpenMRS installation with the
 * necessary 'stuff' that our module expects to operate in the OpenMRS
 * environment. It does things like adding Concepts for things like a patient
 * phone number, as this is required for sending them SMS messages.
 */
public class MotechModuleActivator implements Activator {

	private Log log = LogFactory.getLog(this.getClass());

	private RegistrarBean registrarBean;

	public MotechModuleActivator() {
		init(true);
	}

	public MotechModuleActivator(boolean useModuleClassLoader) {
		init(useModuleClassLoader);
	}

	/*
	 * Module services and spring context are not available to startup or
	 * shutdown. This is an in-code representation of registrarBeanProxy from
	 * registrar-bean.xml, with the proxy using the openmrs or module
	 * classloader
	 */
	private void init(boolean useModuleClassLoader) {
		ContextService contextService = new ContextServiceImpl();

		ContextSessionAdvice contextSessionAdvice = new ContextSessionAdvice();
		contextSessionAdvice.setContextService(contextService);

		AuthenticateAdvice authenticateAdvice = new AuthenticateAdvice();
		authenticateAdvice.setContextService(contextService);

		ProxyPrivilegesAdvice proxyPrivilegesAdvice = new ProxyPrivilegesAdvice();
		proxyPrivilegesAdvice.setContextService(contextService);

		RegistrarBeanImpl registrarBeanImpl = new RegistrarBeanImpl();
		registrarBeanImpl.setContextService(contextService);

		ProxyFactory registrarBeanProxy = new ProxyFactory();
		registrarBeanProxy.setTarget(registrarBeanImpl);
		registrarBeanProxy.addInterface(RegistrarBean.class);
		registrarBeanProxy.addAdvice(contextSessionAdvice);
		registrarBeanProxy.addAdvice(authenticateAdvice);
		registrarBeanProxy.addAdvice(proxyPrivilegesAdvice);

		ClassLoader classLoader = null;
		if (useModuleClassLoader) {
			Module motechmodule = ModuleFactory.getModuleById("motechmodule");
			classLoader = ModuleFactory.getModuleClassLoader(motechmodule);
		} else {
			classLoader = OpenmrsClassLoader.getInstance();
		}
		registrarBean = (RegistrarBean) registrarBeanProxy
				.getProxy(classLoader);
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}

	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	public void startup() {
		log.info("Starting Motech Module");

		registrarBean.addInitialData();
	}

	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	public void shutdown() {
		log.info("Shutting down Motech Module");

		registrarBean.removeAllTasks();
	}
}

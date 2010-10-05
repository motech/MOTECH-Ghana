/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

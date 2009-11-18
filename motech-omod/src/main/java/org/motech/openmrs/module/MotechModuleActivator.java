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
package org.motech.openmrs.module;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.svc.RegistrarBean;
import org.openmrs.module.Activator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
		// Load Spring configs needed to access bean,
		// module spring config not yet available
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				new String[] { "registrar-bean.xml",
						"common-program-beans.xml", "programs/*.xml" });
		registrarBean = (RegistrarBean) applicationContext
				.getBean("registrarBeanProxy");
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

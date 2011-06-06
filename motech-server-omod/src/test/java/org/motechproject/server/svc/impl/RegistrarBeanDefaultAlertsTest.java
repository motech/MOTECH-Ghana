package org.motechproject.server.svc.impl;

import org.junit.*;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechModuleActivator;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import static org.easymock.EasyMock.createMock;

public class RegistrarBeanDefaultAlertsTest extends BaseModuleContextSensitiveTest {


    static MotechModuleActivator activator;
    private RegistrarBeanImpl registrarBean;
    private ContextService contextService ;

    @BeforeClass
    public static void setUpClass() throws Exception {
        activator = new MotechModuleActivator();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        activator = null;
    }

    @Before
    public void setUp() {
        activator.startup();
        contextService = createMock(ContextService.class);
        registrarBean = new RegistrarBeanImpl();
        registrarBean.setContextService(contextService);
    }

    @After
	public void tearDown() throws Exception {
		activator.shutdown();
	}

    @Test
    @Ignore
    public void defaulterAlertsTest() {
    }

}

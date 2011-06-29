package org.motechproject.server.omod.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.model.MessageProcessorURL;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.motechproject.server.omod.web.model.MappedURLs;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class URLMapControllerTest extends BaseModuleContextSensitiveTest{

    @Autowired
    private MessageProcessorDAO messageProcessorDAO;

    @Before
    public void setUp() throws Exception {
      executeDataSet("incoming-message-data.xml");
    }

    @Test
    public void shouldShowPageWithAllMappedURLs() {
        URLMapController controller = new URLMapController();
        controller.setDao(messageProcessorDAO);
        ModelAndView modelAndView = controller.list();
        assertEquals("/module/motechmodule/urlmap",modelAndView.getViewName());
        MappedURLs mappedURLs = (MappedURLs) modelAndView.getModelMap().get("mappedURLs");
        assertTrue(mappedURLs.has(new MessageProcessorURL("SUPPORT","/module/motechmodule/supportcase.form")));
        assertTrue(mappedURLs.has(new MessageProcessorURL("SEARCH","http://www.google.com")));
    }
}

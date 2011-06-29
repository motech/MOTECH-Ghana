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
import static org.easymock.EasyMock.*;
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

    @Test
    public void shouldAddPlaceholderForNewProcessorURL() {
        URLMapController controller = new URLMapController();
        controller.setDao(messageProcessorDAO);
        ModelAndView modelAndView = controller.add();
        assertEquals("/module/motechmodule/urlmap",modelAndView.getViewName());
        assertTrue((Boolean) modelAndView.getModelMap().get("addNew"));
    }

    @Test
    public void shouldSaveOrUpdateMessageProcessorURLs() {
        MessageProcessorDAO dao = createMock(MessageProcessorDAO.class);
        URLMapController controller = new URLMapController();
        controller.setDao(dao);
        MessageProcessorURL existingURL = new MessageProcessorURL("A", "url:A");
        MessageProcessorURL newURL = new MessageProcessorURL("B", "url:B");

        expect(dao.urlFor("A")).andReturn(existingURL);
        expect(dao.urlFor("B")).andReturn(null);

        dao.update(eq(existingURL));
        expectLastCall();

        dao.save(eq(newURL));
        expectLastCall();

        replay(dao);

        String redirection1 = controller.edit(existingURL);
        String redirection2 = controller.edit(newURL);

        verify(dao);

        assertEquals("redirect:/module/motechmodule/urlmap",redirection1);
        assertEquals("redirect:/module/motechmodule/urlmap",redirection2);
    }
}

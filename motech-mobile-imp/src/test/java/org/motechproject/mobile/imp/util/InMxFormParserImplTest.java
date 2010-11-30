package org.motechproject.mobile.imp.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mobile.core.model.IncomingMessage;
import org.motechproject.mobile.imp.util.exception.MotechParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit Tests for  InMxFormParserImpl class
 *
 *Created: 26-Nov-2010
 *@author Igor Opushnyev (iopushnyev@2paths.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/imp-test-config.xml"})
public class InMxFormParserImplTest {

    @Autowired
    InMxFormParserImpl inMxFormParser;

    @Test
    public void testParseIncomingMessage() throws Exception{

        IncomingMessage incomingMessage = inMxFormParser.parseIncomingMessage(validIncomingXmlString);
        assertEquals(incomingMessage.getContent(), expectedResultString);
    }

    @Test
    @ExpectedException(IllegalArgumentException.class)
    public void testParseNullIncomingMessage() throws Exception{

        inMxFormParser.parseIncomingMessage(null);
    }

    @Test
    public void testGetIncomingMessageContext() throws Exception{



        String context = inMxFormParser.getIncomingMessageContext(validIncomingXmlString);

        assertEquals(expectedResultString, context);
    }

    @Test
    @ExpectedException(IllegalArgumentException.class)
    public void testGetIncomingMessageContextNullMessage() throws Exception {

         inMxFormParser.getIncomingMessageContext(null);
     }

    @Test
    @ExpectedException(MotechParseException.class)
    public void testGetIncomingMessageContextNotXmlMessage() throws Exception {

         inMxFormParser.getIncomingMessageContext(" ");
     }

    @Test
    @ExpectedException(MotechParseException.class)
    public void testGetIncomingMessageContextInvalidXml() throws Exception {

         String incomingXmlString = "<?xml version='1.0' encoding='UTF-8' ?>" +
                "<child id=\"2\" name=\"CWC Visit\" xmlns:xf=\"http://www.w3.org/2002/xforms\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <staffId>1</staffId>\n" +
                "        <formname>childcwc-jf</formname>\n" +
                "      ";

         inMxFormParser.getIncomingMessageContext(incomingXmlString);

     }

    @Test
    @ExpectedException(MotechParseException.class)
    public void testGetIncomingMessageContextNoFormTypeElement() throws Exception {

         String incomingXmlString = "<?xml version='1.0' encoding='UTF-8' ?>" +
                "<child id=\"2\" name=\"CWC Visit\" xmlns:xf=\"http://www.w3.org/2002/xforms\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <staffId>1</staffId>\n" +
                "        <formname>childcwc-jf</formname>\n" +
                "      </child>";


         inMxFormParser.getIncomingMessageContext(incomingXmlString);
     }

    @Test
    @ExpectedException(MotechParseException.class)
    public void testGetIncomingMessageContextNoFormType() throws Exception {

        String incomingXmlString = "<?xml version='1.0' encoding='UTF-8' ?>" +
                "<child id=\"2\" name=\"CWC Visit\" xmlns:xf=\"http://www.w3.org/2002/xforms\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <staffId>1</staffId>\n" +
                "        <facilityId>1234567</facilityId>\n" +
                "        <date>2010-11-25</date>\n" +
                "        <motechId>7654321</motechId>\n" +
                "        <immunization>N</immunization>\n" +
                "        <bcg />\n" +
                "        <opvdose />\n" +
                "        <pentadose />\n" +
                "        <yellowfever />\n" +
                "        <csm />\n" +
                "        <measles />\n" +
                "        <iptidose>NA</iptidose>\n" +
                "        <vitaminA>Y</vitaminA>\n" +
                "        <dewormer>N</dewormer>\n" +
                "        <weight>10</weight>\n" +
                "        <muac />\n" +
                "        <height />\n" +
                "        <maleInvolved>N</maleInvolved>\n" +
                "        <cwcLocation>2</cwcLocation>\n" +
                "        <house>some house</house>\n" +
                "        <community>some comunity</community>\n" +
                "        <comments />\n" +
                "        <formtype></formtype>\n" +
                "        <formname>childcwc-jf</formname>\n" +
                "      </child>";


         inMxFormParser.getIncomingMessageContext(incomingXmlString);
     }

    @Test
    @ExpectedException(MotechParseException.class)
    public void testGetIncomingMessageContextNoFormNameElement() throws Exception {

         String incomingXmlString = "<?xml version='1.0' encoding='UTF-8' ?>" +
                "<child id=\"2\" name=\"CWC Visit\" xmlns:xf=\"http://www.w3.org/2002/xforms\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <staffId>1</staffId>\n" +
                "        <formtype>data_entry</formtype>\n" +
                "      </child>";


         inMxFormParser.getIncomingMessageContext(incomingXmlString);
     }

    @Test
    @ExpectedException(MotechParseException.class)
    public void testGetIncomingMessageContextUnknownFormType() throws Exception {

        String incomingXmlString = "<?xml version='1.0' encoding='UTF-8' ?>" +
                "<child id=\"2\" name=\"CWC Visit\" xmlns:xf=\"http://www.w3.org/2002/xforms\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <staffId>1</staffId>\n" +
                "        <facilityId>1234567</facilityId>\n" +
                "        <date>2010-11-25</date>\n" +
                "        <motechId>7654321</motechId>\n" +
                "        <immunization>N</immunization>\n" +
                "        <bcg />\n" +
                "        <opvdose />\n" +
                "        <pentadose />\n" +
                "        <yellowfever />\n" +
                "        <csm />\n" +
                "        <measles />\n" +
                "        <iptidose>NA</iptidose>\n" +
                "        <vitaminA>Y</vitaminA>\n" +
                "        <dewormer>N</dewormer>\n" +
                "        <weight>10</weight>\n" +
                "        <muac />\n" +
                "        <height />\n" +
                "        <maleInvolved>N</maleInvolved>\n" +
                "        <cwcLocation>2</cwcLocation>\n" +
                "        <house>some house</house>\n" +
                "        <community>some comunity</community>\n" +
                "        <comments />\n" +
                "        <formtype>Unknown</formtype>\n" +
                "        <formname>childcwc-jf</formname>\n" +
                "      </child>";


         inMxFormParser.getIncomingMessageContext(incomingXmlString);
     }

    @Test
    public void testFormatDateString() throws Exception {

        String inDateString = "2010-11-25";
        String formattedDateStr = inMxFormParser.formatData(inDateString);

        Date inDate = inMxFormParser.getImpDateFormat().parse(formattedDateStr);
        Date formattedDate = inMxFormParser.getOxdDateFormat().parse(inDateString);

        assertEquals(inDate, formattedDate);

    }

    @Test
    public void testFormatDataNullParameter() throws Exception {

        assertNull(inMxFormParser.formatData(null));
    }

    @Test
    public void testFormatDataNoFormatting() throws Exception {

        String inData = "In Data";
        String formattedString = inMxFormParser.formatData(inData);

        assertEquals(inData, formattedString);

    }

     private String validIncomingXmlString = "<?xml version='1.0' encoding='UTF-8' ?>" +
                "<child id=\"2\" name=\"CWC Visit\" xmlns:xf=\"http://www.w3.org/2002/xforms\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "        <staffId>1</staffId>\n" +
                "        <facilityId>1234567</facilityId>\n" +
                "        <date>2010-11-25</date>\n" +
                "        <motechId>7654321</motechId>\n" +
                "        <immunization>N</immunization>\n" +
                "        <bcg />\n" +
                "        <opvdose />\n" +
                "        <pentadose />\n" +
                "        <yellowfever />\n" +
                "        <csm />\n" +
                "        <measles />\n" +
                "        <iptidose>NA</iptidose>\n" +
                "        <vitaminA>Y</vitaminA>\n" +
                "        <dewormer>N</dewormer>\n" +
                "        <weight>10</weight>\n" +
                "        <muac />\n" +
                "        <height />\n" +
                "        <maleInvolved>N</maleInvolved>\n" +
                "        <cwcLocation>2</cwcLocation>\n" +
                "        <house>some house</house>\n" +
                "        <community>some comunity</community>\n" +
                "        <comments />\n" +
                "        <formtype>data_entry</formtype>\n" +
                "        <formname>childcwc-jf</formname>\n" +
                "      </child>";

        private String expectedResultString="Type=childcwc-jf\n" +
                "staffId=1\n" +
                "facilityId=1234567\n" +
                "date=25/11/2010\n" +
                "motechId=7654321\n" +
                "immunization=N\n" +
                "bcg=\n" +
                "opvdose=\n" +
                "pentadose=\n" +
                "yellowfever=\n" +
                "csm=\n" +
                "measles=\n" +
                "iptidose=NA\n" +
                "vitaminA=Y\n" +
                "dewormer=N\n" +
                "weight=10\n" +
                "muac=\n" +
                "height=\n" +
                "maleInvolved=N\n" +
                "cwcLocation=2\n" +
                "house=some house\n" +
                "community=some comunity\n" +
                "comments=";
}

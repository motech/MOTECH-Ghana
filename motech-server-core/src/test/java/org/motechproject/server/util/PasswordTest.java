package org.motechproject.server.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PasswordTest {

    @Test
    public void createPasswordWithSpecifiedLength(){
        String password = new Password(10).create();
        assertNotNull(password);
        assertEquals(10,password.length());
    }
}

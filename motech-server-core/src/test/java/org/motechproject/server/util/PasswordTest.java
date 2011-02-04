package org.motechproject.server.util;

import junit.framework.Assert;
import org.junit.Test;

public class PasswordTest {

    @Test
    public void createPasswordWithSpecifiedLength(){
        String password = new Password(10).create();
        Assert.assertNotNull(password);
        Assert.assertEquals(10, password.length());
    }
}

package org.motechproject.server.svc.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.omod.ContextService;
import org.openmrs.User;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class AuthenticationServiceImplTest {

    @Mock
    ContextService contextService;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldDelegateCallToContextService(){
        AuthenticationServiceImpl service = new AuthenticationServiceImpl(contextService);
        User user = new User();
        when(contextService.getAuthenticatedUser()).thenReturn(user);
        User authenticatedUser = service.getAuthenticatedUser();
        verify(contextService).getAuthenticatedUser();
        assertSame(user,authenticatedUser);
    }
}

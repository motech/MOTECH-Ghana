package org.motechproject.server.svc.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.easymock.IArgumentMatcher;
import org.junit.Ignore;
import org.motechproject.server.model.DefaultedExpectedObsAlert;

@Ignore
public class DefaultedExpectedObsAlertMatcher implements IArgumentMatcher{

    private DefaultedExpectedObsAlert expected;

    public DefaultedExpectedObsAlertMatcher(DefaultedExpectedObsAlert expectedObsAlert) {
        this.expected = expectedObsAlert;
    }

    public boolean matches(Object argument) {
        DefaultedExpectedObsAlert actual = (DefaultedExpectedObsAlert) argument;
        return EqualsBuilder.reflectionEquals(expected, actual);
    }

    public void appendTo(StringBuffer buffer) {
        buffer.append("Expected ").append(expected);
    }
}

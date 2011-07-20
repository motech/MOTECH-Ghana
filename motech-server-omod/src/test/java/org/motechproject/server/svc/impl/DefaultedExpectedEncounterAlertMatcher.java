package org.motechproject.server.svc.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.easymock.IArgumentMatcher;
import org.junit.Ignore;
import org.motechproject.server.model.DefaultedExpectedEncounterAlert;

@Ignore
public class DefaultedExpectedEncounterAlertMatcher implements IArgumentMatcher {
    private DefaultedExpectedEncounterAlert expected;

    public DefaultedExpectedEncounterAlertMatcher(DefaultedExpectedEncounterAlert expectedEncounterAlert) {
        this.expected = expectedEncounterAlert;
    }

    public boolean matches(Object argument) {
        DefaultedExpectedEncounterAlert actual = (DefaultedExpectedEncounterAlert) argument;
        return expected.isSameAs(actual);
    }

    public void appendTo(StringBuffer buffer) {
        buffer.append("Expected ").append(expected);
    }
}
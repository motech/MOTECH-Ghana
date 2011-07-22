package org.motechproject.server.omod.tasks;

import org.easymock.IArgumentMatcher;

import java.util.Arrays;
import java.util.List;

public class ArrayMatcher<T> implements IArgumentMatcher {

    private List<T> expected;

    public ArrayMatcher(T[] expected) {
        this.expected = Arrays.asList(expected);
    }

    public boolean matches(Object object) {
        List<T> actual = Arrays.asList((T[]) object);
        if (expected.size() != actual.size()) return false;

        boolean match = true;

        for (T item : expected) {
            match = match && actual.contains(item);
        }

        return match;
    }

    public void appendTo(StringBuffer buffer) {
        buffer.append(expected);
    }
}

package org.motechproject.server.filters.condition;

public interface Condition<E> {

    public Boolean metBy(E e);
}

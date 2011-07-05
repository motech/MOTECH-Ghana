package org.motechproject.server.model.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.motechproject.server.model.MotechUserType;
import org.motechproject.server.model.MotechUserTypes;

public class MotechUsers {

    private SessionFactory sessionFactory;

    public MotechUserTypes types() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MotechUserType.class);
        return new MotechUserTypes(criteria.list());
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

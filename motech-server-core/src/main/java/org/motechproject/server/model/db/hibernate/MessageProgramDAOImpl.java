package org.motechproject.server.model.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.motechproject.server.event.MessageProgram;
import org.motechproject.server.model.ExpectedCareMessageDetails;

import java.util.List;

public class MessageProgramDAOImpl implements MessageProgramDAO{

    SessionFactory sessionFactory;

    public List<ExpectedCareMessageDetails> messageDetails() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(ExpectedCareMessageDetails.class);
        return criteria.list();
    }

    public MessageProgram weeklyProgram(String programName) {
        return null;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}

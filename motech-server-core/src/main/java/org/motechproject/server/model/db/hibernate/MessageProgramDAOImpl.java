package org.motechproject.server.model.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.motechproject.server.model.MessageProgram;
import org.motechproject.server.model.ExpectedCareMessageDetails;
import org.motechproject.server.model.MotechMessageProgram;

import java.util.List;

public class MessageProgramDAOImpl implements MessageProgramDAO{

    SessionFactory sessionFactory;

    public List<ExpectedCareMessageDetails> messageDetails() {
        Session session = currentSession();
        Criteria criteria = session.createCriteria(ExpectedCareMessageDetails.class);
        return criteria.list();
    }

    public MessageProgram weeklyProgram(String programName) {
        Session session = currentSession();
        Criteria criteria = session.createCriteria(MotechMessageProgram.class);
        criteria.add(Restrictions.eq("name",programName));
        return (MessageProgram) criteria.uniqueResult();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session currentSession() {
        Session session = sessionFactory.getCurrentSession();
        return session;
    }
}

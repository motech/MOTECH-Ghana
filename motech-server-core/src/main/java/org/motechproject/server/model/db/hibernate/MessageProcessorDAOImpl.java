package org.motechproject.server.model.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.motechproject.server.model.MessageProcessorURL;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MessageProcessorDAOImpl implements MessageProcessorDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public MessageProcessorURL urlFor(String keyword) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MessageProcessorURL.class);
        criteria.add(Restrictions.eq("key",keyword));
        return (MessageProcessorURL) criteria.uniqueResult();
    }

    public List<MessageProcessorURL> list() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MessageProcessorURL.class);
        return criteria.list();
    }

    public void update(Object obj) {
        sessionFactory.getCurrentSession().update(obj);
    }

    public void save(Object obj) {
        sessionFactory.getCurrentSession().save(obj);
    }
}

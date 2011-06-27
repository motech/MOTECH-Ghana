package org.motechproject.server.model.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.motechproject.server.model.MessageProcessorURL;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.springframework.beans.factory.annotation.Autowired;

public class MessageProcessorDAOImpl implements MessageProcessorDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public MessageProcessorURL urlFor(String keyword) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(MessageProcessorURL.class);
        criteria.add(Restrictions.eq("key",keyword));
        MessageProcessorURL result = (MessageProcessorURL) criteria.uniqueResult();
        return null != result ? result : new MessageProcessorURL();
    }
}

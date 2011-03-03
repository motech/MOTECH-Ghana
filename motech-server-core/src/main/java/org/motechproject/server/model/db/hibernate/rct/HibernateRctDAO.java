package org.motechproject.server.model.db.hibernate.rct;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.db.RctDAO;
import org.motechproject.server.model.rct.PhoneOwnershipType;
import org.motechproject.server.model.rct.RCTPatient;
import org.motechproject.server.model.rct.Stratum;
import org.motechproject.ws.rct.PregnancyTrimester;

public class HibernateRctDAO implements RctDAO {

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public Stratum stratumWith(Facility facility, PhoneOwnershipType phoneOwnershipType, PregnancyTrimester trimester) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Stratum.class, "s");
        criteria.add(Restrictions.eq("s.phoneOwnership", phoneOwnershipType));
        criteria.add(Restrictions.eq("s.pregnancyTrimester", trimester));
        criteria.add(Restrictions.eq("s.facility", facility));
        return (Stratum) criteria.uniqueResult();
    }

    public RCTPatient saveRCTPatient(RCTPatient patient) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(patient);
        return patient;
    }

    public Stratum updateStratum(Stratum stratum) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(stratum);
        return stratum;
    }
}

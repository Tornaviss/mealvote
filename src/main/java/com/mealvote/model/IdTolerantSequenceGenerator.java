package com.mealvote.model;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.io.Serializable;

public class IdTolerantSequenceGenerator extends SequenceStyleGenerator {

    /** If the entity already has an id method keeps the id unchanged,
     *  if object's id == null then generates a new value using sequence passed through SEQUENCE_PARAM
    */
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        Serializable id = session.getEntityPersister(null, object)
                .getClassMetadata().getIdentifier(object, session);
        return id != null ? id : super.generate(session, object);
    }
}

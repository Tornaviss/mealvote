package com.mealvote.model;

import com.mealvote.HasId;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class AbstractBaseEntity implements HasId {
    public static final int START_SEQ = 100000;
    private static final String START_SEQ_STR = "100000";

    @Id
    @GenericGenerator(name = "custom_generator", strategy = "com.mealvote.model.IdTolerantSequenceGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = IdTolerantSequenceGenerator.SEQUENCE_PARAM, value = "global_seq"),
                    @org.hibernate.annotations.Parameter(name = IdTolerantSequenceGenerator.INCREMENT_PARAM, value = "1"),
                    @org.hibernate.annotations.Parameter(name = IdTolerantSequenceGenerator.INITIAL_PARAM, value = START_SEQ_STR)
            })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "custom_generator")
    //  https://hibernate.atlassian.net/browse/HHH-12034
    //  Proxy initialization when accessing its identifier managed now by JPA_PROXY_COMPLIANCE setting
    protected Integer id;

    public AbstractBaseEntity() {
    }

    public AbstractBaseEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !getClass().equals(Hibernate.getClass(o))) return false;
        AbstractBaseEntity that = (AbstractBaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AbstractBaseEntity{" +
                "id=" + id +
                '}';
    }
}

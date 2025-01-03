/*
 * This file is generated by jOOQ.
 */
package jooq.tables.records;


import java.time.LocalDateTime;

import jooq.tables.Collisions;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class CollisionsRecord extends UpdatableRecordImpl<CollisionsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.collisions.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.collisions.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.collisions.involved_planes</code>.
     */
    public void setInvolvedPlanes(String[] value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.collisions.involved_planes</code>.
     */
    public String[] getInvolvedPlanes() {
        return (String[]) get(1);
    }

    /**
     * Setter for <code>public.collisions.time</code>.
     */
    public void setTime(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.collisions.time</code>.
     */
    public LocalDateTime getTime() {
        return (LocalDateTime) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached CollisionsRecord
     */
    public CollisionsRecord() {
        super(Collisions.COLLISIONS);
    }

    /**
     * Create a detached, initialised CollisionsRecord
     */
    public CollisionsRecord(Integer id, String[] involvedPlanes, LocalDateTime time) {
        super(Collisions.COLLISIONS);

        setId(id);
        setInvolvedPlanes(involvedPlanes);
        setTime(time);
        resetChangedOnNotNull();
    }
}

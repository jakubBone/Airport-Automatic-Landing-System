/*
 * This file is generated by jOOQ.
 */
package jooq.tables.records;


import java.time.LocalDateTime;

import jooq.tables.Planes;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class PlanesRecord extends UpdatableRecordImpl<PlanesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.planes.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.planes.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.planes.flight_number</code>.
     */
    public void setFlightNumber(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.planes.flight_number</code>.
     */
    public String getFlightNumber() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.planes.start_time</code>.
     */
    public void setStartTime(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.planes.start_time</code>.
     */
    public LocalDateTime getStartTime() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>public.planes.landing_time</code>.
     */
    public void setLandingTime(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.planes.landing_time</code>.
     */
    public LocalDateTime getLandingTime() {
        return (LocalDateTime) get(3);
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
     * Create a detached PlanesRecord
     */
    public PlanesRecord() {
        super(Planes.PLANES);
    }

    /**
     * Create a detached, initialised PlanesRecord
     */
    public PlanesRecord(Integer id, String flightNumber, LocalDateTime startTime, LocalDateTime landingTime) {
        super(Planes.PLANES);

        setId(id);
        setFlightNumber(flightNumber);
        setStartTime(startTime);
        setLandingTime(landingTime);
        resetChangedOnNotNull();
    }
}

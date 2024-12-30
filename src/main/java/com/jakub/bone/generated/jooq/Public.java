/*
 * This file is generated by jOOQ.
 */
package jooq;


import java.util.Arrays;
import java.util.List;

import jooq.tables.Collisions;
import jooq.tables.Planes;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.collisions</code>.
     */
    public final Collisions COLLISIONS = Collisions.COLLISIONS;

    /**
     * The table <code>public.planes</code>.
     */
    public final Planes PLANES = Planes.PLANES;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Collisions.COLLISIONS,
            Planes.PLANES
        );
    }
}

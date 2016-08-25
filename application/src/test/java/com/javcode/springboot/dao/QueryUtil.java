package com.javcode.springboot.dao;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertQuery;
import org.jooq.Query;
import org.jooq.Table;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class QueryUtil {

    public static Builder<Field, Object> paramBuilder() {
        return ImmutableMap.builder();
    }

    public static long insert(final DSLContext dsl, final Table table,
            final Map<Field, Object> params) {
        final Query query = createInsertQuery(dsl, table, params);
        query.execute();
        return dsl.lastID().longValue();
    }

    public static Query createInsertQuery(final DSLContext dsl,
            final Table table, final Map<Field, Object> params) {
        Validate.notEmpty(params, "insert query params is empty");
        final InsertQuery query = dsl.insertQuery(table);
        query.addValues(params);
        return query;
    }
}

package ru.sbt.orient;

import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.tinkerpop.blueprints.impls.orient.OrientDynaElementIterable;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Collections.EMPTY_LIST;

/**
 * Created by gosha-user on 23.07.2016.
 */
public class Query {
    private static final Logger     LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static List<OrientVertex> executeQuery (String sQuery, Map params) {
        try (AGraph graph = new AGraph(Database.pool().getTx())) {
            OSQLSynchQuery query = new OSQLSynchQuery(sQuery);
            OrientDynaElementIterable result = graph.get().command(query).execute(params);
            final List<OrientVertex> res = new LinkedList();
            result.forEach(obj -> {
                OrientVertex vtx = (OrientVertex) obj;
                res.add(vtx);
            });

            return res;
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }

        return EMPTY_LIST;
    }
    
    public static List<OrientVertex> executeQuery (OrientGraph graph, String sQuery, Map params) {
        OSQLSynchQuery query = new OSQLSynchQuery(sQuery);
        OrientDynaElementIterable result = graph.command(query).execute(params);
        final List<OrientVertex> res = new LinkedList();
        result.forEach(obj -> {
            OrientVertex vtx = (OrientVertex) obj;
            res.add(vtx);
        });

        return res;
    }
}

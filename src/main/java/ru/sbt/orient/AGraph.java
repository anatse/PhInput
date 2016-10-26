package ru.sbt.orient;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by gosha-user on 23.07.2016.
 */
public class AGraph implements Closeable {
    private final OrientGraph graph;

    public AGraph(OrientGraph graph) {
        this.graph = graph;
    }

    @Override
    public void close() throws IOException {
        graph.shutdown();
    }

    public OrientGraph get() {
        return graph;
    }
}

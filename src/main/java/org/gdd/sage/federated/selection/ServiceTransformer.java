package org.gdd.sage.federated.selection;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.TransformCopy;
import org.apache.jena.sparql.algebra.Transformer;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpService;

import java.util.LinkedList;
import java.util.List;

/**
 * Collect all URIS from SERVICE clauses, and peform data localization on each BGP in each SERVICE clause
 * @author Thomas Minier
 */
public class ServiceTransformer extends TransformCopy {
    private List<String> uris;

    public ServiceTransformer() {
        super();
        uris = new LinkedList<>();
    }

    public List<String> getUris() {
        return uris;
    }

    @Override
    public Op transform(OpService opService, Op subOp) {
        String uri = opService.getService().getURI();
        boolean silent = opService.getSilent();
        uris.add(uri);
        BasicPatternLocalizer bgpLocalizer = new BasicPatternLocalizer(uri, silent);
        return new OpGraph(NodeFactory.createURI(uri), Transformer.transform(bgpLocalizer, subOp));
    }
}

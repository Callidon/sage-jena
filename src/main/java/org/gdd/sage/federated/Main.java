package org.gdd.sage.federated;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.Transformer;
import org.apache.jena.sparql.core.BasicPattern;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.engine.main.StageBuilder;
import org.apache.jena.sparql.util.QueryExecUtils;
import org.gdd.sage.engine.SageStageGenerator;
import org.gdd.sage.federated.factory.FederatedQueryFactory;
import org.gdd.sage.federated.factory.ServiceFederatedQueryFactory;

public class Main {
    public static void main(String[] args) {
        String q = "PREFIX bsbm-inst: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/>\n" +
                "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                "\n" +
                "SELECT ?label ?comment ?producer ?productFeature ?propertyTextual1 ?propertyTextual2 ?propertyTextual3\n" +
                " ?propertyNumeric1 ?propertyNumeric2 ?propertyTextual4 ?propertyTextual5 ?propertyNumeric4 \n" +
                "WHERE {\n" +
                "    SERVICE <http://localhost:8000/sparql/bsbm1k> { <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> rdfs:label ?label .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> rdfs:comment ?comment .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:producer ?p .\n" +
                "    ?p rdfs:label ?producer .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> dc:publisher ?p . \n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productFeature ?f .\n" +
                "    ?f rdfs:label ?productFeature . } \n" +
                "    SERVICE <http://localhost:8001/sparql/bsbm1k> { <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyTextual1 ?propertyTextual1 .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyTextual2 ?propertyTextual2 .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyTextual3 ?propertyTextual3 .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyNumeric1 ?propertyNumeric1 .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyNumeric2 ?propertyNumeric2 .\n" +
                "}}\n";
        Query query = QueryFactory.create(q);
        FederatedQueryFactory factory = new ServiceFederatedQueryFactory("http://localhost:8000/sparql/bsbm1k", query);
        factory.buildFederation();
        Op localizedQuery = factory.getLocalizedQuery();
        DatasetGraph federation = factory.getFederationDataset();
        StageBuilder.setGenerator(ARQ.getContext(), SageStageGenerator.createDefault());
        /*QueryIterator qIter = Algebra.exec(localizedQuery, federation);
        qIter.forEachRemaining(System.out::println);*/
        QueryExecUtils.execute(localizedQuery, federation);
        federation.close();
    }
}

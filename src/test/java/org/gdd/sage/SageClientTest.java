package org.gdd.sage;

import org.apache.jena.query.*;
import org.apache.jena.sparql.algebra.Algebra;
import org.gdd.sage.engine.SageExecutionContext;
import org.gdd.sage.federated.factory.FederatedQueryFactory;
import org.gdd.sage.federated.factory.ServiceFederatedQueryFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SageClientTest {

    @Ignore
    @Test
    public void bsbsmQuery() {
        String url = "http://localhost:8000/sparql/bsbm1M";
        String queryString = "PREFIX bsbm-inst: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/>\n" +
                "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n" +
                "\n" +
                "SELECT ?label ?comment ?producer ?productFeature ?propertyTextual1 ?propertyTextual2 ?propertyTextual3\n" +
                " ?propertyNumeric1 ?propertyNumeric2 ?propertyTextual4 ?propertyTextual5 ?propertyNumeric4 \n" +
                "WHERE {\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> rdfs:label ?label .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> rdfs:comment ?comment .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:producer ?p .\n" +
                "    ?p rdfs:label ?producer .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> dc:publisher ?p . \n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productFeature ?f .\n" +
                "    ?f rdfs:label ?productFeature .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyTextual1 ?propertyTextual1 .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyTextual2 ?propertyTextual2 .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyTextual3 ?propertyTextual3 .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyNumeric1 ?propertyNumeric1 .\n" +
                "    <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyNumeric2 ?propertyNumeric2 .\n" +
                "    OPTIONAL { <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyTextual1 ?propertyTextual4 }\n" +
                "    OPTIONAL { <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyTextual5 ?propertyTextual5 }\n" +
                "    OPTIONAL { <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/dataFromProducer6/Product216> bsbm:productPropertyNumeric4 ?propertyNumeric4 }\n" +
                "}\n";
        Query query = QueryFactory.create(queryString);
        FederatedQueryFactory factory = new ServiceFederatedQueryFactory(url, query);
        factory.buildFederation();
        query = factory.getLocalizedQuery();
        Dataset dataset = factory.getFederationDataset();
        SageExecutionContext.configureDefault(ARQ.getContext());
        try(QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
            ResultSet results = qexec.execSelect();
            List<QuerySolution> solutions = new ArrayList<>();
            results.forEachRemaining(querySolution -> {
                System.out.println(querySolution);
                assertTrue("?propertyTextual4 should be bounded", querySolution.contains("propertyTextual4"));
                assertFalse("?propertyTextual5 should not be bounded", querySolution.contains("propertyTextual5"));
                assertFalse("?propertyNumeric4 should not be bounded", querySolution.contains("propertyNumeric4"));
                solutions.add(querySolution);
            });
            assertEquals("It should find 22 solutions bindings", 22, solutions.size());
        }
    }

    @Ignore
    @Test
    public void federatedQuery() {
        String url = "http://sage.univ-nantes.fr/sparql/dbpedia-2016-04";
        String queryString = "PREFIX dbp: <http://dbpedia.org/property/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "\n" +
                "SELECT ?titleEng ?title\n" +
                "WHERE {\n" +
                "  ?movie dbp:starring [ rdfs:label 'Natalie Portman'@en ].\n" +
                "  SERVICE <http://sage.univ-nantes.fr/sparql/dbpedia-2015-04en> {\n" +
                "    ?movie rdfs:label ?titleEng, ?title.\n" +
                "  }\n" +
                "  FILTER LANGMATCHES(LANG(?titleEng), 'EN')\n" +
                "  FILTER (!LANGMATCHES(LANG(?title), 'EN'))\n" +
                "}\n";
        Query query = QueryFactory.create(queryString);
        FederatedQueryFactory factory = new ServiceFederatedQueryFactory(url, query);
        factory.buildFederation();
        query = factory.getLocalizedQuery();
        Dataset dataset = factory.getFederationDataset();
        SageExecutionContext.configureDefault(ARQ.getContext());
        try(QueryExecution qexec = QueryExecutionFactory.create(query, dataset)) {
            ResultSet results = qexec.execSelect();
            List<QuerySolution> solutions = new ArrayList<>();
            results.forEachRemaining(querySolution -> {
                System.out.println(querySolution);
                solutions.add(querySolution);
            });
            assertEquals("It should find 218 solutions bindings", 218, solutions.size());
        }
    }
}

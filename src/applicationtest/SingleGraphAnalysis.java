/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationtest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.statistics.plugin.*;
import org.openide.util.Exceptions;

/**
 *
 * @author Matt Groeninger (mgroeninger@gmail.com)
 */
public class SingleGraphAnalysis {

    public String graphName = ""; //used for title, etc
    GraphModel graphModel;
    AttributeModel attributeModel;
    Graph graph;
    ConnectedComponents componentObj = new ConnectedComponents();
    GraphDistance distanceObj = new GraphDistance();
    ClusteringCoefficient clusteringObj = new ClusteringCoefficient();
    EigenvectorCentrality eigenvectorObj = new EigenvectorCentrality();
    Degree degreeObj = new Degree();
    Modularity modularityObj = new Modularity();
    Hits HITSObj = new Hits();
    GraphDensity densityObj = new GraphDensity();
    boolean directed = false;
    HashMap<String, Boolean> execbool = new HashMap<String, Boolean>();
    HashMap<String, String> moduleMap = new HashMap<String, String>();

    public SingleGraphAnalysis(String graphname,GraphModel graphModel, AttributeModel attributeModel) {
        this.graphName = graphname;
        this.graphModel = graphModel;
        this.attributeModel = attributeModel;
        this.graph = graphModel.getGraph();
        execbool.put("GraphDistance", false);
        execbool.put("EigenvectorCentrality", false);
        execbool.put("ConnectedComponents", false);
        execbool.put("Modularity", false);
        execbool.put("ClusteringCoefficient", false);
        execbool.put("Degree", false);
        execbool.put("Hits", false);
        execbool.put("GraphDensity", false);

        moduleMap.put(GraphDistance.BETWEENNESS, "GraphDistance");
        moduleMap.put(GraphDistance.CLOSENESS, "GraphDistance");
        moduleMap.put(GraphDistance.ECCENTRICITY, "GraphDistance");
        moduleMap.put("Eigenvector Centrality", "EigenvectorCentrality");
        moduleMap.put(ConnectedComponents.STRONG, "ConnectedComponents");
        moduleMap.put(ConnectedComponents.WEAKLY, "ConnectedComponents");
        moduleMap.put(Modularity.MODULARITY_CLASS, "Modularity");
        moduleMap.put(ClusteringCoefficient.CLUSTERING_COEFF, "ClusteringCoefficient");
        moduleMap.put("Number of triangles", "ClusteringCoefficient");
        moduleMap.put(Degree.DEGREE, "Degree");
        moduleMap.put(Degree.INDEGREE, "Degree");
        moduleMap.put(Degree.OUTDEGREE, "Degree");
        moduleMap.put(Hits.AUTHORITY, "Hits");
        moduleMap.put(Hits.HUB, "Hits");
        moduleMap.put(PageRank.PAGERANK, "PageRank");
    }

    public double getNodeCount() {
        if (!this.execbool.get("Degree")) {
            ExecuteDegree();
        }
        return this.graph.getNodeCount();
    }

    public double getEdgeCount() {
        if (!this.execbool.get("Degree")) {
            ExecuteDegree();
        }
        return this.graph.getEdgeCount();
    }

    public void ExecuteComponent() {
        if (!this.execbool.get("ConnectedComponents")) {
            componentObj.execute(graphModel, attributeModel);
            this.execbool.put("ConnectedComponents", true);
        }
    }

    public void ExecuteDistance() {
        if (!this.execbool.get("GraphDistance")) {
            distanceObj.setDirected(false);
            distanceObj.execute(graphModel, attributeModel);
            this.execbool.put("GraphDistance", true);
        }
    }

    public void ExecuteClustering() {
        if (!this.execbool.get("ClusteringCoefficient")) {
            clusteringObj.setDirected(this.directed);
            clusteringObj.execute(graphModel, attributeModel);
            this.execbool.put("ClusteringCoefficient", true);
        }
    }

    public void ExecuteEigenCenter() {
        if (!this.execbool.get("EigenvectorCentrality")) {
            eigenvectorObj.execute(graphModel, attributeModel);
            this.execbool.put("EigenvectorCentrality", true);
        }
    }

    public void ExecuteDegree() {
        if (!this.execbool.get("Degree")) {
            degreeObj.execute(graphModel, attributeModel);
            this.execbool.put("Degree", true);
        }
    }

    public void ExecuteModularity() {
        if (!this.execbool.get("Modularity")) {
            degreeObj.execute(graphModel, attributeModel);
            this.execbool.put("Modularity", true);
        }
    }

    public void ExecuteHITS() {
        if (!this.execbool.get("Hits")) {
            HITSObj.execute(graphModel, attributeModel);
            this.execbool.put("Hits", true);
        }
    }

    public void ExecuteDensity() {
        if (!this.execbool.get("GraphDensity")) {
            densityObj.execute(graphModel, attributeModel);
            this.execbool.put("GraphDensity", true);
        }
    }

    public HashMap<Number, Number> GetComponentHash() {
        if (!this.execbool.get("ConnectedComponents")) {
            ExecuteComponent();
        }
        HashMap<Number, Number> componenthash = new HashMap<Number, Number>();
        int[] componentcountarr = this.componentObj.getComponentsSize();
        for (Number component : componentcountarr) {
            if (componenthash.containsKey(component.intValue())) {
                Number value =  componenthash.get(component.intValue());
                componenthash.put(component.intValue(), value.intValue() + 1);
            } else {
                componenthash.put(component.intValue(), 1);
            }
        }
        return componenthash;
    }

    public HashMap<Number, Number> GetColumnHash(String colName) {
        if (this.execbool.get(moduleMap.get(colName))) {
            HashMap<Number, Number> colhash = new HashMap<Number, Number>();
            //Get Centrality column created
            AttributeColumn col = attributeModel.getNodeTable().getColumn(colName);
            //Iterate over values
            for (Node n : this.graph.getNodes()) {
                Number colvalue = (Number) n.getNodeData().getAttributes().getValue(col.getIndex());
                if (colhash.containsKey(colvalue)) {
                    Integer value = (Integer) colhash.get(colvalue);
                    colhash.put(colvalue, value + 1);
                } else {
                    colhash.put(colvalue, 1);
                }
            }
            return colhash;
        }
        return null;
    }
    
    public HashMap<Number, Number> GetColumnNormalHash(String colName,double normValue) {
        if (this.execbool.get(moduleMap.get(colName))) {
            HashMap<Number, Number> colhash = new HashMap<Number, Number>();
            HashMap<Number, Number> normhash = new HashMap<Number, Number>();
            //Get Centrality column created
            AttributeColumn col = attributeModel.getNodeTable().getColumn(colName);
            //Iterate over values
            for (Node n : this.graph.getNodes()) {
                Number colvalue = (Number) n.getNodeData().getAttributes().getValue(col.getIndex());
                if (colhash.containsKey(colvalue)) {
                    Integer value = (Integer) colhash.get(colvalue);
                    colhash.put(colvalue, value + 1);
                } else {
                    colhash.put(colvalue, 1);
                }
            }
            Set countset = colhash.entrySet();
            Iterator countiter = countset.iterator();
            while (countiter.hasNext()) {
                Map.Entry mentry = (Map.Entry) countiter.next();
                Number key = Number.class.cast(mentry.getKey()).doubleValue()/normValue;
                Number value = (Number) mentry.getValue();
                normhash.put(key, value);
            }
            return normhash;
        }
        return null;
    }    
    
    public double GetAvgColumn(String colName) {
        if (this.execbool.get(moduleMap.get(colName))) {
            //Get Centrality column created
            AttributeColumn col = attributeModel.getNodeTable().getColumn(colName);

            double sum = 0;
            int i = 0;
            //Iterate over values
            for (Node n : this.graph.getNodes()) {
                i++;
                Number colvalue = (Number) n.getNodeData().getAttributes().getValue(col.getIndex());
                sum += colvalue.doubleValue();
            }
            return sum/i;
        }
        return 0;
    }

    public HashMap<Number, Number> GetDegreeHash() {
        if (!this.execbool.get("Degree")) {
            ExecuteDegree();
        }
        return GetColumnHash(Degree.DEGREE);
    }
    
    public HashMap<Number, Number> GetTriangleHash() {
        if (!this.execbool.get("ClusteringCoefficient")) {
            ExecuteClustering();
        }
        return GetColumnHash("Number of triangles");
    }    
    public HashMap<Number, Number> GetDegreeNormHash() {
        if (!this.execbool.get("Degree")) {
            ExecuteDegree();
        }
        return GetColumnNormalHash(Degree.DEGREE,this.graph.getEdgeCount());
    }
    
    public HashMap<Number, Number> GetInDegreeHash() {
        if (!this.execbool.get("Degree")) {
            ExecuteDegree();
        }
        return GetColumnHash(Degree.INDEGREE);
    }

    public HashMap<Number, Number> GetOutDegreeHash() {
        if (!this.execbool.get("Degree")) {
            ExecuteDegree();
        }
        return GetColumnHash(Degree.OUTDEGREE);
    }

    public HashMap<Number, Number> GetBetweennessHash() {
        if (!this.execbool.get("GraphDistance")) {
            ExecuteDistance();
        }
        return GetColumnHash(GraphDistance.BETWEENNESS);
    }

    public HashMap<Number, Number> GetClosenessHash() {
        if (!this.execbool.get("GraphDistance")) {
            ExecuteDistance();
        }
        return GetColumnHash(GraphDistance.CLOSENESS);
    }

    public HashMap<Number, Number> GetEccentricityHash() {
        if (!this.execbool.get("GraphDistance")) {
            ExecuteDistance();
        }
        return GetColumnHash(GraphDistance.ECCENTRICITY);
    }

    public HashMap<Number, Number> GetEigenvectorHash() {
        if (!this.execbool.get("EigenvectorCentrality")) {
            ExecuteEigenCenter();
        }
        return GetColumnHash("Eigenvector Centrality");
    }

    public HashMap<Number, Number> GetWeaklyConnectedHash() {
        if (!this.execbool.get("ConnectedComponents")) {
            ExecuteComponent();
        }
        return GetColumnHash(ConnectedComponents.WEAKLY);
    }

    public HashMap<Number, Number> GetStronlyConnectedHash() {
        if (!this.execbool.get("ConnectedComponents")) {
            ExecuteComponent();
        }
        return GetColumnHash(ConnectedComponents.STRONG);
    }

    public HashMap<Number, Number> GetModularityHash() {
        if (!this.execbool.get("Modularity")) {
            ExecuteModularity();
        }
        return GetColumnHash(Modularity.MODULARITY_CLASS);
    }

    public HashMap<Number, Number> GetClusteringHash() {
        if (!this.execbool.get("ClusteringCoefficient")) {
            ExecuteClustering();
        }
        return GetColumnHash(ClusteringCoefficient.CLUSTERING_COEFF);
    }

    public HashMap<Number, Number> GetHubHash() {
        if (!this.execbool.get("Hits")) {
            ExecuteHITS();
        }
        return GetColumnHash(Hits.HUB);
    }

    public HashMap<Number, Number> GetAuthorityHash() {
        if (!this.execbool.get("Hits")) {
            ExecuteHITS();
        }
        return GetColumnHash(Hits.AUTHORITY);
    }

    public double getComponentCount() {
        if (!this.execbool.get("ConnectedComponents")) {
            ExecuteComponent();
        }
        return this.componentObj.getConnectedComponentsCount();
    }
    
    public double getAvgComponentSize() {
        if (!this.execbool.get("ConnectedComponents")) {
            ExecuteComponent();
        }
        int[] componentcountarr = this.componentObj.getComponentsSize();
        int componentSum = 0;
        for (int component : componentcountarr) {
            componentSum += component;
        }
        if (componentcountarr.length!=0) {
            return componentSum/componentcountarr.length;
        } else {
            return 0;
        }
              
    }
    
    public double getGiantComponent() {
        if (!this.execbool.get("ConnectedComponents")) {
            ExecuteComponent();
        }
        return this.componentObj.getGiantComponent();
    }

    public double getPathLength() {
        if (!this.execbool.get("GraphDistance")) {
            ExecuteDistance();
        }
        return this.distanceObj.getPathLength();

    }

    public double getDiameter() {
        if (!this.execbool.get("GraphDistance")) {
            ExecuteDistance();
        }
        return this.distanceObj.getDiameter();
    }

    public double getAverageDegree() {
        if (!this.execbool.get("Degree")) {
            ExecuteDegree();
        }
        return GetAvgColumn(Degree.DEGREE);
        
        //return this.degreeObj.getAverageDegree();
    }
    
    public double getDensity() {
        if (!this.execbool.get("GraphDensity")) {
            ExecuteDensity();
        }
        return this.densityObj.getDensity();
    }

    public double getAverageClusteringCoefficient() {
        if (!this.execbool.get("ClusteringCoefficient")) {
            ExecuteClustering();
        }
        return this.clusteringObj.getAverageClusteringCoefficient();
    }

    public double getAvgTriangleCount() {
        if (!this.execbool.get("ClusteringCoefficient")) {
            ExecuteClustering();
        }
        return GetAvgColumn("Number of triangles");
    }
    
    public double getAvgBetweeness() {
        if (!this.execbool.get("ClusteringCoefficient")) {
            ExecuteClustering();
        }
        return GetAvgColumn(GraphDistance.BETWEENNESS);
    }
    
    public double getAvgCloseness() {
        if (!this.execbool.get("ClusteringCoefficient")) {
            ExecuteClustering();
        }
        return GetAvgColumn(GraphDistance.CLOSENESS);
    }
    
    public double getAvgEccentricity() {
        if (!this.execbool.get("ClusteringCoefficient")) {
            ExecuteClustering();
        }
        return GetAvgColumn(GraphDistance.ECCENTRICITY);
    }
    
    public void setDirected(boolean value) {
        this.directed = value;
    }

    public boolean getDirected() {
        return this.directed;
    }
    
    public void drawInstanceGraph(HashMap<Number, Number> counts, String seriesname,String fileprefix, String filename, String graphtitle, String x_axis, String y_axis,boolean xInt, boolean yInt, boolean showDatapoint) throws IOException {
        DescrStatImageFile imageoutput = new DescrStatImageFile(counts,seriesname, DescrStatImageFile.GRAPH_XYSERIES, fileprefix+"_n="+getNodeCount(), filename, graphtitle, x_axis, y_axis, false, xInt, yInt, showDatapoint);
        imageoutput.GenerateImage();
    }
    
    public void drawGraphTriangleDist () {
        HashMap<Number, Number> hash = GetTriangleHash();
        try {
            drawInstanceGraph(hash,"Triangles",this.graphName, "triangle_dist", "Occurance of Triangles in "+this.graphName, "Number of Triangles", "Count",true,true,true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public void drawGraphComponentDist () {
        HashMap<Number, Number> hash = GetComponentHash();
        try {
            drawInstanceGraph(hash,"Components",this.graphName, "component_dist", "Component Size in "+this.graphName, "Number of Nodes in Component", "Count",true,true,true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
    public void drawGraphClusterDist () {
        HashMap<Number, Number> hash = GetClusteringHash();
        try {
            drawInstanceGraph(hash,"Cluster Coefficient",this.graphName, "cluster_dist", "Cluster Coefficient Occurance in "+this.graphName, "Cluster Coefficient", "Count",false,true,true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    
     public void drawGraphDegreeDist () {
        HashMap<Number, Number> hash = GetDegreeHash();
        try {
            drawInstanceGraph(hash,"Degree Distribution",this.graphName, "degree_dist", "Degree Occurance in "+this.graphName, "Degree", "Count",true,true,true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
     
     
         public void drawGraphEigenvectorDist () {
        HashMap<Number, Number> hash = GetEigenvectorHash();
        try {
            drawInstanceGraph(hash,"Eigenvector Centrality Distribution",this.graphName, "eigen_dist", "Eigenvalue Centrality Occurance in "+this.graphName, "Eigenvalue Centrality", "Count",false,true,true);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}

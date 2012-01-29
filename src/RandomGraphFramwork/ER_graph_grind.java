/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RandomGraphFramwork;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 *
 * @author Matt Groeninger (mgroeninger@gmail.com)
 */
public class ER_graph_grind {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        int numNodes = 25;
        double alpha = 0;
        double alpha_step = 0.005;
        double max_alpha = 1;
        int testCount = 200;
        
        double expectedEdges = (numNodes*(numNodes-1))/2;
        double expectedDegree = (numNodes-1);


//        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
//        pc.newProject();
//        Workspace workspace = pc.getCurrentWorkspace();
//
//        //Load file
//        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
//        

        //Import file
//          Container container;
//            try {
//                URI resource = URI.create("file:/D:/Users/Matt/Documents/Hobbies/School/CSCI-7000-2011/Project/karate.gml");
//                File file = new File(resource);
//                container = importController.importFile(file);
//                container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED);   //Force UNDIRECTED
//                container.closeLoader();
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                return;
//            }

        DecimalFormat twoDForm = new DecimalFormat("#.####");
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        TreeMap<Number, List<Number>> degreeMap = new TreeMap<Number, List<Number>>();
        TreeMap<Number, List<Number>> degreeExpectedMap = new TreeMap<Number, List<Number>>();
        TreeMap<Number, List<Number>> edgeMap = new TreeMap<Number, List<Number>>();
        TreeMap<Number, List<Number>> edgeExpectedMap = new TreeMap<Number, List<Number>>();
        TreeMap<Number, List<Number>> componentMap = new TreeMap<Number, List<Number>>();
        TreeMap<Number, List<Number>> componentSizeMap = new TreeMap<Number, List<Number>>();
        TreeMap<Number, List<Number>> densityMap = new TreeMap<Number, List<Number>>();
        TreeMap<Number, List<Number>> triangleMap = new TreeMap<Number, List<Number>>();
        TreeMap<Number, List<Number>> diameterMap = new TreeMap<Number, List<Number>>();
        TreeMap<Number, List<Number>> clustercoMap = new TreeMap<Number, List<Number>>();
        TreeMap<Number, List<Number>> pathMap = new TreeMap<Number, List<Number>>();
        TreeMap<Number, List<Number>> betweenessMap = new TreeMap<Number, List<Number>>();
        TreeMap<Number, List<Number>> closenessMap = new TreeMap<Number, List<Number>>();
        int order = 0;
        String filePrefix = "ER_n="+numNodes; 
        List entry = null;
        
        while (alpha <= max_alpha) {
            double[] degreeArr = new double[testCount];
            double[] componentsArr = new double[testCount];
            double[] componentSizeArr = new double[testCount];
            double[] pathArr = new double[testCount];
            double[] diameterArr = new double[testCount];
            double[] edgesArr = new double[testCount];
            double[] clustercoArr = new double[testCount];
            double[] densityArr = new double[testCount];
            double[] triangleArr = new double[testCount];
            double[] betweenessArr = new double[testCount];
            double[] closenessArr = new double[testCount];
            FamilyGraphAnalysis degreeDistr = new FamilyGraphAnalysis();       
            pc.newProject();

            for (int i = 0; i < testCount; i++) {
                Workspace workspace = pc.getCurrentWorkspace();
                Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();

                RandomGraph randomGraph = new RandomGraph();
                randomGraph.setNumberOfNodes(numNodes);
                randomGraph.setWiringProbability(alpha);
                randomGraph.generate(container.getLoader());

                ImportController importController = Lookup.getDefault().lookup(ImportController.class);
                importController.process(container, new DefaultProcessor(), workspace);
                container.closeLoader();
                GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
                AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();

                SingleGraphAnalysis analysisObj = new SingleGraphAnalysis("ER_p="+alpha,graphModel, attributeModel);

                System.out.println("Count (" + alpha + "): " + i);
                System.out.println("Nodes: " + analysisObj.getNodeCount());

                degreeArr[i] = analysisObj.getAverageDegree();
                System.out.println(degreeArr[i]);
                degreeDistr.addSeries(analysisObj.GetDegreeHash());
                edgesArr[i] = analysisObj.getEdgeCount();
                componentsArr[i] = analysisObj.getComponentCount();
                componentSizeArr[i] = analysisObj.getAvgComponentSize();
                pathArr[i] = analysisObj.getPathLength();
                diameterArr[i] = analysisObj.getDiameter();
                clustercoArr[i] = analysisObj.getAverageClusteringCoefficient();
                triangleArr[i] = analysisObj.getAvgTriangleCount();
                betweenessArr[i] = analysisObj.getAvgBetweeness();
                densityArr[i] = analysisObj.getDensity();
                closenessArr[i] = analysisObj.getAvgCloseness();
//                CreateLayoutPDF graphoutput = new CreateLayoutPDF(graphModel,workspace);
//                graphoutput.DoCombinedLayout(1);
//                graphoutput.DoColorPartition("Degree");
//                graphoutput.CreateFile("graph_degree_"+alpha);
//                graphoutput.DoColorPartition("Triangles");
//                graphoutput.CreateFile("graph_triangles_"+alpha);
//                graphoutput.DoColorPartition("Component ID");
//                graphoutput.CreateFile("graph_component_"+alpha);
//                graphoutput = null;

                analysisObj = null;
                importController = null;
                container = null;
                graphModel.destroyView(graphModel.getVisibleView());
                graphModel.clear();
                pc.cleanWorkspace(workspace);
            }
            pc.closeCurrentProject();
            pc.removeProject(pc.getCurrentProject());

            
            entry = new ArrayList<Number>();
            entry.add(0, expectedDegree*alpha);
            entry.add(1, 0);
            degreeExpectedMap.put(alpha,entry); 
            
            
            List avgList = getAverageRange(degreeArr);
            degreeMap.put(alpha, avgList);
            DescrStatImageFile testoutput = new DescrStatImageFile(degreeMap, "N=" + testCount, DescrStatImageFile.GRAPH_XYSERIES, filePrefix+"_N=" + testCount + "_step=" + alpha_step, "degree", "Degree distribution (n="+numNodes+")", "p", "Average Degree", false);
            testoutput.NestedHash2Series(degreeExpectedMap, "Expected Average Degree");                 
            testoutput.GenerateImage();

            List edgeList = getAverageRange(edgesArr);
            edgeMap.put(alpha, edgeList);

            entry = new ArrayList<Number>();
            entry.add(0, expectedEdges*alpha);
            entry.add(1, 0);
            edgeExpectedMap.put(alpha,entry); 
            
            testoutput = new DescrStatImageFile(edgeMap, "N=" + testCount, DescrStatImageFile.GRAPH_XYSERIES, filePrefix+"_N=" + testCount + "_step=" + alpha_step, "edge", "Edge distribution (n="+numNodes+")", "p", "Edge Count", false);
            testoutput.NestedHash2Series(edgeExpectedMap, "Expected Number of Edges");
            testoutput.GenerateImage();

            List componentList = getAverageRange(componentsArr);
            componentMap.put(alpha, componentList);
            testoutput = new DescrStatImageFile(componentMap, "N=" + testCount, DescrStatImageFile.GRAPH_XYSERIES, filePrefix+"_N=" + testCount + "_step=" + alpha_step, "component", "Component Count (n="+numNodes+")", "p", "Component Count", false);
            testoutput.GenerateImage();


            List componentSizeList = getAverageRange(componentSizeArr);
            componentSizeMap.put(alpha, componentSizeList);
            testoutput = new DescrStatImageFile(componentSizeMap, "N=" + testCount, DescrStatImageFile.GRAPH_XYSERIES, filePrefix+"_N=" + testCount + "_step=" + alpha_step, "component_size", "Average Component Size (n="+numNodes+")", "p", "Component Size", false);
            testoutput.GenerateImage();


            List diameterList = getAverageRange(diameterArr);
            diameterMap.put(alpha, diameterList);
            testoutput = new DescrStatImageFile(diameterMap, "N=" + testCount, DescrStatImageFile.GRAPH_XYSERIES, filePrefix+"_N=" + testCount + "_step=" + alpha_step, "diameter", "Average Diameter", "p", "Graph Diamater (n="+numNodes+")", false);
            testoutput.GenerateImage();


            List densityList = getAverageRange(densityArr);
            densityMap.put(alpha, densityList);
            testoutput = new DescrStatImageFile(densityMap, "N=" + testCount, DescrStatImageFile.GRAPH_XYSERIES, filePrefix+"_N=" + testCount + "_step=" + alpha_step, "density", "Average Density distribution", "p", "Average Density (n="+numNodes+")", false);
            testoutput.GenerateImage();

            List triangleList = getAverageRange(triangleArr);
            triangleMap.put(alpha, triangleList);
            testoutput = new DescrStatImageFile(triangleMap, "N=" + testCount, DescrStatImageFile.GRAPH_XYSERIES, filePrefix+"_N=" + testCount + "_step=" + alpha_step, "triangle", "Average Triangle count", "p", "Triangle Count (n="+numNodes+")", false);
            testoutput.GenerateImage();

            List clustercoList = getAverageRange(clustercoArr);
            clustercoMap.put(alpha, clustercoList);
            testoutput = new DescrStatImageFile(clustercoMap, "N=" + testCount, DescrStatImageFile.GRAPH_XYSERIES, filePrefix+"_N=" + testCount + "_step=" + alpha_step, "clusterco", "Average Cluster Coefficent", "p", "Cluster Coefficent (n="+numNodes+")", false);
            testoutput.GenerateImage();

            List pathList = getAverageRange(pathArr);
            pathMap.put(alpha, pathList);
            testoutput = new DescrStatImageFile(pathMap, "N=" + testCount, DescrStatImageFile.GRAPH_XYSERIES, filePrefix+"_N=" + testCount + "_step=" + alpha_step, "path", "Average Shortest Path", "p", "Shortest Path (n="+numNodes+")", false);
            testoutput.GenerateImage();

            List betweenessList = getAverageRange(betweenessArr);
            betweenessMap.put(alpha, betweenessList);
            testoutput = new DescrStatImageFile(betweenessMap, "N=" + testCount, DescrStatImageFile.GRAPH_XYSERIES, filePrefix+"_N=" + testCount + "_step=" + alpha_step, "betweeness", "Average Node Betweeness", "p", "Node Betweeness (n="+numNodes+")", false);
            testoutput.GenerateImage();

            List closenessList = getAverageRange(closenessArr);
            closenessMap.put(alpha, closenessList);
            testoutput = new DescrStatImageFile(closenessMap, "N=" + testCount, DescrStatImageFile.GRAPH_XYSERIES, filePrefix+"_N=" + testCount + "_step=" + alpha_step, "closeness", "Average Node Closeness", "p", "Node Closeness (n="+numNodes+")", false);
            testoutput.GenerateImage();
            
            TreeMap<Number, List<Number>> test = degreeDistr.getCombinedSeries();
            testoutput = new DescrStatImageFile(test, "N=" + testCount+", p=" + alpha, DescrStatImageFile.GRAPH_XYSERIES, filePrefix+"O="+order+"_N=" + testCount + "_p=" + alpha, "degree_avg", "Average Degree Distribution (n="+numNodes+")", "Degree", "Count", false, true,true,true,true);
            testoutput.GenerateImage();
            
            alpha = alpha + alpha_step;
            alpha = Double.valueOf(twoDForm.format(alpha));
            order++;
        }
    }

    static List getAverageRange(double[] dataArr) {
        int dataCount = dataArr.length;
        double mean = 0;
        double dev = 0;

        for (int j = 0; j < dataCount; j++) {
            mean += dataArr[j];
        }
        mean = mean / dataCount;
        for (int j = 0; j < dataCount; j++) {
            dev += Math.pow(dataArr[j] - mean, 2);
        }
        dev = Math.sqrt(dev / dataCount);
        List entry = new ArrayList<Number>();
        entry.add(0, mean);
        entry.add(1, dev);
        return entry;
    }

}

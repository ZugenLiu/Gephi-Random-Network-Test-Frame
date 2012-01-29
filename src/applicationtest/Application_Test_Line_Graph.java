/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationtest;

import java.io.IOException;
import java.util.*;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.*;
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
public class Application_Test_Line_Graph {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        int numNodes = 25;
        double alpha = 0.25;
        int testCount = 50;
        int distCount = 200;
        String filePrefix = "Graph_man_" + "_n=" + numNodes;
        
        double expectedEdges = ((numNodes*(numNodes-1))/2)*alpha;
        double expectedDegree = (numNodes-1*alpha);
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();
        HashMap<Number, Number> hash = null;
        FamilyGraphAnalysis triangleDistr = new FamilyGraphAnalysis();
        FamilyGraphAnalysis degreeDistr = new FamilyGraphAnalysis();
        FamilyGraphAnalysis clustercoDistr = new FamilyGraphAnalysis();
        FamilyGraphAnalysis betweenessDistr = new FamilyGraphAnalysis();
        FamilyGraphAnalysis closenessDistr = new FamilyGraphAnalysis();

        for (int i = 0; i < distCount; i++) {
            workspace = pc.getCurrentWorkspace();
            container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();
            RandomGraph randomGraph = new RandomGraph();
            randomGraph.setNumberOfNodes(numNodes);
            randomGraph.setWiringProbability(alpha);
            randomGraph.generate(container.getLoader());
            ImportController importController = Lookup.getDefault().lookup(ImportController.class);
            importController.process(container, new DefaultProcessor(), workspace);
            container.closeLoader();
            GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
            AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
            SingleGraphAnalysis analysisObj = new SingleGraphAnalysis(filePrefix + "p=" + alpha, graphModel, attributeModel);
            triangleDistr.addSeries(analysisObj.GetTriangleHash());
            degreeDistr.addSeries(analysisObj.GetDegreeHash());
            clustercoDistr.addSeries(analysisObj.GetClusteringHash());
            betweenessDistr.addSeries(analysisObj.GetBetweennessHash());
            closenessDistr.addSeries(analysisObj.GetClosenessHash());
            graphModel.destroyView(graphModel.getVisibleView());
            graphModel.clear();
            pc.cleanWorkspace(workspace);
        }
        TreeMap<Number, List<Number>> triangleDist = triangleDistr.getCombinedSeries();
        DescrStatImageFile triangleOutput = new DescrStatImageFile(triangleDist, "Triangle distribution, " + distCount + " ER graphs, p=" + alpha, DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_triangle_pre_N=" + testCount + "_p=" + alpha, "", "Triangle Distribution With Initial Distribution", "Triangles", "Count", false, true, true, true, true);

        TreeMap<Number, List<Number>> degreeDist = degreeDistr.getCombinedSeries();
        DescrStatImageFile degreeOutput = new DescrStatImageFile(degreeDist, "Degree distribution, " + distCount + " ER graphs, p=" + alpha, DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_degree_pre_N=" + testCount + "_p=" + alpha, "", "Degree Distribution With Initial Distribution", "Degree", "Count", false, true, true, true, true);



        SingleGraphAnalysis analysisObj = null;

        workspace = pc.getCurrentWorkspace();
        container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();
        RandomGraph randomGraph = new RandomGraph();
        randomGraph.setNumberOfNodes(numNodes);
        randomGraph.setWiringProbability(alpha);
        randomGraph.generate(container.getLoader());
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        importController.process(container, new DefaultProcessor(), workspace);
        container.closeLoader();
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();

        analysisObj = new SingleGraphAnalysis("", graphModel, attributeModel);

        //Generate initial degree graph
        hash = analysisObj.GetTriangleHash();
        triangleOutput.HashMap2Series(hash, "Starting Graph");
        triangleOutput.GenerateImage();
        //Prep post degree graph
        triangleOutput = new DescrStatImageFile(triangleDist, "Triangle distribution, " + distCount + " ER graphs, p=" + alpha, DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_triangle_post_N=" + testCount + "_p=" + alpha, "", "Triangle Distribution vs Post-Process", "Triangle", "Count", false, true, true, true, true);
        triangleOutput.HashMap2Series(hash, "Starting Graph");

        //Generate initial degree graph
        hash = analysisObj.GetDegreeHash();
        degreeOutput.HashMap2Series(hash, "Starting Graph");
        degreeOutput.GenerateImage();
        //Prep post degree graph
        degreeOutput = new DescrStatImageFile(degreeDist, "Degree distribution, " + distCount + " ER graphs, p=" + alpha, DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_degree_post_N=" + testCount + "_p=" + alpha, "", "Degree Distribution vs Post-Process", "Degree", "Count", false, true, true, true, true);
        degreeOutput.HashMap2Series(hash, "Starting Graph");


        double[] degreeArr = new double[testCount];
        double[] triangleArr = new double[testCount];
        double[] componentsArr = new double[testCount];
        double[] componentSizeArr = new double[testCount];
        double[] pathArr = new double[testCount];
        double[] diameterArr = new double[testCount];
        double[] edgesArr = new double[testCount];
        double[] clustercoArr = new double[testCount];
        double[] densityArr = new double[testCount];
        double[] betweenessArr = new double[testCount];
        double[] closenessArr = new double[testCount];

        double lastValue = analysisObj.getAvgTriangleCount();
        int i = 0;
        while ((i < testCount) && (lastValue!=0)) {
            Random random = new Random();
            workspace = pc.getCurrentWorkspace();
            Workspace workingSpace = pc.duplicateWorkspace(workspace);
            pc.openWorkspace(workingSpace);
            graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
            attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
            Graph graph = graphModel.getUndirectedGraph();
            //destroy a random edge
            int randomInt = random.nextInt(graph.getEdgeCount());
            Edge[] edgeArr = graph.getEdges().toArray();
            Edge edge = edgeArr[randomInt];
            graph.removeEdge(edge);
            //create a random edge
            boolean contBool = true;
            Node[] NodeArr = graph.getNodes().toArray();
            Node node1 = null;
            Node node2 = null;
            while (contBool) {
                int randomNode1 = 0;
                int randomNode2 = 0;
                while (randomNode1 == randomNode2) {
                    randomNode1 = random.nextInt(graph.getNodeCount());
                    randomNode2 = random.nextInt(graph.getNodeCount());
                }
                node1 = NodeArr[randomNode1];
                node2 = NodeArr[randomNode2];
                if (!graph.isAdjacent(node1, node2)) {
                    contBool = false;
                }
            }

            Edge addedge = graphModel.factory().newEdge(node1, node2);
            graph.addEdge(addedge);
            System.out.println("Iteration:" + i);
            analysisObj = new SingleGraphAnalysis("Iteration=" + i, graphModel, attributeModel);
            triangleArr[i] = analysisObj.getAvgTriangleCount();
            degreeArr[i] = analysisObj.getAverageDegree();
            System.out.println("Old: " + lastValue);
            System.out.println("New: " + triangleArr[i]);

            analysisObj.ExecuteDegree();
            analysisObj.ExecuteClustering();

            if (triangleArr[i] > lastValue) {
                System.out.println("Keeping new graph!");
                lastValue = triangleArr[i];
                analysisObj.drawGraphDegreeDist();
                analysisObj.drawGraphTriangleDist();

                edgesArr[i] = analysisObj.getEdgeCount();
                componentsArr[i] = analysisObj.getComponentCount();
                componentSizeArr[i] = analysisObj.getAvgComponentSize();
                pathArr[i] = analysisObj.getPathLength();
                diameterArr[i] = analysisObj.getDiameter();
                clustercoArr[i] = analysisObj.getAverageClusteringCoefficient();
                betweenessArr[i] = analysisObj.getAvgBetweeness();
                densityArr[i] = analysisObj.getDensity();
                closenessArr[i] = analysisObj.getAvgCloseness();

                CreateLayoutFile graphoutput = new CreateLayoutFile(graphModel, workspace);
                graphoutput.CreateGraphMLFile(filePrefix + "Iteration=" + i + "_graph");
                graphoutput = null;
                workspace = pc.duplicateWorkspace(workingSpace);
                pc.openWorkspace(workspace);
                pc.cleanWorkspace(workingSpace);
                i++;
            } else {
                pc.openWorkspace(workspace);
                pc.cleanWorkspace(workingSpace);
            }
        }


        //Generate triangle graph
        hash = analysisObj.GetTriangleHash();
        triangleOutput.setRoundDoubles(false);
        triangleOutput.HashMap2Series(hash, "Triangles post process");
        triangleOutput.GenerateImage();

        
        //Generate degree graph
        hash = analysisObj.GetDegreeHash();
        degreeOutput.HashMap2Series(hash, "Degree post process");
        degreeOutput.GenerateImage();

        DescrStatImageFile graphOutput = new DescrStatImageFile(triangleArr, "Triangle count", DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_triangle_count_p=" + alpha + "_I=" + testCount, "", "Change in Triangle Count", "Iteration", "Triangle", false);
        graphOutput.GenerateImage();
 
        graphOutput = new DescrStatImageFile(triangleArr, "Triangle count", DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_triangle_count_p=" + alpha + "_I=" + testCount, "", "Change in Average Triangle Count", "Iteration", "Triangles", false);
        graphOutput.GenerateImage();
        graphOutput = new DescrStatImageFile(edgesArr, "Edge count", DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_edge_count_p=" + alpha + "_I=" + testCount, "", "Change in Edge Count", "Iteration", "Edges",false);
        graphOutput.GenerateImage();

        graphOutput = new DescrStatImageFile(componentsArr, "Component count", DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_component_count_p=" + alpha + "_I=" + testCount, "", "Change in Component Count", "Iteration", "Components",false);
        graphOutput.GenerateImage();

        graphOutput = new DescrStatImageFile(componentSizeArr, "Avg. Component size", DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_componentsize_p=" + alpha + "_I=" + testCount, "", "Change in Avg. Component Size", "Iteration", "Avg. Component size",false);
        graphOutput.GenerateImage();

        graphOutput = new DescrStatImageFile(pathArr, "Avg. Path length", DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_pathlength_p=" + alpha + "_I=" + testCount, "", "Change in Avg. Path Length", "Iteration", "Avg. Path Length",false);
        graphOutput.GenerateImage();

        graphOutput = new DescrStatImageFile(diameterArr, "Diameter", DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_diameter_p=" + alpha + "_I=" + testCount, "", "Change in Diameter", "Diameter", "Iteration",false);
        graphOutput.GenerateImage();

        graphOutput = new DescrStatImageFile(clustercoArr, "Avg. Cluster Coefficient", DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_clusterco_count_p=" + alpha + "_I=" + testCount, "", "Change in Avg. Cluster Coefficient", "Iteration", "Avg. Cluster Coefficient",false);
        graphOutput.GenerateImage();

        graphOutput = new DescrStatImageFile(betweenessArr, "Avg. Betweenness", DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_betweenness_p=" + alpha + "_I=" + testCount, "", "Change in Avg. Betweenness", "Iteration", "Avg. Betweenness",false);
        graphOutput.GenerateImage();

        graphOutput = new DescrStatImageFile(densityArr, "Density", DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_density_p=" + alpha + "_I=" + testCount, "", "Change in Density", "Iteration", "Density",false);
        graphOutput.GenerateImage();

        graphOutput = new DescrStatImageFile(closenessArr, "Avg. Closeness", DescrStatImageFile.GRAPH_XYSERIES, filePrefix + "_closeness_p=" + alpha + "_I=" + testCount, "", "Change in Avg. Closeness", "Iteration", "Avg. Closeness",false);
        graphOutput.GenerateImage();


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

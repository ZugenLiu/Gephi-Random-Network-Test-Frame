/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RandomGraphFramwork;

import com.itextpdf.text.PageSize;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.exporter.preview.PDFExporter;
import org.gephi.io.exporter.spi.Exporter;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.partition.api.Partition;
import org.gephi.partition.api.PartitionController;
import org.gephi.partition.plugin.NodeColorTransformer;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 *
 * @author Matt Groeninger (mgroeninger@gmail.com)
 */
public class CreateLayoutFile {
    private Workspace workspace;
    private GraphModel graphModel;

    public CreateLayoutFile(GraphModel graphModel,Workspace workspace) {
        this.workspace = workspace;
        this.graphModel = graphModel;
    }
    
    public void DoYifanHuLayout(int minutes) {
            AutoLayout autoLayout = new AutoLayout(minutes, TimeUnit.MINUTES);
            autoLayout.setGraphModel(graphModel);
            YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(1f));
            autoLayout.addLayout(firstLayout, minutes*1f);
            autoLayout.execute();
    }
    
    
    public void DoForceAtlasLayout(int minutes) {
            AutoLayout autoLayout = new AutoLayout(minutes, TimeUnit.MINUTES);
            autoLayout.setGraphModel(graphModel);
            ForceAtlasLayout firstLayout = new ForceAtlasLayout(null);
            AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);//True after 10% of layout time
            AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", new Double(500.), 0f);//500 for the complete period
            autoLayout.addLayout(firstLayout, minutes*1f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
            autoLayout.execute();
    }

    public void DoFruchtermanReingoldLayout(int minutes) {
            AutoLayout autoLayout = new AutoLayout(minutes, TimeUnit.MINUTES);
            autoLayout.setGraphModel(graphModel);
            FruchtermanReingold firstLayout = new FruchtermanReingold(null);
            autoLayout.addLayout(firstLayout, minutes*1f);
            autoLayout.execute();
    }
    
    public void DoCombinedLayout(int minutes) {
        AutoLayout autoLayout = new AutoLayout(minutes, TimeUnit.MINUTES);
        autoLayout.setGraphModel(graphModel);
        FruchtermanReingold firstLayout = new FruchtermanReingold(null);
        ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);
        AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);//True after 10% of layout time
        AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", new Double(500.), 0f);//500 for the complete period
        autoLayout.addLayout(firstLayout, minutes*0.5f);
        autoLayout.addLayout(secondLayout, minutes*0.5f, new AutoLayout.DynamicProperty[]{adjustBySizeProperty, repulsionProperty});
        autoLayout.execute();
    }
    
    public void DoColorPartition(String columnname) {
        AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel(this.workspace);
        Graph graph = this.graphModel.getGraph();
        PartitionController partitionController = Lookup.getDefault().lookup(PartitionController.class);
        Partition p = partitionController.buildPartition(attributeModel.getNodeTable().getColumn(columnname), graph);
        NodeColorTransformer nodeColorTransformer = new NodeColorTransformer();
        nodeColorTransformer.randomizeColors(p);
        partitionController.transform(p, nodeColorTransformer);
    }
    
        
    public void CreatePDFFile(String filename) throws IOException {
        if (filename == null || "".equals(filename)) {
            throw new IOException("Invalid file name.");
        } else {
            ExportController ec = Lookup.getDefault().lookup(ExportController.class);
            PDFExporter pdfExporter = (PDFExporter) ec.getExporter("pdf");
            pdfExporter.setPageSize(PageSize.A0);
            pdfExporter.setWorkspace(this.workspace);
            try {
               ec.exportFile(new File(filename+".pdf"));
            } catch (IOException ex) {
               ex.printStackTrace();
               return;
            }
        }
    }

    public void CreateGraphMLFile(String filename) throws IOException {
        if (filename == null || "".equals(filename)) {
            throw new IOException("Invalid file name.");
        } else {
            ExportController ec = Lookup.getDefault().lookup(ExportController.class);
            Exporter exporter = (Exporter) ec.getExporter("gexf");
            exporter.setWorkspace(this.workspace);
            try {
               ec.exportFile(new File(filename+".graphml"));
            } catch (IOException ex) {
               ex.printStackTrace();
               return;
            }
        }
    }
}

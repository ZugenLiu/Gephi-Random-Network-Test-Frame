/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RandomGraphFramwork;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DeviationRenderer;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;

/**
 *
 * @author Matt Groeninger (mgroeninger@gmail.com)
 */
public class DescrStatImageFile {

    YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
    public int type = GRAPH_XYSERIES;
    public String fileprefix;
    public String filename= "graph";
    public String graphtitle = "";;
    public String x_axis = "";
    public String y_axis = "";
    public boolean logaxis = false;
    public boolean includeZero = false;
    public boolean rangeInt = false;
    public boolean domainInt = false;
    public static final int GRAPH_XYSERIES = 2;
    public static final int GRAPH_BARGRAPH = 4;
    private boolean showDatapoint = false;
    private boolean roundDoubles = false;

    public DescrStatImageFile(HashMap<Number, Number> counts, String seriesname) {
        HashMap2Series(counts, seriesname);
    }

    public DescrStatImageFile(HashMap<Number, Number> counts, String seriesname, int type, String filename) {
        this.type = type;
        this.filename = filename;

        HashMap2Series(counts, seriesname);
    }

    public DescrStatImageFile(HashMap<Number, Number> counts, String seriesname, int type, String fileprefix, String filename, String graphtitle, String x_axis, String y_axis, boolean logaxis) {
        this.type = type;
        this.fileprefix = fileprefix;
        this.filename = filename;
        this.graphtitle = graphtitle;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.logaxis = logaxis;
        HashMap2Series(counts, seriesname);
    }

    public DescrStatImageFile(HashMap<Number, Number> counts, String seriesname, int type, String fileprefix, String filename, String graphtitle, String x_axis, String y_axis, boolean logaxis, boolean domainInt, boolean rangeInt, boolean showdatapoints) {
        this.type = type;
        this.fileprefix = fileprefix;
        this.filename = filename;
        this.graphtitle = graphtitle;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.logaxis = logaxis;
        this.includeZero = false;
        this.rangeInt = rangeInt;
        this.domainInt = domainInt;
        this.showDatapoint = showdatapoints;
        HashMap2Series(counts, seriesname);
    }

    public DescrStatImageFile(int[] counts, String seriesname, int type, String fileprefix, String filename, String graphtitle, String x_axis, String y_axis, boolean logaxis) {
        int i = 0;
        HashMap<Number, Number> newcount = new HashMap<Number, Number>();
        for (int entry : counts) {
            newcount.put(Number.class.cast(i), Number.class.cast(entry));
            i++;
        }
        this.type = type;
        this.fileprefix = fileprefix;
        this.filename = filename;
        this.graphtitle = graphtitle;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.logaxis = logaxis;
        HashMap2Series(newcount, seriesname);
    }

    public DescrStatImageFile(double[] counts, String seriesname, int type, String fileprefix, String filename, String graphtitle, String x_axis, String y_axis, boolean logaxis) {
        int i = 0;
        HashMap<Number, Number> newcount = new HashMap<Number, Number>();
        for (double entry : counts) {
            newcount.put(Number.class.cast(i), Number.class.cast(entry));
            i++;
        }
        this.type = type;
        this.fileprefix = fileprefix;
        this.filename = filename;
        this.graphtitle = graphtitle;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.logaxis = logaxis;
        HashMap2Series(newcount, seriesname);
    }

    public DescrStatImageFile(Object[] counts, String seriesname, int type, String fileprefix, String filename, String graphtitle, String x_axis, String y_axis, boolean logaxis) {
        int i = 0;
        HashMap<Number, Number> newcount = new HashMap<Number, Number>();
        for (Object entry : counts) {
            newcount.put(Number.class.cast(i), Number.class.cast(entry));
            i++;
        }
        this.type = type;
        this.fileprefix = fileprefix;
        this.filename = filename;
        this.graphtitle = graphtitle;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.logaxis = logaxis;
        HashMap2Series(newcount, seriesname);
    }

    public DescrStatImageFile(Map<Number, List<Number>> counts, String seriesname, int type, String fileprefix, String filename, String graphtitle, String x_axis, String y_axis, boolean logaxis) {
        this.type = type;
        this.fileprefix = fileprefix;
        this.filename = filename;
        this.graphtitle = graphtitle;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.logaxis = logaxis;
        NestedHash2Series(counts, seriesname);
    }

    public DescrStatImageFile(Map<Number, List<Number>> counts, String seriesname, int type, String fileprefix, String filename, String graphtitle, String x_axis, String y_axis, boolean logaxis, boolean includeZero) {
        this.type = type;
        this.fileprefix = fileprefix;
        this.filename = filename;
        this.graphtitle = graphtitle;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.logaxis = logaxis;
        this.includeZero = includeZero;
        NestedHash2Series(counts, seriesname);
    }

    public DescrStatImageFile(Map<Number, List<Number>> counts, String seriesname, int type, String fileprefix, String filename, String graphtitle, String x_axis, String y_axis, boolean logaxis, boolean includeZero, boolean domainInt, boolean rangeInt) {
        this.type = type;
        this.fileprefix = fileprefix;
        this.filename = filename;
        this.graphtitle = graphtitle;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.logaxis = logaxis;
        this.includeZero = includeZero;
        this.rangeInt = rangeInt;
        this.domainInt = domainInt;
        NestedHash2Series(counts, seriesname);
    }
     public DescrStatImageFile(Map<Number, List<Number>> counts, String seriesname, int type, String fileprefix, String filename, String graphtitle, String x_axis, String y_axis, boolean logaxis, boolean includeZero, boolean domainInt, boolean rangeInt, boolean showDatapoint) {
        this.type = type;
        this.fileprefix = fileprefix;
        this.filename = filename;
        this.graphtitle = graphtitle;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.logaxis = logaxis;
        this.includeZero = includeZero;
        this.rangeInt = rangeInt;
        this.domainInt = domainInt;
        this.showDatapoint = showDatapoint;
        NestedHash2Series(counts, seriesname);
    }
     
    public DescrStatImageFile(YIntervalSeries series, String seriesname, int type, String fileprefix, String filename, String graphtitle, String x_axis, String y_axis, boolean logaxis, boolean includeZero, boolean domainInt, boolean rangeInt) {
        this.type = type;
        this.fileprefix = fileprefix;
        this.filename = filename;
        this.graphtitle = graphtitle;
        this.x_axis = x_axis;
        this.y_axis = y_axis;
        this.logaxis = logaxis;
        this.includeZero = includeZero;
        this.rangeInt = rangeInt;
        this.domainInt = domainInt;
        this.dataset.addSeries(series);
    }    

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileprefix() {
        return fileprefix;
    }

    public void setFileprefix(String fileprefix) {
        this.fileprefix = fileprefix;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getGraphtitle() {
        return graphtitle;
    }

    public void setGraphtitle(String graphtitle) {
        this.graphtitle = graphtitle;
    }

    public boolean isLogaxis() {
        return logaxis;
    }

    public void setLogaxis(boolean logaxis) {
        this.logaxis = logaxis;
    }

    public String getX_axis() {
        return x_axis;
    }

    public void setX_axis(String x_axis) {
        this.x_axis = x_axis;
    }

    public String getY_axis() {
        return y_axis;
    }

    public void setY_axis(String y_axis) {
        this.y_axis = y_axis;
    }

    public boolean isIncludeZero() {
        return includeZero;
    }

    public void setIncludeZero(boolean includeZero) {
        this.includeZero = includeZero;
    }

    public boolean isDomainInt() {
        return domainInt;
    }

    public void setDomainInt(boolean domainInt) {
        this.domainInt = domainInt;
    }

    public boolean isRangeInt() {
        return rangeInt;
    }

    public void setRangeInt(boolean rangeInt) {
        this.rangeInt = rangeInt;
    }
    
    public boolean isRoundDoubles() {
        return this.roundDoubles;
    }
    
    public void setRoundDoubles(boolean roundDoubles) {
        this.roundDoubles = rangeInt;
    }
    

    public void GenerateImage() throws IOException {
        NumberAxis domainAxis;
        NumberAxis rangeAxis;
        JFreeChart chart = null;

        if (this.logaxis) {
            this.includeZero = false;
        }
        if ("".equals(this.graphtitle)) {
            this.graphtitle = null;
        }
        String file = this.fileprefix + "_" + this.filename + ".png";
        if (file == null || "".equals(file)) {
            throw new IOException("Invalid file name.");
        } else {
            if (this.type == 0) {
                throw new IOException("Graph Type not set.");
            } else {
                switch (this.type) {
                    case 2:
                    case 4:
                        if (this.type == GRAPH_XYSERIES) {
                            chart = ChartFactory.createXYLineChart(
                                    this.graphtitle,
                                    this.x_axis,
                                    this.y_axis,
                                    this.dataset,
                                    org.jfree.chart.plot.PlotOrientation.VERTICAL,
                                    true,
                                    false,
                                    false);
                        } else {
                            chart = ChartFactory.createXYBarChart(
                                    this.graphtitle,
                                    this.x_axis,
                                    false,
                                    this.y_axis,
                                    this.dataset,
                                    org.jfree.chart.plot.PlotOrientation.VERTICAL,
                                    true,
                                    true,
                                    false);
                        }
                        break;
                }
                if (this.isLogaxis()) {
                    domainAxis = new LogarithmicAxis(this.x_axis);
                    rangeAxis = new LogarithmicAxis(this.y_axis);
                } else {
                    domainAxis = new NumberAxis(this.x_axis);
                    rangeAxis = new NumberAxis(this.y_axis);
                }
                chart.setBackgroundPaint(Color.white);
                domainAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 16));
                if (this.domainInt) {
                    domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                }
                domainAxis.setAutoRangeIncludesZero(true);
                rangeAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 16));
                if (this.rangeInt) {
                    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                }
                rangeAxis.setAutoRangeIncludesZero(true);
                XYPlot plot = (XYPlot) chart.getPlot();
                plot.setDomainAxis(domainAxis);
                plot.setRangeAxis(rangeAxis);
                plot.setOutlinePaint(Color.black);
                plot.setBackgroundPaint(Color.lightGray);
                plot.setRangeGridlinePaint(Color.white);

                DeviationRenderer renderer = new DeviationRenderer(true, false);
                int seriescount = this.dataset.getSeriesCount();
                for (int j = 0; j < seriescount; j++) {
                    renderer.setSeriesStroke(j, new BasicStroke(1.0f));
                    if (this.showDatapoint) {
                        renderer.setSeriesShapesFilled(j, false);
                        renderer.setSeriesShapesVisible(j, true);
                    }
                }
                renderer.setSeriesFillPaint(0, new Color(200, 200, 255));
                renderer.setSeriesFillPaint(1, new Color(255, 200, 200));

                plot.setRenderer(renderer);

                ChartUtilities.saveChartAsPNG(new File(file), chart, 600, 600);
            }
        }
    }

    public final void HashMap2Series(HashMap<Number, Number> counts, String seriesname) {
        DecimalFormat twoDForm = new DecimalFormat("#.####");
        YIntervalSeries series = new YIntervalSeries(seriesname);
        Set countset = counts.entrySet();
        Iterator countiter = countset.iterator();
        while (countiter.hasNext()) {
            Map.Entry mentry = (Map.Entry) countiter.next();
            Number key = (Number) mentry.getKey();
            Number value = (Number) mentry.getValue();
            if (this.includeZero || (!this.includeZero && value.doubleValue() != 0)) {
                if (this.roundDoubles) {
                    key = Double.valueOf(twoDForm.format(key.doubleValue()));                    
                }
                series.add(key.doubleValue(), value.doubleValue(), value.doubleValue(), value.doubleValue());
            }
            
        }
        this.dataset.addSeries(series);
    }

    public final void NestedHash2Series(Map<Number, List<Number>> counts, String seriesname) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        YIntervalSeries series = new YIntervalSeries(seriesname);
        Set countset = counts.entrySet();
        Iterator countiter = countset.iterator();
        while (countiter.hasNext()) {
            Map.Entry mentry = (Map.Entry) countiter.next();
            Number key = (Number) mentry.getKey();
            ArrayList value = (ArrayList) mentry.getValue();
            Number mean = (Number) value.get(0);
            Number stddev = (Number) value.get(1);
            if (this.includeZero || (!this.includeZero && mean.doubleValue() != 0)) {
                if (this.roundDoubles) {
                    key = Double.valueOf(twoDForm.format(key.doubleValue()));
                }
            series.add(key.doubleValue(), mean.doubleValue(), mean.doubleValue() - stddev.doubleValue(), mean.doubleValue() + stddev.doubleValue());
            }
        }
        this.dataset.addSeries(series);
    }
}

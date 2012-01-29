/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RandomGraphFramwork;

import java.util.*;

/**
 *
 * @author Matt Groeninger (mgroeninger@gmail.com)
 */
public class FamilyGraphAnalysis {
    int seriesCount = 0;
    HashMap<Number, Number>seriesTotal = new HashMap<Number, Number>();
    HashMap<Number, Number>avgSeries = new HashMap<Number, Number>();
    HashMap<Number, Number>varSeries = new HashMap<Number, Number>();
    HashMap<Number, Number>mseSeries = new HashMap<Number, Number>();
    HashMap<Number, Number>compareSeries = new HashMap<Number, Number>();
    HashMap<Integer, HashMap> seriesHash = new HashMap<Integer, HashMap>();
    boolean avgCalced = false;
    boolean varCalced = false;
    boolean compareSet = false;
    
    public void FamilyGraphAnalysis(HashMap<Number, Number> series) {
        addSeries(series);
    }
    
    public void setCompareSeries(HashMap<Number, Number> series){
        this.compareSeries = series;
        this.compareSet = true;
    }
    
    public void addSeriesMSE(HashMap<Number, Number> series) {
        List<Number> processed = new ArrayList<Number>();
        if (this.compareSet) {
            Set countset = series.entrySet();
            Iterator countiter = countset.iterator();
            while (countiter.hasNext()) {
                Map.Entry mentry = (Map.Entry) countiter.next();
                Number key = (Number) mentry.getKey();
                Number value = (Number) mentry.getValue();
                if (this.compareSeries.containsKey(key)) {
                    Number origValue = compareSeries.get(key);
                    this.mseSeries.put(key,  Math.pow(value.doubleValue() - origValue.doubleValue(),2));
                } else {
                    this.mseSeries.put(key, Math.pow(value.doubleValue(),2));
                }
                processed.add(key);
            }
            countset = compareSeries.entrySet();
            countiter = countset.iterator();
            while (countiter.hasNext()) {
                Map.Entry mentry = (Map.Entry) countiter.next();
                Number key = (Number) mentry.getKey();
                Number value = (Number) mentry.getValue();
                if (series.containsKey(key) && (!processed.contains(key))) {
                    Number origValue = compareSeries.get(key);
                    this.mseSeries.put(key,  Math.pow(value.doubleValue() - origValue.doubleValue(),2));
                } else {
                    this.mseSeries.put(key, Math.pow(value.doubleValue(),2));
                }
            }            
        }
        addSeries(series);
    }
    
    public double getMSE() {
        double MSEout = 0;
           Set countset = mseSeries.entrySet();
           Iterator countiter = countset.iterator();
            while (countiter.hasNext()) {
                Map.Entry mentry = (Map.Entry) countiter.next();
                Number value = (Number) mentry.getValue();
                MSEout += value.doubleValue();
            }
        return MSEout/mseSeries.size();
    }
    
    public void addSeries(HashMap<Number, Number> series){
        Set countset = series.entrySet();
        Iterator countiter = countset.iterator();
        while (countiter.hasNext()) {
            Map.Entry mentry = (Map.Entry) countiter.next();
            Number key = (Number) mentry.getKey();
            Number value = (Number) mentry.getValue();
            if (seriesTotal.containsKey(key)) {
                Number origValue = seriesTotal.get(key);
                seriesTotal.put(key,  value.doubleValue() + origValue.doubleValue());
            } else {
                seriesTotal.put(key, value.doubleValue());
            }
        }
        seriesHash.put(this.seriesCount, series);
        this.seriesCount +=1;
    }
    public void calcAvgSeries() {
        if (!this.avgCalced) {
            Set countset = seriesTotal.entrySet();
            Iterator countiter = countset.iterator();
            while (countiter.hasNext()) {
                Map.Entry mentry = (Map.Entry) countiter.next();
                Number key = (Number) mentry.getKey();
                Number value = (Number) mentry.getValue();
                this.avgSeries.put(key, value.doubleValue()/this.seriesCount);
            }
            this.avgCalced = true;
        }
    }    
    public HashMap<Number, Number> getAvgSeries() {
        if (!this.avgCalced) {
            calcAvgSeries();
        }
        return this.avgSeries;
    }

   public void calcVarSeries() {
        calcAvgSeries();
        Set countset = seriesTotal.entrySet();
        Iterator countiter = countset.iterator();
        while (countiter.hasNext()) {
            Map.Entry mentry = (Map.Entry) countiter.next();
            Number key = (Number) mentry.getKey();
            double varSum = 0;
            for (int i=0;i<this.seriesCount;i++) {
                HashMap<Number, Number> indivdualSeries = seriesHash.get(i);
                if (indivdualSeries.containsKey(key) && this.avgSeries.containsKey(key)) {
                    varSum += Math.pow(indivdualSeries.get(key).doubleValue()-this.avgSeries.get(key).doubleValue(),2);
                }
                
            }
            this.varSeries.put(key, Math.sqrt(varSum/this.seriesCount));
        }
    }
   
    public HashMap<Number, Number> getVarSeries() {
        if (!this.varCalced) {
            calcVarSeries();
        }
        return this.varSeries;
    }
    
    public TreeMap<Number, List<Number>> getCombinedSeries() {
        if (!this.varCalced) {
            calcVarSeries();
        }
        TreeMap<Number, List<Number>> output = new TreeMap<Number, List<Number>>();
        if (this.varSeries.size() == this.avgSeries.size()) {
            Set countset = avgSeries.entrySet();
            Iterator countiter = countset.iterator();
            while (countiter.hasNext()) {
                List entry = new ArrayList<Number>();
                Map.Entry mentry = (Map.Entry) countiter.next();
                Number key = (Number) mentry.getKey();
                Number mean = (Number) mentry.getValue();
                Number dev = (Number) varSeries.get(key);
                entry.add(0, mean);
                entry.add(1, dev);
                output.put(key, entry);
            }
        }
        return output;
    }
    
}

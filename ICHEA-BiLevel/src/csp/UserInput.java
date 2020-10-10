/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;


public class UserInput implements Cloneable,Serializable {

    public int totalObjectives;
    private int totalDecisionVars;

    public int getTotalDecisionVars() {
        return totalDecisionVars;
    }

    public void setTotalDecisionVars(int totalDecisionVars) {
        this.totalDecisionVars = totalDecisionVars;
        for (int i = 0; i < this.totalDecisionVars; i++) {
            upperLevelIdx.add(i);
        }
    }

    
    private List<Integer> lowerLevelIdx;
    private List<Integer> upperLevelIdx;

    public final List<Integer> getUpperLevelIdx() {
        return Collections.unmodifiableList(upperLevelIdx);
    }

    public final List<Integer> getLowerLevelIdx() {
        return Collections.unmodifiableList(lowerLevelIdx);
    }

    public void addLowerLevelIdx(Integer x) {
        this.lowerLevelIdx.add(x);
        upperLevelIdx.remove(new Integer(x));
    }
    public ArrayList<Double> minVals;
    public ArrayList<Double> maxVals;
    public int totalConstraints;
    String dataType;
    //public boolean fileData; 
    public int population;
    public int generation;
    public boolean doMutation;
    public boolean saveChromes;
    public ArrayList<ArrayList<Double>> domainVals;//applicable for ordinal data only. Domain values for each dimension
    /**
     * {@link Chromosome#BY_AGE}<BR>
     * {@link Chromosome#BY_DISCOURAGE}<BR>
     * {@link Chromosome#BY_FITNESS}<BR>
     * {@link Chromosome#BY_IMMUNITY}<BR>
     * {@link Chromosome#BY_RO}<BR>
     * {@link Chromosome#BY_SATISFACTIONS}<BR>
     * {@link Chromosome#BY_VIOLATIONS}<BR>
     * 
     */
    public int solutionBy;
    public boolean bWeighted;
    public boolean bHasConstraintPreferences;
    public int gxFn=-1;
    public int tpxFn = -1;
    public boolean isBiLevel;
    public int maxDynamicTime;
    //public boolean dataIsDiscrete;
   
    private UserInput(){
        ;
    }
    /**
     * The only constructor
     */
    public UserInput(Class t, boolean saveChromes) throws InstantiationException, IllegalAccessException{
        this.totalObjectives = -1;
        this.totalDecisionVars = -1;
        this.lowerLevelIdx = new ArrayList<Integer>();
        this.upperLevelIdx = new ArrayList<Integer>();
        this.minVals = new ArrayList();
        this.maxVals = new ArrayList();
        this.totalConstraints = -1;
        this.population = 0;
        this.generation = 0;
        solutionBy = -1;
        //this.externalData_ = null;
        dataType = t.getName();
        //fileData = false;
        doMutation = true;
        domainVals = null; // only needed in special cases.
        this.saveChromes = saveChromes;
        
        bWeighted = false;
        bHasConstraintPreferences = false;
    }

    @Override public String toString() {
        String msg;
        msg = "totalObjectives: " + String.valueOf(totalObjectives);
        msg = msg + "\nTotal Decision Vars: " + String.valueOf(totalDecisionVars);
        msg = msg + "\nTotal Constraints: " + String.valueOf(totalConstraints);
        msg = msg + "\nLower Level Vars: " + lowerLevelIdx.toString();
        msg = msg + "\nMin Vals: " + minVals.toString();
        msg = msg + "\nMax Vals: " + maxVals.toString();
        msg = msg + "\nData Type: " + dataType;
        //msg = msg + "\nFile Data: " + fileData;

        return msg;
    }

    public void validateData() throws MyException{
        if(totalDecisionVars != minVals.size() || totalDecisionVars != maxVals.size()){
            throw new MyException("Incorrect Total Decision Vars value", "Incorrect Data Combinations",JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * lone defined for UserInput is ONLY SHALLOW CLONE.
     * @return Object.clone();
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


}

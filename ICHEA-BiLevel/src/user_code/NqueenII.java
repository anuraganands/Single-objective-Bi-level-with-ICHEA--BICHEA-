///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package user_code;
//
//import csp.Chromosome;
//import csp.ExternalData;
////import csp.GroupCS;
//import csp.MyException;
//import csp.MyRandom;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.LinkedHashSet;
//import java.util.NoSuchElementException;
//import java.util.Scanner;
//import java.util.Set;
//import java.util.StringTokenizer;
//import javax.swing.JOptionPane;
//
///**
// *
// * @author s425770
// */
//public class NqueenII extends ExternalData{
//    private int chessBoardSize_;
//    
//    public NqueenII(Scanner dataFile, int populaion, int generation, boolean saveChromes, int solutionBy, Class t) throws InstantiationException, IllegalAccessException, MyException {
//        super(dataFile, populaion, generation, saveChromes, solutionBy, t);
//        this.chessBoardSize_ = 0;
//        this.readData();
//    }
//       
//
//    @Override
//    protected int getConstraintID(Double val) {
//        return val.intValue();
//    }
//
//    @Override
//    protected ArrayList<Chromosome> initializeExternalChrmosomes(final int population) {
//        if(userInput_ == null)
//            throw new UnsupportedOperationException("User input not initialized");
//
//        Chromosome chrome;
//        ArrayList<Integer> randChromes;
//        
//        ArrayList<Chromosome> newChromes = new ArrayList<Chromosome>();   
//        
//        //////chromosome_ = new ArrayList<Chromosome>();
//        
//        //get chromosomes from file.
//        //<<
//        try {
//            File file = new File(new File(".").getCanonicalPath() + "/chromosomes.txt");
//            Scanner chromeFile = new Scanner(file);
//                
//            int TOTAL_CHROMES = chromeFile.nextInt();
//            chromeFile.nextLine(); //ignore comments
//
//            int CHROMES_LENGTH = chromeFile.nextInt();
//            chromeFile.nextLine(); //ignore
//
//            int sz;
//            String tempStr;
//            StringTokenizer str;                   
//            
//            if (CHROMES_LENGTH > userInput_.totalConstraints){
//                throw new IOException("stored data has bigger domain!");
//            }
//            
//            while(chromeFile.hasNext()){    
//                tempStr = chromeFile.nextLine();
//                str = new StringTokenizer(tempStr," ");
//                sz = str.countTokens();
//                chrome = new Chromosome(userInput_.solutionBy, this);
//                for (int i = 0; i < sz; i++){                
//                    tempStr = str.nextElement().toString();  
//                                   
//                    chrome.appendVal(Double.valueOf(tempStr));
//                }
//                newChromes.add(chrome);       
//            } 
//            
//        } catch (IOException ioe) {
//            //System.out.println("Chromosomes NOT generated from the file");
//            newChromes = new ArrayList<Chromosome>();
//        }catch (NoSuchElementException nsee){
//            //System.out.println("Chromosomes NOT generated from the file. It seems the file is empty.");
//            newChromes = new ArrayList<Chromosome>();
//        }
//        //>>
//        
//        //System.out.println(chromosome_);
//        
//        for (int i = 0; i < population; i++) { 
//            initializeCounter = (initializeCounter+1)%userInput_.totalConstraints;
//            chrome = new Chromosome(userInput_.solutionBy, this);
//            chrome.appendVal(initializeCounter);
//            newChromes.add(chrome);                     
//        }
//  
//        //userInput_.population = chromosome_.size();
//        return newChromes;
//    }
//
////    @Override
////    public ArrayList<Double> negateVal(ArrayList<Double> vals) {
////        ArrayList<Double> negVals = new ArrayList<Double>();        
////        for (int i = 0; i < vals.size(); i++) {
////           negVals.add(userInput_.maxVals.get(i) - vals.get(i));
////        }
////    }
//       
//    
////    @Override
//    protected int isViolated(Object obj1, Object obj2, Object... additionalInfo) {
//        int violated; // 1 = > true; 0 = false;
//
//        if (obj1 instanceof Integer && obj2 instanceof Integer){
//            ;
//        }else{
//            throw new ClassCastException("Expecting Integers");
//        }
//        
//        Integer pos1 = (Integer)obj1;
//        Integer pos2 = (Integer)obj2;  
//        Integer dist = (Integer)additionalInfo[0];
//        
//        if(pos1 == pos2){ 
//            violated = 1; //note..............
//            return violated;
//        }
//        
//        if(pos1 == pos2){ //same row
//            violated = 1; //note..............        
//        }else if (Math.abs(pos1-pos2) == dist){//diagonally same
//            violated = 1;
//        }else{
//            violated = 0;
//        }
//        
//        return violated;
//    }
//
//    @Override
//    protected void readData() throws MyException {
//        String val;
//        
//        val = JOptionPane.showInputDialog(null, "Enter queen problem size (n)", "Nqueen Problem", JOptionPane.QUESTION_MESSAGE);
//        if(val == null)
//            throw new UnsupportedOperationException("Cannot accept null value");
//        chessBoardSize_ = Integer.parseInt(val);
//                    
//        //set user input values...        
//        userInput_.fileData = true;//???
//        userInput_.totalConstraints = chessBoardSize_;
//        userInput_.totalDecisionVars = userInput_.totalConstraints; //currently i take it one for all ordinal values..
//        userInput_.totalObjectives = 1; //currently i take it one for all ordinal values..
//
//        for (int i = 0; i < userInput_.totalDecisionVars; i++) {
//            userInput_.minVals.add(0.0);
//            userInput_.maxVals.add(userInput_.totalConstraints*1.0-1);            
//        }               
//        
//        userInput_.validateData();
//        //userInput_.population = Math.min(chessBoardSize_*chessBoardSize_, userInput_.population); //it is minimum.
//
//        userInput_.domainVals = new  ArrayList<ArrayList<Double>>();
//        for (int i = 0; i < userInput_.totalDecisionVars; i++) {
//            userInput_.domainVals.add(new ArrayList<Double>());
//            for (Double j = userInput_.minVals.get(i); j <= userInput_.maxVals.get(i); j++) {
//                userInput_.domainVals.get(i).add(j);
//            }
//        }
//
//        userInput_.doMutation = true;
//    }
//
//    @Override
//    protected void ObjectiveFnRemove(ArrayList<Double> vals, ArrayList<Double> fitness_, ArrayList<ArrayList> constraints, final Integer idx) {
//        vals.remove(idx);
//        constraints.remove(idx);
//    }
// 
//    
//    @Override
//    protected void ObjectiveFnAppend(ArrayList<Double> vals, ArrayList<Double> fitness_, ArrayList<ArrayList> constraints) {
//        ObjectiveFnReset(vals, fitness_, constraints);
//    }
//    
//    @Override
//    protected void ObjectiveFnReset(ArrayList<Double> vals, ArrayList<Double> fitness_, ArrayList<ArrayList> constraints) {
//        //GroupCS gcs;
//        ArrayList<Integer> gcs;
//        
//        fitness_.clear();
//        fitness_.add(Double.NaN);
//        constraints.clear(); //satisfactions
//        
//        Set<Double> s = new LinkedHashSet<Double>(vals);
//        vals.clear();
//        vals.addAll(s);
//        
//        for (int i = 0; i < vals.size(); i++) {
//            for (int j = i+1; j < vals.size(); j++) {
//                 if(i!=j){
//                    if(isViolated(vals.get(i).intValue(), vals.get(j).intValue(),Math.abs(i-j))>0){
//                        vals.remove(j);
//                        j--;
//                    }else{
//                        ;
//                    }
//                }
//            }
//        }
//        
//        constraints.clear();//satisfactions
//                                    
//        for (Double v : vals) {
//            //gcs = new GroupCS(1);
//            gcs = new ArrayList<Integer>();
//            gcs.add(v.intValue());
//            constraints.add(gcs);
//        }        
//    }  
//}
//
//
//
//
//
////
////
/////*
//// * To change this template, choose Tools | Templates
//// * and open the template in the editor.
//// */
////package user_code;
////
////import csp.Chromosome;
////import csp.ExternalData;
////import csp.MyException;
////import csp.MyRandom;
////import java.io.File;
////import java.io.IOException;
////import java.util.ArrayList;
////import java.util.HashSet;
////import java.util.NoSuchElementException;
////import java.util.Scanner;
////import java.util.StringTokenizer;
////import javax.swing.JOptionPane;
////
/////**
//// *
//// * @author s425770
//// */
////public class NqueenII extends ExternalData{
////    private int chessBoardSize_;
////    
////    public NqueenII(Scanner dataFile, int populaion, int generation, boolean saveChromes, int solutionBy, Class t) throws InstantiationException, IllegalAccessException, MyException {
////        super(dataFile, populaion, generation, saveChromes, solutionBy, t);
////        this.chessBoardSize_ = 0;
////        this.readData();
////    }
////    
////    @Override
////    protected double degreeOfViolation(Object obj1, Object obj2) {                
////        throw new UnsupportedOperationException("Not supported yet.");
////    }
////
////    @Override
////    protected int getConstraintID(Double val) {
////        return val.intValue();
////    }
////
////    @Override
////    protected ArrayList<Chromosome> initializeExternalChrmosomes(final int population) {
////        if(userInput_ == null)
////            throw new UnsupportedOperationException("User input not initialized");
////
////        Chromosome chrome;
////        ArrayList<Integer> randChromes;
////        
////        chromosome_ = new ArrayList<Chromosome>();
////        
////        //get chromosomes from file.
////        //<<
////        try {
////            File file = new File(new File(".").getCanonicalPath() + "/chromosomes.txt");
////            Scanner chromeFile = new Scanner(file);
////                
////            int TOTAL_CHROMES = chromeFile.nextInt();
////            chromeFile.nextLine(); //ignore comments
////
////            int CHROMES_LENGTH = chromeFile.nextInt();
////            chromeFile.nextLine(); //ignore
////
////            int sz;
////            String tempStr;
////            StringTokenizer str;                   
////            
////            if (CHROMES_LENGTH > userInput_.totalConstraints){
////                throw new IOException("stored data has bigger domain!");
////            }
////            
////            while(chromeFile.hasNext()){    
////                tempStr = chromeFile.nextLine();
////                str = new StringTokenizer(tempStr," ");
////                sz = str.countTokens();
////                chrome = new Chromosome(userInput_.solutionBy, this);
////                for (int i = 0; i < sz; i++){                
////                    tempStr = str.nextElement().toString();  
////                                   
////                    chrome.appendVal(Double.valueOf(tempStr));
////                }
////                chromosome_.add(chrome);       
////            } 
////            
////        } catch (IOException ioe) {
////            //System.out.println("Chromosomes NOT generated from the file");
////            chromosome_ = new ArrayList<Chromosome>();
////        }catch (NoSuchElementException nsee){
////            //System.out.println("Chromosomes NOT generated from the file. It seems the file is empty.");
////            chromosome_ = new ArrayList<Chromosome>();
////        }
////        //>>
////        
////        randChromes = MyRandom.randperm(0, userInput_.totalConstraints-1);
////        for (int i = 0; chromosome_.size() < population; i++) {            
////            chrome = new Chromosome(userInput_.solutionBy, this);
////            chrome.appendVal(randChromes.get(i%userInput_.totalConstraints).doubleValue());
////            chromosome_.add(chrome);                     
////        }
////
////        return chromosome_;
////    }     
////    
////    @Override
////    protected int isViolated(Object obj1, Object obj2, Object additionalInfo) {
////        int violated; //0 => false; 1 => true
////
////        if (obj1 instanceof Integer && obj2 instanceof Integer){
////            ;
////        }else{
////            throw new ClassCastException("Expecting Integers");
////        }
////        
////        Integer pos1 = (Integer)obj1;
////        Integer pos2 = (Integer)obj2;  
////        Integer dist = (Integer)additionalInfo;
////        
////        if(pos1 == pos2){ 
////            violated = 1; //note..............
////            return violated;
////        }
////        
////        if(pos1 == pos2){ //same row
////            violated = 1; //note..............        
////        }else if (Math.abs(pos1-pos2) == dist){//diagonally same
////            violated = 1;
////        }else{
////            violated = 0;
////        }
////        
////        return violated;
////    }
////
////    @Override
////    protected void readData() throws MyException {
////        String val;
////        
////        val = JOptionPane.showInputDialog(null, "Enter queen problem size (n)", "Nqueen Problem", JOptionPane.QUESTION_MESSAGE);
////        if(val == null)
////            throw new UnsupportedOperationException("Cannot accept null value");
////        chessBoardSize_ = Integer.parseInt(val);
////                    
////        //set user input values...        
////        userInput_.fileData = true;//???
////        userInput_.totalConstraints = chessBoardSize_;
////        userInput_.totalDecisionVars = chessBoardSize_; //currently i take it one for all ordinal values..
////        userInput_.totalObjectives = 1; //currently i take it one for all ordinal values..
////
////        for (int i = 0; i < userInput_.totalDecisionVars; i++) {
////            userInput_.minVals.add(0.0);
////            userInput_.maxVals.add(userInput_.totalConstraints*1.0-1);            
////        }               
////        
////        userInput_.validateData();
////        //userInput_.population = Math.min(chessBoardSize_*chessBoardSize_, userInput_.population); //it is minimum.
////
////        userInput_.domainVals = new  ArrayList<ArrayList<Double>>();
////        for (int i = 0; i < chessBoardSize_; i++) {
////            userInput_.domainVals.add(new ArrayList<Double>());
////            for (int j = 0; j < chessBoardSize_; j++) {
////                userInput_.domainVals.get(i).add(j*1.0);
////            }
////        }
////
////        userInput_.doMutation = true;
////    }
////    
////}

 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csp;

import static csp.CCSPfns.knownOptSolDP;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.activation.UnsupportedDataTypeException;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import org.jdesktop.application.Application;
//import org.jfree.data.category.DefaultCategoryDataset;
//import org.jfree.data.xy.XYDataset;
//import org.jfree.data.xy.XYSeries;
//import org.jfree.data.xy.XYSeriesCollection;


public class CspProcess{
    //static boolean abToHoJaFlag;
    public boolean bMatlabDraw;
    private boolean drawStart;
    private boolean axisDrawn;
    private ArrayList<Chromosome> chromosomes_;
    private Queue<Chromosome> suspended_;
    private static Chromosome bestSoFar;
    private static Chromosome bestSoFar_prevProgressive;
    private double prevBest_;
    private double curBest_;
    private int stillSameBestCount;
    private ArrayList<Chromosome> solutions_;
    private ArrayList<ArrayList<Double>> chromeValues;
    public static UserInput userInput_;
    private ExternalData externalData_;
    //private int population_;
    //private int generation_;
    private int poolSize_;
    private int tourSize_;
    private int knearest_;
    private String dataType_;
    private Double[] range_;
    static int MAX_MOVES = 20;//This is 5 always updated in chomosome.isMarriageCompatible
    private final int MUM = 20;
    private final int MU = 20;
    private double MUTATION_RATE = 0.5;
    //private final int ARCHIVE_MAX;
    private final double REPLACE_PERCENT = 0.10; //8% PERCENT of chromosomes replaced by new population
    private final double IMMUNITY_PERCENT = 0.10; 
    private final double PARTIAL_SOL_PERCENT = 0.10;
    private MyRandom r_;
    private ArrayList<ArrayList<Double>> sameBestChromeVals_; //stores top ranked SAME_BEST_VAL_PERCENT % of chromosomes
    private int hasAllSame_; //counter to check if the SAME_BEST_VAL_PERCENT % of chromosomes is same for SAME_BEST_GENERATIONS generations
    private final double SAME_BEST_VAL_PERCENT = 0.5; //top ranked percentage of total chromosome population
    private final int SAME_BEST_GENERATIONS = 12; //12;//measure to SAME_BEST_VAL_PERCENT % of top ranked chromosomes remain same for number of generations.
    private final int NO_PROGRESS_LIMIT = 6;//limit for no progress made in the NO_PROGRESS_LIMIT generations.
    static boolean bStagnant;
    static int stagnantCount;
    private int stagnantVisit;
    public static double bringCloserRatio = 0.5;
    public static double sendFurtherRatio = 0.2;
    private static boolean bOptimizationMode;
    public static double maxCSPval;
    /**
     * Number of function call or function evaluations
     */
    public static int NFC=0;
    
    
    private static final int minPow = -10;
    private static final int maxPow = -8;
    private static final double MIN_DT = Math.pow(10, minPow);
    private static final double MAX_DT = Math.pow(10, maxPow); 
    
    /**
     * used in highly constrained functions in {@link CCSPfns}. For example see 
     * {@link CCSPfns#chem()}, {@link CCSPfns#h77(int)} etc. Its value is set
     * inside these functions. 
     */
    public static double margin = MAX_DT; 
    
    /**
     * Keeps CSP solutions only. Not COP solutions.
     * It checks if all the current constraints are satisfied then accepts the
     * solution into CSPsols repertoire. 
     */
    public static ArrayList<ArrayList<ArrayList<Double>>> CSPsols;
    private final double FORCED_PERCENT = 0.75;
    public static boolean bInTransition;
    static int MaxComb; //10
    private int MaxHospital = 1; //2; //MaxComb/2;
    private static int dynamicTime;
    public static int dynamicTimeInc = 1; //* NOTE it can be changed in Gx function
    public static  int useConstraintNo = -1;
    private static int gensEachConstraints = -1;
    public static ArrayList<Double> bestCOPLowLevelfitnessHistory; 
    private double tabuDist;
    public static int negFeasibleRange;
    private int maxSolPop; //(int)(userInput_.population*0.25);
    private int maxNonSolPop;
    public static ArrayList<Chromosome> sols;
    public static ArrayList<Chromosome> nonSols;
    private volatile Thread myThread;
   
    
    
    public int RAcommunitySize;//10
    private ArrayList<Chromosome> RAcommoners_; //from reincarnation algorithm (RA)
    private ArrayList<Chromosome> RAatheist;
//    private ArrayList<Chromosome> RAgurusLikely; //from reincarnation algorithm (RA)
    private boolean RAinfluenceWithBest = true;
    private ArrayList<Chromosome> RAgurus;
    private ArrayList<Chromosome> RAgurusTabu;
    public int RAmaxAge;
    private int RAdegreeOfInfluence;
    private int RAdegreeOfInfluenceINITIAL = -1;
    private final double RAblindTrust = 0.25;
    private Queue<Chromosome> RAsuspendedQueue;
    private int RAcommonerSize;
    private int RAguruSize;
//    private int RAatheistSize;
    private int RAsuspendRate; 
    public double RAfullInfluencePer = 0.0; //NO FULL?
    public double rhoCOP = 0.5;
    public double rhoCSP = 5.0;
    private static boolean RAstarted = false;
    private static int gGen = 0; //generation
    
    public static int getgGen() {
        return gGen;
    }
    
    private static int gGenLastBestOn = 0;
    private static final int gGenTrackStruggle = 10;
    private static MyQueue<Chromosome> prevBests;
    private static double bestProgressThreshold;
    private static boolean bTabuMode = false;
    public static boolean inLowLevelMode = false;
    
    private static final int SORT_HARDCONSVIOS_THEN_FITNESS = 1;
    private static final int SORT_HARDCONSVIOS_THEN_RHO = 2;
    private static final int SORT_FITNESS_THEN_NOVELTY = 3;
    private static final int SORT_FITNESS = 4;
    private static final int SORT_SATISFACTION = 5;
    
    private ArrayList<Chromosome> top5 = new ArrayList<Chromosome>();
    
    /**
     * 
     * @param userInput
     * @throws MyException 
     */
    public CspProcess(UserInput userInput) throws MyException{
        //this();
        this.userInput_ = userInput;
        this.externalData_ = null;

        if(userInput_ == null){
            throw new MyException("No user input provided.", "Incorrect Data",JOptionPane.ERROR_MESSAGE);
        }

        initialize();
    } // Toavoid calling this constructor

    public double getPARTIAL_SOL_PERCENT() {
        return PARTIAL_SOL_PERCENT;
    }

    public static double getBestProgressThreshold() {
        if(prevBests.curSize()<prevBests.capacity()){
            return Double.MAX_VALUE;
        }else{
            double avg = 0.0;
            ArrayList<Chromosome> c = new ArrayList<Chromosome>();
            while(prevBests.curSize()>0){
                c.add(prevBests.dequeue());
            }
            for (int i = 0; i < c.size()-1; i++) {                
                avg += Math.abs(c.get(i).getRank()-c.get(i+1).getRank());
            }
            while(c.size()>0){
                prevBests.forceEnqueueByDequeue(c.remove(0));
            }
            avg = avg/prevBests.curSize();
            return avg;
        }
    }
    
    
    

    public CspProcess(ExternalData externalData)throws MyException{
        //this();
        this.externalData_ = externalData;
        this.userInput_ = this.externalData_.getUserInput();

        if(userInput_ == null || this.externalData_ == null){
            throw new MyException("No user input provided or empty external data.", "Incorrect Data",JOptionPane.ERROR_MESSAGE);
        }

        initialize();
    }

    private CspProcess(){
        ;
    }

    public static void upgradeBestSoFar(final Chromosome newBest) {
//        if(bestSoFar == null){
//            gGenLastBestOn = gGen;
//            CspProcess.bestSoFar = (Chromosome)newBest.clone();
//            prevBests.clearAll();
//            return;
//        }
//        
//        if(newBest.isMorePromisingThan(bestSoFar)){ //assume its always better... 
            double improvement = Math.abs(newBest.getRank()-getBestSoFar().getRank());
//            if(gGen-gGenLastBestOn > gGenTrackStruggle){// && improvement > getBestProgressThreshold()/5){
                prevBests.forceEnqueueByDequeue((Chromosome)newBest.clone());
//            }
            
            if(improvement>getBestProgressThreshold()/5)
                gGenLastBestOn = gGen;
            
//            if(newBest.isMorePromisingThan(bestSoFar))
            if(bTabuMode){
                //Here fitness is more important because at this stage we are only after better fitness.
                if(newBest.isMorePromisingThan(getBestSoFar()) && newBest.getRank()<getBestSoFar().getRank()){
                    setBestSoFar((Chromosome)newBest.clone());
                }
            }else{
                setBestSoFar((Chromosome)newBest.clone());
            }
//        }                
    }

    public static int getDynamicTime() {
        return dynamicTime;
    }

    public static void dynamicTimeIncrement(){
        dynamicTime+=dynamicTimeInc;
        if(dynamicTime > userInput_.maxDynamicTime){// userInput_.totalConstraints-userInput_.getTotalDecisionVars()){
            dynamicTime = userInput_.maxDynamicTime; //userInput_.totalConstraints-userInput_.getTotalDecisionVars();        
        }
        

        double diff;
        diff = (maxPow - minPow)*dynamicTime/userInput_.maxDynamicTime;
        CspProcess.margin = MAX_DT*Math.pow(10, -diff); 
        
        setbOptimizationMode(false);
        gGenLastBestOn = gGen;
        
        if(!bTabuMode)
            prevBests.clearAll();
    }
    
    public static Chromosome getBestSoFar() {
        return bestSoFar;
    }
    
    public static void setBestSoFar(Chromosome ch){
//        if(ch.age>10 || CspProcess.gGen<=1) //causes sorting error
//        if(ch.age>0)
            bestSoFar = ch;
    }

    public static void setbOptimizationMode(boolean bOptMode) {
        CspProcess.bOptimizationMode = bOptMode;
        if (bOptMode){
            Chromosome.tmpSortBy = Chromosome.BY_FITNESS;
        }else{
            Chromosome.tmpSortBy = userInput_.solutionBy;
        }
    }

    
    
    
    /**
     * private Constructor used only for default initialization
     */
    private void initialize() throws MyException{
        //abToHoJaFlag = false;
        bMatlabDraw = false;
        drawStart = false;
        axisDrawn = false;
        chromosomes_ = new ArrayList<Chromosome>();
        suspended_ = new LinkedList<Chromosome>();
        solutions_ = new ArrayList<Chromosome>();
        this.tourSize_ = 2; //default value assumed
        this.knearest_ = (int)(0.05*userInput_.population); //default value assumed
        this.r_ = new MyRandom();
        
        this.poolSize_ = userInput_.population/2; //default values assumed
        //ARCHIVE_MAX = userInput_.population/2;
        this.dataType_ = this.userInput_.dataType;

        this.range_ = new Double[userInput_.getTotalDecisionVars()];
        for (int i = 0; i < userInput_.getTotalDecisionVars(); i++) {
            this.range_[i] = 0.5; //double assumed.
        }

        if (this.userInput_.population < 5 || this.userInput_.generation < 1){
            throw new MyException("poulation size should be > 5 and generation should be > 1", "Input Data Error!",JOptionPane.ERROR_MESSAGE);
        }
        hasAllSame_ = 0;
        sameBestChromeVals_ = null;
        bStagnant = false;
        stagnantCount = 0;
        prevBest_ = Double.POSITIVE_INFINITY;
        curBest_ = Double.POSITIVE_INFINITY;
        stillSameBestCount = 0;
        stagnantVisit = 0;
        setbOptimizationMode(false);
        maxCSPval = Double.MAX_VALUE;
        dynamicTime = 0;//0
        negFeasibleRange = 0;
        tabuDist = -1.0;
        setGensEachConstraints();
        
        CSPsols = new ArrayList<ArrayList<ArrayList<Double>>>();
        CSPsols.add(new ArrayList<ArrayList<Double>>());
        for (int i = 0; i < userInput_.totalConstraints; i++) {
            CSPsols.get(0).add(new ArrayList<Double>());
        }
        bestCOPLowLevelfitnessHistory = new ArrayList<Double>(); //new double[(int)(userInput_.generation*1.0/gensEachConstraints)]; //example 400/40 = 10.
        
        bInTransition = false;
        
        RAgurus = new ArrayList<Chromosome>();
        RAcommoners_ = new ArrayList<Chromosome>();
        RAsuspendedQueue = new LinkedList<Chromosome>();
        RAguruSize = (int)(0.1*userInput_.population);
//        RAatheistSize = RAguruSize;
        RAcommonerSize = userInput_.population - RAguruSize; // inclusive of atheist...atheist? may use isAtheist?
        RAcommunitySize = RAcommonerSize/RAguruSize;

        maxSolPop = (int)(userInput_.population*0.75); //RAguruSize*3; //
        maxNonSolPop = userInput_.population-maxSolPop;
        
        sols = new ArrayList<Chromosome>();
        nonSols = new ArrayList<Chromosome>(); //should merge in CSPsols

        if(maxSolPop<RAguruSize){
            throw new UnsupportedOperationException("maxSolPop must be atleast 2x more than RAguruSize");
        }

        RAsuspendRate = (int)(RAcommonerSize*0.1);
        RAmaxAge = Math.max(5,userInput_.totalConstraints/2);
        RAdegreeOfInfluence = 1; //(int)Math.ceil(userInput_.getTotalDecisionVars()*0.1); //hes-5%, sta-10%, ute - 15%, tre etc - 20%

        prevBests = new MyQueue<Chromosome>(4);
        
        MaxComb = Math.max(5,userInput_.totalConstraints); 
        top5 = new ArrayList<Chromosome>();
    }

    public static int getGensEachConstraints() {
        return gensEachConstraints;
    }

    public static void setGensEachConstraints() {
        gensEachConstraints = userInput_.generation/userInput_.maxDynamicTime;
    }

    public double getRAfullInfluencePer() {
        return RAfullInfluencePer;
    }

    public void setRAfullInfluencePer(double RAfullInfluencePer) {
        this.RAfullInfluencePer = RAfullInfluencePer;
    }
    
    
    /**
     * Starts the whole process
     */
    public void start(JProgressBar pb, boolean saveChromes, ByRef nextPrefSuggestion, Draw draw, Thread inMyThread) throws MyException{
        this.myThread = inMyThread;
        ArrayList<Chromosome> parents;
        ArrayList<Chromosome> offspring;
        ArrayList<Chromosome> temp;
        double startTime = 0.0;
        double endTime = 0.0;
        int totalSaved;
 
        PrintWriter runHistory = null; 
        
        File directory=null;
        int fileNo=0;
        final String matlabFigHandle="FitnessHistory";
        final String matlabFigName="Fitness_History";
        String problemName ="";
        
        try{
            
        directory = new File ( "..\\TestResults" ) ;
        
        String tmp = "G";
        if(userInput_.isBiLevel)
            tmp = "TP"+userInput_.tpxFn;
        else{
            if(userInput_.gxFn == 240)
                tmp = tmp+"24\\_0";
            else if(userInput_.gxFn == 241)
                tmp = tmp+"24\\_1";
            else if(userInput_.gxFn == 243)
                tmp = tmp+"24\\_3";
            else if(userInput_.gxFn == 244)
                tmp = tmp+"24\\_4";
            else
                tmp = tmp+userInput_.gxFn;
        }
        final String problem = tmp;
        problemName = problem;
        
        File [ ] filesInDir = directory.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return (pathname.getName().startsWith(problem+"-") 
                            && pathname.getName().endsWith("_history.csv"));
                }
            }
        );

        
        fileNo = 1;

        if(filesInDir.length>0){
            fileNo = filesInDir.length+1;
        }

        runHistory = new PrintWriter(directory.getAbsolutePath() + "\\"+problem+ "-"+fileNo+"_history.csv");

        //columns
        runHistory.println("Problem"+","+"RunNo" + "," + "Gen"+","
            + "Best Fitness UL - F"+","+"Best Fitness LL - f" + ","+"Violations" + "," 
            + "DynamicTime"+","+"Processing Time" + "," + "||bestknown-F||" + "," + "||bestknown - f||" + ","
            + "minError" + "," + "NFC");

        initializeChromosomes(this.chromosomes_, userInput_.population, gGen);
        this.chromosomes_.get(0).age = 1;
        setBestSoFar(this.chromosomes_.get(0));
        this.chromosomes_.get(0).age = 0;
//            bestCOPLowLevelfitnessHistory.add(getBestSoFar().getFitnessLowerLevel());

        startTime = System.nanoTime();
        startTime = startTime/Math.pow(10, 9);


        //calculate constraint strengths:
//        uncomment it to determine constraint strength. Just change the population
//        size in GUI to 10,000 to more. And then run the desired function. That's it!
//            int feasible = 0;
//            for (Chromosome c : chromosomes_) {
//                if(c.getRankComponents().size() <= 1){
//                    feasible++;
//                }
//            }
//            System.out.println("constraint strenght ro: " + feasible*1.0/chromosomes_.size());
//            System.exit(1);        
        //
        
        for (gGen = 1; gGen <= userInput_.generation; gGen++) {
            if (myThread.isInterrupted()){                        
                Thread.interrupted();
                System.out.println("start up process interrupted...");

                synchronized(myThread){     
                    System.out.println("about to call wait() for start thread. " +myThread.getState());                            
                    myThread.wait();    
                    System.out.println("wait finished for start thread" +myThread.getState());   
                    System.out.println("RESUME start...");
                }
            }
                
            inLowLevelMode = true;
           
            chromosomes_ = mutationBiLevel(chromosomes_);                  
////                   Chromosome.sortByLowLevelFitness = true;//doing it for local search
////                    RAapply(); //causing sorting error for sortTwice... 
////                    Chromosome.sortByLowLevelFitness = false;//Bi-level requires upper level F to be minimized.                  
            parents = noveltyTournamentSelection(); //select best parents only.
            offspring = interRaceCrossover(parents); //crossover selected parents only  

            Chromosome.sortByLowLevelFitness = false;//Bi-level requires upper level F to be minimized.
////                    mutation(offspring);//mutating crossovered offspring only                                                                            
            parents.clear();//no longer needed               
            
            chromosomes_.addAll(offspring);//include all offspring into the chromosome set.                        
                                       
            temp = new ArrayList<Chromosome>();
            initializeChromosomes(temp, userInput_.population-chromosomes_.size(), gGen);
            chromosomes_.addAll(temp);

//            System.out.println("Extra chromes: " + (chromosomes_.size()-userInput_.population));
            sortAndReplace(gGen);//sort according to least violation first then on ro value (novelty) 

            top5.clear();
            for (int i = 0; i < 5; i++) {
                if(chromosomes_.get(i).isSolution()){
                    top5.add(chromosomes_.get(i));
                }else{
                    break;
                }
            }
                    
////            if(userInput_.isBiLevel){
////                ArrayList<Chromosome> oldChromes = new ArrayList<Chromosome>();
////                for (int i = 0; i < 0.1*chromosomes_.size(); i++) {
////                    if(chromosomes_.get(i).age>0 && chromosomes_.get(i).isSolution()){
////                        oldChromes.add(chromosomes_.get(i));
////                    }                                
////                }
////
////                if(!oldChromes.isEmpty()){
////                    Chromosome.tmpSortBy = Chromosome.BY_AGE;
////                    Chromosome tmpBest = (Chromosome)Collections.max(oldChromes).clone();
////                    Chromosome.tmpSortBy = userInput_.solutionBy;
////                    setBestSoFar(tmpBest);
////                }
////            }
                    
////            Chromosome.tmpSortBy = Chromosome.BY_AGE;
////            Chromosome tmpBest = (Chromosome)Collections.max(chromosomes_.subList(0, 10)).clone();
////            Chromosome.tmpSortBy = userInput_.solutionBy;
////            setBestSoFar(tmpBest);
                    
                    
            System.out.println("Gen: "+gGen); 
            System.out.println(getBestSoFar());
            System.out.println("Lower Level history: "+bestCOPLowLevelfitnessHistory);

            endTime = System.nanoTime();
            endTime = endTime/Math.pow(10, 9);
            endTime = MyMath.roundN(endTime - startTime,2);


            System.out.println("Gen: " + gGen + ", Dynamic Time: " + getDynamicTime()+"/"
                    + userInput_.maxDynamicTime
                    + ", time: "+endTime+" Sec");
            
//            runHistory.println(gGen+","+getBestSoFar().getFitnessVal()+","+getBestSoFar().getRankComponents().size() + "," 
//                    + dynamicTime+","+endTime);

            System.out.println("top ones at DP="+knownOptSolDP);
            System.out.println("NFC: " + NFC);
            
            String F,f;
            for (Chromosome t5 : top5) {
                String p = "";
                if(!t5.isSolution()){
                    p = "(x)" + t5.getRankComponents().toString(); 
                }else{
                    p = "("+t5.age+")";
                }
                F = new DecimalFormat ("#.#####").format(t5.getFitnessVal());
                f = new DecimalFormat  ("#.#####").format(t5.getFitnessLowerLevel());
                System.out.print(p+"["+ F +", ");
                System.out.print(f +"], ") ;
                
                Double deltaF, deltaf;
                deltaF = Math.abs(CCSPfns.knownOptSol-Double.parseDouble(F));
                deltaf = Math.abs(CCSPfns.knownOptSolLowerLevel-Double.parseDouble(f)); 
                
                runHistory.println(userInput_.tpxFn + "," + fileNo + "," + gGen+","+F+"," + f+","
                    + getBestSoFar().getRankComponents().size() + "," 
                    + dynamicTime + "," + endTime + ","
                    + deltaF + ","
                    + deltaf + ","
                    + (deltaF + deltaf) + ","
                    + NFC);
            }

            System.out.println("");
            System.out.println("sols: " + sols.size()+"/"+maxSolPop);
            System.out.println("NonSols: " + nonSols.size()+"/"+maxNonSolPop);
            
            if(bMatlabDraw){
                draw.startDrawing(matlabPlotBuildGeneration(matlabFigHandle, matlabFigName));
            }

            if(pb != null)
                pb.setValue(pb.getMinimum()+(pb.getMaximum()-pb.getMinimum())*gGen/(userInput_.generation));
        }            
    }catch(SolutionFoundException SFE){
        System.out.println("\nSolution found at generation " + (gGen));
        System.out.println("Reason: " + SFE.getMessage());
        runHistory.println("\n\nSolution found at generation " + (gGen));
        runHistory.println("Reason: " + SFE.getMessage());
        try {
            if(bMatlabDraw){
                draw.startDrawing(matlabPlotBuildGeneration(matlabFigHandle, matlabFigName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//            g = userInput_.generation;
        pb.setValue(pb.getMinimum()+(pb.getMaximum()-pb.getMinimum())*userInput_.generation/(userInput_.generation));            
    }catch(MyException me){
        me.showMessageBox();
    }catch(UnsupportedDataTypeException udte){
        udte.printStackTrace();
    }catch (MatlabConnectionException mce) {
        mce.printStackTrace();           
    }
    catch(Exception e){
        e.printStackTrace();
        throw new MyException("Exception raised in Start Process", "Check Start Process()",JOptionPane.ERROR_MESSAGE);           
    }finally{
        endTime = System.nanoTime();
        endTime = endTime/Math.pow(10, 9);
        System.out.println("Process time(Sec): " + MyMath.roundN(endTime - startTime,2));
        System.out.println("total chromosomes: "+chromosomes_.size());
        System.out.println("Total Evaluations: "+ NFC);


        runHistory.println("Process time(Sec): " + MyMath.roundN(endTime - startTime,2));
        runHistory.println("total chromosomes: "+chromosomes_.size());
        runHistory.println("Total Evaluations: "+ NFC);


        System.err.flush();
        System.out.flush();
        //Thread.currentThread().sleep(100);//sleep for 1000 ms

        setSolution();
        if(this.solutions_.isEmpty()){                            
            System.out.println("No Solution Found :( ****************");
            System.out.println("best chromosomes\n" + getBestSoFar());
            runHistory.println("No Solution Found :( ****************"); 
            runHistory.println("\nbest chromosomes\n" + getBestSoFar());                
        }else{
            System.out.println("Solution Found");
            System.out.println("best chromosomes\n" + getBestSoFar());
            runHistory.println("\n\nSolution Found"); 
            runHistory.println("\nbest chromosomes\n" + getBestSoFar());
        }  

        runHistory.close();
            
        ArrayList<String> MatlabCommands;

        if(bMatlabDraw){
            MatlabCommands = new ArrayList<String>();

            String FigFileName = directory.getAbsolutePath(); 

            FigFileName = FigFileName+"\\"+problemName+ "-"+fileNo+matlabFigName;

            MatlabCommands.add("outFile = ['"+ FigFileName +".fig' ]");
            MatlabCommands.add("savefig("+matlabFigHandle+",outFile);");
            try {
                draw.startDrawing(MatlabCommands);
            } catch (MatlabInvocationException mie) {
                mie.printStackTrace();
                System.exit(0);
            }
        }

        if(externalData_ != null){ //external data is used.  
            nextPrefSuggestion.setVal(String.valueOf(externalData_.getNextPrefLimit()));
            String fileName = "partial_solutions_pref_"+externalData_.getCurPref()+".ichea";
            try {
                FileOutputStream fos;
                fos = new FileOutputStream(fileName);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                Set<Chromosome> s = new LinkedHashSet<Chromosome>(chromosomes_);
                chromosomes_ = new ArrayList<Chromosome>(s); 
                totalSaved = Math.min(chromosomes_.size(),(int)(PARTIAL_SOL_PERCENT*userInput_.population));

                chromosomes_ = new ArrayList<Chromosome>(chromosomes_.subList(0, totalSaved));
                chromosomes_.add(0, getBestSoFar());
                for (Chromosome c : chromosomes_) {
                    try{
                        c.refreshFitness();
                    }catch (SolutionFoundException sfe){
                        ;
                    }
                }
                    
                oos.writeObject(chromosomes_);//chromosomes_.subList(0, (int)(PARTIAL_SOL_PERCENT*userInput_.population)));
                oos.flush();
                oos.close();
                System.out.println("["+ totalSaved + "] chromosomes of data successfully Saved to File ["+fileName+"].");
            } catch (FileNotFoundException fnfe) {
                System.err.println("Serialize Error! File cannot be created.");
            } catch (IOException ioe){
                ioe.printStackTrace();
                System.err.println("Serialize Error! Cannot write to the file ["+fileName+"].");
            }                
        }
    }    
}

/**
     * only applicable to solutions.
     */    
    private ArrayList<Chromosome> cleanChromes(ArrayList<Chromosome> chromes, final int rmTotal){  
        
        chromes = sortTwice(Chromosome.BY_VIOLATIONS, Chromosome.BY_FITNESS, chromes, chromes.size()); //Math.min(list.size(),listPop*2)); 

        ArrayList<Element> gap = new ArrayList<Element>();
        try{  
        if(/*chromes.get(0).isSolution() &&*/ Math.random()<MyMath.expProbablity(gGen, userInput_.generation)/1.0){//was 2.0
            for (int i = 1; i < chromes.size(); i++) {
                /*if(!chromes.get(i).isSolution()){
                    break;
                }*/
                gap.add(new Element( Math.abs(chromes.get(i).getFitnessVal()-chromes.get(i-1).getFitnessVal()) ,i));
            }
        }else{
            return chromes;
        }
            
        
        Collections.sort(gap);//Bad ones first, good ones later
        
        //do some cleaning remove 20% bad ones - bad ones are those who are very
        //close to each other "relatively"
        //NOTE: you can use Vector Quantization
//        final double rmPercent = 0.1; 
//        int rmTotal=0;
//
//        rmTotal = (int)Math.floor(gap.size()*rmPercent);
        
        if(rmTotal <= 0){
            return chromes;
        }

        Integer [] rmIdx = new Integer[rmTotal];
        for (int i = 0; i < rmTotal; i++) {
            rmIdx[i] = gap.remove(0).idx;
        }                
        
        Arrays.sort(rmIdx, Collections.reverseOrder());//bigger index first

        for (int r : rmIdx) {
            chromes.remove(r);            
        }         

        System.out.println(rmTotal + " chromes cleaned!");
        
        
                }catch(IllegalArgumentException e){
            System.out.println(gap);
            System.out.println("");
        }
        
        return chromes;

        
    }

    public static int getMaxComb() {
        return MaxComb;
    }


    
    public static int getDyanmicTime(){
        return CspProcess.dynamicTime;
    }
    
    
    private boolean allDynamicConstraintsIncluded(){
        //why minus getTotalDecisionVars()?
        //See input data: totalConstraints = totalConstraints + getTotalDecisionVars()
        return (CspProcess.dynamicTime >= userInput_.maxDynamicTime);
    }
    
    private void RAapply() throws Exception, SolutionFoundException{

//        if(!bOptimizationMode || !allDynamicConstraintsIncluded()){
//            return;
//        }
        
        if(!RAstarted){
            System.out.println("\nRA started..................");
            RAstarted = true;
        }
        //make commoners and gurus...
        ArrayList<Integer> selectGuruIdx = MyMath.negExpFnSelection(maxSolPop, RAguruSize, 2, false);

        for (Chromosome c : chromosomes_) {
            c.RAclearAttributes();
        }

        RAgurus.clear();
        int tmp = 0;
        for (Integer i : selectGuruIdx) {
            RAgurus.add(chromosomes_.remove(i.intValue()-tmp++));
        }

        RAcommoners_.clear();
        RAcommoners_.addAll(chromosomes_);
        
        for (Chromosome ath : RAcommoners_) {
            ath.RAclearAttributes();
            
            if(Math.random()<0.2){
                ath.RAmakeAtheist();
            }
        }
        
        //MUST do this at the end
        chromosomes_.addAll(RAgurus);

        if(!getBestSoFar().isSolution()){
            return;
        }
   
        int commonerId = -1;

        Chromosome guru, commoner;                                                    

        boolean isBlind = false;
//        int[] guruStillBad = new int[RAgurus.size()];//default val will be 0 for each element
//        boolean bGuruStillBad;
        for (int t = 0; t < RAmaxAge; t++) {
            commonerId = 0;            
            int gID=-1;
            for (gID = 0; gID < RAgurus.size(); gID++){//Chromosome guru : RAgurus) {
//                bGuruStillBad = false;
                guru = RAgurus.get(gID);
                isBlind = false;
                
                RAperformKarma4guru(guru, t, isBlind);
                
                
                for (int cm = 0; cm < RAcommunitySize; cm++) {
//                    isBlind = (Math.random()<RAblindTrust) ? false:true;
                    isBlind = true;
                    commoner = RAcommoners_.get(commonerId);

//                    if(Math.random()<0.1){
//                        commoner.RAmakeDead();
//                    }

                    if(!commoner.isRAalive() || !commoner.isSolution()){
                        continue;
                    }

                    if(commoner.isRAatheist()){
                        RAperformKarma4athiest(commoner, t, isBlind); // true??; //he knows what to follow....mmm not necessary.... 
                    }else{
                        RAperformKarma4commoner(guru, commoner, t, isBlind);
//                        if (commoner.isMorePromisingThan(guru)){
//                            bGuruStillBad = true;
//                        }
                        //if(t>RAmaxAge/10 && guruStillBad[gID] > RAmaxAge/5){//check only certain generations.
//                            guruStillBad[gID] = 0;
                            if (commoner.isMorePromisingThan(guru)){
                                RAcommoners_.set(commonerId, guru);
                                RAgurus.set(gID, commoner);
                            }
                        //}
                    }                                             
                    commonerId++; 
                }
//                if(bGuruStillBad){
//                    guruStillBad[gID]++;                    
//                }else{
//                    guruStillBad[gID]=0;
//                }  
            }                             
        }                                             
    }

    private void RAperformKarma4guru(final Chromosome guru, final int curAge,
            final boolean isBlindFollower) throws SolutionFoundException, Exception{
        
        Chromosome finalTarget;
        ArrayList<Double> finalTargetVal = new ArrayList<Double>();
        finalTarget = new Chromosome(this.userInput_.solutionBy, this.userInput_); 
        
        double rnd;
        //finalTargetVal = userInput_.minVals; //don't need to keep minTarget. It can be modified.      
        for (int i = 0; i < userInput_.minVals.size(); i++) {
            rnd = Math.random();
            if(rnd < 0.5){
                finalTargetVal.add(userInput_.minVals.get(i).doubleValue()+ 
                        (getBestSoFar().getVals(i).doubleValue()-userInput_.minVals.get(i).doubleValue())*Math.random());
            }else{
                finalTargetVal.add(guru.getVals(i).doubleValue()+ 
                        (userInput_.maxVals.get(i).doubleValue()-getBestSoFar().getVals(i).doubleValue())*Math.random());
            }
        }     
        
        finalTarget.setVals(finalTargetVal);
        guru.RAinfluencePartialWith(finalTarget, RAdegreeOfInfluence, isBlindFollower);
                    
        
    }
    
    /**
     * Can check boundary values
     * @param atheist
     * @param curAge
     * @param isBlindFollower
     * @throws SolutionFoundException
     * @throws Exception 
     */
    private void RAperformKarma4athiest(final Chromosome atheist, final int curAge,
            final boolean isBlindFollower) throws SolutionFoundException, Exception{
        Chromosome minTarget, maxTarget, finalTarget;
             
        //min Target is modified in finalTarget don't worry
        minTarget = new Chromosome(this.userInput_.solutionBy, this.userInput_);                      
        minTarget.setVals(userInput_.minVals);
        
        maxTarget = new Chromosome(this.userInput_.solutionBy, this.userInput_);
        
        maxTarget.setVals(userInput_.maxVals);
        
        ArrayList<Double> finalTargetVal = new ArrayList<Double>();
        finalTarget = new Chromosome(this.userInput_.solutionBy, this.userInput_); 
        double rnd;
        //finalTargetVal = userInput_.minVals; //don't need to keep minTarget. It can be modified.      
        for (int i = 0; i < userInput_.minVals.size(); i++) {
            rnd = Math.random();
            if(rnd<0.4){//towards min
                finalTargetVal.add(userInput_.minVals.get(i).doubleValue());
            }else if(rnd < 0.8){//towards max
                finalTargetVal.add(userInput_.maxVals.get(i).doubleValue());
            }else{ //somewhere in between min and max
                finalTargetVal.add(userInput_.minVals.get(i).doubleValue()+ 
                        (userInput_.maxVals.get(i).doubleValue()-userInput_.minVals.get(i).doubleValue())*Math.random());
            }
        }      
        finalTarget.setVals(finalTargetVal);  
        atheist.RAinfluencePartialWith(finalTarget, RAdegreeOfInfluence, isBlindFollower);
        double rnk = atheist.getRank();
        
//        if(Math.random()<0.5){//towards min
//            atheist.RAinfluencePartialWith(minTarget, RAdegreeOfInfluence, isBlindFollower);
//        }else{//towards max
//            atheist.RAinfluencePartialWith(maxTarget, RAdegreeOfInfluence, isBlindFollower);
//        }
        
//        if(atheist.getRank()<=5.5){
//            System.out.println("bad robot: " + atheist.getRank() + ", " 
//                    + atheist.Xdebug_getViolations().size() + ", BF4: " + rnk);
//        }
        

    }    

    private void RAperformKarma4commoner(final Chromosome guru, final Chromosome commoner, final int curAge,
            final boolean isBlindFollower) throws SolutionFoundException, Exception{

            if(curAge <= RAfullInfluencePer * RAmaxAge){
                //(1) Influence with guru
                //<<blind
//                prevFit = commoner.getFitnessVal(0);
                //>>
//                externalData_.RAinfluenceFull(commoner.satisfactions_, guru.satisfactions_, RAdegreeOfInfluence);
//                if(commoner.getFitnessVal(0)>prevFit)
//                    commoner.satisfactions_ = satCommoner; //revert
                //(2) Influence with best guru
////                prevFit = commoner.getFitnessVal(0);
                commoner.RAinfluenceFullWith(guru, RAdegreeOfInfluence, isBlindFollower);
                commoner.RAinfluenceFullWith(getBestSoFar(), RAdegreeOfInfluence, isBlindFollower);
//                externalData_.RAinfluenceFull(commoner.satisfactions_, guru.satisfactions_, RAdegreeOfInfluence);
//                externalData_.RAinfluenceFull(commoner.satisfactions_, bestSoFarCOP.satisfactions_, RAdegreeOfInfluence);

                //<<4 blind
//                if(commoner.getFitnessVal(0)<prevFit || isBlindFollower){ // better or blind follower. let it change
//                    ;
//                }else
//                    commoner.satisfactions_ = satCommoner; //revert
                //>>
                //(3) Influence with closed ones
                //?
//                commoner.RAupdateFitness();
            }else{
//                if(guru.isSolution()){
                    //(1) Influence with guru
                    commoner.RAinfluencePartialWith(guru, RAdegreeOfInfluence, isBlindFollower);
                    commoner.RAinfluencePartialWith(getBestSoFar(), RAdegreeOfInfluence, isBlindFollower);
                    
                    //(2) Influence with best guru
//                    externalData_.RAinfluencePartial(commoner.satisfactions_, bestSoFarCOP.satisfactions_, RAdegreeOfInfluence);
                    //commoner.RAupdateFitness();
//                }
            }
        
    }
    
    
    public ArrayList<Chromosome> getSolution(){
        return this.solutions_;
    }
    
    public ArrayList<Chromosome> getChromosomes(){
        return this.chromosomes_;
    }
    
    /**
     * It is the measure of novely. The larger the ro value the more the novelty
     * in the search space.
     * @param chrome
     * @return
     * @throws MyException
     * @throws UnsupportedDataTypeException 
     */
    double getRoValue(Chromosome chrome) throws MyException, UnsupportedDataTypeException{
        double ro;
        if(this.dataType_.contains("Integer")){
            ro = getIntegerRoValue(chrome);
        }
        else if(this.dataType_.contains("Double")){
            ro = getDoubleRoValue(chrome);
        }else{
            throw new UnsupportedDataTypeException("Only supports Integer and Double data type");
        }
        return ro;
    }
    
    /**
     * method determines ro value for nominal data types. the higher the better.
     * @param chrome
     * @return
     * @throws MyException 
     */
    double getIntegerRoValue(Chromosome chrome) throws MyException{
        double ro;
        ArrayList<Integer> validChromosomesIdx = new ArrayList<Integer>();
        Double []dist;
        int tempKnearest;
        
        if (chromosomes_.isEmpty()){
            throw new MyException("No chromosme population", "Variable Initialization Error",JOptionPane.ERROR_MESSAGE);
        }
 
        for (int i = 0; i < chromosomes_.size(); i++) {
            if (chromosomes_.get(i).getRank() != this.userInput_.totalConstraints)
                validChromosomesIdx.add(i);            
        }

        dist = new Double[validChromosomesIdx.size()];
        for (int i = 0; i < validChromosomesIdx.size(); i++) {
            dist[i] = MyMath.norm(chrome.getValsCopy(), chromosomes_.get(validChromosomesIdx.get(i)).getValsCopy(), MyMath.DIST_DUPLICATE_MISMATCH);
//            NOTE: I am using "SQUARE of distance" instead of just distance
//            because I will be using variance for ro_min.
            dist[i] = Math.pow(dist[i], 2);

        }
        Arrays.sort(dist);        
        // x1 itself is included in this set which should have the value 0.
        if(dist.length<=knearest_){ //////@Danger code............................
            tempKnearest = dist.length-1;
        }else{
            tempKnearest = knearest_;
        }
        ro = (1.0/tempKnearest) * MyMath.sum(dist, 0, tempKnearest);//Note: should not be knearest-1 as x1 itself is also included

        ro = MyMath.roundN(ro, 0); //to reduce so much variations...
        return ro;                     
    }
    
    /**
     * ro value determines the rank of novelty. The higher value the better.
     * @param chrome
     * @return
     * @throws MyException 
     */
    double getDoubleRoValue(Chromosome chrome) throws MyException{
        double ro;
        int maxViolation = this.userInput_.totalConstraints;
        ArrayList<Integer> validChromosomesIdx = new ArrayList<Integer>();
        Double []dist;
        int tempKnearest;
        
//        if(chrome.getRank() == maxViolation-1){
//            return -1.0;
//        }
        
        if (chromosomes_.isEmpty()){
            throw new MyException("No chromosme population", "Variable Initialization Error",JOptionPane.ERROR_MESSAGE);
        }
 
        for (int i = 0; i < chromosomes_.size(); i++) {
            if (chromosomes_.get(i).getRank() != maxViolation)
                validChromosomesIdx.add(i);            
        }

        dist = new Double[validChromosomesIdx.size()];
        for (int i = 0; i < validChromosomesIdx.size(); i++) {
            dist[i] = MyMath.norm(chrome.getValsCopy(), chromosomes_.get(validChromosomesIdx.get(i)).getValsCopy(), MyMath.DIST_EUCLEADIAN);
//            NOTE: I am using "SQUARE of distance" instead of just distance
//            because I will be using variance for ro_min.
            dist[i] = Math.pow(dist[i], 2);

        }
        Arrays.sort(dist);        
        // x1 itself is included in this set which should have the value 0.
        if(dist.length<=knearest_){ //////@Danger code............................
            tempKnearest = dist.length-1;
        }else{
            tempKnearest = knearest_;
        }
        ro = Math.pow(1.0/tempKnearest, 2) * MyMath.sum(dist, 0, tempKnearest);//Note: should not be knearest-1 as x1 itself is also included

        ro = MyMath.roundN(ro, 0);
        return ro;
    }


    private ArrayList<String> matlabPlotBuildGeneration(String FigHandle, String FigName) throws Exception{
        ArrayList<String> MatlabCommands = new ArrayList<String>();
        int drawXdata;
        String drawYdata;
        String drawLowYdata ="";
        boolean badLimit = false;
        String title = "G";
        int fitnessUpdate = 0;
        if(userInput_.isBiLevel){
            title = "TP"+userInput_.tpxFn;
            fitnessUpdate = 1;
        }
        else{
            fitnessUpdate = -1;
            if(userInput_.gxFn == 240)
                title = title+"24\\_0";
            else if(userInput_.gxFn == 241)
                title = title+"24\\_1";
            else if(userInput_.gxFn == 243)
                title = title+"24\\_3";
            else if(userInput_.gxFn == 244)
                title = title+"24\\_4";
            else
                title = title+userInput_.gxFn;
        }
                
//        if(!drawStart){            
//            
//            
//////            drawStart = true;
//        }

        
        drawXdata = gGen; //x axis;
//        drawYdata = fitnessUpdate*MyMath.roundN(getBestSoFar().getFitnessVal(),1);//y axis
        
        drawYdata = "[";
        for (int i = 0; i < top5.size(); i++) {
            if(top5.get(i).getFitnessVal()<-500 || top5.get(i).getFitnessVal()>500){
                top5.remove(i);
                i--;
            }else{
                drawYdata += fitnessUpdate*MyMath.roundN(top5.get(i).getFitnessVal(),1) + " ";
            }
        }
        drawYdata +="]";
        
        
        
        
//        int lowerLimit, upperLimit;
//        lowerLimit = MyMath.numberIntegerPart(CCSPfns.knownOptSol);
        
        if(top5.isEmpty()){//drawYdata<-500 || drawYdata>500){
            badLimit = true;
        }else{
            MatlabCommands.add("xF=ones(1,"+top5.size()+")*" + drawXdata + ";"); 
            MatlabCommands.add("F=" + drawYdata + ";");
            
            if(userInput_.isBiLevel){
//                drawLowYdata = fitnessUpdate*MyMath.roundN(getBestSoFar().getFitnessLowerLevel(),1);//y axis
                drawLowYdata = "[";
                for (int i = 0; i < top5.size(); i++) {
                    if(top5.get(i).getFitnessLowerLevel()<-500 || top5.get(i).getFitnessLowerLevel()>500){
                        top5.remove(i);
                        i--;
                    }else{
                        drawLowYdata += fitnessUpdate*MyMath.roundN(top5.get(i).getFitnessLowerLevel(),1) + " ";
                    }
                }
                drawLowYdata += "]";
                
                if(top5.isEmpty()){//drawLowYdata<-500 || drawLowYdata>500){
                    badLimit = true;
                }else{
                    MatlabCommands.add("plot(xF,F,'.k');");//print first
                    
                    MatlabCommands.add("xf=ones(1,"+top5.size()+")*" + drawXdata + ";");
                    MatlabCommands.add("f=" + drawLowYdata + ";");
                    MatlabCommands.add("plot(xf,f,'or');");//print second
                }
            }else{
                MatlabCommands.add("plot(x,F,'.k');");//print first
            }
        }
        
        if(!badLimit){
            if(!drawStart){
                int idx = 0;
                MatlabCommands.add(idx++,FigHandle +" = figure('Name','"+FigName+"');");
                //mainFig = clf(mainFig); in case of multiple runs. 
                //mainFig = figure(mainFig);
                MatlabCommands.add(idx++,"hold on;");
                MatlabCommands.add(idx++,"title('"+title+"','FontSize',20);"); 
                MatlabCommands.add(idx++,"xlabel('Generations','FontSize',20);");
                MatlabCommands.add(idx++,"ylabel('Fitness','FontSize',20);");  
                MatlabCommands.add(idx++,"xlim([0 " + userInput_.generation+"]);");
                MatlabCommands.add(idx++,"ylim(["+CCSPfns.lLimit + " " + CCSPfns.uLimit +"]);");
                MatlabCommands.add("legend({'F','f'},'FontSize',20);"); //see what are you printing first and what second.
//                 MatlabCommands.add("lgd.FontSize=14");
                drawStart = true;
            }

            if(drawStart){//gGen%getGensEachConstraints()==0){
                MatlabCommands.add("Yrange = ylim;");
                MatlabCommands.add("maxVal = ceil(Yrange(2));");
                MatlabCommands.add("minVal = ceil(Yrange(1));");
                if(!axisDrawn) {
                    int xAxis = getGensEachConstraints()/2;
                    final int add = (int)(xAxis/2);
                    int writeAt = add;
                    String [] modelType = {"EO_F","O_F","O_f","EO_f"};
                    for (int i = 1; i <= userInput_.maxDynamicTime*2; i++) {
                        MatlabCommands.add("vertX = ones(maxVal-minVal+1,1)*"+xAxis+";");  
                        MatlabCommands.add("vertY = [minVal:maxVal];");
                        MatlabCommands.add("plot(vertX,vertY,'b-');");
                        MatlabCommands.add("text("+writeAt+",minVal+0.75*(maxVal-minVal),'"+modelType[i-1]+
                                "','FontSize',30,'Color',[0.7,0.7,0.7],'FontWeight','bold','FontSmoothing','on');");
                        writeAt=xAxis+add;
                        xAxis = xAxis+getGensEachConstraints()/2;
                        
                    }
                    
                    
////                    MatlabCommands.add("xRange = [0 : " + userInput_.generation/4 + " : " + userInput_.generation + "];");
////                    MatlabCommands.add("xticks(xRange);");
                    ////                MatlabCommands.add("xticklabels(strtrim(cellstr(num2str(xRange'))'));");
                    
                    MatlabCommands.add("horzX = [1:"+userInput_.generation+"];");  
                    MatlabCommands.add("horzY = ones("+userInput_.generation+",1)*"+CCSPfns.knownOptSol+";");
                    MatlabCommands.add("plot(horzX,horzY,'b-');");
                    
                    MatlabCommands.add("horzY = ones("+userInput_.generation+",1)*"+CCSPfns.knownOptSolLowerLevel+";");
                    MatlabCommands.add("plot(horzX,horzY,'b--');");
                    
                    String Fcaption = "";
                    String fcaption = "";
                    double uloc, lloc;
                    if(CCSPfns.uLimit>CCSPfns.lLimit){
                        Fcaption = "Fbest = sprintf('best F \\n   \\\\downarrow ');";
                        fcaption = "fbest = sprintf('   \\\\uparrow \\n best f ');";
                        uloc = CCSPfns.knownOptSol+0.05*(CCSPfns.uLimit-CCSPfns.lLimit);
                        lloc = CCSPfns.knownOptSolLowerLevel-0.05*(CCSPfns.uLimit-CCSPfns.lLimit);
                    }else{
                        fcaption = "fbest = sprintf('best f \\n   \\\\downarrow ');";
                        Fcaption = "Fbest = sprintf('   \\\\uparrow \\n best F ');"; 
                        uloc = CCSPfns.knownOptSol-0.05*(CCSPfns.uLimit-CCSPfns.lLimit);
                        lloc = CCSPfns.knownOptSolLowerLevel+0.05*(CCSPfns.uLimit-CCSPfns.lLimit);
                    }
                    
                    MatlabCommands.add(Fcaption);                    
                    MatlabCommands.add("text("+userInput_.generation*0.70+","+uloc +",Fbest,'FontSize',18);");
                    
                    MatlabCommands.add(fcaption);                   
                    MatlabCommands.add("text("+userInput_.generation*0.90+","+lloc +",fbest, 'FontSize',18);");
                    axisDrawn = true;
                }
            }
       
//            MatlabCommands.add("set(gca,'XTick',0:"+getGensEachConstraints()/2+":"+userInput_.generation+",'FontSize',20);");    
            MatlabCommands.add("set(gca,'XTick',0:" + userInput_.generation/5 +":"+userInput_.generation+",'FontSize',20);");  
            MatlabCommands.add("drawnow;");
        }


        return MatlabCommands;
    }
    
    private ArrayList<String> matlabPlotBuildConstraints(){
        ArrayList<String> commands = new ArrayList<String>();
        
                commands.add("hold on;");
        commands.add("x = [-100:0.1:100];");
        commands.add("br = 10.0;");
        commands.add("sr = 9.9;");
        commands.add("y1p = sqrt(br^2 - x.^2);");
        commands.add("y1m = -sqrt(br^2 - x.^2);");
        commands.add("y2p = sqrt(sr^2 - x.^2);");
        commands.add("y2m = -sqrt(sr^2 - x.^2);");
        
        commands.add("y3p = sqrt(br^2 - (x+2*br - 0.1).^2);");
        commands.add("y3m = -sqrt(br^2 - (x+2*br - 0.1).^2);");
        commands.add("y4p = sqrt(sr^2 - (x+2*br - 0.1).^2);");
        commands.add("y4m = -sqrt(sr^2 - (x+2*br - 0.1).^2);");


        commands.add("fig1 = gcf;"); //get current figure or create figure fig1
        commands.add("axes1 = axes('Parent',fig1);"); //Create axes
        
        commands.add("ylim(axes1,[-100 100]);");
        commands.add("box(axes1,'on');");
        commands.add("hold(axes1,'all');");
        commands.add("plot(x,y1p);");
        commands.add("plot(x,y1m,'Parent',axes1);");
        commands.add("plot(x,y2p,'Parent',axes1);");
        commands.add("plot(x,y2m,'Parent',axes1);");
        commands.add("plot(x,y3p,'Parent',axes1);");
        commands.add("plot(x,y3m,'Parent',axes1);");
        commands.add("plot(x,y4p,'Parent',axes1);");
        commands.add("plot(x,y4m,'Parent',axes1);");
                
        
//        commands.add("hold on;");
//        commands.add("X1 = [-100:1:100];");
//        commands.add("YMatrix1 = -9*X1+6.0;");
//        commands.add("YMatrix2 = 9*X1 - 1.0;");
////        commands.add("XMatrix3 = 1;");
////        commands.add("XMatrix4 = 0;");
//        commands.add("YMatrix3 = -9*X1+7.0;");
//        commands.add("YMatrix4 = 9*X1 - 2.0;");
//
//        commands.add("fig1 = gcf;"); //get current figure or create figure fig1
//        commands.add("axes1 = axes('Parent',fig1);"); //Create axes
//        
//        commands.add("ylim(axes1,[-100 100]);");
//        commands.add("box(axes1,'on');");
//        commands.add("hold(axes1,'all');");
//        commands.add("plot(X1,YMatrix1);");
//        commands.add("plot(X1,YMatrix2,'Parent',axes1);");
////        commands.add("plot(XMatrix3,X1,'Parent',axes1);");
////        commands.add("plot(XMatrix4,X1,'Parent',axes1);");
//        commands.add("plot(X1,YMatrix3,'Parent',axes1);");
//        commands.add("plot(X1,YMatrix4,'Parent',axes1);");
        
        return commands;
    }
    
    /**
     * Remove stagnant best values from the population of chromosomes.
     * preprocess - chromosome population should be sorted and contains unique
     * chromosomes.
     * @param PERCENT - what PERCENT of chromosomes to be checked.
     * @param sameBestVals - Arraylist of same best val
     * @param sameGens - same best val for how many generations
     * @return
     */
//    private boolean isStagnant(){    
//        boolean allsame;
//        boolean bstagnant;
//        bstagnant = false;
//        allsame = false;
//        ArrayList<ArrayList<Double>> diverse;
//
//        if(sameBestChromeVals_ != null)
//            diverse = (ArrayList<ArrayList<Double>>)sameBestChromeVals_.clone();
//        else{
//            diverse = new ArrayList<ArrayList<Double>>();
//            sameBestChromeVals_ = new ArrayList<ArrayList<Double>>();
//        }
//
//        for (int ofsp = 0; ofsp < (int)(SAME_BEST_VAL_PERCENT*userInput_.population); ofsp++) {
//            diverse.add(chromosomes_.get(ofsp).getValsCopy());
//        }
//        
//        HashSet<ArrayList<Double>> hashSet = new HashSet<ArrayList<Double>>(diverse);
//        diverse = new ArrayList<ArrayList<Double>>(hashSet);
//
//        if(diverse.size() == sameBestChromeVals_.size()){
//            allsame = true;
//        }else{            
//            sameBestChromeVals_ = new ArrayList<ArrayList<Double>>();
//            for (int ofsp = 0; ofsp < (int)(SAME_BEST_VAL_PERCENT*userInput_.population); ofsp++) {
//                sameBestChromeVals_.add(chromosomes_.get(ofsp).getValsCopy());
//            }            
//        }
//
//        if(allsame){
//            hasAllSame_++;
//        }else{
//            hasAllSame_ = 0;
//        }
//
//        if(hasAllSame_ >= SAME_BEST_GENERATIONS){
//            hasAllSame_ = 0;
//            sameBestChromeVals_ = null;
//            bstagnant = true;
//        }
//
//        return bstagnant;
//    }

    private void initializeChromosomes(ArrayList<Chromosome> chromosome, final int SIZE, final int gen) throws Exception {
        boolean bInitialStage;
        if(SIZE<=0){
            chromosome = null;
            return;
        }
        
        if (externalData_ != null){
            if(gen<10){
                bInitialStage = true;
            }else{
                bInitialStage = false;
            }
                
            chromosome.addAll(externalData_.initializeExternalChrmosomes(SIZE));
        }else{
            initializeChromosomesRandomly(chromosome,SIZE);
        }
    }

    /**
     * Initializes Chromosomes with random values
     */
    private void initializeChromosomesRandomly(ArrayList<Chromosome> chromosome, final int SIZE) throws Exception{
        for (int i = 0; i < SIZE; i++) {            
            chromosome.add(initializeChromosomeRandomly());
        }        
    }
    
    
    private Chromosome initializeChromosomeRandomly() throws Exception{
        Object rand = null;
        Chromosome tempChromosome;
               
        tempChromosome = new Chromosome(this.userInput_.solutionBy, this.userInput_);
        for (int j = 0; j < userInput_.getTotalDecisionVars(); j++) {
            if (userInput_.dataType.contains("Integer")){
                rand = r_.randVal(userInput_.minVals.get(j).intValue(), userInput_.maxVals.get(j).intValue());
            }else if (userInput_.dataType.contains("Double")){
                if(Math.random()<0.5){
                    rand = r_.randVal((Double)userInput_.minVals.get(j), (Double)userInput_.maxVals.get(j));
                }else{
                    if(Math.random() < 0.5){
                        rand = userInput_.minVals.get(j);
                    }else{
                        rand = userInput_.maxVals.get(j);
                    }
                }
            }
            else{
                System.err.println("Incorrect use of data types");
                System.exit(1);
            }
            tempChromosome.appendVal((Double)rand);
        }
        return tempChromosome;       
    }
    

//    private void setFitness(ArrayList<Chromosome> chromosome, final int SIZE){
//        for (int ofsp = 0; ofsp < SIZE; ofsp++) {
//            //ObjectiveFunction.definition(chromosome.get(ofsp));
//            chromosome.get(ofsp).setObjectiveFunctionVars();
//        }
//    }

    /**
     * noveltyTournamentSelection() - Tournament selection based on novelty
     * of the chromosome in the population.
     * @return Returns ArrayList<Chromosome> of parent selected population 
     */
    private ArrayList<Chromosome> noveltyTournamentSelection() throws MyException, UnsupportedDataTypeException{
        ArrayList<Chromosome> candidates = new ArrayList<Chromosome>();
        ArrayList<Chromosome> parents = new ArrayList<Chromosome>(); // shoud have this.pool sizse
        ArrayList<Integer> temp;
        double csize0, csize1;
        double ro0, ro1;
        int candidate0dominates;
        
        if(this.tourSize_ != 2){
            throw new MyException("Tour Should be 2", "Inappropriate Tour Size",JOptionPane.ERROR_MESSAGE);
        }
                 
        for (int p = 0; p < this.poolSize_; p++) {
            //select tourSize_ chromosomes k.e 2 chromosomes randomly from the population
            temp = MyRandom.randperm(0, chromosomes_.size()-1);
            candidates.clear();
            for (int t = 0; t < this.tourSize_; t++) {                
                candidates.add(chromosomes_.get(temp.get(t)));
            }
            temp = null;

            try{                   
                if(candidates.get(0).getRankComponents().size() < candidates.get(1).getRankComponents().size()){
                    parents.add(candidates.get(0));
                }else if (candidates.get(0).getRankComponents().size() > candidates.get(1).getRankComponents().size()){
                    parents.add(candidates.get(1));
                }else{
                
                    csize0 = MyMath.roundN(candidates.get(0).getRank(),3);
                    csize1 = MyMath.roundN(candidates.get(1).getRank(),3);

                    if (csize0 < csize1){ // the lower the better
                        parents.add(candidates.get(0));                
                    }
                    else if (csize1 < csize0){
                        parents.add(candidates.get(1));                
                    }
                    else{  
                        //
                        //??here??    
                         //<< you can move it bottom .....
                        ro0 = getRoValue(candidates.get(0)); //do not need to use getRo function, check sortnreplace function if it has already been set in tempRo property.
                        ro1 = getRoValue(candidates.get(1));

                        if (ro0 > ro1 || candidates.get(0).getValsCopy().size() == 1) // the larger the better
                            parents.add(candidates.get(0));
                        else if (ro1 > ro0 || candidates.get(1).getValsCopy().size() == 1)
                            parents.add(candidates.get(1));
                        else{
                        //>>..........................                   
                            
                        //    
                        candidate0dominates = 0;

                        if(candidates.get(0).isStagnant(this.NO_PROGRESS_LIMIT)){
                            parents.add(candidates.get(1));
                        }else if(candidates.get(1).isStagnant(this.NO_PROGRESS_LIMIT)){
                            parents.add(candidates.get(0));
                        }else{
                            temp = MyRandom.randperm(0, 1);
                            parents.add(candidates.get(temp.get(0)));
                        }
                    }                                    
                }
                }
                }catch(Exception e){
                e.printStackTrace();
            }
//            //>>
        }                
        
        return parents;
    }
    
    /**
     * IMPROPER METHOD... NEEDS CORRECTION.... Checks if the solution for CSP has been achieved
     * @return Returns ArrayList<Chromosome> of solution chromosomes.
     */
    private void setSolution(){
        //ArrayList<ArrayList<Double>> duplicates = new ArrayList<ArrayList<Double>>();
        chromeValues = new ArrayList<ArrayList<Double>>();
        int beforeSize, afterSize;
                
        for (Chromosome chromosome : this.chromosomes_) {
            if(chromosome.isSolution()){ // no violations
//                beforeSize = chromeValues.size();  
//                //Collections.sort(chromosome.getValsCopy());
//                chromeValues.add(chromosome.getValsCopy());
//                //this.solutions_.add(chromosome); 
//                
//                
//                HashSet<ArrayList<Double>> hashSet = new HashSet<ArrayList<Double>>(chromeValues);
//                chromeValues = new ArrayList<ArrayList<Double>>(hashSet);            
//                afterSize = chromeValues.size();
//            
//                if(afterSize>beforeSize){
//                    this.solutions_.add(chromosome);
//                }    
                this.solutions_.add(chromosome);//no need to clone... it is called at the end
            }
        }  
        if(getBestSoFar().isSolution()){
            this.solutions_.add(getBestSoFar());
        }
    }
    
    public String printChromeValues(){
        String str;
        
        str = Integer.toString(chromeValues.size()) + "\n";
        str += Integer.toString(this.userInput_.totalConstraints) + "\n";
        for (int i = 0; i < chromeValues.size(); i++) {
            for (int j = 0; j < chromeValues.get(i).size(); j++) {
                str += chromeValues.get(i).get(j).toString() + " ";                
            }
            str += "\n";            
        }
        return str;
    }
    
//    private Chromosome notVals(Chromosome in){
//        Chromosome out;
//        
//        Double[] temp = new Double[userInput_.totalConstraints];
//        for (int i = 0; i < temp.length; i++) {
//            temp[i] = i*1.0;            
//        }
//        
//        for (Double startDrawing : in.getValsCopy()) {
//            temp[startDrawing.intValue()] = -1.0;
//        }
//        
//        ArrayList<Double> notVal = new ArrayList<Double>();
//        
//        
//        for (int i = 0; i < temp.length; i++) {
//            if(temp[i]!=-1.0){
//                notVal.add(temp[i]);
//            }       
//        }
//        
//        out = (Chromosome)in.clone();
//        out.setVals(notVal, maxCSPval);
//        return out;
//    }

     /**
     * inter race crossover - offers crossover between 2 different constraint regions only
     * the offspring will have better or same constraint violation than their parents.
     * This process requires 2 parents that produce 2 offspring
     * @param parents list of parents
     * @return returns offspring
     * @throws MyException
     * @throws UnsupportedDataTypeException
     */
    private ArrayList<Chromosome> interRaceCrossover(final ArrayList<Chromosome> parents) throws MyException, UnsupportedDataTypeException,  SolutionFoundException, Exception{
        ArrayList<Chromosome> candidates = new ArrayList<Chromosome>(this.tourSize_);
        ArrayList<Chromosome> offspring = new ArrayList<Chromosome>();
        ArrayList<Integer> tempIntAL; 

        //ArrayList<Double> directions;
        //ArrayList<Double> approachDist = new ArrayList<Double>(1);
        
        //double maxDist;
        //double ratio;
        int count;
        
        if(this.tourSize_ != 2 && parents.size() >1){
            throw new MyException("Tour Should be 2", "Inappropriate Tour Size",JOptionPane.ERROR_MESSAGE);
        }
        
        if(parents.isEmpty()){
            System.out.println("Sigh! no parents!");
        }
        
        for (int i = 0; i < userInput_.population/2; i++) {
            if(Math.random() < 0.9){
                //Randomly pick two parents.
                tempIntAL = MyRandom.randperm(0, parents.size()-1);
                candidates.clear();
                for (int t = 0; t < this.tourSize_; t++) {                
                    candidates.add((Chromosome)parents.get(tempIntAL.get(t)).clone());
                }
                
                try{
                    //Note here we can make integer and double combined problem
                    //set as well.
                    if(dataType_.contains("Integer")){
                        count = this.tourSize_;
                        //while both parents belong to same Constraint region
                        //Drawback - this will case very few crossover + very few
                        //final solutions. That might affect the optimization
                        //problem where we need many candidate solutions.
    //                    while(candidates.get(0).violations.containsAll(candidates.get(1).violations) &&
                        
//                        if(bStagnant && bestSoFar.getValsCopy().size() != userInput_.totalConstraints){
//                            candidates.clear();
//                            candidates.add(bestSoFar);
//                            candidates.add(notVals(bestSoFar));
//                        }else{
                            while(candidates.get(0).hasSameRankComponent(candidates.get(1))){// &&
                                    //candidates.get(0).getRank() == candidates.get(1).getRank()){
                                candidates.remove(1);
                                candidates.add(parents.get(tempIntAL.get(count)));
                                count++;
                                if(count >= parents.size()){
                                    //candidates.add(parents.get(tempIntAL.get(immuneCount-1)));
                                    break;
                                    //throw new MyException("No unique parents exist", "Parents in crossover",JOptionPane.WARNING_MESSAGE);
                                }
                            }
//                        }
                        
                        offspring.addAll(interRaceCrossoverInteger(candidates));//only 1 move
                        
                        
                    }else if(dataType_.contains("Double")){
                        //further filter for boundary intersections... 
                        count = this.tourSize_;

                        while(!candidates.get(0).isMarriageCompatible(candidates.get(1))){
                            candidates.remove(1);
                            candidates.add((Chromosome)parents.get(tempIntAL.get(count)).clone());
                            count++;
                            if(count >= parents.size()){                            
                                break;                            
                            }
                        }
                        
//                        if(userInput_.getTotalDecisionVars() < 10 && !bOptimizationMode)
                            offspring.addAll(interRaceCrossoverDoubleStagnant(this.MAX_MOVES, candidates));
//                        else
//                            offspring.addAll(interRaceCrossoverDouble(this.MAX_MOVES, candidates));
////                        
                        
                        
//                        if(candidates.get(0).isMarriageCompatible(candidates.get(1))){
//                            offspring.addAll(interRaceCrossoverDouble(this.MAX_MOVES, candidates));
//                        }else{
//                            offspring.addAll(interRaceCrossoverDouble(1, candidates));
//                        }
                        
                        //I used this when distance error function is used.....
//                        if(bOptimizationMode)
//                            offspring.addAll(interRaceCrossoverDouble(this.MAX_MOVES, candidates));
//                        else
//                            offspring.addAll(interRaceCrossoverDouble(1, candidates));
                        
                        
                    }else{
                        throw new UnsupportedDataTypeException("Only supports Integer and Double");
                    }         

                }catch (UnsupportedDataTypeException udte) {
                    throw new UnsupportedDataTypeException("Check your data type");
                }
//                catch (MyException me){
//                    me.printMessage();
//                }
            }
        }
        
        return offspring;
    }

    /**
     * interRaceCrossoverInteger - is used only with nominal data types. for integer data
     * use interRaceCrossoverDouble. it virtually moves 2 parents.
     * @param move
     * @param candidates - parents from which offspring are sought.
     * @return returns offspring from given candidate parents
     */
    private ArrayList<Chromosome> interRaceCrossoverInteger(final ArrayList<Chromosome> candidates) throws Exception{
        ArrayList<Chromosome> offspring = new ArrayList<Chromosome>();
        ArrayList<Integer> idx = new ArrayList<Integer>();
        Chromosome tempChrome;
        int move;
        boolean isSol;
        
        if (candidates.size() != 2){
            throw new UnsupportedOperationException("Require only 2 parents");
        }
        
        isSol = false;
        for (Chromosome can : candidates) {
            if(can.isSolution()){
                isSol = true;
                break;
            }
        }
        
        //Check common values in both candidate parents.
        int [] commonVals = new int[userInput_.totalConstraints]; //all initialized to 0
        int constVal = 1;
        
        
        for (int j = 0; j < candidates.size(); j++) {
            for (double v : candidates.get(j).getValsCopy()) {
                commonVals[(int)v] += constVal;
            }
            constVal = constVal*10;
        }
        //Those commonVals that have value 11 as element value, it means that is common in both parents.
        //otherwise it will have 1 or 10 respectively for both parents.
       
        int prevLength, newLength;
        //Technique 1 - Append chromosomes- multi-offpring (0-n) afrom 2 parents. - 
        //<< Build up structure for satisfaction list
//        if(Math.random() < 0.5){//5 && !isSol){ //!bStagnant){ //obviously Math.random is always [0 1)
//        if(!bOptimizationMode){
        if(!isSol){
            constVal = 1;
            for (int j = 0; j < candidates.size(); j++) {
                tempChrome = (Chromosome)candidates.get((j+1)%tourSize_).clone();
                prevLength = tempChrome.noGood.size();
                
                for (int i = 0; i < commonVals.length; i++) {
                    if(commonVals[i]==constVal){
                        tempChrome.appendVal(i);//NOTE ofsp want getSatisfaction value but in this case both are same
                    }                    
                }

                newLength = tempChrome.noGood.size();
                
//                if(newLength > prevLength){ //nogood added
//                    tempChrome.forceFindSolution();
//                }
                offspring.add((Chromosome)tempChrome);
                constVal = constVal*10;
            }

        }
        
//        for no goods... 
//        first remove all duplicates....
//        cross-over..
        
        //>>

        //Technique 2(a) - 1 point crossover, where split point is the half of the size of chromosome
        //crossover first half of one chromosome with second half of another chrromosome
        //<< Rebuild the satisfaction structure - because the solution is stagnant.
////        if(offspring.isEmpty()){
        else{ 
        /*    
            Chromosome p0;
            Chromosome p1;
            Chromosome init_p0;
            int part0, part1;
            final double PERCENT = 0.75;

            try{
                part0 = (int)Math.ceil(candidates.get(0).getValsCopy().size()*PERCENT);
                part1 = (int)Math.ceil(candidates.get(1).getValsCopy().size()*PERCENT);

                p0 = (Chromosome)candidates.get(0).clone();
                init_p0 = (Chromosome)p0.clone();
                p1 = (Chromosome)candidates.get(1).clone();

                if(part0 <=1 || part1 <=1){
                    p0.appendVal(candidates.get(1).getVals(0));
                    offspring.add(p0);
                    p1.appendVal(candidates.get(0).getVals(0));
                    offspring.add(p1);
                    return offspring;
                }               
                
                p0.restructure(1-PERCENT, true);
                p1.restructure(1-PERCENT, false);
                
                if(p0.getValsCopy().isEmpty() || p1.getValsCopy().isEmpty()){
                    System.out.println("stope reee...");
                }
                
                //add half one at a time
                for (int i = 0; i < p1.getValsCopy().size(); i++) {
                    prevLength = p0.noGood.size();
                    p0.appendVal(p1.getVals(i)); //here val and satisfaction values are same so getVal() method is fine here
                    newLength = p0.noGood.size();
                    if(newLength > prevLength){ //nogood added
                        p0.forceFindSolution();
                    }
                }
                
                //add half one at a time
                for (int i = 0; i < init_p0.getValsCopy().size(); i++) {
                    prevLength = p1.noGood.size();
                    p1.appendVal(init_p0.getVals(i)); //here val and satisfaction values are same so getVal() method is fine here
                    newLength = p1.noGood.size();
                    if(newLength > prevLength){ //nogood added
                        p1.forceFindSolution();
                    }
                }
                
                //rank check??????????????
//                if(p0.getRank() <= candidates.get(0).getRank())
                    offspring.add(p0);
//                if(p1.getRank() <= candidates.get(1).getRank())
                    offspring.add(p1);

            }catch(IndexOutOfBoundsException e){
                System.err.println("No offspring\n" + e.getLocalizedMessage());
            }
         */   
        }
        
        return offspring;
    }
   
    /**
     * highest valid index of CSPsols.
     * It is possible that the index = highest+1 is in buildup process and 
     * not available for usage.
     * 
     * @return highest valid index. -1 means no valid index available
     */
    private int maxIdxAvailCSPsols(){        
        int highestIndx = CSPsols.size()-1; //can be 1 initially.        
        
        for (ArrayList<Double> valGrp: CSPsols.get(highestIndx)) {
            if(valGrp.isEmpty()){
                highestIndx--; //it can become -1
                break;
            }
        }
        
        //last index can get cleared in CCSPfns so we take the second last
        
        if(highestIndx>0){
            highestIndx--;
        }
        
        return highestIndx;
    }
        
    
    /**
     * interRaceCrossoverDouble - can be used for interger or double data types.
     * double crossover reqires 2 parents and generate 2 offspring
     * process: the original genes of parents are moved closer to each other until
     * the better or same ofsp.e. (less or equal violations) is reached. the number
     * of moves is determined by  move parameter
     * @param move - number of maximum moves until the better/same solution is reached
     * @param candidates Two parents
     * @return Two offspring
     * @throws UnsupportedDataTypeException
     */
    
    private ArrayList<Chromosome> interRaceCrossoverDouble(final int move, final ArrayList<Chromosome> candidates) throws SolutionFoundException, UnsupportedDataTypeException, Exception{
        ArrayList<Double> delta;
        ArrayList<Double> newDelta;
        ArrayList<Chromosome> offspring = new ArrayList<Chromosome>();
        Chromosome childChrome = null;

        if (candidates.size() != 2){
            throw new UnsupportedOperationException("Require only 2 parents");
        }
      
        Chromosome p1 = null, p2=null; //parent 1 and parent 2;
        int pickedCons = -1;
        int randPickIdx = 0; //0 is always available... worst case can be empty..
        final int highestValidCSPidx = maxIdxAvailCSPsols();
        
        int trials = 0;
        boolean bMoved = false;  
        ArrayList<Chromosome> hospital = new ArrayList<Chromosome>();
 
        for (int j = 0; j < this.tourSize_; j++) {
            //directions = MyAlgorithms.getDirection(candidates.get(j).getValsCopy(), candidates.get((j+1)%tourSize_).getValsCopy());
            //maxDist = MyMath.norm(candidates.get(j).getValsCopy(), candidates.get((j+1)%tourSize_).getValsCopy(), MyMath.DIST_EUCLEADIAN);
        
            
            p1 = candidates.get(j);
            
            if(highestValidCSPidx >= 0 && Math.random()<FORCED_PERCENT && !p1.isSolution() && p1.getRankComponents().size()>0){ // p1.getRankComponents().size() caters for transition sols                        
                pickedCons = MyRandom.randperm(0, p1.getRankComponents().size()-1).get(0);
                pickedCons = p1.getRankComponents().get(pickedCons);
                
                p2 = new Chromosome(this.userInput_.solutionBy,  userInput_);
                randPickIdx = MyRandom.randperm((int)Math.floor(0.5*highestValidCSPidx), highestValidCSPidx).get(0);
                p2.setVals(CSPsols.get(randPickIdx).get(pickedCons));   
                
            }else{
                if(highestValidCSPidx == -1 && !p1.isSolution() && p1.getRankComponents().size()>0){ //I use then when it is difficult to find CSP
                    for (int i = 0; i < p1.getRankComponents().size(); i++) {
                        pickedCons = p1.getRankComponents().get(i);
                        
                        
                        if(CSPsols.get(0).get(pickedCons).isEmpty()){
                            p2 = null;
                            continue;
                        }else{
                            p2 = new Chromosome(this.userInput_.solutionBy,  userInput_);
                            p2.setVals(CSPsols.get(0).get(pickedCons));
                            break;
                        }
                    }
                }                               
                
                if(p2 == null)
                    p2 = candidates.get((j+1)%tourSize_);  
            }            
            
            delta = MyMath.vectorSubtraction(p2.getValsCopy(), p1.getValsCopy());
            
            int k = 0;
            ArrayList<Double> prevVals = null, newVals=null;

            for (k = 1; k <= move; k++){ //) move; k++) {
                //find which direction to move?
                childChrome = new Chromosome(this.userInput_.solutionBy, this.userInput_);               
                newDelta = MyMath.constMultiplicationToVector(Math.pow(bringCloserRatio,k), delta); 
                newVals = MyMath.vectorAddition(p1.getValsCopy(), newDelta);
                childChrome.setVals(newVals);

                //vp = new VirusProliferate(movingChrome.vals.toArray(), this.range_);

                //**************************************************************************************************************//
                //NOTE: ofsp changed <= sign to < sign
                //It is now giving me less solutions
                //It is good or bad......... I don't know.... It only promotes local search.
                //if(childChrome.getRank() < p1.getRank()){// || (childChrome.getRank() <= p1.getRank() && move == 1)){                    
                    bMoved = false;

                    if(!childChrome.myParent(p1)){ //there is a gap/black hole so no point searching further.
                        hospital.add(childChrome);
                        bMoved = true;
                        break;
                    }
                    if(childChrome.isSolution() || childChrome.getRank()<=p1.getRank() || bStagnant){//|| p1.isMyChild(childChrome)){
                        //offspring.add(childChrome); // if using do while then put this line in correct place.
//                        childChrome.tempSortBy = userInput_.solutionBy;//refresh it
                        hospital.add(childChrome);
                        if(childChrome.isSolution()){
                            //bestSoFar = childChrome;//.clone()                                    
                            //throw new SolutionFoundException("Sol found during crossover...");
                        }
                        bMoved = true;
                        break;
                    }
                prevVals = newVals;
            }
            
            if(!hospital.isEmpty()){
                //Collections.sort(hospital);                
                offspring.add(hospital.get(0)); //(Chromosome)hospital.get(0).clone());
                hospital.clear();
            }
        }        
        return offspring;
    }
    
    
    private ArrayList<Chromosome> interRaceCrossoverDoubleStagnant(final int MOVE, final ArrayList<Chromosome> candidates) 
            throws SolutionFoundException, UnsupportedDataTypeException, Exception{
        ArrayList<Double> delta;
        ArrayList<Double> newDelta;
        ArrayList<Chromosome> offspring = new ArrayList<Chromosome>();
        Chromosome childChrome = null;
        ArrayList<Chromosome> tempMut;

        if (candidates.size() != 2){
            throw new UnsupportedOperationException("Require only 2 parents");
        }
      
        Chromosome p1 = null, p2=null, pTowardsBest = new Chromosome(this.userInput_.solutionBy, this.userInput_); //parent 1 and parent 2;
        int pickedCons = -1;
        int randPickIdx = 0; //0 is always available... worst case can be empty..
        final int highestValidCSPidx = maxIdxAvailCSPsols();
        int k;
        int trials = 0;
//        boolean bMoved = false;  
        ArrayList<Double> initDelta;
        //neighborhood = (ArrayList<Double>)Collections.unmodifiableList(initDelta);
        ArrayList<Double> neighborDelta;
//        ArrayList<Chromosome> hospital = new ArrayList<Chromosome>();
        boolean bCondition;
        ArrayList<Double> newVals;
        
        ArrayList<String> permutes;   
        ArrayList<ArrayList<Double>> combinations;
        //ArrayList<Double> dims = new ArrayList<Double>(); //dimensions..
        double dtemp;
//        boolean isInvalid = false;
        double coe = 0.5;
        
//        if (bOptimizationMode)
//            MaxHospital = MaxComb/2;
            
        for (int j = 0; j < this.tourSize_; j++) {
            //directions = MyAlgorithms.getDirection(candidates.get(j).getValsCopy(), candidates.get((j+1)%tourSize_).getValsCopy());
            //maxDist = MyMath.norm(candidates.get(j).getValsCopy(), candidates.get((j+1)%tourSize_).getValsCopy(), MyMath.DIST_EUCLEADIAN);
        
            
            p1 = candidates.get(j);
            
            if(!bOptimizationMode){
                if(highestValidCSPidx >= 0 && Math.random()<FORCED_PERCENT && !p1.isSolution() && p1.getRankComponents().size()>0){                        
                    pickedCons = MyRandom.randperm(0, p1.getRankComponents().size()-1).get(0);
                    pickedCons = p1.getRankComponents().get(pickedCons); 

                    p2 = new Chromosome(this.userInput_.solutionBy,  userInput_);
                    randPickIdx = MyRandom.randperm((int)Math.floor(0.5*highestValidCSPidx), highestValidCSPidx).get(0);
                    p2.setVals(CSPsols.get(randPickIdx).get(pickedCons));   

                }else{
                    if(highestValidCSPidx == -1 && !p1.isSolution() && p1.getRankComponents().size()>0){ //I use then when it is difficult to find CSP
                        for (int i = 0; i < p1.getRankComponents().size(); i++) {
                            pickedCons = p1.getRankComponents().get(i);


                            if(CSPsols.get(0).get(pickedCons).isEmpty()){
                                p2 = null;
                                continue;
                            }else{
                                p2 = new Chromosome(this.userInput_.solutionBy,  userInput_);
                                p2.setVals(CSPsols.get(0).get(pickedCons));
                                break;
                            }
                        }
                    }                               

                    if(p2 == null)
                        p2 = candidates.get((j+1)%tourSize_);  
                }            
            }else{
                if(p2 == null)
                    p2 = candidates.get((j+1)%tourSize_);
     
                
////                //PSO concept p2+bestsofar
//                pTowardsBest.setVals(MyMath.constMultiplicationToVector(Math.random()*2, MyMath.vectorAddition(p2.getValsCopy(), bestSoFar.getValsCopy())),maxCSPval);
//                p1.isMarriageCompatible(pTowardsBest);
//                delta = new ArrayList<Double>(MyMath.vectorSubtraction(pTowardsBest.getValsCopy(), p1.getValsCopy()));
//                for (k = 1; k <= MOVE; k++){ //) MOVE; k++) {                    
//                    childChrome = new Chromosome(this.userInput_.solutionBy, this.userInput_);
//                    newDelta = MyMath.constMultiplicationToVector(Math.pow(bringCloserRatio,k), delta); 
//                    newVals = MyMath.vectorAddition(p1.getValsCopy(), newDelta);
//                    childChrome.setVals(newVals, maxCSPval);
//
//                     if(childChrome.getRank()<=p1.getRank()){                        
//                        offspring.add(childChrome);
//                        break;
//                     } 
//                }                
                
            }

          
////            if(p1.isSolution()){
////                continue; //what? no optimization. Only relying on RA.
////            }

            Chromosome.sortByLowLevelFitness = false;
            double rn = Math.random();
            offspring.addAll(interMarriageOperatorAtomic(p1, p2, MOVE, rn, true, false));
            
        } 

        return offspring;
    }
    
    /**
     * 
     * @param chrome
     * @param N - size of list
     * @param rank 1:N
     * @return 
     */
    private ArrayList<Chromosome> hyperClone(Chromosome chrome, final int N, final int rank){
        int cloneSize;
        final double beta = 0.5;

        ArrayList<Chromosome> clones = new ArrayList<Chromosome>();

        cloneSize = (int)Math.ceil(beta*N/rank); //(int)Math.ceil(MyMath.expProbablity(rank, N)*N); //

        cloneSize = Math.min(cloneSize, 10);
        
        for (int j = 0; j < cloneSize; j++) {
            clones.add((Chromosome)chrome.clone());
        }

        return clones;
    }
    
    
    /**
     * No empty return.
     * @param off
     * @param cloneSize: inspired from clonax.
     * @return
     * @throws SolutionFoundException
     * @throws Exception 
     */
    private Chromosome mutationBiLevel(final Chromosome off, final int N, final int rank)
    throws SolutionFoundException, Exception{
//        int cloneSz = 50; //DO NOT make it 1. It DOES NOT work.
        Chromosome tmpC;
        ArrayList<Chromosome> tmpClist = new ArrayList<Chromosome>();
        ArrayList<Chromosome> clones = hyperClone(off, N, rank);
//        Chromosome boundryOff;
//        double rand;
//        double maxMultiple, minMultiple;
//        for (int i = 0; i < cloneSz; i++) {
        for(Chromosome boundryOff: clones){
//            boundryOff = (Chromosome)off.clone();
            
            for (Integer lidx : userInput_.getLowerLevelIdx()) {
//                if(Math.signum(Chromosome.curMaxVals.get(lidx))>0){ //+
//                    maxMultiple = 2;
//                }else
//                    maxMultiple = 0.5;
//                
//                if(Math.signum(Chromosome.curMinVals.get(lidx))>0){ //+
//                    minMultiple = 0.5;
//                }else
//                    minMultiple = 2;
                
                if(Math.random()>0.5)
                    boundryOff.setVal(lidx, userInput_.maxVals.get(lidx)); //maxMultiple*Chromosome.curMaxVals.get(lidx)); // 
                else
                    boundryOff.setVal(lidx, userInput_.minVals.get(lidx)); //minMultiple*Chromosome.curMinVals.get(lidx)); //
            }
            double rn = Math.random(); //1-gGen%getGensEachConstraints()*0.75/getGensEachConstraints();//Math.random(); //0.5+Math.random()*(1.0-0.5); //
            Chromosome.sortByLowLevelFitness = true;
            tmpClist.addAll(interMarriageOperatorAtomic(off, boundryOff, this.MAX_MOVES, rn, false, true));
        }
        
        if(tmpClist.isEmpty())
            tmpC = off;
        else{
            Chromosome.tmpSortBy = Chromosome.BY_FITNESS;
            if(gGen >= getDynamicTime()*getGensEachConstraints() && 
            gGen <= getDynamicTime()*getGensEachConstraints() + getGensEachConstraints()/2.0)
                Chromosome.sortByLowLevelFitness = false; //false: tolerate for infeasible => high diversity [Pessimistic - somewhat  - Wolfram Wiesemann(optimistic), Colson(pessimistic) & Anurag]
            else
                Chromosome.sortByLowLevelFitness = true; //Q2 - actual optimistic:: true: no tolerance for infeasible => low diversity [optimistic - Sinha]
            
            Collections.sort(tmpClist);
            
            tmpC = tmpClist.get(0);
        }
        
        if(tmpC.getFitnessLowerLevel()<off.getFitnessLowerLevel()){
            off.violatesLowLevelConstraint = true; //
            tmpC.age=0; //this is NEW off. Can we trust him at this stage?
        }else{
            off.violatesLowLevelConstraint = false;
            tmpC.age++; //this is same OLD off. It proves it is now becoming reliable. Age prop reliability
        }
        
        return tmpC;
    }
    
    /**
     * if better then return better otherwise return same.
     * @param chromes
     * @return
     * @throws SolutionFoundException
     * @throws Exception 
     */
    private ArrayList<Chromosome> mutationBiLevel(ArrayList<Chromosome> chromes)
    throws SolutionFoundException, Exception{
        ArrayList<Chromosome> offBiLevel = new ArrayList<Chromosome>();
        if(userInput_.isBiLevel && CspProcess.inLowLevelMode){
        //do bi-level update....
            ArrayList<Chromosome> tmpClist = new ArrayList<Chromosome>();
            Chromosome tmpC;
            Chromosome boundryOff;
            int N = chromes.size();
            int rank = 1;
            for (Chromosome off : chromes) {
//                if(off.isSolution()){
                    tmpC = mutationBiLevel(off,N,rank++);  
//                    tmpC.age = off.age+1;
//                }

                offBiLevel.add(tmpC);
            }
            
            chromes = offBiLevel;
        }
        return chromes;
    }
    
    public ArrayList<Chromosome> interMarriageOperatorAtomic(Chromosome p1, Chromosome p2, 
            final int MOVE, final double closerRatio, final boolean doOptimization, final boolean forLowerlevel)
    throws SolutionFoundException, UnsupportedDataTypeException, Exception{
        ArrayList<Chromosome> hospital = new ArrayList<Chromosome>();
        ArrayList<Double> neighborDelta;
        ArrayList<Double> delta;
        ArrayList<Double> newDelta;
        ArrayList<ArrayList<Double>> combinations;
        ArrayList<Double> newVals;
        ArrayList<Double> initDelta;
        Chromosome childChrome = null;
        ArrayList<Chromosome> offspring = new ArrayList<Chromosome>();
        boolean bCondition;
        int k;
        
        p1.isMarriageCompatible(p2);
            
        initDelta = new ArrayList<Double>(MyMath.vectorSubtraction(p2.getValsCopy(), p1.getValsCopy()));
        hospital.clear();
        combinations = MyMath.getXrandomBinPermutes(userInput_.getTotalDecisionVars(), MaxComb);              
        delta = initDelta;
        
        for(ArrayList<Double> dims: combinations){ //for doOptimization                              
            k = 0;
            newVals=null;

            for (k = 1; k <= MOVE; k++){ //) MOVE; k++) {
                //find which direction to MOVE?
                childChrome = new Chromosome(this.userInput_.solutionBy, this.userInput_);
                newDelta = MyMath.constMultiplicationToVector(Math.pow(closerRatio,k), delta); 

                newVals = MyMath.vectorAddition(p1.getValsCopy(), newDelta);                    
                childChrome.setVals(newVals);
//??? check deb ranking
////                if(k<MOVE){
////                     bCondition = childChrome.isMorePromisingThan(p1, forLowerlevel) /* .getRank(forLowerlevel)<=p1.getRank(forLowerlevel)*/ || (bStagnant && !forLowerlevel);
////                    
////                }
////                else   
                    bCondition = childChrome.isMorePromisingOrEqThan(p1, forLowerlevel);// /* .getRank(forLowerlevel)<=p1.getRank(forLowerlevel)*/ || (bStagnant && !forLowerlevel);
               
                
                if(bCondition && forLowerlevel && p1.getRankComponents().isEmpty()){
                        bCondition = bCondition;
                    } 
                
                if(bCondition){
//                    childChrome.age = p1.age+1;
                    hospital.add(childChrome);                    
                    break;
                }

                if(!childChrome.myParent(p1)){ //there is a gap/black hole so no point searching further.
                    break;
                }
            }
             
            if(forLowerlevel){
                break;
            }
            
            if(doOptimization){    
                if(forLowerlevel){
                    for (Integer ll : userInput_.getLowerLevelIdx()) {
                        dims.set(ll, 1.0);
                    }
                }
                                
                neighborDelta = (ArrayList < Double >)MyMath.vectorMultiplication(true, dims, initDelta);                
                p2 = new Chromosome(this.userInput_.solutionBy, this.userInput_);
                p2.setVals(MyMath.vectorAddition(p1.getValsCopy(), neighborDelta));  
                delta = neighborDelta; //MyMath.vectorSubtraction(p2.getValsCopy(), p1.getValsCopy()); 
                
////                //<<
////                p2 = (Chromosome)p1.clone();
////                for (Integer lidx : userInput_.getLowerLevelIdx()) {
////                    if(Math.random()>0.5)
////                        p2.setVal(lidx, userInput_.maxVals.get(lidx)); //maxMultiple*Chromosome.curMaxVals.get(lidx)); // 
////                    else
////                        p2.setVal(lidx, userInput_.minVals.get(lidx)); //minMultiple*Chromosome.curMinVals.get(lidx)); //
////                }
////                delta = new ArrayList<Double>(MyMath.vectorSubtraction(p2.getValsCopy(), p1.getValsCopy()));
////                bad hai 
////                        use delta 
////                //>>>
                p1.isMarriageCompatible(p2);
            }else{
                break;//Sure?what if closerRatio is random???
            }
                
        }          
        
        if(!hospital.isEmpty()){
            hospital = sortTwice(Chromosome.BY_VIOLATIONS, Chromosome.BY_FITNESS,hospital, Math.min(MaxHospital, hospital.size()));            
            for (int i = 0; i < Math.min(MaxHospital, hospital.size()); i++) {
                offspring.add(hospital.get(i)); 
            }             
            hospital.clear();
        }    
        
        return offspring;
    }


    /**
     * Mutate the given set of offspring.
     * @param offspring mutation applied only to offspring
     */
    private void mutation(ArrayList<Chromosome> offspring) throws UnsupportedDataTypeException, SolutionFoundException{
        if(offspring.isEmpty()){
            return;
        }
        if(!userInput_.doMutation){
            return;
        }

         //update the offspring
        if(this.dataType_.contains("Integer")){
            mutationInteger(offspring);
        }
        else if(this.dataType_.contains("Double")){
            mutationDouble(offspring);
        }else{
            throw new UnsupportedDataTypeException("Only supports Integer and Double data type");
        }
    }

     /**
     * mutationDouble only mutate Doubles. It uses Polynomial Mutation as described in NSGA - II <br>
     * <B>Note</B> that offspring ArrayList is updated here.
     * @param offspring offspring generated after crossover.
     */
    private void mutationDouble(ArrayList<Chromosome> offspring) throws SolutionFoundException{
        int size = offspring.size();
        ArrayList<Integer> randInts;
        Chromosome temp;
        double val;
        double rand;
        double add;
        int muteBits = (int)Math.ceil(0.1*userInput_.getTotalDecisionVars()); //10%
        
        for (int i = 0; i < size; i++) {
            
            try{
                if(Math.random()< MUTATION_RATE){//1.0/userInput_.getTotalDecisionVars()){
                    randInts = MyRandom.randperm(0, size-1);
                    temp = offspring.get(randInts.get(0));                   

                    for (int j : MyRandom.randperm(0, userInput_.getTotalDecisionVars()-1).subList(0, muteBits)){                   
//                    for (int j = 0; j < userInput_.getTotalDecisionVars(); j++) {
                        val = temp.getVals(j);
                        rand = Math.random();
                        if(rand<0.5)
                            add = Math.pow(2.0*rand,1.0/(MUM+1)) -1;
                        else
                            add = 1- Math.pow(2.0*(1-rand),1.0/(MUM+1));

                        val = val+add;

                        if(val>userInput_.maxVals.get(j))
                            val = userInput_.maxVals.get(j);
                        else if(val<userInput_.minVals.get(j))
                            val = userInput_.minVals.get(j);

                        temp.replaceVal(j, val); 
//                        temp.age++;
                    }                
                }  
            }catch(SolutionFoundException sfe){
                throw sfe;
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    
    private void mutationSwap(Chromosome ch, double maxSwap, final double maxIteration) throws Exception{
        ArrayList<Integer> randVal; // = MyRandom.randperm(0, ch.getValsCopy().size()-1);
        ArrayList<Double> vals = new ArrayList<Double>();
        double val0, val1;
        int lowIdx, hiIdx;
        //double maxSwap = 1; //0.05*ch.getValsCopy().size();
//        final double maxIteration = 4;
        double bfFitness, afFitness;
        
        if(ch.getValsCopy().size()<2){
            return;
        }
        
        bfFitness = ch.getFitnessVal();
        
        for (int j = 0; j < maxIteration; j++) {
            for (int i = 0; i < maxSwap; i++) {
                randVal = MyRandom.randperm(0, ch.getValsCopy().size()-1);

                lowIdx = randVal.get(0);
                hiIdx = randVal.get(1);

                if(lowIdx > hiIdx){
                    lowIdx = randVal.get(1);
                    hiIdx = randVal.get(0);
                }

                val0 = ch.getVals(lowIdx);
                val1 = ch.getVals(hiIdx);

                vals.add(val0);
                vals.add(val1);

                try {
                    ch.remove(lowIdx);
                    ch.remove(hiIdx-1);
                    //ch.appendVal(val1);
                    //ch.appendVal(val0);
                } catch (Exception e) {
                    e.printStackTrace();    
                    System.out.println("uffff... yeh ulfat..");

                }   
            }

            for (Double v : vals) {
                ch.appendVal(v);
            }
        
            afFitness = ch.getFitnessVal();
            
            if(afFitness!=bfFitness){
                if(afFitness<bfFitness){
                    afFitness = afFitness;
                }
                break;
            }
        }
    }
    
    private void mutationSwapNew(Chromosome ch, double maxSwap){
        ArrayList<Integer> randVal; // = MyRandom.randperm(0, ch.getValsCopy().size()-1);
        ArrayList<Double> vals = new ArrayList<Double>();
        double val0;
        int lowIdx;
        //double maxSwap = 1; //0.05*ch.getValsCopy().size();
//        final double maxIteration = 4;
        
        if(ch.getValsCopy().size()<2){
            return;
        }
        
        
        for (int i = 0; i < maxSwap; i++) {
            randVal = MyRandom.randperm(0, ch.getValsCopy().size()-1);

            lowIdx = randVal.get(0);               

            val0 = ch.getVals(lowIdx);
            vals.add(val0);

            try {
                ch.remove(lowIdx);
            } catch (Exception e) {
                e.printStackTrace();    
                System.out.println("uffff... yeh ulfat..");
            }   
        }

            
        ch.tryForcedCSPsolUpdate();
       
    }
    
    private void mutationGroupSwap(Chromosome ch) throws Exception{
        ArrayList<Integer> randVal = MyRandom.randperm(0, ch.getSatisfaction().size()-1);
        int Idx0, Idx1;
        int loc;
        ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
        final int sz = ch.getSatisfaction().size();
        
        if(Math.random()<0.1){
            loc = MyRandom.randperm(0, sz -1).get(0);
            
            for (int i = 0; i < sz; i++) {
                temp.add(ch.getSatisfaction().get(i));
            }
            
            for (int i = 0; i < sz; i++) {
                ch.getSatisfaction().set(i,temp.get((i+loc)%sz));
            }
           
        }else{
            Idx0 = randVal.get(0);
            Idx1 = randVal.get(1);

            ArrayList<Double> list0 = ch.getSatisfaction().get(Idx0);
            ArrayList<Double> list1 = ch.getSatisfaction().get(Idx1);

            ch.getSatisfaction().set(Idx0, list1);
            ch.getSatisfaction().set(Idx1, list0);
        }

        //ch.refreshValVsConstIdx();       
        ch.refreshFitness();        
    }
    
    /**
     * METHOD IS NOT TESTED. TEST IT FIRST BEFORE USE.
     * mutationInteger only mutate integers. It uses swap elements technique. 
     * so that it disrupts order more to get new allel values<br>
     * <B>Note:</B> that offspring ArrayList is updated here but rank will remain
     * same because swapping satisfaction value will produce same result. It may
     * only give different results in crossover.
     * @param offspring offspring generated after crossover.
     */
    private void mutationInteger(ArrayList<Chromosome> offspring){
        ArrayList<Integer> randDim;
        ArrayList<Integer> randVal;
        Double temp = 0.0;
        int muteBits;
        
        //System.out.println("testing... " + offspring);

        if(userInput_.domainVals == null || userInput_.domainVals.isEmpty()){ //mutation not supported
            return;
        }

        if(externalData_ == null){ //currently works only for external data
            return;
        }
        
        //Technique 1: swapping values
        //<<
//        for (int ofsp = 0; ofsp < offspring.size(); ofsp++) {            
//            if(Math.random()<1.0/offspring.get(ofsp).getValsCopy().size()){
//                //Only deal with valid values...
//                randDim = MyRandom.randperm(0, offspring.get(ofsp).getRankComponents().size()-1);                
//                
//                if(randDim.size()<2){ //swapping not possible
//                    continue;
//                }else{
//                    holdVals = (ArrayList<Double>)offspring.get(ofsp).getValsCopy().clone();
//                    holdVals.set(randDim.get(0), offspring.get(ofsp).getValsCopy(randDim.get(1)));
//                    holdVals.set(randDim.get(1), offspring.get(ofsp).getValsCopy(randDim.get(0)));                    
//                    offspring.get(ofsp).setVals(holdVals);                    
//                }
//            }
//        }
        //>>
        
        
        ArrayList<Double> vals;
        //ArrayList<Double> noGoods; 
        int expectedVal;
        
        
        
        //Technique 2: mutate a given value from available domain value;
        //<<
//        for (int ofsp = 0; ofsp < offspring.size(); ofsp++) {  
        for (Chromosome offsp : offspring) {                    
            if(Math.random()<MUTATION_RATE){ //1.0/userInput_.getTotalDecisionVars()){ //>1.0/offspring.get(ofsp).getValsCopy().size() || bStagnant){
                               
                vals = offsp.getValsCopy();
                Collections.sort(vals);
                expectedVal = 0;
//                noGoods = new ArrayList<Double>();
//
//                for (int i = 0; i < vals.size(); i++) {                        
//                    if(vals.get(i).intValue() != expectedVal){
//                        for (int j =expectedVal; j < vals.get(i).intValue(); j++) {
//                            noGoods.add(j*1.0); 
//                        } 
//                        expectedVal = vals.get(i).intValue();
//                    } 
//                    expectedVal++;
//                }
//
//                for (int i = vals.get(vals.size()-1).intValue()+1; i < userInput_.getTotalDecisionVars(); i++) {
//                    noGoods.add(i*1.0); 
//                }
                vals.clear();
                if(offsp.noGood.isEmpty()){
                    continue; //nothing to replace with
                }
                
                if(offsp.getValsCopy().size()<2){ //swapping not possible
                    continue;
                }else{
                    if(userInput_.domainVals == null){
                        continue;
                    }else if(userInput_.domainVals.isEmpty()){
                        continue;
                    }
                    
                    muteBits = 1;
                    if(bStagnant){
                        muteBits = Math.max(1,(int)(offsp.getValsCopy().size()*0.2));
                    }
                    //randVal = MyRandom.randperm(0,offsp.noGood.size()-1);
                    
                    for (int j = 0; j < muteBits && j<offsp.noGood.size(); j++) {
                        
                        randVal = MyRandom.randperm(0,offsp.noGood.size()-1);
                        
                        if(bStagnant){ //Important... must refresh in every iteration....
                            muteBits = Math.max(1,(int)(offsp.getValsCopy().size()*0.2));
                        }

                        //Only deal with valid values...
                        randDim = MyRandom.randperm(0, offsp.getValsCopy().size()-1);
                        
                        if(randDim.get(0) >= offsp.getValsCopy().size()){
                            System.out.println("ee kaisey sake...");
                            System.out.println(vals);
                        }
                        try{
//                            randVal = MyRandom.randperm(0,userInput_.domainVals.get(randDim.get(0)).size()-1);
//                            randVal = MyRandom.randperm(0,noGoods.size()-1);
                            if(!externalData_.isHighlyConstrained(offsp.getVals(randDim.get(0)).intValue())) //in optimization mode noGood is empty so automatically this won't be executed.
                                offsp.replaceVal(randDim.get(0),offsp.noGood.get(randVal.get(0)));

//                            int prevVal =  vals.get(randDim.get(0)).intValue();
//                            for (int k = 0; k < userInput_.domainVals.get(prevVal).size(); k++) {
//                                temp = userInput_.domainVals.get(prevVal).get(randVal.get(k));
//                                if(vals.get(randDim.get(0)) != temp){                                
//                                    offspring.get(ofsp).replaceVal(randDim.get(0), temp);//Be warned! may create duplicate values..
//                                    break;
//                                }   
//                            }
                        
                        }catch(Exception e){
                            e.printStackTrace();
                            System.out.println("arey??");
                        }
                        
                    }
                             
                }
            }
        }
        //>>
    }

    private void sortAndReplace(int gen) throws Exception, SolutionFoundException{
        if (userInput_.dataType.contains("Integer")){
//            noViolationSortAndReplace(gen); //duplicateSatisfactionSortAndReplace();
            ;
        }else if (userInput_.dataType.contains("Double")){
            noViolationSortAndReplaceDouble(gen);
        }
        else{    
            throw new Exception("Incorrect use of data types");
        }
    }
 
    
    private void noViolationSortAndReplaceDouble (int gen) throws Exception{
        
        
        sols = new ArrayList<Chromosome>();
        nonSols = new ArrayList<Chromosome>(); 

//        final int funcionalConstraints = userInput_.maxDynamicTime+1; //userInput_.totalConstraints - userInput_.getTotalDecisionVars()+1;
        ArrayList<ArrayList<Chromosome>> grouping = new ArrayList<ArrayList<Chromosome>>();
        int front2[] = new int[2];

        if(bOptimizationMode)
            curBest_ = MyMath.roundN(getBestSoFar().getFitnessVal(),3);
        else
            curBest_ = getBestSoFar().getRankComponents().size();
                    
        if(prevBest_ == curBest_){
            stillSameBestCount++;
        }else{
            stillSameBestCount = 0;
        }          
        
        if(stagnantVisit >= 10 || stillSameBestCount == 0){
            stillSameBestCount = 0;
            bStagnant = false;
            stagnantVisit = 0;
        }        
        
        
        Chromosome ch; 
        for (int i = 0; i < chromosomes_.size(); i++) {
            ch = chromosomes_.get(i);
//            ch.age++;
            
            if(Chromosome.tmpSortBy != userInput_.solutionBy){
                System.out.println("bad robot...");
            }
            
            if(ch.isSolution()){
                sols.add(ch);
                chromosomes_.remove(i);
                i--;
            }else{
                nonSols.add(ch);
                chromosomes_.remove(i);
                i--;
            }
        }

        chromosomes_.clear();
               
//        final int maxSolPop = (int)(userInput_.population*0.25);
//        final int maxNonSolPop = userInput_.population-maxSolPop;
//        
        int solPop = Math.min(maxSolPop,sols.size());
        int nonSolPop = Math.min(maxNonSolPop,nonSols.size()); //userInput_.population - solPop;
        
        if(solPop < maxSolPop){
            nonSolPop = userInput_.population - solPop;
        }else if(nonSolPop <maxNonSolPop){
            solPop = userInput_.population - nonSolPop;
        }else if (solPop + nonSolPop != userInput_.population){         
            if(sols.size() > solPop){
                solPop++;
            }else if(nonSols.size() > nonSolPop){
                nonSolPop++;
            }else{
                System.err.println("population size error mate on noViolationSortAndReplace.");
                Application.getInstance().exit();   
            }   
        }
                    
        if(!sols.isEmpty()){
            setbOptimizationMode(true);           
//            this.maxCSPval = sols.get(sols.size()-1).getFitnessVal(1);  ??? I though 1 is for lower-level????                                      
            //throw new SolutionFoundException("found reee...");
        }

        if(sols.size()>1){
//            Chromosome.tmpSortBy = Chromosome.BY_FITNESS;
//            final double maxVal = Collections.max(sols).getFitnessVal();
//            final double minVal = Collections.min(sols).getFitnessVal();
//            final int range = (int)Math.max(maxVal-minVal, 1);
//            final int slots = (int)Math.min(range,0.1*userInput_.population);
                final int slots = (int)(0.1*userInput_.population);
            sols = cleanChromes(sols,sols.size()-solPop);
            sols = categorizeChromesList(slots, sols, solPop, SORT_FITNESS_THEN_NOVELTY, rhoCOP, null, false);            
        }
        try{ 
            if(!nonSols.isEmpty()){   
                nonSols = cleanChromes(nonSols, nonSols.size()-nonSolPop);
                final int slots = (int)(0.1*userInput_.population);
                nonSols = categorizeChromesList(slots,nonSols, nonSolPop, SORT_HARDCONSVIOS_THEN_FITNESS, rhoCOP, null, false);
                                       
                if(!bOptimizationMode){
                    if(nonSols.get(0).getFitnessVal()<getBestSoFar().getFitnessVal())
                        CspProcess.upgradeBestSoFar(nonSols.get(0)); //It is not necessary that get(0) it the best accroding to fitness val
                                                                     // but it donsen't matter in case of CSP...
                }

                if(!bOptimizationMode && stillSameBestCount >= SAME_BEST_GENERATIONS){// && externalData_.getCurPref()>= externalData_.maxPref())){
                    bStagnant = true;                 
                    stagnantVisit++;           
                    
                    if(stagnantVisit == 1){
                        System.out.println("*** temp -- removed...");
                        ArrayList<Double> nearestNeighbor = null;
                        tabuDist = -1.0;
                                            
                        if(grouping.size()>=2){
                            if(grouping.get(front2[0]).size()>0 && grouping.get(front2[1]).size()>0){ //obviously... huun..
                                tabuDist = Math.abs(MyMath.norm(grouping.get(front2[0]).get(0).getValsCopy(), 
                                        grouping.get(front2[1]).get(0).getValsCopy(),MyMath.DIST_EUCLEADIAN));
                            }
                        }    
                        
                        
                        if(tabuDist>0){
                            System.out.println("Tabu contraint added");
                            addDynamicConstraint(grouping.get(front2[0]).get(0).getValsCopy(), tabuDist);                        
                        }
                        for (Chromosome c : nonSols) {
                            c.refreshFitness();
                        }
                    }
                }
            }
        } catch(Exception e){
            throw e;
        }    
        
  

        chromosomes_.clear(); //just to be safe;
        chromosomes_.addAll(sols);
        chromosomes_.addAll(nonSols);
            
        if(chromosomes_.size() != userInput_.population){
            System.err.println("population size error on noViolationSortAndReplace.");
            Application.getInstance().exit();
        }

        Chromosome.tmpSortBy = userInput_.solutionBy;
         
        prevBest_ = curBest_;
        if(bStagnant){
            randomDeath(gen, 1, true); //spacre top one            
        }
        else            
            randomDeath(gen, (int)(userInput_.population*0.03),false);//,true     
        
        
        if((gen%getGensEachConstraints() == 0 /*&& bestSoFar.isSolution()*/)){
//            System.out.println("****best before change: "+bestSoFar);
                   
            
            if(getDynamicTime() < userInput_.maxDynamicTime){//userInput_.totalConstraints-userInput_.getTotalDecisionVars()){ // related with allDynamicConstraintsSolved();
                dynamicTimeIncrement();                          
            
                for (Chromosome c : chromosomes_) {
                    c.refreshFitness();
                }

                suspended_.clear();
                setbOptimizationMode(false);
                stillSameBestCount = 0;
                bStagnant = false;
                stagnantVisit = 0;

////                Collections.sort(chromosomes_);
                chromosomes_ = categorizeChromesList(chromosomes_.size()
                        , chromosomes_, chromosomes_.size(), SORT_HARDCONSVIOS_THEN_FITNESS, rhoCOP, null, false);
                if(chromosomes_.get(0).isSolution())
                    CspProcess.upgradeBestSoFar(chromosomes_.get(0));  
            }
        }
        
        
        System.out.println("debug last best: " + (gGen - gGenLastBestOn));
        System.out.println("prevbest: " + prevBests.curSize());
        
        if(bOptimizationMode && allDynamicConstraintsIncluded() && gGen - gGenLastBestOn>gGenTrackStruggle
                && prevBests.curSize() > 0 && getBestSoFar().isSolution()){ /////////////////////////////////////??????????????????????????????????????
             System.out.println("Opt Tabu contraint added");
             tabuDist = Math.abs(MyMath.norm(getBestSoFar().getValsCopy(), 
                                        prevBests.dequeue().getValsCopy(),MyMath.DIST_EUCLEADIAN));
             
//             final int knownOptSolDP = 4;
//            for (int i = 0; i < sols.size(); i++) {
//              if(MyMath.roundN(chromosomes_.get(i).getFitnessVal(0), knownOptSolDP) == MyMath.roundN(bestSoFar.getFitnessVal(0),knownOptSolDP)){
//                  chromosomes_.remove(i);
//                  i--;
//                  chromosomes_.add(initializeChromosomeRandomly());
//              }else{
//                  break;
//              }  
//            }  
                         
             addDynamicConstraint(getBestSoFar().getValsCopy(), tabuDist);                         
        }                
        
        
        if(bOptimizationMode && allDynamicConstraintsIncluded()){
            bTabuMode = true; //once true then remain true forever ever..ever..
        }
    }
    
    
        
    /**
     * @param list - (Pass by value). input list will be destroyed. Get the returned list
     * @param listPop
     * @param minVal
     * @param maxVal
     * @param categorizeBy
     * @param a 1 is preferred can use lesser values as well
     * @param ro current test results shows no difference in picking any value
     * @param grpIdx (Pass by Ref) - indices in final list indicating starting indices of groups/slots
     */
     private ArrayList<Chromosome> categorizeChromesList(final int slots, ArrayList<Chromosome> list, final int listPop, 
        final int categorizeBy, final double ro, ArrayList<Integer> grpIdx, final boolean debugPrint){
         
        //System.out.println("<<catgorizing...>>: " + list.size());
        
        int slotSize;
        int FirstSlotSize;
        int empty;
        int incompleteSlots;
        int slotAddition;
        int vios;
        ArrayList<ArrayList<Chromosome>> grouping = new ArrayList<ArrayList<Chromosome>>();
        
        int front2[] = new int[2];

        if(list.isEmpty()){
            return list;
        }     
        
        if(categorizeBy == SORT_HARDCONSVIOS_THEN_FITNESS){
            list = sortTwice(Chromosome.BY_VIOLATIONS, Chromosome.BY_FITNESS, list, list.size()); //Math.min(list.size(),listPop*2)); 
            Chromosome.tmpSortBy = Chromosome.BY_FITNESS;
        }else if(categorizeBy == SORT_HARDCONSVIOS_THEN_RHO){
            list = sortTwice(Chromosome.BY_VIOLATIONS, Chromosome.BY_FITNESS, list, list.size()); //Math.min(list.size(),listPop*2)); 
            Chromosome.tmpSortBy = Chromosome.BY_RO;
        }else if(categorizeBy == SORT_SATISFACTION){
            Chromosome.tmpSortBy = Chromosome.BY_SATISFACTIONS;
            Collections.sort(list);
//            list = new ArrayList<Chromosome>(list.subList(0, Math.min(list.size(),listPop*2)));
        }else if(categorizeBy == SORT_FITNESS){ //depends on the current Chromosome.tmpSortBy specified by the caller             
            Chromosome.tmpSortBy = Chromosome.BY_FITNESS;
            Collections.sort(list);
//            list = new ArrayList<Chromosome>(list.subList(0, Math.min(list.size(),listPop*2)));
        }else if (categorizeBy == SORT_FITNESS_THEN_NOVELTY){
            list = sortTwice(Chromosome.BY_FITNESS, Chromosome.BY_RO, list, list.size()); //Math.min(list.size(),listPop*2)); 
//            list = sortFitnessThenNovelty(list, Math.min(list.size(),listPop*2));
            Chromosome.tmpSortBy = Chromosome.BY_FITNESS;
        }
        
         
        //final int range = (int)Math.max(maxVal-minVal, 1);
        
        //final int slots = (int)Math.min(range,10);//0.2*userInput_.totalConstraints); //BAD* try to make relationship with population man...
        //final int slots = Math.max((int)(0.1*maxVal), 5); //        
        slotSize = listPop/(slots); //last one for infeasibles as well
        FirstSlotSize = slotSize + (listPop - slotSize*(slots));
        empty = 0;
        incompleteSlots = 0;

        for (int i = 0; i < slots; i++) {//funcionalConstraints
            grouping.add(new ArrayList<Chromosome>());
        }      
        
        vios = -1;                
//                    for (Integer i : MyRandom.randperm(0, nonSols.size()-1)) {
        for(int i = 0; i<list.size(); i++){ 
            vios = Math.min(slots-1,list.get(i).getRankComponents().size()); //1 to total constraints
            grouping.get(vios).add(list.get(i));
        }
        
        Chromosome.tmpSortBy = userInput_.solutionBy;
        for (ArrayList<Chromosome> grp : grouping) {
            Collections.sort(grp);
        }

        int fr = 0;                                        
        for (int i = 0; i < grouping.size(); i++) {
            ArrayList<Chromosome> g = grouping.get(i); 

            if(g.size()>0 && fr < 2){
                front2[fr++] = i;
            }                        
        }

        empty = 0;
        incompleteSlots = 0;
        for (int i = 0; i < grouping.size(); i++) {
            if(grouping.get(i).size()<slotSize){
                empty += slotSize-grouping.get(i).size();
                incompleteSlots++;
            }
        }

        slotAddition = empty/(slots-incompleteSlots); //empty slot space has to be distributed to filled/partially filled slots 
        slotSize += slotAddition;
        FirstSlotSize += slotAddition;
        FirstSlotSize += empty - (slots-incompleteSlots)*slotAddition;

        ArrayList<Chromosome> additionals = new ArrayList<Chromosome>();
//        ArrayList<ArrayList<Chromosome>> additionals = new ArrayList<ArrayList<Chromosome>>();
//         for (int i = 0; i < slots; i++) {
//             additionals.add(new ArrayList<Chromosome>());
//         }

        ArrayList<Integer> tmpIdx;
        ArrayList<Chromosome> chTmp;
        int count;
             
        boolean bFirstSlotAdded = false;
        for (int i = 0; i < grouping.size(); i++) {
            if(!bFirstSlotAdded && grouping.get(i).size() >= FirstSlotSize){  
                
                //<<.... 
//                tmpIdx = MyMath.linearFnSelection(grouping.get(i), FirstSlotSize, debugPrint);
                tmpIdx = MyMath.negExpFnSelection(grouping.get(i).size(), FirstSlotSize, ro, debugPrint);
                chTmp = new ArrayList<Chromosome>();

                for (int j = 0; j < tmpIdx.size(); j++) {
                    chTmp.add(grouping.get(i).get(tmpIdx.get(j)));                    
                }                                
                
                if(grouping.get(i).size() > FirstSlotSize ){
                    count = 0;
                    for (int j = 0; j < grouping.get(i).size(); j++) {
                        if(count<tmpIdx.size()){
                            if(j==tmpIdx.get(count).intValue()){
                                count++;
                                continue;
                            }
                        }
//                        additionals.get(i).add(grouping.get(i).get(j));   
                        additionals.add(grouping.get(i).get(j));
                    }    
                }
                grouping.set(i, chTmp);
                //>>...                

                bFirstSlotAdded = true;
                continue;
            }
            
            //<<
                tmpIdx = new ArrayList<Integer>();
                chTmp = new ArrayList<Chromosome>();
                int sz = Math.min(slotSize,grouping.get(i).size());                                
                
//                tmpIdx = MyMath.linearFnSelection(grouping.get(i), sz, debugPrint);
                tmpIdx = MyMath.negExpFnSelection(grouping.get(i).size(), sz, ro, debugPrint);
                
                for (int j = 0; j < tmpIdx.size(); j++) {
                    chTmp.add(grouping.get(i).get(tmpIdx.get(j)));                    
                }
                                
                if(grouping.get(i).size() > slotSize ){
                    count = 0;
                    for (int j = 0; j < grouping.get(i).size(); j++) {
                        if(count<tmpIdx.size()){
                            if(j==tmpIdx.get(count).intValue()){
                                count++;
                                continue;
                            }
                        }
//                        additionals.get(i).add(grouping.get(i).get(j));    
                        additionals.add(grouping.get(i).get(j));
                    }    
                }
                grouping.set(i, chTmp);
            //>>
            
        }

        list.clear(); 
        if(grpIdx == null){
            grpIdx = new ArrayList<Integer>(); //NOTE pass-by-ref distroyed here. Code for grpIdx below are now USELESS                    
        }
        
        grpIdx.add(0);//first index obviously.
        for (int i = 0; i < grouping.size(); i++) {
            ArrayList<Chromosome> g = grouping.get(i); 
            list.addAll(g);
            grpIdx.add(list.size());//next index
        }
        grpIdx.remove(grpIdx.size()-1);//last one is invalid it is size+1. there is no next after size().
        
//        int reqAdditionals = listPop-list.size();
//        for (int i = 0; i < reqAdditionals; i++) {
//            if(additionals.get(i%slots).isEmpty()){
//                reqAdditionals++; // because this i is ignored
//                continue;
//            }
//            list.add(additionals.get(i%slots).remove(0));
//        }
        try{
        list.addAll(additionals.subList(0, listPop-list.size())); 
        }catch(IndexOutOfBoundsException ee){
            ee = ee;
        }
               

        Chromosome.tmpSortBy = userInput_.solutionBy;  
        
        return list;
    }
    
   
   
    
    private void addDynamicConstraint(ArrayList<Double> center, double radius){ 
        negFeasibleRange = 0;
        //dynamicConstraintNo = 0;
        userInput_.totalConstraints++;
        //MAX_FUNCTIONAL_CONSTRAINTS = userInput_.totalConstraints - userInput_.getTotalDecisionVars();
        
        
        CCSPfns.addTabuConstraint(center, radius); 
        
        try {
            for (Chromosome c : chromosomes_) {
                c.restructure(1, true);
            }
            if(!bTabuMode)
                getBestSoFar().restructure(1, true);
            prevBests.clearAll();
            
        } catch (SolutionFoundException ex) {
            ex.printStackTrace();
        }
    }
    

    
    /**
     * NOTE: This function <B>DOES NOT</B> sort the chromosomes inside a ranked group.
     * If it has <I>n</I> ranks/groups, it only tries to give best ranked chromosomes,
     * then the leftovers are <B>ONLY</B> sorted according to fitness.
     * You must sort each ranked groups separately afterwards.
     * @param in
     * @param size
     * @return 
     */
    private ArrayList<Chromosome> sortTwice(int firstSortType, int SecondSortType, final ArrayList<Chromosome> in, final int size){         
        Chromosome.tmpSortBy = firstSortType; 
        Collections.sort(in);
        
        ArrayList<Chromosome> out = null;
        
         
        int safePointer = -1;
        Chromosome chrome;
        ArrayList<Chromosome> temp = new ArrayList<Chromosome>();
        
        try{
            if(in.size()<=1 || in.size() <= size){
                out = in;
                throw new ExecutionException(null);
            }
            final double maxAcceptedRank = in.get(size-1).getRank(); // getRankComponents().size(); //violations

            
            //lets assume sort twice is vio then fitenss
            int grpIdx = 0;
            ArrayList<Integer> slots = new ArrayList<Integer>();
            slots.add(0);
            int lastGrpIdx = 0;
            
            for (int i = 0; i < in.size(); i++) {
                if(MyMath.roundN(in.get(i).getRank(),knownOptSolDP) == MyMath.roundN(in.get(lastGrpIdx).getRank(),knownOptSolDP)){
                    if(MyMath.roundN(in.get(i).getRank(),knownOptSolDP) > MyMath.roundN(maxAcceptedRank,knownOptSolDP)){
                        break;
                    }                    
                    slots.set(grpIdx, slots.get(grpIdx)+1);
                }else{
                    i--;
                    grpIdx++;
                    lastGrpIdx += slots.get(slots.size()-1);
                    slots.add(0);
                }
            }
            
            //last grpIdx can have elements >= 0
            while (slots.get(grpIdx) == 0){
                slots.remove(grpIdx);
                grpIdx--; //last grpIdx.
            }
            
            ArrayList<ArrayList<Chromosome>> sortedIn = new ArrayList<ArrayList<Chromosome>>();
            for (int i = 0; i <= grpIdx; i++) {
                sortedIn.add(new ArrayList<Chromosome>()); //last one needs to be trimmed.
            }
            int prev = 0;
            int cur = 0;
            int i = 0;
            
            
            for (Integer s : slots) {                
                cur = s+prev;                
                sortedIn.get(i++).addAll(new ArrayList<Chromosome>(in.subList(prev, cur)));
                prev = cur;
            }
            
            Chromosome.tmpSortBy = SecondSortType;
            out = new ArrayList<Chromosome>();
            int total=0;
            for (int j = 0; j < grpIdx; j++) {
                Collections.sort(sortedIn.get(j));
                out.addAll(sortedIn.get(j));
                total+=sortedIn.get(j).size();
            }
            Collections.sort(sortedIn.get(grpIdx));
            out.addAll(sortedIn.get(grpIdx).subList(0, size-total));
        
        }catch(ExecutionException ee){
            out = out;
        }
        catch(Exception e){
            e.printStackTrace();
            out = out;
        }

        Chromosome.tmpSortBy = userInput_.solutionBy;
        return out;
    }
   
    /**
     * yoni..
     * @param gen
     * @param spareSize
     * @param bImportSuspended
     * @throws SolutionFoundException 
     */
    private void randomDeath(int gen, int spareSize, boolean bImportSuspended) throws SolutionFoundException{
        int d;
        ArrayList<Chromosome> newRandPop = new ArrayList<Chromosome>();
        d = (int)Math.round(this.REPLACE_PERCENT*userInput_.population);
        
        try {
            initializeChromosomes(newRandPop, d, gen);             
        } catch (Exception e) {
            e.printStackTrace();
            Application.getInstance().exit();
        }


        //hey??????????? its not a random............. bluffffffffff
//        for (int ofsp = 0; ofsp < startDrawing; ofsp++) {  //spare the top startDrawing chromes          
//            chromosomes_.set(userInput_.population-1-ofsp, newRandPop.get(ofsp));
//        }
        int totChromesRemoved = 0;
        Chromosome ch;
//       for(int i: MyRandom.randperm(spareSize, chromosomes_.size()-1).subList(0, startDrawing)){
//        for(int i = chromosomes_.size()-1; i> Math.max(chromosomes_.size()-spareSize,chromosomes_.size()-startDrawing); i--){   
        for(int i = chromosomes_.size()-1; i> spareSize; i--){ 
            if(suspended_.size()<userInput_.population && chromosomes_.get(i).isSolution() && allDynamicConstraintsIncluded())
                suspended_.add(chromosomes_.get(i)); //getting reference? it is ok as it will be deleted below.
            
//            if(gen<5 || !bImportSuspended || suspended_.size()<5){//0.01*userInput_.generation){
////            if(gen<5 || suspended_.size()<5){
////                newRandPop.get(totChromesRemoved).forceFindSolution();
////                chromosomes_.set(i,newRandPop.get(totChromesRemoved));
////                totChromesRemoved++; //chromosome Changed                                               
////            }
////            else{    
////                ch = suspended_.remove();
////                //ch.refreshFitness(maxCSPval);
////                chromosomes_.set(i,ch);   
//////                suspended_.add((Chromosome)ch.clone());
////                totChromesRemoved++;
////            }
            
            
            if(suspended_.size()>2*d){
                ch = suspended_.remove();;
                chromosomes_.set(i,ch); 
                totChromesRemoved++;
            }
            if(totChromesRemoved >= d){
                break;
            }
        }        
    }
} //End of class definition

package tagger;

import java.io.File;

import tagger.algo.GeniaData;
import tagger.algo.HmmModel;
import tagger.algo.ViterbiHmm;

public class Main {

    public static void main(String[] args) {
    	GeniaData a = new GeniaData();
    	long startTime = System.currentTimeMillis();
    	/*File folder = new File("D:\\Data\\Searching\\study\\Trainset-POS-1\\Trainset-POS-1");*/
        /*a.sumAllFilesForFolder(folder);*/
    	a.driveSumFile();
    	
		HmmModel hmmModel = new HmmModel();
		hmmModel.run();
		
    	ViterbiHmm viterbiHmm = new ViterbiHmm();
    	viterbiHmm.run();
    	long endTime   = System.currentTimeMillis();
    	System.out.println(endTime - startTime);
    }
}

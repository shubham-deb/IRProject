import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ScoreEvaluation {
	
	static Map <Integer,Set<String>>relMap=new HashMap<Integer,Set<String>>();
	static Map <Integer,ArrayList<String>>scoreMap=new HashMap<Integer,ArrayList<String>>();
	
	public static void main(String[] args) throws IOException {
		String ScoreName="Phase2.txt";
		File scoreFile = new File(ScoreName);
		File relevanceFile = new File("cacm.rel");
		
		try (BufferedReader br = new BufferedReader(new FileReader(relevanceFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] pline=line.split(" ");
				if(relMap.containsKey(Integer.parseInt(pline[0]))) {
					relMap.get(Integer.parseInt(pline[0])).add(pline[2]);
				} else {
					Set<String> rSet=new LinkedHashSet<String>();
					rSet.add(pline[2]);
					relMap.put(Integer.parseInt(pline[0]), rSet);
				}
			}
		}
/*		
		for (int k : relMap.keySet()){
			for (String val : relMap.get(k)){
				System.out.println("query" + k + " doc: " +val);
			}
		}
*/	
		
		try (BufferedReader br = new BufferedReader(new FileReader(scoreFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] pline=line.trim().split("\\s+");
				//for(String s:pline) { System.out.println(s);}
				if (pline.length>2){
					String q_ID=pline[0];
					
					//for(String s:docID) { System.out.println(s);}
					
					if(Pattern.matches("[0-9]+", q_ID)) {
						String docID=pline[2].split("-|\\.")[1];
						if(scoreMap.containsKey(Integer.parseInt(q_ID))) {
							scoreMap.get(Integer.parseInt(q_ID)).add(docID);
						} else {
							ArrayList<String> sList=new ArrayList<String>();
							sList.add(docID);
							scoreMap.put(Integer.parseInt(q_ID), sList);
						}
					}
				}
			}
		}
/*		
		for (int k : scoreMap.keySet()){
			int count=1;
			for (String val : scoreMap.get(k)){
				System.out.println(count+ " " +k+ " " +val);
				count++;
			}
		}
*/
		String[] RM=ScoreName.split("\\.");
//		for (String s:RM){
//			System.out.println(s);
//		}
		calculatePR(RM[0]);
		
	}
	
	static Map<Integer,Double> avgPrecision = new HashMap<Integer, Double>();
	static Map<Integer,Double> MRRmap = new HashMap<Integer, Double>(); 
	
	static void calculatePR(String ModelName) throws IOException{
		Map<Integer,Double> Precision = new HashMap<Integer, Double>(); 
		Map<Integer,Double> Recall = new HashMap<Integer, Double>(); 
		CreateDir(new File (ModelName));
		PrintWriter PRWriter = new PrintWriter(new BufferedWriter(new FileWriter(ModelName+"/PR.csv")));
		PRWriter.write("query,rank,document,precision,recall\n");
		
		PrintWriter PKWriter = new PrintWriter(new BufferedWriter(new FileWriter(ModelName+"/P@K.csv")));
		PKWriter.write("query,P@5,P@20\n");
		for (int query : scoreMap.keySet()){
			boolean isRR=false;
			Set<String> Relevantdocs=relMap.get(query);
			if (Relevantdocs == null) continue; 
			PRWriter.write("\n");
			Double avgP=0.0d;
			int Relcount=0;
			int rank=0;
			
/*
			System.out.println("=======Relset===========");
			if (Relevantdocs != null) {
			for (String relval : Relevantdocs){
				System.out.println(query+ " " +relval);
			}
			}
			System.out.println("=======Reldocs===========");
*/
			
			//CreateDir(new File (ModelName+"/"+query));
			
			ArrayList<String> RankedDocs=scoreMap.get(query);
			for (String val : RankedDocs){
				 
				++rank;
				//double P = Relcount/rank;
				if (Relevantdocs != null) {
					if (Relevantdocs.contains("CACM-"+val)){
						++Relcount;
						//System.out.println( rank+ " " +query + " " + "CACM-"+val );
						avgP +=(double)Relcount/(double)rank;
						if(isRR==false) {
							MRRmap.put(query, 1.0d/(double)rank);
						}
					}
				}
				double P = (double)Relcount/(double)rank;
				Precision.put(rank, P);
				if (Relevantdocs != null) 
					Recall.put(rank, (double) Relcount/(double)Relevantdocs.size());
				
				PRWriter.write(query+","+rank+","+"CACM-"+val+","+P+","+Recall.get(rank)+"\n");

			}
			
			avgP=avgP/(double)Relevantdocs.size();
			//System.out.println("query: "+query+" avgP: "+ avgP);
			avgPrecision.put(query,avgP);
			
			
			PKWriter.write(query+"," +Precision.get(5)+"," + Precision.get(20)+"\n");
			//PKWriter.write("P@20"+"," + Precision.get(20)+"\n");
			

		}
		PKWriter.close();
		PRWriter.close();
		PrintWriter MAPWriter = new PrintWriter(new BufferedWriter(new FileWriter(ModelName+"/MAP.txt")));
		Double MeanAP=0.0d;
		for(int qury:avgPrecision.keySet()){
			MeanAP+=avgPrecision.get(qury);
		}
		MeanAP=MeanAP/avgPrecision.keySet().size();
		MAPWriter.write("MAP: " + MeanAP);
		MAPWriter.close();
		
		PrintWriter MRRWriter = new PrintWriter(new BufferedWriter(new FileWriter(ModelName+"/MRR.txt")));
		Double MRR=0.0d;
		for(int qury:MRRmap.keySet()){
			MRR+=MRRmap.get(qury);
		}
		MRR=MRR/MRRmap.keySet().size();
		MRRWriter.write("MRR: " + MRR);
		MRRWriter.close();
	}
	
	public static void CreateDir(File directory){
		if (!directory.exists()) {
		    System.out.println("creating directory: " + directory.toString());
		    boolean result = false;

		    try{
		    	directory.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		    if(result) {    
		        System.out.println("DIR created");  
		    }
		}
	}
	

}

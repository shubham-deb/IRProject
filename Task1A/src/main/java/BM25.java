import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class BM25 {

	public double getAverageDocLength() throws IOException
	{
		double totalterms = 0,totaldocs = 0;
		File folder=new File("corpus");
		String line = null;
		FileInputStream fis = new FileInputStream("index.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		// get the total documents in the corpus
		while((line = br.readLine()) != null){
			totaldocs++;
		}
		//System.out.println(totaldocs);
		
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			Document doc = Jsoup.parse(listOfFiles[i], "UTF-8");
			String[] tokenList=doc.text().split("\\s+");
			for(String s: tokenList){
				totalterms++;}
		}
		
		br.close();
		return totalterms/totaldocs;
		//System.out.println("total terms are "+totalterms);
	}
	
	public int getDocumentLength(Document doc)
	{
		int doclength = 0;
		String[] tokenList=doc.text().split("\\s+");
		for(String s: tokenList){
			doclength++;}
		return doclength;
	}
	
	// br for UnigramDocFrequency file(for reading docfrequency of the queryterm)
	public int getDocumentFrequency(String query) throws IOException
	{
		// open the file for reading info about tokens in each document
		FileInputStream fis = new FileInputStream("UnigramDocFrequency.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		int docfrequency = 0;
		String line = null;
		// count the docfrequency of the term in the file
		while((line = br.readLine()) != null)
		{
			String[] splitterm = line.split(" ");
			if(splitterm[0].equals(query))
			{
				docfrequency = Integer.parseInt(splitterm[2]);
				//System.out.println("doc frequency is "+docfrequency);
				break;
			}
		}
		br.close();
		return docfrequency;
	}
	
	public int getTermFrequency(String queryterm, Document doc)
	{
		int termfrequency = 0;
		String[] tokenList=doc.text().split("\\s+");
		for(String s: tokenList){
			if(s.equals(queryterm))
				termfrequency++;}
		return termfrequency;
	}
	
	public ArrayList<String> getRelevantDocs(int querynumber) throws IOException
	{
		 ArrayList<String> relevantdocs = new ArrayList<String>();
		FileInputStream fis = new FileInputStream("cacm-rel.rel");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line=null;
		// get the total documents in the corpus
		while((line = br.readLine()) != null){
			String[] tokenList=line.split("\\s+");
			int currentquerynumber = Integer.parseInt(tokenList[0]);
			if (currentquerynumber==querynumber)
				relevantdocs.add(tokenList[2]);
		}
		br.close();
		return relevantdocs;
	}
	
	public Boolean containsTerm(String queryterm,String doc) throws IOException
	{
		// HANDLING CACM-756 AS CACM-0756 IS GIVEN IN THE CORPUS
		try
		{
		FileInputStream fis = new FileInputStream("corpus\\"+doc+".html"+".txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line=null;
		// get the total occurences of the term in the relevant docs
		while((line = br.readLine()) != null){
			String[] tokenList=line.split("\\s+");
			for (String term: tokenList)
				if (queryterm.equals(term))
					return true;
		}
		br.close();
		return false;
		}
		catch(Exception e)
		{
			StringBuilder s  = new StringBuilder("corpus\\"+doc+".html"+".txt");
			s.insert(s.indexOf("-")+1, "0");
			FileInputStream fis = new FileInputStream(s.toString());
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line=null;
			// get the total occurences of the term in the relevant docs
			while((line = br.readLine()) != null){
				String[] tokenList=line.split("\\s+");
				for (String term: tokenList)
					if (queryterm.equals(term))
						return true;
			}
			br.close();
			return false;
		}
	}
	
	public int getNumberofRelevantDocs(String queryterm, ArrayList<String> reldocs) throws IOException
	{
		int numberofreldocs = 0;
		for(int docnumber = 0; docnumber<reldocs.size(); docnumber++)
		{
			if (containsTerm(queryterm,reldocs.get(docnumber)))
			{
				numberofreldocs++;
			}
		}
		return numberofreldocs;
	}
	
	public void CalculateBM25() throws IOException
	{

		double averagedoclength=0;
		LinkedHashMap<String, Integer> querytermfreq = new LinkedHashMap<String, Integer>();
		LinkedHashMap<String, Double> BM25QueryScore = new LinkedHashMap<String, Double>();
		PrintWriter writer2 = new PrintWriter(new BufferedWriter(new FileWriter("BM25.txt")));
		
		
		BM25 object = new BM25();
		Indexer indexer = new Indexer();
		GetQueries getqueryobj = new GetQueries(); 
		
		// get average doc length(avgdl)
		averagedoclength = object.getAverageDocLength();
		
		int k2=100;
		double k1 =1.2, b=0.75;
		// *******************BM25 ALGORITHM************************************
		int count = 0;
		File folder=new File("corpus");
		File[] listOfFiles = folder.listFiles();
		
		// parse all the queries first and then take query by query
		for(int querynum = 0; querynum<getqueryobj.getTotalQueries();querynum++)
		{
			
			count = 0;
			String query = getqueryobj.getQuery(querynum+1);
			
			//System.out.println("Query is "+query);
			
			//tokenize each term in the query
			String[] tokenizedquery = indexer.processToken(query);
			
			//get the frequency of each query term(qf(i))
			for(String queryterm: tokenizedquery)
			{
				//System.out.println(queryterm);
				int querytermfrequency = 0;
				for(String nextqueryterm: tokenizedquery)
					if(queryterm.equals(nextqueryterm))
						querytermfrequency++;
				querytermfreq.put(queryterm, querytermfrequency);
			}
			
		for(int docnumber = 0;docnumber<listOfFiles.length;docnumber++)
		{ 
			
			if (count==5)
				break;
			count++;
			
			Document doc = Jsoup.parse(listOfFiles[docnumber], "UTF-8");
			double finalScore = 0;
			
			double avgdoclength = object.getAverageDocLength();
			
			int doclength = object.getDocumentLength(doc);
			
			ArrayList<String> reldocs = object.getRelevantDocs(docnumber+1);

/*			for(int i=0; i<reldocs.size(); i++)
			{
				System.out.println("reldocs are "+reldocs.get(i));
			}*/
			
			// Calculating K
			double K = (0.75 * (doclength/avgdoclength) + 0.25) * 1.2;
				
			for(String queryterm : tokenizedquery)
				{
				
				// one half of the multiplied constants in BM25
				// This is the left half of the constants
				double normalizedTF = (2.2 * object.getTermFrequency(queryterm, doc))
									   /
									  (K + object.getTermFrequency(queryterm, doc));
				
				//System.out.println("Value of normalizedTF : "+normalizedTF);
				
				// This is the right half of the constants
				double normalizedQueryFreq = (101 * querytermfreq.get(queryterm))
											  /
											 (100 + querytermfreq.get(queryterm));
				
				//System.out.println("Value of normalizedQueryFreq : "+normalizedQueryFreq);
				
				double multipliedConstantFactor = normalizedTF * normalizedQueryFreq;
				
				//System.out.println("Value of K : "+K);
				//System.out.println("Value of multipliedConstantFactor : "+multipliedConstantFactor);
				
				// +1 bcz I am doing it sequentially from each query and assuming first query is Q1
				// 0+1 = 1st doc's relevance docs fetched
				
				// value of R
				int R = reldocs.size();
				//System.out.println("reldocs size is  "+R);
				
				// value of ri
				int ri = object.getNumberofRelevantDocs(queryterm, reldocs);
				
				// value of ni
				int ni = object.getDocumentFrequency(queryterm);
				
				// total docs in the corpus
				int N = listOfFiles.length;
				
				double numerator = ((ri+0.5)
									 /
									(R-ri+0.5));
				
				double denominator = ((ni - ri + 0.5)
						               /
						              (N - ni - R - + ri + 0.5));
				
				double intermediatedivision = numerator / denominator;
				
				// the final BM25 score of this document
				finalScore += Math.log(intermediatedivision);
				//String[] tokenList=doc.text().split("\\s+");
				}
			BM25QueryScore.put(listOfFiles[docnumber].getName(), finalScore);
		
		}// end of docnumber loop
		
		// get the linkedhashmap values
		Set<Entry<String, Double>> set = BM25QueryScore.entrySet();
		Iterator itr = set.iterator();
		while(itr.hasNext())
			System.out.println(itr.next());
		
		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
		
		// sort each document id's with it's similarity score
		Collections.sort( list, new Comparator<Map.Entry<String, Double>>()
		{
			public int compare( Map.Entry<String, Double> o1, Map.Entry<String, Double> o2 )
			{
				return (o2.getValue()).compareTo( o1.getValue() );
			}
		} );
		
		int rank=0;
		//write to  text file
		for(Map.Entry<String, Double> entry:list){
			rank++;
			// get the top 100 documents
			if(rank==101)
				break;
			writer2.write(" "+(querynum+1)+" "+"Q"+(querynum+1)+" "+entry.getKey()
							 +" "+rank+" "+entry.getValue()+" BM25_System"+"\n");
		}
		writer2.write("*********************************************************************");
		}// end of first for loop
		
		writer2.close();
	}
	
	public static void main(String[] args) throws Exception {
		BM25 object1 = new BM25();
		object1.CalculateBM25();
		// TODO: READ EACH QUERY FROM THE FILE AS A STRING
		// AND THEN PUT THEM IN A LINKEDHASHMAP
		// ASSUMING MY QUERY
		//String myQuery = " What articles exist which deal with TSS (Time Sharing System), an"
							//+ " operating system for IBM computers?";	
	
		// testing for getting relevantdocs method for a query term
		/*
		ArrayList<String> reldocs = object.getRelevantDocs(1);
		int numofreldocs = object.getNumberofRelevantDocs("sdc-arpa", reldocs);
		System.out.println("Number of relevant docs are "+numofreldocs);
		*/
		/*
		
		// FOR TESTING METHODS
		File folder=new File("corpus");
		String line = null;
		File[] listOfFiles = folder.listFiles();
		
		for (int i = 0; i < 2; i++) 
		{
			Document doc = Jsoup.parse(listOfFiles[i], "UTF-8");
			int doclength = object.getDocumentLength(doc);
			String[] tokenList=doc.text().split("\\s+");
			for(String term: tokenList)
			{
				int tf = object.getTermFrequency(term, doc);
				System.out.println("Term frequency of "+term+" is "+tf);
				int df = object.getDocumentFrequency(term);
				System.out.println("Document frequency of "+term+" is "+df);
			}
			System.out.println("Doclength is "+doclength);
		}
		*/
/*		Set<Entry<String, Integer>> set = querytermfreq.entrySet();
		Iterator itr = set.iterator();
		while(itr.hasNext())
			System.out.println(itr.next());*/
		
	}// end of main
}//end of class
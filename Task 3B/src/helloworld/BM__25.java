package helloworld;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class BM__25 {

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

	public void CalculateBM25() throws IOException
	{

		double averagedoclength=0;
		LinkedHashMap<String, Double> querytermfreq = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> BM25QueryScore = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, LinkedHashSet<String>> RelevantDocs = new LinkedHashMap<String, LinkedHashSet<String>>();
		PrintWriter writer2 = new PrintWriter(new BufferedWriter(new FileWriter("BM25stemmed.txt")));


		BM__25 object = new BM__25();
		CorpusGeneration indexer = new CorpusGeneration();
		getQueries getqueryobj = new getQueries(); 

		double avgdoclength = object.getAverageDocLength();

		// get average doc length(avgdl)
		averagedoclength = object.getAverageDocLength();

		File folder=new File("corpus");
		File[] listOfFiles = folder.listFiles();

		ArrayList<Integer> doclength = new ArrayList<Integer>();
		for(int docnumber = 0;docnumber<3204;docnumber++)
		{
			Document doc = Jsoup.parse(listOfFiles[docnumber], "UTF-8");
			doclength.add(object.getDocumentLength(doc));
		}
		System.out.println("Out of docnumber loop");

		int k2=100;
		double k1 =1.2, b=0.75;
		// total docs in the corpus
		int N = 3204;

		LinkedHashMap<String, Integer> df = new LinkedHashMap<String,Integer>();
		// parse all the queries first and then take query by query
		//System.out.println("query number "+querynum);
		FileInputStream fis = new FileInputStream("cacm_stem.query.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		// get the total documents in the corpus
		while((line = br.readLine()) != null){
			String[] query = line.split("\\s+");
			//String query = "computers";
			//System.out.println("Query is "+query);
			for(String s : query)
			{
				//tokenize each term in the query
				//System.out.println("The query terms are "+s);
				String[] tokenizedquery = indexer.processToken(s);

				//get the frequency of each query term(qf(i))
				for(String queryterm: tokenizedquery)
				{
					//System.out.println("query terms are "+queryterm);
					df.put(queryterm, object.getDocumentFrequency(queryterm));
					//System.out.println(queryterm);
					double querytermfrequency = 0;
					for(String nextqueryterm: tokenizedquery)
						if(queryterm.equals(nextqueryterm))
							querytermfrequency++;
					querytermfreq.put(queryterm, querytermfrequency);
				}
			}
		}

		// *******************BM25 ALGORITHM************************************
		//System.out.println("in bm25 algo");
		FileInputStream fis2 = new FileInputStream("cacm_stem.query.txt");
		BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
		String line2=null;
		int count = 0;
		// get the total documents in the corpus
		while((line2 = br2.readLine()) != null)
		{
			// parse all the queries first and then take query by query
			//System.out.println("query is "+line2);
			//String query = getqueryobj.getQuery(querynum+1);
			//String query = "computers";
			//System.out.println("Query is "+query);
			String[] query = line2.split(" ");
			//tokenize each term in the query
			count++;
			for (String s: query)
			{
				//System.out.println("query term is "+s);
				String[] tokenizedquery = indexer.processToken(s);
				// value of R
				int R = 0;
				for(int docnumber = 0;docnumber<listOfFiles.length;docnumber++)
				{ 

					//if (count==5)
					//	break;
					//count++;
					System.out.println("Query number "+count+" Doc number is "+docnumber);
					Document doc = Jsoup.parse(listOfFiles[docnumber], "UTF-8");
					//System.out.println("doc is "+listOfFiles[docnumber].getName());
					double finalScore = 0;

					//System.out.println("average doc length is "+avgdoclength);

					int dl = doclength.get(docnumber);
					//System.out.println("doc length is "+doclength);

					// Calculating K
					double K = (0.75 * (dl/avgdoclength) + 0.25) * 1.2;
					//System.out.println("K is "+K);

					for(String queryterm : tokenizedquery)
					{
						//System.out.println("current queryterm is "+queryterm);
						// one half of the multiplied constants in BM25
						// This is the left half of the constants
						double normalizedTF = (2.2 * object.getTermFrequency(queryterm, doc))
								/
								(K + object.getTermFrequency(queryterm, doc));

						if(normalizedTF == 0)
							normalizedTF = 1;

						//System.out.println("Value of normalizedTF : "+normalizedTF);

						//System.out.println("Value of query term frequency : "+querytermfreq.get(queryterm));
						// This is the right half of the constants
						double normalizedQueryFreq = (101.0 * querytermfreq.get(queryterm))
								/
								(100 + querytermfreq.get(queryterm));

						if(normalizedQueryFreq == 0)
							normalizedQueryFreq = 1;

						//System.out.println("Value of normalizedQueryFreq : "+normalizedQueryFreq);

						double multipliedConstantFactor = normalizedTF * normalizedQueryFreq;

						//System.out.println("Value of K : "+K);
						//System.out.println("Value of multipliedConstantFactor : "+multipliedConstantFactor);

						// +1 bcz I am doing it sequentially from each query and assuming first query is Q1
						// 0+1 = 1st doc's relevance docs fetched

						//System.out.println("R is  "+R);

						// value of ri
						int ri = 0;
						//System.out.println("value of ri is "+ri);

						// value of ni
						int ni = df.get(queryterm);
						//System.out.println("value of ni is "+ni);

						double numerator = ((ri+0.5)
								/
								(R-ri+0.5));

						double denominator = ((ni - ri + 0.5)
								/
								(N - ni - R - + ri + 0.5));

						double intermediatedivision = (numerator / denominator) * multipliedConstantFactor;
						//System.out.println("value of intermediatedivision is "+intermediatedivision);

						// the final BM25 score of this document
						finalScore += Math.log(intermediatedivision);
						//System.out.println("finalScore is "+finalScore);
						//String[] tokenList=doc.text().split("\\s+");
					}
					//System.out.println("Score is "+finalScore);
					BM25QueryScore.put(listOfFiles[docnumber].getName(), finalScore);	
				}// end of docnumber loop
			}// end of first for loop(query loop)
			// get the linkedhashmap values
			Set<Entry<String, Double>> set = BM25QueryScore.entrySet();
			Iterator itr = set.iterator();
			//while(itr.hasNext())
			//	System.out.println(itr.next());

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
				writer2.write(" "+count+" "+"Q"+count+" "+entry.getKey()
				+" "+rank+" "+entry.getValue()+" BM25_System"+"\n");
			}
			writer2.write("*********************************************************************"+"\n");
		}// end of while loop
		writer2.close();
	}

	public static void main(String[] args) throws Exception {
		BM__25 object1 = new BM__25();
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

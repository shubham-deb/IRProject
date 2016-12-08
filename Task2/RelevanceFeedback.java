import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
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
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import org.jsoup.parser.TokenQueue;

public class RelevanceFeedback {

	public LinkedHashMap<String,Integer> getFrequentTerms(String reldoc) throws IOException {
		LinkedHashMap<String, Integer> tfofeachterm = new LinkedHashMap<String,Integer>();
		FileInputStream fis = new FileInputStream("corpus\\"+reldoc+".html"+".txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		FileInputStream fis2 = new FileInputStream("common_words.txt");
		BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
		String line=null;
		String line2 = null;
		ArrayList<String> allUniqueTokens = new ArrayList<String>();
		ArrayList<String> allTokens = new ArrayList<String>();
		ArrayList<String> stopWords = new ArrayList<String>();
		ArrayList<String> refinedtokens = new ArrayList<String>();
		
		// get all the tokens in the relevant doc in each line
		// and then calculate frequency of each token in the corpus
		while((line = br.readLine()) != null)
		{
			String[] tokenList=line.split("\\s+");
			for (String term: tokenList)
			{
				allTokens.add(term);
				if(!allUniqueTokens.contains(term))
					allUniqueTokens.add(term);
			}
		}
		//System.out.println("After getting all the unique tokens");
		
		// fetch all the stopwords from the file
		while((line2 = br2.readLine()) != null)
		{
			String[] stopword = line2.split("\\s+");
			stopWords.add(stopword[0]);
		}
		
		// remove all the stopwords from the unique tokens
		for(int uniquetoken = 0;uniquetoken < allUniqueTokens.size(); uniquetoken++)
		{
			int similarity = 0;
			for(int stopword = 0;stopword < stopWords.size(); stopword++)
			{
				if(allUniqueTokens.get(uniquetoken).equals(stopWords.get(stopword)))
					similarity++;
			}
			if (similarity==0)
				refinedtokens.add(allUniqueTokens.get(uniquetoken));
		}
		
		//System.out.println("After removing some of the unique tokens");
		// put token as key and the occurences of the token in the doc as the value
		for (String term: refinedtokens)
		{
		int termfrequency = 0;
		for (String nextterm: allTokens)
		{
			if (term.equals(nextterm))
				termfrequency++;
		}
			tfofeachterm.put(term, termfrequency);
		}
		br.close();
		br2.close();
		return tfofeachterm;
	}
	
	public ArrayList<String> getRelevantTerms(int querynumber) throws IOException
	{
		BM_25 bm25object = new BM_25();
		ArrayList<String> toprelterms = new ArrayList<>();
		LinkedHashMap<String,Integer> relterms = new LinkedHashMap<String,Integer>();
		ArrayList<String> reldocs = new ArrayList<String>();
		
		// get relative docs according to the query number
		reldocs = bm25object.getRelevantDocs(querynumber);
		
		for(int i=0;i<reldocs.size();i++)
			System.out.println("Relevant docs are "+reldocs.get(i));
		
		// get (FrequentTerm:Frequency) as the output as this function
		for(int reldocnum = 0;reldocnum<reldocs.size();reldocnum++)
		{
			relterms = getFrequentTerms(reldocs.get(reldocnum));
		}
		
		Set<Entry<String, Integer>> set = relterms.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
		
		// sort each document id's with it's similarity score
		Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
		{
			public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 )
			{
				return (o2.getValue()).compareTo( o1.getValue() );
			}
		});
		
		//write to  text file
		PrintWriter writer2 = new PrintWriter(new BufferedWriter(new FileWriter("Relevance Feedback.txt")));
		for(Map.Entry<String, Integer> entry:list){
			writer2.write(entry.getKey()+" "+entry.getValue()+"\n");
		}
		for(Map.Entry<String, Integer> entry:list){
		if(toprelterms.size()>5)
			break;
		 for (char c : entry.getKey().toCharArray())
		    {
		        if (!Character.isDigit(c)) 
		        	{
		        	toprelterms.add(entry.getKey());
		        	break;
		        	}
		        else break;
		    }
		
		}
/*		Entry<String, Integer> topword1  = list.get(0);
		Entry<String, Integer> topword2  = list.get(1);
		toprelterms.add(topword1.getKey());
		toprelterms.add(topword2.getKey());
		writer2.close();*/
		//for(int i=0;i<toprelterms.size();i++)
		//	System.out.println("Terms are "+toprelterms.get(i));
		//TODO Sorting this map
		//     Testing
		//     Getting all the frequent terms and putting it in an arraylist(optional)
		return toprelterms;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//LinkedHashMap<String,Integer> relterms = new LinkedHashMap<String,Integer>();
		ArrayList<String> relterms = new ArrayList<String>();
		RelevanceFeedback object = new RelevanceFeedback();
		relterms = object.getRelevantTerms(1);
		
/*		Set<Entry<String, Integer>> set = relterms.entrySet();
		Iterator itr = set.iterator();
		while(itr.hasNext())
			System.out.println(itr.next());*/
		}// end of main
}// end of class

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import javax.swing.SpringLayout;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class getqueries {
	
	public String getQuery(int number) throws IOException
	{
		StoppedCorpusGeneration indexer = new StoppedCorpusGeneration();
		String query=""	;
		LinkedHashMap<Integer, String> parsedqueries = new LinkedHashMap<Integer, String>();
		File input = new File("cacm-query.query");
		Document doc = Jsoup.parse(input, "UTF-8");
		Elements links = doc.getElementsByTag("doc");
		for (Element link : links) {
			  String queryterms = "";
			  String linkText = link.text();
			  String[] splitterms = linkText.split(" ");
			  int queryid = Integer.parseInt(splitterms[0]);
			  for(int i=1;i<splitterms.length;i++)
			  {
				  queryterms = " " + splitterms[i] + queryterms;  
			  }
			  //ArrayList<String> tokenizedTerms = new ArrayList<String>();
			  String[] terms = indexer.processToken(queryterms);
			  String tokenizedterm = "";
			  for (int i=0;i<terms.length;i++)
				  tokenizedterm = terms[i]+" "+tokenizedterm;
			  parsedqueries.put(queryid, tokenizedterm);
			  //System.out.println("Element is "+linkText);
			  //System.out.println("split term length "+splitterms.length);
			}
/*		PrintWriter writer2 = new PrintWriter(new BufferedWriter(new FileWriter("CACM-Queries.txt"))); 
		Set<Entry<Integer, String>> set = parsedqueries.entrySet();
		Iterator itr = set.iterator();
		while(itr.hasNext())
			writer2.write(""+itr.next()+"\n");
		writer2.close();*/
		return parsedqueries.get(number);
		
	}
	
	public Integer getTotalQueries() throws IOException
	{
		int totalqueries = 0;
		File input = new File("cacm-query.query");
		Document doc = Jsoup.parse(input, "UTF-8");
		Elements links = doc.getElementsByTag("doc");
		for (Element link : links) {
			totalqueries++;}
		return totalqueries;
	}
	
	public static void main(String args[]) throws IOException
	{
/*		StringBuilder s  = new StringBuilder("CACM-756");
		s.insert(s.indexOf("-")+1, "0");
		System.out.println(s);*/
		getqueries object = new getqueries();
		//int totalqueries = object.getTotalQueries();
		//System.out.println("The total queries are "+totalqueries);
		String returnedquery = object.getQuery(1);
		//System.out.println("The returnedquery is "+returnedquery);
		/*Set<Entry<Integer, String>> set = parsedqueries.entrySet();
		Iterator itr = set.iterator();
		while(itr.hasNext())
			System.out.println(itr.next());*/
		//System.out.println("First query is "+parsedqueries.get(1));
		//System.out.println("Second query is "+parsedqueries.get(2));
	}
}
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author debshubham
 * 
 */

public class RetreivalModule {

	/**
	 * @param queryterm  interpretation: (queryterm given by the user)
	 * @param buffreader interpretation: (reads each line of the text in the file)
	 * @return count     interpretation: (the number of occcurences of the queryterm in the document)
	 * @throws IOException
	 */
	private static double countWord(String queryterm, BufferedReader buffreader) throws IOException {
		// TODO Auto-generated method stub
		String readLine = "";
		double wordcount = 0;

		while((readLine = buffreader.readLine()) != null) 
		{
			// split words based on whitespace and store them in an array
			String[] words = readLine.split(" ");

			// for each word in words check if the queryterm is equal to the queryterm
			for(String s : words) 
				if(s.equals(queryterm)) 
					wordcount++;
		}
		return wordcount;
	}


	/**
	 * @param buffreader  interpretation: (reads each line of the text in the file)
	 * @return totaltokens interpretation: (the number of tokens in the document)
	 * @throws IOException
	 */
	private static double countWordsInFile(BufferedReader buffreader) throws IOException {
		// TODO Auto-generated method stub
		String readLine = "";
		double totaltokens = 0.0;
		while((readLine = buffreader.readLine()) != null) 
		{
			String[] words = readLine.split(" ");
			for(String s : words) 
				totaltokens++;
		}
		return totaltokens;
	}

	
	/**
	 * @param map
	 */
	public static <K, V> void printMap(Map<K, V> map) {
		for (Map.Entry<K, V> entry : map.entrySet()) {
			System.out.println("Key : " + entry.getKey()
			+ " Value : " + entry.getValue());
		}
	}

	/**
	 * @param unsortMap
	 * @return 
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> unsortMap) {

		List<Map.Entry<K, V>> list =
				new LinkedList<Map.Entry<K, V>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// open the file for reading info about tokens in each document
		FileInputStream fis = new FileInputStream("UnigramDocFrequency.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		//open the file for writing each query's document similarity scores
		PrintWriter writer2 = new PrintWriter(new BufferedWriter(new FileWriter("q4.txt"))); 
		String line = null;
		int docfreq=0, numberofdocuments = 1000, index = 0;
		
		//get input of the query from user
		System.out.println("Enter the query");
		Scanner input = new Scanner(System.in);
		String query = input.nextLine();
		
		// if there are no queries given by user terminate
		if(query.length()==0)
			return;

		//get each term in the query
		String[] q = query.split(" ");
		
		//convert to an arraylist for easier computation
		ArrayList<String> p = new ArrayList<String>(Arrays.asList(q));
		double lenofq = p.size();
		ArrayList<Double> numberofoccurs = new ArrayList<Double>();
		
		// get the unique words in the query and put it into a new data structure
		Set<String> uniqueWords = new LinkedHashSet<String>(p);
		p.clear();
		p.addAll(uniqueWords);

		//System.out.println(p);
		//System.out.println(uniqueWords);

		// for each unique query term count the number of occurences of each word
		// in the original list of query terms
		for (String firstsrs: uniqueWords)
		{    
			double occurences = 0;
			for (String restsrs: p){
				if(firstsrs.equals(restsrs))
					occurences+=1;
			}
			// add the term frequency of the unique word in an arraylist
			numberofoccurs.add(occurences/lenofq);
			//System.out.println(occurences);			
		}

		//for(int i = 0;i<numberofoccurs.size();i++)
		//	System.out.println(" "+numberofoccurs.get(i));

		//create a matrix of 
		// (1000 * number-of-words-in-query) with zeroes
		BigDecimal[][] matrix = new BigDecimal[numberofdocuments][p.size()];
		ArrayList<Double> querytermweights = numberofoccurs;

		for(int i=0;i<numberofdocuments;i++)
		{
			for(int j=0;j<p.size();j++)
			{
				BigDecimal bd = new BigDecimal(0);
				matrix[i][j]=bd;
			}
			System.out.println();
		}

		//for each term in query terms, get the query term weight
		/*for(int incr=0; incr<p.size();incr++)
		{
			TFforquery=1.00/q.length;
			//System.out.println("term frequency for query is "+TFforquery);
			fis = new FileInputStream("UnigramDocFrequency.txt");
			br = new BufferedReader(new InputStreamReader(fis));
			line=null;
			while((line = br.readLine()) != null)
			{
				String[] UDFinfo = line.split(":");
				String unigram = UDFinfo[0].trim();
				//System.out.println("unigram: " +UDFinfo[0]+" queryterm:"+q[incr]);

				//get the queryterm from the document
				if(unigram.equals(q[incr])){
					//System.out.println("Unigram matched is "+unigram);
					docfreq = Integer.parseInt(UDFinfo[2].trim());
					//System.out.println("doc frequency for query "+q[incr]+ " :"+docfreq);
					BigDecimal df2 = new BigDecimal(Math.log((double)numberofdocuments/docfreq)+1);
					//System.out.println("doc frequency for query is "+df2);
					//df2 = Math.log((double)numberofdocuments/docfreq)+1;
					BigDecimal qtf = new BigDecimal(TFforquery);
					MathContext mc = new MathContext(4); // 4 precision
					querytermweights[incr]= df2.multiply(qtf,mc);
					//System.out.println("query term weight is frequency for query is "+querytermweights[incr]);
					break;
				}
			}
		}
		 */
		
		
/*		System.out.println("Query term weights are: ");
		for(int incr=0; incr<p.size();incr++)
			System.out.println(p.get(incr)+" weight : "+querytermweights.get(incr));*/


		//BigDecimal sum = new BigDecimal(0);
		double sum = 0;
		
		//calculating part of denominator for cosine similarity
		for(int incr=0; incr<p.size();incr++)
		{
			sum = sum + (querytermweights.get(incr)*(querytermweights.get(incr)));
		}
		//double sqrsum = sum.doubleValue();
		// represents the square root of all the sum of products of the weights of each queryterm
		double sqrsum = Math.sqrt(sum);

		//System.out.println("square root of query term weights is "+sqrsum);
		br.close();

		//for each term in query terms, get the document term weight
		for(int incr=0; incr<p.size();incr++)
		{
			//System.out.println("the query term is "+q[incr]);
			fis = new FileInputStream("UnigramDocFrequency.txt");
			br = new BufferedReader(new InputStreamReader(fis));
			line=null;
			while((line = br.readLine()) != null)
			{
				//System.out.println("val: "+incr);
				String[] UDFinfo = line.split(":");
				String unigram = UDFinfo[0].trim();
				//System.out.println("unigram: " +UDFinfo[0]+" queryterm:"+q[incr]);

				//get the queryterm from the document
				if(unigram.equals(p.get(incr))){
					//System.out.println("unigram: "+unigram);

					//get document frequency of the term
					docfreq = Integer.parseInt(UDFinfo[2].trim());
					//System.out.println("doc frequency: "+docfreq);
					//System.out.println("number of docs "+numberofdocuments);
					
					// the normalized doc frequency for each query term
					BigDecimal df = new BigDecimal(Math.log((double)numberofdocuments/docfreq)+1);
					//System.out.println("normalized doc frequency "+df);

					//take the list of docs from the file
					StringBuilder listofdocs = new StringBuilder(UDFinfo[1]);

					//remove certain characters from the listofdocs
					for(int i=0; i<listofdocs.length();i++)
					{
						if(listofdocs.charAt(i)=='['||listofdocs.charAt(i)==']'||listofdocs.charAt(i)==',')
							listofdocs.deleteCharAt(i);
					}
					//System.out.println("The list of docs are: "+listofdocs);

					//split listofdocs to get each doc
					String[] docs = listofdocs.toString().split(" ");
					
					// get the raw html files from each folder
					File folder = new File("C:/Users/debsh/workspace/current/Assignment4/RAW HTML FILES/");
					File[] listOfFiles = folder.listFiles();
					//System.out.println("first document "+listOfFiles[0].getName());

					// for each list of files get the document term weight of each document
					// using normalized term weight and document weight
					for (int i = 0; i < listOfFiles.length; i++)
					{
						File file = listOfFiles[i];
						if (file.isFile()&& file.getName().endsWith(".txt"))
						{
							FileReader reader = new FileReader(file);
							BufferedReader buffreader = new BufferedReader(reader);
							
							//count the number of occurences of word in the document
							double count = countWord(p.get(incr), buffreader);

							FileReader reader2 = new FileReader(file);
							BufferedReader buffreader2 = new BufferedReader(reader2);
							
							// count the number of tokens in the file
							double totalcount = countWordsInFile(buffreader2);
							//System.out.println("total terms are "+totalcount);

							//System.out.println("Normalized document frequency "+df);
							BigDecimal tf = new BigDecimal(0);
							//Normalized term frequency of the query term
							if(count!=0)
								tf = BigDecimal.valueOf((double)Math.log(totalcount/count));

							//System.out.println("Normalized term frequency "+tf);

							MathContext mc = new MathContext(4); // 4 precision
							// multiply bg1 with bg2 using mc
							//System.out.println("the matrix value is "+tf.multiply(df, mc));
							
							// put the value of tf*idf in the matrix
							if(count==0.0 || totalcount==0.0)
								matrix[i][incr] = new BigDecimal(0);
							else
								matrix[i][incr] = tf.multiply(df, mc);
							//System.out.println("Term frequecncy of word: "+q[incr]+" in "+docs[i]+" :"+tf);
						}
					}
					break;
				}
				//end of while
			}
			//end of for
			
			// get the value of matrix
/*			for(int i=0;i<numberofdocuments;i++)
			{
				for(int j=0;j<p.size();j++)
					System.out.print(matrix[i][j]+" ");
				System.out.println();
			}*/
		}

		// the final matrix values
/*		for(int i=0;i<numberofdocuments;i++)
		{
			for(int j=0;j<p.size();j++)
				System.out.print(matrix[i][j]+" ");
			System.out.println();
		}*/

		// open the raw html files
		File folder = new File("C:\Users\debsh\Desktop\Assignment - 4\RAW HTML FILES\");
		File[] listOfFiles = folder.listFiles();
		LinkedHashMap<String,Double> docidfreq = new LinkedHashMap<String,Double>();	
		double[] finalScore = new double[1000];

		//calculating document similarity measure using vector space model
		for(int i=0;i<numberofdocuments;i++)
		{
			double intermediatesum = 0;
			double dtmul = 0;
			for(int j=0;j<p.size();j++)
			{
				dtmul = dtmul + matrix[i][j].doubleValue() * matrix[i][j].doubleValue();
			}
			
			// taking square root of the sum of products of document terms
			dtmul = Math.sqrt(dtmul);
			
			// getting the numerator of the equation
			// by multiplying document term weights by it's corresponding query term weights
			for(int j=0;j<p.size();j++)
			{
				intermediatesum = intermediatesum + matrix[i][j].doubleValue() * querytermweights.get(j).doubleValue();
			}
			
			if (dtmul*sqrsum==0.0)
				finalScore[i] = 0;
			else
				finalScore[i] = intermediatesum/(dtmul*sqrsum);
			
			docidfreq.put(listOfFiles[i].getName(), finalScore[i]);
		}

		// sort the finalscore array based on the score
		Arrays.sort(finalScore);

/*		for(int a =9; a>=0;a--)
			System.out.println("Document "+a+": "+finalScore[a]+" :");*/

/*		Iterator it = docidfreq.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			System.out.println(pair.getKey() + " = " + pair.getValue());
			//it.remove(); // avoids a ConcurrentModificationException
		}*/

		//System.out.println("\nSorted Map......By Value");

		// getting info of each document and it's corresponding similarity score
		Set<Entry<String, Double>> set = docidfreq.entrySet();
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
			writer2.write("4 "+"Q0 "+entry.getKey()+" "+rank+" "+entry.getValue()+" DEB_System"+"\n");
		}

		//it2.remove(); // avoids a ConcurrentModificationException

		//close the writer2 object
		writer2.close();
		//printMap(sortedMap);

		//
		//TODO:Multiplication

		// diplay the query
/*		for(int i=0;i<p.size();i++)
			System.out.println(p.get(i));

		//display the matrix
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<p.size();j++)
				System.out.print(matrix[i][j]);
			System.out.println();
		}*/

	}
}

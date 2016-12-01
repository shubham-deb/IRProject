import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.filechooser.FileSystemView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CorpusGeneration {

	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream("links.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		LinkedHashMap<String,LinkedHashMap<String,Integer>> word = 
				new LinkedHashMap<String,LinkedHashMap<String,Integer>>();
		LinkedHashMap<String,Integer> ivList = new LinkedHashMap<String,Integer>();
		LinkedHashSet<Integer> tokenindocument = new LinkedHashSet<Integer>();

		String line = null;
		int counter=0;
		PrintWriter writer2 = new PrintWriter(new BufferedWriter(new FileWriter("index.txt"))); 

		String title;
		while ((line = br.readLine()) != null)
		{    //connecting to the document via url
			counter++;
			Document doc = Jsoup.connect(line).get();
			//this would give me all elements which have anchor tag
			Elements hrefelements = doc.select("a");
			hrefelements.remove();
			Elements tablerowelements = doc.select("tr");
			tablerowelements.remove();
			//getting title of the url 
		     //StringBuilder title = new StringBuilder(doc.title());
			if(line.contains("_"))
		     title = line.substring((line.lastIndexOf("/")+1), line.length()).replace("_", "");
			else
			title = line.substring((line.lastIndexOf("/")+1), line.length()).replace("_", "");
		     title.toString().toCharArray();
		     //get the text of the file
			StringBuilder text =  new StringBuilder (doc.body().text());
			//System.out.println("words "+words);
			
			File theDir = new File("corpus");
			
			if (!theDir.exists()) {
			    System.out.println("creating directory: " + theDir.toString());
			    boolean result = false;

			    try{
			        theDir.mkdir();
			        result = true;
			    } 
			    catch(SecurityException se){
			        //handle it
			    }        
			    if(result) {    
			        System.out.println("DIR created");  
			    }
			}
			
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(theDir.toString()+"/"+title+".txt", true))); 		
			String[] lines2 = text.toString().split(" ");
			for(String s2: lines2){
				//System.out.println(text.charAt(counter3));
				s2.replaceAll("[,.;!?(){}\\[\\]<>%]", "");}
			tokenindocument.add(text.length());
			for (int counter3 = 0;counter3<text.length();counter3++)
				{
					if(text.charAt(counter3) >= 65 && text.charAt(counter3) <=90)    
					      // check for upper case letter ...   ascii code of "a" is 65 ... "z" is 90.
					  {
					  text.setCharAt(counter3, (char)(text.charAt(counter3)+32));    
					        //   The character at the specified index is set to  to uppercase
					  }
				}
			for(int counter3=0;counter3<text.length();counter3++){
				//System.out.println(text.charAt(counter3));
			if(text.charAt(counter3)== ',' ||
				   text.charAt(counter3)== '.' ||
				   text.charAt(counter3)== ';' ||
				   text.charAt(counter3)== '"' ||
				   text.charAt(counter3)== '<' ||
				   text.charAt(counter3)== '>' ||
				   text.charAt(counter3)== '^' ||
				   text.charAt(counter3)== '[' ||
				   text.charAt(counter3)== ']' ||
				   text.charAt(counter3)== ':'){
				   text.deleteCharAt(counter3);}}
			
			for(int counter2=0;counter2<text.length();counter2++)
				writer.write(text.charAt(counter2));
			String[] lines = text.toString().split(" ");
			for(String s: lines){	
				ivList= new LinkedHashMap<String,Integer>();
				//Integer frequency = getFrequency(s,title);
				int frequency = getFrequency(s,text);
				//System.out.println("String "+s+" has frequency "+frequency);
				String t = title.toString();
				ivList.put(t, frequency+1);
				//System.out.println("String "+s+" has key-value pair: "+wordValue);
				if(word.containsKey(s)){
				//System.out.println("it contains same key");
				word.get(s).put(t, frequency);
				//word.put(s,ivList);
				}
				else{
				//System.out.println("it contains different key");
				word.put(s,ivList);}
				}
			writer.close();
			}
		for (String key : word.keySet()) {
			 writer2.write(key + ":\t" + word.get(key)+"\n");}
		writer2.close();
		
		PrintWriter writer3 = new PrintWriter(new BufferedWriter(new FileWriter("UnigramTermFrequency.txt", true)));
		
		PrintWriter writer4 = new PrintWriter(new BufferedWriter(new FileWriter("UnigramDocFrequency.txt", true)));
		
		Comparator<Map.Entry<String,Integer>> byMapValues = new Comparator<Map.Entry<String,Integer>>() {
	    	 @Override
	        public int compare(Map.Entry<String, Integer> left, Map.Entry<String,Integer> right) {
	            return left.getValue().compareTo(right.getValue());
	        }
	    };
		Comparator<Map.Entry<Integer,String>> byMapKeys = new Comparator<Map.Entry<Integer,String>>() {
	    	 @Override
	        public int compare(Map.Entry<Integer, String> left, Map.Entry<Integer,String> right) {
	            return left.getValue().compareTo(right.getValue());
	        }
	    };
		LinkedHashMap<String,Integer> vals = new LinkedHashMap<String,Integer>();
		LinkedHashSet<Integer> ints = new LinkedHashSet<>();
		LinkedHashMap<Integer,String> keys = new LinkedHashMap<Integer,String>();
	    List<Map.Entry<String,Integer>> SortedValues = new ArrayList<Map.Entry<String, Integer>>();
	    List<Map.Entry<Integer,String>> SortedKeys = new ArrayList<Map.Entry<Integer, String>>();
		
		for (String key2 :word.keySet()){
			int value=0;
			Collection<Integer> c = word.get(key2).values();
			for (Integer values : c) {
			    int i = values.intValue();
			    value = value +i;
			    //do something with either value or i
			}	
			keys.put(value,key2);
			//writer3.write(key2+" : "+word.get(key2).values()+"\n");
			}
		SortedKeys.addAll(keys.entrySet()); 
		Collections .sort(SortedKeys,byMapKeys);
		
		TreeMap<Integer, String> sorted = new TreeMap<>(Collections.reverseOrder());
		
		for(Integer k:keys.keySet()){
			sorted.put(k, keys.get(k));
		}
			
		for (Integer s: sorted.keySet())
			writer3.write(sorted.get(s)+":"+s+"\n");
/*		
		for(int i=0;i<SortedKeys.size();i++)
			writer3.write(SortedKeys.indexOf(i)+" : "+SortedKeys.get(i)+"\n");*/
		writer3.close();
		
		
	    for (String key3 :word.keySet()){
	    		writer4.write(key3+" : "+word.get(key3).keySet()+" : "+word.get(key3).size()+"\n");
	   }
	    writer4.close();
	    // add all candy bars
	    SortedValues.addAll(vals.entrySet());
	    // sort the collection
	    Collections.sort(SortedValues, byMapValues);
	    
		br.close();
		//close two parenthesis, one for main and other for class
	}

	private static int getFrequency(String s, StringBuilder text) throws IOException {
		// TODO Auto-generated method stub
		int occcurences=0;
		String[] words = text.toString().split(" ");
		for(String w: words)
			if(w.equals(s))
				occcurences+=1;
		return occcurences;
	}
	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByKeys(Map<K,V> map){
        List<K> keys = new LinkedList<K>(map.keySet());
        Collections.sort(keys);
      
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
        for(K key: keys){
            sortedMap.put(key, map.get(key));
        }
      
        return sortedMap;
    }

}

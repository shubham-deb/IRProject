
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CorpusGeneration {

	public static void main(String[] args) throws IOException {
		
		File folder=new File("cacm");
		File[] listOfFiles = folder.listFiles();
		Map<String,Long> Tf=new HashMap<String,Long>();
		Map<String,Set<Integer>> Df=new HashMap<String,Set<Integer>>();
		
		//Setup index file
		PrintWriter indexWriter = new PrintWriter(new BufferedWriter(new FileWriter("index.txt"))); 
		
		for (int i = 0; i < listOfFiles.length; i++) {
			//System.out.println(listOfFiles[i].toString());
			// Assign docID to document
			indexWriter.write(listOfFiles[i].getName() + " " +i+"\n");
			Document doc = Jsoup.parse(listOfFiles[i], "UTF-8");
			
			//System.out.println(doc.toString());
			//System.out.println("====================================");
			//System.out.println(doc.text());
			//System.out.println("====================================");
			
			String[] tokenList=doc.text().split("\\s+");
			List<String> cleanedList = new LinkedList<String>();
			for (String token:tokenList) {
				//System.out.print(token + " ");
				String[] CleanedTokens=processToken(token);
				for (String Ctoken:CleanedTokens)
					cleanedList.add(Ctoken);
			}
			//System.out.println("====================================");
			/*
			for(String token:cleanedList){
				System.out.println(token);
			}
			*/
			
			File corpusDir = new File("corpus");
			createCorpusDir(corpusDir);
			PrintWriter corpusWriter = new PrintWriter(new BufferedWriter
					(new FileWriter(corpusDir.toString()+"/"+listOfFiles[i].getName()+".txt")));
			
			for(String token:cleanedList){
				corpusWriter.write(token+"\n");
				if(Tf.containsKey(token)){
					Tf.put(token, Tf.get(token)+1);
				} else {
					Tf.put(token,(long) 1);
				}
				
				if(Df.containsKey(token)){
					Set<Integer> docSet= Df.get(token);
					docSet.add(i);
					Df.put(token, docSet);
				} else {
					Set<Integer> docSet=new HashSet<Integer>();
					docSet.add(i);
					Df.put(token,docSet);
				}
			}
			
			corpusWriter.close();
		}
		indexWriter.close();
		
		
		
		PrintWriter DFWriter= new PrintWriter(new BufferedWriter(new FileWriter("UnigramDocFrequency.txt")));
		for(String tokenKey:Df.keySet()){
			Set<Integer> docSet= Df.get(tokenKey);
			DFWriter.write(tokenKey+" ");
			StringBuilder docString=new StringBuilder();
			String prefix = "";
			for (int docid:docSet){
				docString.append(prefix);
				prefix=",";
				docString.append(docid);
			}
			docString.append(" "+docSet.size());
			DFWriter.write(docString.toString()+"\n");
		}
		DFWriter.close();
		
		PrintWriter TFWriter = new PrintWriter(new BufferedWriter(new FileWriter("UnigramTermFrequency.txt")));
		for(String tokenKey:Tf.keySet()){
			TFWriter.write(tokenKey+ " " + Tf.get(tokenKey).toString()+"\n");
		}
		TFWriter.close();
	}
	
	public static void createCorpusDir(File directory){
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
	
	public static String[] processToken(String token) {
		//if token has only special characters
		//if(!token.matches("^[\\W_]+$")) {
			//TODO : Fix '=' sign
			token=token.toLowerCase();
			//Replace all special char at end of token
			token = token.replaceAll("[^a-zA-Z0-9]+$", "");
			//Replace all special char at start end of token
			token = token.replaceAll("^[^a-zA-Z0-9]+", "");
			
			//Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
	        //Matcher match= pt.matcher(token);
			
	        //Get tokens with special chars inbetween them
			if(!token.matches("[a-z0-9]+")){
				//System.out.println("case1: "+token);
				if(token.matches("[^0-9]+")){
					//System.out.println("case1.1: "+token);
					//Tokens that have letters and special chars
					//System.out.println("text only token: "+token);
					//ignore words with ONLY hyphens
					if(!token.matches("^[a-z-]+$")){	
						String[] cleanedToken=token.split("[^\\w]+");
						return cleanedToken;
					}
				} else if (token.matches("[^a-z]+")) {
					//Tokens that have numbers and special chars
					//System.out.println("case1.2: "+token);
				} else {
					//alphanumeric with special char
					//System.out.println("case1.3: "+token);
					String[] cleanedToken=token.split("[^\\w]+");
					return cleanedToken;
				}
				
			}
			
		//} 
		/*
		else {
			System.out.println("only symbol: "+token);
		}
		*/
		ArrayList<String> dummyList = new ArrayList<String>();
		dummyList.add(token);
		return dummyList.toArray(new String[dummyList.size()]);
	}

}

Name: Prathamesh Tajane
CCIS Username: prathameshtajane@ccs.neu.edu

Instructions to run the code

Requirements: 
1. Python 2.7.x or more running on system (specifically 2.7.12) 
2. Required packages:  - urllib, sys, time, re,math,beautifulSoup

How to run: 
1. The folder “ITask1A” which is inside IRProject folder contains following python program code file,
	i)calculate_tfidf_score.py
	ii)inverted_indexer_IRHW4_backup8.py
	
	Brief Description about the python files:
	i)calculate_tfidf_score.py
	  -This is a python score which ranks the documents present in the corpus and the query using tf*idf calculations.
           Please read ms-word file for detailed implementation guide of this algorithm.
	ii)inverted_indexer_IRHW4_backup8.py
	  -This is a python score which ranks the documents present in the corpus and the query using cosine similarity calculations.
           Please read ms-word file for detailed implementation guide of this algorithm.


2. Open command line and redirect to the directory “\IRProject\Task1A”. 
   e.g \User\Admin\Documents\IRProject\Task1A> 

3. “Task1A” Folder has following things(since this read me file is for python code execution ,we will concentrate only on those files which will be
    required to execute given python codes):
   i) calculate_tfidf_score.py
   ii) inverted_indexer_IRHW4_backup8.py
   iii)ReadMeForPythonFiles.txt 
   iv)corpus_main.txt
   v)CACM-Queries.txt
   vi)TFIDFRankingOutput2.txt
   vii)CosineLatestRun64Q.txt


5. Run the corpus.py using following command and a output will be generated in TFIDFRankingOutput2_1.txt
   python calculate_tfidf_score.py corpus_main.txt CACM-Queries.txt > TFIDFRankingOutput2_1.txt
   
   Following format is used for program execution:
   
   python calculate_tfidf_score.py <corpus_file> <queries file> > <output file>

   This file takes approximately 10 to 15 minutes to generate an output so request you to be patient.
 
6. Run the inverted_indexer_IRHW4_backup8.py using following command and a output will be generated in CosineLatestRun64Q_1.txt
   
   Following format is used for program execution:

   python inverted_indexer_IRHW4_backup8.py corpus_main.txt CACM-Queries.txt>CosineLatestRun64Q_1.txt   

   Following format is used for program execution:
   
   python inverted_indexer_IRHW4_backup8.py <corpus_file> <queries file> > <output file> 

   This file takes approximately 60 to 90 minutes to generate an output so request you to be patient.
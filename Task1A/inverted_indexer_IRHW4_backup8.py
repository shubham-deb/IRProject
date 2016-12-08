__author__ = 'Prathamesh Tajane'

import sys
import math
import time
from time import gmtime,strftime
from tabulate import tabulate
from collections import OrderedDict
from decimal import *
from math import *
from collections import defaultdict
import numpy as np
import operator
import warnings
warnings.filterwarnings('error')



getcontext().prec = 6
listOfTokens=[]
doc_id_key_dict={}
inverted_index={}
term_frequency_dict={}
sorted_term_frequency_dict={}
dictionary_sorted_inverted_index=[]
current_time = strftime("%Y-%m-%d-%H%M%S")
tokens_per_document_dict={}


'''
 split_file_into_list(list) Function
 description : create a new list by splitting by "#" the given list
               and removing first element of the list.
 Input:
 input: a list of string
 Output:
 Generate a list of strings with each element of the list are the tokens from the one webpage
'''
def split_file_into_list(input):
    return input.split("#_#")[1:]

'''
 Filter
 description : Filter which removes the numbers.
 '''
ignore_numbers = lambda num:not num.isdigit()


'''
 generateInvertedIndecx(corpus_file,ngramchoice) Function
 description : Creates an inverted index from the tokens(unigram,bigram,trigram) found in given corpus_file
 based on the given bigram choice.
 Input:
 corpus_file -> Corpus
 ngramchoice -> Choice of n-grams ,either unigram(1),bigram(2) or trigram(3)
 Output:
 An iverted list of tokens found in given corpus file based on the value of n-gram choice,
'''

def generateInvertedIndecx(corpus_file,ngramchoice,input_query,qid) :
    listOfTokensAllFile=split_file_into_list(corpus_file)

    i=0 #initialising i=1 so that tokens_per_document_dict will give total tokens present.

    '''
        Initialising j to 1 which will be used as a iterator for creating dictionary
        tokens_per_document_dict
    '''
    j=1


    '''
        tokens_per_document_dict is a dictionary,where the key is the docID and the values are the
        coresponding total number of tokens that are present in the given docID
        example :
        listOfTokensAllFile = ['1 the sky is blue','2 the sun is bright','3 the sun in the sky is bright']
        tokens_per_document_dict={1: 4, 2: 4, 3: 7}
    '''

    for eachdoc in listOfTokensAllFile:
        tokens_per_document_dict[j]=len(eachdoc.split(" "))
        j=j+1


    '''
        creating doc_id_key_dict which will stores all the tokens(here filter is used to remove the numbers
        and the key of the dictianry will be docID
        e.g. doc_id_key_dict['1'] => ('token1','token2','token3')
        example:
        {'1': ['the', 'sky', 'is', 'blue'], '3': ['the', 'sun', 'in', 'the', 'sky', 'is', 'bright'], '2': ['the', 'sun', 'is', 'bright']}
    '''
    for tokensEachFile in listOfTokensAllFile:
        doc_id_key_dict[tokensEachFile.split()[0]]=filter(ignore_numbers,tokensEachFile.split()[1:])

    '''
        calling create_ngrams_dict function here for this assignment ngramchoice is harcoded to 1 as only 1-gram
        inverted index has to be created
    '''

    create_ngrams_dict(doc_id_key_dict, ngramchoice,tokens_per_document_dict,input_query,qid)


'''
 create_ngrams_dict(input_dict,choice) Function
 description : Created a dictionary of n-grams based on the given  n-gram choice.
 Input:
 input_dict -> Dictionary of doc-id as key and tokens of that doc-id as values
 choice-> Choice of n-grams ,either unigram(1),bigram(2) or trigram(3)
 Output:
 Inverted index where the index term are modified according to the choice of n-gram.
'''

def create_ngrams_dict(input_dict,choice,tokens_per_document_dict,input_query,qid):
    ngram_dict={}
    build_term_docid_tf_list(input_dict,choice,tokens_per_document_dict,input_query,qid)
    ngram_dict={}


'''
 build_term_docid_tf_list(input_dict,ngramchoice) Function
 description : Created a dictionary of n-grams based on the given  n-gram choice.
 Input:
 input_dict -> Dictionary of doc-id as key and tokens of that doc-id as values
 choice-> 1
 Output:
 Inverted index where the index term are modified according to the choice of 1-gram.
 example:
 {'blue': [('1', 1)], 'sun': [('3', 1), ('2', 1)], 'is': [('1', 1), ('3', 1), ('2', 1)], 'sky': [('1', 1), ('3', 1)],
 'bright': [('3', 1), ('2', 1)], 'in': [('3', 1)], 'the': [('1', 1), ('3', 2), ('2', 1)}

'''


#build the list as [term,docid,tf]
def build_term_docid_tf_list(input_dict,ngramchoice,tokens_per_document_dict,input_query,qid):
    term_occur={}
    term_doc_occur=[]
    choice=ngramchoice
    for key,value_list in input_dict.items():
        for eachterm in value_list:
            if eachterm not in term_occur:
                term_occur[eachterm]=1
            else:
                term_occur[eachterm]=term_occur[eachterm]+1

        for k,v in term_occur.items():
            term_doc_occur.append((key,v,k))

        term_occur={}

    buildInvertedIndex(term_doc_occur,choice,tokens_per_document_dict,input_query,qid)



'''
 buildInvertedIndex(input_list,ngramchoice) Function
 description : Create inverted index in the laxicographical order of the index term
 Input:
 input_list -> A list of a list of of index-term,docID,and term frequency of given index term
 ngramchoice-> Choice of n-grams ,either unigram(1),bigram(2) or trigram(3)
 Output:
 Inverted index in term, docID, and df format in laxicographical order of index term
'''
def buildInvertedIndex(input_list,ngramchoice,tokens_per_document_dict,input_query,qid):
    list_for_tabular_format=[]
    idf_of_all_index_terms = {}
    for each in input_list:
        if each[2] not in  inverted_index:
            inverted_index[each[2]] = [each[0:2]]
        else:
            inverted_index[each[2]].append(each[0:2])

    '''
        This creates idf_of_all_index_terms for all the index terms which are present in the inverted_index
        by using a formula
        IDF(each_term) = 1 + loge(Total Number Of Documents / Number Of Documents with term 'each_term' in it)
        if 'each_term' not present in the corpus then returns 1.
        example:
        idf_of_all_index_terms={'blue': 2.09861228866811, 'sun': 1.4054651081081644, 'is': 1.0,
        'sky': 1.4054651081081644, 'bright': 1.4054651081081644, 'in': 2.09861228866811, 'the': 1.0}

        tokens_per_document_dict={1: 4, 2: 4, 3: 7}

    '''

    for eachkey in inverted_index:
        idf_of_all_index_terms[str(eachkey)]=createidf((len(inverted_index[eachkey])))

    '''
    this creates a matrix of idf of all the words that are present in the given query.
    so the size of the matrix will be (total number of docs * number of terms in input query)
    here it will be
    1000 * (number of terms in input query)

    '''

    create_matrix_of_document_idf(inverted_index,tokens_per_document_dict,input_query,idf_of_all_index_terms,qid)


'''
 createidf(df) Function
 description : This creates idf_of_all_index_terms for all the index terms which are present in the inverted_index
        by using a formula
        IDF(each_term) = 1 + loge(Total Number Of Documents / Number Of Documents with term 'each_term' in it)
        if 'each_term' not present in the corpus then returns 1.
 Input:
 df -> Document frequency of term
 Output:
 idf of the given df for given corpus
'''

def createidf(df):
    if df>0:
        return (1.0+log(3204.0/df))
    else:
        return 1.0



'''
 create_matrix_of_document_idf(inverted_index,tokens_per_document_dict,input_query,idf_of_all_index_terms,qid) Function
 description : This creates a matrix of size (Total number of docid's in the given corpus * total words present in
                the given query.The each value of the matrix will have (tf * idf) calculation for particaular word in particular
                document

                        word1       word2       word3
                docid1  tf.idf    tf.idf       tf.idf
                docid2  tf.idf    tf.idf       tf.idf
                docid3  tf.idf    tf.idf       tf.idf

                where (input_query = word1 word2 word3)
 Input:
 inverted_index -> Inverted index of the given corpus
 tokens_per_document_dict -> dictonary with docid as the key and toatl number of tokens present in that docid as a value.
 input_query -> Input query
 idf_of_all_index_terms -> idf of all the term which are present in the index
 qid -> query id
 Output:
 matrix of tf * idf as shown above.

 Example:
 input_query="the sun in the sky"
 input_query_list=['the', 'sun', 'in', 'the', 'sky']

 input_query_list_no_duplicates=['the', 'sun', 'in', 'sky']
 len(input_query_list)=5

 doc_idf_matrix=[[1, 0, 0, 1], [1, 1, 0, 0], [2, 1, 1, 1]]

 list_of_idf_each_input_term=[0.2, 0.28109302162163285, 0.419722457733622, 0.28109302162163285]

 doc_idf_matrix after tf.idf calculations=
[[0.05, 0.0, 0.0, 0.07027325540540821], [0.05, 0.07027325540540821, 0.0, 0.0], [0.05714285714285714, 0.04015614594594755, 0.05996035110480314, 0.04015614594594755]]

'''

def create_matrix_of_document_idf(inverted_index,tokens_per_document_dict,input_query,idf_of_all_index_terms,qid):
    input_query_list=(input_query.split(" "))

    '''
    creating input_query_list_no_duplicates by keeping only one instance of the duplicate words that might be present in
    the query.
    '''
    input_query_list_no_duplicates = getUniqueItems(input_query_list)
    width = len(input_query_list_no_duplicates)

    inverted_index_arr = []
    inverted_index_arr_with_no_duplicates = []
    for key, values in inverted_index.items():
        inverted_index_arr.append(key)

    width1 = len(inverted_index_arr)
    height1 = 3204

    doc_idf_matrix1 = [[0 for x in range(width1)] for y in range(height1)]
    inverted_index_for_input_query1 = {}

    wordnum11 = 0

    print "Creating tokens_per_document_dict"
    # for each_query_term in input_query_list_no_duplicates:
    for each_query_term in inverted_index_arr:
        if inverted_index.has_key(each_query_term):
            inverted_index_for_input_query1[each_query_term] = inverted_index[each_query_term]
        else:
            inverted_index_for_input_query1[each_query_term] = []
        for k, v in inverted_index_for_input_query1.iteritems():
            for item in v:
                doc_idf_matrix1[int(item[0]) - 1][wordnum11] = (item[1])
            wordnum11 = wordnum11 + 1
        inverted_index_for_input_query1 = {}

    docid1 = 1
    wordnum1 = 1

    print "Creation of tokens_per_document_dict Completed"
    # print tokens_per_document_dict


    print "Creation of doc_tf.idf_matrix started"
    while docid1 <= height1:
        # while wordnum <=(len(input_query_list_no_duplicates)):
        while wordnum1 <= (len(inverted_index_arr)):
            doc_idf_matrix1[docid1- 1][wordnum1 - 1] = (
            (int(doc_idf_matrix1[docid1 - 1][wordnum1 - 1]) + 0.0) / int(tokens_per_document_dict[docid1]))
            wordnum1 = wordnum1 + 1
        docid1 = docid1 + 1
        wordnum1 = 1

    # print "doc_tf_matrix"
    # print doc_idf_matrix
    list_of_idf_each_input_term1 = []

    # for term in input_query_list_no_duplicates:
    for term in inverted_index_arr:
        if inverted_index.has_key(term):
            list_of_idf_each_input_term1.append(idf_of_all_index_terms[term])
        else:
            list_of_idf_each_input_term1.append(0)

    # print "list_of_idf_each_input_term"
    # print list_of_idf_each_input_term
    # multiplying idf for each input term to the words present in doc_idf_matrix

    wordcol1 = 0
    docol1 = 0

    while wordcol1 <= (len(inverted_index_arr_with_no_duplicates) - 1):
        # while docol <= 999:
        while docol1 <= 3203:
            doc_idf_matrix1[docol1][wordcol1] = doc_idf_matrix1[docol1][wordcol1] * list_of_idf_each_input_term1[wordcol1]
            docol1 = docol1 + 1
        docol1 = 0
        wordcol1 = wordcol1 + 1



    ###########################################################################################
    '''
    Height will be the number of doc(i.e. docid's) that are present in the given corpus
    '''
    #height = 1000
    height = 3204
    doc_idf_matrix = [[0 for x in range(width)] for y in range(height)]
    inverted_index_for_input_query={}

    '''
    input_query_list_no_duplicates contain the list of all the unique words from the given query
    '''
    input_query_list_no_duplicates=getUniqueItems(input_query_list)

    wordnum1=0

    for each_query_term in input_query_list_no_duplicates:
        if inverted_index.has_key(each_query_term):
            inverted_index_for_input_query[each_query_term]=inverted_index[each_query_term]
        else:
            inverted_index_for_input_query[each_query_term]=[]
        for k,v in inverted_index_for_input_query.iteritems():
            for item in v:
                doc_idf_matrix[int(item[0])-1][wordnum1]=(item[1])
            wordnum1=wordnum1+1
        inverted_index_for_input_query = {}
    docid=1
    wordnum=1

    while docid <= height:
        while wordnum <=(len(input_query_list_no_duplicates)):
            doc_idf_matrix[docid-1][wordnum-1]=((int(doc_idf_matrix[docid-1][wordnum-1])+0.0)/int(tokens_per_document_dict[docid]))
            wordnum=wordnum+1
        docid=docid+1
        wordnum=1

    list_of_idf_each_input_term=[]

    for term in input_query_list_no_duplicates:
        if inverted_index.has_key(term):
            list_of_idf_each_input_term.append(idf_of_all_index_terms[term])
        else:
            list_of_idf_each_input_term.append(0)


    #multiplying idf for each input term to the words present in doc_idf_matrix

    wordcol=0
    docol=0

    while wordcol<=(len(input_query_list_no_duplicates)-1):
        #while docol <= 999:
        while docol <= 3203:
            doc_idf_matrix[docol][wordcol]=doc_idf_matrix[docol][wordcol]*list_of_idf_each_input_term[wordcol]
            docol=docol+1
        docol=0
        wordcol=wordcol+1

    createtfidf_list_for_query(input_query,doc_idf_matrix,qid,input_query_list,doc_idf_matrix1)


def createtfidf_list_for_query(input_query,doc_idf_matrix,qid,input_query_list,doc_idf_matrix1):
    query_tfidf_list=[]
    query_tfidf_dict = {}
    query_dict = defaultdict(int)
    for word in input_query.split():
        query_dict[word] += 1

    wordlist = input_query.split(" ")


    for eachword in wordlist:
        query_tfidf_dict[eachword]=(query_dict[eachword]/(len(input_query_list)+0.0))

    wordlist_no_duplicate_ordered=getUniqueItems(wordlist)

    for eachword in wordlist_no_duplicate_ordered:
        query_tfidf_list.append(query_tfidf_dict[eachword])

    '''
    #original formula where tf pf query is the number of times that particular term appears in query.
        for eachword in wordlist_no_duplicate_ordered:
        query_tfidf_list.append(query_tfidf_dict[eachword])
    '''

    createCosineSimForEachDoc(query_tfidf_list, doc_idf_matrix,qid,doc_idf_matrix1)


def createCosineSimForEachDoc(query_tfidf_list,doc_idf_matrix,qid,doc_idf_matrix1):
    docidcount = 0
    #height = 1000
    height = 3204
    cosinesimdocdict={}
    while docidcount < height:
        cosinesimdocdict[docidcount+1]=createCosineSim(query_tfidf_list,doc_idf_matrix[docidcount],doc_idf_matrix1[docidcount])
        docidcount=docidcount+1
    print "COSINE-SIMILARITY-SCORE"
    sorted_cosinesimdocdict_list = sorted(cosinesimdocdict.items(), key=operator.itemgetter(1))
    eachterm=len(sorted_cosinesimdocdict_list)
    rank=1
    myfile2 = open('CosineSimilarity-for-query' + str(qid) +'-'+ current_time + '.txt', 'a')
    while eachterm > 0 and rank <= 100:
        print str(qid) + " Q0 " +" "+str(sorted_cosinesimdocdict_list[eachterm-1][0]) +" "+ str(rank) +" "+ str(sorted_cosinesimdocdict_list[eachterm-1][1]) +" "+ "CosineSimAlgo"
        myfile2.write(str(qid) + " Q0 " +" "+str(sorted_cosinesimdocdict_list[eachterm-1][0]) +" "+ str(rank) +" "+ str(sorted_cosinesimdocdict_list[eachterm-1][1]) +" "+
                      "CosineSimAlgo\n")
        eachterm=eachterm-1
        rank=rank+1



def createCosineSim(qlist,dlist,dlist1):
    vq = np.array(qlist)
    vd = np.array(dlist)
    try:
        cosine_sim=((np.dot(vq,vd))/sqrt(squareoflist(qlist)*squareoflist(dlist1)))
        return cosine_sim
    except:
        return 0

def squareoflist(input_list):
    total=0
    for eachterm in input_list:
        total=total+((eachterm)*(eachterm))
    return total


'''
 getUniqueItems(iterable) Function
 description : Creates a list of unique elements without changing its order.
 Input:
 iterable -> A list of an inverted index
 Output :
 A list of unique elements without changing its order.
'''


def getUniqueItems(iterable):
    seen2 = set()
    result2 = []
    for item in iterable:
        if item not in seen2:
            seen2.add(item)
            result2.append(item)
    return result2


def main():

    if len(sys.argv) == 3:
        given_file = sys.argv[1]
        query_file = sys.argv[2]
        #qid = sys.argv[2]
        #input_query=sys.argv[3]
        ngramchoice = "1"
        input_file=open(given_file)
        corpus_file=input_file.read()
        query_list=[]
        raw_query_file=open(query_file)
        query_file_read=raw_query_file.read()
        query_list=query_file_read.split("=")
        #print query_list
        #print len(query_list[1:])
        qid=1
        for each_query in query_list[1:]:
            print each_query
            generateInvertedIndecx(corpus_file,ngramchoice,each_query,qid)
            qid+=1
    else:
       sys.exit('Format to run: python {0} <corpus_filename> <query_file>'.format(sys.argv[0]))

if __name__ == '__main__':
    main()



'''
def main():


        Input: python <corpus_filename> <query_id> <query_string>
        Then: sys.argv = [<corpus_filename>, <query_id>,<query_string>].

        Answers:
            1. python inverted_indexer_IRHW4.py corpus.txt 1 "global warming potential"
            2. python inverted_indexer_IRHW4.py corpus.txt 2 "green	power renewable energy"
            3. python inverted_indexer_IRHW4.py corpus.txt 3 "solar	energy california"
            4. python inverted_indexer_IRHW4.py corpus.txt 4 "light	bulb bulbs alternative alternatives"




    if len(sys.argv) == 4:
        given_file = sys.argv[1]
        qid = sys.argv[2]
        input_query=sys.argv[3]
        ngramchoice = "1"
        input_file=open(given_file)
        corpus_file=input_file.read()
        generateInvertedIndecx(corpus_file,ngramchoice,input_query,qid)
    else:
       sys.exit('Format to run: python {0} <corpus_filename> <query_id> <query_string>'.format(sys.argv[0]))

if __name__ == '__main__':
    main()
'''

import rawdoc
import corpus
import topic
import socialgraph
import index
import pagerank
import search
import os
import sys
import time

def gen():
	rdoc = rawdoc.RawDocuments(20)
	hwids = ["1fb5f5eb95abf677", 
			"37d1cc080bd90d61", 
			"58dae26e68c055b5", 
			"59a1aad2ac9e18cf",
			"5fd01dd3c4253311",
			"70cad1272ff9c13c",
			"ee8a9b6e9e0deb89", 
			"fe411ac462309f62"]
	hwids = []
	for i in range(10000):
		hwids.append(hex(i))
	rdoc.random_generate(hwids, 10000)

def build():
	rdoc = rawdoc.RawDocuments(15)
	rdoc.build_corpus()

def read():
	c = corpus.MyCorpus(20)
	print c.read_corpus()

def tfidf():
	c = corpus.MyCorpus(20)
	t = topic.MyTopicModel()
	print t.tfidf_model(c.read_corpus())

def lda():
	c = corpus.MyCorpus(20).read_corpus()
	t = topic.MyTopicModel()
	mlda = t.lda_model(c, 5)
	lda_corpus = mlda[c]
	for doc in lda_corpus:
		print doc

def graph():
	c = corpus.MyCorpus(20).read_corpus()
	t = topic.MyTopicModel()
	mlda = t.lda_model(c, 5)
	sg = socialgraph.MyTopicGraph(mlda, c)
	sg.build_graph(0.01)
	sg.save_graph()

def rindex():
	idx = index.MyIndexBuilder()
	idx.build_index()

def rank():
	rk = pagerank.MyPageRank()
	rk.calculate2()

def search_test():
	sh = search.MySearchHelper()
	t = time.time()
	sh.save_xml(sh.search("59a1aad2ac9e18cf", 4), "ans.xml")
	print time.time() - t

if __name__ == "__main__":
	#gen()
	#build()
	#read()
	#tfidf()
	#lda()
	#graph()
	#rindex()
	rank()
	#search_test()

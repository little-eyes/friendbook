import corpus
import gensim
import pythondb
import time


class MyTopicModel:
	
	"""
		This class is used to find the topics among docuemnts through different 
		ways. The Major may is LDA decomposation. Data is coming from database.
	"""

	_db_handler = None

	def __init__(self):
		self._db_handler = pythondb.MyDatabaseHandler("localhost", \
				"friendbook-dev", "2TMy2mHPmB5YmBd5", "friendbook")
	
	def tfidf_model(self, input_corpus):
		return gensim.models.TfidfModel(input_corpus)
	
	def lda_model(self, input_corpus, ntopics):
		mlda = gensim.models.ldamodel.LdaModel(input_corpus, num_topics=ntopics)
		self.__save_lda_topic(mlda, input_corpus)
		return mlda
	
	# The
	def __save_lda_topic(self, mlda, input_corpus):
		lda_corpus = mlda[input_corpus]
		ids = corpus.MyCorpus(20).read_ids()
		o = 0
		for li in lda_corpus:
			#sorted(li, key=lambda o: o[1], reverse=True)
			#print li
			li.sort(key=lambda o: (o[1], o[0]), reverse=True)
			#print li
			k = len(li)/2
			tp = ""
			for i in range(k):
				tp += str(li[i][0]) + ","
			#print tp
			query = "select * from `topic` where `hwid`='"+ids[o]+"'"
			res = self._db_handler.read_db_matrix(query)
			if len(res) > 0:
				query = "update `topic` set `timestamp`='"+str(int(time.time())) \
					+"', `topic`='"+tp[0:len(tp)-1]+ \
					"' where `hwid`='"+ids[o]+"'"
				self._db_handler.update_db(query)
			else:
				query = "insert into `topic` (`hwid`, `timestamp`, `topic`) \
					values ('"+ids[o]+"', '"+str(int(time.time()))+ \
					"', '"+tp[0:len(tp)-1]+"')"
				self._db_handler.update_db(query)
			o += 1


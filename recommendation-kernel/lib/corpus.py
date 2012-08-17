import pythondb


class MyCorpus:
	
	"""
		This class is used to generate the corpus from database and return
		the corpus in memory to external usage.
		Here, a all-in-memory mode is applied, since the data set is not that
		large. But eventually, this should go to disk and use constant memory.
	"""
	_db_handler = None
	_nwords = 0

	def __init__(self, nwords):
		self._db_handler = pythondb.MyDatabaseHandler("localhost", \
				"friendbook-dev", "2TMy2mHPmB5YmBd5", "friendbook")
		self._nwords = nwords

	def read_corpus(self):
		corpus = []
		query = "select * from `corpus`"
		res = self._db_handler.read_db_matrix(query)
		for li in res:
			tr = []
			for j in range(self._nwords):
				tr.append((j, int(li[j+3])))
			corpus.append(tr)
		
		return corpus
	
	def read_ids(self):
		ids = {}
		query = "select `hwid` from `corpus`"
		res = self._db_handler.read_db_matrix(query)
		n = len(res)
		for i in range(n):
			ids[i] = res[i][0]

		return ids

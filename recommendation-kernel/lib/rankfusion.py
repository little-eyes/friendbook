import pythondb

class MyRankFusion:
	"""
		This class is to fuse the rankings from both the system page rank
		and the user's ratings. The equation is the following:
			
			R = a*R + (1-a)*F
			
		If a user gets multiple ratings, we should run the algorithm
		multiple time to get cumulative ratings.
	"""
	
	_page_rank = {}	
	_db_handler = None

	def __init__(self):
		self._db_handler = pythondb.MyDatabaseHandler("localhost", \
		"friendbook-dev", "2TMy2mHPmB5YmBd5", "friendbook")
	
	def fuse(self, confidence_factor=0.8):
		query = "select `hwid`, `rank` from `ranks`"
		res = self._db_handler.read_db_matrix(query)
		if len(res) == 0:
			return
		for li in res:
			self._page_rank[li[0]] = li[1]

		query = "select * from `rating`"
		res = self._db_handler.read_db_matrix(query)
		
		for li in res:
			usr = li[2]
			score = li[3]
			self._page_rank[usr] = confidence_factor * self._page_rank[usr] + \
				(1 - confidence_factor) * score

		for user in self._page_rank.keys():
			query = "update `ranks` set `frank`='"+str(self._page_rank[user]) \
				+"' where `hwid`='"+str(user)+"'"
			self._db_handler.update_db(query)


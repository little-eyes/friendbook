import pythondb
import time


class MyIndexBuilder:

	"""
		This class is to build the index of each topic,
		so that the the front-end database has the following
		key type:
			<topic>:<user1>, <user2>, <user3>...
	"""
	
	_db_handler = None

	def __init__(self):
		self._db_handler = pythondb.MyDatabaseHandler("localhost", \
				"friendbook-dev", "2TMy2mHPmB5YmBd5", "friendbook")

	def build_index(self):
		query = "select * from `topic`"
		res = self._db_handler.read_db_matrix(query)
		rindex = {}
		for li in res:
			tp = li[3].split(",")
			for k in tp:
				if k not in rindex.keys():
					rindex[k] = []
				if li[1] not in rindex[k]:
					rindex[k].append(li[1])

		for k in sorted(rindex.iterkeys()):
			docs = ""
			for j in rindex[k]:
				docs += j + ","
			query = "select * from `index` where `topic`='"+str(k)+"'"
			res = self._db_handler.read_db_matrix(query)
			if len(res) > 0:
				query = "update `index` set `timestamp`='"+str(int(time.time()))+ \
				"', `doclist`='"+docs[0:len(docs)-1]+ \
				"' where `topic`='"+str(k)+"'"
				self._db_handler.update_db(query)
			else:
				query = "insert into `index` (`timestamp`, `topic`, `doclist`) \
					values ('"+str(int(time.time()))+"', '"+str(k)+ \
					"', '"+docs[0:len(docs)-1]+"')"
				self._db_handler.update_db(query)


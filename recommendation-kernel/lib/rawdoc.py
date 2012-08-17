import pythondb
import os
import random
import time


class RawDocuments:
	
	"""
		This class is responsible for raw document process.
		In reality, the users will upload their daily documents,
		then the class provides the interface to append the
		daily document to a user's overall life document.
	"""

	_db_handler = None
	_nwords = 0

	def __init__(self, nwords):
		self._db_handler = pythondb.MyDatabaseHandler("localhost", \
				"friendbook-dev", "2TMy2mHPmB5YmBd5", "friendbook") 
		self._nwords = nwords

	# This is used to generate some random initial documents for
	# each user.
	def random_generate(self, hwids, length):
		for idx in hwids:
			entity = ""
			for i in range(length):
				docid = random.randint(1, self._nwords)
				entity += str(docid) + ";"

			query = "INSERT INTO `rawdoc` (`hwid`, `timestamp`, \
			`entity`) VALUES ('"+str(idx)+"', '"+str(int(time.time())) \
			+"', '"+str(entity[0:len(entity)-1])+"');"
			self._db_handler.update_db(query)
	
	def build_corpus(self):
		query = "SELECT DISTINCT `hwid` from `rawdoc`"
		hwid = self._db_handler.read_db_matrix(query)
		n = len(hwid)
		for i in range(n):
			idx = hwid[i][0]
			data = [0 for o in range(2*self._nwords)]
			query = "select `entity` from `rawdoc` where `hwid`='"+str(idx)+"'"
			res = self._db_handler.read_db_matrix(query)
			if len(res) == 0: continue
			for li in res:
				words = li[0].split(";")
				for k in words:
					#if len(k) == 0 or int(k) > 20: continue
					data[int(k)-1] += 1
			query = "select * from `corpus` where `hwid`='"+str(idx)+"'"
			res = self._db_handler.read_db_matrix(query)
			if len(res) == 0:
				query = "insert into `corpus` (`hwid`, `timestamp`"
				for o in range(self._nwords):
					query += ", `w"+str(o+1)+"`"
				query += ") values ('"+idx+"', '"+str(int(time.time()))+"'"
				for o in range(self._nwords):
					query += ", '"+str(data[o])+"'"
				query += ")"
				self._db_handler.update_db(query)
			else:
				query = "update `corpus` set `timestamp`='"+str(int(time.time()))+"'"
				for o in range(self._nwords):
					query += ", `w"+str(o+1)+"`='"+str(data[o])+"'"
				query += " where `hwid`='"+str(idx)+"'"
				self._db_handler.update_db(query)
	

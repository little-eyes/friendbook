import MySQLdb

"""
	This lib requires the successful installation of python-mysqldb
	package.
"""

class MyDatabaseHandler:
	
	_conn = None
	_host_name = None
	_user_name = None
	_password = None
	_db_name = None
		
	def __init__(self, host, usr, pwd, db):
		self._host_name = host
		self._user_name = usr
		self._password = pwd
		self._db_name = db
		self._conn = self.__open_db()
	
	def __open_db(self):
		try:
			conn = MySQLdb.connect(self._host_name, self._user_name, 
				self._password, self._db_name)
			return conn
		except MySQLdb.Error, e:
			print "Error %d: %s" % (e.args[0], e.args[1])
			return None

	def read_db_matrix(self, query):
		try:
			cursor = self._conn.cursor()
			cursor.execute(query)
			item = cursor.fetchall()
			cursor.close()
			return item
		except MySQLdb.Error, e:
			print "Error %d: %s" % (e.args[0], e.args[1])
			return None;

	def read_db_dict(self, query):
		try:
			cursor = self._conn.cursor(MySQLdb.cursors.DictCursor)
			cursor.execute(query)
			res = cursor.fetchall()
			cursor.close()
			return res
		except MySQLdb.Error, e:
			print "Error %d: %s" % (e.args[0], e.args[1])
			return None

	def update_db(self, query):
		try:
			cursor = self._conn.cursor()
			cursor.execute(query)
			cursor.close()
			self._conn.commit()
			return True
		except MySQLdb.Error, e:
			print "Error %d: %s" % (e.args[0], e.args[1])
			return False

	def exit(self):
		try:
			self._conn.commit()
			self._conn.close()
			return True
		except MySQLdb.Error, e:
			print "Error %d: %s" % (e.args[0], e.args[1])
			return False


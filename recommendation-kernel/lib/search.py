import pythondb
import time
import xml.dom.minidom
import xml.etree.ElementTree


class MySearchHelper:
	"""
		MySearchHelper is used to search the most relevant friends
		and rank them into a XML results list.
		MySearchHelper only care about "hwid" and "top-k".
		If there is "no document" information for hwid, the results will
		be an empty XML document.
	"""

	_db_handler = None

	def __init__(self):
		self._db_handler = pythondb.MyDatabaseHandler("localhost", \
				"friendbook-dev", "2TMy2mHPmB5YmBd5", "friendbook")
	
	# step 1. find hwid's topic
	# step 2. take the topic index and find relevant people.
	# step 3. grab ranks and sort.
	# step 4. generate final xml results.
	def search(self, hwid, k):
		query = "select * from `topic` where `hwid`='"+hwid+"'"
		res = self._db_handler.read_db_matrix(query)
		if len(res) == 0:
			return None
		topics = res[0][3].split(",")
		candidates = {}
		for t in topics:
			query = "select * from `index` where `topic`='"+str(t)+"'"
			res = self._db_handler.read_db_matrix(query)
			if len(res) == 0:
				candidates[t] = []
			candidates[t] = res[0][3].split(",")
		people = self.__fuse(candidates)
		for p in people.keys():
			query = "select `frank` from `ranks` where `hwid`='"+str(p)+"'"
			print query
			res = self._db_handler.read_db_matrix(query)
			print res
			if len(res) == 0:
				people[p] = -1.0
				continue
			people[p] = res[0][0]
		sorted_people = sorted(people.items(), \
			key=lambda o: o[1], reverse=True)	
		top_k = []
		for key, value in sorted_people:
			if k == 0: 
				break
			if value < 0:
				continue
			top_k.append((key, value))
			k -= 1

		return self.__to_xml(top_k)

	def __fuse(self, candidates):
		res = {}
		for key in candidates.keys():
			for hwid in candidates[key]:
				res[hwid] = 0.0
		return res

	def __to_xml(self, top_k):
		doc = xml.dom.minidom.Document()
		base = doc.createElement("results")
		doc.appendChild(base)

		for k, v in top_k:
			friend = doc.createElement("friend")

			hwid = doc.createElement("hwid")
			hwid_text = doc.createTextNode(str(k))
			hwid.appendChild(hwid_text)
			friend.appendChild(hwid)

			name = doc.createElement("name")
			name_text = doc.createTextNode("N/A")
			name.appendChild(name_text)
			friend.appendChild(name)

			company = doc.createElement("company")
			company_text = doc.createTextNode("N/A")
			company.appendChild(company_text)
			friend.appendChild(company)

			score = doc.createElement("score")
			score_text = doc.createTextNode(str(v))
			score.appendChild(score_text)
			friend.appendChild(score)

			base.appendChild(friend)

		return doc

	def save_xml(self, doc, path):
		f = open(path, "w")
		doc.writexml(f, indent="  ", addindent="  ", newl="\n")

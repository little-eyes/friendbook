#import topic
#import pythondb
#import corpus
import time


class MyTopicGraph:
	
	_graph = {}
	def __init__(self):
		self._graph = {}

	# An improved version: truncate to 80% of a user's topic explaination.
	def __similarity2(self, va, vb, explain_rate=0.8):
		s = 0
		norm_a = 0
		norm_b = 0
		for i in range(1,len(va)-1): 
			s += va[i]*vb[i]
			norm_a += va[i]**2
			norm_b += vb[i]**2
		norm_a = norm_a**0.5
		norm_b = norm_b**0.5
		s = s/norm_a/norm_b

		aa = []
		for i in range(1,len(va)-1):
			aa.append((i-1, va[i]))
		bb = []
		for i in range(1,len(vb)-1):
			bb.append((i-1, vb[i]))
			
		aa.sort(key=lambda o: (o[1], o[0]), reverse=True)
		bb.sort(key=lambda o: (o[1], o[0]), reverse=True)

		ua = []
		ub = []

		x = 0
		for o in aa:
			if x >= explain_rate: break
			x += o[1]
			ua.append(o)
		x = 0
		for o in bb:
			if x >= explain_rate: break
			x += o[1]
			ub.append(o)
		x = 0
		for i in range(len(ua)):
			for j in range(len(ub)):
					if ua[i][0] == ub[j][0]:
						x += 1
		p = x*2.0/(len(ua)+len(ub))
		#return p*s
		return s
	
	def build_graph(self, threshold, doc):
		n = len(doc)
		for i in range(n):
			self._graph[i] = []
		for i in range(n):
			for j in range(n):
				if i == j: continue
				strength = self.__similarity2(doc[i][1:len(doc[i])-1], doc[j][1:len(doc[j])-1]) 
				if strength >= threshold:
					self._graph[i].append((j, strength))
	
	def get_graph(self):
		return self._graph
	
		

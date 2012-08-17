import pythondb
import time


class MyPageRank:

	_graph = {}
	_page_rank = {}

	def __init__(self, graph):
		self._graph = graph
		self._page_rank = {}
		n = len(self._graph)
		for i in self._graph.keys():	
			self._page_rank[i] = 1.0/n
	
	def __compare_ranks(self, pr_a, pr_b, eps):
		#print "---------------Page Rank--------------------"
		#print pr_a
		#print pr_b
		#print "--------------------------------------------"
		err = 0.
		ok = True
		for key in pr_a.keys():
			err += abs(pr_a[key] - pr_b[key])
			if abs(pr_a[key] - pr_b[key]) >= eps:
				ok = False
		#print "Current error: %f"%err
		return ok

	def calculate(self, dump_factor=0.85):
		n = len(self._graph)
		itr = 0
		while True:
			itr += 1
			next_page_rank = {}
			for node in self._graph.keys():
				s = 0
				for neighbor in self._graph[node]:
					s += self._page_rank[neighbor[0]] * \
						self.__norm_factor(node, self._graph[neighbor[0]])
				next_page_rank[node] = (1.0-dump_factor)/n + dump_factor*s
			
			# check if reach convergence.
			if self.__compare_ranks(next_page_rank, self._page_rank, 0.000000001):
				break
			
			self._page_rank = next_page_rank
		
		print itr

	def __norm_factor(self, target, peer):
		weight_sum = 0
		target_weight = 0
		for r in peer:
			weight_sum += r[1]
			if r[0] == target:
				target_weight = r[1]
		return target_weight / weight_sum
	
	def get_rank(self):
		return self._page_rank

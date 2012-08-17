import numpy
import csv
import time
import random
from lib import socialgraph
from lib import pagerank
from lib import search


def generate_life_vector(group_id, mean, sigma, nmembers, path):
	writer = csv.writer(open(path, 'ab'))
	c = 0
	for i in range(nmembers):
		v = numpy.random.multivariate_normal(mean, sigma)
		v = numpy.append(c + group_id*nmembers, v)
		v = numpy.append(v, int(group_id))
		writer.writerow(v)
		c += 1
	print '%d simulate users have been generated...' % c

def generate_engine(nlife_vector, ngroups, nmembers, path):
	for i in range(ngroups):
		mean = [random.uniform(0, 1) for j in range(nlife_vector)]
		sigma = [[0 for j in range(nlife_vector)] for k in range(nlife_vector)]
		for j in range(nlife_vector):
			sigma[j][j] = 0.05
		generate_life_vector(i, mean, sigma, nmembers, path)

def load_data(path):
	reader = csv.reader(open(path, 'rb'))
	doc = []
	for li in reader:
		q = []
		for x in li:
			q.append(eval(x))
		doc.append(q)
	return doc

# An improved version: truncate to 80% of a user's topic explaination.
def sim(va, vb, explain_rate=0.8):
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
	return p*s

def search(q_user, doc, rank, th, p):
	friend = []
	qvector = doc[q_user]
	for li in doc:
		if li == qvector: continue
		if sim(qvector[1:len(qvector)-1], li[1:len(li)-1]) >= th:
			friend.append((int(li[0]), rank[int(li[0])], int(li[len(li)-1])))

	friend.sort(key=lambda o: (o[1], o[0], o[2]), reverse=True)
	#print len(friend)
	#for f in friend:
	#	print f
	return friend[0:p]

if __name__ == '__main__':
	#generate_engine(10, 10, 100, 'sim.csv')
	sg = socialgraph.MyTopicGraph()
	doc = load_data('sim.csv')
	sg.build_graph(0.3, doc)
	graph = sg.get_graph()
	rk = pagerank.MyPageRank(graph)
	rk.calculate()
	rank = rk.get_rank()
	
	hr = [[], [], []]	
	th = 0.05
	while th < 1.:
		for i in range(3):
			f = search(2*100+23, doc, rank, th, 100*(i+1))
			hit = 0
			for u in f:
				if u[2] == 2:
					hit += 1
			hr[i].append(hit)
		print "insert result successfully..."
		
		th += 0.05 
	
	print hr
	
	#hr = [0]*10
	#for cl in range(10):
	#	for i in range(10):
	#		f = search(cl*100+23, doc, rank, 0, 100*(i+1))
	#		hit = 0
	#		for u in f:
	#			if u[2] == cl:
	#				hit += 1
	#		hr[i] += hit
	#print hr
	

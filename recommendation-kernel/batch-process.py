from lib import rawdoc
from lib import corpus
from lib import socialgraph
from lib import index
from lib import topic
from lib import pagerank
from lib import rankfusion
import os
import sys
import time
import getopt


def main(nwords=15, ntopics=10, threshold=0.425):
	rdoc = rawdoc.RawDocuments(nwords)
	rdoc.build_corpus()
	c = corpus.MyCorpus(nwords).read_corpus()
	lda_model = topic.MyTopicModel().lda_model(c, ntopics)
	social_graph = socialgraph.MyTopicGraph(lda_model, c)
	social_graph.build_graph(threshold)
	social_graph.save_graph()
	rindex = index.MyIndexBuilder()
	rindex.build_index()
	page_rank = pagerank.MyPageRank()
	page_rank.calculate()
	rank_fusion = rankfusion.MyRankFusion()
	rank_fusion.fuse()

if __name__ == "__main__":
	main()

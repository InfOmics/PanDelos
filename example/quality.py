#!/usr/bin/python3

import sys

idb = sys.argv[1]
iclus = sys.argv[2]


seq2genome = dict()
seq2descr = dict()
allseqs = set()
allgenomes = set()


l = 0
for line in open(idb, 'r'):
	if (l%2) == 0:
		cc = line.strip().split('\t')
		seq2genome[ cc[1] ] = cc[0]
		seq2descr[ cc[1] ] = cc[2]
		allseqs.add(cc[1])
		allgenomes.add(cc[0])
	l += 1


clusters = list()
cseqs = set()

for line in open(iclus, 'r'):
	ss = line.strip().split(' ')
	if len(ss) > 1:
		for s in ss:
			cseqs.add(s)
	clusters.append( line.strip().split(' ') ) 
#print(len(clusters))



def count_genomes(clu):
	gs = set()
	for s in clu:
		gs.add( seq2genome[s] )
	return len(gs)

def get_descr_set(clu):
	ds = set()
	for s in clu:
		ds.add(seq2descr[s])
	return ds


l_distr = dict()
g_distr = dict()
for c in clusters:
	l_distr[ len(c) ] = l_distr.get(len(c),0) + 1
	if len(c) > 1:
		gc = count_genomes(c)
		g_distr[ gc ] = g_distr.get(gc,0) + 1
		ds = get_descr_set(c)
#		print("#descr#", len(c), gc, len(ds), ds)
#		if gc == len(allgenomes):
#			print("###@@@g")

print("-"*40)
print("nof genomes", len(allgenomes))
print("nof seqs", len(allseqs))
print("nof reads", int(l/2))
print("cluster size distribution")
print("number of sequences for each gene family")
print("-"*20)
print("number of sequences\tnumber of families")
print(1, len( allseqs - cseqs ))
for k in range(2, max( l_distr.keys()) + 1):
	print(k, l_distr.get(k,0) )
print("-"*40)
print("cluster genome-spread distribution")
print("number of involved genomes for each gene family")
print("-"*20)
print("number of genomes\tnumber of families")
print(1, len( allseqs - cseqs ))
for k in range(2, max( g_distr.keys()) + 1):
	print(k, g_distr.get(k,0) )


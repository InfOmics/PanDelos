#!/usr/bin/python3

import sys

idb = sys.argv[1]
iclus = sys.argv[2]


seq2genome = dict()
seq2descr = dict()
allseqs = set()
allgenomes = set()
genomeSeqCount = dict()

l = 0
for line in open(idb, 'r'):
	if (l%2) == 0:
		cc = line.strip().split('\t')
		seq2genome[ cc[1] ] = cc[0]
		seq2descr[ cc[1] ] = cc[2]
		allseqs.add(cc[1])
		allgenomes.add(cc[0])
		genomeSeqCount[cc[0]] = genomeSeqCount.get(cc[0],0) + 1
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

def get_clu_genomes(clu):
	gs = set()
	for s in clu:
		gs.add( seq2genome[s] )
	return gs


def get_descr_set(clu):
	ds = set()
	for s in clu:
		ds.add(seq2descr[s])
	return ds


l_distr = dict()
g_distr = dict()
pg_distr = dict()
for c in clusters:
	l_distr[ len(c) ] = l_distr.get(len(c),0) + 1
	if len(c) > 1:
		gc = count_genomes(c)
		g_distr[ gc ] = g_distr.get(gc,0) + 1
		ds = get_descr_set(c)
#		print("#descr#", len(c), gc, len(ds), ds)
#		if gc == len(allgenomes):
#			print("###@@@g")

for c in clusters:
	gc = count_genomes(c)
	gs = get_clu_genomes(c)
	if gc not in pg_distr:
		pg_distr[gc] = dict()
	for g in gs:
		pg_distr[gc][g] = pg_distr[gc].get(g,0) + 1

print("-"*40)
print("nof genomes", len(allgenomes))
print("nof seqs", len(allseqs))
print("nof reads", int(l/2))
print("-"*20)
print("nof seqs of each genome")
for g in sorted(allgenomes):
	print(g,genomeSeqCount.get(g,0))

print("-"*40)
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


print("-"*40)
print("per genome spread distribution")
print("-"*20)
print("number of genomes\tnumber of families")
print("--",end='\t')
for g in sorted(allgenomes):
	print(g,end='\t')
print('\n', end='')
for k in sorted(pg_distr.keys()):
	print(k, end='\t')
	for g in sorted(allgenomes):
		print(pg_distr[k].get(g,0),end='\t')
	print('\n', end='')

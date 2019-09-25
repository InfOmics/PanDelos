#!/usr/bin/python3


import sys
import networkx as nx
from operator import itemgetter

iseqs = sys.argv[1]
inet = sys.argv[2]

#seq_names = list()
seq_names = dict()  #id -> name
seq_genome = dict()
seq_descr = dict()
genomes = dict()

i = 0
for line in open(iseqs, 'r'):
    if i%2 == 0:
        cols = line.strip().split('\t')
        seq_id= len(seq_names)
#        seq_names.append(cols[1])
        seq_names[seq_id] = cols[1] 
        seq_genome[seq_id] = cols[0]
        seq_descr[seq_id] = cols[2]
        if cols[0] not in genomes:
            genomes[cols[0]] = list()
        genomes[cols[0]].append(seq_id)
    i += 1

print("nof sequences", len(seq_names))
print("nof genomes", len(genomes))


ccheck = set()
for k,v in seq_names.items():
	if v in ccheck:
		print("duplicated seq name", v)
	ccheck.add(v)

inodes = set()
pnet = nx.Graph()
for line in open(inet, 'r'):
    cols = line.strip().split('\t')
    cols[0] = int(cols[0])
    cols[1] = int(cols[1])
    cols[2] = float(cols[2])
    if cols[0] not in inodes:
        inodes.add(cols[0])
        pnet.add_node(cols[0])
    if (cols[1] not in inodes) and (cols[0] != cols[1]):
        inodes.add(cols[1])
        pnet.add_node(cols[1])
    if cols[0] != cols[1]:
        pnet.add_edge(cols[0], cols[1], weight=cols[2])
        pnet.add_edge(cols[1], cols[0], weight=cols[2])

print("nof net nodes", pnet.number_of_nodes())
print("nof net edges", pnet.number_of_edges())


print('-'*40)

comps_size_distr = dict()
comps = nx.algorithms.components.connected_components(pnet)
nof_comps = 0
#print('nof connected components', len(comps))
for comp in comps:
    comps_size_distr[ len(comp) ] = comps_size_distr.get(len(comp),0)+1
    nof_comps += 1
for k,v in sorted(comps_size_distr.items()):
    print(k,v)
print('nof connected components', nof_comps)

print('-'*40)



def get_max_collision(coco, pnet):
    collisions = dict()
    for s in coco:
        if seq_genome[s] not in collisions:
            collisions[ seq_genome[s] ] = list()
        collisions[ seq_genome[s] ].append(s)
    max_k = 0
    for k,v in collisions.items():
        for s1 in v:
            s_k = 0;
            for s2 in v:
                if (s1 != s2) and not(pnet.has_edge(s1,s2) or pnet.has_edge(s2,s1)) :
                    s_k += 1
            if s_k > max_k:
                max_k = s_k
    #if max_k > 0:
    #    print(max_k, len(coco))
    return max_k

def heaviest(G):
    u, v, w = max(G.edges(data='weight'), key=itemgetter(2))
    return (u, v)
def split_until_max_k(coco, pnet):
    snet = pnet.subgraph(coco)
    #gcoms = nx.algorithms.community.centrality.girvan_newman(snet, most_valuable_edge=heaviest)
    gcoms = nx.algorithms.community.centrality.girvan_newman(snet)
    #coms = tuple( c for c in next(gcoms) )
    coms = tuple( sorted(c) for c in next(gcoms) )
    print("gn", coms)
    rcoms = list()
    nof_coms = 0
    for com in coms:
        if get_max_collision(com, snet) > 0:
            rcoms = rcoms + split_until_max_k(com, snet)
        else:
            rcoms.append(com)
    return rcoms

def print_family(fam):
        print(len(fam))
        print("fam", sorted(fam))
        print("F{ ", end='', sep='')
        print(" ; ".join([ seq_names[f] for f in sorted(fam) ] ), end='',sep='')
        #for f in fam:
        #        print(seq_names[f] + " ; ", end='',sep='')
        print('}\n', end='')

def print_family_descriptions(fam):
        print("D{ ", end='', sep='')
        print(" ; ".join([ seq_descr[f] for f in sorted(fam) ] ), end='',sep='')
        #for f in fam:
        #        print(seq_descr[f] + " ; ", end='',sep='')
        print('}\n', end='')
        print("S{ ", end='', sep='')
        print(" ; ".join([ d for d in set( [ seq_descr[f] for f in sorted(fam) ]) ] ), end='',sep='')
        #for f in fam:
        #        print(seq_descr[f] + " ; ", end='',sep='')
        print('}\n', end='')
        #print( set( [ seq_descr[f] for f in fam ]) )
        print('-')



remaining_singletons = set()
for g in seq_names.keys():
	remaining_singletons.add(g)

fnodes = set()
coms_size_distr = dict()
nof_coms = 0
for coco in nx.algorithms.components.connected_components(pnet):
	print('-'*10)
	print("coco", sorted(coco))
	max_k = get_max_collision(coco, pnet)
	if max_k > 0:
		print(max_k, len(coco))
		coms = split_until_max_k(coco, pnet)
		#print("coms", sorted(coms))
		nof_coms += len(coms)
		for com in coms:
			coms_size_distr[ len(com) ] = coms_size_distr.get(len(com),0)+1
			#print("com", sorted(com))
			print_family(com)
			for g in com:
				remaining_singletons.remove(g)
			print_family_descriptions(com)
	else:
		nof_coms += 1
		coms_size_distr[ len(coco) ] = coms_size_distr.get(len(coco),0)+1
		#print("coco", sorted(coco))
		print_family(coco)
		for g in coco:
			remaining_singletons.remove(g)
		print_family_descriptions(coco)

for g in remaining_singletons:
	print("F{ "+seq_names[g]+" }")


for k,v in sorted(coms_size_distr.items()):
    print(k,v)
print('nof communities', nof_coms)
print('-'*40)

#!/usr/bin/python3


import sys
from os import listdir
from os.path import isfile, join
import re
from Bio import SeqIO

genome_length = dict()
genome2cdstag = dict()
cdstag2genome = dict()
cdstag2product = dict()
cdsseqs = dict()


def read_gbk(ifile, genome_id):
    print(genome_id)
    genome_cdslist = genome2cdstag.get(genome_id, list())

    for record in SeqIO.parse(ifile, "genbank"):
        sequence_id = record.id 
        genome_id = record.annotations['accessions'][-1]
        print("\t"+genome_id+"\t"+sequence_id)
        for feature in record.features:
            #print(feature)
            if (feature.type == 'source'):
                genome_length[genome_id] = genome_length.get(genome_id, 0) + feature.location.end
            elif (feature.type == 'CDS'):
                if ('translation' in feature.qualifiers):
                    tag = (genome_id, sequence_id, feature.qualifiers['locus_tag'][0])
                    genome_cdslist.append(tag)
                    cdstag2genome[tag] = genome_id
                    cdsseqs[tag] = feature.qualifiers['translation'][0]
                    cdstag2product[tag] = (feature.qualifiers['product'][0]).replace('\t','')
    genome2cdstag[genome_id] = genome_cdslist

idir = sys.argv[1]
ofile = sys.argv[2]
print("reading gbk files from", idir)

gbkfiles = [f for f in listdir(idir) if isfile(join(idir, f)) and re.match('^.+\.gbk$', f)]
print(gbkfiles)

for gbk in gbkfiles:
	print(gbk)
	read_gbk(idir + gbk, re.sub('\.gbk$', '', gbk))


uniques = dict()

print('writing to', ofile)
with open(ofile, 'w') as off:
	for k in sorted(cdsseqs.keys()):
		gen_id = k[0]+":"+k[1]
		if gen_id not in uniques:
			uniques[ gen_id ] = dict()
		uniques[ gen_id ][k[2]] = uniques[ gen_id ].get(k[2],0) + 1
		cc = uniques[ gen_id ][k[2]]
		acc = k[0]+":"+k[1]+":"+k[2]+":"+str(cc)
		#off.write(k[0]+"\t"+k[1]+"\t"+ cdstag2product[k] +"\n")
		off.write(k[0]+"\t"+ acc +"\t"+ cdstag2product[k] +"\n")
		off.write(cdsseqs[k]+"\n")

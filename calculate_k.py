#!/usr/bin/python3
import sys
import math
ifile = sys.argv[1]
total_length = 0
alphabet = set()
i = 0
for line in open(ifile, 'r'):
	if i%2 != 0:
		total_length += len(line.strip())
		for s in line.strip():
			alphabet.add(s)
	i += 1
print("total length ", total_length)
print("alphabet", alphabet)
print("LG ", math.log(total_length, len(alphabet)))
print("k = ", math.floor(math.log(total_length, len(alphabet))))

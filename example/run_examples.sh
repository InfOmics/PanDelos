#!/bin/bash

list="escherichia salmonella xanthomonas mycoplasma"

for s in $list
do
echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo "running $s 's example ..."
#bash download.sh ${s}.list.txt ${s}
bash ../pandelos.sh ${s}.faa ${s}
#python3 quality.py ${s}.faa ${s}.clus
echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
exit
done


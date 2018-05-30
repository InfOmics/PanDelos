#!/bin/bash
ilist="$1"
prefix="$2"
mkdir -p $prefix
rm $prefix/*.gbk
while read line
do
echo $line
id=`echo $line | sed s/^.*\_NC\_/NC\_/g`
id=`echo $id | sed s/^.*\_NZ\_/NZ\_/g`
echo "efecth for $id"
efetch -db nuccore -id $id -format gbwithparts > ${prefix}/${id}.gbk
done < "$ilist"
python3 gbk2ig.py ${prefix}/ ${prefix}.faa

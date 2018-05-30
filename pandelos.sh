#!/bin/bash
echo "################################################################################"
echo "# PanDelos :                                                                   #"
echo "# a dictionary-based method for pan-genome content discovery.                  #"
echo "#------------------------------------------------------------------------------#"
echo "# Bonnici et. al                                                               #"
echo "#==============================================================================#"
echo "# This software is under MIT license!                                          #"
echo "# Please visit https://github.com/GiugnoLab/PanDelos                           #"
echo "################################################################################"

sdir=`dirname $0`
kk="${sdir}/calculate_k.py"
ig="${sdir}/ig/ig.jar"
nc="${sdir}/netclu_ng.py"

if [ -z "$PANDELOS_PATH"]; then
	echo "environment variable PANDELOS_PATH not set!"
	if [ ! -f "$ig" ]; then
		echo "file ig.jar not found in $sdir !"
		exit
	fi
	if [ ! -f "$nc" ]; then
		echo "file netclu_ng.py not found in $sdir !"
		exit
	fi
fi

idb="$1"
oprefix="$2"

if [ ! -f "$idb" ]; then
	echo "input dataset file not found: $idb !"
	echo "usage is: pandelos.sh dataset.faa out_prefix"
	exit
fi

if [ -z "$oprefix" ]; then
	echo "output prefix not given: $oprefix !"
	echo "usage is: pandelos.sh dataset.faa out_prefix"
	exit
fi


tmp=`mktemp -p ./`
echo "working on $tmp"
dnet="${tmp}.net"
clus="${oprefix}.clus"


echo "calculating k ..."
python3 $kk $idb >$tmp
k=`grep "k =" $tmp | sed s/k\ =\ //g`
echo "k = $k"
echo "clustering ..."
date
#java -server -d64 -Xmn2560M -Xms6144M -Xmx60144M 
java -cp ${sdir}/ext/commons-io-2.6.jar -cp $ig infoasys.cli.pangenes.Pangenes $idb $k $dnet >$tmp
echo "de-clustering ..."
date
python3 $nc $idb  $dnet >>$tmp
echo "writing gene gene families in $clus ..."
date
grep "F{ " $tmp | sed s/F{\ //g | sed s/}//g | sed s/\ \;//g | sort | uniq >$clus
date
rm $tmp


echo "################################################################################"
echo "# PanDelos :                                                                   #"
echo "# a dictionary-based method for pan-genome content discovery.                  #"
echo "#------------------------------------------------------------------------------#"
echo "# Bonnici et. al                                                               #"
echo "#==============================================================================#"
echo "# This software is under MIT license!                                          #"
echo "# Please visit https://github.com/GiugnoLab/PanDelos                           #"
echo "################################################################################"


echo "Finish!"

# PanDelos
PanDelos: a dictionary-based method for pan-genome content discovery

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [](#lang-en)

<hr />

## Software architecture
The PanDelos software is organized in 2 python modules and a Java library that are piped together by a bash script.
The bash script, `pandelos.sh`, provides the acces point the the PanDelos pipeline.

```
bash pandelos.hs <input.faa> <output_prefix>
```

<hr />

### Input format

The complete set of (gene) sequences `<input.faa>`, belonging to any of the studied genomes, must be provided as a text file.


For each sequence, two lines are reported in the file. An identification line that is composed of three parts separated by a tabulaiton character. The parts represent the genome identifiers, the gene identifier and the gene product. 

After the identification line, the complete gene sequence in FASTA amino acid format is reported in a single line. No black lines are admitted between the indetification line and the sequence line, neighter between genes.

A valid file is given by the following example listing 4 genes from 2 genomes:
```
NC_000913	b0001@NC_000913:1	thr operon leader peptide
MKRISTTITTTITITTGNGAG
NC_000913	b0024@NC_000913:1	uncharacterized protein
MCRHSLRSDGAGFYQLAGCEYSFSAIKIAAGGQFLPVICAMAMKSHFFLISVLNRRLTLTAVQGILGRFSLF
NC_002655	Z_RS03160@NC_002655:1	hok/gef family protein
MLTKYALVAVIVLCLTVPGFTLLVGDSLCEFTVKERNIEFRAVLAYEPKK
NC_002655	Z_RS03165@NC_002655:1	protein HokE
MLTKYALVAVIVLCLTVLGFTLLVGDSLCEFTVKERNIEFKAVLAYEPKK
```
> IMPORTANT: make sure the gene identifiers are unique within the input file. Commonly used file formats used to share genome annotaitons do not require that different locus tags of the same gene must be unique.

We suggest to use the following format to build unique gene identifiers:
```
gene_identifier@genome_identifier:unique_integer
```
The fields `gene_identifier` and `genome_identifier` are the same reported in the input file, while the `unique_integer` is used to disitrnghuish multiple copies of the same gene (same gene identifier) wihtin the same genome. The integer starts from 1 and it is incremented according to the order gene are written in the input file.


The examples provided in the `examples` folder generate 4 different dataset files, having the `.faa` extension, which can be consulted.

<hr />

### Output format
The exectution of PanDelos produces an outpu file named `[out_prefix].clus` which reports the gene families retrieved by the software.
Each row of the output file represented a specific gene family retrieved by PanDelos.

<hr />

## Installation
### System requirements
PanDelos can run on any operating system where Bash, Python 3 (or higher) and Java SE RunTime 8 (or higher) have been previously installed.

### Compiling the Java source code
The current repository contains a pre-compiled version of the interal Java labrary needed by PanDelos. The library has been pre-compiled with Java 8, however it can be compiled on the user system by runnig the script `compile.sh` inside the `ig` folder. In case of success, the script replaces the `ig.jar` with a new version. 

Alternatively, you may run the following instruction form the `ig` folder:

```
javac -classpath ext/commons-io-2.6.jar  -sourcepath ./ infoasys/cli/pangenes/Pangenes.java
jar cvf ig.jar infoasys/
```
The compilation requires that the command `javac` and `jar` are located in your stystem folders.

<hr />

## Running the examples
The script `run_examples.sh` inside the `examples` forlder will run the benchmarks that have been used in the scientific article of PanDelos.

```
cd examples
bash run_examples.sh
```

Benchmarks regard 4 datasets from which the pan-genome content has been extracted:
* 7 isolates of the Typhi serotype of Salmonella enterica 
* 14 Xanthomonas campestris
* 10 Escehrichia coli
* 64 Mycoplasma

The identifiers of the isolates are stored in the `.list.txt` files.
Genomes are downloaded from the NCBI repoitories by means of the `download.sh` sciprt in the form of GenBank `gbk` files.
The [efecth](https://www.ncbi.nlm.nih.gov/books/NBK179288/) tool, available via the Entrez E-Utilities toolkit, must be installed in order to download the required genome files from the NCBI database.
The script `gbk2ig.py` merge the gbk files and produces a single benchmark file that is used as input for the PanDelos pipeline.
The script `quality.py` calculates statistics about th eextracted pan-genome content and print them.

<hr />

## Scanning a GBK folder
The script `gbk2ig.py` (located inside the `examples` folder) can also be used to analyse a set of genomes provided in GBK format. The script takes as input the folder where the file are stored (every file with extension `.gbk` is taken into account) and the name of an output file. The resultant file can be used as input for the PanDelos pipeline.

<hr />

## License
PanDelos is distributed under the MIT license. This means that it is free for both academic and commercial use. Note however that some third party components in PanDelos require that you reference certain works in scientific publications.
You are free to link or use PanDelos inside source code of your own program. If do so, please reference (cite) PanDelos and this website. We appreciate bug fixes and would be happy to collaborate for improvements. 
[License](https://raw.githubusercontent.com/GiugnoLab/PanDelos/master/LICENSE)

<hr />

## Citation
If you have used any of the PanDelos project software, please cite the following paper:

     Bonnici, V., Giugno, R., Manca, V.
     PanDelos: a dictionary-based method for pan-genome content discovery
     BMC bioinformatics 19.15 (2018): 437.

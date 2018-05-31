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

### Input format

The complete set of (gene) sequences `<input.faa>`, belonging to any of the studie genomes, must be provided as a text file.
For each sequence, two lines are reported in the file. An identification line that is composed of three parts separated by a tabulaiton character. The parts represent the genome identifiers, the gene identifier and the gene product. After the identification line, the complete gene sequence in FASTA amino acid format is reported in a single line. No black lines are admitted between the indetification line and the sequence line, neighter between genes.
The examples reported in the `examples` folder generate 4 different dataset files, having the `.faa` extension, which can be consulted.

<hr />

## Installation
### System requirements
### Compiling the Java source code

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
The script `gbk2ig.py` merge the gbk files and produces a single benchmark file that is used as input for the PanDelos pipeline.
The script `quality.py` calculates statistics about th eextracted pan-genome content and print them.

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

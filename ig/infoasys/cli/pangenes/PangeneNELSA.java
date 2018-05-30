package infoasys.cli.pangenes;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import infoasys.core.common.CharArraySequence;
import infoasys.core.dictionaries.IELSAIterator;
import infoasys.core.dictionaries.NELSA;

public class PangeneNELSA {

	public CharArraySequence globalSequence;
	
	public Vector<String> sequenceID =  new Vector<String>();
	public Vector<Integer> sequenceLength = new Vector<Integer>();
	public Vector<String> sequenceGenome = new Vector<String>();
	public Vector<String> genomes = new Vector<String>();
	public Vector<Integer> sequence2genome = new Vector<Integer>();
	
	public NELSA nelsa;
	public int[] nelsa2seq;
	public int[] nelsa2genome;
	
	public PangeneNELSA(PangeneIData pid){
		this.sequenceID = pid.sequenceName;
		this.sequenceGenome = pid.sequenceGenome;
		
		int totalLength = 0;
		for(String s : pid.sequences){
			totalLength += s.length();
		}
		totalLength += pid.sequences.size();
		
		char[] globalseq = new char[totalLength];
		int i = 0;
		for(String s : pid.sequences){
			sequenceLength.addElement(s.length());
			for(int j=0; j<s.length(); j++){
				globalseq[i] = s.charAt(j);
				i++;
			}
			//globalseq[i] = CharArraySequence.NULL_CODE;
			globalseq[i] = '@';
			i++;
		}
		
		globalSequence = new CharArraySequence(globalseq);
		
		nelsa = new NELSA(globalSequence, '@');
		
		int[] i2nelsa = new int[nelsa.length()];
		int[] sa = nelsa.sa();
		for(i=0; i<nelsa.length(); i++){
			i2nelsa[ sa[i] ] = i;
		}
		
		nelsa2seq = new int[totalLength];
		
		i = 0;
		for(int si=0; si<pid.sequences.size(); si++){
			String s = pid.sequences.elementAt(si);
			for(int j=0; j<s.length(); j++){
				nelsa2seq[ i2nelsa[i] ] = si;
				i++;
			}
			nelsa2seq[ i2nelsa[i] ] = -1;
			i++;
		}
		
		
		for(String g : this.sequenceGenome){
			if(!this.genomes.contains(g)){
				this.genomes.add(g);
			}
		}
		for(i=0; i<this.sequenceGenome.size(); i++){
			this.sequence2genome.add(this.genomes.indexOf( this.sequenceGenome.get(i) ));
		}
	}
	
	
	
	public Map<Integer, Integer> countSequenceOccurrences(IELSAIterator it){
		HashMap<Integer,Integer> counts = new HashMap<Integer,Integer>();
		for(int i=it.istart(); i<it.iend(); i++){
			counts.put( nelsa2seq[i] ,  counts.getOrDefault(nelsa2seq[i], 0) + 1);
		}
		return counts;
	}
	
	public Map<Integer, Integer> countGenomeOccurrences(IELSAIterator it){
		HashMap<Integer,Integer> counts = new HashMap<Integer,Integer>();
		for(int i=it.istart(); i<it.iend(); i++){
			counts.put( this.sequence2genome.get(nelsa2seq[i]) ,  counts.getOrDefault( this.sequence2genome.get(nelsa2seq[i]), 0) + 1);
		}
		return counts;
	}
}

package infoasys.cli.pangenes;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import infoasys.core.common.CharArraySequence;
import infoasys.core.dictionaries.IELSAIterator;
import infoasys.core.dictionaries.NELSA;

public class SlicedPangeneNELSA {

	public CharArraySequence globalSequence;
	public char concatenationCharacter = '@';
	
	public NELSA nelsa;
	public int[] nelsa2seq;
	
	public Vector<Integer> sequenceLength = new Vector<Integer>();
	
	public SlicedPangeneNELSA(PangeneIData pid, Vector<Integer> allowedSequences){
		int totalLength = allowedSequences.size();
		for(Integer i : allowedSequences){
			totalLength += pid.sequences.get(i).length();
		}
		
		char[] globalseq = new char[totalLength];
		String s;
		int i = 0;
		for(Integer si : allowedSequences){
			s = pid.sequences.get(si);
			sequenceLength.add(s.length());
			for(int j=0; j<s.length(); j++){
				globalseq[i] = s.charAt(j);
				i++;
			}
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
		for(Integer si : allowedSequences){
			s = pid.sequences.get(si);
			for(int j=0; j<s.length(); j++){
				nelsa2seq[ i2nelsa[i] ] = si;
				i++;
			}
			nelsa2seq[ i2nelsa[i] ] = -1;
			i++;
		}
	}
	
	
	public Map<Integer, Integer> countSequenceOccurrences(IELSAIterator it){
		HashMap<Integer,Integer> counts = new HashMap<Integer,Integer>();
		for(int i=it.istart(); i<it.iend(); i++){
			counts.put( nelsa2seq[i] ,  counts.getOrDefault(nelsa2seq[i], 0) + 1);
		}
		return counts;
	}
}

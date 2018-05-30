package infoasys.cli.pangenes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import infoasys.core.common.CharArraySequence;

public class PangeneIData {

	public Vector<String> sequences = new Vector<String>();
	public Vector<String> sequenceName =  new Vector<String>();
	public Vector<String> sequenceDescription = new Vector<String>();
	
	public Vector<Integer> sequenceGenome = new Vector<Integer>();
	public Vector<String> genomeNames = new Vector<String>();
	

	public PangeneIData(){
	}
	
	public Set<Character> getAlphabet(){
		Set<Character> alphabet = new TreeSet<Character>();
		for(String s : this.sequences){
			for(int i=0; i<s.length(); i++){
				alphabet.add(s.charAt(i));
			}
		}
		return alphabet;
	}
	
	
	public static PangeneIData readFromFile(String file) throws Exception{
		PangeneIData d = new PangeneIData();
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		boolean nameLine = true;
		String seqName=null, genomeName=null, product=null, seq;
		String[] cc;
		Map<String,Integer> genomeID = new HashMap<String,Integer>();
		Integer genomeid;
	    while ((line = br.readLine()) != null) {
	       // process the line.
	    	if(nameLine){
	    		cc = line.trim().split("\t");
	    		genomeName = cc[0];
	    		seqName = cc[1];
	    		product = cc[2];
	    	}
	    	else{
	    		seq = line.trim();
	    		d.sequences.add(seq);
	    		d.sequenceName.add(seqName);
	    		genomeid = genomeID.get(genomeName);
	    		if(genomeid == null){
	    			genomeid = genomeID.size();
	    			genomeID.put(genomeName, genomeid);
	    		}
	    		d.sequenceGenome.add(genomeid);
	    		d.sequenceDescription.add(product);
	    	}
	    	nameLine = !nameLine;
	    }
	    
	    d.genomeNames = new Vector<String>();
	    d.genomeNames.setSize(genomeID.size());
	    for(Map.Entry<String,Integer> en : genomeID.entrySet()){
	    	d.genomeNames.set(en.getValue(), en.getKey());
	    }
		
		return d;
	}
	
	public Map<String,Integer> getNamedGenomeLengths(){
		Map<String,Integer> lengths = new HashMap<String,Integer>();
		for(int i=0; i<this.sequences.size(); i++){
			lengths.put( this.genomeNames.get(i),  lengths.getOrDefault(this.genomeNames.get(i), 0) + this.sequences.get(i).length());
		}
		return lengths;
	}
	public Map<Integer,Integer> getGenomeLengths(){
		Map<Integer,Integer> lengths = new HashMap<Integer,Integer>();
		for(int i=0; i<this.sequences.size(); i++){
			lengths.put( this.sequenceGenome.get(i),  lengths.getOrDefault(this.sequenceGenome.get(i), 0) + this.sequences.get(i).length());
		}
		return lengths;
	}
	public Map<Integer,Integer> getGenomeKLengths(int k){
		Map<Integer,Integer> lengths = new HashMap<Integer,Integer>();
		for(int i=0; i<this.sequences.size(); i++){
			lengths.put( this.sequenceGenome.get(i),  lengths.getOrDefault(this.sequenceGenome.get(i), 0) + this.sequences.get(i).length() -k +1);
		}
		return lengths;
	}
	
	public Map<Integer, Vector<Integer> > getGenomeSets(){
		Map<Integer, Vector<Integer> > genomes = new HashMap<Integer, Vector<Integer> >();
		for(int g=0; g<genomeNames.size(); g++){
			genomes.put(g,  new Vector<Integer>());
		}
		for(int i=0; i<sequenceGenome.size(); i++){
			genomes.get( sequenceGenome.get(i) ).add(i);
		}
		return genomes;
	}
}

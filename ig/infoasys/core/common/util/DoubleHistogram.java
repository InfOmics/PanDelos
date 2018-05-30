package infoasys.core.common.util;

import java.util.Map;
import java.util.TreeMap;

public class DoubleHistogram {
	
	public double min;
	public double max;
	public int numberOfBins;
	
	public double factor;
	
	public Map<Integer,Integer> counts;
	public int lessMinCount = 0;
	public int greaterMaxCount = 0;
	
	public DoubleHistogram(double min, double max, int numberOfBins){
		this.min = min;
		this.max = max;
		this.numberOfBins = numberOfBins;
		this.factor = (max - min) / ((double)numberOfBins);
		counts = new TreeMap<Integer,Integer>();
		
		int i=0;
		for(double k = min; k<=max; k+=factor, i++){
			counts.put((int)i, 0);
		}
		counts.put((int)i, 0);
	}
	
	public void add(double v){
		if(v>=min && v<=max){
			int k = (int)Math.floor( v / factor);
			counts.put(k, counts.get(k) + 1);
			
		}else if(v< min){
			lessMinCount++;
		}
		else{
			greaterMaxCount++;
		}
	}
	
	@Override
	public String toString(){
		String s = "";
		s += "<"+this.min+" "+this.lessMinCount+"\n";
		int i=0;
		double k = this.min;
		for( ; k<=this.max; k+=this.factor, i++){
			s += k+" "+this.counts.get(i)+"\n";
		}
		s += k+" "+this.counts.get(i)+"\n";
		s += ">"+this.max+" "+this.greaterMaxCount+"\n";
		return s;
	}
	
}

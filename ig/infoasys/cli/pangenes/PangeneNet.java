package infoasys.cli.pangenes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class PangeneNet {
	
	public static class Edge implements Comparable<Edge>{
		public int node;
		public double score;
		public Edge(int node, double score){
			this.node = node;
			this.score = score;
		}
		@Override
		public int compareTo(Edge o) {
			return this.node - o.node;
		}
		@Override
		public int hashCode(){
			return this.node;
		}
		
	}
	
	public Map<Integer, Set<Edge> > adjacents;
	
	
	public PangeneNet(){
		this.adjacents = new HashMap<Integer, Set<Edge>>();
	}

	public void addConnection(int src, int dest, double score){
		if(! this.adjacents.containsKey(src)){
			Set<Edge> edges = new TreeSet<Edge>(); 
			edges.add( new Edge(dest, score));
			this.adjacents.put(src,  edges);
		}
		else{
			this.adjacents.get(src).add(new Edge(dest, score));
		}
	}
	
	/*public void addNode(int n){
		if(! this.adjacents.containsKey(n)){
			Set<Edge> edges = new TreeSet<Edge>(); 
			this.adjacents.put(n,  edges);
		}
	}*/
	
	
	public Set<Integer> getNodeList(){
		Set<Integer> nodes = new HashSet<Integer>();
		for(Map.Entry<Integer, Set<Edge>> adjs_e : this.adjacents.entrySet()){
			nodes.add(adjs_e.getKey());
			for(Edge i : adjs_e.getValue())
				nodes.add(i.node);
		}
		return nodes;
	}
	
	
	public long countNodes(){
		return this.getNodeList().size();
	}
	public long countEdges(){
		long count = 0;
		for(Map.Entry<Integer, Set<Edge>> adjs_e : this.adjacents.entrySet()){
			count +=  adjs_e.getValue().size();
		}
		return count;
	}
	
	
	public PangeneNet getUnirected(){
		PangeneNet pnet = new PangeneNet();
		
		for(Map.Entry<Integer, Set<Edge>> adjs_e : this.adjacents.entrySet()){
			for(Edge i : adjs_e.getValue()){
				pnet.addConnection(adjs_e.getKey(), i.node, i.score);
				pnet.addConnection(i.node , adjs_e.getKey(), i.score);
			}
		}
		
		return pnet;
	}
	
	
	public List<Set<Integer>> undirectedConnectedComponets(){
		PangeneNet pnet = this.getUnirected();
		return pnet.connectedComponets();
	}
	
	public List<Set<Integer>> connectedComponets(){
		List<Set<Integer>> coco = new LinkedList<Set<Integer>>();
		
		Set<Integer> nodes = this.getNodeList();
		HashMap<Integer,Boolean> visited = new HashMap<Integer,Boolean>();
		for(Integer n : nodes){
			visited.put(n,  false);
		}
		
		for(Integer root : nodes){
			if(! visited.get(root) ){
				List<Integer> co = new LinkedList<Integer>();
				co.add(root);
				visited.put(root, true);
				
				Set<Integer> coconodes= new HashSet<Integer>();
				coconodes.add(root);
				
				for(int i=0; i<co.size(); i++){
					 visited.put(co.get(i), true);
					if(this.adjacents.containsKey( co.get(i) )){
						for(Edge ed : this.adjacents.get( co.get(i) )){
							if(!coconodes.contains(ed.node)){
								co.add(ed.node);
								coconodes.add(ed.node);
							}
							//if(!co.contains(ed.node)){
							//	co.add(ed.node);
							//}
						}
					}
				}
				
				coco.add(new HashSet<Integer>(co));
			}
		}
		
		return coco;
	}
	
	
	void printDegreeDistribution(){
		Map<Integer,Integer> distr = new TreeMap<Integer,Integer>();
		for(Map.Entry<Integer, Set<Edge>> adjs_e : this.adjacents.entrySet()){
			distr.put( adjs_e.getValue().size(),  distr.getOrDefault(adjs_e.getValue().size(), 0) + 1 );
		}
		for(Map.Entry<Integer,Integer> en : distr.entrySet()){
			System.out.println(en.getKey()+"\t"+en.getValue());
		}
	}
	void printUndirectedDegreeDistribution(){
		PangeneNet pnet = this.getUnirected();
		pnet.printDegreeDistribution();
	}
	
	
	void print(){
		for(Map.Entry<Integer, Set<Edge>> adjs_e : this.adjacents.entrySet()){
			System.out.print(adjs_e.getKey()+": ");
			for(Edge ed : adjs_e.getValue()){
				System.out.print("("+ed.node+","+ed.score+")");
			}
			System.out.print("\n");
		}
	}
	
	void saveToFile(String file, boolean directed) throws Exception{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file)));
		if(directed){
			for(Map.Entry<Integer, Set<Edge>> adjs_e : this.adjacents.entrySet()){
				for(Edge ed : adjs_e.getValue()){
					writer.write(adjs_e.getKey()+"\t"+ed.node+"\t"+ed.score+"\n");
				}
			}
		}
		else{
			for(Map.Entry<Integer, Set<Edge>> adjs_e : this.adjacents.entrySet()){
				for(Edge ed : adjs_e.getValue()){
					if(adjs_e.getKey() <= ed.node){
						writer.write(adjs_e.getKey()+"\t"+ed.node+"\t"+ed.score+"\n");
					}
				}
			}
		}
		
		writer.flush();
		writer.close();
	}
	
}

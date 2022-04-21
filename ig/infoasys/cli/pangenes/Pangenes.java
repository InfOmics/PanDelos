package infoasys.cli.pangenes;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import infoasys.core.dictionaries.IELSAIterator;

public class Pangenes {

	public static void usage(){
		System.err.println("Usage: cmd ifile.faa k ofile.net");
	}
	public static void main(String[] args){
		
		String ifile = null;
		int k = 1;
		String netofile = null;
		
		try{
			ifile = args[0];
			k = Integer.parseInt(args[1]);
			netofile = args[2];
		}catch(Exception e){
			usage();
			System.exit(1);
		}
		
		double kk = (double)k;
		
		
		try{
			PangeneIData pid = PangeneIData.readFromFile(ifile);
			Map<Integer, Vector<Integer> > genomeSets = pid.getGenomeSets();
			
			
			Map<Integer,Integer> genomeLengths = pid.getGenomeLengths();
			Map<Integer,Integer> genomeKLengths = pid.getGenomeKLengths(k);
			int nofGenomes = genomeLengths.size();
			
			PangeneNet pnet = new PangeneNet();
			
			int[] mapGlobal2Local = new int[ pid.sequences.size() ];
			int[] mapLocal2Global = new int[ pid.sequences.size() ];
			
			
			for(int g1=0; g1<nofGenomes; g1++){
				//for(int g2=g1; g2<pid.genomeNames.size(); g2++){
				for(int g2=g1+1; g2<nofGenomes; g2++){
					//System.out.println("##\t"+g1+"\t"+g2);
					
					Vector<Integer> allowedSequences = new Vector<Integer>();
					allowedSequences.addAll(genomeSets.get(g1));
					if(g1 != g2) allowedSequences.addAll(genomeSets.get(g2));
					
					Arrays.fill(mapGlobal2Local, -1);
					Arrays.fill(mapLocal2Global, -1);
					for(int i=0; i<allowedSequences.size(); i++){
						mapGlobal2Local[ allowedSequences.get(i) ] = i;
						mapLocal2Global[i] =  allowedSequences.get(i);
					}
					
					SlicedPangeneNELSA pnelsa = new SlicedPangeneNELSA(pid, allowedSequences);
					
					int g1_size = genomeSets.get(g1).size();
					int g2_size = genomeSets.get(g2).size();
					
					double[][] mins =  new double[g1_size + g2_size][g1_size + g2_size];
					double[][] maxs =  new double[g1_size + g2_size][g1_size + g2_size];
					double[][] percs  =  new double[g1_size + g2_size][g1_size + g2_size];
					
					IELSAIterator it = pnelsa.nelsa.begin(k);
					int k1,k2;
					while(it.next()){
						Map<Integer,Integer> counts = pnelsa.countSequenceOccurrences(it);
						if(counts.size() > 1){
							for(Map.Entry<Integer, Integer> e1 : counts.entrySet()){
								for(Map.Entry<Integer, Integer> e2 : counts.entrySet()){
									if(e1.getKey() != e2.getKey()){
										if(e1.getValue() >= e2.getValue()){
											maxs[ mapGlobal2Local[e1.getKey()]  ][ mapGlobal2Local[e2.getKey()] ] += e1.getValue();
											mins[ mapGlobal2Local[e1.getKey()]  ][ mapGlobal2Local[e2.getKey()] ] += e2.getValue();
										}
										else{
											maxs[ mapGlobal2Local[e1.getKey()]  ][ mapGlobal2Local[e2.getKey()] ] += e2.getValue();
											mins[ mapGlobal2Local[e1.getKey()]  ][ mapGlobal2Local[e2.getKey()] ] += e1.getValue();
										}
										
										percs[ mapGlobal2Local[e1.getKey()] ][ mapGlobal2Local[e2.getKey()] ] += e1.getValue();
									}
								}
							}
						}
					}
						
					double[][] scores =  new double[g1_size + g2_size][g1_size + g2_size];
					
					
					double li,lj, lsum;
					for(int i=0; i<scores.length; i++){
						li = (double)(pnelsa.sequenceLength.get(i) - k + 1);
						for(int j=0; j<scores.length; j++){
							lj = (double)(pnelsa.sequenceLength.get(j) - k + 1);
							if(mins[i][j] > 0.0){
								lsum = maxs[i][j] + mins[i][j];
								scores[i][j] = ( mins[i][j]) / (maxs[i][j] + (li + lj - lsum));
								percs[i][j] = percs[i][j] / (li);
							}
						}
					}
					
					for(int i=0; i<scores.length; i++){
						li = (double)(pnelsa.sequenceLength.get(i) - k + 1);
						for(int j=0; j<scores.length; j++){
							lj = (double)(pnelsa.sequenceLength.get(j) - k + 1);
							
							if(percs[i][j] < ( 1.0/(2.0*kk) ) && percs[j][i] < ( 1.0/(2.0*kk) )){
								scores[i][j] = 0.0;
							}
						}
					}
					
					
					if(g1 == g2){
						/* connect identical sequences of same genome */
						for(int i=0; i<g1_size; i++){
							for(int j=i+1; j<g1_size; j++){
								if(scores[i][j] == 1.0){
									pnet.addConnection( mapLocal2Global[i] , mapLocal2Global[j], scores[i][j]);
								}
							}
						}
					}
					else{
						//0x
						//x0
						double[] max_intra_score = new double[scores.length];
						Arrays.fill(max_intra_score, 0.0);
						
						double[] max_inter_score = new double[scores.length];
						Arrays.fill(max_inter_score, 0.0);
						
						
						//-x
						//x0
						for(int i=0; i<genomeSets.get(g1).size(); i++){
							for(int j=0; j<genomeSets.get(g1).size(); j++){
								if(i!=j){
									if(scores[i][j] > max_intra_score[i]){
										max_intra_score[i] = scores[i][j];
									}
								}
							}
						}
						//0x
						//x-
						for(int i=genomeSets.get(g1).size(); i<scores.length; i++){
							for(int j=genomeSets.get(g1).size(); j<scores.length; j++){
								if(i!=j){
									if(scores[i][j] > max_intra_score[i]){
										max_intra_score[i] = scores[i][j];
									}
								}
							}
						}
						//0-
						//x0
						for(int i=0; i<genomeSets.get(g1).size(); i++){
							for(int j=genomeSets.get(g1).size(); j<scores.length; j++){
								if(scores[i][j] > max_inter_score[i]){
									max_inter_score[i] = scores[i][j];
								}
							}
						}
						//0x
						//-0
						for(int i=genomeSets.get(g1).size(); i<scores.length; i++){
							for(int j=0; j<genomeSets.get(g1).size(); j++){
								if(scores[i][j] > max_inter_score[i]){
									max_inter_score[i] = scores[i][j];
								}
							}
						}
						
						boolean[] engagged = new boolean[scores.length];
						Arrays.fill(engagged, false);
						
						/* get inter bbh */
						/* also, calcolate threshold for inter non-bbh as the average non-null and non-bbh scores*/
						double inter_thr_sum = 0.0;
						double inter_thr_count = 0.0;
						double inter_max_score = 0.0;
						double inter_min_score = 1.0;
						
						double inter_max_perc = 0.0;
						double inter_min_perc = 1.0;
						
						//0-
						//x0
						for(int i=0; i<genomeSets.get(g1).size(); i++){
							if(max_inter_score[i] > 0.0){
								for(int j=genomeSets.get(g1).size(); j<scores.length; j++){
									if(max_inter_score[j] > 0.0){
										if((scores[i][j] == max_inter_score[i])  &&  (scores[j][i] == max_inter_score[j])){
											pnet.addConnection( mapLocal2Global[i] , mapLocal2Global[j], scores[i][j]);
											pnet.addConnection( mapLocal2Global[j] , mapLocal2Global[i], scores[j][i]);
											engagged[i] = true;
											engagged[j] = true;
											
											inter_thr_sum += scores[i][j] + scores[j][i];
											inter_thr_count += 2;
											if(scores[i][j]<1.0 && scores[i][j] > inter_max_score){
												inter_max_score = scores[i][j]; 
											}
											else if(scores[j][i]<1.0 && scores[j][i] > inter_max_score){
												inter_max_score = scores[j][i]; 
											}
											
											if(scores[i][j]>0.0 && scores[i][j] < inter_min_score){
												inter_min_score = scores[i][j]; 
											}
											else if(scores[j][i]>0.0 && scores[j][i] < inter_min_score){
												inter_min_score = scores[j][i]; 
											}
											
											if(percs[i][j] > inter_max_perc) inter_max_perc=  percs[i][j];
											if(percs[j][i] > inter_max_perc) inter_max_perc=  percs[j][i];
											if(percs[i][j] < inter_min_perc) inter_min_perc=  percs[i][j];
											if(percs[j][i] < inter_min_perc) inter_min_perc=  percs[j][i];
										}
									}
								}
							}
						}
						
						
						/* calcolate threshold for inter non-bbh */
						double inter_thr = inter_thr_sum / inter_thr_count; 
						
						//System.out.println("score\t"+inter_thr +"\t"+ inter_min_score +"\t"+ inter_max_score);
						//System.out.println("perc\t"+inter_min_perc +"\t"+ inter_max_perc);
						//System.out.println(pnet.countNodes()+"\t"+pnet.countEdges());
						
						
						/* get identical paralogs */
						//-x
						//x0
						for(int i=0; i<genomeSets.get(g1).size(); i++){
							if(engagged[i]){
								for(int j=i+1; j<genomeSets.get(g1).size(); j++){
									if(scores[i][j] == 1.0){
										pnet.addConnection( mapLocal2Global[i] , mapLocal2Global[j], scores[i][j]);
									}
								}
							}
						}
						//0x
						//x-
						for(int i=genomeSets.get(g1).size(); i<scores.length; i++){
							if(engagged[i]){
								for(int j=i+1; j<scores.length; j++){
									if(scores[i][j] == 1.0){
										pnet.addConnection( mapLocal2Global[i] , mapLocal2Global[j], scores[i][j]);
									}
								}
							}
						}
						
						
						/* get paralog / intra bbh, filtered by inter max scores */
						//-x
						//x0
						for(int i=0; i<genomeSets.get(g1).size(); i++){
							if(engagged[i]){
								for(int j=i+1; j<genomeSets.get(g1).size(); j++){
									if(scores[i][j]>0.0 && scores[i][j] == max_intra_score[i] && scores[i][j] == max_intra_score[j] && scores[i][j] >= inter_max_score){
										pnet.addConnection( mapLocal2Global[i] , mapLocal2Global[j], scores[i][j]);
									}
								}
							}
						}
						//0x
						//x-
						for(int i=genomeSets.get(g1).size(); i<scores.length; i++){
							if(engagged[i]){
								for(int j=genomeSets.get(g1).size(); j<scores.length; j++){
									if(scores[i][j]>0.0 && scores[i][j] == max_intra_score[i] && scores[i][j] == max_intra_score[j] && scores[i][j] >= inter_max_score){
										pnet.addConnection( mapLocal2Global[i] , mapLocal2Global[j], scores[i][j]);
									}
								}
							}
						}
						
						
						//System.out.println(pnet.countNodes()+"\t"+pnet.countEdges());
					}
					
				}
			}

			
			System.out.println("----------");
			System.out.println("undirected degree distribution");
			pnet.printUndirectedDegreeDistribution();
			
			System.out.println("----------");
			System.out.println("directed degree distribution");
			pnet.printDegreeDistribution();
			
			System.out.println("----------");
			Map<Integer,Integer> cocodistr = new TreeMap<Integer,Integer>();
			List<Set<Integer>> cocos = pnet.undirectedConnectedComponets();
			for(Set<Integer> coco : cocos){
				cocodistr.put(coco.size(), cocodistr.getOrDefault(coco.size(), 0) + 1);
			}
			System.out.println("----------");
			System.out.println("CoCo sizes");
			for(Map.Entry<Integer,Integer> en : cocodistr.entrySet()){
				System.out.println(en.getKey()+"\t"+en.getValue());
			}
			
			
			
			System.out.println("----------");
			System.out.println("writing into "+netofile+"");
			pnet.saveToFile(netofile, false);
			
			
			
			
		}catch(Exception e){
			System.err.println(e);
			e.printStackTrace(System.err);
		}
	}
	
	
	
	
	
}

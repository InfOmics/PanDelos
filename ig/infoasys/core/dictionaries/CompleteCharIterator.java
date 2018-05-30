package infoasys.core.dictionaries;

public class CompleteCharIterator implements IWordIterator{

	private int k;
	private char alpha_size;
	private char[] kmer;
	
	private int p = -1;
	
	public CompleteCharIterator(int k, char alpha_size){
		this.k = k;
		this.alpha_size = alpha_size;
		kmer = new char[k];
		for(int i=0;i<k;i++){
			kmer[i] = 0;
		}
	}
	
	@Override
	public int k() {
		return k;
	}

	@Override
	public char[] kmer() {
		return kmer;
	}

	@Override
	public void kmer(char[] ns) {
		for(int i=0; i<ns.length; i++){
			ns[i] = kmer[i];
		}
	}

	@Override
	public int multiplicity() {
		return 1;
	}

	@Override
	public boolean next() {
		if(p == -1){
			p++;
			return p<k;
		}
		else{
			p = k-1;
			while(p > -1){
				if(kmer[p] == alpha_size){
					kmer[p] = 0;
					p--;
				}
				else{
					kmer[p]++;
					return true;
				}
			}
			if(p == -1){
//				p = 0;
				return false;
			}
			return true;
		}
	}

	@Override
	public boolean prev() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}

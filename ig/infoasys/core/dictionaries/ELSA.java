package infoasys.core.dictionaries;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

import infoasys.core.common.CharArraySequence;
import infoasys.core.common.versioning.DataVersioning;

public class ELSA implements IELSA{
	public static final long serialVersionUID = DataVersioning.ELSA |	0x0000000000000001L;
	
	CharArraySequence cseq = null;
	//protected char[] s = null;
	protected int r_limit;
	protected int s_length;
	
	protected int[] sa;
	protected int[] lcp;
	
	public ELSA(CharArraySequence cseq){
		init(cseq);
	}
	
	protected void init(CharArraySequence cseq){
		this.cseq = cseq;
		r_limit = cseq.length()-1;
		s_length = cseq.length();
		char maxValue = cseq.alphabetSize();
		sa = ByteSAIS.buildSuffixArray(cseq.data, 0, cseq.length(), maxValue);
		lcp = ByteSAIS.computeLCP(cseq.data, 0, cseq.length(), sa);
	}
	
	public CharArraySequence charArraySequence(){
		return this.cseq;
	}
	
	public ELSA(){
		cseq = null;
		r_limit = 0;
		s_length = 0;
		sa = null;
		lcp = null;
	}
	
//	public void setSequence(char[] sequence){
//		this.s = sequence;
//	}
	
	public void setSequence(CharArraySequence cseq){
		init(cseq);
	}

	public int length() {
		return s_length;
	}
	public CharArraySequence seq() {
		return cseq;
	}
	public int[] sa() {
		return sa;
	}
	public int[] lcp() {
		return lcp;
	}

	
	
	
	
	
	
	private class _Iterator implements IELSAIterator{
		private int k;
		protected int istart;//inclusive
		protected int iend;//exclusive
		protected int limit;
        
		_Iterator(int _k, int _istart, int _iend){
			k = _k;
			limit = s_length - k;
			istart = _istart;
			iend = _iend;
		}
		@Override
		public int istart() {
			return istart;
		}
		@Override
		public int iend() {
			return iend;
		}
		@Override
		public char[] kmer() {
			return Arrays.copyOfRange(cseq.data, sa[istart], sa[istart]+k);
		}
        @Override
		public void kmer(char[] ns) {
            for(int i=0; i<ns.length; i++){
            	ns[i] = cseq.data[sa[istart]+i];
            }
		}
		@Override
		public int multiplicity() {
			return iend - istart;
		}
		@Override
		public int[] positions() {
			return Arrays.copyOfRange(sa, istart, iend);
		}
		@Override
		public int[] sortedPositions() {
			int[] pos =  Arrays.copyOfRange(sa, istart, iend);
			Arrays.sort(pos);
			return pos;
		}
		
		@Override
		public boolean next() {
			int l = iend;
			while(l<sa.length && (sa[l] > limit)){
				l++;
			}
			
			if(l >= sa.length){
				istart = -1;
				iend = 0;
				return false;
			}
			
			istart = l;
			l++;
			while(l<sa.length && lcp[l] >= k && sa[l] < limit){
				l++;
			}
			iend = l;
			return true;
		}
		@Override
		public boolean prev() {
			int l = istart;
			if(l == sa.length)
				l = sa.length-1;
			while(l>=0 && sa[l]>limit)
				l--;
			
			if(l<0){
				istart = -1;
				iend = 0;
				return false;
			}
			
			iend = l+1;
			while(l>=0 && lcp[l]>=k && sa[l]<limit){
				l--;
			}
			istart = l;
			return true;
		}
        @Override
        public IELSAIterator clone() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        @Override
        public int compare(IELSAIterator it) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        @Override
        public int k() {
            return k;
        }
        @Override
        public ELSA elsa() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
	}
	
	
	
	
	
	public IELSAIterator begin(int k) {
		_Iterator it = new _Iterator(k, -1, 0);
		return it;
	}

	public void load(String file) throws Exception {
		DataInputStream reader = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		long version = reader.readLong();
		if(version !=  ELSA.serialVersionUID){
			reader.close();
			throw new Exception("Wrong ELSA file version. \nExpected: "+ Long.toBinaryString(ELSA.serialVersionUID) +"\nfound: \n"+Long.toBinaryString(version));
		}
		s_length = reader.readInt();
		r_limit = s_length-1;
		sa = new int[s_length];
		lcp = new int[s_length];
		for(int i=0; i<s_length; i++){
			sa[i] = reader.readInt();
			lcp[i] = reader.readInt();
		}
		reader.close();
	}

	public void save(String file) throws Exception {
		DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file,false)));
		writer.writeLong(ELSA.serialVersionUID);
		writer.writeInt(s_length);
		for(int i=0; i<s_length; i++){
			writer.writeInt(sa[i]);
			writer.writeInt(lcp[i]);
		}
		writer.flush();
		writer.close();
	}

//	@Override
//	public void print(int k) {
//		System.out.println("i\tSA\tLCP\tsuff");
//		for(int i=0; i< sa.length; i++){
//			System.out.print(i +"\t"+ sa[i] +"\t"+ lcp[i] +"\t");
//			  
//			int nn = s.length() - sa[i] < k ? s.length() - sa[i] : k;
//			B3Nucleotide[] suff = new B3Nucleotide[nn];
//			s.getB3(sa[i], suff);
//			for(int j=0; j<suff.length; j++)
//				System.out.print(suff[j]);  
//			System.out.println();
//		}
//	}
}

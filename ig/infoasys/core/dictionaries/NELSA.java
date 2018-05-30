package infoasys.core.dictionaries;

import infoasys.core.common.CharArraySequence;
import infoasys.core.common.versioning.DataVersioning;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

public class NELSA extends ELSA implements INELSA{
	public static final long p_serialVersionUID = DataVersioning.NELSA |	0x0000000000000001L;
	public static final long serialVersionUID = DataVersioning.NELSA |	0x0000000000000002L;

	protected int[] ns;
	protected char nullChar = CharArraySequence.NULL_CODE;

	public NELSA(){
		super();
		ns = null;
	}

	public NELSA(CharArraySequence cseq, Character nullChar){
		init(cseq, nullChar);
	}
	
	public NELSA(CharArraySequence cseq){
		init(cseq, null);
	}

	protected void init(CharArraySequence cseq, Character nullChar){
		super.init(cseq);
		if(nullChar != null)
			this.nullChar = nullChar;
		ns = computeNS(cseq, cseq.encode(this.nullChar) , sa);
	}


	//	/**
	//	 * ns[i] > 0 ever
	//	 * if seq[sa[i]]==N  =>  ns[i] = 1
	//	 * if seq[sa[i]...sa[i]+1]==AN  =>  ns[i] = 2
	//	 */
	//new version
	/**
	 * ns[i] >= 0
	 * if seq[sa[i]]==N  =>  ns[i] = 0
	 * if seq[sa[i]...sa[i]+1]==AN  =>  ns[i] = 1
	 */
	public int[] ns(){
		return ns;
	}


	private static int[] computeNS(CharArraySequence cseq, char nullChar, int[] sa){
		char[] input = cseq.data;

		int[] nn = new int[input.length];

		int pn = input.length;
		for(int i=input.length - 1; i>=0; i--){
			if(input[i] == nullChar){
				nn[i] = 0;
				pn = i;
			}
			else{
				nn[i] = pn - i;
			}
		}

		int[] fn = new int[input.length];
		for(int i=0; i<fn.length; i++){
			//fn[i] = nn[sa[i]] + 1;
			fn[i] = nn[sa[i]];//new version
		}
		return fn;
	}

	private class _Iterator implements IELSAIterator{
		private NELSA nelsa;
		private int k;
		protected int istart;//inclusive
		protected int iend;//exclusive
		protected int limit;
		_Iterator(NELSA _nelsa, int _k, int _istart, int _iend){
			nelsa = _nelsa;
			k = _k;
			limit = s_length - k;
			istart = _istart;
			iend = _iend;
		}
		@Override
		public int k() {
			return k;
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
			while(l<sa.length && (ns[l]<k || sa[l]>limit)){//new version
				l++;
			}
			if(l >= sa.length){
				istart = -1;
				iend = 0;
				return false;
			}
			istart = l;
			l++;
			while(l<sa.length && lcp[l]>=k && sa[l]<limit && ns[l]>=k){//new version
				l++;
			}
			iend = l;
			return true;
		}
		@Override
		public boolean prev() {
			int l = istart;
			l--;
			if(l == sa.length)
				l = sa.length-1;
			while(l>=0 && (ns[l]<k || sa[l]>limit))//new version
				l--;
			if(l<0){
				istart = -1;
				iend = 0;
				return false;
			}
			while(l>=0 && lcp[l]>=k && sa[l]<limit && ns[l]>=k){//new version
				l--;
			}
			iend = istart;
			istart = l;
			return true;
		}
		@Override
		public IELSA elsa() {
			return nelsa;
		}
		@Override
		public IELSAIterator clone() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not supported yet.");
		}
		@Override
		public int compare(IELSAIterator it) {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}


	/*
	 * IELSAIterator it = nelsa.begin(k);
		while(it.next()){
		}
	 */
	@Override
	public IELSAIterator begin(int k) {
		_Iterator it = new _Iterator(this, k, -1, 0);
		return it;
	}

	public int nof(int k) {
		int nof = 0;
		IELSAIterator it = begin(k);
		while(it.next()){
			nof++;
		}
		return nof;
	}

	public int nof_mults(int k) {
		int nof = 0;
		IELSAIterator it = begin(k);
		while(it.next()){
			nof += it.multiplicity();
		}
		return nof;
	}



	public int[] sortedNsByPositions(IELSAIterator it){
		int[][]  data = new int[2][it.multiplicity()];
		data[0] = Arrays.copyOfRange(sa, it.istart(), it.iend());
		data[1] = Arrays.copyOfRange(ns, it.istart(), it.iend());
		infoasys.core.common.util.Arrays.qsortOnFirstRow(data);
		data[0] = null;
		return data[1];
	}



	@Override
	public void load(String file) throws Exception {
		DataInputStream reader = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		long version = reader.readLong();

		if(version == NELSA.p_serialVersionUID){
			s_length = reader.readInt();
			System.out.println("s length: "+s_length);
			r_limit = s_length-1;
			sa = new int[s_length];
			lcp = new int[s_length];
			ns = new int[s_length];
			for(int i=0; i<s_length; i++){
				sa[i] = reader.readInt();
				lcp[i] = reader.readInt();
				ns[i] = reader.readInt() -1;
			}
		}
		else if(version == NELSA.serialVersionUID){
			s_length = reader.readInt();
			System.out.println("s length: "+s_length);
			r_limit = s_length-1;
			sa = new int[s_length];
			lcp = new int[s_length];
			ns = new int[s_length];
			for(int i=0; i<s_length; i++){
				sa[i] = reader.readInt();
				lcp[i] = reader.readInt();
				ns[i] = reader.readInt();
			}
		}
		else{
			reader.close();
			throw new Exception("Wrong NELSA file version. \nExpected: "+ Long.toBinaryString(NELSA.serialVersionUID) +"\nfound: \n"+Long.toBinaryString(version));
		}

		reader.close();
	}

	@Override
	public void save(String file) throws Exception {
		DataOutputStream writer = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file,false)));
		writer.writeLong(NELSA.serialVersionUID);
		writer.writeInt(s_length);
		for(int i=0; i<s_length; i++){
			writer.writeInt(sa[i]);
			writer.writeInt(lcp[i]);
			writer.writeInt(ns[i]);
		}
		writer.flush();
		writer.close();
	}
}

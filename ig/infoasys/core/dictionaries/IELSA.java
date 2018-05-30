package infoasys.core.dictionaries;

public interface IELSA {
	public int length();
	
	public int[] sa();
	public int[] lcp();
	
	public IELSAIterator begin(int k);
		
	public void load(String file) throws Exception;
	public void save(String file) throws Exception;
}

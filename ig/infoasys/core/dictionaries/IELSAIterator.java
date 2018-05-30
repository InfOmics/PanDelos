package infoasys.core.dictionaries;

public interface IELSAIterator extends IWordIterator {

	public IELSA elsa();
    /**
     * start SA position of the word
     * @return
     */
	public int istart();
	/**
	 * End SA position +1 of the word.
	 * Yes, you can do for(i=start; i<end; i++)
	 * 
	 * @return
	 */
	public int iend();
	
    public int k();
	public char[] kmer();
    public void kmer(char[] ns);
	public int multiplicity();
	public int[] positions();
	public int[] sortedPositions();
	

	public boolean next();
	public boolean prev();    
        
    public IELSAIterator clone();
    public int compare(IELSAIterator it);
    
}

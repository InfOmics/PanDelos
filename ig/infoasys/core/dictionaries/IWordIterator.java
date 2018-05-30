package infoasys.core.dictionaries;

public interface IWordIterator {

	public int k();
	public char[] kmer();
    public void kmer(char[] ns);
	public int multiplicity();
	
	public boolean next();
	public boolean prev();
}

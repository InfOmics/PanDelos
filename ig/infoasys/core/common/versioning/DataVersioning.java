package infoasys.core.common.versioning;

public class DataVersioning {
	/*
	 * 0xA_B_C_DL :  A(area) B(type) C(class) D(version)
	 *
	 * 
	 * 
	 * area
	 * 	1 core
	 * 	2 shell
	 * 	3 charts
	 * 
	 * type
	 * 	1 genome sequence
	 * 	2 kmer
	 * 	3 dictionary 
	 * 
	 * class
	 *  0 not specified
	 * 	1 infoGenomics.genome.LMLR3BitSet
	 *
	 */
	
	
	//public static final long LMLR3BitSet 			= 0x0000_0000_0000_0000L;
	//public static final long B3LLSequence			= 0x0001000100010000L;
	public static final long CharArraySequence		= 0x0001000100010000L;
	
	//public static final long BaseK12Dict 			= 0x0001000300000000L;
	//public static final long BaseK8Dict 			= 0x0001000300080000L;
	//public static final long BaseKDict 			= 0x0001000300100000L;
	
	public static final long LSA 					= 0x0001000300010000L;
	public static final long ELSA					= 0x0001000300020000L;
	public static final long NELSA					= 0x0001000300030000L;
	//public static final long OnDiskELSA			= 0x0001000300040000L;
	//public static final long DLNELSA			= 0x0001000300050000L;
}

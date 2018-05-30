package infoasys.core.dictionaries;



/*
 * sais.java for sais-java
 * Copyright (c) 2008-2009 Yuta Mori All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * TODO: [dw] Clean up the code, resign from interfaces and use primitive types instead?
 */

/**
 * SA-IS algorithm, implemented by Yuta Mori. SAIS is a very simple and small library that
 * provides an implementation of the induced sorting-based suffix array construction
 * algorithm. The algorithm runs in O(n) worst-case time, and MAX(2n, 4k) worst-case extra
 * working space, where n and k are the length of the input string and the number of
 * alphabets.
 * <p>
 * Ge Nong, Sen Zhang and Wai Hong Chan, Two Efficient Algorithms for Linear Suffix Array
 * Construction, 2008.
 * 
 * @see "http://yuta.256.googlepages.com/sais"
 */
public final class ByteSAIS
{
  private static interface BaseArray
  {
    public int get(int i);
    public void set(int i, int val);
    public int update(int i, int val);
  }

  private static final class ByteArray implements BaseArray
  {
    private byte[] m_A;
    private int m_pos;

    ByteArray(byte[] A, int pos) { m_A = A; m_pos = pos; }
    public int get(int i) { return m_A[m_pos + i] & 0xff; }
    public void set(int i, int val) { m_A[m_pos + i] = (byte)(val & 0xff); }
    public int update(int i, int val) { return m_A[m_pos + i] += val & 0xff; }
  }
  private static final class IntArray implements BaseArray
  {
    private int[] m_A;
    private int m_pos;
    
    IntArray(int[] A, int pos) { m_A = A; m_pos = pos; }
    public int get(int i) { return m_A[m_pos + i]; }
    public void set(int i, int val) { m_A[m_pos + i] = val; }
    public int update(int i, int val) { return m_A[m_pos + i] += val; }
  }
  
  private static final class CharArray implements BaseArray
  {
    private char[] m_A;
    private int m_pos;
    
    CharArray(char[] A, int pos) { m_A = A; m_pos = pos; }
    public int get(int i) { return m_A[m_pos + i]; }
    public void set(int i, int val) { m_A[m_pos + i] =  (char)(val & 0xffff); }
    public int update(int i, int val) { return m_A[m_pos + i] += val; }
  }
  
  
  

  /* find the start or end of each bucket */
  private static void getCounts(BaseArray T, BaseArray C, int n, int k) {
    for(int i = 0; i < k; ++i) { C.set(i, 0); }
    for(int i = 0; i < n; ++i) { C.update(T.get(i), 1); }
  }

  private static void getBuckets(BaseArray C, BaseArray B, int k, boolean end) {
    int i, sum = 0;
    if (end != false) { for(i = 0; i < k; ++i) { sum += C.get(i); B.set(i, sum); } }
    else { for(i = 0; i < k; ++i) { sum += C.get(i); B.set(i, sum - C.get(i)); } }
  }

  /* compute SA and BWT */
  private static void induceSA(BaseArray T, int[] SA, BaseArray C, BaseArray B, int n, int k)
  {
    int b, i, j;
    int c0, c1;
    /* compute SAl */
    if(C == B) { getCounts(T, C, n, k); }
    getBuckets(C, B, k, false); /* find starts of buckets */
    j = n - 1;
    b = B.get(c1 = T.get(j));
    SA[b++] = ((0 < j) && (T.get(j - 1) < c1)) ? ~j : j;
    for(i = 0; i < n; ++i) {
      j = SA[i]; SA[i] = ~j;
      if(0 < j) {
        if((c0 = T.get(--j)) != c1) { B.set(c1, b); b = B.get(c1 = c0); }
        SA[b++] = ((0 < j) && (T.get(j - 1) < c1)) ? ~j : j;
      }
    }
    /* compute SAs */
    if(C == B) { getCounts(T, C, n, k); }
    getBuckets(C, B, k, true); /* find ends of buckets */
    for(i = n - 1, b = B.get(c1 = 0); 0 <= i; --i) {
      if(0 < (j = SA[i])) {
        if((c0 = T.get(--j)) != c1) { B.set(c1, b); b = B.get(c1 = c0); }
        SA[--b] = ((j == 0) || (T.get(j - 1) > c1)) ? ~j : j;
      } else {
        SA[i] = ~j;
      }
    }
  }
  
  private static int computeBWT(BaseArray T, int[] SA, BaseArray C, BaseArray B, int n, int k) {
    int b, i, j, pidx = -1;
    int c0, c1;
    /* compute SAl */
    if(C == B) { getCounts(T, C, n, k); }
    getBuckets(C, B, k, false); /* find starts of buckets */
    j = n - 1;
    b = B.get(c1 = T.get(j));
    SA[b++] = ((0 < j) && (T.get(j - 1) < c1)) ? ~j : j;
    for(i = 0; i < n; ++i) {
      if(0 < (j = SA[i])) {
        SA[i] = ~(c0 = T.get(--j));
        if(c0 != c1) { B.set(c1, b); b = B.get(c1 = c0); }
        SA[b++] = ((0 < j) && (T.get(j - 1) < c1)) ? ~j : j;
      } else if(j != 0) {
        SA[i] = ~j;
      }
    }
    /* compute SAs */
    if(C == B) { getCounts(T, C, n, k); }
    getBuckets(C, B, k, true); /* find ends of buckets */
    for(i = n - 1, b = B.get(c1 = 0); 0 <= i; --i) {
      if(0 < (j = SA[i])) {
        SA[i] = (c0 = T.get(--j));
        if(c0 != c1) { B.set(c1, b); b = B.get(c1 = c0); }
        SA[--b] = ((0 < j) && (T.get(j - 1) > c1)) ? ~((int)T.get(j - 1)) : j;
      } else if(j != 0) {
        SA[i] = ~j;
      } else {
        pidx = i;
      }
    }
    return pidx;
  }


  /* find the suffix array SA of T[0..n-1] in {0..k-1}^n
     use a working space (excluding T and SA) of at most 2n+O(1) for a constant alphabet */
  private static int SA_IS(BaseArray T, int[] SA, int fs, int n, int k, boolean isbwt) {
	  
    BaseArray C, B, RA;
    int i, j, c, m, p, q, plen, qlen, name, pidx = 0;
    int c0, c1;
    boolean diff;

    /* stage 1: reduce the problem by at least 1/2
       sort all the S-substrings */
    if(k <= fs) {
      C = new IntArray(SA, n);
      B = (k <= (fs - k)) ? new IntArray(SA, n + k) : C;
    } else {
      B = C = new IntArray(new int[k], 0);
    }
    getCounts(T, C, n, k); getBuckets(C, B, k, true); /* find ends of buckets */
    for(i = 0; i < n; ++i) { SA[i] = 0; }
    for(i = n - 2, c = 0, c1 = T.get(n - 1); 0 <= i; --i, c1 = c0) {
      if((c0 = T.get(i)) < (c1 + c)) { c = 1; }
      else if(c != 0) { SA[B.update(c1, -1)] = i + 1; c = 0; }
    }
    induceSA(T, SA, C, B, n, k);
    C = null; B = null;

    /* compact all the sorted substrings into the first m items of SA
       2*m must be not larger than n (proveable) */
    for(i = 0, m = 0; i < n; ++i) {
      p = SA[i];
      if((0 < p) && (T.get(p - 1) > (c0 = T.get(p)))) {
        for(j = p + 1; (j < n) && (c0 == (c1 = T.get(j))); ++j) { }
        if((j < n) && (c0 < c1)) { SA[m++] = p; }
      }
    }
    j = m + (n >> 1);
    for(i = m; i < j; ++i) { SA[i] = 0; } /* init the name array buffer */
    /* store the length of all substrings */
    for(i = n - 2, j = n, c = 0, c1 = T.get(n - 1); 0 <= i; --i, c1 = c0) {
      if((c0 = T.get(i)) < (c1 + c)) { c = 1; }
      else if(c != 0) { SA[m + ((i + 1) >> 1)] = j - i - 1; j = i + 1; c = 0; }
    }
    /* find the lexicographic names of all substrings */
    for(i = 0, name = 0, q = n, qlen = 0; i < m; ++i) {
      p = SA[i]; plen = SA[m + (p >> 1)]; diff = true;
      if(plen == qlen) {
        for(j = 0; (j < plen) && (T.get(p + j) == T.get(q + j)); ++j) { }
        if(j == plen) { diff = false; }
      }
      if(diff != false) { ++name; q = p; qlen = plen; }
      SA[m + (p >> 1)] = name;
    }

    /* stage 2: solve the reduced problem
       recurse if names are not yet unique */
    if(name < m) {
      RA = new IntArray(SA, n + fs - m);
      for(i = m + (n >> 1) - 1, j = n + fs - 1; m <= i; --i) {
        if(SA[i] != 0) { SA[j--] = SA[i] - 1; }
      }
      SA_IS(RA, SA, fs + n - m * 2, m, name, false);
      RA = null;
      for(i = n - 2, j = m * 2 - 1, c = 0, c1 = T.get(n - 1); 0 <= i; --i, c1 = c0) {
        if((c0 = T.get(i)) < (c1 + c)) { c = 1; }
        else if(c != 0) { SA[j--] = i + 1; c = 0; } /* get p1 */
      }
      for(i = 0; i < m; ++i) { SA[i] = SA[SA[i] + m]; } /* get index */
    }

    /* stage 3: induce the result for the original problem */
    if(k <= fs) {
      C = new IntArray(SA, n);
      B = (k <= (fs - k)) ? new IntArray(SA, n + k) : C;
    } else {
      B = C = new IntArray(new int[k], 0);
    }
    /* put all left-most S characters into their buckets */
    getCounts(T, C, n, k); getBuckets(C, B, k, true); /* find ends of buckets */
    for(i = m; i < n; ++i) { SA[i] = 0; } /* init SA[m..n-1] */
    for(i = m - 1; 0 <= i; --i) {
      j = SA[i]; SA[i] = 0;
      SA[B.update(T.get(j), -1)] = j;
    }
    if(isbwt == false) { induceSA(T, SA, C, B, n, k); }
    else { pidx = computeBWT(T, SA, C, B, n, k); }
    C = null; B = null;
    return pidx;
  }


  /** Suffixsorting **/
  /* byte */
//  public static
//  int
//  suffixsort(byte[] T, int[] SA, int n) {
//    if((T == null) || (SA == null) || (T.length < n) || (SA.length < n)) { return -1; }
//    if(n <= 1) { if(n == 1) { SA[0] = 0; } return 0; }
//    return SA_IS(new ByteArray(T, 0), SA, 0, n, 256, false);
//  }
  
  public static
  int
  suffixsort(byte[] T, int[] SA, int n, int k) {
    if((T == null) || (SA == null) ||
       (T.length < n) || (SA.length < n) ||
       (k <= 0)) { return -1; }
    if(n <= 1) { if(n == 1) { SA[0] = 0; } return 0; }
    return SA_IS(new ByteArray(T, 0), SA, 0, n, k, false);
  }
  public static
  int
  suffixsort(char[] T, int[] SA, int n, int k) {
    if((T == null) || (SA == null) ||
       (T.length < n) || (SA.length < n) ||
       (k <= 0)) { return -1; }
    if(n <= 1) { if(n == 1) { SA[0] = 0; } return 0; }
    return SA_IS(new CharArray(T, 0), SA, 0, n, k, false);
  }

  /** Burrows-Wheeler Transform **/
  /* byte */
//  public static
//  int
//  bwtransform(byte[] T, byte[] U, int[] A, int n) {
//    int i, pidx;
//    if((T == null) || (U == null) || (A == null) ||
//       (T.length < n) || (U.length < n) || (A.length < n)) { return -1; }
//    if(n <= 1) { if(n == 1) { U[0] = T[0]; } return n; }
//    pidx = SA_IS(new ByteArray(T, 0), A, 0, n, 256, true);
//    U[0] = T[n - 1];
//    for(i = 0; i < pidx; ++i) { U[i + 1] = (byte)(A[i] & 0xff); }
//    for(i += 1; i < n; ++i) { U[i] = (byte)(A[i] & 0xff); }
//    return pidx + 1;
//  }
  
//  private static final int max(byte [] input, int start, int length)
//  {
//      assert length >= 1;
//      int max = input[start];
//      for (int i = length - 2, index = start + 1; i >= 0; i--, index++)
//      {
//          final int v = input[index];
//          if (v > max)
//          {
//              max = v;
//          }
//      }
//      return max;
//  }
 

  public static int [] buildSuffixArray(byte[] input, int start, int length, int maxValue)
  {
      // TODO: [dw] add constraints here. 
      int [] SA = new int [length];
      suffixsort(input, SA, length, maxValue+1);
      //suffixsort(input, SA, length);
      return SA;
  }
  
  public static int [] buildSuffixArray(char[] input, int start, int length, int maxValue)
  {
      // TODO: [dw] add constraints here. 
      int [] SA = new int [length];
      suffixsort(input, SA, length, maxValue+1);
      //suffixsort(input, SA, length);
      return SA;
  }
  
  /**
   * Calculate longest prefix (LCP) array for an existing suffix array and input. Index
   * <code>i</code> of the returned array indicates the length of the common prefix
   * between suffix <code>i</code> and <code>i-1<code>. The 0-th
   * index has a constant value of <code>-1</code>.
   * <p>
   * The algorithm used to compute the LCP comes from
   * <tt>T. Kasai, G. Lee, H. Arimura, S. Arikawa, and K. Park. Linear-time longest-common-prefix
   * computation in suffix arrays and its applications. In Proc. 12th Symposium on Combinatorial
   * Pattern Matching (CPM ’01), pages 181–192. Springer-Verlag LNCS n. 2089, 2001.</tt>
   */
	public static int[] computeLCP(byte[] input, final int start, final int length, int[] sa)
	  {
	      final int [] rank = new int [length];
	      for (int i = 0; i < length; i++)
	          rank[sa[i]] = i;
	      int h = 0;
	      final int [] lcp = new int [length];
	      for (int i = 0; i < length; i++)
	      {
	          int k = rank[i];
	          if (k == 0)
	          {
	              lcp[k] = -1;
	          }
	          else
	          {
	              final int j = sa[k - 1];
	              while (i + h < length && j + h < length
	                  && input[start + i + h] == input[start + j + h])
	              {
	                  h++;
	              }
	              lcp[k] = h;
	          }
	          if (h > 0) h--;
	      }
	
	      return lcp;
	  }
	
	public static int[] computeLCP(char[] input, final int start, final int length, int[] sa)
	  {
	      final int [] rank = new int [length];
	      for (int i = 0; i < length; i++)
	          rank[sa[i]] = i;
	      int h = 0;
	      final int [] lcp = new int [length];
	      for (int i = 0; i < length; i++)
	      {
	          int k = rank[i];
	          if (k == 0)
	          {
	              lcp[k] = -1;
	          }
	          else
	          {
	              final int j = sa[k - 1];
	              while (i + h < length && j + h < length
	                  && input[start + i + h] == input[start + j + h])
	              {
	                  h++;
	              }
	              lcp[k] = h;
	          }
	          if (h > 0) h--;
	      }
	
	      return lcp;
	  }
}

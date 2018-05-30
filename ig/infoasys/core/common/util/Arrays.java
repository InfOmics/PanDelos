package infoasys.core.common.util;

public class Arrays {

	public static void qsortOnLastRow(int[][] matrix){
		qsortOnLastRow(matrix, 0, matrix[0].length - 1);
	}
	public static void qsortOnLastRow(int[][] matrix, int left, int right) {
	    if (left < right) {
	        int i = lastRowpartition(matrix, left, right);
	        qsortOnLastRow(matrix, left, i - 1);
	        qsortOnLastRow(matrix, i + 1, right);
	    }
	}
	private static int lastRowpartition(int[][] matrix, int left, int right) {
	    int lastrow = matrix.length - 1;
	    int pivotValue = matrix[lastrow][left];
	    int i = left;
	    for (int j = left + 1; j <= right; j++) {
	        if (matrix[lastrow][j] <= pivotValue) {
	            i++;
	            swapColumns(matrix, i, j);
	        }
	    }
	    swapColumns(matrix, left, i);
	    return i;
	}
	private static void swapColumns(int[][] matrix, int c0, int c1) {
	    if (c0 != c1) {
	        for (int i = 0; i < matrix.length; i++) {
	            int t = matrix[i][c0];
	            matrix[i][c0] = matrix[i][c1];
	            matrix[i][c1] = t;
	        }
	    }
	}
	
	
	public static void qsortOnFirstRow(int[][] matrix){
		qsortOnFirstRow(matrix, 0, matrix[0].length - 1);
	}
	public static void qsortOnFirstRow(int[][] matrix, int left, int right) {
	    if (left < right) {
	        int i = firstRowpartition(matrix, left, right);
	        qsortOnFirstRow(matrix, left, i - 1);
	        qsortOnFirstRow(matrix, i + 1, right);
	    }
	}
	private static int firstRowpartition(int[][] matrix, int left, int right) {
	    int pivotValue = matrix[0][left];
	    int i = left;
	    for (int j = left + 1; j <= right; j++) {
	        if (matrix[0][j] <= pivotValue) {
	            i++;
	            swapColumns(matrix, i, j);
	        }
	    }
	    swapColumns(matrix, left, i);
	    return i;
	}
	
	
	private static void swap(int[] a, int i, int j){
		int t = a[i];
		a[i] = a[j];
		a[j] = t;
	}
	public static void keyedQsort(int[] keys, int[] values){
		keyedQsort(keys, values, 0, keys.length -1);
	}
	private static void keyedQsort(int[] keys, int[] values, int left, int right){
		if (left < right) {
	        int i = keyedQsortPartition(keys, values, left, right);
	        keyedQsort(keys, values, left, i - 1);
	        keyedQsort(keys, values, i + 1, right);
	    }
	}
	private static int keyedQsortPartition(int[] keys, int[] values, int left, int right){
		int pivotValue = keys[left];
	    int i = left;
	    for (int j = left + 1; j <= right; j++) {
	        if (keys[j] <= pivotValue) {
	            i++;
	            swap(keys, i, j);
	            swap(values, i, j);
	        }
	    }
	    swap(keys, left, i);
	    swap(values, left, i);
	    return i;
	}
	
	
	
	
	public static int intersectionSize(int[] a, int[] b){
		int[][] poss = new int[2][];
		poss[0] = a;
		poss[1] = b;
		
		if(poss[0].length>0 && poss[1].length>0){
			//pointers in poss[i]
			int[] poss_i = {0,0};
			
			int a_i = poss[0][0] <= poss[1][0] ? 0 : 1;
	        int a_j = poss[1][0] < poss[0][0] ? 0 : 1; 
			
	        int count = 0;
	        
			while(poss_i[0] < poss[0].length  &&  poss_i[1] < poss[1].length ){
				while(poss_i[a_i] < poss[a_i].length &&  
	                    //poss[a_i][poss_i[a_i]] <= poss[a_j][poss_i[a_j]]
						poss[a_i][poss_i[a_i]] < poss[a_j][poss_i[a_j]]
	                    ){
	                poss_i[a_i]++;
	            }
				if(poss[a_i][poss_i[a_i]] == poss[a_j][poss_i[a_j]]){
					count++;
					poss_i[a_i]++;
				}
				
				//dist = (poss[a_j][poss_i[a_j]]) - (poss[a_i][poss_i[a_i] - 1]);
				a_i = a_i == 0 ? 1 : 0;
	            a_j = a_j == 0 ? 1 : 0;
			}
			
			
			return count;
			
			
		}
		return 0;
	}
	
	
	
	
	public static double max(double[] a){
		double max = a[0];
		for(int i=1; i<a.length; i++){
			if(a[i] > max){
				max = a[i];
			}
		}
		return max;
	}
	
	public static double min(double[] a){
		double min = a[0];
		for(int i=1; i<a.length; i++){
			if(a[i] < min){
				min = a[i];
			}
		}
		return min;
	}
}

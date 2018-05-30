package infoasys.core.common.util;

public class Pair
	<T1 extends Comparable<T1>, T2 extends Comparable<T2>> 
	implements Comparable<Pair<T1,T2>>{

	public T1 first;
	public T2 second;
	
	public Pair(T1 first, T2 second){
		this.first = first;
		this.second = second;
	}

	@Override
	public int compareTo(Pair<T1, T2> t) {
		int c = first.compareTo(t.first);
		if(c == 0){
			return second.compareTo(t.second);
		}
		return c;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o){
		try{
			Pair<T1,T2> t = (Pair<T1,T2>)(o);
			return first.equals(t.first) && second.equals(t.second);
		}catch(ClassCastException e){
			return false;
		}
	}
	
}

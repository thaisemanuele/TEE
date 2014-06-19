package uk.ac.gla.mir.util;

import java.util.*;

public class KeyValue<T extends Comparable, Z> implements Comparable<KeyValue>{
	
	private Z key;
	private T value;	
	
	public KeyValue(Z key, T value){
		this.key = key;
		this.value = value;
	}
	
	public Z getKey() {
		return key;
	}

	public void setKey(Z key) {
		this.key = key;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public int compareTo(KeyValue otherKeyValue) {
		return -1* value.compareTo(otherKeyValue.value);
	}

	public KeyValue<T,Z>[] sort( ArrayList<KeyValue<T,Z>> listToSort){
		KeyValue<T,Z>[] tmp = listToSort.toArray( new KeyValue[0] );
		return sort(tmp);
	}	
	
	public KeyValue<T,Z>[] sort(KeyValue<T,Z>[] listToSort){
		Arrays.sort(listToSort);
		return listToSort;
	}

	public String toString(){
		return key + " : " + value;
	}
	
	public static void main(String[] args) {
		KeyValue<Double,Integer>[] tmp = new KeyValue[2];
		tmp[0] = new KeyValue<Double,Integer>(1,2.0);
		tmp[1] = new KeyValue<Double,Integer>(2, 1.0);
		Arrays.sort( tmp );		
		System.out.println( Arrays.toString( tmp ));
	}
}

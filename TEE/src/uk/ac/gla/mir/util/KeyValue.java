package uk.ac.gla.mir.util;

import java.util.*;
/**
 * Copyright 2014, The University of Glasgow
 * 
 * This file is part of TEE.
 * TEE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * TEE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with TEE.  If not, see <http://www.gnu.org/licenses/>.
 */

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

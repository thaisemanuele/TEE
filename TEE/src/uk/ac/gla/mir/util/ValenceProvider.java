package uk.ac.gla.mir.util;

import java.util.*;
import uk.ac.gla.mir.entity.Entity;
import uk.ac.gla.mir.input.Fileloader;
import uk.ac.gla.mir.triplets.Triplet;
import edu.smu.tspell.wordnet.SynsetType;
import gnu.trove.TObjectDoubleHashMap;
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
public class ValenceProvider{

	private static final Object lock = new Object();
	static TObjectDoubleHashMap<String> Noun_Map;
	static TObjectDoubleHashMap<String> Adjective_Map;
	static TObjectDoubleHashMap<String> Adverb_Map;
	static TObjectDoubleHashMap<String> Name_Map;
	static TObjectDoubleHashMap<String> Verb_Map;
	static HashSet<String> exceptionAdverb;
	static TObjectDoubleHashMap<String>  prospectVal;
	static TObjectDoubleHashMap<String>  prospectPol;
	public static boolean affect = false;
	
	public static boolean loadDataStores(){
		boolean ok = true;
		synchronized (lock) {
			if( Noun_Map == null ){
				try{
					Noun_Map = Fileloader.Load_Noun_Base_List();
					Adjective_Map = Fileloader.Load_Adjective_Base_List();
					Adverb_Map = Fileloader.Load_Adverb_Base_List();
					Name_Map = Fileloader.Load_Name_Base_List();
					Verb_Map = Fileloader.Load_Verb_Base_List();
					exceptionAdverb = Fileloader.Load_Except_Adverb();
				}catch (Exception e) {
					e.printStackTrace();
					ok = false;
				}
			}
		}
		return ok;
	}
	public static void Give_Valence_To_Verb(Entity e) throws Exception
	{
		final String lowerCaseVerb = e.name.toLowerCase().trim();
		
		if( Verb_Map.containsKey( lowerCaseVerb ))
		{
			e.valence = Verb_Map.get( lowerCaseVerb );
		}
		else
		{
			e.valence = Wordnet.word_net( lowerCaseVerb, SynsetType.VERB );
		}
	}
	public static void Give_Valence_To_Noun(Entity e) throws Exception
	{
		final String lowerCaseNoun = e.name.toLowerCase().trim();
		final String type = e.type;
		
		if(e.type.contains("PRP") )
			e.valence = 1.0;
		else if(Noun_Map.containsKey( lowerCaseNoun ))
			e.valence = Noun_Map.get( lowerCaseNoun );
		else if(Name_Map.containsKey( lowerCaseNoun ))
			e.valence=Name_Map.get(  lowerCaseNoun );
		else if(Adjective_Map.containsKey(  lowerCaseNoun ))
			e.valence = Adjective_Map.get( lowerCaseNoun );

		if(e.valence == 0.0)
		{
			if(( type.equalsIgnoreCase("NN")) || ( type.equalsIgnoreCase("NNPS")) || 
					( type.equalsIgnoreCase("NNP")) || ( type.equalsIgnoreCase("NNS")))
			{
				e.valence = Wordnet.word_net( lowerCaseNoun, SynsetType.NOUN );
			}
			else if((e.type.equalsIgnoreCase("JJS")) || (e.type.equalsIgnoreCase("JJR")) || 
					(e.type.equalsIgnoreCase("JJ")) )
			{
				e.valence = Wordnet.word_net( lowerCaseNoun, SynsetType.ADJECTIVE );
			}

			else if((e.type.equalsIgnoreCase("RB")) || (e.type.equalsIgnoreCase("RBR")) || 
					(e.type.equalsIgnoreCase("RBS")) )
			{
				e.valence = Wordnet.word_net( lowerCaseNoun, SynsetType.ADVERB );
			}
		}
	}
	public static double Give_Valence_To_Verb_Attribs(Entity e) throws Exception
	{
		Double sum=0.0;
		int counter = 0;
		final int size = e.attributes.size();
		for(int i=0;i< size; i++)
		{
			final String attibuteName = e.attributes.get(i).name.toLowerCase().trim();
			final String attibuteType = e.type;
			double temp = 0.0;
			
			if( Adverb_Map.containsKey(  attibuteName ) )
			{
				temp = Adverb_Map.get( attibuteName );
				sum += temp;
				if(temp > 0.0)
					counter++;
				else
					counter--;
			}
			else if(( attibuteType.equalsIgnoreCase("RB")) || ( attibuteType.equalsIgnoreCase("RBR")) || 
					(attibuteType.equalsIgnoreCase("RBS")) )
			{
				temp = Wordnet.word_net( attibuteName, SynsetType.ADVERB );
				sum += temp;
				if(temp > 0.0)
					counter++;
				else
					counter--;
			}
		}
		if(counter < 0)
			counter =  -counter;
		if(counter != 0)
			return (sum/counter);
		else 
			return sum;
	}
	public static double Give_Valence_To_Noun_Attribs(Entity e) throws Exception
	{
		double sum = 0.0;
		int count = 0;
		final int size = e.attributes.size();
		for(int i=0;i< size;i++)
		{
			double temp = 0.0;
			final String str = e.attributes.get(i).name.toLowerCase().trim();
			final String type = e.attributes.get(i).type;
			if( str.equalsIgnoreCase("") )
				continue;
			
			if( type.equalsIgnoreCase("DT") ||
				type.equalsIgnoreCase("CC") ||
				type.equalsIgnoreCase("TO") ||
				str.equalsIgnoreCase("with") || str.equalsIgnoreCase("where") ||
				str.equalsIgnoreCase("when") || str.equalsIgnoreCase("who") ||
				str.equalsIgnoreCase("which") || str.equalsIgnoreCase("where"))
				continue;
			
			else if( Adjective_Map.containsKey( str ) )
			{
				temp = Adjective_Map.get( str );
				sum += temp;
				count++;
			}else if( Name_Map.containsKey( str ) )
			{
				temp = Name_Map.get( str );
				sum += temp;
				count++;
			}

			else if(( type.equalsIgnoreCase("JJ")) || ( type.equalsIgnoreCase("JJR")) || 
					( type.equalsIgnoreCase("JJS")) )
			{				
				temp = Wordnet.word_net( str, SynsetType.ADJECTIVE );
				sum += temp;
				count++;
			}

			else if(( type.equalsIgnoreCase("NN")) || ( type.equalsIgnoreCase("NNP")) || 
					( type.equalsIgnoreCase("NNS")) || ( type.equalsIgnoreCase("NNPS")))
			{
				
				if( Noun_Map.contains( str ) )
					temp = Noun_Map.get( str );
				else{
					temp = Wordnet.word_net( str , SynsetType.NOUN );
				}
				sum += temp;
				count++;
			}else {
				
				temp = Noun_Map.get( str );
				sum += temp;
				count++;
			}
			
			if( temp == 0.0 ){
				Name_Map.put( str, temp);
			}
		}
		return (sum/count);
	}
	public static void Set_Valence_For_Noun_Entity(Entity e) throws Exception
	{
		Give_Valence_To_Noun(e);
		if( Double.isInfinite( e.valence) )
			e.valence = 0.0;
		if( Double.isNaN( e.valence ) )
			e.valence = 0.0;
		double temp = Give_Valence_To_Noun_Attribs(e);
		if( Double.isInfinite( temp ) ){
			temp = 0.0;
		}
		if(Double.isNaN( temp ) )
			temp = 0.0;
		if( e.valence < 0.0 ){
			e.valence = Math.abs( e.valence ) + Math.abs(temp);
			e.valence = e.valence * -1;
		}else if( temp < 0.0 ){
			e.valence = Math.abs( e.valence ) + Math.abs(temp);
			e.valence = e.valence * -1;
		}else{
			e.valence = Math.abs( e.valence ) + Math.abs(temp);
		}		
	}
	private static boolean hasNegation(Entity verb) {
		boolean ok = false;
		for( int i = 0; i < verb.attributes.size(); i++){
			String type =  verb.attributes.get(i).name ;
			ok = ok || type.equalsIgnoreCase( "not" );
		}
		return ok;
	}
	public static boolean Set_Valence_For_Verb_Entity(Entity e) throws Exception
	{
		e.negation = hasNegation( e );
		
		Give_Valence_To_Verb(e);
		double temp = Give_Valence_To_Verb_Attribs(e);
		
		if( Double.isInfinite( temp ) )
			temp = 0.0;
		if( Double.isNaN( temp ) )
			temp = 0.0;		
			if( !e.negation){
				if(e.valence >= 0.0 && temp >= 0.0){
					e.valence = Math.abs( e.valence )+ Math.abs( temp );
				}else if( e.valence > 0.0 && temp < 0.0 ){
					e.valence = Math.abs( e.valence )+ Math.abs( temp );
					e.valence = e.valence * -1;
				}else if( e.valence < 0.0 && temp >= 0.0 ){
					e.valence = Math.abs( e.valence )+ Math.abs( temp );
					e.valence = e.valence * -1;
				}else{
					e.valence = Math.abs( e.valence )+ Math.abs( temp );
					e.valence = e.valence * -1;
				}
			}
		return false;
	}
	public static void Set_Valence_For_Verb_Object_Pair(Triplet t) throws Exception
	{
		Set_Valence_For_Noun_Entity(t.object);
		if(Set_Valence_For_Verb_Entity(t.verb) == true)
		{
			if( t.verb.valence > 0.0 && !t.verb.negation  )
				t.valence = Math.abs( t.verb.valence ) + Math.abs(t.object.valence );
			else if( t.verb.negation || t.verb.valence < 0.0)
				t.valence = -1 * ( Math.abs( t.verb.valence ) + Math.abs(t.object.valence ));

			t.affect = true;
		}
		else
		{
			if( t.object.isEmpty() )
				t.valence = t.verb.negation ? -1 * t.verb.valence : t.verb.valence;
			else
			{
					if(t.verb.valence < 0.0 && t.object.valence >= 0.0)
						t.valence = -1 * ( Math.abs( t.verb.valence ) +  Math.abs( t.object.valence)); 
					else if(t.verb.valence < 0.0 && t.object.valence < 0.0)
						t.valence = -1 * Math.abs( t.verb.valence ) +  Math.abs( t.object.valence);
					else if( t.verb.valence > 0.0 && t.object.valence > 0.0 )
						t.valence = Math.abs( t.verb.valence ) +  Math.abs( t.object.valence);
					else if( t.verb.valence > 0.0 && t.object.valence < 0.0 )
						t.valence = -1 * ( Math.abs( t.verb.valence ) +  Math.abs( t.object.valence) );
					if( t.verb.negation ){
						t.valence = -1 * t.valence;
					}
			}
			t.affect = false;
		}
		
		t.verbObjectVal = t.valence;
	}
	public static void set_valences(Triplet t) throws Exception
	{		
		Set_Valence_For_Noun_Entity(t.subject);
		Set_Valence_For_Noun_Entity(t.object);
	}
	public static  double [] ret_valences(Triplet t)
	{
		double temp [] = new double [3];
		temp[0] = t.subject.valence;
		temp[1] = t.object.valence;
		temp[2] = t.valence;	
		return temp;
	}
	public static boolean Set_Valence_For_Triplet(Triplet t) throws Exception
	{
		Set_Valence_For_Noun_Entity( t.subject );
		Set_Valence_For_Verb_Object_Pair( t );
				
		if( t.valence < 0.0 ){
			t.valence = -1 * ( Math.abs( t.valence) + Math.abs( t.subject.valence ) );
			return false;
		} else if( t.valence == 0.0){
			t.valence = t.subject.valence;
			return false;
		}else {
			t.valence = ( Math.abs( t.valence) + Math.abs( t.subject.valence ) );
			return true;
		}
	}
	public static double Get_Valence_For_Sentence(List<Triplet> list) throws Exception
	{
		double result = 0.0;
		Boolean to_dependent = false;
		double valence = 0.0;
		int numberOfNegObjectVerbs = 0;
		int numberOfPos = 0;
		int numberOfNeg = 0;
		double maxPos = 0;
		double maxNeg = 0;
		boolean fliped = false;
		boolean flipedBecauseTo = false;
		final boolean[] pos = new boolean[ list.size() ];
		for( int i = 0; i < list.size(); i++ ){
			final Triplet t = list.get( i );
			pos[i] = Set_Valence_For_Triplet( t );
			valence= list.get(i).valence;
			if( t.verbObjectVal < 0 ){
				numberOfNegObjectVerbs++;
			}
			if( i == 0){
				result = valence;
			}else{
				if(to_dependent==false){
					if( result < 0.0 && valence < 0.0){
						result =( Math.abs( result) + Math.abs(valence) )/2;
						result = -1 * result;
					}else if( result < 0.0 && valence >= 0.0){
						result =( Math.abs( result) + Math.abs(valence) )/2;
						fliped = true;
					}else if( result > 0.0 && valence < 0.0 ){
						result =( Math.abs( result) + Math.abs(valence) )/2;
						result = -1*result;
						fliped = true;
					}else{
						result =( Math.abs( result) + Math.abs(valence) )/2;
					}
				}else{
					if( result < 0.0 && valence < 0.0){
						result = ( Math.abs( result) + Math.abs(valence) )/2;
					}else if( result < 0.0 && valence >= 0.0){
						result =( Math.abs( result) + Math.abs(valence) )/2;
						result = -1 * result;
					}else if( result > 0.0 && valence < 0.0 ){
						result =( Math.abs( result) + Math.abs(valence) )/2;
						result = -1 * result;
					}else{
						result =( Math.abs( result) + Math.abs(valence) )/2;
					}
					if( fliped )
						flipedBecauseTo = true;
				}
			}
			if( valence < 0.0 ){
				numberOfNeg++;
				if( Math.abs( valence ) >= maxNeg )
					maxNeg = Math.abs( valence );
			}else{
				numberOfPos++;
				if( Math.abs( valence ) >= maxPos )
					maxPos = Math.abs( valence );
			}
			to_dependent=false;
			for(int j=0;j<list.get(i).verb.attributes.size();j++){
				if(list.get(i).verb.attributes.get(j).type.equalsIgnoreCase("TO")){
					to_dependent=true;
				}
				if(list.get(i).verb.attributes.get(j).name.equalsIgnoreCase("not")){
					to_dependent=true;
				}
			}
			to_dependent = to_dependent || list.get(i).toDependancy.equalsIgnoreCase("to");
		}
		return result;
	}
	public static boolean isExpection(String name) {
		return exceptionAdverb.contains( name.toLowerCase() );
	}
}
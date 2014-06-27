package uk.ac.gla.mir.entity;

import java.util.*;

import edu.stanford.nlp.trees.Tree;

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

public class Entity
{
		public String name;
		public String type;
		public double valence;
		public List<Entity> attributes;
		public Tree tree;
		public Tree treeParent;
		
		public double prospectPol;
		public double prospectiveVal;
		public double praiseWorthy;
		public boolean negation;
		public boolean isVerb;
		
		public Entity()
		{
			name = "";
			type = "";
			valence = 0.0;
			attributes = new ArrayList<Entity>();
		}
		
		public Entity(String a,String b)
		{
			name = a;
			type = b;
			valence = 0.0;
			attributes = new ArrayList<Entity>();
		}
		
		public Entity(String a,String b,List<Entity>c)
		{
			name = a;
			type = b;
			valence = 0.0;
			attributes = c;
		}
		
		public Entity (String a,String b,double c)
		{
			name = a;
			type = b;
			valence = c;
			attributes = new ArrayList<Entity>();
		}
		
		public Entity(String a,String b, List<Entity> c, double d)
		{
			name = a;
			type = b;
			attributes = c;
			valence = d;
		}
		
		public boolean isEmpty()
		{
			return this.name.equalsIgnoreCase("");
		}
					    		
		public void print()
		{
			System.out.print("( name : "+name);
			if(!type.equalsIgnoreCase(""))
				System.out.print(" , type : "+type);
				System.out.print(" , attributes : ");
				printattributes();
			System.out.print(" )");
		}
		
		public String toString(){
			StringBuffer msg = new StringBuffer();
			
			msg.append("( name : "+name);
			if(!type.equalsIgnoreCase(""))
				msg.append(" , type : "+type);
			
			msg.append(" , valence : " + valence);

			msg.append(" , attributes : ");
			msg.append( getAttributes() );
			msg.append(" )");
			
			return msg.toString();
		}
		
		public String getAttributes(){
			StringBuffer msg = new StringBuffer();
			msg.append("[ ");
			for(int i=0;i<attributes.size();i++)
			{
				msg.append(attributes.get(i).type+" : " + attributes.get(i).name+" ");	
			}
			msg.append(" ]");
			return msg.toString();
		}
		
		public void printattributes()
		{
			System.out.print("[ ");
			for(int i=0;i<attributes.size();i++)
			{
				System.out.print(attributes.get(i).type+" : "+attributes.get(i).name+" ");	
			}
			System.out.print(" ]");
		}
		
		public static final boolean isSentenceStart( String type ){
			return type.equalsIgnoreCase("S") || type.equalsIgnoreCase("SQ") ||
					type.equalsIgnoreCase("FRAG") || type.equalsIgnoreCase("SINV") ||
					type.equalsIgnoreCase("UCP");
		}
		public static final boolean isCC( String type ){
			return type.equalsIgnoreCase("CC");
		}
		public static final boolean isVerb( String type){
			return	 type.equalsIgnoreCase("VB") || 
					 type.equalsIgnoreCase("VBD") ||
					 type.equalsIgnoreCase("VBG") ||
					 type.equalsIgnoreCase("VBN") ||
					 type.equalsIgnoreCase("VBP") ||
					 type.equalsIgnoreCase("VBZ");
		}
		public static final boolean isVerbPhrase( String type){
			return	 type.equalsIgnoreCase("VP"); 
		}
		
		public static final boolean isNoun( String type){
			return	 type.equalsIgnoreCase("NN") || 
					 type.equalsIgnoreCase("NNP") ||
					 type.equalsIgnoreCase("NNPS") ||
					 type.equalsIgnoreCase("NNS");		
		}

		public static final boolean isNounPhrase( String type){
			return	type.equalsIgnoreCase("NP");
		}
		public static final boolean isNounPhraseAttrib( String type ){
			return type.equalsIgnoreCase("NP") || type.equalsIgnoreCase("NP-TMP");
		}
		public static final boolean isAdjective( String type){
			return	 type.equalsIgnoreCase("JJ") || 
					 type.equalsIgnoreCase("JJR") ||
					 type.equalsIgnoreCase("JJS");
		}
		public static final boolean isAdjectivePhrase( String type){
			return type.equalsIgnoreCase("ADJP");
		}
		public static final boolean isAdVerbPhrase( String type){
			return type.equalsIgnoreCase("ADVP");
		}
		public static final boolean isPrepositionPhrase( String type){
			return	type.equalsIgnoreCase("PP");
		}
		public static final boolean isAttribForNoun( String type){
			return type.equalsIgnoreCase("PRP$") ||
				   type.equalsIgnoreCase("POS")  ||
				   type.equalsIgnoreCase("JJ") || 
				   type.equalsIgnoreCase("CD") ||
				   type.equalsIgnoreCase("QP") ||
				   type.equalsIgnoreCase("IN") ||
				   type.equalsIgnoreCase("RB");
		}
		public static final boolean isAttribForNounPhrase( String type){
			return type.equalsIgnoreCase("ADJP") ||
			   type.equalsIgnoreCase("NP");
		}
		public static final boolean isAdverb( String type){
			return type.equalsIgnoreCase("RB") || 
				   type.equalsIgnoreCase("RBR") || 
				   type.equalsIgnoreCase("RBS");
		}
		public static String processVerb(String value) {
			value = value.trim();
			if( value.equalsIgnoreCase("'re")	)
				value = "are";
			else if( value.equalsIgnoreCase("'s") )
				value = "is";
			else if( value.equalsIgnoreCase("'m") )
				value = "am";
			return value;
		}
}
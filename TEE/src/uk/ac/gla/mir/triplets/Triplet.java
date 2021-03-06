package uk.ac.gla.mir.triplets;

import java.util.ArrayList;
import edu.stanford.nlp.trees.Tree;
import uk.ac.gla.mir.entity.Entity;

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

public class Triplet implements Comparable<Triplet>
{
	public Integer index;
	public Entity verb;
	public Entity subject;
	public Entity object;
	public String toDependancy = "";
	public String andDependancy = "";
	public String butDependancy = "";
	public String notToDependancy = "";
	public Entity lastNP;
	public Tree verbTree;
	public Tree subjectTree;
	public Tree objectTree;
	public double valence;
	public double verbObjectVal;
	public boolean affect = false;
	
	public Triplet()
	{
		verb = new Entity();
		subject = new Entity();
		object = new Entity();
		valence = 0.0;
		verbObjectVal = 0.0;
	}
	
	public Triplet(Entity a,Entity b, Entity c)
	{
		verb = a;
		subject = b;
		object = c;
		valence = 0.0;
		verbObjectVal = 0.0;
	}
	
	public void print()
	{
		System.out.print(" , Subject = ");
		subject.print();
		System.out.print("{ Verb = ");
		verb.print();
		System.out.print(" , Object = ");
		object.print();
		System.out.println(" }");
	}
	
	public String toString(){
		return 
		"\nIndex : " + index + 
		"\nSubject: " + subject.name + "  : " + subject.valence+ " : " + subject.attributes + 
		"\n"+"Predicate: "+verb.name+" : "+verb.valence + " : " + verb.attributes+"\n"+
		"Object: "+object.name+" : "+object.valence+ " : " + object.attributes + 
		"\nTriplet Valence: " + this.valence + 
		"\nand-Dependancy: "+ andDependancy+
		"\nbutDependancy: "+ butDependancy+
		"\nnoToDependancy: "+ notToDependancy+
		"\nToDependancy: "+ toDependancy+
		"\nLastNoun:"+lastNP+"\n"+emotions;
	}

	public boolean isEmpty() {
		return subject.isEmpty() && verb.isEmpty() && object.isEmpty();
	}

	private ArrayList<String> emotions;
	public void setEmotions(ArrayList<String> tripletEmo) {
		emotions = tripletEmo;
	}
	
	public ArrayList<String> getEmotions() {
		return emotions;
	}

	public void addEmotions(ArrayList<String> tmp) {
		if( emotions == null )
			emotions =tmp;
		else 
			emotions.addAll( tmp );
	}

	public int compareTo(Triplet o) {		
		return this.index.compareTo(o.index);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}

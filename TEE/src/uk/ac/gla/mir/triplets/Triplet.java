package uk.ac.gla.mir.triplets;

import java.util.ArrayList;
import edu.stanford.nlp.trees.Tree;
import uk.ac.gla.mir.entity.Entity;

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

package uk.ac.gla.mir.util;

public class Get_valence_synonym {
	
	public static double get_valence_adjective(String[] a)
	{
		double total = 0.0;
		int counter = 0;
		for(int i=0 ;i< a.length; i++)
		{
			double temp = 0.0;
			if( ValenceProvider.Noun_Map.containsKey( a[i] ) )
					temp = ValenceProvider.Noun_Map.get( a[i] );
			 if( ValenceProvider.Adjective_Map.containsKey( a[i] ))
				temp = ValenceProvider.Adjective_Map.get( a[i] );
			
			if(temp != 0.0)
			{
				total += temp;
				counter++;
			}
		}
		
		if(counter != 0)
			return total/counter;
		else
			return total;
	}
	
	public static double get_valence_verb(String[] a)
	{
		double total = 0.0;
		int counter = 0;
		for(int i=0 ;i< a.length; i++)
		{
			double temp = 0.0;
			 if( ValenceProvider.Verb_Map.containsKey( a[i] ))
				temp = ValenceProvider.Verb_Map.get( a[i] );
			
			if(temp != 0.0)
			{
				total += temp;
				counter++;
			}
		}
		
		if(counter != 0)
			return total/counter;
		else
			return total;
	}
	
	public static double get_valence_adverb(String[] a)
	{
		double total = 0.0;
		int counter = 0;
		for(int i=0 ;i< a.length; i++)
		{
			double temp = 0.0;
			if( ValenceProvider.Adverb_Map.containsKey( a[i] ))
				temp = ValenceProvider.Adverb_Map.get( a[i] );
			
			if(temp != 0.0)
			{
				total += temp;
				counter++;
			}
		}
		
		if(counter != 0)
			return total/counter;
		else
			return total;
	}

	public static double get_valence_noun(String[] a) {
		double total = 0.0;
		int counter = 0;
		for(int i=0 ;i< a.length; i++)
		{
			double temp = 0.0;
			if( ValenceProvider.Adverb_Map.containsKey( a[i] ))
				temp = ValenceProvider.Adverb_Map.get( a[i] );
			
			if(temp != 0.0)
			{
				total += temp;
				counter++;
			}
		}
		
		if(counter != 0)
			return total/counter;
		else
			return total;
	}
	
	public static double get_valence_all(String[] a) {
		double total = 0.0;
		int counter = 0;
		for(int i=0 ;i< a.length; i++)
		{
			double temp = 0.0;
			if( ValenceProvider.Noun_Map.containsKey( a[i] ) )
					temp = ValenceProvider.Noun_Map.get( a[i] );
			else if( ValenceProvider.Adjective_Map.containsKey( a[i] ))
				temp = ValenceProvider.Adjective_Map.get( a[i] );
			else if( ValenceProvider.Adverb_Map.containsKey( a[i] ))
				temp = ValenceProvider.Adverb_Map.get( a[i] );
			else if( ValenceProvider.Name_Map.containsKey( a[i] ))
				temp = ValenceProvider.Name_Map.get( a[i] );
			 if( ValenceProvider.Verb_Map.containsKey( a[i] ))
				temp = ValenceProvider.Verb_Map.get( a[i] );

			
			if(temp != 0.0)
			{
				total += temp;
				counter++;
			}
		}
		
		if(counter != 0)
			return total/counter;
		else
			return total;
	}

	public static double get_valence_verb(String string) {
			double temp = 0.0;
			 if( ValenceProvider.Verb_Map.containsKey( string ))
				temp = ValenceProvider.Verb_Map.get( string );			
			return temp;
	}
	
}

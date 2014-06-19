package uk.ac.gla.mir.util;

import java.util.Arrays;
import java.util.HashSet;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class Wordnet {
	private static WordNetDatabase database = null;		
	public static Double word_net( final String word, SynsetType type) throws Exception 
	{
		double d = 0.0;
		if( database == null ){
			System.setProperty("wordnet.database.dir", "dict/");
			database = WordNetDatabase.getFileInstance();
		}
		final HashSet<String> syns = new HashSet<String>();
		final Synset[] tmp = database.getSynsets( word, type);
		for( int i = 0; i < tmp.length; i++){
			final String[] s = tmp[i].getWordForms();
			for( int j = 0; j < s.length; j++){
				String[] newS = s[j].split("\\s+");
				syns.addAll( Arrays.asList( newS ) );
			}
		}		
		final String[] synonyms = syns.toArray( new String[0] );
		if (synonyms != null) 
		{
			Arrays.sort( synonyms );
			d = Get_valence_synonym.get_valence_verb( synonyms );
			if( d == 0.0)
				d = Get_valence_synonym.get_valence_all( synonyms );
		} 
		if( Double.isInfinite(d) || Double.isNaN(d))
			d = 0.0;		
		return d;
	}
}

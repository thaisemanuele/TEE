package uk.ac.gla.mir.util;

import java.io.*;
import java.util.HashSet;

import omcsnet.JOMCSNetAPI;

public class Conceptnet 
{
	public final static JOMCSNetAPI j = new JOMCSNetAPI();
	
	public static int c_count(final String ob) throws IOException {
		final String res = j.find_analogous_nodes_simple(ob);
		final String[] tmp = res.split("\\n+");	
		return tmp.length;
	}

	public static HashSet<String> relationsWithSubject(String lowerCase) {
		HashSet<String> relations = new HashSet<String>();
		final String res = j.find_analogous_nodes_simple( lowerCase );
		return relations;
	}
}

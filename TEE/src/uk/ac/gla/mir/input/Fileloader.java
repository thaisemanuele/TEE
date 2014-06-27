package uk.ac.gla.mir.input;

import java.io.*;
import java.rmi.dgc.VMID;
import java.util.*;

import uk.ac.gla.mir.util.Settings;
import gnu.trove.*;

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

public class Fileloader{
	
	private static final java.rmi.dgc.VMID guid = new VMID();
	private static final String ID = guid.toString().replaceAll(":", "-");
	
	public static TObjectDoubleHashMap<String> Load_Noun_Base_List() throws IOException
    {
		final long startTime = System.currentTimeMillis();
		final String conceptsFolder = Settings.getProperty("concepts.folder", "data/concepts/");
		final File dir = new File( conceptsFolder );
		
		System.err.print("Loading noun list from "+dir+" ... ");
		
		final File[] files = dir.listFiles( new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.toLowerCase().contains("concept") && !name.startsWith(".");
			}
		});
		final TObjectDoubleHashMap<String> myMap = new TObjectDoubleHashMap<String>();
		for( File fin : files){
			final BufferedReader bin = new BufferedReader( new FileReader( fin ) );
			String line = "";
			
			while( ( line = bin.readLine() ) != null ){
				if( line.trim().equalsIgnoreCase("") )
					continue;
								
				String[] tmp = line.split("\\s+");
				try{
				  final double value = Double.parseDouble( tmp[ tmp.length-1]  );
				  String noun = "";
				  for( int i = 0; i < tmp.length-2; i++)
				     noun = noun+tmp[i]+" ";
				  	 noun = noun.trim();

				  	 if( myMap.containsKey( noun ) ){
						 double v = myMap.get( noun );
						 if( Math.abs( v ) > Math.abs( value) )
							 myMap.put(noun, v);
						 else
							 myMap.put( noun, value);
					 }else
						 myMap.put( noun, value);

				}catch(Exception e){
				e.printStackTrace();
				System.err.println( line );
				System.exit(-1 ); 
				}				
			}
			bin.close();
		}			
        final long time = System.currentTimeMillis() - startTime;
		System.err.println(" done [" + time + " milli-sec].");
		return myMap;
    }
	
	public static TObjectDoubleHashMap<String> Load_Adjective_Base_List() throws IOException
    {
		final long startTime = System.currentTimeMillis();
		final String conceptsFolder = Settings.getProperty("adjectives.folder", "data/adjectives/");
		final File dir = new File( conceptsFolder );
		
		System.err.print("Loading adjective list from "+dir+" ... ");
		
		final File[] files = dir.listFiles( new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.toLowerCase().contains("adjective") && !name.startsWith(".");
			}
		});
		final TObjectDoubleHashMap<String> myMap = new TObjectDoubleHashMap<String>();
		for( File fin : files){
			final BufferedReader bin = new BufferedReader( new FileReader( fin ) );
			String line = "";
			
			while( ( line = bin.readLine() ) != null ){
				if( line.trim().equalsIgnoreCase("") )
					continue;
				final String noun = line.substring(0, line.lastIndexOf(" ") );
				final double value = Double.parseDouble( line.substring( line.lastIndexOf(" ")+1).trim() );
				 if( myMap.containsKey( noun ) ){
					 double v = myMap.get( noun );
					 if( Math.abs( v ) > Math.abs( value) )
						 myMap.put(noun, v);
					 else
						 myMap.put( noun, value);
				 }else
					 myMap.put( noun, value);
			}
			bin.close();
		}			
        final long time = System.currentTimeMillis() - startTime;
		System.err.println(" done [" + time + " milli-sec].");
		return myMap;
    }
	
	public static TObjectDoubleHashMap<String> Load_Adverb_Base_List() throws IOException
    {
		final long startTime = System.currentTimeMillis();
		final String adverbsFolder = Settings.getProperty("adverb.folder", "data/adverbs/");
		final File dir = new File( adverbsFolder );
		if( !dir.exists() )
		System.err.print("Loading adverbs list from "+dir+" ... ");
		
		final File[] files = dir.listFiles( new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.toLowerCase().contains("adverb") && !name.startsWith(".") ;
			}
		});
		final TObjectDoubleHashMap<String> myMap = new TObjectDoubleHashMap<String>();
		for( File fin : files){
			final BufferedReader bin = new BufferedReader( new FileReader( fin ) );
			String line = "";
			
			while( ( line = bin.readLine() ) != null ){
				if( line.trim().equalsIgnoreCase("") )
					continue;
				
				if( line.toLowerCase().contains("<except") ){
					line = line.replaceAll("<except>", "").trim();
				}
				final String noun = line.substring(0, line.lastIndexOf(" ") );
				double value = Double.parseDouble( line.substring( line.lastIndexOf(" ")+1).trim() );
				
				if( myMap.containsKey( noun ) ){
					 double v = myMap.get( noun );
					 if( Math.abs( v ) > Math.abs( value) )
						 myMap.put(noun, v);
					 else
						 myMap.put( noun, value);
				 }else
					 myMap.put( noun, value);
			}
			bin.close();
		}			
        final long time = System.currentTimeMillis() - startTime;
		System.err.println(" done [" + time + " milli-sec].");
		return myMap;
    }
	
	public static TObjectDoubleHashMap<String> Load_Verb_Base_List() throws Exception
    {
		final TObjectDoubleHashMap<String> myMap = new TObjectDoubleHashMap<String>();		
		final long startTime = System.currentTimeMillis();
		final String adverbsFolder = Settings.getProperty("verb.folder", "data/verbs/");
		final File dir = new File( adverbsFolder );
		if( !dir.exists() )
		System.err.print("Loading verbs list from "+dir+" ... ");
		
		final File[] files = dir.listFiles( new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.toLowerCase().contains("verb") && !name.startsWith(".");
			}
		});
		        
        for( int f = 0; f < files.length; f++){
		    String verb="";
		    String line = "";
		    Double polarity;
		    int length = 0;
		    final BufferedReader bin = new BufferedReader( new FileReader( files[f] ) );
	        while( ( line = bin.readLine() ) != null ){
	        	if( line.trim().equalsIgnoreCase(""))
	        		continue;
	        	
				    String [] tokens= line.split("\\s+");
				    
				    length=tokens.length;
				    verb="";
				    
				    if( tokens[length-1].toLowerCase().contains("affect") || tokens[length-1].toLowerCase().contains("opinion")){
				    	polarity = Double.valueOf(tokens[ length - 4 ]);
				    }
				    else{
				    	polarity = Double.valueOf( tokens[ length - 3 ]);
				    }
				    verb = verb.trim();
				    myMap.put( verb, polarity );
							
			}
	        bin.close();
        }
        final long time = System.currentTimeMillis() - startTime;
		System.err.println(" done [" + time + " milli-sec].");
		return myMap;
    }
	
	public static TObjectDoubleHashMap<String> Load_Name_Base_List() throws IOException
    {
		final long startTime = System.currentTimeMillis();
		final String adverbsFolder = Settings.getProperty("name.folder", "data/names/");
		final File dir = new File( adverbsFolder );
		if( !dir.exists() )
		System.err.print("Loading Named Entities list from "+dir+" ... ");
		
		final File[] files = dir.listFiles( new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.toLowerCase().contains("named") && !name.startsWith(".");
			}
		});
		final TObjectDoubleHashMap<String> myMap = new TObjectDoubleHashMap<String>();
		for( File fin : files){
			final BufferedReader bin = new BufferedReader( new FileReader( fin ) );
			String line = "";
			
			while( ( line = bin.readLine() ) != null ){
				if( line.trim().equalsIgnoreCase("") )
					continue;
				
				String [] tokens=line.split("\\s+");
				int length=tokens.length;
				String name="";
				for(int i=0;i<length-6;i++)
				  	name+=tokens[i];
					myMap.put(name, (Double.valueOf(tokens[length-1])+Double.valueOf(tokens[length-2]))/2.0);
			}
			bin.close();
		}			
        final long time = System.currentTimeMillis() - startTime;
		System.err.println(" done [" + time + " milli-sec].");
		return myMap;
    }

	public static HashSet<String> affectiveVerbs() throws IOException{
		
		final HashSet<String> affective = new HashSet<String>();
		final File dir = new File( "data/affective/" );
		final File[] files = dir.listFiles( new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.endsWith("txt");
			}
		});
		String line = "";
		for( final File fin : files ){
			final BufferedReader bin = Files.openFileReader( fin );
			while( ( line = bin.readLine() ) != null ){
				String tmp = line.toLowerCase().trim();
				affective.add( tmp );
			}
			bin.close();
		}
		return affective;
	}

	public static HashSet<String> Load_Except_Adverb() throws IOException{
		final long startTime = System.currentTimeMillis();
		final String adverbsFolder = Settings.getProperty("adverb.folder", "data/adverbs/");
		final File dir = new File( adverbsFolder );
		if( !dir.exists() )
		System.err.print("Loading adverbs list from "+dir+" ... ");
		
		final File[] files = dir.listFiles( new FilenameFilter(){
			public boolean accept(File dir, String name) {
				return name.toLowerCase().contains("adverb") && !name.startsWith(".");
			}
		});
		final HashSet<String> myMap = new HashSet<String>();
		for( File fin : files){
			final BufferedReader bin = new BufferedReader( new FileReader( fin ) );
			String line = "";
			
			while( ( line = bin.readLine() ) != null ){
				if( line.trim().equalsIgnoreCase("") )
					continue;
				if( line.toLowerCase().contains("<except>") ){
						line = line.replaceAll("<except>", "").trim();
						final String noun = line.substring(0, line.lastIndexOf(" ") );
						myMap.add( noun.toLowerCase() );
				}
			}
			bin.close();
		}			
        final long time = System.currentTimeMillis() - startTime;
		System.err.println(" done [" + time + " milli-sec].");
		return myMap;
	}
}
package uk.ac.gla.mir.util;

import java.io.*;
import java.util.*;

public class Settings {

	private static Properties props = new Properties();
	public static String fileSep = System.getProperty("file.separator");
	public static boolean DEBUG = false;
	public static String FEELIT_HOME = System.getProperty("feelit.home");
	public static String ETC = System.getProperty("feelit.etc");
	private static String location;
	private static String temp = "tmp";
	private static String query;
	private static String INDEX_PATH;
	public static String stopfilelocation = "" ;
	public static void initSettings(String location){
		if(FEELIT_HOME == null){
			FEELIT_HOME = System.getProperty("user.dir");
			System.setProperty("feelit.home", FEELIT_HOME);
		}
		
		if(ETC == null && FEELIT_HOME != null){
			ETC = FEELIT_HOME+fileSep+"etc/feelit.properties";
		}
		Settings.location = location;
		if( Settings.location == null )
			Settings.location = ETC;

		if(location != null){
			try{
				props.load( new FileInputStream( Settings.location) );
				setProperty("feelit.etc", Settings.location);
			}catch (Exception e) {
			}
		}
		Properties p = System.getProperties();
		Set<Object> keys = p.keySet();
		Iterator<Object> iterator = keys.iterator();
		while(iterator.hasNext()){
			String key = (String)iterator.next();	
			String value = p.getProperty(key);
			props.setProperty(key, value);
		}
		DEBUG = getProperty("feelit.debug", true);		
		
		stopfilelocation = getProperty("feelit.stopword.file", "");
		temp = getProperty("feelit.temp.dir", "tmp");
		INDEX_PATH = getProperty("feelit.index.path","");
	}
	
	public static synchronized void getSettings(String location) {
		initSettings(location);
	}
	
	public static String getIndexPath() {
		return INDEX_PATH;
	}

	public static String getLocation() throws IOException{
		return location;
	}

	public static String getQuery() throws IOException {
		return location;
	}
	
	public static String getTemp(){
		return temp;
	}
	
	public static void list(){
		final Iterator<Object> it = props.keySet().iterator();
		while(it.hasNext()){
			final Object key = it.next();
			final Object value = props.get(key);
			System.out.println( key +" == " + value);
		}
	}
	
	public static String getProperty(String name, String defaultVal){
		if( props.containsKey(name) )
			return props.getProperty(name);
		else{
			props.setProperty(name, defaultVal);
			return defaultVal;
		}
	}

	public static double getProperty(String name, double defaultVal){
		if( props.containsKey(name) )
			return Double.parseDouble( props.getProperty(name) );
		else{
			props.setProperty(name, defaultVal+"");
			return defaultVal;
		}
	}
	
	public static boolean getProperty(String name, boolean defaultVal){
		if( props.containsKey(name) )
			return Boolean.parseBoolean( props.getProperty(name) );
		else{
			props.setProperty(name, defaultVal+"");
			return defaultVal;
		}
	}
	
	public static int getProperty(String name, int defaultVal){
		if( props.containsKey(name) )
			return Integer.parseInt( props.getProperty(name) );
		else{
			props.setProperty(name, defaultVal+"");
			return defaultVal;
		}
	}
	
	public static long getProperty(String name, long defaultVal){
		if( props.containsKey(name) )
			return Long.parseLong( props.getProperty(name) );
		else{
			props.setProperty(name, defaultVal+"");
			return defaultVal;
		}
	}
	
	public static void setProperty(String name, boolean value){
		props.setProperty(name, value+"");
	}
	
	public static void setProperty(String name, String value){
		props.setProperty(name, value);
	}
	
	public static void setProperty(String name, int value){
		props.setProperty(name, value+"");
	}
	
	public static void setProperty(String name, double value){
		props.setProperty(name, value+"");
	}
	
	public static void main(String[] args) {
		initSettings( null );
		list();
	}
}

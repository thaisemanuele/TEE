package uk.ac.gla.mir.util;
import java.io.IOException;
import java.util.*;
import uk.ac.gla.mir.entity.Entity;
import uk.ac.gla.mir.triplets.Triplet;

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

public class EmotionExtractor {
	
	private static final String JOY 		= "joy";
	private static final String DISTRESS	= "distress";
	private static final String HAPPYFOR 	= "happyfor"; 
	private static final String SORRYFOR	= "sorryfor";
	private static final String RESENTMENT	= "resentment";
	private static final String GLOATING	= "gloating";
	private static final String HOPE		= "hope";
	private static final String FEAR		= "fear";
	private static final String RELIEF		= "relief";
	private static final String SHOCK		= "shock";
	private static final String SURPRISE	= "surprise";
	private static final String PRIDE		= "pride";
	private static final String SHAME		= "shame";
	private static final String ADMIRATION	= "admiration";
	private static final String REPROACH	= "reproach";
	private static final String REMORSE		= "remorse";
	private static final String GRATITUDE	= "gratitude";
	private static final String ANGER		= "anger";
	private static final String LOVE		= "love";
	private static final String HATE		= "hate";
	private static final String GRATIFICATION	= "gratification";
 	private static final String DISAPPOINTMENT	= "disappointment";
	private static final String SATISFACTION	= "satisfaction";
	private static final String FEARSCONFIRMED	= "fearsconfirmed";
			
	private boolean af; 
	private boolean de;
	private boolean of;
	private boolean oa;
	private boolean sr;
	private boolean sp;
	private boolean op;
	private byte pros;
	private byte status;
	private boolean unexp;
	private byte sa;
	private boolean vr;
	private boolean ed;
	private boolean eoa;
	private boolean edev;
	private boolean ef;
	public double prospectPolarity;
	public double prospectiveValue;
	public double praiseworthValue;
	public boolean statusTmp;
	public boolean negation;

	public HashSet<String> getEmotions( ArrayList<Triplet> triplets, double sentenceValence ) throws IOException{
		HashSet<String> emotions = null;
		for( int i = 0; i < triplets.size(); i++){
			final Triplet triplet = triplets.get( i );
			final HashSet<String> emotionsForTriplet = extractEmotionsFromTriplet( triplet, sentenceValence);

			if( emotions == null ){
				emotions = emotionsForTriplet;
			}else{
				emotions = applyAndLogic(emotions, emotionsForTriplet);
			}
		}
		if( emotions == null )
			emotions = new HashSet<String>();
		return emotions;
	}

	public HashSet<String> extractEmotionsFromTriplet( Triplet triplet, double sentenceValence) throws IOException{ 
		HashSet<String> emotions = new HashSet<String>();
		
		double eventValence = triplet.verbObjectVal;
		double agentValence = triplet.subject.valence;
		negation = hasNegation( triplet.verb );
		
		prospectPolarity =   triplet.verb.prospectPol;
		prospectiveValue= triplet.verb.prospectiveVal;
		praiseworthValue = ( triplet.verb.valence + prospectiveValue ) / 2.0;
	
		if ( negation )
			prospectiveValue = prospectiveValue * -1;
		
		if ( negation )
			praiseworthValue = praiseworthValue * -1;
		
		sp = false; sr = false;
		if( eventValence >= 0.0 )
			sp = true;
		sr = sp;
	
		op = false;
		if((eventValence >= 0.0) && (agentValence >= 0.0))
			op = true;
		else if((eventValence < 0.0) && (agentValence < 0.0))
			op = true;
		
		de = false;
		if(triplet.object.type.equalsIgnoreCase("NNP") || triplet.object.type.equalsIgnoreCase("PRP"))
			de = true;
		
		pros = 0; 

		if(prospectiveValue > 4.0)
			pros = 1;
		else if(prospectiveValue < -4.0)
			pros = -1;
		sa = 0;
		
		if(praiseworthValue > 3.0)
			sa = 1;
		else if(praiseworthValue < -3.0)
			sa = -1;
		
		 statusTmp = triplet.verb.type.equalsIgnoreCase("VBD") || triplet.verb.type.equalsIgnoreCase("VBN");
		if( !statusTmp )
			status = 0;
		else if( triplet.verb.valence >= 0.0  )
			status = +1;
		else if(  triplet.verb.valence < 0.0 && !negation )
			status = +1;
		else
			status = -1;
		
		af = false;
		if(triplet.subject.valence > 0.0)
			af = true;

		of = false;
		if(triplet.object.valence > 0.0)
			of = true;
		
		vr = false;
		if((sentenceValence > 3.5) || (sentenceValence < -3.5))
			vr = true;
		
		ArrayList<String> termsInTriplet = new ArrayList<String>();
		termsInTriplet = getTermsFromTriplet( triplet );
		
		ed = eventValence >= 0.7 ? true : false;
		eoa = findeoa( triplet );
		edev = findedev( triplet );
		ef = findef( triplet );
		applyEmotionRules(termsInTriplet, emotions, eventValence);		
		
		return emotions;
	}
		
	private boolean findef(Triplet triplet) {
		return false;
	}

	private boolean findedev(Triplet triplet) {
		HashSet<String> relations = Conceptnet.relationsWithSubject( triplet.subject.name.toLowerCase() );	
		return relations.contains( triplet.verb.name.toLowerCase() );
	}

	private boolean findeoa(Triplet triplet) {
		boolean ok = false;
		List<Entity> attrib = triplet.verb.attributes;
		for( int i = 0; i < attrib.size(); i++){
			ok = ok || ValenceProvider.isExpection( attrib.get( i ).name );
		}
		return ok;
	}
	

	public void applyEmotionRules( ArrayList<String> termsInTriplet, HashSet<String> emotions, double eventValence ){
		if( isJoy( termsInTriplet ) )
			emotions.add(JOY);
		
		if( isDistress( termsInTriplet ) )
			emotions.add( DISTRESS );
		
		if( isHappyFor( termsInTriplet ) )
			emotions.add( HAPPYFOR );
		
		if( isSorryFor( termsInTriplet ) )
			emotions.add( SORRYFOR );
		
		if( isResentment( termsInTriplet ) )
			emotions.add( RESENTMENT );
		
		if( isGloating( termsInTriplet ) )
			emotions.add( GLOATING );
		
		if( isHope( termsInTriplet ) )
			emotions.add( HOPE );
		
		if( isFear( termsInTriplet ) )
			emotions.add( FEAR );
		
		if( isSatisfaction( termsInTriplet ) )
			emotions.add( SATISFACTION );
		
		if( isFearConfirmed( termsInTriplet ) )
			emotions.add( FEARSCONFIRMED );
		
		if( isRelief( termsInTriplet ) )
			emotions.add( RELIEF );
		
		if( isDisappointment( termsInTriplet ) )
			emotions.add( DISAPPOINTMENT );
		
		if( isShame( termsInTriplet ) )
			emotions.add( SHAME );
		
		if( isPride( termsInTriplet ) )
			emotions.add( PRIDE );
		
		if( isAdmiration( termsInTriplet ) )
			emotions.add( ADMIRATION );
		
		if( isReproach( termsInTriplet ) )
			emotions.add( REPROACH );
		
		if( isLove( termsInTriplet, eventValence) )
			emotions.add( LOVE );
		
		if( isHate( termsInTriplet, eventValence) )
			emotions.add( HATE );
			
		isGratification(emotions);
		isRemorse(emotions);
		isGratitude(emotions);
		isAnger( emotions );
		isShock(emotions);
		isSurprise(emotions);	
	}
	
	public HashSet<String> applyAndLogic( HashSet<String> triplet1, HashSet<String> triplet2 ){
		HashSet<String> emotions = new HashSet<String>();
		
		emotions.addAll( triplet1 );
		emotions.addAll( triplet2 );
		
		if( emotions.contains( HOPE ) && emotions.contains( SATISFACTION ) )
			emotions.remove( HOPE );
		
		if( emotions.contains( FEAR ) && emotions.contains( FEARSCONFIRMED ) )
			emotions.remove( FEAR) ;

		if( emotions.contains( PRIDE) && emotions.contains( GRATIFICATION ) )
			emotions.remove( PRIDE) ;

		if( emotions.contains( SHAME ) && emotions.contains( REMORSE ) )
			emotions.remove(SHAME) ;

		if( emotions.contains( ADMIRATION ) && emotions.contains( GRATITUDE ) )
			emotions.remove( ADMIRATION ) ;
		
		if( emotions.contains( REPROACH ) && emotions.contains( ANGER ) )
			emotions.remove( REPROACH) ;

		if( emotions.contains( GRATITUDE ) && emotions.contains( GRATIFICATION ) )
			emotions.remove( GRATIFICATION) ;

		if( emotions.contains( REMORSE ) && emotions.contains( ANGER ) )
			emotions.remove( REMORSE ) ;
			
		return emotions;
	}
	
	public HashSet<String> applyButLogic( HashSet<String> triplet1, HashSet<String> triplet2 ){
		HashSet<String> emotions = new HashSet<String>();
		if( ( triplet1.contains( FEARSCONFIRMED ) || triplet1.contains( FEAR ) ) && triplet2.contains( SATISFACTION )) {
			triplet2.remove(SATISFACTION);
			emotions.add( RELIEF );
		}
		
		if( triplet1.contains( HOPE )  && ( triplet2.contains( FEAR ) || triplet2.contains( FEARSCONFIRMED ) ) ) {
			triplet2.remove(FEARSCONFIRMED);
			triplet2.remove(FEAR);
			emotions.add( DISAPPOINTMENT );
		}
		
		if( ( triplet1.contains( ANGER ) || triplet1.contains( REMORSE ))  && ( triplet2.contains( GRATIFICATION ) || triplet2.contains( GRATITUDE ) ) ) {
			triplet2.remove(GRATIFICATION);
			emotions.add( GRATITUDE );
		}
		
		if( ( triplet1.contains( GRATIFICATION ) || triplet1.contains( GRATITUDE ) )  && ( triplet2.contains( ANGER ) || triplet2.contains( REMORSE ) ) ) {
			triplet2.remove( REMORSE );
			emotions.add( ANGER );
		}
		emotions.addAll( triplet2 );
		
		return emotions;
	}
	
	private void isGratification( HashSet<String> emotions ){
		if( emotions.contains( JOY ) && emotions.contains( PRIDE ) ){
			emotions.add( GRATIFICATION );
			emotions.remove( JOY ); emotions.remove( PRIDE );
		}
	}
	
	private void isRemorse( HashSet<String> emotions ){
		if( emotions.contains( DISTRESS ) && emotions.contains( SHAME ) ){
			emotions.add( REMORSE );
			emotions.remove( DISTRESS ); emotions.remove( SHAME );
		}
	}
	
	private void isGratitude( HashSet<String> emotions ){
		if( emotions.contains( JOY ) && emotions.contains( ADMIRATION ) ){
			emotions.add( GRATITUDE );
			emotions.remove( JOY ); emotions.remove( ADMIRATION );
		}
	}

	private void isAnger( HashSet<String> emotions ){
		if( emotions.contains( DISTRESS  ) && emotions.contains( REPROACH ) ){
			emotions.add( ANGER );
			emotions.remove( DISTRESS ); emotions.remove( REPROACH );
		}
	}
	
	private void isShock( HashSet<String> emotions ){
		if( emotions.contains( DISTRESS ) && unexp ){
			emotions.add( SHOCK );
			emotions.remove( DISTRESS );
		}
	}
	
	private void isSurprise( HashSet<String> emotions ){
		if( emotions.contains( JOY ) && unexp ){
			emotions.add( SURPRISE );
			emotions.remove( JOY );
		}
	}
	
	private boolean isJoy( ArrayList<String> terms ){
		return vr && sr && sp;
	}
	
	private boolean isDistress( ArrayList<String> terms ){
		return vr && !sr && !sp && !de;
	}
	
	private boolean isSorryFor( ArrayList<String> terms ){
		return vr && !sr  && !op && af && de;
	}
	
	private boolean isHappyFor( ArrayList<String> terms ){
		return vr && sr && op && af && de;
	}
	
	private boolean isResentment( ArrayList<String> terms ){
		return vr && !sr && op && !af && de;
	}
	
	private boolean isGloating( ArrayList<String> terms ){
		return vr && sr && !op && !af && de;
	}
	
	private boolean isHope( ArrayList<String> terms ){
		return vr && sr && pros > 0  && sp && status == 0 && !de;
	}
	
	private boolean isFear( ArrayList<String> terms ){
		return vr && !sr && pros < 0 && !sp && status == 0 && !de;
	} 
	
	private boolean isSatisfaction( ArrayList<String> terms ){
		return vr && sr && pros > 0 && sp && status == 1 && !de;
	} 
	
	private boolean isFearConfirmed( ArrayList<String> terms ){
		return vr && !sr && pros < 0 && !sp && status == 1 && !de;
	} 
	
	private boolean isRelief( ArrayList<String> terms ){
		return vr && sr && pros < 0 && !sp && status < 0  && !de;
	}
	
	private boolean isDisappointment( ArrayList<String> terms ){
		return vr && !sr && pros > 0 && !sp && status < 0 && !de;
	}
	
	private boolean isPride( ArrayList<String> terms ){
		return vr && sr && sa > 0 && sp && !de;
	}
	
	private boolean isShame( ArrayList<String> terms ){
		return vr && !sr && sa < 0  && !sp  && !de;
	}
	
	private boolean isAdmiration( ArrayList<String> terms ){
		return vr && sr && sa > 0 && op  && de;
	}
	
	private boolean isReproach( ArrayList<String> terms ){
		return vr && !sr && sa < 0 && !op && de;
	}
	
	private boolean isLove( ArrayList<String> terms, double eventValence ){
		return vr && sp && sr && of && oa && eventValence > 0.0 && de;
	}
	
	private boolean isHate( ArrayList<String> terms, double eventValence ){
		return vr && !sp && !sr && !of && !oa && eventValence < 0.0 && de;
	}
	
	private static boolean hasNegation(Entity verb) {
		boolean ok = false;
		for( int i = 0; i < verb.attributes.size(); i++){
			String type =  verb.attributes.get(i).name.trim() ;
			ok = ok || type.equalsIgnoreCase( "not");
		}
		return ok;
	}
	
	public static ArrayList<String> getTermsFromTriplet( Triplet t ){
		ArrayList<String> words_triplets=new ArrayList<String>();
		words_triplets.add(t.subject.name);
		for(int x=0; x<t.subject.attributes.size(); x++)
			words_triplets.add(t.subject.attributes.get(x).name);
		words_triplets.add(t.object.name);
		for(int y=0; y<t.object.attributes.size(); y++)
			words_triplets.add(t.object.attributes.get(y).name);
		words_triplets.add(t.verb.name);
		for(int z=0; z<t.verb.attributes.size(); z++)
			words_triplets.add(t.verb.attributes.get(z).name);
		return words_triplets;
	}

	private static ArrayList<Triplet> extractTriplets( String sentence ){
		ArrayList<Triplet> list = null;
		list = TripletExtractor.extractAllTriplets(sentence);
		return list;
	}
	
	private static double getValance( ArrayList<Triplet> triplets ){
		double valence = 0.0;
		try{
			valence = ValenceProvider.Get_Valence_For_Sentence( triplets );
		}catch (Exception e) {
			e.printStackTrace();
		}
		return valence;
	}
	
	private static String[] tests = new String[]{
		"Tokyo stocks likely to be stuck in narrow range.",
		"what do the toys do when Andy is not with them, they come to life",
		"I did not see John for the last few hours; I thought he might miss the flight but I suddenly found him on the plane.",
		"A little boy named Andy loves to be in his room, playing with his toys, especially his doll named Woody",
		"Bomb destroyed many houses in the last few hours"
	};
		
	public static void main(String[] args) throws IOException {	
//		System.setProperty("http.proxyHost", "wwwcache.dcs.gla.ac.uk");
//		System.setProperty("http.proxyPort", "8080");
	
		ValenceProvider.loadDataStores();
		EmotionExtractor emoExtact = new EmotionExtractor();		
		String sentence = tests[ tests.length - 1 ];
		double valence = 0.0;
		
		ArrayList<Triplet> triplets = null;
		HashSet<String> emotionsForTriplet1 = null;
		triplets = extractTriplets( sentence );
		valence = getValance( triplets );
		emotionsForTriplet1 = emoExtact.getEmotions(triplets, valence);
		
		System.out.println( sentence );
		System.out.println( triplets );
		System.out.println( valence );
		System.out.println( emotionsForTriplet1 );
	}
}

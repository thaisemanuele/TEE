package uk.ac.gla.mir.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import uk.ac.gla.mir.entity.Entity;
import uk.ac.gla.mir.triplets.Triplet;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;
import gnu.trove.TIntHashSet;
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
public class TripletExtractor {
	private static String lexicalParserFile = "stanford-parser/englishPCFG.ser.gz";
	private static LexicalizedParser lp;
	private static ArrayList<Triplet> triplets;
	private static ArrayList<Triplet> tmpTriplets;
	private static boolean nounFound = false;
	private static Entity e;
	private static int dependancyDepth = 0;
	private static String dependancy = "";
	private static String toDependancy = "";
	private static int verbDepth;
	private static Tree deepestVerbPhrase;
	private static Tree deepestSentence;
	private static int depthOfSentence;
	private static int childNumberOfDeepestSenctence = 0;
	
	public static final boolean isSentenceStart( final Tree treeNode ){
		return treeNode.value().equalsIgnoreCase("S") || treeNode.value().equalsIgnoreCase("SQ") ||
				treeNode.value().equalsIgnoreCase("FRAG") || treeNode.value().equalsIgnoreCase("SINV");
	}
	public static final boolean isCC( final Tree treeNode ){
		return treeNode.value().equalsIgnoreCase("CC");
	}
	public static final boolean isVerb( final Tree treeNode){
		return	 treeNode.value().equalsIgnoreCase("VB") || 
				 treeNode.value().equalsIgnoreCase("VBD") ||
				 treeNode.value().equalsIgnoreCase("VBG") ||
				 treeNode.value().equalsIgnoreCase("VBN") ||
				 treeNode.value().equalsIgnoreCase("VBP") ||
				 treeNode.value().equalsIgnoreCase("VBZ");
	}
	public static final boolean isVerbPhrase( final Tree treeNode){
		return	 treeNode.value().equalsIgnoreCase("VP"); 
	}
	public static final boolean isNoun( final Tree treeNode){
		return	 treeNode.value().equalsIgnoreCase("NN") || 
				 treeNode.value().equalsIgnoreCase("NNP") ||
				 treeNode.value().equalsIgnoreCase("NNPS") ||
				 treeNode.value().equalsIgnoreCase("NNS") ||
				 treeNode.value().equalsIgnoreCase("PRP");
	}
	public static final boolean isNoun( final Entity treeNode){
		return	 treeNode.type.equalsIgnoreCase("NN") || 
				 treeNode.type.equalsIgnoreCase("NNP") ||
				 treeNode.type.equalsIgnoreCase("NNPS") ||
				 treeNode.type.equalsIgnoreCase("NNS");
	}
	public static final boolean isNounPhrase( final Tree treeNode){
		return	treeNode.value().equalsIgnoreCase("NP");
	}
	public static final boolean isNounPhraseAttrib( final Tree treeNode ){
		return treeNode.value().equalsIgnoreCase("NP") || treeNode.value().equalsIgnoreCase("NP-TMP");
	}
	public static final boolean isAdjective( final Tree treeNode){
		return	 treeNode.value().equalsIgnoreCase("JJ") || 
				 treeNode.value().equalsIgnoreCase("JJR") ||
				 treeNode.value().equalsIgnoreCase("JJS");
	}
	public static final boolean isAdjectivePhrase( final Tree treeNode){
		return treeNode.value().equalsIgnoreCase("ADJP");
	}
	public static final boolean isAdVerbPhrase( final Tree treeNode){
		return treeNode.value().equalsIgnoreCase("ADVP");
	}
	public static final boolean isPrepositionPhrase( final Tree treeNode){
		return	treeNode.value().equalsIgnoreCase("PP");
	}
	public static final boolean isAttribForNoun( final Tree treeNode){
		return treeNode.value().equalsIgnoreCase("PRP$") ||
			   treeNode.value().equalsIgnoreCase("POS")  ||
			   treeNode.value().equalsIgnoreCase("JJ") || 
			   treeNode.value().equalsIgnoreCase("CD") ||
			   treeNode.value().equalsIgnoreCase("QP") ||
			   treeNode.value().equalsIgnoreCase("IN") ||
			   treeNode.value().equalsIgnoreCase("RB");
	}
	public static final boolean isAttribForNounPhrase( final Tree treeNode){
		return treeNode.value().equalsIgnoreCase("ADJP") ||
		   treeNode.value().equalsIgnoreCase("NP");
	}
	public static final boolean isAttribForVerb( final Tree treeNode){
		return treeNode.value().equalsIgnoreCase("RB") ;
	}
	
	private static void getTriplets( final Tree sentence , List<Tree> leaves){
		Comparator<KeyValue<Integer, Entity>> cmp = new Comparator< KeyValue<Integer, Entity> >() {
			public int compare(KeyValue<Integer, Entity> o1, KeyValue<Integer, Entity> o2) {
				return o1.getValue().compareTo( o2.getValue() );
			}
		};
		deepestSentence = null;
		dependancy = "";
		toDependancy = "";
		e = null; deepestVerbPhrase = null; dependancyDepth = 0;		
		depthOfSentence = -10;
		childNumberOfDeepestSenctence = -1;
		getDeepestSentence(sentence, 0);
		if( deepestSentence != null ){
			tmpTriplets = new ArrayList<Triplet>();
			Triplet tempTriplet = extractTriplet( deepestSentence, leaves );
			final Tree ancestor = deepestSentence.ancestor(1, sentence);
			if( ancestor == null )
				return;
			if( childNumberOfDeepestSenctence != -1){
				addAttribute(cmp, sentence, deepestSentence );
				ancestor.removeChild( childNumberOfDeepestSenctence );	
			}
			if( ancestor.value().equalsIgnoreCase("ROOT") ){
				return;
			}
			else{	
				Tree parentOfSentence = ancestor.ancestor(1, sentence);
				int clauseIndex = parentOfSentence.indexOf( ancestor );
				for( int j = clauseIndex-1; j >= 0; j--){
					Tree lastNoun = parentOfSentence.getChild(j);
					nounFound = false;
					e = new Entity();
					extractLastNoun( lastNoun );
					tempTriplet.lastNP = e;
					if( !tempTriplet.lastNP.isEmpty() )
						if( tempTriplet.subject.isEmpty() ){
							tempTriplet.subject = e;
							tempTriplet.subject.attributes = getSubjectAttributes( e.tree.ancestor(1, sentence), true, sentence, e.tree);
						}
					if( !tempTriplet.lastNP.isEmpty() )
						break;
				}
			}
			getTriplets(sentence, leaves);
			
		}else{
			tmpTriplets = new ArrayList<Triplet>();
			Triplet tempTriplet = extractTriplet( sentence, leaves );
			addAttribute(cmp, sentence, sentence );
		}	
	}
	private static void addAttribute( Comparator cmp , Tree rootSentence, Tree deepSentence){
		List<Tree> leaves = deepSentence.getLeaves();
		TIntHashSet intSet = new TIntHashSet();
		ArrayList< KeyValue<Integer, Entity> >  tripletEntity2Index = new ArrayList< KeyValue<Integer, Entity> >();
		for( int i = 0; i < tmpTriplets.size(); i++){
			Triplet triplet = tmpTriplets.get( i );
			if( triplet.subject != null && !triplet.subject.name.trim().equalsIgnoreCase("") ){
				Entity subject = triplet.subject;
				subject.attributes.clear();
				int value = findIndex( subject.name,  leaves );
				if( !intSet.contains( value ) ){
					KeyValue<Integer, Entity> keyValue = new KeyValue<Integer, Entity>( subject, value);
					tripletEntity2Index.add(  keyValue );
					intSet.add(value);
				}
			}
			if( triplet.verb != null && !triplet.verb.name.trim().equalsIgnoreCase("") ){
				Entity verb = triplet.verb;
				verb.isVerb = true;
				verb.attributes.clear();
				int value = findIndex( verb.name,  leaves );
				if( !intSet.contains( value ) ){
					KeyValue<Integer, Entity> keyValue = new KeyValue<Integer, Entity>( verb, value);
					tripletEntity2Index.add(  keyValue );
					intSet.add(value);
				}
			}
			if( triplet.object != null && !triplet.object.name.trim().equalsIgnoreCase("") ){
				Entity object = triplet.object;
				object.attributes.clear();
				int value = findIndex( object.name,  leaves );
				if( !intSet.contains( value ) ){
					KeyValue<Integer, Entity> keyValue = new KeyValue<Integer, Entity>( object, value);
					tripletEntity2Index.add(  keyValue );
					intSet.add( value);
				}
			}
		}
						
		Entity closetVerb = null;
		if(tripletEntity2Index.size() == 0)
			return;
		
		int EndInterval = 0, StartInterval = -1;
		for(int i = 0; i < leaves.size(); i++){
			final KeyValue<Integer, Entity> keyValue = tripletEntity2Index.get(EndInterval);
			Entity entity = keyValue.getKey();
			if( entity.isVerb )
				closetVerb = entity;
			int index = keyValue.getValue();
			
			Tree leaf = leaves.get(i);
			if(i == index){
				StartInterval = index;
				EndInterval++;
				
				if( EndInterval == tripletEntity2Index.size() ){
					break;
				}
				
			} else if(i > StartInterval && i < index){
				Entity attrib = new Entity( leaf.value(), leaf.ancestor(1, rootSentence).value() );
				if( leaf.ancestor(1, rootSentence).value().equalsIgnoreCase("RP")  &&  closetVerb != null && !closetVerb.isEmpty())
					closetVerb.name += " " + leaf;
				else
					entity.attributes.add( attrib );
			}

		}
		
		for( int i = StartInterval+1; i < leaves.size(); i++){
			final KeyValue<Integer, Entity> keyValue = tripletEntity2Index.get(EndInterval-1);
			Entity entity = keyValue.getKey();
			Tree leaf = leaves.get(i);
			Entity attrib = new Entity( leaf.value(), leaf.ancestor(1, rootSentence).value() );

			if( leaf.ancestor(1, rootSentence).value().equalsIgnoreCase("RP") &&  closetVerb != null && !closetVerb.isEmpty() )
				closetVerb.name += " " + leaf;
			else
				entity.attributes.add( attrib );
		}
	}
	private static int findIndex(String entityName, List<Tree> leaves) {

		for( int i = 0; i < leaves.size(); i++){
			Tree tree = leaves.get( i );
			String currentNodeName = tree.value();
			
			currentNodeName = currentNodeName.trim();
			entityName = entityName.trim();
			
			if(currentNodeName.equalsIgnoreCase(entityName))
				return i;
		}
		return -1;
	}
	private static Triplet extractTriplet(Tree sentence, List<Tree> leaves) {
		
		Triplet tempTriplet = new Triplet();
		final Tree root = sentence;
		
		Tree firstLeaveTerm = root.getLeaves().get(0);
		
		int indexOffirstLeaveTermInTheSentence = -1;
		for(int i = 0; i < leaves.size(); i++){
			if(leaves.get(i) == firstLeaveTerm){
				indexOffirstLeaveTermInTheSentence =  i;
				break;
			}
		}
		
		try{
			Entity subject = getSubject( sentence, root, false );
			if( subject.isEmpty() )
				subject = getSubject(sentence, root, true);
			List<Tree> verbPhrases = getVerbPhrases( sentence );
			if( verbPhrases.size() == 0 ){
				tempTriplet = new Triplet();
				tempTriplet.subject = subject;
				if( !tempTriplet.isEmpty() ){
					triplets.add( tempTriplet );
					tmpTriplets.add( tempTriplet );
					tempTriplet.index = indexOffirstLeaveTermInTheSentence;
				}
			}
			
			for( int i = 0;i < verbPhrases.size(); i++){
				tempTriplet = new Triplet();
				Tree verbPhrase = verbPhrases.get(i);
				deepestVerbPhrase = null;
				Entity verb = getVerb( verbPhrase, root, false );
				if( verb.isEmpty() ){
					verb = getVerb( verbPhrase, root, true );
				}
				if( verb.isEmpty() )
					continue;
				Entity object = getObject( deepestVerbPhrase, root, subject.tree);
				if( object.isEmpty() ){
					object = getObject( deepestVerbPhrase.ancestor(1, root), root, subject.tree);
				}
					
				tempTriplet.subject = subject;
				tempTriplet.verb = verb;
				tempTriplet.object = object;
				
				if( !tempTriplet.isEmpty() ){					
					for(int j = 0; j < leaves.size(); j++){
						if(leaves.get(j) == verb.tree.getChild(0)){
							indexOffirstLeaveTermInTheSentence =  j;
							break;
						}
					}
					tempTriplet.index = indexOffirstLeaveTermInTheSentence;
					
					
					triplets.add( tempTriplet );
					tmpTriplets.add( tempTriplet );
				}
			}
		}catch (Exception e) {
			System.err.println( sentence );
			e.printStackTrace();
		}
		return tempTriplet;
	}
	private static List<Tree> getVerbPhrases( final Tree sentence ) {
		List<Tree> list = new ArrayList<Tree>();
		final List<Tree> childList = sentence.getChildrenAsList();
		for( int i = 0; i < childList.size(); i++){
			final Tree tempTree = childList.get(i);
			if( isVerbPhrase( tempTree ) ){
				List<Tree> tempList = tempTree.getChildrenAsList();
				for( int j = 0; j < tempList.size(); j++){
					Tree t =  tempList.get( j );
					if( isVerbPhrase( t ) )
						list.add( tempList.get(j) );
				}
				if( list.size() == 0 )
					list.add( tempTree );
			}
		}
		return list;
	}

	public static void extractLastNoun( Tree sentence ){
		List<Tree> tempList = sentence.getChildrenAsList();
		for( int i = tempList.size()-1; i >= 0; i--){
			if( nounFound )
				break;
			Tree tempTree = tempList.get( i );
			if( TripletExtractor.isNoun( tempTree ) && tempTree.getChildrenAsList().size() > 0){
				e.name = tempTree.getChild(0).toString();
				e.tree  = tempTree;
				e.type = tempTree.value();
				nounFound = true;
			}else{
					extractLastNoun( tempTree);
			}
		}
	}
	private static Entity getSubject( final Tree sentence, final Tree root, boolean tryAdjective ){
		Entity e = new Entity();
		final List<Tree> childList = sentence.getChildrenAsList();
		for( int i = 0; i < childList.size(); i++){
			final Tree tempTree = childList.get(i);
			if( isNounPhrase( tempTree )  || ( tryAdjective && isAdjectivePhrase( tempTree ))){
				Tree subject = getSubjectNode( tempTree, tryAdjective );
				if( subject != null && subject.getChildrenAsList().size() > 0  ){
					e.tree = tempTree;
					e.attributes = getSubjectAttributes( tempTree, true, root, subject);
					e.name = subject.getChild(0).value();
					e.type = subject.value();
				}
			}
			if( isVerbPhrase( tempTree ) )
				break;
		}
		return e;
	}
	private static List<Entity> getSubjectAttributes(final Tree tempTree, boolean searchUncles, final Tree root, final Tree objectOrSubject) {
		
		final ArrayList<Entity> list = new ArrayList<Entity>();
		
		final List<Tree> childList = tempTree.getChildrenAsList();
		for( int i = 0; i < childList.size(); i++){
			Tree sibling = childList.get(i);
			if( ( isAttribForNoun ( sibling ) ||  isNoun( sibling ) ) &&  sibling != objectOrSubject ){
				if( sibling.getChildrenAsList().size() > 0 )
					list.add( new Entity(sibling.getChild(0).value(), sibling.value() ) );
			}else if( isAttribForNounPhrase( sibling ) ){
				list.addAll( getSubjectAttributes( sibling, false, root, objectOrSubject ) );
			}
		}
		
		if( searchUncles ){
			Tree parent = tempTree.ancestor(1, root);		
			final List<Tree> childList2 = parent.getChildrenAsList();
			for( int i = 0; i < childList2.size(); i++){
				Tree uncle = childList2.get(i);
				if( ( isAdjectivePhrase(uncle) || isPrepositionPhrase( uncle ) || isNounPhraseAttrib(uncle) ) && uncle != tempTree ){
					List<Entity> tmp = getSubjectAttributes( uncle, false, root, objectOrSubject);
					list.addAll( tmp );
				}
			}
		}
		return list;
	}
	private static List<Entity> getVerbAttributes(final Tree tempTree, final  boolean searchUncles, final Tree root, final Tree verb) {
		
		final ArrayList<Entity> list = new ArrayList<Entity>();
		
		final List<Tree> childList = tempTree.getChildrenAsList();
		for( int i = 0; i < childList.size(); i++){
			Tree sibling = childList.get(i);
			if( isAttribForVerb( sibling ) ){
				if( sibling.getChildrenAsList().size() > 0 )
					list.add( new Entity(sibling.getChild(0).value(), sibling.value() ) );
			}else if( sibling.value().equalsIgnoreCase("ADVP") || sibling.value().equalsIgnoreCase("PRT")){
				List<Tree> tmp = sibling.getChildrenAsList();
				for( int j = 0; j < tmp.size();  j++){
					Tree tr = tmp.get(j);
					if( tr.getChildrenAsList().size() > 0 )
						list.add( new Entity(tr.getChild(0).value(), tr.value() ) );
				}
			}
		}
		
		if( searchUncles ){
			Tree parent = tempTree.ancestor(1, root);		
			final List<Tree> childList2 = parent.getChildrenAsList();
			for( int i = 0; i < childList2.size(); i++){
				Tree uncle = childList2.get(i);
				if( ( isVerb( uncle ) || uncle.value().equalsIgnoreCase("RB") || uncle.value().equalsIgnoreCase("MD") )&& uncle != verb  ){
					if( uncle.getChildrenAsList().size() > 0 )
						list.add( new Entity(uncle.getChild(0).value(), uncle.value() ) );
				}
				if( isAdjectivePhrase( uncle ) || isAdVerbPhrase( uncle ) ){
					list.addAll( getVerbAttributes( uncle, false, root, verb));
				}
			}
		}
		return list;
	}
	private static Entity getObject( final Tree sentence, final Tree root, final Tree subjectTree ){
		Entity e = new Entity();
		if( sentence == null )
			return e;
		final List<Tree> childList = sentence.getChildrenAsList();
		for( int i = 0; i < childList.size(); i++){
			final Tree tempTree = childList.get(i);
			if( ( isNounPhrase( tempTree ) || isPrepositionPhrase( tempTree ) ) && tempTree != subjectTree){
				Tree object = getSubjectNode( tempTree, false  );
				if( object != null && object.getChildrenAsList().size() > 0 ){
					e.type = object.value();
					e.attributes = getSubjectAttributes( tempTree, true, root, object );
					e.name = object.getChild(0).value();
					e.tree = object;
				}
			}else if( isAdjectivePhrase( tempTree ) && tempTree != subjectTree ){
				Tree object = getAdjectiveNode( tempTree );
				if( object != null  && object.getChildrenAsList().size() > 0 ){
					e.attributes = getSubjectAttributes( tempTree, true, root, object );
					e.name = object.getChild(0).value();
					e.type = object.value();
					e.tree = object;
				}
			}
			if( !e.name.trim().equalsIgnoreCase("") ){
				break;
			}
		}
		return e;
	}
	private static Tree getAdjectiveNode( final Tree phrase  ){
		final List<Tree> childList = phrase.getChildrenAsList();
		for( int i = 0; i < childList.size(); i++){
			final Tree child = childList.get(i);
			if( isAdjective( child ) ){
				return child;
			}else if( isAdjectivePhrase( child ) )
				return getAdjectiveNode( child );
			else if( isNoun( child ) )
				getSubjectNode( child, false );
		}
		return null;
	}
	private static Tree getSubjectNode(Tree tempTree, boolean tryAdj) {
		final List<Tree> childList = tempTree.getChildrenAsList();
		Tree subject = null;
		for( int i = 0; i < childList.size(); i++){
			if( subject != null)
				break;
			final Tree child = childList.get(i);
			if( isNoun( child ) || child.value().equalsIgnoreCase("VBG") || ( tryAdj && isAdjective( child )) ){
				subject = child;
			}else{
				Tree tmp = getSubjectNode( child, tryAdj  );
				if( tmp != null ){
					subject = tmp;
				}
			}
		}
		return subject;
	}
	private static Entity getVerb( final Tree sentence, final Tree root, final boolean checkWordNet){
		Entity e = new Entity();
		verbDepth = 0;
		deepestVerbPhrase = sentence;
		Tree verb = getDeepestVerb( sentence, 0, checkWordNet  );
		if( verb != null  && verb.getChildrenAsList().size() > 0 ){
			List<Entity> attribs = getVerbAttributes( deepestVerbPhrase, true, root, verb);
			e.attributes = attribs;
			e.type = verb.value();
			e.name = verb.getChild(0).value();
			e.tree = verb;
			e.treeParent = sentence;
		}
		return e;
	}

	private static Tree getDeepestVerb(Tree tempTree, int depth, boolean checkwordNet ) {
		Tree verb = null;
		final List<Tree> childList = tempTree.getChildrenAsList();
		for( int i = 0; i < childList.size(); i++){
			final Tree child = childList.get(i);
			String word = child.getChildrenAsList().size() > 0 ? child.getChild(0).value() : "";
			
			if( ( isVerb( child ) || checkwordNet  ) &&  depth >= verbDepth ){
				deepestVerbPhrase = tempTree;
				verbDepth = depth;
				verb = child;
			}else{
				if( !isNounPhrase( child ) &&  !child.value().equalsIgnoreCase("ADVP") && !child.value().equalsIgnoreCase("PRT")){
					Tree tmp = getDeepestVerb( child, depth+1,checkwordNet);
					if( tmp != null )
						verb = tmp;
				}
			}
		}
		return verb;
	}
	private static void getDeepestSentence( final Tree sentence, final int inDepth ){
		final List<Tree> children = sentence.getChildrenAsList();
		for( int i = children.size()-1; i >= 0; i--){
			final Tree tempTree = children.get( i );
			if( isSentenceStart( tempTree ) ){
				if( inDepth >= depthOfSentence ){
					deepestSentence = tempTree;
					depthOfSentence = inDepth;
					childNumberOfDeepestSenctence = i;
				}
			}
			getDeepestSentence( tempTree, inDepth+1);
		}
	}
		
	private static void findDependencies(List<Tree> leaves,
		ArrayList<Triplet> triplets2, List<Tree> parentsOfLeaves) {
		
		if(triplets2.size() <= 1)
			return;
		
		for(int i = 0; i < leaves.size() -1 ; i++){
			Tree leaf = leaves.get(i);
			Tree nextLeaf = leaves.get(i+1);
			Tree parentNextLeaf = parentsOfLeaves.get(i+1);
			if(isToDependencies(leaf)){
				if(isVerb( parentNextLeaf ) ){					
					setToDepencency(triplets2, i);
				}
			}else if(isButDependencies(leaf)){
				setButDepencency(triplets2,i);	
			}else if(isAndDependencies(leaf)){
				setAndDepencency(triplets2,i);	
			}else if(isNoToDependencies(leaf)){
				setNoToDepencency(triplets2,i);	
			}
		}
	}
	private static void setNoToDepencency(ArrayList<Triplet> triplets2,
			int index ) {
		for(int i = 0; i < triplets2.size(); i++){
			if(triplets2.get(i).index >= index){
				if(i-1 < 0)
					triplets2.get(0).notToDependancy = "not-to";
				else
					triplets2.get(i-1).notToDependancy = "not-to";
				break;
			}
		}
	}
	private static void setAndDepencency(ArrayList<Triplet> triplets2, int index) {
		for(int i = 0; i < triplets2.size(); i++){
			if(triplets2.get(i).index >= index){
				if(i-1 < 0)
					triplets2.get(0).andDependancy = "and";
				else
					triplets2.get(i-1).andDependancy = "and";
				break;
			}
		}		
	}
	private static void setButDepencency(ArrayList<Triplet> triplets2, int index) {
		for(int i = 0; i < triplets2.size(); i++){
			if(triplets2.get(i).index >= index){
				if(i-1 < 0)
					triplets2.get(0).butDependancy = "but";
				else
					triplets2.get(i-1).butDependancy = "but";
				break;
			}
		}		
	}
	private static void setToDepencency(ArrayList<Triplet> triplets2, int index) {
		for(int i = 0; i < triplets2.size(); i++){
			if(triplets2.get(i).index >= index){
				if(i-1 < 0)
					triplets2.get(0).toDependancy = "to";
				else
					triplets2.get(i-1).toDependancy = "to";
				break;
			}
		}
	}
	
	private static boolean isToDependencies(Tree t) {
		return t.toString().equalsIgnoreCase("to");
	}
	private static boolean isNoToDependencies(Tree t) {
		if(t.value().equalsIgnoreCase("WP") || 
				t.value().equalsIgnoreCase("WDT") || 
				t.toString().equalsIgnoreCase("that")
				){
			return true;
		}
		return false;
	}
	private static boolean isAndDependencies(Tree t) {
		return t.toString().equalsIgnoreCase("and");
	}
	private static boolean isButDependencies(Tree t) {
		return t.toString().equalsIgnoreCase("but") || 
			   t.toString().equalsIgnoreCase("though") || 
			   t.toString().equalsIgnoreCase("although"); 
	}	
	public static void printAsList( Tree tree ){
		List<Tree> list = tree.getLeaves();
		for( int i = 0; i < list.size(); i++){
			printTree( list.get(i) );
		}
	}
	public static void printTree( Tree tree ){
		System.out.println( tree );
	}

	public static ArrayList<Triplet> extractAllTriplets( String sentence){
		
		lp = new LexicalizedParser( lexicalParserFile );
		lp.setOptionFlags(new String[]{"-maxLength", "500", "-retainTmpSubcategories"});		
		final Tree tree = lp.apply( sentence );		
		triplets = new ArrayList<Triplet>();			
		List<Tree> leaves = tree.getLeaves();
		List<Tree> leavesParents = new ArrayList<Tree>();
		for( int i = 0; i < leaves.size(); i++){
			leavesParents.add( leaves.get(i).ancestor(1, tree));
		}		
		getTriplets( tree , leaves);		
		Collections.sort(triplets);
		Collections.sort(tmpTriplets);	
		findDependencies(leaves, triplets, leavesParents);
		return triplets;
	}
}
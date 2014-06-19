package uk.ac.gla.mir.input;

import java.io.*;
import java.util.zip.*;

/* 
 * The Original Code is Files.java.
 * From Terrier - Terabyte Retriever
 * Webpage: http://ir.dcs.gla.ac.uk/terrier
 * Contact: terrier{a.}dcs.gla.ac.uk
 * University of Glasgow - Department of Computing Science
 * http://www.ac.gla.uk
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 *
 * The Original Code is Copyright (C) 2004-2008 the University of Glasgow.
 * All Rights Reserved.
 *
 * Contributor(s):
 *   Gianni Amati <gba{a.}fub.it> (original author)
 *   Vassilis Plachouras <vassilis{a.}dcs.gla.ac.uk>
 *   Ben He <ben{a.}dcs.gla.ac.uk>
 *   Craig Macdonald <craigm{a.}dcs.gla.ac.uk>
 */
public class Files {

	public static BufferedReader openFileReader(File file) throws IOException
	{
		return openFileReader(file.getPath(),null);
	}
	
	public static BufferedReader openFileReader(String filename, String charset) throws IOException
	{
		BufferedReader rtr = null;
		if (filename.toLowerCase().endsWith("gz")) {
			rtr = new BufferedReader(
				 charset == null				
				? new InputStreamReader(new GZIPInputStream(new FileInputStream(filename)))
				: new InputStreamReader(new GZIPInputStream(new FileInputStream(filename)), charset)
			);
		} else {
			rtr = new BufferedReader(
				charset == null 
					? new FileReader(filename)
					: new InputStreamReader(new FileInputStream(filename), charset)
				);
		}
		return rtr;
	}	
}

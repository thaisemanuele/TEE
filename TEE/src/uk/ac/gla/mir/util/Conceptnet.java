package uk.ac.gla.mir.util;

import java.io.*;
import java.util.HashSet;

import omcsnet.JOMCSNetAPI;

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

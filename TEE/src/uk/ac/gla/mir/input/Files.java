package uk.ac.gla.mir.input;

import java.io.*;
import java.util.zip.*;

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

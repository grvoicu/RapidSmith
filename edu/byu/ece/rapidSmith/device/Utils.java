/*
 * Copyright (c) 2010 Brigham Young University
 * 
 * This file is part of the BYU RapidSmith Tools.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package edu.byu.ece.rapidSmith.device;

import java.util.HashSet;

/**
 * This is a helper class for creating PrimitiveTypes and TileTypes
 * as well as helping to categorize TileTypes. 
 */
public class Utils{

	private static HashSet<TileType> clbs;
	
	private static HashSet<TileType> dsps;
	
	private static HashSet<TileType> brams;
	
	private static HashSet<TileType> ints;
	
	/**
	 * Returns a PrimitiveType enum based on the given string. If such
	 * an enum does not exist, it will return null.
	 * @param s The string to be converted to an enum type
	 * @return The PrimitiveType corresponding to the string s, null if none exists.
	 */
	public static PrimitiveType createPrimitiveType(String s){
		return PrimitiveType.valueOf(s.toUpperCase());
	}

	/**
	 * Returns a TileType enum based on the given string s.  If such an enum
	 * does not exist, it will return null
	 * @param s The string to be converted to an enum type
	 * @return The TileType corresponding to String s, null if none exists.
	 */
	public static TileType createTileType(String s){
		return TileType.valueOf(s.toUpperCase());
	}
	
	/**
	 * Determines if the provided tile type contains SLICE primitive sites
	 * of any type.
	 * @param type The tile type to test for.
	 * @return True if this tile type has SLICE (any kind) primitive sites.
	 */
	public static boolean isCLB(TileType type){
		return clbs.contains(type);
	}
	
	/**
	 * Determines if the provided tile type contains DSP primitive sites
	 * of any type.
	 * @param type The tile type to test for.
	 * @return True if this tile type has DSP (any kind) primitive sites.
	 */
	public static boolean isDSP(TileType type){
		return dsps.contains(type);
	}
	
	/**
	 * Determines if the provided tile type contains BRAM primitive sites
	 * of any type.
	 * @param type The tile type to test for.
	 * @return True if this tile type has BRAM (any kind) primitive sites.
	 */
	public static boolean isBRAM(TileType type){
		return brams.contains(type);
	}
	
	/**
	 * Determines if the provided tile type contains BRAM primitive sites
	 * of any type.
	 * @param type The tile type to test for.
	 * @return True if this tile type has BRAM (any kind) primitive sites.
	 */
	public static boolean isSwitchBox(TileType type){
		return ints.contains(type);
	}

	static{
		clbs = new HashSet<TileType>();
		clbs.add(TileType.CLB);
		clbs.add(TileType.CLBLL);
		clbs.add(TileType.CLBLM);
		clbs.add(TileType.CLEXL);
		clbs.add(TileType.CLEXM);
		clbs.add(TileType.CLBLL_L);
		clbs.add(TileType.CLBLL_R);
		clbs.add(TileType.CLBLM_L);
		clbs.add(TileType.CLBLM_R);
		
		dsps = new HashSet<TileType>();
		dsps.add(TileType.DSP);
		dsps.add(TileType.DSP_L);
		dsps.add(TileType.DSP_R);
		dsps.add(TileType.MACCSITE2);
		dsps.add(TileType.MACCSITE2_BRK);
		dsps.add(TileType.BRAMSITE);
		dsps.add(TileType.BRAMSITE2);
		dsps.add(TileType.BRAMSITE2_BRK);
		
		brams = new HashSet<TileType>();
		brams.add(TileType.BRAM);
		brams.add(TileType.BRAM_L);
		brams.add(TileType.BRAM_R);
		brams.add(TileType.LBRAM);
		brams.add(TileType.RBRAM);
		brams.add(TileType.BRAMSITE);
		brams.add(TileType.BRAMSITE2);
		brams.add(TileType.BRAMSITE2_3M);
		brams.add(TileType.BRAMSITE2_3M_BRK);
		brams.add(TileType.BRAMSITE2_BRK);
		brams.add(TileType.MBRAM);

		ints = new HashSet<TileType>();
		ints.add(TileType.INT);
		ints.add(TileType.INT_L);
		ints.add(TileType.INT_R);
		ints.add(TileType.INT_SO);
		ints.add(TileType.INT_SO_DCM0);
		ints.add(TileType.INT_BRAM);
		ints.add(TileType.INT_BRAM_BRK);
		ints.add(TileType.INT_BRK);
		ints.add(TileType.INT_GCLK);
		ints.add(TileType.IOI_INT);
		ints.add(TileType.INT_TERM);
		ints.add(TileType.INT_TERM_BRK);
		ints.add(TileType.LIOI_INT);
		ints.add(TileType.LIOI_INT_BRK);
		
	}
}

/*
 * Copyright (c) 2010 Brigham Young University
 * 
 * This file is part of the BYU RapidSmith Tools.
 * 
 * BYU RapidSmith Tools is free software: you may redistribute it 
 * and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * BYU RapidSmith Tools is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License is included with the BYU 
 * RapidSmith Tools. It can be found at doc/gpl2.txt. You may also 
 * get a copy of the license at <http://www.gnu.org/licenses/>.
 * 
 */
package edu.byu.ece.rapidSmith.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartNameTools {

	private static Pattern partNamePattern = Pattern.compile("([a-z]+)|([0-9]+)|([a-z]*)|([0-9]*)|([a-z]*)");
	
	/**
	 * This method removes the speed grade (ex: -10) from a conventional Xilinx part name.
	 * @param partName The name of the part to remove the speed grade from.
	 * @return The base part name with speed grade removed.  If no speed grade is present, returns
	 * the original string.
	 */
	public static String removeSpeedGrade(String partName){
		if(partName != null && partName.contains("-")){
			return partName.substring(0, partName.indexOf("-"));
		}
		else{
			return partName;
		}
	}
	
	/**
	 * This method will take a Xilinx part name and determine its base family 
	 * architecture name. Ex: xq4vlx100 will return VIRTEX4.  For differentiating
	 * family types (qvirtex4 rather than virtex4) use getExactFamilyTypeFromPart().
	 * @param partName Name of the part
	 * @return The base family architecture type or null if invalid partName.
	 */
	public static FamilyType getFamilyTypeFromPart(String partName){
		return getBaseTypeFromFamilyType(getExactFamilyTypeFromPart(partName));
	}
	
	/**
	 * This helper method will parse a part name into smaller parts which 
	 * are alternating letters and numbers.  For example: xc5vlx110tff1136 
	 * becomes ['xc', '5', 'vlx', '110', 'tff', '1136']
	 * @param partName Part name to parse.
	 * @return The parts of the part name
	 */
	private static String[] splitPartName(String partName){
		if(partName == null){
			partName = "";
		}

	    int last_match = 0;
	    LinkedList<String> splitted = new LinkedList<String>();
		Matcher m = partNamePattern.matcher(partName);
        while(m.find()){
        	if(!partName.substring(last_match,m.start()).trim().isEmpty()){
        		splitted.add(partName.substring(last_match,m.start()));
        	}
        	if(!m.group().trim().isEmpty()){
        		splitted.add(m.group());
        	}
            last_match = m.end();
        }
        if(!partName.substring(last_match).trim().isEmpty()){
            splitted.add(partName.substring(last_match));        	
        }
        return splitted.toArray(new String[splitted.size()]);
	}
	
	
	/**
	 * Gets and returns the exact Xilinx family type of the part name 
	 * (ex: qvirtex4 instead of virtex4). DO NOT use exact family 
	 * methods if it is to be used for accessing device or wire enumeration 
	 * files as RapidSmith does not generate files for devices that have 
	 * XDLRC compatible files.  
	 * @return The exact Xilinx family type from the given part name.
	 */
	public static FamilyType getExactFamilyTypeFromPart(String partName){
		partName = removeSpeedGrade(partName);
		partName = partName.toLowerCase();
		if(!partName.startsWith("x")){
			return null;
		}
		// Chop up partName into regular pieces for matching
		String[] tokens = splitPartName(partName);

		// Match part name with family
		if(tokens[0].equals("xcv")){
			if(tokens.length >= 3 && tokens[2].startsWith("e")){
				return FamilyType.VIRTEXE;
			}else{
				return FamilyType.VIRTEX;
			}
		}else if(tokens[0].equals("xc")){
			if(tokens[1].equals("2")){
				if(tokens[2].equals("s")){
					if(tokens.length >= 5 && tokens[4].startsWith("e")){
						return FamilyType.SPARTAN2E;
					}else{
						return FamilyType.SPARTAN2; 
					}
				}else if(tokens[2].startsWith("vp")){
					return FamilyType.VIRTEX2P;
				}else if(tokens[2].startsWith("v")){
					return FamilyType.VIRTEX2;
				}
			}else if(tokens[1].equals("3")){
				if(tokens[2].equals("sd")){
					return FamilyType.SPARTAN3ADSP;
				}else if(tokens[2].startsWith("s")){
					if(tokens.length >= 5 && tokens[4].startsWith("e")){
						return FamilyType.SPARTAN3E;
					}else if(tokens.length >= 5 && tokens[4].startsWith("a")){
						return FamilyType.SPARTAN3A;
					}else {
						return FamilyType.SPARTAN3;
					}
				}
			}else if(tokens[1].equals("4")){
				if(tokens[2].startsWith("v")){
					return FamilyType.VIRTEX4;
				}
			}else if(tokens[1].equals("5")){
				if(tokens[2].startsWith("v")){
					return FamilyType.VIRTEX5;
				}
			}else if(tokens[1].equals("6")){
				if(tokens[2].startsWith("v")){
					if(tokens.length >= 5 && (tokens[4].startsWith("l") || tokens[4].startsWith("tl"))){
						return FamilyType.VIRTEX6L;
					}else{
						return FamilyType.VIRTEX6;	
					}
				}else if(tokens[2].startsWith("s")){
					if(tokens.length >= 5 && tokens[4].startsWith("l")){
						return FamilyType.SPARTAN6L;
					}else{
						return FamilyType.SPARTAN6;	
					}
				}
			}else if(tokens[1].equals("7")){
				if(tokens[2].startsWith("v")){
					return FamilyType.VIRTEX7;
				}else if(tokens[2].startsWith("a")){
					return FamilyType.ARTIX7;
				}else if(tokens[2].startsWith("k")){
					return FamilyType.KINTEX7;
				}
			}
		}else if(tokens[0].equals("xa")){
			if(tokens[1].equals("2") && tokens.length >= 5 && tokens[4].startsWith("e")){
				return FamilyType.ASPARTAN2E;
			}else if(tokens[1].equals("3")){
				if(tokens[2].equals("sd")){
					return FamilyType.ASPARTAN3ADSP;
				}else if(tokens[2].startsWith("s")){
					if(tokens.length >= 5 && tokens[4].startsWith("e")){
						return FamilyType.ASPARTAN3E;
					}else if(tokens.length >= 5 && tokens[4].startsWith("a")){
						return FamilyType.ASPARTAN3A;
					}else {
						return FamilyType.ASPARTAN3;
					}
				}
			}else if(tokens[1].equals("6")){
				return FamilyType.ASPARTAN6;
			}
		}else if(tokens[0].equals("xq")){
			if(tokens[1].equals("2")){
				if(tokens[2].equals("v")){
					return FamilyType.QVIRTEX2;
				}else if(tokens[2].equals("vp")){
					return FamilyType.QVIRTEX2P;
				}
				
			}
			else if(tokens[1].equals("4")){
				if(tokens[2].startsWith("v")){
					return FamilyType.QVIRTEX4;
				}
			}
			else if(tokens[1].equals("5")){
				if(tokens[2].startsWith("v")){
					return FamilyType.QVIRTEX5;
				}
			}else if(tokens[1].equals("6")){
				if(tokens[2].startsWith("v")){
					return FamilyType.QVIRTEX6;
				}else if(tokens[2].startsWith("s")){
					if(tokens.length >= 5 && tokens[4].startsWith("l")){
						return FamilyType.QSPARTAN6L;
					}else{
						return FamilyType.QSPARTAN6;
					}
				}
			}else if(tokens[1].equals("7")){
				if(tokens[2].startsWith("v")){
					return FamilyType.QVIRTEX7;
				}else if(tokens[2].startsWith("a")){
					return FamilyType.QARTIX7;
				}else if(tokens[2].startsWith("k")){
					return FamilyType.QKINTEX7;
				}
			}
		}
		else if(tokens[0].equals("xqv")){
			if(tokens.length >= 3 && tokens[2].startsWith("e")){
				return FamilyType.QVIRTEXE;
			}else{
				return FamilyType.QVIRTEX;
			}
		}else if(tokens[0].equals("xqvr")){
			return FamilyType.QRVIRTEX;
		}else if(tokens[0].equals("xqr")){
			if(tokens[1].equals("2")){
				return FamilyType.QRVIRTEX2;
			}else if(tokens[1].equals("4")){
				return FamilyType.QRVIRTEX4;
			}else if(tokens[1].equals("5")){
				return FamilyType.QRVIRTEX5;
			}else if(tokens[1].equals("6")){
				return FamilyType.QRVIRTEX6;
			}else if(tokens[1].equals("7")){
				return FamilyType.QRVIRTEX7;
			}
		}
		return null;
	}
	
	
	/**
	 * This method will take a familyType and return the base familyType 
	 * architecture.  For example, the XDLRC RapidSmith uses for Automotive 
	 * Spartan 6, Low Power Spartan 6 and Military Grade Spartan 6 all have
	 * the same base architecture: Spartan 6.  This method determines the
	 * base architecture based on the familyType.
	 * @param type The given family type.
	 * @return The base family type architecture.
	 */
	public static FamilyType getBaseTypeFromFamilyType(FamilyType type){
		switch(type){
			case ARTIX7: return FamilyType.ARTIX7;
			case ASPARTAN2E: return FamilyType.SPARTAN2E;
			case ASPARTAN3: return FamilyType.SPARTAN3;
			case ASPARTAN3A: return FamilyType.SPARTAN3A;
			case ASPARTAN3ADSP: return FamilyType.SPARTAN3ADSP;
			case ASPARTAN3E: return FamilyType.SPARTAN3E;
			case ASPARTAN6: return FamilyType.SPARTAN6;
			case KINTEX7: return FamilyType.KINTEX7;
			case QARTIX7: return FamilyType.ARTIX7;
			case QKINTEX7: return FamilyType.KINTEX7;
			case QRVIRTEX: return FamilyType.VIRTEX;
			case QRVIRTEX2: return FamilyType.VIRTEX2;
			case QRVIRTEX4: return FamilyType.VIRTEX4;
			case QRVIRTEX5: return FamilyType.VIRTEX5;
			case QRVIRTEX6: return FamilyType.VIRTEX6;
			case QRVIRTEX7: return FamilyType.VIRTEX7;
			case QSPARTAN6: return FamilyType.SPARTAN6;
			case QSPARTAN6L: return FamilyType.SPARTAN6;
			case QVIRTEX: return FamilyType.VIRTEX;
			case QVIRTEX2: return FamilyType.VIRTEX2;
			case QVIRTEX2P: return FamilyType.VIRTEX2P;
			case QVIRTEX4: return FamilyType.VIRTEX4;
			case QVIRTEX5: return FamilyType.VIRTEX5;
			case QVIRTEX6: return FamilyType.VIRTEX6;
			case QVIRTEX7: return FamilyType.VIRTEX7;
			case QVIRTEXE: return FamilyType.VIRTEXE;
			case SPARTAN2: return FamilyType.SPARTAN2;
			case SPARTAN2E: return FamilyType.SPARTAN2E;
			case SPARTAN3: return FamilyType.SPARTAN3;
			case SPARTAN3A: return FamilyType.SPARTAN3A;
			case SPARTAN3ADSP: return FamilyType.SPARTAN3ADSP;
			case SPARTAN3E: return FamilyType.SPARTAN3E;
			case SPARTAN6: return FamilyType.SPARTAN6;
			case SPARTAN6L: return FamilyType.SPARTAN6;
			case VIRTEX: return FamilyType.VIRTEX;
			case VIRTEX2: return FamilyType.VIRTEX2;
			case VIRTEX2P: return FamilyType.VIRTEX2P;
			case VIRTEX4: return FamilyType.VIRTEX4;
			case VIRTEX5: return FamilyType.VIRTEX5;
			case VIRTEX6: return FamilyType.VIRTEX6;
			case VIRTEX6L: return FamilyType.VIRTEX6;
			case VIRTEX7: return FamilyType.VIRTEX7;
			case VIRTEXE: return FamilyType.VIRTEXE;
			default: return null;
		}
	}
		
	/**
	 * Gets and returns the all lower case exact Xilinx family name of the 
	 * part name (ex: qvirtex4 instead of virtex4). DO NOT use exact family 
	 * methods if it is to be used for accessing device or wire enumeration 
	 * files as RapidSmith does not generate files for devices that have 
	 * XDLRC compatible files.  
	 * @return The exact Xilinx family name of the part name.
	 */
	public static String getExactFamilyNameFromPart(String partName){
		return getExactFamilyTypeFromPart(partName).toString().toLowerCase();
	}
	
	/**
	 * Gets and returns the all lower case base family name of the part name.
	 * This ensures compatibility with all RapidSmith files. For 
	 * differentiating family names (qvirtex4 rather than virtex4) use 
	 * getExactFamilyName().
	 * @return The base family name of the given part name.
	 */
	public static String getFamilyNameFromPart(String partName){
		return getFamilyTypeFromPart(partName).toString().toLowerCase();
	}
	
	public static void main(String[] args){
		// Run some tests to make sure we are doing things right
		for(FamilyType type : FamilyType.values()){
			System.out.println("Current Type: " + type);
			ArrayList<String> names = RunXilinxTools.getPartNames(type.toString().toLowerCase(), false);
			if(names == null) continue;
			for(String name : names){
				System.out.println("    " + name + " " + getExactFamilyTypeFromPart(name));
			}
		}
		
		String[] testPartNames = {"xs6slx4ltqg144", "xs6slx4tqg144", 
				"xc7v285tffg484", "xc7vh290tffg1155", "xc7k30tsbg324", "xc7a20cpg236", "xc7a175tcsg324",
				"xq7v285tffg484", "xq7vh290tffg1155", "xq7k30tsbg324", "xq7a20cpg236", "xq7a175tcsg324",
				"xqr7v285tffg484", "xqr7vh290tffg1155"};
		for(String name : testPartNames){
			System.out.println("    " + name + " " + getExactFamilyTypeFromPart(name));
		}
	}
}

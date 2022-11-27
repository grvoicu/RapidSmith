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
 * limitations under the License..
 * 
 */
package edu.byu.ece.rapidSmith.design;

import java.io.Serializable;

/**
 * The Attribute object in XDL is used in several places, Design, Instance, Net 
 * and Module.  Each are generally a list of attributes.  An attribute in XDL consists
 * of a triplet of Strings separated by colons: "Physical Name":"Logical Name":"Value".
 * This class captures these elements of an attribute. 
 * 
 * In XDL, the physical name of an attribute can have multiple logical names and/or values
 * for a single "physical name". To represent multiple logical names and values 
 * in a standard Map, the multiple logical and value strings are stored in a single
 * string and are separated by the final multiValueSeparator character. A variety
 * of methods are available for determining whether the attribute is multi-valued
 * and accessing the multi-value fields.
 * 
 * @author Chris Lavin
 * Created on: Jun 22, 2010
 */
public class Attribute implements Serializable{

	private static final long serialVersionUID = -7266101885597264094L;

	/** Physical name of the attribute (_::) */
	private String physicalName;
	/** Logical or user name of the attribute (:_:) */
	private String logicalName;
	/** Value of the attribute (::_) */
	private String value;
	/** 
	 * This is used to separate multiple value and multiple logical names 
	 * found in attributes where multiple entries have the same physical name.
	 */
	public static final String multiValueSeparator = "`";
	
	/**
	 * @param physicalName Physical name of the attribute (_::)
	 * @param logicalName Logical or user name of the attribute (:_:)
	 * @param value Value of the attribute (::_)
	 */
	public Attribute(String physicalName, String logicalName, String value){
		this.physicalName = physicalName;
		this.logicalName = logicalName;
		this.value = value;
	}

	/**
	 * Creates a new attribute by copying the class members from attr.
	 * @param attr The attribute to model the new attribute after.
	 */
	public Attribute(Attribute attr){
		this.physicalName = attr.physicalName;
		this.logicalName = attr.logicalName;
		this.value = attr.value;
	}
	
	/**
	 * Gets the physical name of the attribute (_::)
	 * @return Physical name of the attribute (_::)
	 */
	public String getPhysicalName(){
		return physicalName;
	}

	/**
	 * Sets the physical name of the attribute (_::)
	 * @param physicalName physical name of the attribute (_::)
	 */
	public void setPhysicalName(String physicalName){
		this.physicalName = physicalName;
	}

	/**
	 * Gets the logical or user name of the attribute (:_:)
	 * @return Logical or user name of the attribute (:_:)
	 */
	public String getLogicalName(){
		return logicalName;
	}

	/**
	 * Sets the logical or user name of the attribute (:_:)
	 * @param logicalName Logical or user name of the attribute (:_:)
	 */
	public void setLogicalName(String logicalName){
		this.logicalName = logicalName;
	}

	/**
	 * Gets the value of the attribute (::_)
	 * @return Value of the attribute (::_)
	 */
	public String getValue(){
		return value;
	}

	/**
	 * Sets the value of the attribute (::_)
	 * @param value Value of the attribute (::_)
	 */
	public void setValue(String value){
		this.value = value;
	}
	
	/**
	 * Some physical name attributes have multiple values. This method indicates whether
	 * this attribute is a multiple value attribute or not. If this attribute is
	 * a multi-value attribute, additional methods are available to access the
	 * multiple logical and value strings.
	 */
	public boolean isMultiValueAttribute() {
		return (getLogicalName().contains(Attribute.multiValueSeparator));
	}
	
	/**
	 * If this attribute is a multiple value attribute, this method returns
	 * a set of Strings that correspond to the "values" of the attribute.
	 * If this attribute is a single value attribute, it returns a set of one
	 * String which is the same as this.value.
	 */
	public String[] getMultiValueValues() {		
		return value.split(Attribute.multiValueSeparator, -1);
	}
	
	/**
	 * If this attribute is a multiple value attribute, this method returns
	 * a set of Strings that correspond to the "logical names" of the attribute.
	 * If this attribute is a single value attribute, it returns a set of one
	 * String which is the same as this.logicalName.
	 */
	public String[] getMultiValueLogicalNames() {		
		return logicalName.split(Attribute.multiValueSeparator, -1);
	}

	/**
	 * Creates a string representation of the attribute that follows 
	 * how it would appear in and XDL file.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(logicalName.contains(multiValueSeparator)){
			String[] logicalNames = getMultiValueLogicalNames();
			String[] values = getMultiValueValues();
			for (int i = 0; i < values.length; i++) {
				sb.append(physicalName);
				sb.append(":");
				sb.append(logicalNames[i]);
				sb.append(":");
				sb.append(values[i]);
				if(i < values.length-1) sb.append(" ");
			}
			return sb.toString();
		}
		sb.append(physicalName);
		sb.append(":");
		sb.append(logicalName);
		sb.append(":");
		sb.append(value);
		return sb.toString();
	}
}

/*
 * Copyright (c) 2010-2011 Brigham Young University
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
package edu.byu.ece.rapidSmith.bitstreamTools.configurationSpecification;

/**
 * The BlockSubType class represents a block subtype for a specific FPGA
 * family (i.e. V4, V5, V6). Each family should have it's own Set of
 * BlockSubTypes that are shared across all of the parts in the family.
 * 
 * A BlockSubType contains a name and an int representing the number of
 * configuration frames required by the BlockSubType.
 */
public class BlockSubType {
    
    public BlockSubType(String name, int framesPerConfigurationBlock) {
        _name = name;
        _framesPerConfigurationBlock = framesPerConfigurationBlock;
    }
    
    public int getFramesPerConfigurationBlock() {
        return _framesPerConfigurationBlock;
    }
    
    public String getName() {
        return _name;
    }
    
    public String toString() {
        return _name;
    }
    
    protected String _name;
    
    protected int _framesPerConfigurationBlock;
}

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
package edu.byu.ece.rapidSmith.gui;

import io.qt.core.QObject;

public class FileNameFilters {
	/** Xilinx Design Language File Filter */
	public static String xdlFilter = QObject.tr("Xilinx Design Language Files (*.xdl)");
	/** Native Circuit Description File Filter */
	public static String ncdFilter = QObject.tr("Design Files (*.ncd)");
	/** Hard Macro File Filter */
	public static String nmcFilter = QObject.tr("Hard Macro Files (*.nmc)");
	/** Portable Document Format File Filter */
	public static String pdfFilter = QObject.tr("Portable Document Format Files (*.pdf)");
	/** Xilinx Trace Report File Filter */
	public static String twrFilter = QObject.tr("Xilinx Trace Report Files (*.twr)");
	/** EDK Microprocessor Hardware Specification File Filter */
	public static String mhsFilter = QObject.tr("Microprocessor Hardware Specification Files (*.mhs)");
}

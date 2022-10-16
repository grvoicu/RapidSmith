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
package edu.byu.ece.rapidSmith.device.browser;

import java.util.ArrayList;

import io.qt.core.QModelIndex;
import io.qt.core.Qt.DockWidgetArea;
import io.qt.core.Qt.ItemDataRole;
import io.qt.core.Qt.SortOrder;
import io.qt.widgets.QApplication;
import io.qt.widgets.QDockWidget;
import io.qt.widgets.QLabel;
import io.qt.widgets.QMainWindow;
import io.qt.widgets.QStatusBar;
import io.qt.widgets.QTreeWidget;
import io.qt.widgets.QTreeWidgetItem;
import io.qt.widgets.QWidget;
import io.qt.widgets.QDockWidget.DockWidgetFeature;

import edu.byu.ece.rapidSmith.device.Device;
import edu.byu.ece.rapidSmith.device.PrimitiveSite;
import edu.byu.ece.rapidSmith.device.Tile;
import edu.byu.ece.rapidSmith.device.WireConnection;
import edu.byu.ece.rapidSmith.device.WireEnumerator;
import edu.byu.ece.rapidSmith.gui.TileView;
import edu.byu.ece.rapidSmith.gui.WidgetMaker;
import edu.byu.ece.rapidSmith.util.FileTools;
import edu.byu.ece.rapidSmith.util.MessageGenerator;

/**
 * This class creates an interactive Xilinx FPGA device browser for all of the
 * devices currently installed on RapidSmith.  It provides the user with a 2D view
 * of all tile array in the device.  Allows each tile to be selected (double click)
 * and populate the primitive site and wire lists.  Wire connections can also be drawn
 * by selecting a specific wire in the tile (from the list) and the program will draw
 * all connections that can be made from that wire.  The wire positions on the tile
 * are determined by a hash and are not related to FPGA Editor positions.   
 * @author Chris Lavin and Marc Padilla
 * Created on: Nov 26, 2010
 */
public class DeviceBrowser extends QMainWindow{
	/** The Qt View for the browser */
	protected TileView view;
	/** The Qt Scene for the browser */
	private DeviceBrowserScene scene;
	/** The label for the status bar at the bottom */
	private QLabel statusLabel;
	/** The current device loaded */
	Device device;
	/** The current wire enumerator loaded */
	WireEnumerator we;
	/** The current part name of the device loaded */
	private String currPart;
	/** This is the tree of parts to select */
	private QTreeWidget treeWidget;
	/** This is the list of primitive sites in the current tile selected */
	private QTreeWidget primitiveList;
	/** This is the list of wires in the current tile selected */
	private QTreeWidget wireList;
	/** This is the current tile that has been selected */
	private Tile currTile = null;
	
	protected boolean hideTiles = false;
	
	protected boolean drawPrimitives = true; 
	/**
	 * Main method setting up the Qt environment for the program to run.
	 * @param args
	 */
	public static void main(String[] args){
		//QApplication.setGraphicsSystem("raster");
		QApplication.initialize(args);
		DeviceBrowser testPTB = new DeviceBrowser(null);
		testPTB.show();
		QApplication.exec();
	}

	/**
	 * Constructor which initializes the GUI and loads the first part found.
	 * @param parent The Parent widget, used to add this window into other GUIs.
	 */
	public DeviceBrowser(QWidget parent){
		super(parent);
		
		// set the title of the window
		setWindowTitle("Device Browser");
		
		initializeSideBar();
		
		// Gets the available parts in RapidSmith and populates the selection tree
		ArrayList<String> parts = FileTools.getAvailableParts();
		if(parts.size() < 1){
			MessageGenerator.briefErrorAndExit("Error: No available parts. " +
					"Please generate part database files.");
		}
		if(parts.contains("xc4vlx100ff1148")){
			currPart = "xcv50tq144"; // "xc4vlx100ff1148";
		}
		else{
			currPart = parts.get(0);
		}
		
		device = FileTools.loadDevice(currPart);
		we = FileTools.loadWireEnumerator(currPart);
		
		// Setup the scene and view for the GUI
		scene = new DeviceBrowserScene(device, we, hideTiles, drawPrimitives, this);
		view = new TileView(scene);
		setCentralWidget(view);

		// Setup some signals for when the user interacts with the view
		scene.updateStatus.connect(this, "updateStatus(String, Tile)");
		scene.updateTile.connect(this, "updateTile(Tile)");
		
		// Initialize the status bar at the bottom
		statusLabel = new QLabel("Status Bar");
		statusLabel.setText("Status Bar");
		QStatusBar statusBar = new QStatusBar();
		statusBar.addWidget(statusLabel);
		setStatusBar(statusBar);
		
		// Set the opening default window size to 1024x768 pixels
		resize(1024, 768);
	}

	/**
	 * Populates the treeWidget with the various parts and families of devices
	 * currently available in this installation of RapidSmith.  It also creates
	 * the windows for the primitive site list and wire list.
	 */
	private void initializeSideBar(){
		treeWidget = WidgetMaker.createAvailablePartTreeWidget("Select a part...");
		treeWidget.doubleClicked.connect(this,"showPart(QModelIndex)");
		
		QDockWidget dockWidget = new QDockWidget(tr("Part Browser"), this);
		dockWidget.setWidget(treeWidget);
		dockWidget.setFeatures(DockWidgetFeature.DockWidgetMovable);
		addDockWidget(DockWidgetArea.LeftDockWidgetArea, dockWidget);
		
		// Create the primitive site list window
		primitiveList = new QTreeWidget();
		primitiveList.setColumnCount(2);
		ArrayList<String> headerList = new ArrayList<String>();
		headerList.add("Site");
		headerList.add("Type");
		primitiveList.setHeaderLabels(headerList);
		primitiveList.setSortingEnabled(true);
		
		QDockWidget dockWidget2 = new QDockWidget(tr("Primitive List"), this);
		dockWidget2.setWidget(primitiveList);
		dockWidget2.setFeatures(DockWidgetFeature.DockWidgetMovable);
		addDockWidget(DockWidgetArea.LeftDockWidgetArea, dockWidget2);
		
		// Create the wire list window
		wireList = new QTreeWidget();
		wireList.setColumnCount(2);
		ArrayList<String> headerList2 = new ArrayList<String>();
		headerList2.add("Wire");
		headerList2.add("Sink Connections");
		wireList.setHeaderLabels(headerList2);
		wireList.setSortingEnabled(true);
		QDockWidget dockWidget3 = new QDockWidget(tr("Wire List"), this);
		dockWidget3.setWidget(wireList);
		dockWidget3.setFeatures(DockWidgetFeature.DockWidgetMovable);
		addDockWidget(DockWidgetArea.LeftDockWidgetArea, dockWidget3);

		// Draw wire connections when the wire name is double clicked
		wireList.doubleClicked.connect(this, "wireDoubleClicked(QModelIndex)");
	}
	
	/**
	 * This method will draw all of the wire connections based on the wire given.
	 * @param index The index of the wire in the wire list.
	 */
	public void wireDoubleClicked(QModelIndex index){
		scene.clearCurrentLines();
		if(currTile == null) return;
		int currWire = we.getWireEnum(index.data().toString());
		if(currWire < 0) return;
		if(currTile.getWireConnections(we.getWireEnum(index.data().toString())) == null) return;
		for(WireConnection wire : currTile.getWireConnections(we.getWireEnum(index.data().toString()))){
			scene.drawWire(currTile, currWire, wire.getTile(currTile), wire.getWire());
		}
	}
	
	/**
	 * This method gets called each time a user double clicks on a tile.
	 */
	protected void updateTile(Tile tile){
		currTile = tile;
		updatePrimitiveList();
		updateWireList();
	}
	
	/**
	 * This will update the primitive list window based on the current
	 * selected tile.
	 */
	protected void updatePrimitiveList(){
		primitiveList.clear();
		if(currTile == null || currTile.getPrimitiveSites() == null) return;
		for(PrimitiveSite ps : currTile.getPrimitiveSites()){
			QTreeWidgetItem treeItem = new QTreeWidgetItem();
			treeItem.setText(0, ps.getName());
			treeItem.setText(1, ps.getType().toString());
			primitiveList.insertTopLevelItem(0, treeItem);
		}
	}

	/**
	 * This will update the wire list window based on the current
	 * selected tile.
	 */
	protected void updateWireList(){
		wireList.clear();
		if(currTile == null || currTile.getWireHashMap() == null) return;
		for(Integer wire : currTile.getWireHashMap().keySet()) {
			QTreeWidgetItem treeItem = new QTreeWidgetItem();
			treeItem.setText(0, we.getWireName(wire));
			WireConnection[] connections = currTile.getWireConnections(wire);
			treeItem.setText(1, String.format("%3d", connections == null ? 0 : connections.length));
			wireList.insertTopLevelItem(0, treeItem);
		}
		wireList.sortByColumn(0, SortOrder.AscendingOrder);
	}

	/**
	 * This method loads a new device based on the part name selected in the 
	 * treeWidget.
	 * @param qmIndex The index of the part to load.
	 */
	protected void showPart(QModelIndex qmIndex){
		Object data = qmIndex.data(ItemDataRole.AccessibleDescriptionRole);
		if( data != null){
			if(currPart.equals(data))
				return;
			currPart = (String) data;			
			device = FileTools.loadDevice(currPart);
			we = FileTools.loadWireEnumerator(currPart);
			scene.setDevice(device);
			scene.setWireEnumerator(we);
			scene.initializeScene(hideTiles, drawPrimitives);
			statusLabel.setText("Loaded: "+currPart.toUpperCase());
		}
	}
	
	/**
	 * This method updates the status bar each time the mouse moves from a 
	 * different tile.
	 */
	protected void updateStatus(String text, Tile tile){
		statusLabel.setText(text);
		//currTile = tile;
		//System.out.println("currTile=" + tile);
	}
}
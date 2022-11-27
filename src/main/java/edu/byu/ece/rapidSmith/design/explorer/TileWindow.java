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
package edu.byu.ece.rapidSmith.design.explorer;

import java.util.ArrayList;
import java.util.HashMap;

import io.qt.core.QSize;
import io.qt.core.Qt.AspectRatioMode;
import io.qt.core.Qt.Orientation;
import io.qt.widgets.QGraphicsScene;
import io.qt.widgets.QGraphicsView;
import io.qt.widgets.QGridLayout;
import io.qt.widgets.QLabel;
import io.qt.widgets.QLineEdit;
import io.qt.widgets.QSplitter;
import io.qt.widgets.QWidget;

import edu.byu.ece.rapidSmith.design.Design;
import edu.byu.ece.rapidSmith.design.ModuleInstance;
import edu.byu.ece.rapidSmith.design.Net;
import edu.byu.ece.rapidSmith.design.PIP;
import edu.byu.ece.rapidSmith.device.Tile;
import edu.byu.ece.rapidSmith.device.WireConnection;
import edu.byu.ece.rapidSmith.gui.GuiModuleInstance;
import edu.byu.ece.rapidSmith.gui.TileView;
import edu.byu.ece.rapidSmith.router.Node;
import edu.byu.ece.rapidSmith.timing.PathDelay;
import edu.byu.ece.rapidSmith.timing.PathElement;
import edu.byu.ece.rapidSmith.timing.RoutingPathElement;

/**
 * This class is used for the tile window tab of the design explorer.
 * It could also be used for other applications as well.
 * @author Chris Lavin
 */
public class TileWindow extends QWidget{
	/** Associated view with this window */
	protected TileView view;
	/** Associated scene with this window */
	protected DesignTileScene scene;
	/** The current design */
	protected Design design;
	/** The layout for the window */
	private QGridLayout layout;
	/** The sidebar view (for use with timing analysis) */
	protected QGraphicsView sidebarView;
	
	protected TimingSlider slider;
	
	protected QGraphicsScene sidebarScene;
	/**
	 * Constructor
	 * @param parent 
	 */
	public TileWindow(QWidget parent){
		super(parent);
		scene = new DesignTileScene();
		view = new TileView(scene);
		layout = new QGridLayout();

		// Side bar setup
		QGridLayout sidebarLayout = new QGridLayout();
		sidebarScene = new QGraphicsScene(this);		
		QLineEdit textBox = new QLineEdit();
		sidebarView = new QGraphicsView(sidebarScene);
		slider = new TimingSlider(scene, textBox);
		slider.setFixedHeight(200);
		sidebarLayout.addWidget(new QLabel("Choose\nConstraint:"), 0, 0);
		sidebarLayout.addWidget(slider, 1, 0);
		sidebarLayout.addWidget(textBox, 2, 0);
		sidebarLayout.addWidget(new QLabel("ns"), 2, 1);
		sidebarView.setLayout(sidebarLayout);
		slider.sliderMoved.connect(slider, "updatePaths(Integer)");
		textBox.textChanged.connect(slider, "updateText(String)");
		
		QSplitter splitter = new QSplitter(Orientation.Horizontal);
		splitter.setEnabled(true);
		sidebarView.setMinimumWidth(90);
		splitter.addWidget(sidebarView);
		splitter.addWidget(view);
		layout.addWidget(splitter);		
		this.setLayout(layout);
	}
	
	/**
	 * Updates the design.
	 * @param design New design to set.
	 */
	public void setDesign(Design design){
		this.design = design;
		scene.setDesign(this.design);
		scene.initializeScene(true, true);
		scene.setDevice(design.getDevice());
		scene.setWireEnumerator(design.getWireEnumerator());
		
		// Create hard macro blocks
		for(ModuleInstance mi : design.getModuleInstances()){
			scene.addItem(new GuiModuleInstance(mi, scene, false));
		}
	}
	
	/**
	 * Moves the cursor to a new tile in the tile array.
	 * @param tile The new tile to move the cursor to.
	 */
	public void moveToTile(String tile){
		Tile t = design.getDevice().getTile(tile);
		int tileSize = scene.getTileSize();
		QSize size = this.frameSize();
		view.fitInView(scene.getDrawnTileX(t)*tileSize - size.width()/2,
				scene.getDrawnTileY(t)*tileSize - size.height()/2, 
				size.width(), size.height(), AspectRatioMode.KeepAspectRatio);
		view.zoomIn(); view.zoomIn();
		view.zoomIn(); view.zoomIn();		
		scene.updateCurrXY(scene.getDrawnTileX(t), scene.getDrawnTileY(t));
		scene.updateCursor();
	}
	
	public void drawCriticalPaths(ArrayList<PathDelay> pathDelays){
		DesignTileScene scn = (DesignTileScene) scene;
		for(PathDelay pd : pathDelays){
			ArrayList<Connection> conns = new ArrayList<Connection>();
			for(PathElement pe : pd.getMaxDataPath()){
				if(pe.getType().equals("net")){
					if(pe.getClass().equals(RoutingPathElement.class)){
						RoutingPathElement rpe = (RoutingPathElement) pe;
						Net net = rpe.getNet();
						conns.addAll(getAllConnections(net));
						/*for(Connection conn : conns){
							scn.drawWire(conn);
							//System.out.println(conn.toString(scene.getWireEnumerator()));
						}*/
						//return;
					}
				}
			}
			scn.drawPath(conns, pd);
		}
		scn.sortPaths();
	}
	
	public ArrayList<Connection> getAllConnections(Net net){
		ArrayList<Connection> conns = new ArrayList<Connection>();
		HashMap<Node, Node> nodeMap = new HashMap<Node, Node>();
		for(PIP p : net.getPIPs()){
			
			if(scene.tileXMap.get(p.getTile()) != null && scene.tileYMap.get(p.getTile()) != null){
				conns.add(new Connection(p));
			}
			
			Node start = new Node(p.getTile(), p.getStartWire(), null, 0);
			Node end = new Node(p.getTile(), p.getEndWire(), null, 0);
			nodeMap.put(start, start);
			nodeMap.put(end, end);
		}
		Node tmp = new Node();
		Node tmp2 = new Node();
		Node tmp3 = new Node();
		for(PIP p : net.getPIPs()){
			tmp.setTileAndWire(p.getTile(), p.getEndWire());
			//System.out.println("  " + tmp.toString(scene.getWireEnumerator()));
			if(tmp.getConnections() == null) continue;
			for(WireConnection w : tmp.getConnections()){
				tmp2.setTileAndWire(w.getTile(tmp.getTile()), w.getWire());
				//System.out.println("    " + tmp2.toString(scene.getWireEnumerator()));
				if(!tmp2.getTile().equals(tmp.getTile()) && tmp2.getConnections() != null){
					for(WireConnection w2 : tmp2.getConnections()){
						tmp3.setTileAndWire(w2.getTile(tmp2.getTile()), w2.getWire());
						//System.out.println("      " + tmp3.toString(scene.getWireEnumerator()));
						if(nodeMap.get(tmp3) != null){
							if(scene.tileXMap.get(tmp.getTile()) != null && scene.tileYMap.get(tmp2.getTile()) != null){
								Connection conn = new Connection(tmp.getTile(), tmp2.getTile(), tmp.getWire(), tmp2.getWire()); 
								conns.add(conn);
								//System.out.println("* " + conn.toString(scene.getWireEnumerator()));
							}
						}
					}
				}
			}
		}
		
		
		return conns;
	}
}

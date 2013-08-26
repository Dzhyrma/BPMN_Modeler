package com.jku.bpmn.model.json;

import java.io.Serializable;
import java.util.Vector;

public class JSONDiagram implements Serializable {

	private static final long serialVersionUID = 7068995491771731244L;
	private Vector<JSONNode> nodes = new Vector<JSONNode>();
	private Vector<JSONConnection> connections = new Vector<JSONConnection>();
	private String name;
	private int id;

	public JSONDiagram(String name) {
		this.name = name;
	}

	public final void addConnection(JSONConnection connection) {
		this.connections.add(connection);
	}

	public final void addNode(JSONNode node) {
		this.nodes.add(node);
	}

	public final Vector<JSONConnection> getConnections() {
		return this.connections;
	}

	public final int getId() {
		return this.id;
	}

	public final String getName() {
		return this.name;
	}

	public final Vector<JSONNode> getNodes() {
		return this.nodes;
	}

	public final void setConnections(Vector<JSONConnection> connections) {
		this.connections = connections;
	}

	public final void setId(int id) {
		this.id = id;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void setNodes(Vector<JSONNode> nodes) {
		this.nodes = nodes;
	}
}

package org.motechproject.server.omod.xml;

public class LocationItem {
	private String parentId;
	private String id;
	private LocationContent content;

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LocationContent getContent() {
		return content;
	}

	public void setContent(LocationContent content) {
		this.content = content;
	}

}
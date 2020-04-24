package br.com.kotar.domain.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuHelper implements Serializable {

	private static final long serialVersionUID = 1L;

	private String label;
	private String url;
	private String icon;
	private Integer badge;
	private String key;
	private boolean visible;
	private List<MenuHelper> children = new ArrayList<>();

	public MenuHelper(String label, String url, String icon, boolean visible) {
		super();
		this.label = label;
		this.url = url;
		this.icon = icon;
		this.visible = visible;
	}

	public MenuHelper(String label, String url, String icon, String key, boolean visible) {
		super();
		this.label = label;
		this.url = url;
		this.icon = icon;
		this.key = key;
		this.visible = visible;
	}

	public MenuHelper(String label, String icon, boolean visible) {
		super();
		this.label = label;
		this.icon = icon;
		this.visible = visible;
	}

	public MenuHelper(String label, boolean visible) {
		super();
		this.label = label;
		this.visible = visible;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getBadge() {
		return badge;
	}

	public void setBadge(Integer badge) {
		this.badge = badge;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<MenuHelper> getChildren() {
		return children;
	}

	public void setChildren(List<MenuHelper> children) {
		this.children = children;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}

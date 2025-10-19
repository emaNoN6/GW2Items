/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items.models;

import java.text.MessageFormat;
import org.json.simple.JSONObject;

/**
 *
 * @author Michael
 */
public class Attributes {

	private final String attribute;
	private final Integer modifier;

	/**
	 *
	 * @param attribute
	 * @param modifier
	 */
	public Attributes(String attribute, Integer modifier) {
		this.attribute = attribute;
		this.modifier = modifier;
	}
	
	/**
	 *
	 * @param o
	 */
	public Attributes(JSONObject o) {
		this.attribute = o.get("attribute").toString();
		this.modifier = Integer.valueOf(o.get("modifier").toString());
	}

	/**
	 *
	 * @return
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 *
	 * @return
	 */
	public Integer getModifier() {
		return modifier;
	}

	@Override
	public String toString() {
		return MessageFormat.format("Attribute: {0}, Modifier: +{1}", attribute, modifier);
	}
}

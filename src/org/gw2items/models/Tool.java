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
public class Tool {
	String type;
	int charges;

	/**
	 *
	 * @param obj
	 */
	public Tool(Object obj) {
		JSONObject o = (JSONObject) obj;
		this.type = o.get("type").toString();
		this.charges = Integer.parseInt(o.get("charges").toString());
	}

	/**
	 *
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 *
	 * @return
	 */
	public int getCharges() {
		return charges;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("Tool\n\ttype: {0}\n\tcharges: {1}", this.type, this.charges);
	}

}

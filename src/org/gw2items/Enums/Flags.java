/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items.Enums;

/**
 *
 * @author Michael
 */
public enum Flags {

	/**
	 *
	 */
	AccountBound("Account Bound"),

	/**
	 *
	 */
	HideSuffix("Hide Suffix"),

	/**
	 *
	 */
	NoMysticForge("No Mystic Forge"),

	/**
	 *
	 */
	NoSalvage("Not Salvagable"),

	/**
	 *
	 */
	NoSell("Not Sellable"),

	/**
	 *
	 */
	NotUpgradeable("Can't Upgrade"),

	/**
	 *
	 */
	NoUnderwater("Not Underwater"),

	/**
	 *
	 */
	SoulbindOnAcquire("Soulbound On Acquire"),

	/**
	 *
	 */
	SoulBindOnUse("Soulbound On Use"),

	/**
	 *
	 */
	Unique("Unique");
	private String pretty;
	private Flags(String str) {
		this.pretty = str;
	}

	@Override
	public String toString() {
		return pretty;
	}
	
	
}

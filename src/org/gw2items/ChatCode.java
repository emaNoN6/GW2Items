package org.gw2items;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;

/**
 * Convert the item id to a chat preview code.
 *
 * @author Michael
 */
public class ChatCode {

	/**
	 * raw preview code.
	 */
	private transient String preview;
	private static final Map<String, Byte> chatHeader;

	static {
		Map<String, Byte> hMap = new HashMap<>();
		hMap.put("Coin", (byte) 0x01);
		hMap.put("Item", (byte) 0x02);
		hMap.put("Text", (byte) 0x03);
		hMap.put("Map", (byte) 0x04);
		hMap.put("PvP", (byte) 0x05);
		hMap.put("Skill", (byte) 0x07);
		hMap.put("Trait", (byte) 0x08);
		hMap.put("Player", (byte) 0x09);
		hMap.put("Recipe", (byte) 0x0A);
		chatHeader = Collections.unmodifiableMap(hMap);
	};

	/**
	 *
	 */
	public ChatCode() {
		this(0);
	}

	/**
	 *
	 * @param idNumber item id number
	 */
	public ChatCode(final int idNumber) {
		final byte high = (byte) ((idNumber % 256));
		final byte low = (byte) Math.floor(idNumber / 256);
		final byte[] cCode = {chatHeader.get("Item"), 0x01, high, low, 0x0, 0x0};
		this.preview = Base64.encodeBase64String(cCode);
	}

	@Override
	public String toString() {
		return MessageFormat.format("[&{0}]", this.preview);
	}

	/**
	 *
	 * @return Unformatted preview code
	 */
	public String getPreview() {
		return this.preview;
	}
}

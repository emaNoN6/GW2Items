/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items;

import java.text.MessageFormat;
import javax.swing.JOptionPane;
import org.json.simple.JSONObject;
import org.gw2items.Enums.ItemTypes;
import org.gw2items.Factories.Database;
import static org.gw2items.Start.setStatusLabel;
import org.gw2items.models.Armor;
import org.gw2items.models.Back;
import org.gw2items.models.Bag;
import org.gw2items.models.Consumable;
import org.gw2items.models.Container;
import org.gw2items.models.Gathering;
import org.gw2items.models.Gizmo;
import org.gw2items.models.InfixUpgrade;
import org.gw2items.models.ItemDetail;
import org.gw2items.models.Tool;
import org.gw2items.models.Trinket;
import org.gw2items.models.UpgradeComponent;
import org.gw2items.models.Weapon;

/**
 *
 * @author Michael
 */
class Types {

	final private static boolean DETAIL = false;

	/**
	 * Process the different object types.
	 *
	 * @param obj JSONObject with the item
	 */
	public static void processType(final JSONObject obj) {
		final ItemDetail item = new ItemDetail(obj);
		Object detailObject = null;
		InfixUpgrade inFix = null;
		setStatusLabel("Processing item type.");
		try {
			final ItemTypes type = ItemTypes.valueOf(item.getType());
			switch (type) {
				case Armor:
					detailObject = new Armor(obj.get("armor"));
					inFix = ((Armor) detailObject).getInfix_upgrade();
					break;
				case Weapon:
					detailObject = new Weapon(obj.get("weapon"));
					inFix = ((Weapon) detailObject).getInfix_upgrade();
					break;
				case Back:
					detailObject = new Back(obj.get("back"));
					inFix = ((Back) detailObject).getInfix_upgrade();
					break;
				case Bag:
					detailObject = new Bag(obj.get("bag"));
					break;
				case Consumable:
					detailObject = new Consumable(obj.get("consumable"));
					break;
				case Container:
					detailObject = new Container(obj.get("container"));
					break;
				case CraftingMaterial:
				case Trophy:
				case MiniPet:
					detailObject = null;
					break;
				case Gathering:
					detailObject = new Gathering(obj.get("gathering"));
					break;
				case Gizmo:
					detailObject = new Gizmo(obj.get("gizmo"));
					break;
				case Tool:
					detailObject = new Tool(obj.get("tool"));
					break;
				case Trinket:
					detailObject = new Trinket(obj.get("trinket"));
					inFix = ((Trinket) detailObject).getInfix_upgrade();
					break;
				case UpgradeComponent:
					detailObject = new UpgradeComponent(obj.get("upgrade_component"));
					inFix = ((UpgradeComponent) detailObject).getInfix_upgrade();
					break;
				default:
					throw new UnsupportedOperationException(MessageFormat.format("processType: Type \"{0}\" not written yet.", item.getType()));
			}
			if (detailObject != null) {
				item.setItem(detailObject);
				setStatusLabel("Writing object");
				Database.writeObject(item.getItemId(), detailObject);
			}
				Database.writeInfix(item.getItemId(), inFix);
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(null, MessageFormat.format("processType: Unknown item type \"{0}\" from item #{1} - {2}", item.getType(), item.getItemId(), item.getName()), "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		} catch (UnsupportedOperationException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
}

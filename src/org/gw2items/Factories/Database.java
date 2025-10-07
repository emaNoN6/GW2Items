/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items.Factories;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.JOptionPane;
import org.gw2items.Enums.ItemTypes;
import org.gw2items.Start;
import org.gw2items.ChatCode;
import org.gw2items.models.*;
import org.gw2items.models.InfixUpgrade.Buff;
import org.gw2items.models.Recipe.Ingredient;

/**
 *
 * @author Michael
 */
public class Database {

	/**
	 *
	 */
	private static Connection con;
	private static boolean error = false;
	private static boolean setup = false;
	private final static String today = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
	private static int added = 0;
	private static int skipped = 0;
	private static int total = 0;
	private static int errcount = 0;
	private static int removedCount = 0;
	private static List<String> errors;
	private static ArrayList<Long> ids = new ArrayList<>();
	private static final Map<ItemTypes, String> itemSqlStrings;

	static {
		Map<ItemTypes, String> aMap = new HashMap<>();
		aMap.put(ItemTypes.Armor, "INSERT INTO armor (item_id, type, weight_class, defense, suffix_item_id) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE type=VALUES(type), weight_class=VALUES(weight_class), defense=VALUES(defense), suffix_item_id=VALUES(suffix_item_id)");
		aMap.put(ItemTypes.Back, "INSERT INTO back (item_id, suffix_item_id) VALUES (?,?) ON DUPLICATE KEY UPDATE suffix_item_id=VALUES(suffix_item_id)");
		aMap.put(ItemTypes.Bag, "INSERT INTO bag (item_id, no_sell_or_sort, size) VALUES (?,?,?) ON DUPLICATE KEY UPDATE no_sell_or_sort=VALUES(no_sell_or_sort), size=VALUES(size)");
		aMap.put(ItemTypes.Consumable, "INSERT INTO consumable (item_id, type, duration_ms, description) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE type=VALUES(type), duration_ms=VALUES(duration_ms), description=VALUES(description)");
		aMap.put(ItemTypes.Container, "INSERT INTO container (item_id, type) VALUES (?,?) ON DUPLICATE KEY UPDATE type=VALUES(type)");
		aMap.put(ItemTypes.Gathering, "INSERT INTO gathering (item_id, type) VALUES (?,?) ON DUPLICATE KEY UPDATE type=VALUES(type)");
		aMap.put(ItemTypes.Gizmo, "INSERT INTO gizmo (item_id, type) VALUES (?,?) ON DUPLICATE KEY UPDATE type=VALUES(type)");
		aMap.put(ItemTypes.Tool, "INSERT INTO tool (item_id, type, uses) VALUES (?,?,?) ON DUPLICATE KEY UPDATE type=VALUES(type), uses=VALUES(uses)");
		aMap.put(ItemTypes.Trinket, "INSERT INTO trinket (item_id, type, suffix_item_id) VALUES (?,?,?) ON DUPLICATE KEY UPDATE type=VALUES(type), suffix_item_id=VALUES(suffix_item_id)");
		aMap.put(ItemTypes.UpgradeComponent, "INSERT INTO upgrade_component (item_id, type, flags, infusion_upgrade_flags, suffix) VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE type=VALUES(type), flags=VALUES(flags), infusion_upgrade_flags=VALUES(infusion_upgrade_flags), suffix=VALUES(suffix)");
		aMap.put(ItemTypes.Weapon, "INSERT INTO weapon (item_id, type, damage_type, min_power, max_power, defense, suffix_item_id) VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE type=VALUES(type), damage_type=VALUES(damage_type), min_power=VALUES(min_power), max_power=VALUES(max_power), defense=VALUES(defense), suffix_item_id=VALUES(suffix_item_id)");
		itemSqlStrings = Collections.unmodifiableMap(aMap);
	}

	;

	/**
	 * Create the connection to our database, load the item_ids into an array
	 * for faster checking.
	 */
	private static void setupSQL() {
		Start.setStatusLabel("Setting up SQL.");
		Properties props = new Properties();
		try (FileInputStream in = new FileInputStream("database.properties")) {
			Start.setStatusLabel("Reading database properties.");
			props.load(in);
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(null, "database.properties not found.", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		final String url = props.getProperty("db.url");
		final String db = props.getProperty("db.database");
		final String user = props.getProperty("db.user");
		final String passwd = props.getProperty("db.password");
		try {
			Start.setStatusLabel("Opening connection.");
			con = DriverManager.getConnection(url.concat(db), user, passwd);
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			setup = true;
			ids = detailIds();
		}
	}

	private enum updateType {

		recipes, items
	}

	/**
	 *
	 * @param itemIds
	 * @param which
	 */
	public static void removeDeleted(ItemIds itemIds, String which) {
		String query = MessageFormat.format("DELETE FROM {0} WHERE {1}=?", which, ("items".equals(which)) ? "item_id" : "recipe_id");
		Start.setStatusLabel(MessageFormat.format("Deleting {0} removed {1}.", itemIds.removedCount(), which));
		// Foreign key constraints will take care of the rest.
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			Iterator<Integer> it = itemIds.getRemoved().iterator();
			while (it.hasNext()) {
				Integer itemId = it.next();
				stmt.setInt(1, itemId);
				stmt.executeUpdate();
				removedCount++;
				Start.setOutput(MessageFormat.format("Removed {0} id {1}.\n", ("items".equals(which)) ? "item" : "recipe", itemId));
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "removeDeleted: " + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 *
	 * @param which recipes or items
	 * @param remoteIds List of the remote ids.
	 * @return Populated class of item ids local, remote, added, removed.
	 */
	public static ItemIds pruneExisting(String which, ArrayList<Integer> remoteIds) {
		String idString = null;
		String tableString = null;
		ArrayList<Integer> localIds = new ArrayList<>();
		ArrayList<Integer> removedIds = new ArrayList<>();
		ArrayList<Integer> addedIds = new ArrayList<>();

		if (!setup) {
			setupSQL();
		}
		// Read the list of ids.
		try {
			if (isInEnum(which, updateType.class)) {
				switch (which) {
					case "recipes":
						idString = "recipe_id";
						tableString = "recipes";
						break;
					case "items":
						idString = "item_id";
						tableString = "items";
						break;
					default:
						throw new IllegalArgumentException(MessageFormat.format("Type \'{0}\' not written yet.", which));
				}
			} else {
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		try (PreparedStatement stmt = con.prepareStatement(MessageFormat.format("SELECT {0} FROM {1}", idString, tableString))) {
			Start.setStatusLabel("Prepare and execute query.");
			ResultSet rs = stmt.executeQuery();
			if (rs != null) {
				Start.setStatusLabel("Building array");
				while (rs.next()) {
					localIds.add(rs.getInt(idString));
				}
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}

		Start.setStatusLabel(MessageFormat.format("Pruning existing {0}.", which));

		addedIds.addAll(remoteIds);
		addedIds.removeAll(new HashSet<>(localIds));

		removedIds.addAll(localIds);
		removedIds.removeAll(new HashSet<>(remoteIds));

		return new ItemIds(addedIds, removedIds, remoteIds, localIds);
	}

	/**
	 * Check to see if the value is in the enumerated class.
	 *
	 * @param <E>
	 * @param value
	 * @param enumClass
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static <E extends Enum<E>> boolean isInEnum(String value, Class<E> enumClass) throws IllegalArgumentException {
		for (E e : enumClass.getEnumConstants()) {
			if (e.name().equals(value)) {
				return true;
			}
		}
		throw new IllegalArgumentException("Invalid Enum");
	}

	/**
	 *
	 * @param itemId Item ID
	 * @param inf Infix Upgrade object
	 */
	public static void writeInfix(Integer itemId, InfixUpgrade inf) {
		List<String> desc;
		List<Attributes> a = null;
		Start.setStatusLabel("Writing infix upgrades");
		Buff b = null;
		if (inf != null) {
			a = inf.getAttributes();
			b = inf.getBuff();
		}
		try (PreparedStatement buff = con.prepareStatement("INSERT INTO buff (item_id, skill_id, description) VALUES (?,?,?) ON DUPLICATE KEY UPDATE skill_id=VALUES(skill_id), description=VALUES(description)");
				PreparedStatement attr = con.prepareStatement("INSERT INTO attributes (item_id, attribute, modifier) VALUES (?,?,?) ON DUPLICATE KEY UPDATE attribute=VALUES(attribute), modifier=VALUES(modifier)")) {
			if (a != null) {
				Iterator<Attributes> it = a.iterator();
				Start.setStatusLabel("Writing attributes.");
				while (it.hasNext()) {
					Attributes att = it.next();
					attr.setInt(1, itemId);
					attr.setString(2, att.getAttribute());
					attr.setInt(3, att.getModifier());
					attr.executeUpdate();
				}
			}
			if (b != null) {
				desc = b.getDescription();
				Iterator<String> bi = desc.iterator();
				Start.setStatusLabel("Writing buffs.");
				while (bi.hasNext()) {
					buff.setInt(1, itemId);
					buff.setString(2, b.getSkill_id());
					buff.setString(3, bi.next());
					buff.executeUpdate();
				}
			}
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	private static void writeFlags(Integer itemId, String flags) {
		Start.setStatusLabel(MessageFormat.format("writeFlags: id:{0} flags: {1}", itemId, flags));
		if (flags != null && !flags.trim().isEmpty()) {
			Start.setStatusLabel("writeFlags: len>0");
			try (PreparedStatement clean = con.prepareStatement("DELETE FROM itemFlags WHERE item_id=?");
					PreparedStatement stmt = con.prepareStatement("INSERT INTO itemFlags (item_id, flag) VALUES (?,?)")) {
				Start.setStatusLabel("writeFlags: deleting old flags");
				clean.setInt(1, itemId);
				clean.executeUpdate();
				Start.setStatusLabel("writeFlags: entering loop");
				for (String flag : flags.split(",")) {
					Start.setStatusLabel(MessageFormat.format("writeFlags inside loop: writing id:{0} flag: {1}", itemId, flag.trim()));
					stmt.setInt(1, itemId);
					stmt.setString(2, flag.trim());
					stmt.executeUpdate();
				}
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
			Start.setStatusLabel("writeFlags: exiting");
	}

	private static void writeGameTypes(Integer itemId, String gameTypes) {
		Start.setStatusLabel(MessageFormat.format("writeGameTypes: id:{0} gameTypes: {1}", itemId, gameTypes));
		if (gameTypes != null && !gameTypes.trim().isEmpty()) {
			Start.setStatusLabel("writeGameTypes: len>0");
			try (PreparedStatement clean = con.prepareStatement("DELETE FROM itemGameTypes WHERE item_id=?");
					PreparedStatement stmt = con.prepareStatement("INSERT INTO itemGameTypes (item_id, gameType) VALUES (?,?)")) {
				Start.setStatusLabel("writeGameTypes: deleting old game types");
				clean.setInt(1, itemId);
				clean.executeUpdate();
				Start.setStatusLabel("writeGameTypes: entering loop");
				for (String gameType : gameTypes.split(",")) {
					Start.setStatusLabel(MessageFormat.format("writeGameTypes: writing id:{0} type: {1}", itemId, gameType));
					stmt.setInt(1, itemId);
					stmt.setString(2, gameType.trim());
					stmt.executeUpdate();
				}
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Write the item details. Some objects have no details so just silently
	 * return;
	 *
	 * @param itemId Item ID
	 * @param o Generic object holding the type details.
	 */
	public static void writeObject(Integer itemId, Object o) {
		try (PreparedStatement stmt = con.prepareStatement(itemSqlStrings.get(ItemTypes.valueOf(o.getClass().getSimpleName())))) {
			stmt.setInt(1, itemId);
			switch (ItemTypes.valueOf(o.getClass().getSimpleName())) {
				case Armor:
					Armor arm = (Armor) o;
					stmt.setString(2, arm.getType());
					stmt.setString(3, arm.getWeight_class());
					stmt.setInt(4, arm.getDefense());
					stmt.setInt(5, arm.getSuffix_item_id());
					break;
				case Back:
					Back back = (Back) o;
					stmt.setInt(2, back.getSuffix_item_id());
					break;
				case Bag:
					Bag bag = (Bag) o;
					stmt.setString(2, bag.getNo_sell_or_sort());
					stmt.setInt(3, bag.getSize());
					break;
				case Consumable:
					Consumable consumable = (Consumable) o;
					stmt.setString(2, consumable.getType());
					stmt.setInt(3, consumable.getDuration_ms());
					stmt.setString(4, consumable.getDescription());
					break;
				case Container:
					Container cont = (Container) o;
					stmt.setString(2, cont.getType());
					break;
				case Gathering:
					Gathering gather = (Gathering) o;
					stmt.setString(2, gather.getType());
					break;
				case Gizmo:
					Gizmo giz = (Gizmo) o;
					stmt.setString(2, giz.getType());
					break;
				case Tool:
					Tool tool = (Tool) o;
					stmt.setString(2, tool.getType());
					stmt.setInt(3, tool.getCharges());
					break;
				case Trinket:
					Trinket trinket = (Trinket) o;
					stmt.setString(2, trinket.getType());
					stmt.setInt(3, trinket.getSuffix_item_id());
					break;
				case UpgradeComponent:
					UpgradeComponent upComp = (UpgradeComponent) o;
					stmt.setString(2, upComp.getType());
					stmt.setString(3, upComp.getFlagsString());
					stmt.setString(4, upComp.getInfusionFlagsString());
					stmt.setString(5, upComp.getSuffix());
					break;
				case Weapon:
					Weapon weapon = (Weapon) o;
					stmt.setString(2, weapon.getType().trim());
					stmt.setString(3, weapon.getDamage_type().trim());
					stmt.setInt(4, weapon.getMin_power());
					stmt.setInt(5, weapon.getMax_power());
					stmt.setInt(6, weapon.getDefense());
					stmt.setInt(7, weapon.getSuffix_item_id());
					break;
				default:
					JOptionPane.showMessageDialog(null, MessageFormat.format("writeObject(): Unknown type: {0}", ItemTypes.valueOf(o.getClass().getSimpleName())), "Error", JOptionPane.ERROR_MESSAGE);
					break;
			}
			stmt.executeUpdate();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, MessageFormat.format("writeObject(): {0}\n{1}", ex.toString()), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static ArrayList<Long> detailIds() {
		Start.setStatusLabel("Getting item detail ids");
		try (PreparedStatement stmt = con.prepareStatement("SELECT item_id FROM item_detail ORDER BY item_id ASC");
			ResultSet rs = stmt.executeQuery();) {
			while (rs.next()) {
				ids.add(rs.getLong("item_id"));
			}
			return ids;
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	private static void writeItems(Integer itemId) {
		final String sql = "INSERT INTO items (item_id, added, preview) VALUES (?,?, ?) ON DUPLICATE KEY UPDATE updated=VALUES(added), preview=VALUES(preview)";
		final String cCode = new ChatCode(itemId).toString();

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, itemId);
			stmt.setString(2, today);
			stmt.setString(3, cCode);
			stmt.executeUpdate();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, MessageFormat.format("writeSqlItems: {0}", ex.toString()), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void writeItemName(int itemId, String itemName) {
		final String insertName = "INSERT INTO itemNames (itemId, itemName) VALUES (?,?) ON DUPLICATE KEY UPDATE itemName=VALUES(itemName)";
		try (PreparedStatement stmt = con.prepareStatement(insertName)) {
			stmt.setInt(1, itemId);
			stmt.setString(2, itemName);
			stmt.executeUpdate();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, MessageFormat.format("writeSqlItems: {0}", ex.toString()), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void writeItemDetails(ItemDetail item) {
		final String insert = "INSERT INTO item_detail (item_id,description,type,level,rarity,vendor_value,game_types,flags,restrictions,added,icon_file_id,icon_file_signature, preview)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE description=VALUES(description), type=VALUES(type), level=VALUES(level), rarity=VALUES(rarity), vendor_value=VALUES(vendor_value), game_types=VALUES(game_types), flags=VALUES(flags), restrictions=VALUES(restrictions), updated=VALUES(added), icon_file_id=VALUES(icon_file_id), icon_file_signature=VALUES(icon_file_signature), preview=VALUES(preview)";
		if (!setup) {
			setupSQL();
		}
		setStatus("Processing ");
		setName(item.getName());
		// Add a new item.
		Start.setStatusLabel(MessageFormat.format("Processing {1}.", item.getName()));
		setName(MessageFormat.format("{0}", item.getName()));
		// Add new item details
		total++;
		try (PreparedStatement stmt = con.prepareStatement(insert)) {
			if (!item.getName().equals("ERROR")) {
				Start.setStatusLabel(MessageFormat.format("Adding item details for {0}", item.getName()));
				stmt.setInt(1, item.getItemId());
				stmt.setString(2, item.getDescription().trim());
				stmt.setString(3, item.getType());
				stmt.setInt(4, item.getLevel());
				stmt.setString(5, item.getRarity().trim());
				stmt.setInt(6, item.getVendor_Value());
				stmt.setString(7, item.getGameTypes());
				stmt.setString(8, item.getFlags());
				stmt.setString(9, item.getRestrictions());
				stmt.setString(10, today);
				stmt.setString(11, item.getIconFileId());
				stmt.setString(12, item.getIconFileSignature());
				stmt.setString(13, item.getPreview());
				stmt.executeUpdate();
				added++;
			} else {
				skipped++;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, MessageFormat.format("writeSqlItems: {0}", e.toString()), "Error", JOptionPane.ERROR_MESSAGE);
			error = true;
		}
	}
/**
 * 
 * @param item Item to be written
 */
	public static void writeItem(ItemDetail item) {
		int itemId = item.getItemId();
		Start.setStatusLabel("Calling writeItem");
		writeItems(itemId);
		Start.setStatusLabel("Calling writeItemDetails");
		writeItemDetails(item);
		Start.setStatusLabel("Calling writeItemName");
		writeItemName(itemId, item.getName());
		Start.setStatusLabel("Calling writeFlags");
		writeFlags(itemId, item.getFlags());
		Start.setStatusLabel("Calling writeGameTypes");
		writeGameTypes(itemId, item.getGameTypes());
	}

	/**
	 *
	 * @param recipe
	 */
	public static void writeSqlRecipes(Recipe recipe) {
		PreparedStatement stmt = null;
		PreparedStatement stmt1 = null;
		if (error) {
			return;
		}
		if (!setup) {
			setupSQL();
		}
		try {
			List<Ingredient> l = recipe.getIngredients();
			PreparedStatement rStmt = con.prepareStatement("SELECT itemName FROM itemNames WHERE itemId=?");
			ResultSet rRes;
			String oName = null;
			rStmt.setString(1, recipe.getOutput_item_id());
			rStmt.executeQuery();
			rRes = rStmt.getResultSet();
			while (rRes.next()) {
				oName = rRes.getString("itemName");
			}
			cleanUp(rRes, rStmt);
			stmt = con.prepareStatement("INSERT INTO recipes (recipe_id, added) VALUES (?,?) ON DUPLICATE KEY UPDATE updated=VALUES(added)");
			stmt.setString(1, recipe.getRecipe_id());
			stmt.setString(2, today);
			stmt.executeUpdate();
			cleanUp(stmt);

			stmt = con.prepareStatement("INSERT INTO recipe_detail (recipe_id, type, output_item_id, output_item_count, min_rating, time_to_craft_ms, flags, disciplines, added) VALUES (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE type=VALUES(type), output_item_id=VALUES(output_item_id), output_item_count=VALUES(output_item_count), min_rating=VALUES(min_rating), time_to_craft_ms=VALUES(time_to_craft_ms), flags=VALUES(flags), disciplines=VALUES(disciplines), updated=VALUES(added)");
			stmt.setString(1, recipe.getRecipe_id());
			stmt.setString(2, recipe.getType());
			stmt.setString(3, recipe.getOutput_item_id());
			stmt.setString(4, recipe.getOutput_item_count());
			stmt.setString(5, recipe.getMin_rating());
			stmt.setString(6, recipe.getTime_to_craft_ms());
			stmt.setString(7, recipe.getFlags());
			stmt.setString(8, recipe.getDisciplines());
			stmt.setString(9, today);
			setName(oName != null ? oName : recipe.getRecipe_id());
			setStatus("Adding ingredients for ");
			stmt1 = con.prepareStatement("DELETE FROM recipe_ingredients WHERE recipe_id = ?");
			stmt1.setString(1, recipe.getRecipe_id());
			stmt1.executeUpdate();
			for (int i = 0; i < l.size(); i++) {
				stmt1 = con.prepareStatement("INSERT INTO recipe_ingredients (recipe_id, item_id, count) VALUES (?,?,?)");
				stmt1.setString(1, recipe.getRecipe_id());
				stmt1.setString(2, l.get(i).getItem_id());
				stmt1.setString(3, l.get(i).getCount().toString());
				stmt1.executeUpdate();
			}
			stmt = con.prepareStatement("DELETE FROM recipeDisciplines WHERE recipeId=?");
			stmt.setString(1, recipe.getRecipe_id());
			stmt.execute();
			stmt = con.prepareStatement("INSERT INTO recipeDisciplines (recipeId, discipline) VALUES (?,?)");
			for (String discipline : recipe.getDisciplines().split(",")) {
				stmt.setString(1, recipe.getRecipe_id());
				stmt.setString(2, discipline);
				stmt.executeUpdate();
			}
			setStatus("Adding recipe for ");
			stmt.executeUpdate();
			added++;
			total++;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			error = true;
		} finally {
			Start.setStatusLabel("Cleaning up stmt1");
			cleanUp(stmt);
			Start.setStatusLabel("Cleaning up stmt1");
			cleanUp(stmt1);
		}
	}

	/**
	 *
	 * @param errcount
	 */
	public static void setErrcount(int errcount) {
		Database.errcount = errcount;
	}

	/**
	 *
	 * @param error
	 */
	public static void setError(boolean error) {
		Database.error = error;
	}

	/**
	 *
	 * @param added
	 */
	public static void setAdded(int added) {
		Database.added = added;
	}

	/**
	 *
	 * @param skipped
	 */
	public static void setSkipped(int skipped) {
		Database.skipped = skipped;
	}

	/**
	 *
	 * @param total
	 */
	public static void setTotal(int total) {
		Database.total = total;
	}

	/**
	 *
	 * @param s
	 */
	public static void setSetup(boolean s) {
		Database.setup = s;
	}

	/**
	 *
	 * @return
	 */
	public static int getErrcount() {
		return errcount;
	}

	/**
	 *
	 */
	public static void incErrcount() {
		Database.errcount++;
	}

	/**
	 *
	 * @return
	 */
	public static List<String> getErrors() {
		return errors;
	}

	/**
	 *
	 * @return
	 */
	public static int getRemovedCount() {
		return removedCount;
	}

	/**
	 *
	 */
	public static void incTotal() {
		Database.total++;
	}

	/**
	 *
	 * @param error
	 */
	public static void addErrors(String error) {
		Database.errors.add(error);
		JOptionPane.showMessageDialog(null, Database.errors, "Error", JOptionPane.ERROR_MESSAGE);
	}
	private static String name;
	private static String status;

	private static void cleanUp(PreparedStatement st) {
		if (true) {
			return;
		}
		cleanUp(null, st, null);
	}

	/**
	 *
	 * @param con
	 */
	public static void cleanUp(Connection con) {
		if (true) {
			return;
		}
		cleanUp(null, null, con);
	}

	private static void cleanUp(ResultSet rs, PreparedStatement st) {
		if (true) {
			return;
		}
		cleanUp(rs, st, null);
	}

	private static void cleanUp(ResultSet rs, PreparedStatement st, Connection con) {
		if (true) {
			return;
		}
		StackTraceElement[] cause = Thread.currentThread().getStackTrace();
		try {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (con != null) {
				setup = false;
				con.close();
			}
		} catch (SQLException ex) {
			System.out.println("Exception in cleanUp");
			JOptionPane.showMessageDialog(null, ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		for (StackTraceElement cause1 : cause) {
			System.out.println("cause = " + cause1.toString());
		}
		System.out.println("\n");
	}

	/**
	 *
	 * @param name
	 */
	public static void setName(String name) {
		Database.name = name;
	}

	/**
	 *
	 * @param status
	 */
	public static void setStatus(String status) {
		Database.status = status;
	}

	/**
	 *
	 * @return
	 */
	public static String getName() {
		return name;
	}

	/**
	 *
	 * @return
	 */
	public static String getStatus() {
		return status;
	}

	/**
	 *
	 * @return
	 */
	public static int getAdded() {
		return added;
	}

	/**
	 *
	 * @return
	 */
	public static int getSkipped() {
		return skipped;
	}

	/**
	 *
	 * @return
	 */
	public static int getTotal() {
		return total;
	}

	/**
	 *
	 * @return
	 */
	public static Connection getCon() {
		return con;
	}
}

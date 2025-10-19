/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items;

/**
 *
 * @author Michael
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.swing.text.DefaultCaret;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.gw2items.Factories.Database;
import org.gw2items.Factories.WebClientDevWrapper;
import org.gw2items.models.ItemDetail;
import org.gw2items.models.ItemIds;
import org.gw2items.models.Recipe;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Michael
 */
public class Start extends JPanel {

	private static final long serialVersionUID = 1L;
	private final JProgressBar progressBar;
	private final JButton startButton;
	private final JCheckBox updChk;
	private static JTextArea taskOutput;
	private static JLabel statusLabel;
	private Task task;
	private long start_ns;
	private long end_ns;
	private static boolean update = false;
	private int end;
	private boolean run = false;
	private static boolean getItems = true;
	private static boolean getRecipes = false;
	private final static String API_URL = "https://api.guildwars2.com/";
	private final static String API_VERSION = "v1/";
	private final static String API_RECIPES = "recipes.json";
	private final static String API_ITEMS = "items.json";
	private final static String API_ITEMDETAIL = "item_details.json?item_id={0}";
	private final static String API_RECIPEDETAIL = "recipe_details.json?recipe_id={0}";

	/**
	 *
	 * @return
	 */
	public static boolean getUpdate() {
		return update;
	}

	/**
	 *
	 * @param s
	 */
	public static void setOutput(String s) {
		taskOutput.append(s);
	}

	/**
	 *
	 * @param s
	 */
	public static void setStatusLabel(String s) {
		statusLabel.setText(s);
	}

	class Task extends SwingWorker<Void, Integer> {

		public int getEnd() {
			return end;
		}

		/**
		 * Fetch JSON data from URL
		 *
		 * @param URL
		 * @return JSONObject
		 * @throws IOException
		 * @throws ParseException
		 */
		public JSONObject loadJSONObjectHTTP(String URL) {
			boolean retry = false;
			int l = 0;
			JSONObject obj = null;
			do {
				String respStr = null;
				HttpGet getString = new HttpGet(URL);
				HttpClient httpClient = new DefaultHttpClient();
				httpClient = WebClientDevWrapper.wrapClient(httpClient);

				HttpResponse response = null;
				setStatusLabel("Reading response.");
				try {
					response = httpClient.execute(getString);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(null, MessageFormat.format("1:{0}", ex.toString()), "Error", JOptionPane.ERROR_MESSAGE);
				}
				setStatusLabel("Converting to string.");
				try {
					respStr = EntityUtils.toString((response == null) ? null : response.getEntity());
				} catch (IOException | ParseException ex) {
					JOptionPane.showMessageDialog(null, MessageFormat.format("2:{0}", ex.toString()), "Error", JOptionPane.ERROR_MESSAGE);
				}
				setStatusLabel("Closing connection.");
				httpClient.getConnectionManager().shutdown();
				setStatusLabel("Parsing JSON.");
				try {
					obj = (JSONObject) new JSONParser().parse(respStr);
					retry = false;
				} catch (org.json.simple.parser.ParseException ex) {
					if (ex.toString().equals("Unexpected character (<) at position 0.")) {
						setStatusLabel(MessageFormat.format("Error, retrying after waiting {0} second{1}", l, (l == 1) ? "." : "s."));
						try {
							// Deal with lag error throwing 503 errors
							Thread.sleep(1000 * ((l < 6) ? l++ : l));
						} catch (InterruptedException ignore) {
						}
						retry = true;
					} else {
						JOptionPane.showMessageDialog(null, MessageFormat.format("3:{0}", ex.toString()), "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			} while (retry == true);
			return obj;
		}

		@Override
		public Void doInBackground() {
			boolean one = false;
			String oneItem = "39276";
			boolean detail = false;
			ArrayList<Integer> workingArray = new ArrayList<>();
			ItemIds itemIds;
			JSONObject obj;
			JSONArray arr;
			String itemsUrl = API_URL.concat(API_VERSION).concat(API_ITEMS);
			String recipesUrl = API_URL.concat(API_VERSION).concat(API_RECIPES);
			String itemDetailUrl = API_URL.concat(API_VERSION).concat(API_ITEMDETAIL);
			String recipeDetailUrl = API_URL.concat(API_VERSION).concat(API_RECIPEDETAIL);
			ItemDetail item;
			Recipe recipe;
			obj = loadJSONObjectHTTP(getItems ? itemsUrl : recipesUrl);
			arr = (JSONArray) obj.get(getItems ? "items" : "recipes");
			// Read items
			if (arr != null) {
				for (int i = 0; i < arr.size(); i++) {
					workingArray.add(Integer.valueOf(arr.get(i).toString()));
				}
				setOutput(MessageFormat.format("Read {0} total {1} ids.\n", workingArray.size(), getItems ? "item" : "recipe"));
			}

			String updateType = getItems ? "items" : "recipes";
			setStatusLabel("Pruning existing ids.");
			itemIds = Database.pruneExisting(updateType, workingArray);
			workingArray.clear();
			workingArray.addAll(update ? itemIds.getRemote() : itemIds.getAdded());
			if (!update) {
				if (itemIds.addedCount() > 0) {
					setOutput(MessageFormat.format("Pruned {0} entries.\nReading {1} {2}.\n", itemIds.remoteCount() - itemIds.addedCount(), workingArray.size(), updateType));
				} else {
					setOutput(MessageFormat.format("No new {0}.\n", getItems ? "items" : "recipes"));
				}
			} else {
				setOutput(MessageFormat.format("Reading {0} {1}.\n", workingArray.size(), updateType));
			}

			Database.removeDeleted(itemIds, updateType);
			setProgress(0);
			end = (one ? 1 : workingArray.size());
			progressBar.setMaximum(end);
			progressBar.setStringPainted(true);
			start_ns = System.nanoTime();
			setStatusLabel(MessageFormat.format("Reading {0} ids.", end));
			for (int t = 0; (t < end) && run; t++) {
				String url = getItems
						? MessageFormat.format(itemDetailUrl, !one ? workingArray.get(t).toString() : oneItem)
						: MessageFormat.format(recipeDetailUrl, workingArray.get(t).toString());
				obj = loadJSONObjectHTTP(url);
				if (!obj.containsKey("error")) {
					if (getItems) {
						setStatusLabel("Creating item.");
						item = new ItemDetail(obj);
						setStatusLabel("Writing to database.");
						Database.writeItem(item);
						setStatusLabel("Processig item type.");
						Types.processType(obj);
						if (detail) {
							setOutput(item.toString().concat("\n"));
						} else {
							setOutput(MessageFormat.format("{0} {1}...\n", Database.getStatus(), Database.getName()));
						}
						setStatusLabel("Returned from processType()");
					} else if (getRecipes) {
						setStatusLabel("Creating recipe.");
						recipe = new Recipe(obj);
						setStatusLabel("Writing recipe to database.");
						Database.writeSqlRecipes(recipe);
						setOutput(MessageFormat.format("{0} {1}...\n", Database.getStatus(), Database.getName()));
					}
				} else {
					setStatusLabel("Creating error item.");
					item = new ItemDetail();
					item.setItemId(workingArray.get(t));
					item.setName("ERROR");
					setStatusLabel("Writing error item.");
					Database.writeItem(item);
					setOutput(MessageFormat.format("\n---Error retrieving object_id {0}\n", workingArray.get(t).toString()));
					Database.incErrcount();
					Database.incTotal();
				}
				publish(t + 1);
			}
			setStatusLabel("Finished.  Cleaning up.");
			Database.cleanUp(Database.getCon());
			return null;
		}

		@Override
		protected void process(final List<Integer> chunks) {
			for (Integer chunk : chunks) {
				progressBar.setString(MessageFormat.format("{0}/{1}", chunk, progressBar.getMaximum()));
				progressBar.setValue(chunk);
			}
		}

		/*
		 * Executed in event dispatching thread
		 */
		@Override
		public void done() {
			end_ns = System.nanoTime();
			Toolkit.getDefaultToolkit().beep();
			startButton.setEnabled(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			run = false;
			startButton.setText("Start");
			if (Database.getAdded() != 0) {
				setOutput(MessageFormat.format("\n\nResults:\n{4}:   {0}\nRemoved: {5}\nSkipped: {1}\nErrors:   {3}\nTotal:   {2}\n", Database.getAdded(), Database.getSkipped(), Database.getTotal(), Database.getErrcount(), (update ? "Updated: " : "Added:   "), Database.getRemovedCount()));
				Database.setTotal(0);
				Database.setAdded(0);
				Database.setSkipped(0);
				Database.setErrcount(0);
				Long duration = end_ns - start_ns;
				String hms = String.format("Elapsed time:  %2d:%02d:%02d\n\n", TimeUnit.NANOSECONDS.toHours(duration),
						TimeUnit.NANOSECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.NANOSECONDS.toHours(duration)),
						TimeUnit.NANOSECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(duration)));
				setOutput(hms);
			}
		}
	}

	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public Start() {
		super(new BorderLayout());

		String[] options = {
			"Items",
			"Recipes"
		};
		//Create the UI.
		startButton = new JButton("Start");
		startButton.setActionCommand("start");
		startButton.addActionListener(new StartListener());
		startButton.addMouseListener(new StartMouseListener());

		updChk = new JCheckBox("Update");
		updChk.setSelected(false);
		updChk.addItemListener(new CheckBoxListener());

		@SuppressWarnings("unchecked")
		JComboBox<String> cb;
		cb = new JComboBox<>(options);
		cb.addActionListener(new ComboListener());
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(false);
		progressBar.setPreferredSize(new Dimension(400, 20));
		progressBar.setIndeterminate(false);

		taskOutput = new JTextArea(5, 20);
		taskOutput.setMargin(new Insets(5, 5, 5, 5));
		taskOutput.setEditable(false);
		DefaultCaret caret = (DefaultCaret) taskOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		statusLabel = new JLabel();
		statusLabel.setEnabled(false);

		JPanel panel = new JPanel();
		panel.add(startButton);
		panel.add(progressBar);
		panel.add(updChk);
		panel.add(cb);
		add(statusLabel, BorderLayout.SOUTH);
		add(panel, BorderLayout.PAGE_START);
		add(new JScrollPane(taskOutput), BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	}

	private class StartMouseListener implements MouseListener {

		@Override
		public void mouseEntered(MouseEvent e) {
			if (run) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (run) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
		}

		@Override
		public void mouseClicked(MouseEvent me) {
		}

		@Override
		public void mousePressed(MouseEvent me) {
		}

		@Override
		public void mouseReleased(MouseEvent me) {
		}
	}

	/**
	 * Invoked when the user presses the start button.
	 */
	private class StartListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent evt) {
			switch (startButton.getText()) {
				case "Start":
					startButton.setText("Stop");
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					run = true;
					task = new Task();
//					task.addPropertyChangeListener(new ProgressListener());
					start_ns = System.nanoTime();
					task.execute();
					break;
				case "Stop":
					startButton.setText("Start");
					setCursor(null);
					run = false;
					break;
			}
		}
	}

	/**
	 * Listens to the dropdown
	 */
	private class ComboListener implements ActionListener {

		@Override
		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
			JComboBox<String> c;
			c = (JComboBox<String>) e.getSource();
			int o = c.getSelectedIndex();
			switch (o) {
				case 0:
					getItems = true;
					getRecipes = false;
					break;
				case 1:
					getItems = false;
					getRecipes = true;
					break;
			}
		}
	}

	/**
	 * Listens to the check boxes.
	 */
	private class CheckBoxListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			update = e.getStateChange() != ItemEvent.DESELECTED;
		}
	}

	/**
	 * Create the GUI and show it. As with all GUI code, this must run on the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("GW2 Item And Recipe Updater");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension d = new Dimension(500, 500);
		frame.setMinimumSize(d);

		//Create and set up the content pane.
		JComponent newContentPane = new Start();
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);

		//Display the window.
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}
}

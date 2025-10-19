/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items;

/**
 *
 * @author Michael
 */
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;

/**
 *
 * @author Michael
 */
public class progressBarDemo {

	/**
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
                    JFrame frame = new ProgressBarFrame();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setVisible(true);
                });
	}
}

/**
 * A frame that contains a button to launch a simulated activity, a progress
 * bar, and a text area for the activity output.
 */
class ProgressBarFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public ProgressBarFrame() {
		setTitle("ProgressBarTest");
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

		// this text area holds the activity output
		textArea = new JTextArea();

		// set up panel with button and progress bar
		final int MAX = 50;
		JPanel panel = new JPanel();
		startButton = new JButton("Start");
		progressBar = new JProgressBar(0, MAX);
		progressBar.setStringPainted(true);
		panel.add(startButton);
		panel.add(progressBar);

		checkBox = new JCheckBox("indeterminate");
		checkBox.addActionListener((ActionEvent event) -> {
                    progressBar.setIndeterminate(checkBox.isSelected());
                    progressBar.setStringPainted(!progressBar.isIndeterminate());
                });
		panel.add(checkBox);
		add(new JScrollPane(textArea), BorderLayout.CENTER);
		add(panel, BorderLayout.SOUTH);

       // set up the button action
		startButton.addActionListener((final ActionEvent event) -> {
                    startButton.setEnabled(false);
                    activity = new SimulatedActivity(MAX);
                    activity.execute();
                });
	}

	final transient private JButton startButton;
	final transient private JProgressBar progressBar;
	final transient private JCheckBox checkBox;
	final transient private JTextArea textArea;
	transient private SimulatedActivity activity;

	public static final int DEFAULT_WIDTH = 400;
	public static final int DEFAULT_HEIGHT = 200;

	class SimulatedActivity extends SwingWorker<Void, Integer> {

		/**
		 * Constructs the simulated activity that increments a counter from 0 to
		 * a given target.
		 *
		 * @param t the target value of the counter.
		 */
		public SimulatedActivity(int t) {
			current = 0;
			target = t;
		}

		@Override
		protected Void doInBackground() throws Exception {
			try {
				while (current < target) {
					Thread.sleep(100);
					current++;
					publish(current);
				}
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void process(final List<Integer> chunks) {
			for (Integer chunk : chunks) {
				textArea.append(chunk + "\n");
				progressBar.setString(chunk + "/" + target);
				progressBar.setValue(chunk);
			}
		}

		@Override
		protected void done() {
			startButton.setEnabled(true);
		}

		private int current;
		private final int target;
	}
}
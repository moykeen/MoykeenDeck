package com.moykeen.deck.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;

import com.moykeen.deck.core.MoykeenDeckApp;
import com.moykeen.deck.util.MoykeenDeckUtilities;

public class StatisticsPanel1 extends JPanel{
	
	private JFreeChart dateChart, hourChart, weekDayChart, replyChart, usedAppChart, tweetTypeChart, wordCountChart;
	private ChartPanel cp = null;
	private JLabel promptLabel;
	
	public StatisticsPanel1() {
//		
//		// 棒グラフのデータ
//        double[][] data = new double[][]
//        		{{1.0, 2.0, 3.0, 4.0},
//        		{5.0, 6.0, 7.0, 8.0},
//        		{9.0, 10.0, 11.0, 12.0},
//        		{13.0, 14.0, 15.0, 16.0}};
//        // CategoryDatasetオブジェクトの作成
//        CategoryDataset cData = DatasetUtilities.createCategoryDataset("RowKey ", "ColKey ", data);
//        // CategoryDatasetをデータにしてJFreeChartを作成
//        JFreeChart barChart = ChartFactory.createBarChart ("SampleBarChart",
//        		"categoryAxisLabel",
//        		"valueAxisLabel",
//        		cData, PlotOrientation.VERTICAL,
//        		true, true, true);
                
		//MoykeenDeckApp.getInstance().getTweetController().loadStatusList("kimura6");
		//JFreeChart jfc = MoykeenDeckApp.getInstance().getTweetController().createReplyChart();
		//JFreeChart jfc = MoykeenDeckApp.getInstance().getTweetController().createUsedAppChart();
		//JFreeChart jfc = MoykeenDeckApp.getInstance().getTweetController().createTweetTypeStatChart();
		//JFreeChart jfc = MoykeenDeckApp.getInstance().getTweetController().createWordCountChart();
		//JFreeChart jfc = MoykeenDeckApp.getInstance().getTweetController().createWeekDayStatChart();
		
		
//		JFreeChart jfc = MoykeenDeckApp.getInstance().getTweetController().createHourStatChart();
	//	 XYBarRenderer barRenderer = new XYBarRenderer();
		 //barRenderer.setShadowVisible(false);
		 //barRenderer.setBarPainter(new StandardXYBarPainter());
		 //jfc.getXYPlot().setRenderer(barRenderer);
		
       
	
		cp = new ChartPanel(null);
		
		setLayout(new BorderLayout());
		//add(cp, BorderLayout.CENTER);
		promptLabel = MoykeenDeckUtilities.createSmallJLabel("   左のリストから見たい統計をお選び下さい");
		add(promptLabel, BorderLayout.CENTER);
		
		
		//ボタン
		Object saved = UIManager.get("ButtonUI");//QuaquaのボタンだとroundRectにならないので一時的に切り替える
		UIManager.put("ButtonUI", "com.apple.laf.AquaButtonUI");
		JButton tweetGraphButton = new JButton("moykeen!");
		MoykeenDeckUtilities.putSmallProperty(tweetGraphButton);
		tweetGraphButton.putClientProperty("JButton.buttonType", "roundRect");
		tweetGraphButton.setFocusable(false);		
		UIManager.put("ButtonUI", saved);//元に戻す
				

		JTextAreaWithEmacsKeybind commentTextArea = new JTextAreaWithEmacsKeybind();
		commentTextArea.setBorder(new LineBorder(Color.gray, 1));
		commentTextArea.setText("");
		
		//改行できないようにする	
		InputMap im = commentTextArea.getInputMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//do nothing
			}			
		});
		commentTextArea.setLineWrap(true);
		
		JPanel auxPanel = new JPanel();
		auxPanel.setLayout(new MigLayout("", "[grow,l,fill][r]", "[c][c] [grow,b]"));
		auxPanel.add(MoykeenDeckUtilities.createSmallJLabel("この結果に考察を添えてツイートしましょう"), "cell 0 0");
		auxPanel.add(commentTextArea, "cell 0 1 2 1");
		auxPanel.add(tweetGraphButton, "cell 1 2");
		
		
		
		add(auxPanel, BorderLayout.SOUTH);
		
		
		//動作
		tweetGraphButton.addActionListener(MoykeenDeckApp.getInstance().getTweetController().getSubmitGraphActionListener(commentTextArea, cp));

		
	}
	
	
	public void showDateStats(){
		
		if(dateChart == null){
			dateChart = MoykeenDeckApp.getInstance().getTweetController().createDateStatChart();
			XYBarRenderer barRenderer = new XYBarRenderer();
			barRenderer.setShadowVisible(false);
			barRenderer.setBarPainter(new StandardXYBarPainter());
			dateChart.getXYPlot().setRenderer(barRenderer);
			 
		}
		
		if(cp == null)
			cp = new ChartPanel(dateChart);
		else
			cp.setChart(dateChart);
		
		if(promptLabel != null){
			remove(promptLabel);
			promptLabel = null;
			add(cp, BorderLayout.CENTER);
			this.validate();
			//this.repaint();
		}
		
		
		
			
	}


	public void showHourStats() {
		if(hourChart == null){
			hourChart = MoykeenDeckApp.getInstance().getTweetController().createHourStatChart();
			XYBarRenderer barRenderer = new XYBarRenderer();
			barRenderer.setShadowVisible(false);
			barRenderer.setBarPainter(new StandardXYBarPainter());
			hourChart.getXYPlot().setRenderer(barRenderer);
			 
		}
		
		if(cp == null)
			cp = new ChartPanel(hourChart);
		else
			cp.setChart(hourChart);
		
		if(promptLabel != null){
			remove(promptLabel);
			promptLabel = null;
			add(cp, BorderLayout.CENTER);
			this.validate();
		}
	}


	public void showWeekDayStats() {
		if(weekDayChart == null){
			weekDayChart = MoykeenDeckApp.getInstance().getTweetController().createWeekDayStatChart();
			 
		}
		
		if(cp == null)
			cp = new ChartPanel(weekDayChart);
		else
			cp.setChart(weekDayChart);
		
		if(promptLabel != null){
			remove(promptLabel);
			promptLabel = null;
			add(cp, BorderLayout.CENTER);
			this.validate();
		}
		
	}


	public void showTweetTypeStats() {
		if(tweetTypeChart == null){
			tweetTypeChart = MoykeenDeckApp.getInstance().getTweetController().createTweetTypeStatChart();
			 
		}
		
		if(cp == null)
			cp = new ChartPanel(tweetTypeChart);
		else
			cp.setChart(tweetTypeChart);
		
		if(promptLabel != null){
			remove(promptLabel);
			promptLabel = null;
			add(cp, BorderLayout.CENTER);
			this.validate();
		}
		
	}


	public void showReplyStats() {
		if(replyChart == null){
			replyChart = MoykeenDeckApp.getInstance().getTweetController().createReplyChart();
			 
		}
		
		if(cp == null)
			cp = new ChartPanel(replyChart);
		else
			cp.setChart(replyChart);
		
		if(promptLabel != null){
			remove(promptLabel);
			promptLabel = null;
			add(cp, BorderLayout.CENTER);
			this.validate();
		}
		
	}


	public void showWordCountStats() {
		if(wordCountChart == null){
			wordCountChart = MoykeenDeckApp.getInstance().getTweetController().createWordCountChart();
			 
		}
		
		if(cp == null)
			cp = new ChartPanel(wordCountChart);
		else
			cp.setChart(wordCountChart);
		
		if(promptLabel != null){
			remove(promptLabel);
			promptLabel = null;
			add(cp, BorderLayout.CENTER);
			this.validate();
		}
		
	}


	public void showUsedAppStats() {
		if(usedAppChart == null){
			usedAppChart = MoykeenDeckApp.getInstance().getTweetController().createUsedAppChart();
			 
		}
		
		if(cp == null)
			cp = new ChartPanel(usedAppChart);
		else
			cp.setChart(usedAppChart);
		
		if(promptLabel != null){
			remove(promptLabel);
			promptLabel = null;
			add(cp, BorderLayout.CENTER);
			this.validate();
		}
		
		
	}
}

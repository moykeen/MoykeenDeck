package com.moykeen.deck.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import com.moykeen.deck.core.MoykeenDeckApp;
import com.moykeen.deck.util.MoykeenDeckUtilities;

import net.miginfocom.swing.MigLayout;

public class DataDownloadPanel extends JPanel {
	
	public DataDownloadPanel(final MoykeenDeckFrame f) {
		setLayout(new MigLayout());
		
		//OKボタン
		Object saved = UIManager.get("ButtonUI");
		UIManager.put("ButtonUI", "com.apple.laf.AquaButtonUI");
		JButton okButton = new JButton("データ取得開始");
		MoykeenDeckUtilities.putSmallProperty(okButton);
		okButton.putClientProperty("JButton.buttonType", "roundRect");
		okButton.setFocusable(false);	
		UIManager.put("ButtonUI", saved);//元に戻す
		
		//プログレスバー
		//JProgressBar progressBar = new JProgressBar(0, 100);
		
		add(MoykeenDeckUtilities.createSmallJLabel("解析のため、過去の全ツイートを取得します。時間がかかるので辛抱強く待って下さい。"), "wrap");
		add(okButton, "wrap");

		
		if(MoykeenDeckApp.getInstance().getTweetController().isStatusListAvailable()){
			add(MoykeenDeckUtilities.createSmallJLabel("もしくは、"), "wrap");
			//OKボタン
			saved = UIManager.get("ButtonUI");
			UIManager.put("ButtonUI", "com.apple.laf.AquaButtonUI");
			JButton usePreviousButton = new JButton("以前取得したデータを使う");
			MoykeenDeckUtilities.putSmallProperty(usePreviousButton);
			usePreviousButton.putClientProperty("JButton.buttonType", "roundRect");
			usePreviousButton.setFocusable(false);	
			UIManager.put("ButtonUI", saved);//元に戻す
			add(usePreviousButton);
			
			usePreviousButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					MoykeenDeckApp.getInstance().getTweetController().loadStatusList(null);
					if(f.statisticsPanel1 == null){
						f.statisticsPanel1 = new StatisticsPanel1();
						f.statisticsPanel1.setOpaque(true);
					}
					f.splitPane.setRightComponent(f.statisticsPanel1);
				}
			});
		}
		
		okButton.addActionListener(new ActionListener(){
			int downloaded;

			public void actionPerformed(ActionEvent e) {
				//MoykeenDeckApp.getInstance().getTweetController().getStatusIndicator().displayMessage("データ取得開始。結構時間がかかりますので気長に待って下さい。");
				f.startTask();
				
				
				SwingWorker sw = new SwingWorker(){
					protected Object doInBackground() throws Exception {
						downloaded = MoykeenDeckApp.getInstance().getTweetController().collectStatuses(null, null);
						MoykeenDeckApp.getInstance().getTweetController().saveStatusList();
						return null;
					}
					
					//完了
					public void done(){
						f.finishTask();
						MoykeenDeckApp.getInstance().getTweetController().getStatusIndicator().displayMessage("完了  " + downloaded + "件のツイートを取得しました");
						

						if(f.statisticsPanel1 == null){
							f.statisticsPanel1 = new StatisticsPanel1();
							f.statisticsPanel1.setOpaque(true);
						}
						f.splitPane.setRightComponent(f.statisticsPanel1);
					}
					
				};
				
				sw.execute();
				
			}
		
		});
		//okButton.addActionListener(MoykeenDeckApp.getInstance().getTweetController().getDataDownloadActionListener(progressBar));
	
		
	}
	

}

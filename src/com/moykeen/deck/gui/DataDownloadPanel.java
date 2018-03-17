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
		
		//OK�{�^��
		Object saved = UIManager.get("ButtonUI");
		UIManager.put("ButtonUI", "com.apple.laf.AquaButtonUI");
		JButton okButton = new JButton("�f�[�^�擾�J�n");
		MoykeenDeckUtilities.putSmallProperty(okButton);
		okButton.putClientProperty("JButton.buttonType", "roundRect");
		okButton.setFocusable(false);	
		UIManager.put("ButtonUI", saved);//���ɖ߂�
		
		//�v���O���X�o�[
		//JProgressBar progressBar = new JProgressBar(0, 100);
		
		add(MoykeenDeckUtilities.createSmallJLabel("��͂̂��߁A�ߋ��̑S�c�C�[�g���擾���܂��B���Ԃ�������̂Őh�������҂��ĉ������B"), "wrap");
		add(okButton, "wrap");

		
		if(MoykeenDeckApp.getInstance().getTweetController().isStatusListAvailable()){
			add(MoykeenDeckUtilities.createSmallJLabel("�������́A"), "wrap");
			//OK�{�^��
			saved = UIManager.get("ButtonUI");
			UIManager.put("ButtonUI", "com.apple.laf.AquaButtonUI");
			JButton usePreviousButton = new JButton("�ȑO�擾�����f�[�^���g��");
			MoykeenDeckUtilities.putSmallProperty(usePreviousButton);
			usePreviousButton.putClientProperty("JButton.buttonType", "roundRect");
			usePreviousButton.setFocusable(false);	
			UIManager.put("ButtonUI", saved);//���ɖ߂�
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
				//MoykeenDeckApp.getInstance().getTweetController().getStatusIndicator().displayMessage("�f�[�^�擾�J�n�B���\���Ԃ�������܂��̂ŋC���ɑ҂��ĉ������B");
				f.startTask();
				
				
				SwingWorker sw = new SwingWorker(){
					protected Object doInBackground() throws Exception {
						downloaded = MoykeenDeckApp.getInstance().getTweetController().collectStatuses(null, null);
						MoykeenDeckApp.getInstance().getTweetController().saveStatusList();
						return null;
					}
					
					//����
					public void done(){
						f.finishTask();
						MoykeenDeckApp.getInstance().getTweetController().getStatusIndicator().displayMessage("����  " + downloaded + "���̃c�C�[�g���擾���܂���");
						

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

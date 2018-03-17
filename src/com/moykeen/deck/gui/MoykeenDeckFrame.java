package com.moykeen.deck.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingWorker;


import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.explodingpixels.macwidgets.MacButtonFactory;
import com.explodingpixels.macwidgets.MacUtils;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.SourceListSelectionListener;
import com.explodingpixels.macwidgets.UnifiedToolBar;
import com.explodingpixels.widgets.WindowUtils;
import com.manisma.base.ui.frame.ApplicationWindow;
import com.moykeen.deck.core.MoykeenDeckApp;
import com.moykeen.deck.core.StatusIndicator;
import com.moykeen.deck.util.MoykeenDeckUtilities;

public class MoykeenDeckFrame extends ApplicationWindow implements StatusIndicator{
	
	/**
	 * �c�[���o�[�̃{�^��
	 */
	protected JButton accountButton, tweetButton, statsButton;
	
	
	/**
	 * �X�e�[�^�X���x��
	 */
	protected JLabel statusLabel;
	
	/**
	 * �v���O���X�o�[
	 */
	protected JProgressBar progressBar;
	
	/**
	 * �p�l��
	 */
	protected TweetPanel tweetPanel = null;
	protected AccountSettingPanel accountSettingPanel = null;
	protected StatisticsPanel1 statisticsPanel1 = null;
	protected DataDownloadPanel dataDownloadPanel = null;
	
	/**
	 * ������y�C��
	 */
	protected JSplitPane splitPane;
	
	/**
	 * ���v�̃��X�g�A�C�e��
	 */
	protected SourceListItem [] statsSourceListItems;
	
	
	public MoykeenDeckFrame(String title) {
		super();
		
		//�A�C�R��
		//�Ⴂ���ꂸ�B�B�B
		//String resName="default_window_icon_mac.png";
		//setIconImage(new ImageIcon(getClass().getResource("/resources/" + resName)).getImage());
				
		
		
		setTitle(title);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		initUI();
				
	}
	
	
	/**
	 * UI������
	 */
	private void initUI(){
		setLayout(new BorderLayout());
		
		//�X�v���b�g�y�C����p��
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.putClientProperty("Quaqua.SplitPane.style", "bar");
		splitPane.setDividerSize(1);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		
		//�\�[�X���X�g��ݒ�
		//�V�X�e���ŗp�ӂ��ꂽ�A�C�R�����g������
		Toolkit toolkit = Toolkit.getDefaultToolkit( );
		
		SourceListModel model = new SourceListModel();
		//SourceListCategory category1 = new SourceListCategory("�����̃��C�L�[��");
		//model.addCategory(category1);
		//model.addItemToCategory(new SourceListItem("����"), category1);
		////model.addItemToCategory(new SourceListItem("����", new ImageIcon(toolkit.getImage("NSImage://NSRefreshTemplate"))), category1);
		//model.addItemToCategory(new SourceListItem("���"), category1);
		//model.addItemToCategory(new SourceListItem("�ߋ���T��"), category1);
		
		//SourceListCategory category3 = new SourceListCategory("�Ȃ���");
		//model.addCategory(category3);
		//model.addItemToCategory(new SourceListItem("���C�L�[���ȂԂ₫������"), category3);
		//model.addItemToCategory(new SourceListItem("���C�L�[���Ȑl��T��"), category3);
		//model.addItemToCategory(new SourceListItem("�F�B�����C�L�[���ɏ���"), category3);
		
		
		statsSourceListItems = new SourceListItem[]{new SourceListItem("�c�C�[�g��"), new SourceListItem("���ԑтƃc�C�[�g��"), new SourceListItem("�j���ƃc�C�[�g��"), new SourceListItem("�c�C�[�g�^�C�v"), new SourceListItem("�悭��b���郆�[�U"), new SourceListItem("�c�C�[�g�̕������̕��z"), new SourceListItem("�悭�g���A�v��")};
		SourceListCategory category2 = new SourceListCategory("���v");
		model.addCategory(category2);
		for(int i = 0; i < statsSourceListItems.length; i++){
			model.addItemToCategory(statsSourceListItems[i], category2);
		}
		
	
		//model.addItemToCategory(new SourceListItem("���"), category2);
		//model.addItemToCategory(new SourceListItem("�ߋ���T��"), category2);
				
		SourceList sourceList = new SourceList(model);
		JComponent sourceListComponent = sourceList.getComponent();
		//sourceListComponent.setPreferredSize(new Dimension(200, 100)); //preferred size���minimum size��ݒ肵�������ǂ��݂���
		sourceListComponent.setMinimumSize(new Dimension(200, 100));
		splitPane.setLeftComponent(sourceListComponent);
		
		//source list�Ƀ��X�i����
		sourceList.addSourceListSelectionListener(new SourceListSelectionListener(){
			public void sourceListItemSelected(SourceListItem sli) {
				if(!MoykeenDeckApp.getInstance().getTweetController().isStatsPrepared()){
					statsButton.doClick();
					return;
				}
				
				
				if(sli == statsSourceListItems[0]){
					statisticsPanel1.showDateStats();
				}else if(sli == statsSourceListItems[1]){
					statisticsPanel1.showHourStats();
				}else if(sli == statsSourceListItems[2]){
					statisticsPanel1.showWeekDayStats();
				}else if(sli == statsSourceListItems[3]){
					statisticsPanel1.showTweetTypeStats();
				}else if(sli == statsSourceListItems[4]){
					statisticsPanel1.showReplyStats();
				}else if(sli == statsSourceListItems[5]){
					statisticsPanel1.showWordCountStats();
				}else if(sli == statsSourceListItems[6]){
					statisticsPanel1.showUsedAppStats();
				}
				
				
				
				
				
				
//				System.out.println(arg0);
//				if(statisticsPanel1 == null){
//					statisticsPanel1 = new StatisticsPanel1();
//					statisticsPanel1.setOpaque(true);
//				}
//				splitPane.setRightComponent(statisticsPanel1);
				
				
			}
			
		});
		
		
		
		//������ԂŁA�c�C�[�g�p�l����\������
		tweetPanel = new TweetPanel();
		tweetPanel.setOpaque(true);
		splitPane.setRightComponent(tweetPanel);
			

		//�c�[���o�[�֘A
		initToolbarComponents();	
		MacUtils.makeWindowLeopardStyle(this.getRootPane());
		WindowUtils.createAndInstallRepaintWindowFocusListener(this);
		UnifiedToolBar toolBar = new UnifiedToolBar();
		initToolBar(toolBar);
		toolBar.installWindowDraggerOnWindow(this);
		this.getContentPane().add(toolBar.getComponent(), BorderLayout.NORTH);
		
		
		//�{�g���o�[�֘A
		statusLabel = MoykeenDeckUtilities.createSmallJLabel("");
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);//�f�t�H���g�ł͕\�����Ȃ�
		progressBar.putClientProperty("JProgressBar.style", "circular");

		BottomBar bottomBar = new BottomBar(BottomBarSize.SMALL);
		initBottomBar(bottomBar);
		bottomBar.installWindowDraggerOnWindow(this);
		this.getContentPane().add(bottomBar.getComponent(), BorderLayout.SOUTH);

		
		setSize(700, 400);
		//setPreferredSizeSize(new Dimension(700, 400));
		//frame.setMinimumSize(new Dimension(1000, 280));
		
		//validateButtonsAndMenus();
		//updateBottomBar();
		//addActionListeners();
	}
	
	private void setPreferredSizeSize(Dimension dimension) {
		
	}


	private void initToolbarComponents() {
		accountButton = new JButton("�A�J�E���g");
		//accountButton.setIcon(new ImageIcon(getClass().getResource("/resources/twitter_account.png")));
		accountButton.setIcon(new ImageIcon(getClass().getResource("/resources/md_for_toolbar.png")));
		
		tweetButton = new JButton("moykeen����");
		tweetButton.setIcon(new ImageIcon(getClass().getResource("/resources/tweet_icon.png")));
		//tweetButton.setIcon(new ImageIcon(getClass().getResource("/resources/t_official.png")));
		
		statsButton = new JButton("�A�i���C�Y");
		statsButton.setIcon(new ImageIcon(getClass().getResource("/resources/stats_icon.png")));
		
		
		accountButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				
				//�A�J�E���g�p�l���͍��̂Ɏ��Ԃ�������̂ŁA�ʃX���b�h�ł��
				if(accountSettingPanel == null){
					startTask();
					
					SwingWorker sw = new SwingWorker(){
						protected Object doInBackground() throws Exception {
							accountSettingPanel = new AccountSettingPanel();
							accountSettingPanel.setOpaque(true);
							return null;
						}
						
						//�p�l���ł����������̂ŕ\��
						public void done(){
							splitPane.setRightComponent(accountSettingPanel);
							finishTask();
							
						}
						
					};
					
					sw.execute();
					
					//accountSettingPanel = new AccountSettingPanel();
					//accountSettingPanel.setOpaque(true);
				}else{
					splitPane.setRightComponent(accountSettingPanel);
				}
				
			}
		});
		
		
		tweetButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				splitPane.setRightComponent(tweetPanel);
			}
		});
		
		
		statsButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(dataDownloadPanel == null)
					dataDownloadPanel = new DataDownloadPanel(MoykeenDeckFrame.this);
				
				dataDownloadPanel.setOpaque(true);
				splitPane.setRightComponent(dataDownloadPanel);
			}
		});
		
		
	}
	
	private void initBottomBar(BottomBar bottomBar) {
		bottomBar.addComponentToLeft(statusLabel);
		bottomBar.addComponentToRight(progressBar, 30);
		
	}

	private void initToolBar(UnifiedToolBar toolBar) {
		toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(accountButton));
		toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(tweetButton));
		toolBar.addComponentToLeft(MacButtonFactory.makeUnifiedToolBarButton(statsButton));
	}


	

	@Override
	public JMenu[] getGlobalWindowMenus(Class<?> arg0) {
		return null;
	}

	@Override
	public boolean isAlwaysVisibleInWindowsMenu() {
		return false;
	}

	@Override
	public boolean isWindowsMenuEnabled() {
		return false;
	}


	/**
	 * 
	 */
	public void displayMessage(String message) {
		statusLabel.setText(message);
	}


	public void startTask() {
		progressBar.setVisible(true);
	}


	public void finishTask() {
		progressBar.setVisible(false);
		
	}

}

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
	 * ツールバーのボタン
	 */
	protected JButton accountButton, tweetButton, statsButton;
	
	
	/**
	 * ステータスラベル
	 */
	protected JLabel statusLabel;
	
	/**
	 * プログレスバー
	 */
	protected JProgressBar progressBar;
	
	/**
	 * パネル
	 */
	protected TweetPanel tweetPanel = null;
	protected AccountSettingPanel accountSettingPanel = null;
	protected StatisticsPanel1 statisticsPanel1 = null;
	protected DataDownloadPanel dataDownloadPanel = null;
	
	/**
	 * 分けるペイン
	 */
	protected JSplitPane splitPane;
	
	/**
	 * 統計のリストアイテム
	 */
	protected SourceListItem [] statsSourceListItems;
	
	
	public MoykeenDeckFrame(String title) {
		super();
		
		//アイコン
		//違い現れず。。。
		//String resName="default_window_icon_mac.png";
		//setIconImage(new ImageIcon(getClass().getResource("/resources/" + resName)).getImage());
				
		
		
		setTitle(title);
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		initUI();
				
	}
	
	
	/**
	 * UIをつくる
	 */
	private void initUI(){
		setLayout(new BorderLayout());
		
		//スプリットペインを用意
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.putClientProperty("Quaqua.SplitPane.style", "bar");
		splitPane.setDividerSize(1);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		
		//ソースリストを設定
		//システムで用意されたアイコンを使うため
		Toolkit toolkit = Toolkit.getDefaultToolkit( );
		
		SourceListModel model = new SourceListModel();
		//SourceListCategory category1 = new SourceListCategory("自分のモイキーン");
		//model.addCategory(category1);
		//model.addItemToCategory(new SourceListItem("今日"), category1);
		////model.addItemToCategory(new SourceListItem("今日", new ImageIcon(toolkit.getImage("NSImage://NSRefreshTemplate"))), category1);
		//model.addItemToCategory(new SourceListItem("昨日"), category1);
		//model.addItemToCategory(new SourceListItem("過去一週間"), category1);
		
		//SourceListCategory category3 = new SourceListCategory("つながり");
		//model.addCategory(category3);
		//model.addItemToCategory(new SourceListItem("モイキーンなつぶやきを検索"), category3);
		//model.addItemToCategory(new SourceListItem("モイキーンな人を探す"), category3);
		//model.addItemToCategory(new SourceListItem("友達をモイキーンに招待"), category3);
		
		
		statsSourceListItems = new SourceListItem[]{new SourceListItem("ツイート回数"), new SourceListItem("時間帯とツイート回数"), new SourceListItem("曜日とツイート回数"), new SourceListItem("ツイートタイプ"), new SourceListItem("よく会話するユーザ"), new SourceListItem("ツイートの文字数の分布"), new SourceListItem("よく使うアプリ")};
		SourceListCategory category2 = new SourceListCategory("統計");
		model.addCategory(category2);
		for(int i = 0; i < statsSourceListItems.length; i++){
			model.addItemToCategory(statsSourceListItems[i], category2);
		}
		
	
		//model.addItemToCategory(new SourceListItem("昨日"), category2);
		//model.addItemToCategory(new SourceListItem("過去一週間"), category2);
				
		SourceList sourceList = new SourceList(model);
		JComponent sourceListComponent = sourceList.getComponent();
		//sourceListComponent.setPreferredSize(new Dimension(200, 100)); //preferred sizeよりminimum sizeを設定した方が良いみたい
		sourceListComponent.setMinimumSize(new Dimension(200, 100));
		splitPane.setLeftComponent(sourceListComponent);
		
		//source listにリスナつける
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
		
		
		
		//初期状態で、ツイートパネルを表示する
		tweetPanel = new TweetPanel();
		tweetPanel.setOpaque(true);
		splitPane.setRightComponent(tweetPanel);
			

		//ツールバー関連
		initToolbarComponents();	
		MacUtils.makeWindowLeopardStyle(this.getRootPane());
		WindowUtils.createAndInstallRepaintWindowFocusListener(this);
		UnifiedToolBar toolBar = new UnifiedToolBar();
		initToolBar(toolBar);
		toolBar.installWindowDraggerOnWindow(this);
		this.getContentPane().add(toolBar.getComponent(), BorderLayout.NORTH);
		
		
		//ボトムバー関連
		statusLabel = MoykeenDeckUtilities.createSmallJLabel("");
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);//デフォルトでは表示しない
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
		accountButton = new JButton("アカウント");
		//accountButton.setIcon(new ImageIcon(getClass().getResource("/resources/twitter_account.png")));
		accountButton.setIcon(new ImageIcon(getClass().getResource("/resources/md_for_toolbar.png")));
		
		tweetButton = new JButton("moykeenする");
		tweetButton.setIcon(new ImageIcon(getClass().getResource("/resources/tweet_icon.png")));
		//tweetButton.setIcon(new ImageIcon(getClass().getResource("/resources/t_official.png")));
		
		statsButton = new JButton("アナライズ");
		statsButton.setIcon(new ImageIcon(getClass().getResource("/resources/stats_icon.png")));
		
		
		accountButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				
				//アカウントパネルは作るのに時間がかかるので、別スレッドでやる
				if(accountSettingPanel == null){
					startTask();
					
					SwingWorker sw = new SwingWorker(){
						protected Object doInBackground() throws Exception {
							accountSettingPanel = new AccountSettingPanel();
							accountSettingPanel.setOpaque(true);
							return null;
						}
						
						//パネルできあがったので表示
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

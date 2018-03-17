
package com.moykeen.deck.core;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.explodingpixels.macwidgets.UnifiedToolBar;
import com.manisma.base.core.Application;
import com.manisma.base.ui.frame.ApplicationWindow;
import com.moykeen.deck.gui.MoykeenDeckFrame;


/**
 * 
 */
public final class MoykeenDeckApp extends Application{
	
	/**
	 * The OAuth keys
	 */
	public static final String OAUTH_CONSUMER_KEY = "cuMcdCYXaX2PQoOJZL9XuA";
	public static final String OAUTH_CONSUMER_SECRET = "6yYGo2egSFAYRpNTwgUcV1ytdvZBhnkVRdb84CD3n4";

	
	/**
	 * Controller
	 */
	private TweetController tweetController = null;
	
	/**
	 * @return The application's instance
	 */
	public static MoykeenDeckApp getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * The singleton holder
	 */
	private static class SingletonHolder {
		private static final MoykeenDeckApp INSTANCE = new MoykeenDeckApp();
	}
	
	
	/**
	 * The private constructer 
	 */
	private MoykeenDeckApp() {
	}
	
	/**
	 * The entry point for the app
	 */
	@Override
	public void run() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tweetController = getTweetController();
				
				// Init application windows
				initApplicationWindows();
				
				// Add default menubar to all windows
				updateApplicationMenu();
				
				showMainWindow();
				
				//スタートアップ時にやることをやる
				handleStartupUserInteraction();					
				
//				/*
//				 * Check for updates, if required
//				 */
//				if (getPreferences().getBoolean(PrefConstants.CHECK_FOR_UPDATES_ON_STARTUP,
//						PrefConstants.CHECK_FOR_UPDATES_ON_STARTUP_DEFAULT)) {
//					ApplicationUtilities
//					.checkForUpdates(
//							false,
//							getPreferences()
//							.getBoolean(
//									com.manisma.base.constants.PrefConstants.SUBMIT_ANONYMOUS_KEY,
//									com.manisma.base.constants.PrefConstants.SUBMIT_ANONYMOUS_KEY_DEFAULT));
//				}
				
				/*
				 * Add window listener to invisible window, so that
				 * the app can be displayed whenever the application
				 * is brought to front
				 */
				Window[] windows = Window.getWindows();
				for(Window window : windows) {
					if(window.getX() == 10000 && window.getY() == 10000) {
						window.addWindowListener(new WindowAdapter() {
							public void windowActivated(WindowEvent e) {
								if (e.getOppositeWindow() == null) {
									for(ApplicationWindow window : applicationWindows)
										if(window.getState() == JFrame.ICONIFIED)
											return;
											
											showMainWindow();
								}
								
							}
						});
					}
				}
				
				
			}
		});
		
	}
	
	/**
	 * init app window
	 */
	private void initApplicationWindows(){
		MoykeenDeckFrame mainFrame = new MoykeenDeckFrame("Moykeen Deck");
		
		//frame.setSize(600, 500);
		//frame.setMinimumSize(new Dimension(1000, 280));
		mainFrame.setLocationRelativeTo(null);
		this.restoreSavedWindowState(mainFrame);
		
		this.registerApplicationWindow(mainFrame);
		tweetController.setStatusIndicator(mainFrame);
	}
	
	/**
	 * メインウインドウ表示
	 */
	public void showMainWindow() {
		ApplicationWindow frame = getWindow(MoykeenDeckFrame.class);

		if(frame != null) {
			frame.setVisible(true);
			frame.setAlwaysOnTop(true);
			frame.toFront();
			frame.setAlwaysOnTop(false);
		}
	}	

	
	/**
	 * スタートアップ時に行うこと。
	 */
	private void handleStartupUserInteraction() {
		//twitter コントローラを準備
		
		//現在UI作成中なのでOFF
		getTweetController().prepareForTwitter();
		
	}
	
	/**
	 * Terminates the application
	 */
	public void shutdown() {
		System.exit(0);
	}
	
	/**
	 * Displays the aboutBox
	 */
	public void showAboutBox() {
		//Do nothing
	}
	
	/**
	 * Displays the preferences window
	 */
	public void showPreferences() {
		//Do nothing

	}
	
	/**
	 * @param file
	 */
	public void handleOpenFile(final String file) {
		//DO nothing

	}
	


	@Override
	protected void buildApplicationMenuBar(JMenuBar arg0) {
		
	}





	@Override
	protected void buildHelpMenu(JMenu arg0) {
		
	}





	@Override
	public String getApplicationName() {
		return "Moykeen Deck";
	}





	@Override
	public double getApplicationVersion() {
		return 0;
	}





	@Override
	public boolean isAgent() {
		return false;
	}

	/**
	 * get the tweet controller
	 * @return
	 */
	public TweetController getTweetController() {
		if(tweetController == null){
			tweetController = new TweetController();
		}
		return tweetController;
	}




}



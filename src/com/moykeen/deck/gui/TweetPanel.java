package com.moykeen.deck.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import net.miginfocom.swing.MigLayout;

import com.moykeen.deck.core.MoykeenDeckApp;
import com.moykeen.deck.core.TweetController;
import com.moykeen.deck.util.MoykeenDeckUtilities;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TweetPanel extends JPanel{
	
	
	/**
	 * Components
	 */
	protected JTextArea bodyTextArea;
	//protected JTextField bodyTextField;
	protected JButton submitButton;
		
	public TweetPanel() {
		
		//body text 
		//bodyTextField = new JTextField();
		bodyTextArea = new JTextAreaWithEmacsKeybind();
		bodyTextArea.setBorder(new LineBorder(Color.gray, 1));
		
		//改行できないようにする	
		InputMap im = bodyTextArea.getInputMap();
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//do nothing
			}			
		});
		bodyTextArea.setLineWrap(true);
		
		
		
		//ボタン
		Object saved = UIManager.get("ButtonUI");//QuaquaのボタンだとroundRectにならないので一時的に切り替える
		UIManager.put("ButtonUI", "com.apple.laf.AquaButtonUI");
		submitButton =  new JButton("moykeen!");
		MoykeenDeckUtilities.putSmallProperty(submitButton);
		submitButton.putClientProperty("JButton.buttonType", "roundRect");
		submitButton.setFocusable(false);		
		UIManager.put("ButtonUI", saved);//元に戻す
		
	
		
		//配置
		setBorder(new EmptyBorder(20, 20, 20, 20));
		setLayout(new MigLayout("", "[grow,l,fill][r]", "[c][c] [grow,b]"));

		add(MoykeenDeckUtilities.createSmallJLabel("どうぞ呟いてください"), "cell 0 0");
		//add(bodyTextField, "cell 0 1 2 1");
		add(bodyTextArea, "cell 0 1 2 1");
		add(submitButton, "cell 1 2");

						
		//動作
		//submitButton.addActionListener(MoykeenDeckApp.getInstance().getTweetController().getSubmitTweetActionListener(bodyTextField));
		submitButton.addActionListener(MoykeenDeckApp.getInstance().getTweetController().getSubmitTweetActionListener(bodyTextArea));
	}

	
}
	
	

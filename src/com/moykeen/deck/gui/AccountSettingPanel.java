package com.moykeen.deck.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import net.miginfocom.swing.MigLayout;

import com.moykeen.deck.core.MoykeenDeckApp;
import com.moykeen.deck.util.MoykeenDeckUtilities;

public class AccountSettingPanel extends JPanel{
	
	
	public AccountSettingPanel() {
		
		//指示文
		HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
		StyleSheet styleSheet = htmlEditorKit.getStyleSheet();
		styleSheet.addRule("body {font-size: 12pt; font-family: lucida grande} ");
		htmlEditorKit.setStyleSheet(styleSheet);
		JEditorPane editor = new JEditorPane();
		editor.setEditorKit(htmlEditorKit);
		String MySite = MoykeenDeckApp.getInstance().getTweetController().getAuthorizationSiteURLString();
		editor.setText("<html><ol><li>MoykeenDeckがあなたのtwitterアカウントを利用することを許可するため、下記URLにアクセスしてください。<br/>" +
				"<small><a href='"+MySite+"'>"+MySite+"</a></small></li><br/>" +
				"<li>表示されたPINコードを入力して下さい。</li></ol>");
		editor.setOpaque(false); //editor.setBackground(getBackground());
		editor.setEditable(false); //REQUIRED
		editor.setFont(MoykeenDeckUtilities.get12ptFont());
						
		//PIN入力フィールド
		JTextField pinField = new JTextField(10);
		
		//OKボタン
		Object saved = UIManager.get("ButtonUI");
		UIManager.put("ButtonUI", "com.apple.laf.AquaButtonUI");
		JButton okButton = new JButton("認証");
		MoykeenDeckUtilities.putSmallProperty(okButton);
		okButton.putClientProperty("JButton.buttonType", "roundRect");
		okButton.setFocusable(false);		
		okButton.setEnabled(!MoykeenDeckApp.getInstance().getTweetController().getAuthorizationDone());
		UIManager.put("ButtonUI", saved);//元に戻す
		
		//配置
		setBorder(new EmptyBorder(20, 20, 20, 20));
		setLayout(new MigLayout("", "[l]10[l][grow,r]", "[t,grow][c]"));
				
		add(editor, "cell 0 0 3 1");
		add(MoykeenDeckUtilities.createSmallJLabel("PIN"), "cell 0 1");
		add(pinField, "cell 1 1");
		add(okButton, "cell 2 1");
		
		
		
		
		//動作
		okButton.addActionListener(MoykeenDeckApp.getInstance().getTweetController().getAccountRegistrationActionListener(pinField, okButton));

		//ブラウザ開く動作
		editor.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                      } catch (IOException ioe){
                    	  ioe.printStackTrace();
                      } catch (URISyntaxException use) {
						use.printStackTrace();
					}
                    
                }
            }
        });
		
	}

}

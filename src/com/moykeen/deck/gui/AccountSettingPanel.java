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
		
		//�w����
		HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
		StyleSheet styleSheet = htmlEditorKit.getStyleSheet();
		styleSheet.addRule("body {font-size: 12pt; font-family: lucida grande} ");
		htmlEditorKit.setStyleSheet(styleSheet);
		JEditorPane editor = new JEditorPane();
		editor.setEditorKit(htmlEditorKit);
		String MySite = MoykeenDeckApp.getInstance().getTweetController().getAuthorizationSiteURLString();
		editor.setText("<html><ol><li>MoykeenDeck�����Ȃ���twitter�A�J�E���g�𗘗p���邱�Ƃ������邽�߁A���LURL�ɃA�N�Z�X���Ă��������B<br/>" +
				"<small><a href='"+MySite+"'>"+MySite+"</a></small></li><br/>" +
				"<li>�\�����ꂽPIN�R�[�h����͂��ĉ������B</li></ol>");
		editor.setOpaque(false); //editor.setBackground(getBackground());
		editor.setEditable(false); //REQUIRED
		editor.setFont(MoykeenDeckUtilities.get12ptFont());
						
		//PIN���̓t�B�[���h
		JTextField pinField = new JTextField(10);
		
		//OK�{�^��
		Object saved = UIManager.get("ButtonUI");
		UIManager.put("ButtonUI", "com.apple.laf.AquaButtonUI");
		JButton okButton = new JButton("�F��");
		MoykeenDeckUtilities.putSmallProperty(okButton);
		okButton.putClientProperty("JButton.buttonType", "roundRect");
		okButton.setFocusable(false);		
		okButton.setEnabled(!MoykeenDeckApp.getInstance().getTweetController().getAuthorizationDone());
		UIManager.put("ButtonUI", saved);//���ɖ߂�
		
		//�z�u
		setBorder(new EmptyBorder(20, 20, 20, 20));
		setLayout(new MigLayout("", "[l]10[l][grow,r]", "[t,grow][c]"));
				
		add(editor, "cell 0 0 3 1");
		add(MoykeenDeckUtilities.createSmallJLabel("PIN"), "cell 0 1");
		add(pinField, "cell 1 1");
		add(okButton, "cell 2 1");
		
		
		
		
		//����
		okButton.addActionListener(MoykeenDeckApp.getInstance().getTweetController().getAccountRegistrationActionListener(pinField, okButton));

		//�u���E�U�J������
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

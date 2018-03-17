package com.moykeen.deck.util;

import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;


public abstract class MoykeenDeckUtilities {
	
	/**
	 * small size (11pt) font
	 * @return an 11pt font
	 */
	public static Font getSmallFont(){
		Font smallFont = UIManager.getFont("SmallSystemFont");
		if(smallFont == null){
			return new Font("LucidaGrande", Font.PLAIN, 11);
		}
		return smallFont;
	}
	
	/**
	 * 12pt font
	 * @return a 12pt font
	 */
	public static Font get12ptFont(){
		return new Font("LucidaGrande", Font.PLAIN, 12);
	}
	
	
	/**
	 * give the property of small to components 
	 * @param c list of components (variable length arguments)
	 */
	public static void putSmallProperty(JComponent ... c){
		for(int i = 0; i < c.length; i++){
			c[i].putClientProperty("JComponent.sizeVariant", "small");
		}
	}
	
	/**
	 * give the "mini" property to components
	 * @param c
	 */
	public static void putMiniProperty(JComponent ... c){
		for(int i = 0; i < c.length; i++){
			c[i].putClientProperty("JComponent.sizeVariant", "mini");
		}
	}
	
	/**
	 * create a small label 
	 * @param s 
	 * @return
	 */
	public static JLabel createSmallJLabel(String s){
		JLabel label = new JLabel(s);
		label.putClientProperty("JComponent.sizeVariant", "small");
		return label;
	}
	
	/**
	 * create a 12pt label
	 * @param s
	 * @return
	 */
	public static JLabel create12ptJLabel(String s){
		JLabel label = new JLabel(s);
		label.setFont(get12ptFont());
		//label.putClientProperty("JComponent.sizeVariant", "small");
		return label;
	}
	
	
	

}

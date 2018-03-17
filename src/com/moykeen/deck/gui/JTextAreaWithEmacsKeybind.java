package com.moykeen.deck.gui;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.InputMap;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.TextAction;



public class JTextAreaWithEmacsKeybind extends JTextArea {
	
	public JTextAreaWithEmacsKeybind() {
		super();
		addEmacsKeyBinding();
	}

	 /**Ctrl-Kをするアクション*/
    protected class CtrlKAction extends TextAction{
	DefaultEditorKit.CutAction cutAction = new DefaultEditorKit.CutAction();

	public CtrlKAction(){
	    super("ctrl-K");
	}

	public void actionPerformed(ActionEvent ae){
	    //いまのカーソル位置から行末までを選択する
	    JTextArea t = (JTextArea)getTextComponent(ae);
	    t.select(t.getCaretPosition(), 10000);//終点は適当な大きな値をいれておけばOK
	    cutAction.actionPerformed(ae);
	}
    }

    /**Macのネイティブアプリケーションでサポートされている(Emacs風の)キーバインドを再現する*/
    protected void addEmacsKeyBinding(){
	InputMap im = this.getInputMap();
	
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK), DefaultEditorKit.deleteNextCharAction);//DEL	
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK), DefaultEditorKit.backwardAction);//戻る
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK), DefaultEditorKit.forwardAction);//進む
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK), DefaultEditorKit.upAction);//上
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK), DefaultEditorKit.downAction);//下
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK), DefaultEditorKit.beginLineAction);//行の先頭
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK), DefaultEditorKit.endLineAction);//行の末尾
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK), DefaultEditorKit.pasteAction);//ヤンク
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK), new CtrlKAction());//Ctrl-K
	
	
    }
	
}

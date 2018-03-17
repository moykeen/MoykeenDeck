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

	 /**Ctrl-K������A�N�V����*/
    protected class CtrlKAction extends TextAction{
	DefaultEditorKit.CutAction cutAction = new DefaultEditorKit.CutAction();

	public CtrlKAction(){
	    super("ctrl-K");
	}

	public void actionPerformed(ActionEvent ae){
	    //���܂̃J�[�\���ʒu����s���܂ł�I������
	    JTextArea t = (JTextArea)getTextComponent(ae);
	    t.select(t.getCaretPosition(), 10000);//�I�_�͓K���ȑ傫�Ȓl������Ă�����OK
	    cutAction.actionPerformed(ae);
	}
    }

    /**Mac�̃l�C�e�B�u�A�v���P�[�V�����ŃT�|�[�g����Ă���(Emacs����)�L�[�o�C���h���Č�����*/
    protected void addEmacsKeyBinding(){
	InputMap im = this.getInputMap();
	
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK), DefaultEditorKit.deleteNextCharAction);//DEL	
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_MASK), DefaultEditorKit.backwardAction);//�߂�
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK), DefaultEditorKit.forwardAction);//�i��
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK), DefaultEditorKit.upAction);//��
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK), DefaultEditorKit.downAction);//��
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK), DefaultEditorKit.beginLineAction);//�s�̐擪
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK), DefaultEditorKit.endLineAction);//�s�̖���
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK), DefaultEditorKit.pasteAction);//�����N
	im.put(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK), new CtrlKAction());//Ctrl-K
	
	
    }
	
}

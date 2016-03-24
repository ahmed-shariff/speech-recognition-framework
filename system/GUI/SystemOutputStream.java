package system.GUI;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

public class SystemOutputStream extends OutputStream {

	private JTextArea textArea;
	public SystemOutputStream(JTextArea textArea){
		this.textArea=textArea;
	}
	@Override
	public void write(int arg0) throws IOException {
		final int arg=arg0;
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
				textArea.append(String.valueOf((char) arg));
				textArea.setCaretPosition(textArea.getDocument().getLength());
			}
		});
	}
}

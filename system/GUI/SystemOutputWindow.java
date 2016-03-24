package system.GUI;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

public class SystemOutputWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textArea = new JTextArea();
	private boolean visibility=false;
	/**
	 * Create the frame.
	 * @param visibility 
	 */
	public SystemOutputWindow(String title,int width, Color background, Color forground, boolean visibility) {
		this.visibility=visibility;
		setResizable(false);
		setTitle(title);
		setBounds(100, 100, 100+width, 456);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		textArea.setEditable(false);
		
		textArea.setBackground(background);
		textArea.setForeground(forground);
		textArea.setBounds(10, 11, width+74, 373);
		JScrollPane scroll=new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(10, 11, width+74, 373);
		contentPane.add(scroll);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						setVisible(false);
					}
				});
			}
		});
		btnClose.setBounds(width-5, 393, 89, 23);
		contentPane.add(btnClose);
		setVisible(visibility);
	}
	
	public JTextArea getTextArea(){
		return textArea;
	}
	
	public boolean getVisibility(){
		return visibility;
	}
	
	public boolean changeVisibility(){
		visibility=(!visibility);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				setVisible(visibility);
			}
		});
		return visibility;
	}
}

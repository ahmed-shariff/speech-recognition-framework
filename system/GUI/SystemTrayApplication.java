package system.GUI;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import system.core.RecognizerEngnePauseInterface;

public class SystemTrayApplication {
	private final SystemTray tray;
	private final PopupMenu popUpMenue;
	private final TrayIcon icon;
	
	
	public SystemTrayApplication(String imagePath,final RecognizerEngnePauseInterface recognizerEngineWithPause,final SystemOutputWindow window1,final SystemOutputWindow window2){
		if(!SystemTray.isSupported()){
			JOptionPane.showMessageDialog(null, "System tray not supported\nApplication exiting", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
		}
		tray=SystemTray.getSystemTray();
		popUpMenue= new PopupMenu("Speech recognizer menue");
		icon=new TrayIcon(createImage(imagePath, "System Tray Icon"));
		
		MenuItem aboutItem=new MenuItem("About");
		final CheckboxMenuItem checkBoxForPause=new CheckboxMenuItem("Pause System");
		final CheckboxMenuItem checkBoxFowWindow1=new CheckboxMenuItem(window1.getTitle());
		final CheckboxMenuItem checkBoxFowWindow2=new CheckboxMenuItem(window2.getTitle());
		MenuItem exitMenuItem=new MenuItem("Exit");
		
		popUpMenue.add(aboutItem);
		popUpMenue.addSeparator();
		popUpMenue.add(checkBoxForPause);
		popUpMenue.addSeparator();
		popUpMenue.add(checkBoxFowWindow1);
		popUpMenue.add(checkBoxFowWindow2);
		popUpMenue.addSeparator();
		popUpMenue.add(exitMenuItem);
		
		icon.setPopupMenu(popUpMenue);
		icon.setImageAutoSize(true);
		
		try {
        	tray.add(icon);
    	} catch (AWTException e) {
    		JOptionPane.showMessageDialog(null, "System tray ncannot be added\nApplication exiting", "Error", JOptionPane.ERROR_MESSAGE);
    		System.exit(-1);
    	}
		
		icon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				icon.displayMessage(null, "This is the speech recognizer\nDesigned by Ahmed Shariff\nHave fun", TrayIcon.MessageType.INFO);
			}
		});
		
		checkBoxForPause.setState(false);
		
		checkBoxFowWindow1.setState(window1.getVisibility());
		checkBoxFowWindow2.setState(window2.getVisibility());
		
		checkBoxForPause.addItemListener(new ItemListener() {
			private boolean pauseState=false;
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				int bool=arg0.getStateChange();
				if (bool==ItemEvent.SELECTED && !pauseState) {
					recognizerEngineWithPause.setPauseRecognitionEngine(true);
					pauseState=true;
				}else if(bool==ItemEvent.DESELECTED && pauseState){
					recognizerEngineWithPause.setPauseRecognitionEngine(false);
					pauseState=false;
				}
			}
		});
		
		checkBoxFowWindow1.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				checkBoxFowWindow1.setState(window1.changeVisibility());
			}
		});
		
		checkBoxFowWindow2.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				checkBoxFowWindow2.setState(window2.changeVisibility());
			}
		});
		
		aboutItem.setEnabled(false);
		
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selection=JOptionPane.showConfirmDialog(null, "If you want to the speech recognizer to quit, click yes", "Exit?", JOptionPane.YES_NO_OPTION);
				if(selection==JOptionPane.YES_OPTION)
					System.exit(-1);
			}
		});
	}
	
	
	 protected static Image createImage(String path, String description) {
	        URL imageURL = SystemTrayApplication.class.getResource(path);
	        
	        if (imageURL == null) {
	            System.err.println("Resource not found: " + path);
	            return null;
	        } else {
	            return (new ImageIcon(imageURL, description)).getImage();
	        }
	    }
}

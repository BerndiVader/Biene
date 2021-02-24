package com.gmail.berndivader.biene.gui;

import java.awt.AWTException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.db.UpdateShopTask;

import javax.swing.JTextArea;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.net.URL;

public 
class 
Main 
extends 
JFrame {

	private static final long serialVersionUID = 1L;
	
	private static boolean exit;
	public JPanel contentPane;
	
	public static Main frame;
	public static Image icon_image;
	
	public Settings settings;
	
	public JTextArea log_area;
	public JScrollPane scrollBar;
	public TrayIcon tray_icon;
	public SystemTray tray;
	
	public PopupMenu popup;
	
	public static void init() {
		icon_image=createImage("/appicon.gif","blubb");
		new Main();
	}
	
	protected static Image createImage(String path, String description) {
		URL url=Main.class.getResource(path);
		if (url==null) return new ImageIcon(new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB)).getImage();
		return (new ImageIcon(url,description)).getImage();
	}	
	
	
	/**
	 * Create the frame.
	 */
	public Main() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		exit=false;
		setType(Type.NORMAL);
		setFont(new Font("Tahoma", Font.PLAIN, 12));
		setTitle("Biene Tirolerbienenladen");
		frame=this;
		setBounds(100, 100, 603, 560);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		this.addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				if(e.getNewState()==1) {
					frame.setVisible(false);
				}
			}
		});
		
		tray_icon=new TrayIcon(icon_image);
		frame.setIconImage(icon_image);
		tray=SystemTray.getSystemTray();
		
		try {
			tray.add(tray_icon);
		} catch (AWTException e) {
			Logger.$(e,false,true);
		}
		
		tray_icon.setPopupMenu(createPopup());
		
		log_area = new JTextArea();
		log_area.setFont(new Font("Monospaced", Font.PLAIN, 11));
		log_area.setEditable(false);
		log_area.setLineWrap(true);
		log_area.add(popup=createPopup());
		contentPane.add(log_area);
	
		scrollBar = new JScrollPane(log_area);
		scrollBar.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollBar);
		
		tray_icon.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				int button=e.getButton();
				int count=e.getClickCount();
				if(button==1&&count>1) {
					frame.setVisible(!frame.isVisible());
					frame.setExtendedState(Frame.NORMAL);
				}
			}
		});
		
		log_area.addMouseListener(new MouseListener() {
        	 
            @Override
            public void mousePressed(MouseEvent e) {
                showPopup(e);
            }
 
            @Override
            public void mouseReleased(MouseEvent e) {
                showPopup(e);
            }
 
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
			
            private void showPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                	popup.show(e.getComponent(),e.getX(),e.getY());
                }
            }
			
        });		
		
		
		this.setVisible(false);
	}
	
	private PopupMenu createPopup() {
		PopupMenu popup=new PopupMenu();
		MenuItem popup_update=new MenuItem("Shop aktualisieren");
		MenuItem popup_show=new MenuItem("Toggle Window");
		MenuItem popup_about=new MenuItem("Info");
		MenuItem popup_settings=new MenuItem("Einstellungen");
		MenuItem popup_exit=new MenuItem("Beenden");
		
		popup.add(popup_update);
		popup.add(popup_show);
		popup.add(popup_about);
		popup.add(popup_settings);
		popup.add(popup_exit);
		
		popup_update.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new UpdateShopTask(Config.data.getWinlineQuery());
			}
		});
		
		popup_about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Utils.showInfo();
			}
		});
		
		popup_show.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(!frame.isVisible());
			}
		});
		
		popup_settings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(settings!=null) {
					settings.setExtendedState(Frame.NORMAL);
					settings.setVisible(true);
				} else {
					settings=new Settings();
					settings.setVisible(true);
					settings.setExtendedState(Frame.NORMAL);
				}
			}
		});
		
		popup_exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!exit) {
					exit=true;
					if(JOptionPane.showConfirmDialog(frame,"Biene wirklich beenden?","Frage",JOptionPane.YES_NO_OPTION)==0) {
						System.exit(0);
					} else {
						exit=false;
					}
				}
			}
		});
		return popup;
	}
}

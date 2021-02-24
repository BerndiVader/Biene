package com.gmail.berndivader.biene.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.db.UpdatePicturesTask;

import java.awt.TextField;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.awt.Label;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Button;
import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import java.awt.Checkbox;
import java.awt.List;
import java.awt.MenuItem;
import java.awt.PopupMenu;

public class Settings extends JFrame {

	Settings settings;
	private static final long serialVersionUID = 10000L;
	private JPanel contentPane;
	
	private TextField connection_string_field;
	private	TextArea sql_wl_zu_xtc;
	private Label label_2;
	private TextField benutzer_field;
	private Label label_3;
	private TextField password_field;
	private Button speichern;
	private Label label_4;
	private TextField mesoyear_field;
	private Label label_5;
	private TextField http_string_field;
	private JPanel panel;
	private JPanel panel_1;
	private Button cancel;
	private Button restore;
	private JTabbedPane tabbedPane;
	private TextArea sql_find_changed;
	private TextArea sql_find_inserts;
	private TextArea sql_find_deletes;
	private TextArea katalog;
	private TextArea sql_update_local;
	private Checkbox autoupdate;
	private TextField upd_inverval_value;
	private Label Inverval;
	private List bilder;
	private PopupMenu bilderPopup;


	public Settings() {
		this.settings=this;
		setAlwaysOnTop(true);
		setBounds(100, 100, 628, 730);
		setIconImage(Main.icon_image);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{583, 0};
		gbl_contentPane.rowHeights = new int[] {0, 0, 22, 22, 22, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0};
		contentPane.setLayout(gbl_contentPane);
		
		label_5 = new Label("Httpstring:");
		GridBagConstraints gbc_label_5 = new GridBagConstraints();
		gbc_label_5.anchor = GridBagConstraints.NORTHWEST;
		gbc_label_5.insets = new Insets(0, 0, 5, 0);
		gbc_label_5.gridx = 0;
		gbc_label_5.gridy = 0;
		contentPane.add(label_5, gbc_label_5);
		
		http_string_field = new TextField();
		http_string_field.setFont(new Font("Monospaced", Font.PLAIN, 11));
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.anchor = GridBagConstraints.NORTH;
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.gridx = 0;
		gbc_textField.gridy = 1;
		contentPane.add(http_string_field, gbc_textField);
		
		Label label = new Label("Connectionstring:");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.NORTHWEST;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 0;
		gbc_label.gridy = 2;
		contentPane.add(label, gbc_label);
		
		connection_string_field=new TextField();
		connection_string_field.setFont(new Font("Monospaced", Font.PLAIN, 11));
		connection_string_field.setText(Config.data.getConnection_string());
		GridBagConstraints gbc_connection_string_field = new GridBagConstraints();
		gbc_connection_string_field.anchor = GridBagConstraints.NORTH;
		gbc_connection_string_field.fill = GridBagConstraints.HORIZONTAL;
		gbc_connection_string_field.insets = new Insets(0, 0, 5, 0);
		gbc_connection_string_field.gridx = 0;
		gbc_connection_string_field.gridy = 3;
		contentPane.add(connection_string_field, gbc_connection_string_field);
		
		Label label_1 = new Label("SQL-Queries:");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_label_1.anchor = GridBagConstraints.NORTH;
		gbc_label_1.insets = new Insets(0, 0, 5, 0);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 4;
		contentPane.add(label_1, gbc_label_1);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.gridheight = 2;
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 5;
		tabbedPane.getInputMap().put(KeyStroke.getKeyStroke("UP"),"none");
		tabbedPane.getInputMap().put(KeyStroke.getKeyStroke("DOWN"),"none");
		tabbedPane.getInputMap().put(KeyStroke.getKeyStroke("LEFT"),"none");
		tabbedPane.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"),"none");
		contentPane.add(tabbedPane, gbc_tabbedPane);
		
		sql_wl_zu_xtc = new TextArea();
		tabbedPane.addTab("WL zu XTC", null, sql_wl_zu_xtc, null);
		sql_wl_zu_xtc.setFont(new Font("Monospaced", Font.PLAIN, 11));
		sql_wl_zu_xtc.setText(Config.data.getWinlineQuery());
		
		sql_find_changed = new TextArea();
		sql_find_changed.setFont(new Font("Monospaced", Font.PLAIN, 11));
		tabbedPane.addTab("Updates", null, sql_find_changed, null);
		sql_find_changed.setText(Config.data.getUpdatesQuery());
		
		sql_find_inserts = new TextArea();
		sql_find_inserts.setFont(new Font("Monospaced", Font.PLAIN, 11));
		tabbedPane.addTab("Inserts", null, sql_find_inserts, null);
		sql_find_inserts.setText(Config.data.getInsertsQuery());
		
		sql_find_deletes = new TextArea();
		sql_find_deletes.setFont(new Font("Monospaced", Font.PLAIN, 11));
		tabbedPane.addTab("Deletes", null, sql_find_deletes, null);
		sql_find_deletes.setText(Config.data.getDeletesQuery());
		
		sql_update_local = new TextArea();
		sql_update_local.setText((String) null);
		sql_update_local.setFont(new Font("Monospaced", Font.PLAIN, 11));
		tabbedPane.addTab("Verify Update", null, sql_update_local, null);
		sql_find_deletes.setText(Config.data.getVerifyQuery());
		
		katalog = new TextArea();
		katalog.setFont(new Font("Monospaced", Font.PLAIN, 11));
		tabbedPane.addTab("Katalog", null, katalog, null);
		katalog.setText(Config.data.getKatalog());
		
		bilder = new List();
		bilder.setFont(new Font("Monospaced", Font.PLAIN, 11));
		tabbedPane.addTab("Bilder", null, bilder, null);
		
		bilder.setDropTarget(new DropTarget() {
			private static final long serialVersionUID = 1L;
			public synchronized void drop(DropTargetDropEvent e) {
				try {
					e.acceptDrop(DnDConstants.ACTION_COPY);
					@SuppressWarnings("unchecked")
					java.util.List<File>files=(java.util.List<File>)e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					if(files!=null) {
						Utils.copy_pictures(files);
					}
				} catch (Exception ex) {
					Logger.$(ex,false,true);
				}
			}
		});
		
		bilder.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				update_pictures();
			}
		});
		
		bilderPopup=new PopupMenu();
		bilderPopup.add(new MenuItem("Generiere Bilder am Server"));
		bilderPopup.add(new MenuItem("Entferne ausgewähltes Bild"));
		bilderPopup.add(new MenuItem("Verzeichnis aktualisieren"));
		
		bilderPopup.getItem(0).setActionCommand("update");
		bilderPopup.getItem(1).setActionCommand("delete");
		bilderPopup.getItem(2).setActionCommand("refresh");
		
		
		bilderPopup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch(e.getActionCommand()) {
				case "update":
					new UpdatePicturesTask("");
					break;
				case "delete":
					Utils.delete_selected_pictures(bilder.getSelectedItems());
					break;
				case "refresh":
					break;
				}
			}
		});
		
		bilder.addMouseListener(new MouseListener() {
			
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
				if(e.getButton()==3) {
					bilderPopup.show(bilder,e.getX(),e.getY());
				}
			}
		});
		
		bilder.add(bilderPopup);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.anchor = GridBagConstraints.SOUTH;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 7;
		contentPane.add(panel, gbc_panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		label_4 = new Label("Gesch\u00E4ftsjahr:");
		panel.add(label_4);
		
		mesoyear_field = new TextField();
		panel.add(mesoyear_field);
		mesoyear_field.setFont(new Font("Monospaced", Font.PLAIN, 11));
		
		label_2 = new Label("Benutzername:");
		label_2.setAlignment(Label.RIGHT);
		panel.add(label_2);
		
		benutzer_field = new TextField();
		panel.add(benutzer_field);
		benutzer_field.setFont(new Font("Monospaced", Font.PLAIN, 11));
		
		label_3 = new Label("Passwort:");
		label_3.setAlignment(Label.RIGHT);
		panel.add(label_3);
		
		password_field = new TextField();
		panel.add(password_field);
		password_field.setFont(new Font("Monospaced", Font.PLAIN, 11));
		
		panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.anchor = GridBagConstraints.SOUTHEAST;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 8;
		contentPane.add(panel_1, gbc_panel_1);
		
		autoupdate = new Checkbox("Auto Update?");
		panel_1.add(autoupdate);
		
		Inverval = new Label("Intervall in Minuten");
		Inverval.setAlignment(Label.RIGHT);
		panel_1.add(Inverval);
		
		upd_inverval_value = new TextField();
		upd_inverval_value.setText("60");
		panel_1.add(upd_inverval_value);
		
		speichern = new Button("Speichern");
		panel_1.add(speichern);
		
		restore = new Button("Zur\u00FCcksetzen");
		restore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(JOptionPane.showConfirmDialog(settings,"Konfiguration neu laden?","Zurücksetzen",JOptionPane.OK_CANCEL_OPTION)) {
				case 0:
					update_fields();
					break;
				}
			}
		});
		panel_1.add(restore);
		
		cancel = new Button("Abbrechen");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				update_fields();
				settings.setVisible(false);
			}
		});
		panel_1.add(cancel);
		
		speichern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("Speichern")) {
					switch(JOptionPane.showConfirmDialog(settings,"Konfiguration übernehmen und speichern?")) {
						case 0:
							update_config();
							Config.save_config();
							break;
						case 1:
							break;
						case 2:
							return;
					}
					update_fields();
					settings.setVisible(false);
				}
			}
		});
		
		this.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				update_fields();
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				update_fields();
				settings.setVisible(false);
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				update_fields();
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				update_fields();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				update_pictures();
			}
		});
		
	}
	
	private void update_pictures() {
		Utils.update_picture_list();
		this.bilder.removeAll();
		for(int i1=0;i1<Utils.pictures.size();i1++) {
			this.bilder.add(Utils.pictures.get(i1));
		}
	}
	
	private void update_config() {
		Config.data.setHttp_string(http_string_field.getText());
		Config.data.setConnection_string(connection_string_field.getText());
		Config.data.setUsername(benutzer_field.getText());
		Config.data.setPassword(password_field.getText());
		Config.data.setMeso_year(mesoyear_field.getText());
		Config.data.setWinlineQuery(sql_wl_zu_xtc.getText());
		Config.data.setUpdatesQuery(sql_find_changed.getText());
		Config.data.setVerifyQuery(sql_update_local.getText());
		Config.data.setStapelpreiseQuery("");
		Config.data.setDeletesQuery(sql_find_deletes.getText());
		Config.data.setInsertsQuery(sql_find_inserts.getText());
		Config.data.setKatalog(katalog.getText());
		Config.data.setAutoUpdate(autoupdate.getState());
		Config.data.setUpdateInterval(this.upd_inverval_value.getText());
	}
	
	private void update_fields() {
		http_string_field.setText(Config.data.getHttp_string());
		connection_string_field.setText(Config.data.getConnection_string());
		benutzer_field.setText(Config.data.getUsername());
		password_field.setText(Config.data.getPassword());
		mesoyear_field.setText(Config.data.getMeso_year());
		sql_wl_zu_xtc.setText(Config.data.getWinlineQuery());
		sql_find_changed.setText(Config.data.getUpdatesQuery());
		sql_find_inserts.setText(Config.data.getInsertsQuery());
		sql_find_deletes.setText(Config.data.getDeletesQuery());
		sql_update_local.setText(Config.data.getVerifyQuery());
		katalog.setText(Config.data.getKatalog());
		autoupdate.setState(Config.data.getAutoUpdate());
		upd_inverval_value.setText(Integer.toString(Config.data.getUpdateInterval()));
		update_pictures();
	}
}

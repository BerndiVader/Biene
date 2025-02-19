package com.gmail.berndivader.biene.gui;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.Utils;
import com.gmail.berndivader.biene.db.SimpleResultQuery;
import com.gmail.berndivader.biene.db.UpdatePicturesTask;
import com.gmail.berndivader.biene.enums.Tasks;

import java.awt.TextField;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import java.awt.Panel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.UIManager;

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
	private JTextField client_info;
	private JComboBox<String>client_select;
	
	private int client_selected_index=0;


	public Settings() {
		setTitle("Konfiguration");
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
		
		label_5 = new Label("HTTP Connection String:");
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
		
		Label label = new Label("MSSQL Connection String");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.NORTHWEST;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 0;
		gbc_label.gridy = 2;
		contentPane.add(label, gbc_label);
		
		connection_string_field=new TextField();
		connection_string_field.setFont(new Font("Monospaced", Font.PLAIN, 11));
		connection_string_field.setText(Config.data.connection_string());
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
		sql_wl_zu_xtc.setText(Config.data.winline_query());
		
		sql_find_changed = new TextArea();
		sql_find_changed.setFont(new Font("Monospaced", Font.PLAIN, 11));
		tabbedPane.addTab("Updates", null, sql_find_changed, null);
		sql_find_changed.setText(Config.data.updates_query());
		
		sql_find_inserts = new TextArea();
		sql_find_inserts.setFont(new Font("Monospaced", Font.PLAIN, 11));
		tabbedPane.addTab("Inserts", null, sql_find_inserts, null);
		sql_find_inserts.setText(Config.data.inserts_query());
		
		sql_find_deletes = new TextArea();
		sql_find_deletes.setFont(new Font("Monospaced", Font.PLAIN, 11));
		tabbedPane.addTab("Deletes", null, sql_find_deletes, null);
		sql_find_deletes.setText(Config.data.deletes_query());
		
		sql_update_local = new TextArea();
		sql_update_local.setText((String) null);
		sql_update_local.setFont(new Font("Monospaced", Font.PLAIN, 11));
		tabbedPane.addTab("Verify Update", null, sql_update_local, null);
		sql_find_deletes.setText(Config.data.verify_query());
		
		katalog = new TextArea();
		katalog.setFont(new Font("Monospaced", Font.PLAIN, 11));
		tabbedPane.addTab("Katalog", null, katalog, null);
		katalog.setText(Config.data.katalog());
		
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
						Utils.copyPictures(files);
					}
				} catch (Exception ex) {
					Logger.$(ex,false,true);
				}
			}
		});
		
		bilder.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				update_pictures();
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
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
					new UpdatePicturesTask();
					break;
				case "delete":
					Utils.deleteSelectedPictures(bilder.getSelectedItems());
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
		
		
		Panel misc = new Panel();
		misc.setBackground(new Color(238, 238, 238));
		tabbedPane.addTab("Misc", null, misc, null);
		
		client_select = new JComboBox<String>();
		client_select.setFont(UIManager.getFont("Button.font"));
				
		JLabel client_label = new JLabel("Mandant:");
		client_label.setHorizontalAlignment(SwingConstants.RIGHT);
		
		client_info = new JTextField();
		client_info.setEditable(false);
		client_info.setColumns(10);
		GroupLayout gl_misc = new GroupLayout(misc);
		gl_misc.setHorizontalGroup(
			gl_misc.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_misc.createSequentialGroup()
					.addGap(18)
					.addComponent(client_label, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(client_select, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(client_info, GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_misc.setVerticalGroup(
			gl_misc.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_misc.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_misc.createParallelGroup(Alignment.BASELINE)
						.addComponent(client_select, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(client_info, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
						.addComponent(client_label))
					.addContainerGap(438, Short.MAX_VALUE))
		);
		misc.setLayout(gl_misc);
		
		misc.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				update_clients();
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
			}
			
		});
		
		client_select.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED) {
					client_selected_index=client_select.getSelectedIndex();
				}
			}
			
		});
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.anchor = GridBagConstraints.SOUTH;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 7;
		contentPane.add(panel, gbc_panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		label_4 = new Label("Geschäftsjahr:");
		label_4.setAlignment(Label.RIGHT);
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
		panel_1.setBackground(new Color(238, 238, 238));
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
		
		restore = new Button("Zurücksetzen");
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
							Config.saveConfig();
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
				tabbedPane.setSelectedIndex(0);
				update_fields();
			}
		});
		
	}
	
	private void update_clients() {
		String query="SELECT c000 AS name,c001 AS client,c003 AS city,c004 AS street,c006 AS plz,mesocomp,mesoyear,mesoprim FROM dbo.t001;";
		int index=client_selected_index;
		
		new SimpleResultQuery(query,Tasks.VARIOUS,10l) {
			
			@Override
			public void failed(ResultSet result) {
				Logger.$("Mandanten konnten nicht gelesen werden.");
			}
			
			@Override
			public void completed(ResultSet result) {
				client_select.removeAllItems();
				try {
					while(result.next()) {
						client_select.addItem(result.getString("mesocomp"));
					}
				} catch (SQLException e) {
					Logger.$(e);
				}
				client_selected_index=index;
				client_select.setSelectedIndex(client_selected_index);
			}
			
		};
		
	}
	
	private void update_pictures() {
		Utils.updatePicturesList();
		this.bilder.removeAll();
		for(int i1=0;i1<Utils.pictures.size();i1++) {
			this.bilder.add(Utils.pictures.get(i1));
		}
	}
	
	private void update_config() {
		Config.data.http_string(http_string_field.getText());
		Config.data.connection_string(connection_string_field.getText());
		Config.data.username(benutzer_field.getText());
		Config.data.password(password_field.getText());
		Config.data.meso_year(mesoyear_field.getText());
		Config.data.winline_query(sql_wl_zu_xtc.getText());
		Config.data.updates_query(sql_find_changed.getText());
		Config.data.verify_query(sql_update_local.getText());
		Config.data.stapelpreise_query("");
		Config.data.deletes_query(sql_find_deletes.getText());
		Config.data.inserts_query(sql_find_inserts.getText());
		Config.data.katalogs(katalog.getText());
		Config.data.auto_update(autoupdate.getState());
		Config.data.update_interval(this.upd_inverval_value.getText());
	}
	
	private void update_fields() {
		http_string_field.setText(Config.data.http_string());
		connection_string_field.setText(Config.data.connection_string());
		benutzer_field.setText(Config.data.username());
		password_field.setText(Config.data.password());
		mesoyear_field.setText(Config.data.meso_year());
		sql_wl_zu_xtc.setText(Config.data.winline_query());
		sql_find_changed.setText(Config.data.updates_query());
		sql_find_inserts.setText(Config.data.inserts_query());
		sql_find_deletes.setText(Config.data.deletes_query());
		sql_update_local.setText(Config.data.verify_query());
		katalog.setText(Config.data.katalog());
		autoupdate.setState(Config.data.auto_update());
		upd_inverval_value.setText(Integer.toString(Config.data.update_interval()));
		update_pictures();
	}
}

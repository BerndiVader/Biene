package com.gmail.berndivader.biene;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.db.SteuercodeQuery;
import com.gmail.berndivader.biene.db.ValidatePictureTask;
import com.gmail.berndivader.biene.enums.Action;
import com.gmail.berndivader.biene.enums.EventEnum;
import com.gmail.berndivader.biene.http.get.GetInfo;
import com.gmail.berndivader.biene.http.post.PostSimple;
import com.gmail.berndivader.biene.rtf2html.RtfHtml;
import com.gmail.berndivader.biene.rtf2html.RtfReader;

public
class
Utils
{
    static File lock_file;
    static FileChannel lock_fileChannel;
    static FileLock lock;
    static boolean running=false;
    public static String key;
    static Calendar calendar;
    static SimpleDateFormat date_format;
    static Batcher batcher;
	static DecimalFormat format;
	public static List<String>pictures;
	
	public static File working_dir;
    
    static {
    	calendar=Calendar.getInstance();
    	date_format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	key="01234567";
    	batcher=Biene.batcher;
    	format=new DecimalFormat("0.0000",DecimalFormatSymbols.getInstance(Locale.US));    	
        try {
            URI uri=Biene.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            working_dir=new File(uri.getPath().replace(new File(uri).getName(),""));
        } catch (URISyntaxException ex) {
        	Logger.$(ex);
        }
    	update_picture_list();
    	
    }
    
    public static void init() {
    	File path=new File(Utils.working_dir+"/Bilder");
    	if(!path.mkdirs()&&!path.exists()) {
    		Logger.$("Bilder Verzeichnis existiert nicht und konnte nicht erstellt werden.",false,true);
    	}
    }
    
    @SuppressWarnings("resource")
	public static boolean checkForInstance() throws IOException {
		lock_file=new File(Utils.working_dir+"/biene.lock");
        if (!lock_file.exists()) {
        	lock_file.createNewFile();
        } else {
        	lock_file.delete();
        }

        lock_fileChannel=new RandomAccessFile(lock_file,"rw").getChannel();
        lock=lock_fileChannel.tryLock();

        ShutdownHook shutdownHook=new ShutdownHook();
        Runtime.getRuntime().addShutdownHook(shutdownHook);
         
        if (lock==null) {
        	lock_fileChannel.close();
            return true;
        }
        return running;
    }

    public static void unlockFile() {
        try {
        	if (lock!=null) lock.release();
        	lock_fileChannel.close();
        	lock_file.delete();
        	running=false;
        } catch (IOException e) {
			Logger.$(e);
        }
    }

    private static class ShutdownHook extends Thread {
        public void run() {
            Logger.$("Biene wird beendet.",false);
            unlockFile();
            if(batcher!=null) {
            	if(batcher!=null) {
            		batcher.interrupt();
            	}
            }
        }
    }	
    
    public static String encrypt(String strClearText) {
    	String strData="";
    	try {
    		SecretKeySpec skeyspec=new SecretKeySpec(key.getBytes(),"Blowfish");
    		Cipher cipher=Cipher.getInstance("Blowfish");
    		cipher.init(Cipher.ENCRYPT_MODE,skeyspec);
    		byte[]encrypted=cipher.doFinal(strClearText.getBytes());
    		strData=new String(Base64.getEncoder().encode(encrypted));
    	} catch (Exception e) {
    		Logger.$(e,false,true);
    	}
    	return strData;
    }
    
    public static String decrypt(String strEncrypted) {
    	String strData="";
    	try {
    		SecretKeySpec skeyspec=new SecretKeySpec(key.getBytes(),"Blowfish");
    		Cipher cipher=Cipher.getInstance("Blowfish");
    		cipher.init(Cipher.DECRYPT_MODE,skeyspec);
    		byte[]decrypted=cipher.doFinal(Base64.getDecoder().decode(strEncrypted));
    		strData=new String(decrypted);
    	} catch (Exception e) {
    		Logger.$(e,false,true);
    	}
    	return strData;
    }
    
    public static String loadStringFromStream(InputStream is) {
		Scanner s=new Scanner(is);
		s.useDelimiter("\\A");
		String parse=s.hasNext()?s.next():"";
		s.close();
		try {
			is.close();
		} catch (IOException e) {
    		Logger.$(e,false,true);
		}
		return parse;
    }
    
    public static Document loadXMLFromStream(InputStream is) {
    	try {
        	DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
        	factory.setNamespaceAware(true);
			DocumentBuilder builder=factory.newDocumentBuilder();
			Document xml=builder.parse(is);
			is.close();
			return xml;
		} catch (ParserConfigurationException | SAXException | IOException e) {
    		Logger.$(e,false,true);
		}
    	return null;
    }
    
	public static void writeLog(String log) {
		MultipartEntityBuilder builder=MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("user",new StringBody(Config.data.getShopUser(),ContentType.MULTIPART_FORM_DATA));
		builder.addPart("password",new StringBody(Config.data.getShopPassword(),ContentType.MULTIPART_FORM_DATA));
		builder.addPart("action",new StringBody("log",ContentType.MULTIPART_FORM_DATA));
		builder.addPart("message",new StringBody("("+date_format.format(calendar.getTime())+") "+log,ContentType.MULTIPART_FORM_DATA));
		new PostSimple(Config.data.getHttp_string(),builder.build());
	}
	
	public static void showInfo() {
		Logger.$("SQL-Server: "+Config.data.getConnection_string(),false,true);
		Logger.$("User: "+Config.data.getUsername(),false,true);
		new GetInfo(Config.data.getHttp_string(),EventEnum.HTTP_GET_VERSION);
	}
	
	public static String make_csv_line(Action action_enum,ResultSet result,RtfReader rtf_reader,RtfHtml rtf_html) {
		
		String tmp;
		String delimiter="|";
		String action=action_enum.value();
		String line="XTSOL"+delimiter+action+delimiter;

		try {
			//p_model
			if((tmp=result.getString("c002"))==null) tmp="";
			line+=result.getString("c002")+delimiter;
			//p_stock
			line+=Integer.toString(result.getInt("c008")-result.getInt("c009"))+delimiter;
			//p_sorting
			line+=delimiter;
			//p_shipping
			line+=(action_enum==Action.INSERT?"1":"")+delimiter;
			//p_tpl
			line+=delimiter;
			//p_manufacturer
			line+=delimiter;
			//p_fsk18
			line+=(result.getInt("Expr1")==40?"1":"0")+delimiter;
			//p_priceNoTax
			line+=format.format(result.getFloat("c007"))+delimiter;
	        //p_priceNoTax 1-6
	        line+="||||||";
	        //p_groupAcc 0-6
	        line+="1|1|1|1|1|1|1|";
	        //p_tax
	        int tax=result.getInt("c030");
	        if(tax==1) {
	        	tax=2;
	        } else if(tax==2) {
	        	tax=1;
	        } else {
	        	SteuercodeQuery query=new SteuercodeQuery("select c009 from t010 where c010 = "+tax);
	        	try {
					query.latch.await();
					tax=query.code;
					if(tax==10) {
						tax=3;
					} else if(tax==15) {
						tax=4;
					}
				} catch (InterruptedException e) {
					Logger.$(e,false,true);
					tax=1;
				}
	        }
	        
	        line+=(Integer.toString(tax))+delimiter;
	        //p_status
	        line+=(action_enum==Action.INSERT?"1":"")+delimiter;
	        //p_weight
	        line+=format.format(result.getFloat("c063"))+delimiter;
	        //p_ean
			if((tmp=result.getString("c075"))==null) tmp="";
	        line+=tmp+delimiter;
	        //p_disc
	        line+="0.00"+delimiter;
	        //p_opttpl
	        line+=delimiter;
	        //p_vpe
	        line+="0"+delimiter;
	        //p_vpe_value
	        line+="0.0000"+delimiter;
	        //p_vpe_status
	        line+=delimiter;
	        //p_image.1
	        line+=delimiter;
	        //p_image.2
	        line+=delimiter;
	        String image_name=result.getString("c076");
	        if(image_name!=null&&image_name.length()>0) {
	        	if(image_name.toLowerCase().contains(".jpg")) {
	        		image_name=image_name.substring(0,image_name.length()-4)+".jpg";
	        	}
	        	line+=image_name+delimiter;
	        } else {
	        	line+="noimage.gif"+delimiter;
	        }
	        //englische beschreibungen
	        line+="|||||||"+delimiter;
	        //p_name.de
			if((tmp=result.getString("c003"))==null) tmp="";
	        line+=tmp+delimiter;
	        //p_desc.de
	        try {
	        	String temp=result.getString("c080");
	        	if(temp==null) temp="";
				if (rtf_reader.isValid(temp)) {
					rtf_reader.parse(temp);
					line+=rtf_html.format(rtf_reader.root,false);
				} else {
					line+=temp;
				}
			} catch (Exception e) {
	    		Logger.$(e,false,true);
			}
	        line+=delimiter;
	        //p_shortdesc.de
			if((tmp=result.getString("c073"))==null) tmp="";
	        line+=tmp+delimiter;
	        //p_keywords.de
	        line+=delimiter;
	        //p_meta_title.de
	        line+=delimiter;
	        //p_meta_desc.de
	        line+=delimiter;
	        //p_meta_key.de
	        line+=delimiter;
	        //p_url.de
	        line+=delimiter;
	        //p_cat.0 = Hauptkategorie
	        int id=result.getInt("Expr1");
	        SimpleEntry<String,String>entry=Config.data.getKatalogs(id);
	        if(entry!=null) {
		        line+=entry.getValue()+delimiter;
		        //p_cat.1 = Unterkategorie
		        line+=entry.getKey();
	        } else {
		        line+="1TEMP"+delimiter;
		        //p_cat.1 = Unterkategorie
		        line+="";
	        }
		} catch (SQLException ex) {
    		Logger.$(ex.getMessage(),false,true);
    		Logger.$(ex);
		}
		return line+"\n";
	}
	
	public static File create_csv_file(String csv_string) {
		String file_name=UUID.randomUUID().toString()+".csv";
		File file=new File(file_name);
		if(!file.exists()) {
			try {
				file.createNewFile();
				write_csv_file(file,csv_string);
			} catch (IOException e) {
				Logger.$(e,false,true);
			}
		}
		return file;
	}
	
	private static void write_csv_file(File file,String csv_string) throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(csv_string);
		writer.close();
	}
	
	public static void delete_csv_file(String file_name) {
		File file=new File(file_name);
		if(file.exists()) file.delete();
	}
	
	public static void copy_pictures(List<File>files) {
		int size=files.size();
		for(int i1=0;i1<size;i1++) {
			File file=files.get(i1);
			try {
				if (file.getName().toLowerCase().endsWith(".jpg")) {
					String name=file.getName().substring(0,file.getName().length()-4)+".jpg";
					Files.copy(file.toPath(),new File(Utils.working_dir+"/Bilder/"+name).toPath(),StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (IOException e) {
				Logger.$(e,false,false);
			}
		}
	}
	
	public static void update_picture_list() {
		pictures=new ArrayList<String>();
		File[] files=null;
		files = get_pictures();
		int size=files.length;
		for(int i1=0;i1<size;i1++) {
			pictures.add(files[i1].getName());
		}
		Collections.sort(pictures);
	}
	
	public static void validate_pictures() {
		File[]files=get_pictures();
		int size=files.length;
		for(int i1=0;i1<size;i1++) {
			File file=files[i1];
			String file_name=file.getName();
			if(file_name.toUpperCase().contains(".JPG")) {
				String name=file_name.substring(0,file_name.length()-4);
				String query="select c076 from dbo.biene_temp where c076='"+name+"'";
				new ValidatePictureTask(query,EventEnum.DB_VALIDATE_PICTURE,null);
			} else {
				file.delete();
			}
		}
	}
	
	public static File[]get_pictures() {
		File folder=new File(Utils.working_dir+"/Bilder");
		folder.mkdir();
		return folder.listFiles();
	}
	
	public static String getStringFromResponse(HttpResponse response) {
		
		InputStream stream;
		try {
			stream=response.getEntity().getContent();
			try(Scanner s=new Scanner(stream)) {
				s.useDelimiter("\\A");
				String result=s.hasNext()?s.next():"";
				return result;
			}
		} catch (UnsupportedOperationException | IOException e) {
			Logger.$(e);
		}
		return null;
	}
	
	public static Document getXMLDocument(HttpResponse response) {
		try {
			InputStream stream=response.getEntity().getContent();
			Document xml=Utils.loadXMLFromStream(stream);
			stream.close();
			return xml;
		} catch (UnsupportedOperationException | IOException e) {
			Logger.$(e,false,true);
		}
		return null;
	}
	
	public static void printOut(String parent,NodeList nodes) {
		int size=nodes.getLength();
		for(int i1=0;i1<size;i1++) {
			Node node=nodes.item(i1);
			if(node.hasChildNodes()) {
				printOut(parent.isEmpty()?node.getNodeName():parent+"."+node.getNodeName(),node.getChildNodes());
			} else if(node.getNodeType()==3) {
				String text=node.getTextContent().trim();
				if(!text.isEmpty()) Logger.$(parent+":"+text,false,false);
			};
		}
	}

	public static void delete_selected_pictures(String[]selected_names) {
		int size=selected_names.length;
		if(size>0) {
			
		}
	}
	
	public static long getCurrentTimeMinutes() {
		return (System.currentTimeMillis()/1000)/60;
	}
    
}
	

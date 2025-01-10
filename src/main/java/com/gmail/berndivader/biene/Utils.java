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
    	updatePicturesList();
    	
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
        	if(Biene.no_gui) {
            	Headless.exit=true;
        	}
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
	
	public static String makeCSVLine(Action action_enum,ResultSet result,RtfReader rtf_reader,RtfHtml rtf_html) {
		
		String tmp;
		String delimiter="|";
		String action=action_enum.value();
		StringBuilder line=new StringBuilder("XTSOL"+delimiter+action+delimiter);

		try {
			//p_model
			line.append(result.getString("c002"));
			line.append(delimiter);
			//p_stock
			line.append(Integer.toString(result.getInt("c008")-result.getInt("c009")));
			line.append(delimiter);
			//p_sorting
			line.append(delimiter);
			//p_shipping
			line.append(action_enum==Action.INSERT?"1":"");
			line.append(delimiter);
			//p_tpl
			line.append(delimiter);
			//p_manufacturer
			line.append(delimiter);
			//p_fsk18
			line.append(result.getInt("Expr1")==40?"1":"0");
			line.append(delimiter);
			//p_priceNoTax
			line.append(format.format(result.getFloat("c007")));
			line.append(delimiter);
	        //p_priceNoTax 1-6
			line.append("||||||");
	        //p_groupAcc 0-6
			line.append("1|1|1|1|1|1|1|");
	        //p_tax
	        int tax=result.getInt("c030");
	        if(tax==1) {
	        	tax=2;
	        } else if(tax==2) {
	        	tax=1;
	        } else {
	        	SteuercodeQuery query=new SteuercodeQuery(tax);
	        	try {
					query.latch.await();
					tax=query.code;
				} catch (InterruptedException e) {
					Logger.$(e,false,true);
					tax=1;
				}
	        }
	        
	        line.append(Integer.toString(tax));
			line.append(delimiter);
	        //p_status
			line.append(action_enum==Action.INSERT?"1":"");
			line.append(delimiter);
	        //p_weight
			line.append(format.format(result.getFloat("c063")));
			line.append(delimiter);
	        //p_ean
			if((tmp=result.getString("c075"))==null) tmp="";
	        line.append(tmp);
			line.append(delimiter);
	        //p_disc
			line.append("0.00");
			line.append(delimiter);
	        //p_opttpl
			line.append(delimiter);
	        //p_vpe
			line.append("0");
			line.append(delimiter);
	        //p_vpe_value
			line.append("0.0000");
			line.append(delimiter);
	        //p_vpe_status
			line.append(delimiter);
	        //p_image.1
			line.append(delimiter);
	        //p_image.2
			line.append(delimiter);
	        String image_name=result.getString("c076");
	        if(image_name!=null&&image_name.length()>0) {
	        	if(image_name.toLowerCase().contains(".jpg")) {
	        		image_name=image_name.substring(0,image_name.length()-4)+".jpg";
	        	}
	        	line.append(image_name);
	        } else {
	        	line.append("noimage.gif");
	        }
			line.append(delimiter);
	        //englische beschreibungen
			line.append("|||||||");
	        //p_name.de
			if((tmp=result.getString("c003"))==null) tmp="";
			line.append(tmp);
			line.append(delimiter);
	        //p_desc.de
	        try {
	        	String temp=result.getString("c080");
	        	if(temp==null) temp="";
				if (rtf_reader.isValid(temp)) {
					rtf_reader.parse(temp);
					line.append(rtf_html.format(rtf_reader.root,false));
				} else {
					line.append(temp);
				}
			} catch (Exception e) {
	    		Logger.$(e,false,true);
			}
			line.append(delimiter);
	        //p_shortdesc.de
			if((tmp=result.getString("c073"))==null) tmp="";
			line.append(tmp);
			line.append(delimiter);
	        //p_keywords.de
			line.append(delimiter);
	        //p_meta_title.de
			line.append(delimiter);
	        //p_meta_desc.de
			line.append(delimiter);
	        //p_meta_key.de
			line.append(delimiter);
	        //p_url.de
			line.append(delimiter);
	        //p_cat.0 = Hauptkategorie
	        int id=result.getInt("Expr1");
	        SimpleEntry<String,String>entry=Config.data.getKatalogs(id);
	        if(entry!=null) {
	        	line.append(entry.getValue());
	        	line.append(delimiter);
		        //p_cat.1 = Unterkategorie
	        	line.append(entry.getKey());
	        } else {
	        	line.append("1TEMP");
	        	line.append(delimiter);
		        line.append("");
	        }
		} catch (SQLException ex) {
    		Logger.$(ex.getMessage(),false,true);
    		Logger.$(ex);
		}
		line.append("\n");
		return line.toString();
	}
	
	public static File create_csv_file(String csv_string) {
		String file_name=UUID.randomUUID().toString()+".csv";
		File file=new File(file_name);
		if(!file.exists()) {
			try {
				file.createNewFile();
				file.deleteOnExit();
				writeCSVFIle(file,csv_string);
			} catch (IOException e) {
				Logger.$(e,false,true);
			}
		}
		return file;
	}
	
	private static void writeCSVFIle(File file,String csv_string) throws IOException {
		
		try(FileWriter writer=new FileWriter(file)) {
			writer.write(csv_string);
		}

	}
	
	public static void deleteCSVFile(String file_name) {
		File file=new File(file_name);
		if(file.exists()) file.delete();
	}
	
	public static void copyPictures(List<File>files) {
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
	
	public static void updatePicturesList() {
		pictures=new ArrayList<String>();
		File[] files=null;
		files = getPictures();
		int size=files.length;
		for(int i1=0;i1<size;i1++) {
			pictures.add(files[i1].getName());
		}
		Collections.sort(pictures);
	}
	
	public static void validatePictures() {
		File[]files=getPictures();
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
	
	public static File[]getPictures() {
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

	public static void deleteSelectedPictures(String[]selected_names) {
		int size=selected_names.length;
		if(size>0) {
			
		}
	}
	
	public static long getCurrentTimeMinutes() {
		return (System.currentTimeMillis()/1000)/60;
	}
    
}
	

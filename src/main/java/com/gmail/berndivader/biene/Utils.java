package com.gmail.berndivader.biene;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.config.ConfigTypeAdapter;
import com.gmail.berndivader.biene.config.Gdata;
import com.gmail.berndivader.biene.db.SteuercodeQuery;
import com.gmail.berndivader.biene.enums.Action;
import com.gmail.berndivader.biene.http.get.GetInfoSync;
import com.gmail.berndivader.biene.http.post.PostSimpleSync;
import com.gmail.berndivader.biene.rtf2html.RtfHtml;
import com.gmail.berndivader.biene.rtf2html.RtfParseException;
import com.gmail.berndivader.biene.rtf2html.RtfReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public
class
Utils
{
    private static File lock_file;
    private static FileChannel lock_fileChannel;
    private static FileLock lock;
    private static boolean running=false;
    private static SimpleDateFormat date_format;
    private static DecimalFormat format;
    private static final String KEY;
    
	public static final Gson GSON;
	public static File working_dir;
    
    static {
    	GSON=new GsonBuilder()
    			.disableHtmlEscaping()
    			.setPrettyPrinting()
    			.registerTypeAdapter(Gdata.class,new ConfigTypeAdapter<>(Gdata.class))
    			.create();
    	
    	date_format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	KEY="01234567";
    	format=new DecimalFormat("0.0000",DecimalFormatSymbols.getInstance(Locale.US));
    	
        try {
            URI uri=Biene.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            working_dir=new File(uri.getPath().replace(new File(uri).getName(),""));
        } catch (URISyntaxException ex) {
        	Logger.$(ex);
        }
        
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
            Helper.close();
            Helper.scheduler.shutdown();
        }
    }
    
    public static String encrypt(String strClearText) {
    	String strData="";
    	try {
    		SecretKeySpec skeyspec=new SecretKeySpec(KEY.getBytes(),"Blowfish");
    		Cipher cipher=Cipher.getInstance("Blowfish");
    		cipher.init(Cipher.ENCRYPT_MODE,skeyspec);
    		byte[]encrypted=cipher.doFinal(strClearText.getBytes());
    		strData=new String(Base64.getEncoder().encode(encrypted));
    	} catch (Exception e) {
    		return strClearText;
    	}
    	return strData;
    }
    
    public static String decrypt(String strEncrypted) {
    	String strData="";
    	try {
    		SecretKeySpec skeyspec=new SecretKeySpec(KEY.getBytes(),"Blowfish");
    		Cipher cipher=Cipher.getInstance("Blowfish");
    		cipher.init(Cipher.DECRYPT_MODE,skeyspec);
    		byte[]decrypted=cipher.doFinal(Base64.getDecoder().decode(strEncrypted));
    		strData=new String(decrypted);
    	} catch (Exception e) {
    		return strEncrypted;
    	}
    	return strData;
    }
    
    public static class XML {
    	
    	public enum CODES {

    		OK(0),
    		FAILED(-1),
    		CONTINUE(1),
    		
    		VERSION(111),
    		
    		PHP_ERROR(-101),
    		PHP_RUNTIME_ERROR(-122),
    		PHP_USER_LOGIN_ERROR(-144),
    		PHP_XAUTH_LOGIN_ERROR(-145),
    		PHP_XAUTH_TOKEN_EXPIRED(-146),
    		PHP_EXCEPTION(-133),
    		
    		JAVA_ERROR(-111),
    		JAVA_OK(100);
    		
    		private final int CODE;

			CODES(int code) {
				CODE=code;
			}
			
			public int asInt() {
				return CODE;
			}
			
			public String asStr() {
				return Integer.toString(CODE);
			}
			
			
			public static boolean contains(int value) {
				return Arrays.stream(CODES.values())
						.anyMatch(code->code.asInt()==value);
			}
			
			public static boolean contains(String value) {
				try {
					return contains(Integer.parseInt(value));
				} catch(NumberFormatException e) {
					return false;
				}
			}
			
			public static CODES from(int value) {
				return Arrays.stream(CODES.values())
						.filter(code->code.asInt()==value)
						.findFirst()
						.orElse(CODES.JAVA_ERROR);
			}
			
			public static CODES from(String value) {
				try {
					return from(Integer.parseInt(value));
				} catch(NumberFormatException e) {
					Logger.$(e);
					return CODES.JAVA_ERROR;
				}
			}
			
			public static CODES from(HashMap<String,String>map) {
				try {
					return from(Integer.parseInt(map.get("CODE")));
				} catch(NumberFormatException e) {
					Logger.$(e);
					return CODES.JAVA_ERROR;
				}
			}
    	}
    	
    	/**
    	 * 1. %s = encoding <br>
    	 * 2. %s = CODE <br>
    	 * 3. %s = ACTION <br>
    	 * 4. %s = MESSAGE <br>
    	 * 5. %s = ERROR_CODE <br>
    	 * 6. %s = ERROR <br>
    	 * 
    	 * Error XML from PHP server.
    	 */
    	
    	public static final String ERR_TEMPLATE=
    			"<?xml version='1.0' encoding='%s'?>\n<STATUS>\n<STATUS_DATA>\n"
    					+ "<CODE>%s</CODE>\n"
    					+ "<ACTION>%s</ACTION>\n"
    					+ "<MESSAGE>%s</MESSAGE>\n"
    					+ "<ERROR_CODE>%s</ERROR_CODE>\n"
    					+ "<ERROR>%s</ERROR>\n"
    			+ "</STATUS_DATA>\n</STATUS>\n";
    	private static final DocumentBuilderFactory FACTORY=DocumentBuilderFactory.newInstance();
    	
    	static {
        	FACTORY.setNamespaceAware(true);
    	}
    	
    	public static boolean isError(HashMap<String,String>map) {
    		return map.containsKey("ERROR");
    	}
 
    	public static void printError(HashMap<String,String>map) {
    		Logger.$(String.format("Error:%s Action:%s Message:%s\nDetails:%s",map.get("CODE"),map.get("ACTION"),map.get("MESSAGE"),map.get("ERROR")),false,false);
    	}
    	
    	private static Document createError(CODES code,String action,String message,String errCode,String errValue) {
    		String charSet=Charset.defaultCharset().name();
    		try(StringReader reader=new StringReader(String.format(ERR_TEMPLATE,charSet,code.asInt(),action,message,errCode,errValue))) {
        		return FACTORY.newDocumentBuilder().parse(new InputSource(reader));
    		} catch (Exception e) {
    			Logger.$(e);
			}
    		return null;
    	}
    	
    	public static Document getXMLDocument(HttpResponse response) {
    		Document xml=null;
    		if(response==null) {
    			Logger.$("Http response is NULL.",false,false);
    			return createError(CODES.JAVA_ERROR,"runtime-error","The HTTP-API answered with VOID.","-999","HttpResponse==NULL");
    		}
    		
    		Charset charSet=Charset.defaultCharset();
    		Header encoding=response.getFirstHeader("Content-Encoding");
    		if(encoding!=null) {
    			try {
        			charSet=Charset.forName(encoding.getValue());
    			} catch (Exception e) {
    				Logger.$(e,false,false);
    				charSet=Charset.defaultCharset();
				}
    		}
    		
    		try(InputStream stream=response.getEntity().getContent()) {
    	        String type=response.getEntity().getContentType().getValue();
    	        if(type!=null&&type.contains("text/xml")) {
        			InputSource source=new InputSource(stream);
        			source.setEncoding(charSet.name());
        			xml=Utils.XML.loadXMLFromStream(source);
    	        } else {
    	        	Logger.$("Content is not of type XML.",false,false);
    	        	xml=createError(CODES.JAVA_ERROR,"runtime-error","No XML content.","-999","Expectet xml but got "+type);
    	        }
    		} catch (Exception e) {
    			Logger.$(e,false,false);
	        	xml=createError(CODES.JAVA_ERROR,"runtime-exception","Failed to read response.","-999",e.getMessage());
    		}
    		return xml;
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
        
        private static Document loadXMLFromStream(InputSource is) {
        	Document xml=null;
        	try {
    			xml=FACTORY.newDocumentBuilder().parse(is);
    		} catch (Exception e) {
        		Logger.$(e,false,true);
        		xml=createError(CODES.JAVA_ERROR,"runtime-exception","Failed to parse received content into xml.","-999",e.getMessage());
    		}
        	return xml;
        }
        
        public static String convertDocumentToString(Document doc) {
        	try {
        		TransformerFactory tf=TransformerFactory.newInstance();
        		Transformer transformer=tf.newTransformer();
        		StringWriter writer=new StringWriter();
        		transformer.transform(new DOMSource(doc),new StreamResult(writer));
        		return writer.getBuffer().toString();
        	} catch(TransformerException e) {
        		Logger.$(e);
        		return null;
        	}
        }
        
        public static HashMap<String,String> map(Document xml) {
        	return mapNodes("",xml.getChildNodes(),new HashMap<String,String>());
        }
        
    	private static HashMap<String,String> mapNodes(String node_name,NodeList nodes,HashMap<String,String>result) {
    		int size=nodes.getLength();
    		for(int i1=0;i1<size;i1++) {
    			Node node=nodes.item(i1);
    			if(node.hasChildNodes()) {
    				mapNodes(node.getNodeName(),node.getChildNodes(),result);
    			} else if(node.getNodeType()==Node.TEXT_NODE) {
    				result.put(node_name,node.getTextContent().trim());
    			} else if(node.getNodeType()==Node.CDATA_SECTION_NODE) {
    				result.put(node_name,node.getNodeValue());
    			}
    		}
    		return result;
    	}
    	
    }
    
	public static String getStringFromResource(String path) {

		String output=null;
		try(InputStream stream=Biene.class.getClassLoader().getResourceAsStream(path)) {
			if(stream!=null) {
				if(stream.available()>0) {
					output=new String(stream.readAllBytes());
				}
			}
		} catch (IOException e) {
			Logger.$(e);
		}
		return output;

	}
	
	public static String getStringFromResponse(HttpResponse response) {
		try {
			return EntityUtils.toString(response.getEntity());
		} catch(UnsupportedOperationException | IOException e) {
			Logger.$(e);
		}
		return null;
	}    
    
	public static void writeLog(String log) {
		MultipartEntityBuilder builder=MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("message",new StringBody("("+date_format.format(Calendar.getInstance().getTime())+") "+log,ContentType.MULTIPART_FORM_DATA));
		builder.addPart("action",new StringBody("log",ContentType.MULTIPART_FORM_DATA));
		
		new PostSimpleSync(Config.data.http_string(),builder.build()) {
			
			@Override
			public void _failed(HttpResponse response) {
			}
			
			@Override
			public void _completed(HttpResponse response) {
				Document xml=Utils.XML.getXMLDocument(response);
				HashMap<String,String>map=Utils.XML.map(xml);
				if(Utils.XML.isError(map)) {
					Logger.$("Error while trying to write to server logfile.",false,false);
					Utils.XML.printError(map);
				}
			}

			@Override
			protected void max_seconds(long max) {
				max_seconds=15l;
			}
		};
		
	}
	
	public static void showInfo() {
		Logger.$("SQL-Server: "+Config.data.connection_string(),false,true);
		new GetInfoSync();
	}
	
	@SuppressWarnings("unchecked")
	public static String makeCSVLine(Action action_enum,ResultSet result,RtfReader rtf_reader,RtfHtml rtf_html,LinkedHashMap<String,Object>catTree) {
		
		String tmp;
		String delimiter="|";
		String action=action_enum.value();
		StringBuilder line=new StringBuilder("XTSOL"+delimiter+action+delimiter);

		try {
			//p_model
			line.append(result.getString("p_model"));
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
			//p_manufacturer (name)
			line.append(delimiter);
			//p_man (manufacturer model #)
			line.append(delimiter);
			//p_fsk18
			line.append(result.getInt("p_web")==40?"1":"0");
			line.append(delimiter);
			//p_priceNoTax
			line.append(format.format(result.getFloat("c007")));
			line.append(delimiter);
	        //p_priceNoTax.1-6
			line.append("||||||");
	        //p_groupAcc.0-6
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
	        		if(query.latch.await(query.max_seconds,TimeUnit.SECONDS)) {
						tax=query.code;
	        		}
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
			//p_image
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
			line.append("||||||||");
	        //p_name.de
			if((tmp=result.getString("c003"))==null) tmp="";
			line.append(tmp);
			line.append(delimiter);
	        //p_desc.de
        	if((tmp=result.getString("c080"))==null) tmp="";
        	line.append(rtf2html(tmp,true));
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
	        //p_cat.0-4
			String c_string=result.getString("p_catalog");
			if(c_string==null||c_string.isEmpty()) c_string="0-0-0-0-0";
			String[]tree=Arrays.stream(c_string.split("-"))
						.filter(entry->!entry.equals("00000"))
						.toArray(String[]::new);
			long catCount=Arrays.stream(Config.data.csv_header().split("\\|"))
					.filter(field->field.startsWith("p_cat"))
					.count();
			if(catTree!=null&&!catTree.isEmpty()) {
				for(int a=0;a<catCount;a++) {
					if(a<tree.length&&catTree.containsKey(tree[a])) {
						catTree=(LinkedHashMap<String,Object>)catTree.get(tree[a]);
						LinkedHashMap<String,String>info=(LinkedHashMap<String,String>)catTree.get("INFO");
						line.append(info.get("NAME"));
					}
					line.append(delimiter);
				}
        	} else {
            	line.append("1TEMP");
            	line.append(delimiter);
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
	
	private static String rtf2html(String desc,boolean new_style) {
		if(new_style) {
	    	RTFEditorKit rtfKit=new RTFEditorKit();
	    	javax.swing.text.Document doc=rtfKit.createDefaultDocument();
	    	try(InputStream stream=new ByteArrayInputStream(desc.getBytes())) {
	    		rtfKit.read(stream,doc,0);
		    	HTMLEditorKit htmlKit=new HTMLEditorKit();
		        try(StringWriter writer=new StringWriter()) {
		            htmlKit.write(writer,doc,0,doc.getLength());
		            desc=writer.toString().replaceAll("\\s+"," ").trim();
				}
				return desc;
			} catch (IOException | BadLocationException e) {
				Logger.$(e);
			}
		} else {
			RtfReader rtf_reader=new RtfReader();
			RtfHtml rtf_html=new RtfHtml();
			if (rtf_reader.isValid(desc)) {
				try {
					rtf_reader.parse(desc);
					return rtf_html.format(rtf_reader.root,false);
				} catch (RtfParseException e) {
					Logger.$(e);
				}
			} else {
				return desc;
			}
		}
		return desc;
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
	
	public static ArrayList<String> updatePicturesList() {
		ArrayList<String>pictures=new ArrayList<String>();
		File[] files=getPictures();
		int size=files.length;
		for(int i1=0;i1<size;i1++) {
			pictures.add(files[i1].getName());
		}
		Collections.sort(pictures);
		return pictures;
	}
	
	private static File[]getPictures() {
		File folder=new File(Utils.working_dir+"/Bilder");
		folder.mkdir();
		return folder.listFiles();
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
	

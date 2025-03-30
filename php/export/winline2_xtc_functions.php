<?php

function SendScriptVersion(string $action)
{
  global $version_nr,$version_datum;
  $schema='<?xml version="1.0" encoding="'.CHARSET.'"?>'."\n".
          '<STATUS>'."\n".
          '<STATUS_DATA>'."\n".
          '<ACTION>'.$action.'</ACTION>'."\n".
          '<CODE>'.(string)Codes::VERSION->value.'</CODE>'."\n".
          '<SCRIPT_VER>'.$version_nr.'</SCRIPT_VER>'."\n".
          '<SCRIPT_DATE>'.$version_datum.'</SCRIPT_DATE>'."\n".
          '</STATUS_DATA>'."\n".
          '</STATUS>'."\n\n";
  SendXMLHeader();
  echo $schema;
}

//--------------------------------------------------------------

function print_xml_status(Codes $c,string $action,string $msg,string $mode="",string $item_name="",string $item_value="")
{
  $schema='<?xml version="1.0" encoding="'.CHARSET.'"?>'."\n".
            '<STATUS>'."\n".
            '<STATUS_DATA>'."\n".
            '<CODE>'.(string)$c->value.'</CODE>'."\n" .
            '<ACTION>'.$action.'</ACTION>'."\n".
            '<MESSAGE>'.$msg.'</MESSAGE>'."\n";

  if(!empty($mode))
  {
    $schema.='<MODE>'.$mode.'</MODE>'."\n";
  }
  if(!empty($item_name))
  {
    $schema.='<'.$item_name.'>'.$item_value.'</'.$item_name.'>'."\n";
  }

  $schema.='</STATUS_DATA>'."\n".'</STATUS>'."\n\n";

  SendXMLHeader();
  echo $schema;
  return;
}

//--------------------------------------------------------------

function column_exists(string $table,string $column)
{
  $Table=xtc_db_query("show columns from $table LIKE '{$column}'");
  return xtc_db_fetch_row($Table)!==false;
}

//--------------------------------------------------------------

function SendXMLHeader()
{
  header("Last-Modified: ".gmdate("D, d M Y H:i:s")." GMT");
  header("Cache-Control: no-cache, must-revalidate");
  header("Pragma: no-cache");
  header("Content-type: text/xml");
}
//--------------------------------------------------------------

function CsvFileUpload(string $action)
{
  global $_GET;

  if(!isset($_GET['file_name'])||!isset($_GET['file_size']))
  {
    throw new Wl2Exception("MISSING FILENAME OR FILESIZE",Codes::RUNTIME_ERROR);
  }

  $file_name=$_GET['file_name'];
  $file_size=$_GET['file_size'];

  $putdata=fopen("php://input","r");
  if($putdata===false)
  {
    throw new Wl2Exception("UNABLE TO OPEN CSV INPUT STREAM",Codes::RUNTIME_ERROR);
  }

  $fp=fopen(DIR_FS_DOCUMENT_ROOT.'import/'.$file_name,"w");
  if($fp===false)
  {
    fclose($putdata);
    throw new Wl2Exception("UNABLE TO CREATE CSV OUTPUT FILE",Codes::RUNTIME_ERROR);
  }

  while($data=fread($putdata,1024))
  {
    if(fwrite($fp,$data)===false)
    {
      fclose($fp);
      fclose($putdata);
      throw new Wl2Exception("FAILED TO WRITE CSV OUTPUT FILE",Codes::RUNTIME_ERROR);
    }
  }

  fclose($fp);
  fclose($putdata);
  $fp_size=filesize(DIR_FS_DOCUMENT_ROOT.'import/'.$file_name);

  if($fp_size==$file_size)
  {
    $code=Codes::OK;
    $message="OK {$file_size}:{$fp_size}";
  }
  else
  {
    $code=Codes::FAILED;
    $message="FAILED {$file_size}:{$fp_size}";
  }

  print_xml_status($code,$action,$message,'','OUTCOME',$file_name);
}

//--------------------------------------------------------------

function ImageUpload(string $action)
{
  global $_GET;

  if(!isset($_GET['file_name'])||!isset($_GET['file_size']))
  {
    throw new Wl2Exception("MISSING FILENAME OR FILESIZE",Codes::RUNTIME_ERROR);
  }

  $file_name=$_GET['file_name'];
  $file_size=$_GET['file_size'];

  $putdata=fopen("php://input","r");
  if($putdata===false)
  {
    throw new Wl2Exception("UNABLE TO OPEN IMAGE INPUT STREAM",Codes::RUNTIME_ERROR);
  }

  $fp=fopen(DIR_FS_DOCUMENT_ROOT.'images/product_images/uploaded_images/'.$file_name,"w");
  if($fp===false)
  {
    fclose($putdata);
    throw new Wl2Exception("UNABLE TO CREATE IMAGE OUTPUT FILE",Codes::RUNTIME_ERROR);
  }

  while($data=fread($putdata,1024))
  {
    if(fwrite($fp,$data)===false)
    {
      fclose($fp);
      fclose($putdata);
      throw new Wl2Exception("FAILED TO WRITE IMAGE OUTPUT FILE",Codes::RUNTIME_ERROR);
    }
  }

  fclose($fp);
  fclose($putdata);
  $fp_size=filesize(DIR_FS_DOCUMENT_ROOT.'images/product_images/uploaded_images/'.$file_name);

  if($fp_size==$file_size)
  {
    $code=Codes::OK;
    $message="OK {$file_size}:{$fp_size}";
  }
  else
  {
    $code=Codes::FAILED;
    $message="FAILED {$file_size}:{$fp_size}";
  }

  print_xml_status($code,$action,$message,'','OUTCOME',$file_name);
}

//--------------------------------------------------------------

function ValidateImage(string $action)
{
  global $_POST;

  if(!isset($_POST['image_name']))
  {
    throw new Wl2Exception("MISSING IMAGENAME",Codes::RUNTIME_ERROR);
  }  

  $image_name=$_POST['image_name'];
  $query="SELECT products_image, products_model FROM products WHERE products_image LIKE '{$image_name}'";
  $query_result=xtc_db_query($query);
  $result=xtc_db_fetch_array($query_result);

  if(isset($result))
  {
    print_xml_status(Codes::OK,$action,'','','OUTCOME',$image_name);
  }
  else
  {
    print_xml_status(Codes::FAILED,$action,'','','OUTCOME',$image_name);
  }

}

//--------------------------------------------------------------

function ValidateImageFile(string $action)
{
  global $_POST;

  if(!isset($_POST['image_name']))
  {
    throw new Wl2Exception("MISSING IMAGENAME",Codes::RUNTIME_ERROR);
  }  

  $image_name=$_POST['image_name'];

  if(file_exists(DIR_FS_DOCUMENT_ROOT.'images/product_images/original_images/'.$image_name))
  {
    print_xml_status(Codes::OK,$action,'','','OUTCOME',$image_name);
  }
  else
  {
    print_xml_status(Codes::FAILED,$action,'','','OUTCOME',$image_name);
  }

}

//--------------------------------------------------------------

function CsvFileImport2(string $action)
{
  global $_POST;

  if(!defined("CSV_SEPERATOR"))
  {
    define("CSV_SEPERATOR","|");
    define("CSV_TEXTSIGN","");
    define("CSV_CAT_DEPTH",4);
    define("CSV_CATEGORY_DEFAULT","Top");
  }

  if(!isset($_POST['file_name'])||empty($_POST['file_name']))
  {
    throw new Wl2Exception("MISSING FILENAME",Codes::RUNTIME_ERROR);
  }  
  
	$file_name=$_POST['file_name'];
  
  require DIR_FS_ADMIN.'/includes/classes/import.php';
  require DIR_FS_INC.'xtc_format_filesize.inc.php';
  require DIR_FS_INC.'xtc_get_customers_statuses.inc.php';

  $handler=new xtcImport($file_name);
  $handler->seperator="|";
  $handler->Textsign="^";
  $handler->catDepth=4;
  $handler->CatDefault="1TEMP";

  $map=$handler->generate_map();
  $mapping=$handler->map_file($map);
  if($mapping==="error")
  {
    throw new Wl2Exception("Mapping failed.",Codes::RUNTIME_ERROR);
  }

  $import=$handler->import($mapping);

  $error="";
  $message="";
  $code=CODES::FAILED;

  $file=DIR_FS_DOCUMENT_ROOT.'import/'.$file_name;
  if(file_exists($file)) 
  {
    unlink($file);
  }

  if(isset($import))
  {
    if($import[0]) 
    {
      $code=Codes::OK;
      $message="OK";
    }

    if(isset($import[1])&&$import[1][0]!='')
    {
      $code=Codes::FAILED;
      for($i=0;$i<count($import[1]);$i++) 
      {
        $error.$import[1][$i]."-";
      }
    }
  }
  else
  {  
    throw new Wl2Exception("PHP IMPORTHANDLER FAILED TO IMPORT",CODES::RUNTIME_ERROR);
  }

	print_xml_status($code,$action,$message,$error,'FILE_NAME',$file_name);
  return;
}

//--------------------------------------------------------------

function ImageProzess($action)
{
  define("DIR_FS_CATALOG_IMAGES",DIR_FS_CATALOG);

  define("DIR_FS_CATALOG_ORIGINAL_IMAGES",DIR_FS_CATALOG.DIR_WS_ORIGINAL_IMAGES);
  define("DIR_FS_CATALOG_INFO_IMAGES",DIR_FS_CATALOG.DIR_WS_INFO_IMAGES);
  define("DIR_FS_CATALOG_POPUP_IMAGES",DIR_FS_CATALOG.DIR_WS_POPUP_IMAGES);
  define("DIR_FS_CATALOG_THUMBNAIL_IMAGES",DIR_FS_CATALOG.DIR_WS_THUMBNAIL_IMAGES);
  define("DIR_FS_CATALOG_MIDI_IMAGES",DIR_FS_CATALOG.DIR_WS_MIDI_IMAGES);
  define("DIR_FS_CATALOG_MINI_IMAGES",DIR_FS_CATALOG.DIR_WS_MINI_IMAGES);

  define("DIR_FS_CATALOG_UPLOADED_IMAGES",DIR_FS_DOCUMENT_ROOT.'images/product_images/uploaded_images/');

  include DIR_FS_ADMIN.'includes/classes/image_manipulator.php';

  $original_images=[];
  $processed_images=[];
  $uploaded_images=[];

  if(!file_exists(DIR_FS_CATALOG_UPLOADED_IMAGES))
  {
    mkdir(DIR_FS_CATALOG_UPLOADED_IMAGES);
  }
  $dir=dir(DIR_FS_CATALOG_UPLOADED_IMAGES);

  if($dir===false)
  {
    throw new Wl2Exception("Uploaded images directory not exists.",Codes::RUNTIME_ERROR);
  }

  $outcome="";

  while(false!==($entry=$dir->read()))
  {
    if($entry!=="noimage.gif"&&!is_dir($entry)) 
    {
      $uploaded_images[]=$entry;
    }
  }

  $dir->close();
  
  $total_uploads=count($uploaded_images);
  $uploaded_images=array_slice($uploaded_images,0,5);
  $uploads=count($uploaded_images);
  $count=0;

  if($uploads>0)
  {
    foreach($uploaded_images as $picture)
    {
      $image=img_box(imagecreatefromjpeg(DIR_FS_CATALOG_UPLOADED_IMAGES.$picture),1600,1200);
      
      if($image!==null)
      {
        imagejpeg($image,DIR_FS_CATALOG_UPLOADED_IMAGES.$picture,85);
        rename(DIR_FS_CATALOG_UPLOADED_IMAGES.$picture, DIR_FS_CATALOG_ORIGINAL_IMAGES.$picture);

        $products_image_name=$picture;
        include DIR_FS_ADMIN.'/includes/product_info_images.php';
        include DIR_FS_ADMIN.'/includes/product_popup_images.php';
        include DIR_FS_ADMIN.'/includes/product_thumbnail_images.php';
        include DIR_FS_ADMIN.'/includes/product_midi_images.php';
        include DIR_FS_ADMIN.'/includes/product_mini_images.php';

        $outcome.="{$products_image_name} ok.\n";
        $count++;
      }
    }
  }

  if($uploads===0)
  {
    $outcome="Keine Bilder zum verarbeiten vorhanden.";
    $code=Codes::OK;
  }
  else if($count===$uploads&&$uploads===$total_uploads)
  {
    $outcome.="Alle Bilder erfolgfreich verarbeitet.";
    $code=Codes::OK;
  }
  else if($count===$uploads&&$uploads<$total_uploads)
  {
    $renaim=$total_uploads-$count;
    $outcome.="{$count} Bilder erfolgfreich verarbeitet.\n{$renaim} Bilder noch ausständig.";
    $code=Codes::CONTINUE;
  }
  else if($count<$uploads)
  {
    $outcome.="Nur {$count} Bilder von {$uploads} verarbeitet und vorzeitig abgebrochen.";
    throw new Wl2Exception($outcome,Codes::RUNTIME_ERROR);
  }
  else
  {
    throw new Wl2Exception("Unbekannter Fehler beim verarbeiten der Bilder aufgetreten.",Codes::RUNTIME_ERROR);
  }

  print_xml_status($code,$action,$uploads,'','OUTCOME',$outcome);

}

//--------------------------------------------------------------

function img_box($img, $box_w, $box_h) {
  $new=imagecreatetruecolor($box_w,$box_h);
  if($new===false)
  {
    return null;
  }

  $fill=imagecolorallocate($new,255,255,255);
  imagefill($new,0,0,$fill);

  $hratio=$box_h/imagesy($img);
  $wratio=$box_w/imagesx($img);
  $ratio=min($hratio,$wratio);

  $sy=floor(imagesy($img)*$ratio);
  $sx=floor(imagesx($img)*$ratio);

  $m_y=floor(($box_h-$sy)*0.5);
  $m_x=floor(($box_w-$sx)*0.5);

  if(!imagecopyresampled($new,$img,
      $m_x,$m_y, //dest x, y (margins)
      0,0, //src x, y (0,0 means top left)
      $sx,$sy, //dest w, h (resample to this size (computed above)
      imagesx($img),imagesy($img)) //src w, h (the full size of the original)
    ) 
  {
    imagedestroy($new);
    return null;
  }
  return $new;
}

//--------------------------------------------------------------

function PostBieneLog($action)
{
  global $_POST;

  if(!isset($_POST['message']))
  {
    exit;
  }

  $message=$_POST['message'];
  $filename=DIR_FS_DOCUMENT_ROOT.'import/biene.chk';

  if(!file_exists($filename))
  {
    $handle=fopen($filename,"w");
    fclose($handle);
  }
  
  if(is_writable($filename))
  {
    if($handle=fopen($filename,"a"))
    {
      if(!fwrite($handle,$message."\r\n"))
      {
        throw new Wl2Exception("UNABLE TO WRITE LOG",Codes::RUNTIME_ERROR);
      }
    }
    else
    {
      throw new Wl2Exception("UNABLE TO OPEN LOG",Codes::RUNTIME_ERROR);
    }
    fclose($handle);
  } 
  else 
  {
    throw new Wl2Exception("FILE NOT WRITEABLE",Codes::RUNTIME_ERROR);
  }
  print_xml_status(Codes::OK,$action,'OK','','OUTCOME',$message);
}

//--------------------------------------------------------------

function xtc_remove_product($product_id)
{
  global $LangID,$customers_statuses_array;

  define('DIR_FS_CATALOG_ORIGINAL_IMAGES',DIR_FS_CATALOG.DIR_WS_ORIGINAL_IMAGES);
  define('DIR_FS_CATALOG_MIDI_IMAGES',DIR_FS_CATALOG.DIR_WS_MIDI_IMAGES);
  define('DIR_FS_CATALOG_INFO_IMAGES',DIR_FS_CATALOG.DIR_WS_INFO_IMAGES);
  define('DIR_FS_CATALOG_POPUP_IMAGES',DIR_FS_CATALOG.DIR_WS_POPUP_IMAGES);
  define('DIR_FS_CATALOG_THUMBNAIL_IMAGES',DIR_FS_CATALOG.DIR_WS_THUMBNAIL_IMAGES);
  define('DIR_FS_CATALOG_MINI_IMAGES',DIR_FS_CATALOG.DIR_WS_MINI_IMAGES);
  define('DIR_FS_CATALOG_IMAGES',DIR_FS_CATALOG.DIR_WS_IMAGES);

  $product_image_query=xtc_db_query("select products_image from ".TABLE_PRODUCTS." where products_id='".xtc_db_input($product_id)."'");
  $product_image=xtc_db_fetch_array($product_image_query);

  $duplicate_image_query=xtc_db_query("select count(*) as total from ".TABLE_PRODUCTS." where products_image='".xtc_db_input($product_image['products_image'])."'");
  $duplicate_image=xtc_db_fetch_array($duplicate_image_query);

  if($duplicate_image['total']<2)
  {
    if(is_file(DIR_FS_CATALOG_POPUP_IMAGES.$product_image['products_image']))
    {
      @unlink(DIR_FS_CATALOG_POPUP_IMAGES.$product_image['products_image']);
    }
    if(is_file(DIR_FS_CATALOG_ORIGINAL_IMAGES.$product_image['products_image']))
    {  
      @unlink(DIR_FS_CATALOG_ORIGINAL_IMAGES.$product_image['products_image']);
    }
    if(is_file(DIR_FS_CATALOG_MINI_IMAGES.$product_image['products_image']))
    {
      @unlink(DIR_FS_CATALOG_MINI_IMAGES.$product_image['products_image']);
    }
    if(is_file(DIR_FS_CATALOG_THUMBNAIL_IMAGES.$product_image['products_image']))
    {
      @unlink(DIR_FS_CATALOG_THUMBNAIL_IMAGES.$product_image['products_image']);
    }
    if(is_file(DIR_FS_CATALOG_MIDI_IMAGES.$product_image['products_image']))
    {
      @unlink(DIR_FS_CATALOG_MIDI_IMAGES.$product_image['products_image']);
    }
    if(is_file(DIR_FS_CATALOG_INFO_IMAGES.$product_image['products_image']))
    {
      @unlink(DIR_FS_CATALOG_INFO_IMAGES.$product_image['products_image']);
    }
  }

  $mo_images_query=xtc_db_query("SELECT image_name FROM ".TABLE_PRODUCTS_IMAGES." WHERE products_id='".(int)$product_id."'");
  while($mo_images_values=xtc_db_fetch_array($mo_images_query))
  {
    $duplicate_more_image_query=xtc_db_query("SELECT count(*) AS total FROM ".TABLE_PRODUCTS_IMAGES." WHERE image_name='".xtc_db_input($mo_images_values['image_name'])."'");
    $duplicate_more_image = xtc_db_fetch_array($duplicate_more_image_query);
    if($duplicate_more_image['total']<2)
    {
      if(is_file(DIR_FS_CATALOG_POPUP_IMAGES.$mo_images_values['image_name'])) 
      {
        @unlink(DIR_FS_CATALOG_POPUP_IMAGES.$mo_images_values['image_name']);
      }
      if (is_file(DIR_FS_CATALOG_ORIGINAL_IMAGES.$mo_images_values['image_name']))
      {
        @unlink(DIR_FS_CATALOG_ORIGINAL_IMAGES.$mo_images_values['image_name']);
      }
      if (is_file(DIR_FS_CATALOG_MINI_IMAGES.$mo_images_values['image_name']))
      {
        @unlink(DIR_FS_CATALOG_MINI_IMAGES.$mo_images_values['image_name']);
      }
      if(is_file(DIR_FS_CATALOG_THUMBNAIL_IMAGES.$mo_images_values['image_name']))
      {
        @unlink(DIR_FS_CATALOG_THUMBNAIL_IMAGES.$mo_images_values['image_name']);
      }
      if(is_file(DIR_FS_CATALOG_MIDI_IMAGES.$mo_images_values['image_name']))
      {
        @unlink(DIR_FS_CATALOG_MIDI_IMAGES.$mo_images_values['image_name']);
      }
      if(is_file(DIR_FS_CATALOG_INFO_IMAGES.$mo_images_values['image_name']))
      {
        @unlink(DIR_FS_CATALOG_INFO_IMAGES.$mo_images_values['image_name']);
      }
    }
  }

  xtc_db_query("DELETE FROM ".TABLE_SPECIALS." WHERE products_id='".(int)$product_id."'");
  xtc_db_query("DELETE FROM ".TABLE_PRODUCTS." WHERE products_id='".(int)$product_id."'");
  xtc_db_query("DELETE FROM ".TABLE_PRODUCTS_XSELL." WHERE products_id='".(int)$product_id."'");
  xtc_db_query("DELETE FROM ".TABLE_PRODUCTS_XSELL." WHERE xsell_id='".(int)$product_id."'");
  xtc_db_query("DELETE FROM ".TABLE_PRODUCTS_IMAGES." WHERE products_id='".(int)$product_id."'");
  xtc_db_query("DELETE FROM ".TABLE_PRODUCTS_TO_CATEGORIES." WHERE products_id='".(int)$product_id."'");
  xtc_db_query("DELETE FROM ".TABLE_PRODUCTS_DESCRIPTION." WHERE products_id='".(int)$product_id."'");
  xtc_db_query("DELETE FROM ".TABLE_PRODUCTS_ATTRIBUTES." WHERE products_id='".(int)$product_id."'");
  xtc_db_query("DELETE FROM ".TABLE_CUSTOMERS_BASKET." WHERE products_id='" . (int)$product_id . "' OR products_id LIKE '".(int)$product_id."{%'");
  xtc_db_query("DELETE FROM ".TABLE_CUSTOMERS_BASKET_ATTRIBUTES." WHERE products_id='".(int)$product_id."' OR products_id LIKE '".(int)$product_id."{%'");
  xtc_db_query("DELETE FROM ".TABLE_PRODUCTS_TAGS." WHERE products_id='".(int)$product_id."'");

  if(defined('MODULE_WISHLIST_SYSTEM_STATUS') && MODULE_WISHLIST_SYSTEM_STATUS=='true')
  {
    xtc_db_query("DELETE FROM ".TABLE_CUSTOMERS_WISHLIST." WHERE products_id='".(int)$product_id."' OR products_id LIKE '".(int)$product_id."{%'");
    xtc_db_query("DELETE FROM ".TABLE_CUSTOMERS_WISHLIST_ATTRIBUTES." WHERE products_id='".(int)$product_id."' OR products_id LIKE '".(int)$product_id."{%'");
  }

  $customers_statuses_array=[];
  $customers_statuses_query=xtc_db_query("select * from ".TABLE_CUSTOMERS_STATUS." where language_id='".$LangID."' order by customers_status_id");

  while($customers_statuses=xtc_db_fetch_array($customers_statuses_query))
  {
    $customers_statuses_array[]=array('id'=>$customers_statuses['customers_status_id'],'text' => $customers_statuses['customers_status_name']);
  }

  for($i=0,$n=sizeof($customers_statuses_array);$i<$n;$i++)
  {
    xtc_db_query("delete from personal_offers_by_customers_status_".$i." where products_id='".xtc_db_input($product_id)."'");
  }

  $product_reviews_query=xtc_db_query("select reviews_id from ".TABLE_REVIEWS." where products_id='".xtc_db_input($product_id)."'");
  while($product_reviews=xtc_db_fetch_array($product_reviews_query))
  {
    xtc_db_query("delete from ".TABLE_REVIEWS_DESCRIPTION." where reviews_id='".$product_reviews['reviews_id']."'");
  }
  xtc_db_query("delete from ".TABLE_REVIEWS." where products_id='".xtc_db_input($product_id)."'");

}

?>
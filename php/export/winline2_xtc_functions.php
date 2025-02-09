<?php

function SendScriptVersion()
{
  global $_GET,$version_nr,$version_datum;

  $schema='<?xml version="1.0" encoding="'.CHARSET.'"?>'."\n".
          '<STATUS>'."\n".
          '<STATUS_DATA>'."\n".
          '<ACTION>'.$_GET['action'] .'</ACTION>'."\n".
          '<CODE>'.'111'.'</CODE>'."\n".
          '<SCRIPT_VER>'.$version_nr.'</SCRIPT_VER>'."\n".
          '<SCRIPT_DATE>'.$version_datum.'</SCRIPT_DATE>'."\n".
          '</STATUS_DATA>'."\n".
          '</STATUS>'."\n\n";
  echo $schema;
}

//--------------------------------------------------------------

function print_xml_status($code,$action,$msg,$mode,$item,$value)
{
  $schema='<?xml version="1.0" encoding="'.CHARSET.'"?>'."\n".
            '<STATUS>'."\n".
            '<STATUS_DATA>'."\n".
            '<CODE>'.$code.'</CODE>'."\n" .
            '<ACTION>'.$action.'</ACTION>'."\n".
            '<MESSAGE>'.$msg.'</MESSAGE>'."\n";

  if(strlen($mode)>0) {
    $schema.='<MODE>'.$mode.'</MODE>'."\n";
  }

  if(strlen($item)>0) {
    $schema.='<'.$item.'>'.$value.'</'.$item.'>'."\n";
  }
  $schema.='</STATUS_DATA>'."\n".
             '</STATUS>'."\n\n";

  echo $schema;

  return;
}

//--------------------------------------------------------------

function column_exists($table,$column)
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


function SendHTMLHeader()
{
  header("Last-Modified: ".gmdate("D, d M Y H:i:s")." GMT");
  header("Cache-Control: no-cache, must-revalidate");
  header("Pragma: no-cache");
  header("Content-type: text/html");
}

//--------------------------------------------------------------

function ShowHTMLMenu()
{
  global $version_nr,$version_datum,$PHP_SELF;
  SendHTMLHeader();

?>

  <html><head></head><body>
  <h3>Winline - modified Shopanbindung</a></h3>
  <h4>Version <?php echo $version_nr; ?> Stand : <?php echo $version_datum; ?></h4>
  </body>
  </html>

<?php
}

//--------------------------------------------------------------

function CsvFileUpload()
{
  global $_GET,$_POST;

  $file_name=$_GET['file_name'];
  $file_size=$_GET['file_size'];

  $putdata=fopen("php://input","r");
  $fp=fopen(DIR_FS_DOCUMENT_ROOT.'import/'.$file_name,"w");
  while($data=fread($putdata,1024))
  {
    fwrite($fp,$data);
  }

  fclose($fp);
  fclose($putdata);
  $fp_size=filesize(DIR_FS_DOCUMENT_ROOT.'import/'.$file_name);

  if($fp_size==$file_size)
  {
    $code=0;
    $message="OK {$file_size}:{$fp_size}";
  } else {
    $code = -1;
    $message="FAILED {$file_size}:{$fp_size}";
  }

  print_xml_status($code,$_POST['action'],$message,'','OUTCOME',$file_name);
}

//--------------------------------------------------------------

function ImageUpload()
{
  global $_GET,$_POST;

  $file_name=$_GET['file_name'];
  $file_size=$_GET['file_size'];

  $putdata=fopen("php://input","r");
  $fp=fopen(DIR_FS_DOCUMENT_ROOT.'images/product_images/uploaded_images/'.$file_name, "w");
  while($data=fread($putdata,1024))
  {
    fwrite($fp,$data);
  }

  fclose($fp);
  fclose($putdata);
  $fp_size=filesize(DIR_FS_DOCUMENT_ROOT.'images/product_images/uploaded_images/'.$file_name);

  if($fp_size==$file_size) {
    $code=0;
    $message="OK {$file_size}:{$fp_size}";
  } else {
    $code=-1;
    $message="FAILED {$file_size}:{$fp_size}";
  }

  print_xml_status($code,$_POST['action'],$message,'','OUTCOME',$file_name);
}

//--------------------------------------------------------------

function ValidateImage()
{
  global $_POST;

  $image_name=$_POST['image_name'];
  $query="SELECT products_image, products_model FROM products WHERE products_image LIKE '{$image_name}'";
  $query_result=xtc_db_query($query);
  $result=xtc_db_fetch_array($query_result);

  if(isset($result))
  {
    print_xml_status("0",'','','','OUTCOME',$image_name);
  }
  else
  {
    print_xml_status("-1",'','','','OUTCOME',$image_name);
  }

}

//--------------------------------------------------------------

function ValidateImageFile()
{
  global $_POST;

  $image_name=$_POST['image_name'];

  if(file_exists(DIR_FS_DOCUMENT_ROOT.'images/product_images/original_images/'.$image_name))
  {
    print_xml_status("0",'','','','OUTCOME',$image_name);
  }
  else
  {
    print_xml_status("-1",'','','','OUTCOME',$image_name);
  }

}

//--------------------------------------------------------------

function CsvFileImport2()
{
  global $_GET, $_POST;
	$file_name=$_POST['file_name'];
  
  require(DIR_FS_ADMIN.'/includes/classes/import.php');
  require(DIR_FS_INC.'xtc_format_filesize.inc.php');
  require(DIR_FS_INC.'xtc_get_customers_statuses.inc.php');

  define("CSV_SEPERATOR","|");
  define("CSV_TEXTSIGN","");
  define("CSV_CAT_DEPTH",4);
  define("CSV_CATEGORY_DEFAULT","Top");

  $handler=new xtcImport($file_name);
  $map=$handler->generate_map();
  $mapping=$handler->map_file($map);
  $import=$handler->import($mapping);

  $code=-1;
  $message='FAILED';
  $error='';

  if (isset($import)) 
  {
    if($import[0]) 
    {
      $code=0;
      $message="OK";
    }

    if(isset($import[1])&&$import[1][0]!='')
    {
      $code=-1;
      for($i=0;$i<count($import[1]);$i++) 
      {
        $error.$import[1][$i]."-";
      }
    }
  }

	$file=DIR_FS_DOCUMENT_ROOT.'import/'.$file_name;
  if(file_exists($file)) 
  {
		unlink($file);
  }
  
	print_xml_status($code,$_POST['action'],$message,$error,'FILE_NAME',$file_name);
  return;
}

//--------------------------------------------------------------

function ImageProzess()
{
  global $_GET,$_POST;

  define("DIR_FS_CATALOG_ORIGINAL_IMAGES",DIR_FS_CATALOG.DIR_WS_ORIGINAL_IMAGES);
  define("DIR_FS_CATALOG_INFO_IMAGES",DIR_FS_CATALOG.DIR_WS_INFO_IMAGES);
  define("DIR_FS_CATALOG_POPUP_IMAGES",DIR_FS_CATALOG.DIR_WS_POPUP_IMAGES);
  define("DIR_FS_CATALOG_THUMBNAIL_IMAGES",DIR_FS_CATALOG.DIR_WS_THUMBNAIL_IMAGES);
  define("DIR_FS_CATALOG_IMAGES",DIR_FS_CATALOG.DIR_WS_IMAGES);
  define("DIR_FS_CATALOG_UPLOADED_IMAGES",DIR_FS_DOCUMENT_ROOT.'images/product_images/uploaded_images/');

  $original_images=[];
  $processed_images=[];
  $uploaded_images=[];

  if(!file_exists(DIR_FS_CATALOG_UPLOADED_IMAGES))
  {
    mkdir(DIR_FS_CATALOG_UPLOADED_IMAGES);
  }
  $dir=dir(DIR_FS_CATALOG_UPLOADED_IMAGES);
  $outcome="";

  while(false!==($entry=$dir->read())) 
  {
    if($entry!="noimage.gif"&&!is_dir($entry)) 
    {
      $uploaded_images[]=$entry;
    }
  }

  $dir->close();
  $uploads=count($uploaded_images);
  $count=0;

  if($uploads>0)
  {
    foreach($uploaded_images as $picture) 
    {
      $image=img_box(imagecreatefromjpeg(DIR_FS_CATALOG_UPLOADED_IMAGES.$picture),800,600);
      
      if($image!==null)
      {
        imagejpeg($image,DIR_FS_CATALOG_UPLOADED_IMAGES.$picture,100);
        rename(DIR_FS_CATALOG_UPLOADED_IMAGES.$picture, DIR_FS_CATALOG_ORIGINAL_IMAGES.$picture);

        $products_image_name=$picture;
        if(strlen($outcome)===0)
        {
          $outcome.=$products_image_name;
        } 
        else 
        {
          $outcome.=",{$products_image_name}";
        }

        include(DIR_FS_ADMIN.'/includes/product_info_images.php');
        include(DIR_FS_ADMIN.'/includes/product_popup_images.php');
        include(DIR_FS_ADMIN.'/includes/product_thumbnail_images.php');
        $count++;
      }
    }

    if($count>0) 
    {
      $outcome.=" erfolgreich.";
    } 
    else 
    {
      $outcome="Keine Bilder gefunden.";
    }

  }

  print_xml_status("0",$_POST['action'],$uploads,'','OUTCOME',$outcome);
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

function PostBieneLog()
{
  global $_GET,$_POST;

  $filename=DIR_FS_DOCUMENT_ROOT.'import/biene.chk';
  $message=$_POST['message'];
  
  if(is_writable($filename))
  {
    if($handle=fopen($filename,"a"))
    {
      if(!fwrite($handle,$message."\r\n"))
      {
        print_xml_status('-1',$_POST['action'],"UNABLE TO WRITE LOG",'','OUTCOME',$message);
        exit;
      }
    }
    else
    {
      print_xml_status('-1',$_POST['action'],"UNABLE TO OPEN LOG",'','OUTCOME',$message);
      exit;
    }
    fclose($handle);
  } 
  else 
  {
    print_xml_status('-1',$_POST['action'],"FILE NOT WRITEABLE",'','OUTCOME',$message);
    exit;
  }
  print_xml_status('0',$_POST['action'],'OK','','OUTCOME',$message);
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
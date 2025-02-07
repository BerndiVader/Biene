<?php

require('../includes/application_top_export.php');
require(DIR_FS_INC.'xtc_try_upload.inc.php');

$version_nr    ='1.06';
$version_datum ='2025.02.05';

define('SWITCH_MWST',false);
define ('LOGGER',false);
define('USE_3IMAGES',false);
define('USE_VPE',false);
define('SEND_ACCOUNT_MAIL',false);
define ('_VALID_XTC',false);

$LangID=2;
$Lang_folder='german';

define('SET_TIME_LIMIT',1);
define('CHARSET',(defined('DB_SERVER_CHARSET')&&strpos(DB_SERVER_CHARSET,'utf8')!==false)?'utf-8':'iso-8859-15');
define('DIR_FS_LANGUAGES',DIR_WS_LANGUAGES);
define('CONTENT_CONDITIONS','');
define('DIR_FS_ADMIN',DIR_FS_DOCUMENT_ROOT.DIR_ADMIN);

$user=$password="";

if((isset($_POST['user']))and(isset($_POST['password'])))
{
  $user=$_POST['user'];
  $password=$_POST['password'];
}
else 
{
  $user=$_GET['user'];
  $password=$_GET['password'];
}

if($user=='' or $password=='')
{
?>

<html><head><title></title></head><body>
<h3>Winline - modified eCommerce Shopsoftware Anbindung</a></h3>
<h4>Version <?php echo $version_nr; ?> Stand : <?php echo $version_datum; ?></h4>
</body></html>

<?php
  exit;
}
else
{
  require_once('winline2_xtc_functions.php');

  // security  1.check if admin user with this mailadress exits, and got access to xml-export
  //           2.check if password = true

  if(column_exists('admin_access','xml_export')==false)
  {
    xtc_db_query("ALTER TABLE admin_access ADD xml_export INT(1)  DEFAULT '0';");
  }

  $check_customer_query=xtc_db_query("select customers_id,
                                      customers_status,
                                      customers_password
                                      from customers where
                                      customers_email_address='".xtc_db_input($user)."'");

  if(!xtc_db_num_rows($check_customer_query))
  {
    SendXMLHeader();
    print_xml_status(105,$_POST['action'],'WRONG LOGIN','','','');
    exit; 
  }
  else
  {
    $check_customer=xtc_db_fetch_array($check_customer_query);
    // check if customer is Admin
    if($check_customer['customers_status']!='0') 
    {
      SendXMLHeader();
      print_xml_status(106,$_POST['action'],'WRONG LOGIN','','','');	  
      exit;
    }
    // check if Admin is allowed to access xml_export
    $access_query=xtc_db_query("SELECT
                                xml_export
                                from admin_access
                                WHERE customers_id='".$check_customer['customers_id']."'");
    $access_data=xtc_db_fetch_array($access_query);
    if($access_data['xml_export']!=1) 
    {
      SendXMLHeader();
      print_xml_status(107,$_POST['action'],'WRONG LOGIN','','','');
      exit;
    }

    if(!( ($check_customer['customers_password']==$password) or
          ($check_customer['customers_password']==md5($password)) or
          ($check_customer['customers_password']==md5(substr($password,2,40)))
      ))
    {
      SendXMLHeader();
      print_xml_status(108,$_POST['action'],'WRONG PASSWORD',$check_customer['customers_password']." ".md5($password),'','');
      exit;
    }
  }
}

if($_SERVER['REQUEST_METHOD']=='GET') 
{
  switch($_GET['action'])
  {
    case'version':
      SendXMLHeader();
      SendScriptVersion();
      exit;
    default:
      ShowHTMLMenu();
      exit;
  }
}
  else 
{
  if($_SERVER['REQUEST_METHOD']=='POST')
  {
    switch($_GET['action'])
    {
      case'img_upload':
        SendXMLHeader();
        ImageUpload();
        exit;
      case'csv_upload':
        SendXMLHeader();
        CsvFileUpload();
        exit;
    }

    switch($_POST['action']) 
    {
      case'log':
        SendXMLHeader();
        PostBieneLog();
        exit;
      case'img_upload':
        SendXMLHeader();
        ImageUpload();
        exit;
      case'csv_upload':
        SendXMLHeader();
        CsvFileUpload();
        exit;
      case'csv_import':
        SendXMLHeader();
        CsvFileImport2();
        exit;
	  case'img_process':
		SendXMLHeader();
        ImageProzess();
        exit;
      case'img_validate':
        SendXMLHeader();
        ValidateImage();
        exit;
      case'img_validate_file':
        SendXMLHeader();
        ValidateImageFile();
        exit;
    }
  }
}

?>
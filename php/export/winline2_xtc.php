<?php

define('SET_TIME_LIMIT',1);
define('CHARSET','iso-8859-1');

$version_nr    = '1.05';
$version_datum = '2020.04.25';

define('SWITCH_MWST',false);
define ('LOGGER',true);
define('USE_3IMAGES',false);
define('USE_VPE',false);
define('SEND_ACCOUNT_MAIL',false);

$LangID = 2;
$Lang_folder = 'german';

$order_total_class['ot_cod_fee']['prefix'] = '+';
$order_total_class['ot_cod_fee']['tax'] = '19';

$order_total_class['ot_customer_discount']['prefix'] = '-';
$order_total_class['ot_customer_discount']['tax'] = '19';

$order_total_class['ot_gv']['prefix'] = '-';
$order_total_class['ot_gv']['tax'] = '0';

$order_total_class['ot_loworderfee']['prefix'] = '+';
$order_total_class['ot_loworderfee']['tax'] = '19';

$order_total_class['ot_shipping']['prefix'] = '+';
$order_total_class['ot_shipping']['tax'] = '19';

define('_VALID_XTC',false);
require('../includes/application_top_export.php');

// Kundengruppen ID für Neukunden (default "neue Kunden einstellungen in XTC")
define('STANDARD_GROUP',DEFAULT_CUSTOMERS_STATUS_ID);
include(DIR_FS_DOCUMENT_ROOT.(defined('DIR_ADMIN') ? DIR_ADMIN : 'admin/').'includes/classes/'.IMAGE_MANIPULATOR);
define(DIR_FS_ADMIN,DIR_FS_DOCUMENT_ROOT."admin/");

$user=$password="";
if ((isset($_POST['user']))and(isset($_POST['password']))) 
{
   $user=$_POST['user'];
   $password=$_POST['password'];
} 
  else 
{
   $user=$_GET['user'];
   $password=$_GET['password'];
}

if ($user=='' or $password=='')
{
?>
<html><head><title></title></head><body>
<h3>Winline - modified eCommerce Shopsoftware Anbindung</a></h3>
<h4>Version <?php echo $version_nr; ?> Stand : <?php echo $version_datum; ?></h4>
<br><br>
Aufruf des Scriptes mit <br><b><?php echo $PHP_SELF; ?>?user=<font color="red">ADMIN-EMAIL</font>&password=<font color="red">ADMIN-PASSWORD-IM-KLARTEXT</font>
</b>
</body></html>
<?php
  exit;
}
  else
{
  require_once('winline2_xtc_functions.php');

  // security  1.check if admin user with this mailadress exits, and got access to xml-export
  //           2.check if password = true

  if (column_exists('admin_access','xml_export')==false)
  {
     xtc_db_query('ALTER TABLE admin_access ADD xml_export INT(1)  DEFAULT "0";');
     xtc_db_query('UPDATE admin_access SET xml_export= 1 WHERE customers_id=\'1\';');
  }

  $check_customer_query=xtc_db_query("select customers_id,
                                      customers_status,
                                      customers_password
                                      from " . TABLE_CUSTOMERS . " where
                                      customers_email_address = '" . xtc_db_input($user) . "'");

  if (!xtc_db_num_rows($check_customer_query))
  {
    SendXMLHeader();
    print_xml_status (105, $_POST['action'], 'WRONG LOGIN', '', '', '');	 
    exit; 
  }
    else
  {
    $check_customer = xtc_db_fetch_array($check_customer_query);

    // check if customer is Admin
    if ($check_customer['customers_status']!='0') 
    {
      SendXMLHeader();
      print_xml_status (106, $_POST['action'], 'WRONG LOGIN', '', '', '');	  
      exit;
    }

    // check if Admin is allowed to access xml_export
    $access_query=xtc_db_query("SELECT
                                xml_export
                                from admin_access
                                WHERE customers_id='".$check_customer['customers_id']."'");
    $access_data = xtc_db_fetch_array($access_query);
    if ($access_data['xml_export']!=1) 
    {
      SendXMLHeader ();
      print_xml_status (107, $_POST['action'], 'WRONG LOGIN', '', '', '');
      exit;
    }

    if (!(($check_customer['customers_password'] == $password) or 
             ($check_customer['customers_password'] == md5($password)) or
             ($check_customer['customers_password'] == md5(substr($password,2,40)))
       ))
    {
      SendXMLHeader ();
      print_xml_status (108, $_POST['action'], 'WRONG PASSWORD', $check_customer['customers_password']." ".md5($password), '');
      exit;
    }
  }
}

if ($_SERVER['REQUEST_METHOD']=='GET') 
{
  switch ($_GET['action']) 
  {
     case 'version':        // Ausgabe Scriptversion
       SendXMLHeader ();
       SendScriptVersion ();
       exit; 
     
     case 'categories_export':
       SendXMLHeader ();
       SendCategories ();
       exit;
     
     case 'manufacturers_export':
       SendXMLHeader ();
       SendManufacturers ();
       exit;
     
     case 'orders_export':
       SendXMLHeader();
       SendOrders();
       exit;
     
     case 'products_export':
       SendXMLHeader();
       SendProducts();
       exit;
     
     case 'customers_export':
       SendXMLHeader();
       SendCustomers();
       exit;
     
     case 'customers_newsletter_export':
       SendXMLHeader();
       SendCustomersNewsletter();
       exit;
     
     case 'config_export':
       SendXMLHeader ();
       SendShopConfig ();
       exit;
       
     case 'update_tables':
       UpdateTables ();
       exit;
       
     case 'send_log':
       SendLog ();
       exit;

     default :     
       ShowHTMLMenu();
       exit;
       
   } // End Case   
} // End Method POST
 else 
{
  if ($_SERVER['REQUEST_METHOD']=='POST') 
  {

    switch($_GET["action"])
    {
      case "img_upload":
        SendXMLHeader();
        ImageUpload();
        exit;
      case 'csv_upload':
        SendXMLHeader();
        CsvFileUpload();
        exit;
      }

    switch ($_POST['action']) 
    {
      case 'log':
        SendXMLHeader();
        PostBieneLog();
        exit;
      case 'img_upload':
        SendXMLHeader();
        ImageUpload();
        exit;
      case 'csv_upload':
        SendXMLHeader();
        CsvFileUpload();
        exit;
      case 'csv_import':
        SendXMLHeader();
        CsvFileImport2();
        exit;
	  case 'img_process':
		SendXMLHeader();
        ImageProzess();
        exit;
      case 'img_validate':
        SendXMLHeader();
        ValidateImage();
        exit;
      case 'img_validate_file':
        SendXMLHeader();
        ValidateImageFile();
        exit;
      case 'manufacturers_image_upload':
        SendXMLHeader ();
        ManufacturersImageUpload ();
        exit;
     case 'categories_image_upload':
        SendXMLHeader ();
        CategoriesImageUpload ();
        exit;
     case 'products_image_upload':
        SendXMLHeader ();
        ProductsImageUpload ();
        exit;   
     case 'products_image_upload_med':
        SendXMLHeader ();
        ProductsImageUploadMed ();
        exit;   
     case 'products_image_upload_large':
        SendXMLHeader ();
        ProductsImageUploadLarge ();
        exit;   
     case 'manufacturers_update':
        SendXMLHeader ();
        ManufacturersUpdate ();
        exit;   
      case 'manufacturers_erase':
        SendXMLHeader ();
        ManufacturersErase ();
        exit;   
      case 'products_update':
        SendXMLHeader ();
        ProductsUpdate ();
        exit;
      case 'products_erase':
        SendXMLHeader ();
        ProductsErase ();
        exit;
      case 'products_specialprice_update':
        SendXMLHeader ();
        ProductsSpecialPriceUpdate ();
        exit;
      case 'products_specialprice_erase':  
        SendXMLHeader ();
        ProductsSpecialPriceErase ();
        exit;
      case 'categories_update':
        SendXMLHeader ();
        CategoriesUpdate ();
        exit;
      case 'categories_erase':
        SendXMLHeader ();  
        CategoriesErase ();
        exit;
      case 'prod2cat_update':
        SendXMLHeader ();  
        Prod2CatUpdate ();
        exit;
      case 'prod2cat_erase':
        SendXMLHeader ();  
        Prod2CatErase ();
        exit;
      case 'order_update':
        SendXMLHeader ();  
        OrderUpdate ();
        exit;
      case 'customers_update':
        SendXMLHeader ();  
        CustomersUpdate ();
        exit;
      case 'customers_erase':
        SendXMLHeader ();  
        CustomersErase ();
        exit;
      case 'xsell_update':
        SendXMLHeader ();
        XsellUpdate ();
        exit;
      case 'xsell_erase':
        SendXMLHeader ();
        XsellErase ();
        exit;  
          
    } // End Case
  }  // End Method POST
}


function test() {

}

?>
<?php

require_once(DIR_FS_INC .'xtc_not_null.inc.php');
require_once(DIR_FS_INC . 'xtc_redirect.inc.php');
require_once(DIR_FS_INC . 'xtc_rand.inc.php');

if (!function_exists('xtDBquery')) {

  function xtDBquery($query)
  {
  //  if (defined('DB_CACHE') && DB_CACHE == 'true') //Dokuman - 2011-02-11 - check for defined DB_CACHE
  //  {
  //    $result = xtc_db_queryCached($query);
  //  }
  //    else
  //  {
      $result = xtc_db_query($query);
  //  }
    return $result;
  }

}

return;

//--------------------------------------------------------------

function SendScriptVersion ()
{
   global $_GET, $version_nr, $version_datum;

   $schema = '<?xml version="1.0" encoding="' . CHARSET . '"?>' . "\n" .
             '<STATUS>' . "\n" .
             '<STATUS_DATA>' . "\n" .
             '<ACTION>' . $_GET['action'] . '</ACTION>' . "\n" .
             '<CODE>' . '111' . '</CODE>' . "\n" .
             '<SCRIPT_VER>' . $version_nr . '</SCRIPT_VER>' . "\n" .
             '<SCRIPT_DATE>' . $version_datum . '</SCRIPT_DATE>' . "\n" .
             '</STATUS_DATA>' . "\n" .
             '</STATUS>' . "\n\n";
   echo $schema;
}


//--------------------------------------------------------------

function print_xml_status ($code, $action, $msg, $mode, $item, $value)
{
  $schema = '<?xml version="1.0" encoding="' . CHARSET . '"?>' . "\n" .
            '<STATUS>' . "\n" .
            '<STATUS_DATA>' . "\n" .
            '<CODE>' . $code . '</CODE>' . "\n" .
            '<ACTION>' . $action . '</ACTION>' . "\n" .
            '<MESSAGE>' . $msg . '</MESSAGE>' . "\n";

  if (strlen($mode)>0) {
    $schema .= '<MODE>' . $mode . '</MODE>' . "\n";
  }

  if (strlen($item)>0) {
    $schema .= '<' . $item . '>' . $value . '</' . $item . '>' . "\n";
  }
  $schema .= '</STATUS_DATA>' . "\n" .
             '</STATUS>' . "\n\n";

  echo $schema;

  return;
}

//--------------------------------------------------------------

function table_exists($table_name)
{
  $Table = xtc_db_query("show tables like '" . $table_name . "'");
  if(xtc_db_fetch_row($Table) === false)
  {
    return(false);
  } else {
    return(true);
  }
}

//--------------------------------------------------------------

function column_exists($table, $column)
{
  $Table = xtc_db_query("show columns from $table LIKE '" . $column . "'");
  if(xtc_db_fetch_row($Table) === false)
  {
    return(false);
  } else {
    return(true);
  }
}

//--------------------------------------------------------------

function SendCategories ()
{
  if (defined('SET_TIME_LIMIT')) { @set_time_limit(0);}

  $schema = '<?xml version="1.0" encoding="' . CHARSET . '"?>' . "\n" .
            '<CATEGORIES>' . "\n";

  echo $schema;

  $cat_query = xtc_db_query("select categories_id, categories_image, parent_id, sort_order, date_added, last_modified ".
                            " from " . TABLE_CATEGORIES . " order by parent_id, categories_id");
  while ($cat = xtc_db_fetch_array($cat_query))
  {
    $schema  = '<CATEGORIES_DATA>' . "\n" .
               '<ID>' . $cat['categories_id'] . '</ID>' . "\n" .
               '<PARENT_ID>' . $cat['parent_id'] . '</PARENT_ID>' . "\n" .
               '<IMAGE_URL>' . encode_htmlspecialchars($cat['categories_image']) . '</IMAGE_URL>' . "\n" .
               '<SORT_ORDER>' . $cat['sort_order'] . '</SORT_ORDER>' . "\n" .
               '<DATE_ADDED>' . $cat['date_added'] . '</DATE_ADDED>' . "\n" .
               '<LAST_MODIFIED>' . $cat['last_modified'] . '</LAST_MODIFIED>' . "\n";

    $detail_query = xtc_db_query("select categories_id, language_id,
                                  categories_name,
                                  categories_heading_title,
                                  categories_description,
                                  categories_meta_title,
                                  categories_meta_description,
                                  categories_meta_keywords, " . TABLE_LANGUAGES . ".code as lang_code, " . TABLE_LANGUAGES . ".name as lang_name from " . TABLE_CATEGORIES_DESCRIPTION . "," . TABLE_LANGUAGES .
                                  " where " . TABLE_CATEGORIES_DESCRIPTION . ".categories_id=" . $cat['categories_id'] . " and " . TABLE_LANGUAGES . ".languages_id=" . TABLE_CATEGORIES_DESCRIPTION . ".language_id");

    while ($details = xtc_db_fetch_array($detail_query))
    {
      $schema .= "<CATEGORIES_DESCRIPTION ID='" . $details["language_id"] ."' CODE='" . $details["lang_code"] . "' NAME='" . $details["lang_name"] . "'>\n";
      $schema .= "<NAME>" . encode_htmlspecialchars($details["categories_name"]) . "</NAME>" . "\n";
      $schema .= "<HEADING_TITLE>" . encode_htmlspecialchars($details["categories_heading_title"]) . "</HEADING_TITLE>" . "\n";
      $schema .= "<DESCRIPTION>" . encode_htmlspecialchars($details["categories_description"]) . "</DESCRIPTION>" . "\n";
      $schema .= "<META_TITLE>" . encode_htmlspecialchars($details["categories_meta_title"]) . "</META_TITLE>" . "\n";
      $schema .= "<META_DESCRIPTION>" . encode_htmlspecialchars($details["categories_meta_description"]) . "</META_DESCRIPTION>" . "\n";
      $schema .= "<META_KEYWORDS>" . encode_htmlspecialchars($details["categories_meta_keywords"]) . "</META_KEYWORDS>" . "\n";
      $schema .= "</CATEGORIES_DESCRIPTION>\n";
    }

    // Produkte in dieser Categorie auflisten
    $prod2cat_query = xtc_db_query("select categories_id, products_id from " . TABLE_PRODUCTS_TO_CATEGORIES .
                                   " where categories_id='" . $cat['categories_id'] . "'");

    while ($prod2cat = xtc_db_fetch_array($prod2cat_query))
    {
      $schema .="<PRODUCTS ID='" . $prod2cat["products_id"] ."'></PRODUCTS>" . "\n";
    }
    $schema .= '</CATEGORIES_DATA>' . "\n";
    echo $schema;
  }
  $schema = '</CATEGORIES>' . "\n";
  echo $schema;
}

//--------------------------------------------------------------

function  SendManufacturers ()
{
  if (defined('SET_TIME_LIMIT')) { @set_time_limit(0);}

  $schema = '<?xml version="1.0" encoding="' . CHARSET . '"?>' . "\n" .
            '<MANUFACTURERS>' . "\n";
  echo $schema;

  $cat_query = xtc_db_query("select manufacturers_id, manufacturers_name, manufacturers_image, date_added, last_modified ".
                            " from " . TABLE_MANUFACTURERS . " order by manufacturers_id");

  while ($cat = xtc_db_fetch_array($cat_query))
  {
    $schema  = '<MANUFACTURERS_DATA>' . "\n" .
               '<ID>' . $cat['manufacturers_id'] . '</ID>' . "\n" .
               '<NAME>' . encode_htmlspecialchars($cat['manufacturers_name']) . '</NAME>' . "\n" .
               '<IMAGE>' . encode_htmlspecialchars($cat['manufacturers_image']) . '</IMAGE>' . "\n" .
               '<DATE_ADDED>' . $cat['date_added'] . '</DATE_ADDED>' . "\n" .
               '<LAST_MODIFIED>' . $cat['last_modified'] . '</LAST_MODIFIED>' . "\n";

    $sql = "select
             manufacturers_id, " .
             TABLE_MANUFACTURERS_INFO . ".languages_id,
             manufacturers_url,
             url_clicked,
             date_last_click, " .
             TABLE_LANGUAGES . ".code as lang_code, " .
             TABLE_LANGUAGES . ".name as lang_name
            from " .
             TABLE_MANUFACTURERS_INFO . "," .
             TABLE_LANGUAGES . "
            where " .
             TABLE_MANUFACTURERS_INFO . ".manufacturers_id=" . $cat['manufacturers_id'] . " and " .
             TABLE_LANGUAGES . ".languages_id=" . TABLE_MANUFACTURERS_INFO . ".languages_id";

    $detail_query = xtc_db_query($sql);

    while ($details = xtc_db_fetch_array($detail_query))
    {
      $schema .= "<MANUFACTURERS_DESCRIPTION ID='" . $details["languages_id"] ."' CODE='" . $details["lang_code"] . "' NAME='" . $details["lang_name"] . "'>\n";
      $schema .= "<URL>" . encode_htmlspecialchars($details["manufacturers_url"]) . "</URL>" . "\n" ;
      $schema .= "<URL_CLICK>" . $details["url_clicked"] . "</URL_CLICK>" . "\n" ;
      $schema .= "<DATE_LAST_CLICK>" . $details["date_last_click"] . "</DATE_LAST_CLICK>" . "\n" ;
      $schema .= "</MANUFACTURERS_DESCRIPTION>\n";
    }
    $schema .= '</MANUFACTURERS_DATA>' . "\n";
    echo $schema;
  }
  $schema = '</MANUFACTURERS>' . "\n";
  echo $schema;
}

//--------------------------------------------------------------

function SendOrders ()
{
  global $_GET, $order_total_class;

  $order_from = xtc_db_prepare_input($_GET['order_from']);
  $order_to = xtc_db_prepare_input($_GET['order_to']);
  $order_status = xtc_db_prepare_input($_GET['order_status']);

  if (defined('SET_TIME_LIMIT')) { @set_time_limit(0);}

  $schema = '<?xml version="1.0" encoding="' . CHARSET . '"?>' . "\n" .
            '<ORDER>' . "\n";
  echo $schema;

  $sql ="select * from " . TABLE_ORDERS . " where orders_id >= '" . xtc_db_input($order_from) . "'";
  
  if ($order_to) {
    $sql .= " and orders_id <= " . $order_to;
  }

  if (!$order_status && !$order_from) {    
    $sql .= " and orders_status IN ('". DEFAULT_ORDERS_STATUS_ID ."', '". PAYPAL_ORDER_STATUS_SUCCESS_ID ."', '". PAYPAL_ORDER_STATUS_PENDING_ID ."')";
  } elseif ($order_status) {
    $sql .= " and orders_status = " . $order_status;
  }
 
  $orders_query = xtc_db_query($sql);

  while ($orders = xtc_db_fetch_array($orders_query))
  {
    // Geburtsdatum laden
    $cust_sql = "select * from " . TABLE_CUSTOMERS . " where customers_id=" . $orders['customers_id'];
    $cust_query = xtc_db_query ($cust_sql);
    if (($cust_query) && ($cust_data = xtc_db_fetch_array($cust_query)))
    {
      $cust_dob = $cust_data['customers_dob'];
      $cust_gender = $cust_data['customers_gender'];
    }
      else
    {
      $cust_dob = '';
      $cust_gender = '';
    }
    if ($orders['billing_company']=='') $orders['billing_company']=$orders['delivery_company'];
    if ($orders['billing_name']=='')  $orders['billing_name']=$orders['delivery_name'];
    if ($orders['billing_lastname']=='') $orders['billing_lastname']=$orders['delivery_lastname'];
    if ($orders['billing_firstname']=='') $orders['billing_firstname']=$orders['delivery_firstname'];
    if ($orders['billing_street_address']=='') $orders['billing_street_address']=$orders['delivery_street_address'];
    if ($orders['billing_postcode']=='')  $orders['billing_postcode']=$orders['delivery_postcode'];
    if ($orders['billing_city']=='')  $orders['billing_city']=$orders['delivery_city'];
    if ($orders['billing_suburb']=='') $orders['billing_suburb']=$orders['delivery_suburb'];
    if ($orders['billing_state']=='')  $orders['billing_state']=$orders['delivery_state'];
    if ($orders['billing_country']=='')  $orders['billing_country']=$orders['delivery_country'];
    if ($orders['billing_country_iso_code_2']=='') $orders['billing_country_iso_code_2']=$orders['delivery_country_iso_code_2'];

    $schema  = '<ORDER_INFO>' . "\n" .
               '<ORDER_HEADER>' . "\n" .
               '<ORDER_ID>' . $orders['orders_id'] . '</ORDER_ID>' . "\n" .
               '<CUSTOMER_ID>' . $orders['customers_id'] . '</CUSTOMER_ID>' . "\n" .
               '<CUSTOMER_CID>' . $orders['customers_cid'] . '</CUSTOMER_CID>' . "\n" .
               '<CUSTOMER_GROUP>' . $orders['customers_status'] . '</CUSTOMER_GROUP>' . "\n" .
               '<ORDER_DATE>' . $orders['date_purchased'] . '</ORDER_DATE>' . "\n" .
               '<ORDER_STATUS>' . $orders['orders_status'] . '</ORDER_STATUS>' . "\n" .
               '<ORDER_IP>' . $orders['customers_ip'] . '</ORDER_IP>' . "\n" .
               '<ORDER_CURRENCY>' . encode_htmlspecialchars($orders['currency']) . '</ORDER_CURRENCY>' . "\n" .
               '<ORDER_CURRENCY_VALUE>' . $orders['currency_value'] . '</ORDER_CURRENCY_VALUE>' . "\n" .
               '</ORDER_HEADER>' . "\n" .
               '<BILLING_ADDRESS>' . "\n" .
               '<VAT_ID>' . encode_htmlspecialchars($orders['customers_vat_id']) . '</VAT_ID>' . "\n" . //JP07112005 (Existiert erst ab XTC 3.x)
               '<COMPANY>' . encode_htmlspecialchars($orders['billing_company']) . '</COMPANY>' . "\n" .
               '<NAME>' . encode_htmlspecialchars($orders['billing_name']) . '</NAME>' . "\n" .
               '<FIRSTNAME>' . encode_htmlspecialchars($orders['billing_firstname']) . '</FIRSTNAME>' . "\n" .
               '<LASTNAME>' . encode_htmlspecialchars($orders['billing_lastname']) . '</LASTNAME>' . "\n" .
               '<STREET>' . encode_htmlspecialchars($orders['billing_street_address']) . '</STREET>' . "\n" .
               '<POSTCODE>' . encode_htmlspecialchars($orders['billing_postcode']) . '</POSTCODE>' . "\n" .
               '<CITY>' . encode_htmlspecialchars($orders['billing_city']) . '</CITY>' . "\n" .
               '<SUBURB>' . encode_htmlspecialchars($orders['billing_suburb']) . '</SUBURB>' . "\n" .
               '<STATE>' . encode_htmlspecialchars($orders['billing_state']) . '</STATE>' . "\n" .
               '<COUNTRY>' . encode_htmlspecialchars($orders['billing_country_iso_code_2']) . '</COUNTRY>' . "\n" .
               '<TELEPHONE>' . encode_htmlspecialchars($orders['customers_telephone']) . '</TELEPHONE>' . "\n" . // JAN
               '<EMAIL>' . encode_htmlspecialchars($orders['customers_email_address']) . '</EMAIL>' . "\n" . // JAN
               '<BIRTHDAY>' . encode_htmlspecialchars($cust_dob) . '</BIRTHDAY>' . "\n" .
               '<GENDER>' . encode_htmlspecialchars($cust_gender) . '</GENDER>' . "\n" .
               '</BILLING_ADDRESS>' . "\n" .
               '<DELIVERY_ADDRESS>' . "\n" .
               '<COMPANY>' . encode_htmlspecialchars($orders['delivery_company']) . '</COMPANY>' . "\n" .
               '<NAME>' . encode_htmlspecialchars($orders['delivery_name']) . '</NAME>' . "\n" .
               '<FIRSTNAME>' . encode_htmlspecialchars($orders['delivery_firstname']) . '</FIRSTNAME>' . "\n" .
               '<LASTNAME>' . encode_htmlspecialchars($orders['delivery_lastname']) . '</LASTNAME>' . "\n" .
               '<STREET>' . encode_htmlspecialchars($orders['delivery_street_address']) . '</STREET>' . "\n" .
               '<POSTCODE>' . encode_htmlspecialchars($orders['delivery_postcode']) . '</POSTCODE>' . "\n" .
               '<CITY>' . encode_htmlspecialchars($orders['delivery_city']) . '</CITY>' . "\n" .
               '<SUBURB>' . encode_htmlspecialchars($orders['delivery_suburb']) . '</SUBURB>' . "\n" .
               '<STATE>' . encode_htmlspecialchars($orders['delivery_state']) . '</STATE>' . "\n" .
               '<COUNTRY>' . encode_htmlspecialchars($orders['delivery_country_iso_code_2']) . '</COUNTRY>' . "\n" .
               '</DELIVERY_ADDRESS>' . "\n" .
               '<PAYMENT>' . "\n" .
               '<PAYMENT_METHOD>' . encode_htmlspecialchars($orders['payment_method']) . '</PAYMENT_METHOD>'  . "\n" .
               '<PAYMENT_CLASS>' . encode_htmlspecialchars($orders['payment_class']) . '</PAYMENT_CLASS>'  . "\n";

    switch ($orders['payment_class'])
    {
      case 'banktransfer':
             // Bankverbindung laden, wenn aktiv
             $bank_name = '';
             $bank_blz  = '';
             $bank_kto  = '';
             $bank_inh  = '';
             $bank_stat = -1;

              $bank_sql = "select * from banktransfer where orders_id = " . $orders['orders_id'];
             $bank_query = xtc_db_query($bank_sql);
            if (($bank_query) && ($bankdata = xtc_db_fetch_array($bank_query)))
            {
              $bank_name = $bankdata['banktransfer_bankname'];
              $bank_blz  = $bankdata['banktransfer_blz'];
              $bank_kto  = $bankdata['banktransfer_number'];
              $bank_inh  = $bankdata['banktransfer_owner'];
              $bank_stat = $bankdata['banktransfer_status'];
            }
             $schema .= '<PAYMENT_BANKTRANS_BNAME>' . encode_htmlspecialchars($bank_name) . '</PAYMENT_BANKTRANS_BNAME>' . "\n" .
                        '<PAYMENT_BANKTRANS_BLZ>' . encode_htmlspecialchars($bank_blz) . '</PAYMENT_BANKTRANS_BLZ>' . "\n" .
                        '<PAYMENT_BANKTRANS_NUMBER>' . encode_htmlspecialchars($bank_kto) . '</PAYMENT_BANKTRANS_NUMBER>' . "\n" .
                        '<PAYMENT_BANKTRANS_OWNER>' . encode_htmlspecialchars($bank_inh) . '</PAYMENT_BANKTRANS_OWNER>' . "\n" .
                        '<PAYMENT_BANKTRANS_STATUS>' . encode_htmlspecialchars($bank_stat) . '</PAYMENT_BANKTRANS_STATUS>' . "\n";
             break;
    }
    $schema .= '</PAYMENT>' . "\n" .
               '<SHIPPING>' . "\n" .
               '<SHIPPING_METHOD>' . encode_htmlspecialchars($orders['shipping_method']) . '</SHIPPING_METHOD>'  . "\n" .
               '<SHIPPING_CLASS>' . encode_htmlspecialchars($orders['shipping_class']) . '</SHIPPING_CLASS>'  . "\n" .
               '</SHIPPING>' . "\n" .
               '<ORDER_PRODUCTS>' . "\n";

    $sql = "select
             orders_products_id,
             allow_tax,
             products_id,
             products_model,
             products_name,
             final_price,
             products_tax,
             products_quantity
            from " .
             TABLE_ORDERS_PRODUCTS . "
            where
             orders_id = '" . $orders['orders_id'] . "'";

    $products_query = xtc_db_query($sql);
    while ($products = xtc_db_fetch_array($products_query))
    {
      if ($products['allow_tax']==1) $products['final_price']=$products['final_price']/(1+$products['products_tax']*0.01);
      $schema .= '<PRODUCT>' . "\n" .
                 '<PRODUCTS_ID>' . $products['products_id'] . '</PRODUCTS_ID>' . "\n" .
                 '<PRODUCTS_QUANTITY>' . $products['products_quantity'] . '</PRODUCTS_QUANTITY>' . "\n" .
                 '<PRODUCTS_MODEL>' . encode_htmlspecialchars($products['products_model']) . '</PRODUCTS_MODEL>' . "\n" .
                 '<PRODUCTS_NAME>' . encode_htmlspecialchars($products['products_name']) . '</PRODUCTS_NAME>' . "\n" .
                 '<PRODUCTS_PRICE>' . $products['final_price']/$products['products_quantity'] . '</PRODUCTS_PRICE>' . "\n" .
                 '<PRODUCTS_TAX>' . $products['products_tax'] . '</PRODUCTS_TAX>' . "\n".
                 '<PRODUCTS_TAX_FLAG>' . $products['allow_tax'] . '</PRODUCTS_TAX_FLAG>' . "\n";

      $attributes_query = xtc_db_query("select products_options, products_options_values, options_values_price, attributes_model, price_prefix from " . TABLE_ORDERS_PRODUCTS_ATTRIBUTES . " where orders_id = '" .$orders['orders_id'] . "' and orders_products_id = '" . $products['orders_products_id'] . "'");
      if (xtc_db_num_rows($attributes_query))
      {
        while ($attributes = xtc_db_fetch_array($attributes_query))
        {
          $schema .= '<OPTION>' . "\n" .
                     '<PRODUCTS_OPTIONS>' . encode_htmlspecialchars($attributes['products_options']) . '</PRODUCTS_OPTIONS>' . "\n" .
                     '<PRODUCTS_OPTIONS_VALUES>' . encode_htmlspecialchars($attributes['products_options_values']) . '</PRODUCTS_OPTIONS_VALUES>' . "\n" .
                     '<PRODUCTS_OPTIONS_MODEL>' . encode_htmlspecialchars($attributes['attributes_model']) . '</PRODUCTS_OPTIONS_MODEL>'. "\n".
                     '<PRODUCTS_OPTIONS_PRICE>' . $attributes['price_prefix'] . ' ' . $attributes['options_values_price'] . '</PRODUCTS_OPTIONS_PRICE>' . "\n" .
                     '</OPTION>' . "\n";
        }
      }
      $schema .=  '</PRODUCT>' . "\n";      
    }
    $schema .= '</ORDER_PRODUCTS>' . "\n";
    $schema .= '<ORDER_TOTAL>' . "\n";

    $totals_query = xtc_db_query("select title, value, class, sort_order from " . TABLE_ORDERS_TOTAL . " where orders_id = '" . $orders['orders_id'] . "' order by sort_order");
    while ($totals = xtc_db_fetch_array($totals_query))
    {
      $total_prefix = "";
      $total_tax  = "";
      $total_prefix = $order_total_class[$totals['class']]['prefix'];
      $total_tax = $order_total_class[$totals['class']]['tax'];
      $schema .= '<TOTAL>' . "\n" .
                 '<TOTAL_TITLE>' . encode_htmlspecialchars($totals['title']) . '</TOTAL_TITLE>' . "\n" .
                 '<TOTAL_VALUE>' . encode_htmlspecialchars($totals['value']) . '</TOTAL_VALUE>' . "\n" .
                 '<TOTAL_CLASS>' . encode_htmlspecialchars($totals['class']) . '</TOTAL_CLASS>' . "\n" .
                 '<TOTAL_SORT_ORDER>' . encode_htmlspecialchars($totals['sort_order']) . '</TOTAL_SORT_ORDER>' . "\n" .
                 '<TOTAL_PREFIX>' . encode_htmlspecialchars($total_prefix) . '</TOTAL_PREFIX>' . "\n" .
                 '<TOTAL_TAX>' . encode_htmlspecialchars($total_tax) . '</TOTAL_TAX>' . "\n" .
                 '</TOTAL>' . "\n";
    }
    $schema .= '</ORDER_TOTAL>' . "\n";

    /*
    $sql = "select
             comments
            from " .
             TABLE_ORDERS_STATUS_HISTORY . "
            where
             orders_id = '" . $orders['orders_id'] . "' and
             orders_status_id = '" . $orders['orders_status'] . "' ";

    $comments_query = xtc_db_query($sql);
    if ($comments =  xtc_db_fetch_array($comments_query))
    {
      $schema .=  '<ORDER_COMMENTS>' . encode_htmlspecialchars($comments['comments']) . '</ORDER_COMMENTS>' . "\n";
    }
    */

    //Es werden jetzt alle Kommentare mit �bertragen, nicht nur der letzte
    //JP 2008-12-15
    $comments_query = "SELECT comments FROM " . TABLE_ORDERS_STATUS_HISTORY .
                      " WHERE orders_id = '" . $orders['orders_id'] ."'";
    $comments_result = xtc_db_query ($comments_query);
    $schema .=  '<ORDER_COMMENTS>';
    $oc='';
    while ($comments = xtc_db_fetch_array($comments_result))
    {
      if (strlen($oc)>0)
      {$oc .="\r\n"; }
     $oc .= encode_htmlspecialchars($comments['comments']);
    }
    $schema .=  $oc . '</ORDER_COMMENTS>' . "\n";

    $schema .= '</ORDER_INFO>' . "\n\n";
    echo $schema;
  }
  $schema = '</ORDER>' . "\n\n";
  echo $schema;
}

//--------------------------------------------------------------

function SendProducts ()
{
  global $_GET, $LangID;

  if (defined('SET_TIME_LIMIT')) { @set_time_limit(0);}

  $schema = '<?xml version="1.0" encoding="' . CHARSET . '"?>' . "\n" .
            '<PRODUCTS>' . "\n";
  echo $schema;

  $sql = "select p.products_id,products_fsk18, products_quantity, products_model, products_image, products_price, " .
         "products_ean, products_date_added, products_last_modified, products_date_available, products_weight, " .
         "products_status, products_tax_class_id, manufacturers_id, products_ordered";

  if ((defined('TABLE_PRODUCTS_IMAGES')) and (USE_3IMAGES==true))
  {
    $sql .= ",pi1.image_name as image_1, pi2.image_name as image_2";
  }

  if ((defined('TABLE_PRODUCTS_VPE')) and (USE_VPE==true))
  {
    $sql .=",products_vpe_name";
  }

  $sql .=" from " . TABLE_PRODUCTS . " as p ";

  if ((defined('TABLE_PRODUCTS_IMAGES')) and (USE_3IMAGES==true))
  {
    $sql .= "left outer join ".TABLE_PRODUCTS_IMAGES." pi1 on pi1.products_id = p.products_id and pi1.image_nr=1 " .
            "left outer join ".TABLE_PRODUCTS_IMAGES." pi2 on pi2.products_id = p.products_id and pi2.image_nr=2 ";
  }

  if ((defined('TABLE_PRODUCTS_VPE')) and (USE_VPE==true))
  {
    $sql .="left outer join ".TABLE_PRODUCTS_VPE." on p.products_vpe = ".TABLE_PRODUCTS_VPE.".products_vpe_id and ".TABLE_PRODUCTS_VPE.".language_id=".$LangID;
  }


  $from = xtc_db_prepare_input($_GET['products_from']);
  $anz  = xtc_db_prepare_input($_GET['products_count']);
  if (isset($from))
  {
    if (!isset($anz)) $anz=1000;
    $sql .= " limit " . $from . "," . $anz;
  }

  $orders_query = xtc_db_query($sql);
  while ($products = xtc_db_fetch_array($orders_query))
  {
    $schema  = '<PRODUCT_INFO>' . "\n" .
               '<PRODUCT_DATA>' . "\n" .
               '<PRODUCT_ID>'.$products['products_id'].'</PRODUCT_ID>' . "\n" .
/*               '<PRODUCT_DEEPLINK>'. HTTP_SERVER.DIR_WS_CATALOG.$xtc_filename['product_info'].'?products_id='.$products['products_id'].'</PRODUCT_DEEPLINK>' . "\n" .*/
               '<PRODUCT_QUANTITY>' . $products['products_quantity'] . '</PRODUCT_QUANTITY>' . "\n" .
               '<PRODUCT_MODEL>' . encode_htmlspecialchars($products['products_model']) . '</PRODUCT_MODEL>' . "\n" .
               '<PRODUCT_FSK18>' . encode_htmlspecialchars($products['products_fsk18']) . '</PRODUCT_FSK18>' . "\n" .
               '<PRODUCT_IMAGE>' . encode_htmlspecialchars($products['products_image']) . '</PRODUCT_IMAGE>' . "\n" .
               '<PRODUCT_EAN>'   . encode_htmlspecialchars($products['products_ean']) . '</PRODUCT_EAN>' . "\n";

    if ((defined('TABLE_PRODUCTS_IMAGES')) and (USE_3IMAGES==true))
    {
      $schema .= '<PRODUCT_IMAGE_MED>'  . encode_htmlspecialchars($products['image_1']) . '</PRODUCT_IMAGE_MED>' . "\n" .
                 '<PRODUCT_IMAGE_LARGE>'. encode_htmlspecialchars($products['image_2']) . '</PRODUCT_IMAGE_LARGE>' . "\n";
    }

    if ((defined('TABLE_PRODUCTS_VPE')) and (USE_VPE==true))
    {
      $schema .= '<PRODUCT_VPE>'.encode_htmlspecialchars($products['products_vpe_name']) . '</PRODUCT_VPE>' . "\n";
    }

    if (file_exists('cao_sendprod_1.php')) { include('cao_sendprod_1.php'); }

 /* Wird von CAO derzeit nicht verwendet !!!

    if ($products['products_image']!='')
    {
      $schema .= '<PRODUCT_IMAGE_POPUP>'.HTTP_SERVER.DIR_WS_CATALOG.DIR_WS_POPUP_IMAGES.$products['products_image'].'</PRODUCT_IMAGE_POPUP>'. "\n" .
                 '<PRODUCT_IMAGE_SMALL>'.HTTP_SERVER.DIR_WS_CATALOG.DIR_WS_INFO_IMAGES.$products['products_image'].'</PRODUCT_IMAGE_SMALL>'. "\n" .
                 '<PRODUCT_IMAGE_THUMBNAIL>'.HTTP_SERVER.DIR_WS_CATALOG.DIR_WS_THUMBNAIL_IMAGES.$products['products_image'].'</PRODUCT_IMAGE_THUMBNAIL>'. "\n" .
                 '<PRODUCT_IMAGE_ORIGINAL>'.HTTP_SERVER.DIR_WS_CATALOG.DIR_WS_ORIGINAL_IMAGES.$products['products_image'].'</PRODUCT_IMAGE_ORIGINAL>'. "\n";
    }

*/

    $schema .= '<PRODUCT_PRICE>' . $products['products_price'] . '</PRODUCT_PRICE>' . "\n";

    /* Wird von CAO derzeit nicht verwendet !!!


    require_once(DIR_FS_INC .'xtc_get_customers_statuses.inc.php');

    $customers_status=xtc_get_customers_statuses();
    for ($i=1,$n=sizeof($customers_status);$i<$n; $i++)
    {
      if ($customers_status[$i]['id']!=0)
      {
        $schema .= "<PRODUCT_GROUP_PRICES ID='".$customers_status[$i]['id']."' NAME='".$customers_status[$i]['text']. "'>". "\n";
        $group_price_query=xtc_db_query("SELECT * FROM personal_offers_by_customers_status_".$customers_status[$i]['id'].
                                        " WHERE products_id='" . $products_id . "'");
        while ($group_price_data=xtc_db_fetch_array($group_price_query))
        {
          //if ($group_price_data['personal_offer']!='0')
          //{
          $schema .='<PRICE_ID>'.$group_price_data['price_id'].'</PRICE_ID>';
          $schema .='<PRODUCT_ID>'.$group_price_data['products_id'].'</PRODUCT_ID>';
          $schema .='<QTY>'.$group_price_data['quantity'].'</QTY>';
          $schema .='<PRICE>'.$group_price_data['personal_offer'].'</PRICE>';
          //}
        }
        $schema .= "</PRODUCT_GROUP_PRICES>\n";
      }
    }
    // products Options

    $products_attributes='';
    $products_options_data=array();
    $products_options_array =array();
    $products_attributes_query = xtc_db_query("select count(*) as total
                                               from " . TABLE_PRODUCTS_OPTIONS . "
                                               popt, " . TABLE_PRODUCTS_ATTRIBUTES . "
                                               patrib where
                                               patrib.products_id='" . $products['products_id'] . "'
                                               and patrib.options_id = popt.products_options_id
                                               and popt.language_id = '" . $LangID . "'");

    $products_attributes = xtc_db_fetch_array($products_attributes_query);

    if ($products_attributes['total'] > 0)
    {
      $products_options_name_query = xtc_db_query("select distinct
                                                   popt.products_options_id,
                                                   popt.products_options_name
                                                   from " . TABLE_PRODUCTS_OPTIONS . "
                                                   popt, " . TABLE_PRODUCTS_ATTRIBUTES . " patrib
                                                   where patrib.products_id='" . $products['products_id'] . "'
                                                   and patrib.options_id = popt.products_options_id
                                                   and popt.language_id = '" . $LangID . "' order by popt.products_options_name");
      $row = 0;
      $col = 0;
      $products_options_data=array();
      while ($products_options_name = xtc_db_fetch_array($products_options_name_query))
      {
        $selected = 0;
        $products_options_array = array();
        $products_options_data[$row]=array(
                       'NAME'=>$products_options_name['products_options_name'],
                       'ID' => $products_options_name['products_options_id'],
                       'DATA' =>'');
        $products_options_query = xtc_db_query("select
                                                 pov.products_options_values_id,
                                                 pov.products_options_values_name,
                                                 pa.attributes_model,
                                                 pa.options_values_price,
                                                 pa.options_values_weight,
                                                 pa.price_prefix,
                                                 pa.weight_prefix,
                                                 pa.attributes_stock,
                                                 pa.attributes_model
                                                from " . TABLE_PRODUCTS_ATTRIBUTES . "
                                                 pa, " . TABLE_PRODUCTS_OPTIONS_VALUES . "
                                                 pov
                                                where
                                                 pa.products_id = '" . $products['products_id'] . "'
                                                 and pa.options_id = '" . $products_options_name['products_options_id'] . "' and
                                                 pa.options_values_id = pov.products_options_values_id and
                                                 pov.language_id = '" . $LangID . "'
                                                order by pov.products_options_values_name");
        $col = 0;
        while ($products_options = xtc_db_fetch_array($products_options_query))
        {
          $products_options_array[] = array('id' => $products_options['products_options_values_id'], 'text' => $products_options['products_options_values_name']);
          if ($products_options['options_values_price'] != '0')
          {
            $products_options_array[sizeof($products_options_array)-1]['text'] .=  ' '.$products_options['price_prefix'].' '.$products_options['options_values_price'].' '.$_SESSION['currency'] ;
          }
          $price='';
          $products_options_data[$row]['DATA'][$col]=array(
                                    'ID' => $products_options['products_options_values_id'],
                                    'TEXT' =>$products_options['products_options_values_name'],
                                    'MODEL' =>$products_options['attributes_model'],
                                    'WEIGHT' =>$products_options['options_values_weight'],
                                    'PRICE' =>$products_options['options_values_price'],
                                    'WEIGHT_PREFIX' =>$products_options['weight_prefix'],
                                    'PREFIX' =>$products_options['price_prefix']);
          $col++;
        }
        $row++;
      }
    }
    if (sizeof($products_options_data)!=0)
    {
      for ($i=0,$n=sizeof($products_options_data);$i<$n;$i++)
      {
        $schema .= "<PRODUCT_ATTRIBUTES NAME='".$products_options_data[$i]['NAME']."'>";
        for ($ii=0,$nn=sizeof($products_options_data[$i]['DATA']);$ii<$nn;$ii++)
        {
          $schema .= '<OPTION>';
          $schema .= '<ID>'.$products_options_data[$i]['DATA'][$ii]['ID'].'</ID>';
          $schema .= '<MODEL>'.$products_options_data[$i]['DATA'][$ii]['MODEL'].'</MODEL>';
          $schema .= '<TEXT>'.$products_options_data[$i]['DATA'][$ii]['TEXT'].'</TEXT>';
          $schema .= '<WEIGHT>'.$products_options_data[$i]['DATA'][$ii]['WEIGHT'].'</WEIGHT>';
          $schema .= '<PRICE>'.$products_options_data[$i]['DATA'][$ii]['PRICE'].'</PRICE>';
          $schema .= '<WEIGHT_PREFIX>'.$products_options_data[$i]['DATA'][$ii]['WEIGHT_PREFIX'].'</WEIGHT_PREFIX>';
          $schema .= '<PREFIX>'.$products_options_data[$i]['DATA'][$ii]['PREFIX'].'</PREFIX>';
          $schema .= '</OPTION>';
        }
        $schema .= '</PRODUCT_ATTRIBUTES>';
      }
    }
    */

    require_once(DIR_FS_INC .'xtc_get_tax_rate.inc.php');

    if (SWITCH_MWST=='true')
    {
      // switch IDs
      if ($products['products_tax_class_id']==1)
      {
        $products['products_tax_class_id']=2;
      }
        else
      {
        if ($products['products_tax_class_id']==2)
        {
          $products['products_tax_class_id']=1;
        }
      }
    }

    $schema .= '<PRODUCT_WEIGHT>' . $products['products_weight'] . '</PRODUCT_WEIGHT>' . "\n" .
               '<PRODUCT_STATUS>' . $products['products_status'] . '</PRODUCT_STATUS>' . "\n" .
               '<PRODUCT_TAX_CLASS_ID>' . $products['products_tax_class_id'] . '</PRODUCT_TAX_CLASS_ID>' . "\n"  .
               '<PRODUCT_TAX_RATE>' . xtc_get_tax_rate($products['products_tax_class_id']) . '</PRODUCT_TAX_RATE>' . "\n"  .
               '<MANUFACTURERS_ID>' . $products['manufacturers_id'] . '</MANUFACTURERS_ID>' . "\n" .
               '<PRODUCT_DATE_ADDED>' . $products['products_date_added'] . '</PRODUCT_DATE_ADDED>' . "\n" .
               '<PRODUCT_LAST_MODIFIED>' . $products['products_last_modified'] . '</PRODUCT_LAST_MODIFIED>' . "\n" .
               '<PRODUCT_DATE_AVAILABLE>' . $products['products_date_available'] . '</PRODUCT_DATE_AVAILABLE>' . "\n" .
               '<PRODUCTS_ORDERED>' . $products['products_ordered'] . '</PRODUCTS_ORDERED>' . "\n" ;

   // NEU JP 26.08.2005 Aktionspreise exportieren
   $special_query = "SELECT * from " . TABLE_SPECIALS . " " .
                    "where products_id=" . $products['products_id'] . " limit 0,1";

   $special_result = xtc_db_query($special_query);
   
   if ($specials = xtc_db_fetch_array($special_result)) {

   $schema .='<SPECIAL_PRICE>' . $specials['specials_new_products_price'] . '</SPECIAL_PRICE>' . "\n" .
               '<SPECIAL_DATE_ADDED>' . $specials['specials_date_added'] . '</SPECIAL_DATE_ADDED>' . "\n" .
               '<SPECIAL_LAST_MODIFIED>' . $specials['specials_last_modified'] . '</SPECIAL_LAST_MODIFIED>' . "\n" .
               '<SPECIAL_DATE_EXPIRES>' . $specials['expires_date'] . '</SPECIAL_DATE_EXPIRES>' . "\n" .
               '<SPECIAL_STATUS>' . $specials['status'] . '</SPECIAL_STATUS>' . "\n" .
               '<SPECIAL_DATE_STATUS_CHANGE>' . $specials['date_status_change'] . '</SPECIAL_DATE_STATUS_CHANGE>' . "\n";
	} else {
   $schema .='<SPECIAL_PRICE></SPECIAL_PRICE>' . "\n" .
               '<SPECIAL_DATE_ADDED></SPECIAL_DATE_ADDED>' . "\n" .
               '<SPECIAL_LAST_MODIFIED></SPECIAL_LAST_MODIFIED>' . "\n" .
               '<SPECIAL_DATE_EXPIRES></SPECIAL_DATE_EXPIRES>' . "\n" .
               '<SPECIAL_STATUS></SPECIAL_STATUS>' . "\n" .
               '<SPECIAL_DATE_STATUS_CHANGE></SPECIAL_DATE_STATUS_CHANGE>' . "\n";
	}
	
    // Ende Aktionspreise

    /* Wird von CAO derzeit nicht verwendet !!!

    $categories_query=xtc_db_query("SELECT
                                     categories_id
                                    FROM ".TABLE_PRODUCTS_TO_CATEGORIES."
                                     where products_id='".$products['products_id']."'");
    $categories=array();
    while ($categories_data=xtc_db_fetch_array($categories_query))
    {
      $categories[]=$categories_data['categories_id'];
    }
    $categories=implode(',',$categories);

    $schema .= '<PRODUCTS_CATEGORIES>' . $categories . '</PRODUCTS_CATEGORIES>' . "\n" ;

    */

    $detail_query = xtc_db_query("select
                                   products_id,
                                   language_id,
                                   products_name, " . TABLE_PRODUCTS_DESCRIPTION .
                           ".products_description,
                                   products_short_description,
                                   products_meta_title,
                                   products_meta_description,
                                   products_meta_keywords,
                                   products_url,
                                   name as language_name, code as language_code " .
                                   "from " . TABLE_PRODUCTS_DESCRIPTION . ", " . TABLE_LANGUAGES . " " .
                                   "where " . TABLE_PRODUCTS_DESCRIPTION . ".language_id=" . TABLE_LANGUAGES . ".languages_id " .
                                   "and " . TABLE_PRODUCTS_DESCRIPTION . ".products_id=" . $products['products_id']);

    while ($details = xtc_db_fetch_array($detail_query))
    {
      $schema .= "<PRODUCT_DESCRIPTION ID='" . $details["language_id"] ."' CODE='" . $details["language_code"] . "' NAME='" . $details["language_name"] . "'>\n";

      if ($details["products_name"] !='Array')
      {
        $schema .= "<NAME>" . encode_htmlspecialchars($details["products_name"]) . "</NAME>" . "\n" ;
      }
      $schema .=  "<URL>" . encode_htmlspecialchars($details["products_url"]) . "</URL>" . "\n" ;

      $prod_details = preg_replace('/[\x00-\x1f]/', '',$details["products_description"]);
      if ($prod_details != 'Array')
      {
        $schema .=  "<DESCRIPTION>" . encode_htmlspecialchars($prod_details) . "</DESCRIPTION>" . "\n";
        $schema .=  "<SHORT_DESCRIPTION>" . encode_htmlspecialchars($details["products_short_description"]) . "</SHORT_DESCRIPTION>" . "\n";
        $schema .=  "<META_TITLE>" . encode_htmlspecialchars($details["products_meta_title"]) . "</META_TITLE>" . "\n";
        $schema .=  "<META_DESCRIPTION>" . encode_htmlspecialchars($details["products_meta_description"]) . "</META_DESCRIPTION>" . "\n";
        $schema .=  "<META_KEYWORDS>" . encode_htmlspecialchars($details["products_meta_keywords"]) . "</META_KEYWORDS>" . "\n";
      }
      $schema .= "</PRODUCT_DESCRIPTION>\n";
    }

    $schema .= '</PRODUCT_DATA>' . "\n" .
               '</PRODUCT_INFO>' . "\n";
    echo $schema;
  }
  $schema = '</PRODUCTS>' . "\n\n";
  echo $schema;
}

//--------------------------------------------------------------

function SendCustomers ()
{
  global $_GET;

  if (defined('SET_TIME_LIMIT')) { @set_time_limit(0);}

  $schema = '<?xml version="1.0" encoding="' . CHARSET . '"?>' . "\n" .
            '<CUSTOMERS>' . "\n";
  echo $schema;

  $from = xtc_db_prepare_input($_GET['customers_from']);
  $anz  = xtc_db_prepare_input($_GET['customers_count']);

  $address_query = "select
                    c.customers_gender,
                    c.customers_id,
                    c.customers_cid,
                    c.customers_dob,
                    c.customers_email_address,
                    c.customers_telephone,
                    c.customers_fax,
                    c.customers_status,";

  //JAN Pruefen, ob mind. Version 3.x vom XTC und dann wenn das Feld existiert, dieses mit Abfragen
  $res=xtc_db_query('show fields from ' . TABLE_CUSTOMERS . ' like "customers_vat_id"');
  if (xtc_db_fetch_array($res)) {  $address_query .= "c.customers_vat_id as vat_id,"; }

  $address_query .= "ci.customers_info_date_account_created,
                    a.entry_firstname,
                    a.entry_lastname,
                    a.entry_company,
                    a.entry_street_address,
                    a.entry_city,
                    a.entry_postcode,
                    a.entry_suburb,
                    a.entry_state,
                    co.countries_iso_code_2
                   from
                    " . TABLE_CUSTOMERS . " c,
                    " . TABLE_CUSTOMERS_INFO . " ci,
                    " . TABLE_ADDRESS_BOOK . " a ,
                    " . TABLE_COUNTRIES . " co
                   where
                    c.customers_id = ci.customers_info_id AND
                    c.customers_id = a.customers_id AND
                    c.customers_default_address_id = a.address_book_id AND
                    a.entry_country_id  = co.countries_id";

  if (isset($from))
  {
    if (!isset($anz)) $anz = 1000;
    $address_query.= " limit " . $from . "," . $anz;
  }
  $address_result = xtc_db_query($address_query);

  while ($address = xtc_db_fetch_array($address_result))
  {
    $schema = '<CUSTOMERS_DATA>' . "\n" .
              '<CUSTOMERS_ID>' . encode_htmlspecialchars($address['customers_id']) . '</CUSTOMERS_ID>' . "\n" .
              '<CUSTOMERS_CID>' . encode_htmlspecialchars($address['customers_cid']) . '</CUSTOMERS_CID>' . "\n" .
              '<GENDER>' . encode_htmlspecialchars($address['customers_gender']) . '</GENDER>' . "\n" .
              '<COMPANY>' . encode_htmlspecialchars($address['entry_company']) . '</COMPANY>' . "\n" .
              '<FIRSTNAME>' . encode_htmlspecialchars($address['entry_firstname']) . '</FIRSTNAME>' . "\n" .
              '<LASTNAME>' . encode_htmlspecialchars($address['entry_lastname']) . '</LASTNAME>' . "\n" .
              '<STREET>' . encode_htmlspecialchars($address['entry_street_address']) . '</STREET>' . "\n" .
              '<POSTCODE>' . encode_htmlspecialchars($address['entry_postcode']) . '</POSTCODE>' . "\n" .
              '<CITY>' . encode_htmlspecialchars($address['entry_city']) . '</CITY>' . "\n" .
              '<SUBURB>' . encode_htmlspecialchars($address['entry_suburb']) . '</SUBURB>' . "\n" .
              '<STATE>' . encode_htmlspecialchars($address['entry_state']) . '</STATE>' . "\n" .
              '<COUNTRY>' . encode_htmlspecialchars($address['countries_iso_code_2']) . '</COUNTRY>' . "\n" .
              '<TELEPHONE>' . encode_htmlspecialchars($address['customers_telephone']) . '</TELEPHONE>' . "\n" . // JAN
              '<FAX>' . encode_htmlspecialchars($address['customers_fax']) . '</FAX>' . "\n" . // JAN
              '<EMAIL>' . encode_htmlspecialchars($address['customers_email_address']) . '</EMAIL>' . "\n" . // JAN
              '<BIRTHDAY>' . encode_htmlspecialchars($address['customers_dob']) . '</BIRTHDAY>' . "\n" .
              '<VAT_ID>' . encode_htmlspecialchars($address['vat_id']) . '</VAT_ID>' . "\n" .
              '<DATE_ACCOUNT_CREATED>' . encode_htmlspecialchars($address['customers_info_date_account_created']) . '</DATE_ACCOUNT_CREATED>' . "\n";

    if (file_exists('cao_sendcust_1.php')) { include('cao_sendcust_1.php'); }

    $schema .=  '</CUSTOMERS_DATA>' . "\n";
    echo $schema;
  }
  $schema = '</CUSTOMERS>' . "\n\n";
  echo $schema;
}

//--------------------------------------------------------------

function SendCustomersNewsletter ()
{
  global $_GET;

  if (defined('SET_TIME_LIMIT')) { @set_time_limit(0);}

  $schema = '<?xml version="1.0" encoding="' . CHARSET . '"?>' . "\n" .
            '<CUSTOMERS>' . "\n".

  $from = xtc_db_prepare_input($_GET['customers_from']);
  $anz  = xtc_db_prepare_input($_GET['customers_count']);

  $address_query = "select *
                    from " . TABLE_CUSTOMERS. "
                    where customers_newsletter = 1";

  if (isset($from))
  {
    if (!isset($anz)) $anz = 1000;
    $address_query.= " limit " . $from . "," . $anz;
  }
  $address_result = xtc_db_query($address_query);
  while ($address = xtc_db_fetch_array($address_result))
  {
    $schema .= '<CUSTOMERS_DATA>' . "\n";
    $schema .= '<CUSTOMERS_ID>' . $address['customers_id'] . '</CUSTOMERS_ID>' . "\n";
    $schema .= '<CUSTOMERS_CID>' . $address['customers_cid'] . '</CUSTOMERS_CID>' . "\n";
    $schema .= '<CUSTOMERS_GENDER>' . $address['customers_gender'] . '</CUSTOMERS_GENDER>' . "\n";
    $schema .= '<CUSTOMERS_FIRSTNAME>' . $address['customers_firstname'] . '</CUSTOMERS_FIRSTNAME>' . "\n";
    $schema .= '<CUSTOMERS_LASTNAME>' . $address['customers_lastname'] . '</CUSTOMERS_LASTNAME>' . "\n";
    $schema .= '<CUSTOMERS_EMAIL_ADDRESS>' . $address['customers_email_address'] . '</CUSTOMERS_EMAIL_ADDRESS>' . "\n";
    $schema .= '</CUSTOMERS_DATA>' . "\n";
  }
  $schema .= '</CUSTOMERS>' . "\n\n";
  echo $schema;
}

//--------------------------------------------------------------

function SendShopConfig ()
{
  $schema = '<?xml version="1.0" encoding="' . CHARSET . '"?>' . "\n" .
            '<CONFIG>' . "\n" .
            '<CONFIG_DATA>' . "\n" ;
  echo $schema;

  $config_sql = 'select * from configuration';
  $config_res = xtc_db_query($config_sql);

  while ($config = xtc_db_fetch_array($config_res))
  {
    $schema = '<ENTRY ID="' . $config['configuration_id'] . '">' .  "\n" .
             '<PARAM>' . encode_htmlspecialchars($config['configuration_key']) . '</PARAM>' . "\n" .
             '<VALUE>' . encode_htmlspecialchars($config['configuration_value']) . '</VALUE>' . "\n" .
             '<TITLE>' . encode_htmlspecialchars($config['configuration_title']) . '</TITLE>' . "\n" .
             '<DESCRIPTION>' . encode_htmlspecialchars($config['configuration_description']) . '</DESCRIPTION>' . "\n" .
             '<GROUP_ID>' . encode_htmlspecialchars($config['config_group_id']) . '</GROUP_ID>' . "\n" .
             '<SORT_ORDER>' . encode_htmlspecialchars($config['sort_order']) . '</SORT_ORDER>' . "\n" .
             '<USE_FUNCTION>' . encode_htmlspecialchars($config['use_function']) . '</USE_FUNCTION>' . "\n" .
             '<SET_FUNCTION>' . encode_htmlspecialchars($config['set_function']) . '</SET_FUNCTION>' . "\n" .
             '</ENTRY>' . "\n";
    echo $schema;
  }
  $schema = '</CONFIG_DATA>' . "\n";
  echo $schema;


  $schema = '<TAX_CLASS>' . "\n";
  echo $schema;

  $tax_class_sql = 'select * from tax_class';
  $tax_class_res = xtc_db_query($tax_class_sql);

  while ($tax_class = xtc_db_fetch_array($tax_class_res))
  {
    $schema = '<CLASS ID="' . $tax_class['tax_class_id'] . '">' . "\n" .
             '<TITLE>' .         encode_htmlspecialchars($tax_class['tax_class_title']) .       '</TITLE>' . "\n" .
             '<DESCRIPTION>' .   encode_htmlspecialchars($tax_class['tax_class_description']) . '</DESCRIPTION>' . "\n" .
             '<LAST_MODIFIED>' . encode_htmlspecialchars($tax_class['last_modified']) .         '</LAST_MODIFIED>' . "\n" .
             '<DATE_ADDED>' .    encode_htmlspecialchars($tax_class['date_added']) .            '</DATE_ADDED>' . "\n" .
              '</CLASS>'. "\n";
    echo $schema;
  }

  $schema = '</TAX_CLASS>' . "\n";
  echo $schema;
  $schema = '<TAX_RATES>' . "\n";
  echo $schema;

  $tax_rates_sql = 'select * from tax_rates';
  $tax_rates_res = xtc_db_query($tax_rates_sql);

  while ($tax_rates = xtc_db_fetch_array($tax_rates_res))
  {
    $schema = '<RATES ID="' . $tax_rates['tax_rates_id'] . '">' . "\n" .
              '<ZONE_ID>' .       encode_htmlspecialchars($tax_rates['tax_zone_id']) .     '</ZONE_ID>' . "\n" .
              '<CLASS_ID>' .      encode_htmlspecialchars($tax_rates['tax_class_id']) .    '</CLASS_ID>' . "\n" .
              '<PRIORITY>' .      encode_htmlspecialchars($tax_rates['tax_priority']) .    '</PRIORITY>' . "\n" .
              '<RATE>' .          encode_htmlspecialchars($tax_rates['tax_rate']) .        '</RATE>' . "\n" .
              '<DESCRIPTION>' .   encode_htmlspecialchars($tax_rates['tax_description']) . '</DESCRIPTION>' . "\n" .
              '<LAST_MODIFIED>' . encode_htmlspecialchars($tax_rates['last_modified']) .   '</LAST_MODIFIED>' . "\n" .
              '<DATE_ADDED>' .    encode_htmlspecialchars($tax_rates['date_added']) .      '</DATE_ADDED>' . "\n" .
              '</RATES>' . "\n";
    echo $schema;
  }
  $schema = '</TAX_RATES>' . "\n";
  echo $schema;

  //Ausgabe ProductListingTemplates
  $schema = '<PRODUCT_LISTING_TEMPLATES>' . "\n";
  if ($dir = opendir(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/module/product_listing/'))
  {
      while (($file = readdir($dir)) != false)
      {
// BOF - Tomcraft - 2010-02-04 - Prevent modified eCommerce Shopsoftware from fetching other files than *.html
/*
          if (is_file(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/module/product_listing/'.$file) and
             ($file != "index.html"))
         {
*/
          if (is_file(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/module/product_listing/'.$file) and (substr($file, -5) == ".html") and ($file != "index.html") and (substr($file, 0, 1) !=".")) {
// EOF - Tomcraft - 2010-02-04 - Prevent modified eCommerce Shopsoftware from fetching other files than *.html
             $schema .= "<TEMPLATE>" . $file . "</TEMPLATE>\n";
         } //if
     } // while
     closedir($dir);
  }
  $schema .= '</PRODUCT_LISTING_TEMPLATES>' . "\n";
  echo $schema;

  //Ausgabe ProductInfoTemplates
  $schema = '<PRODUCT_DETAILS_TEMPLATES>' . "\n";
  if ($dir = opendir(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/module/product_info/'))
  {
      while (($file = readdir($dir)) != false)
      {
// BOF - Tomcraft - 2010-02-04 - Prevent modified eCommerce Shopsoftware from fetching other files than *.html
/*
          if (is_file(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/module/product_info/'.$file) and
             ($file != "index.html"))
         {
*/
          if (is_file(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/module/product_info/'.$file) and (substr($file, -5) == ".html") and ($file != "index.html") and (substr($file, 0, 1) !=".")) {
// EOF - Tomcraft - 2010-02-04 - Prevent modified eCommerce Shopsoftware from fetching other files than *.html
             $schema .= "<TEMPLATE>" . $file . "</TEMPLATE>\n";
         } //if
     } // while
     closedir($dir);
  }
  $schema .= '</PRODUCT_DETAILS_TEMPLATES>' . "\n";
  echo $schema;

  //Ausgabe ProductOptionsTemplates
  $schema = '<PRODUCT_OPTIONS_TEMPLATES>' . "\n";
  if ($dir = opendir(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/module/product_options/'))
  {
      while (($file = readdir($dir)) != false)
      {
// BOF - Tomcraft - 2010-02-04 - Prevent modified eCommerce Shopsoftware from fetching other files than *.html
/*
          if (is_file(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/module/product_options/'.$file) and
             ($file != "index.html"))
         {
*/
           if (is_file(DIR_FS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/module/product_options/'.$file) and (substr($file, -5) == ".html") and ($file != "index.html") and (substr($file, 0, 1) !=".")) {
// EOF - Tomcraft - 2010-02-04 - Prevent modified eCommerce Shopsoftware from fetching other files than *.html
             $schema .= "<TEMPLATE>" . $file . "</TEMPLATE>\n";
         } //if
     } // while
     closedir($dir);
  }
  $schema .= '</PRODUCT_OPTIONS_TEMPLATES>' . "\n";
  echo $schema;


  $schema = '</CONFIG>' . "\n";
  echo $schema;
}

//--------------------------------------------------------------

function SendXMLHeader ()
{
  header ("Last-Modified: ". gmdate ("D, d M Y H:i:s"). " GMT");  // immer geändert
  header ("Cache-Control: no-cache, must-revalidate"); // HTTP/1.1
  header ("Pragma: no-cache"); // HTTP/1.0
  header ("Content-type: text/xml");
}
//--------------------------------------------------------------


function SendHTMLHeader ()
{
  header ("Last-Modified: ". gmdate ("D, d M Y H:i:s"). " GMT");  // immer ge�ndert
  header ("Cache-Control: no-cache, must-revalidate"); // HTTP/1.1
  header ("Pragma: no-cache"); // HTTP/1.0
  header ("Content-type: text/html");
}

//--------------------------------------------------------------

function ShowHTMLMenu ()
{
  global $version_nr, $version_datum, $user, $password, $PHP_SELF;

  SendHTMLHeader();
  $Url = $PHP_SELF . "?user=" . $user . "&password=" . $password;


?>
<html><head></head><body>
<h3>Winline - modified Shopanbindung</a></h3>
<h4>Version <?php echo $version_nr; ?> Stand : <?php echo $version_datum; ?></h4>
<br>
<br><b>m&ouml;gliche Funktionen :</b><br><br>
<a href="<?php echo $Url; ?>&action=version">Ausgabe XML Scriptversion</a><br>
<br>
<a href="<?php echo $Url; ?>&action=manufacturers_export">Ausgabe XML Manufacturers</a><br>
<a href="<?php echo $Url; ?>&action=categories_export">Ausgabe XML Categories</a><br>
<a href="<?php echo $Url; ?>&action=products_export">Ausgabe XML Products</a><br>
<a href="<?php echo $Url; ?>&action=customers_export">Ausgabe XML Customers</a><br>
<a href="<?php echo $Url; ?>&action=customers_newsletter_export">Ausgabe XML Customers-Newsletter</a><br>
<br>
<a href="<?php echo $Url; ?>&action=orders_export">Ausgabe XML Orders</a><br>
<br>
<a href="<?php echo $Url; ?>&action=config_export">Ausgabe XML Shop-Config</a><br>
<br>
<a href="<?php echo $Url; ?>&action=update_tables">MySQL-Tabellen f&uuml;r die verwendung mit Winline aktualisieren</a><br>
<br>
<a href="<?php echo $Url; ?>&action=send_log">aktuelles Transfer-Log ansehen (die le. 100 Eintr&auml;ge)</a><br>
</body>
</html>
<?php
}

//--------------------------------------------------------------

function UpdateTables ()
{
  global $version_nr, $version_datum;

  SendHTMLHeader();
  echo '<html><head></head><body>';
  echo '<h3>Tabellen-Update / Erweiterung f�r Winline</h3>';
  echo '<h4>Version ' . $version_nr . ' Stand : ' . $version_datum .'</h4>';

  $sql[1]  = 'ALTER TABLE ' . TABLE_PRODUCTS . ' ADD products_ean VARCHAR(128) AFTER products_id';
  $sql[2]  = 'ALTER TABLE ' . TABLE_ORDERS . ' ADD payment_class VARCHAR(32) NOT NULL';
  $sql[3]  = 'ALTER TABLE ' . TABLE_ORDERS . ' ADD shipping_method VARCHAR(32) NOT NULL';
  $sql[4]  = 'ALTER TABLE ' . TABLE_ORDERS . ' ADD shipping_class VARCHAR(32) NOT NULL';
  $sql[5]  = 'ALTER TABLE ' . TABLE_ORDERS . ' ADD billing_country_iso_code_2 CHAR(2) NOT NULL AFTER billing_country';
  $sql[6]  = 'ALTER TABLE ' . TABLE_ORDERS . ' ADD delivery_country_iso_code_2 CHAR(2) NOT NULL AFTER delivery_country';
  $sql[7]  = 'ALTER TABLE ' . TABLE_ORDERS . ' ADD billing_firstname VARCHAR(32) NOT NULL AFTER billing_name';
  $sql[8]  = 'ALTER TABLE ' . TABLE_ORDERS . ' ADD billing_lastname VARCHAR(32) NOT NULL AFTER billing_firstname';
  $sql[9]  = 'ALTER TABLE ' . TABLE_ORDERS . ' ADD delivery_firstname VARCHAR(32) NOT NULL AFTER delivery_name';
  $sql[10] = 'ALTER TABLE ' . TABLE_ORDERS . ' ADD delivery_lastname VARCHAR(32) NOT NULL AFTER delivery_firstname';
  $sql[11] = 'ALTER TABLE ' . TABLE_ORDERS . ' CHANGE payment_method payment_method VARCHAR(255) NOT NULL';
  $sql[12] = 'ALTER TABLE ' . TABLE_ORDERS . ' CHANGE shipping_method shipping_method VARCHAR(255) NOT NULL';
  $sql[13] = 'CREATE TABLE cao_log ( id int(11) NOT NULL auto_increment, date datetime NOT NULL default "0000-00-00 00:00:00",'.
             'user varchar(64) NOT NULL default "", pw varchar(64) NOT NULL default "", method varchar(64) NOT NULL default "",'.
             'action varchar(64) NOT NULL default "", post_data mediumtext, get_data mediumtext, PRIMARY KEY  (id))';

  $link = 'db_link';

  global $$link, $logger;

  for ($i=1;$i<=13;$i++)
  {
    echo '<b>SQL:</b> ' . $sql[$i] . '<br>';;

    if (@xtc_db_query($sql[$i], $$link))
    {
      echo '<b>Ergebnis : OK</b>';
    }
     else
    {
      $error = ((defined('DB_MYSQL_TYPE') && DB_MYSQL_TYPE=='mysqli') ? @xtc_db_error($query, mysqli_errno(${$link}), mysqli_error(${$link})) : @xtc_db_error($query, mysql_errno(${$link}), mysql_error(${$link})));
      $pos=strpos($error,'Duplicate column name');

      if ($pos===false)
      {
        $pos=strpos($error,'already exists');
        if ($pos===false)
        {
          echo '<b>Ergebnis : </b><font color="red"><b>' . $error . '</b></font>';
      }
        else
      {
        echo '<b>Ergebnis : OK, Tabelle existierte bereits !</b>';
      }
     }
       else
     {
       echo '<b>Ergebnis : OK, Spalte existierte bereits !</b>';
     }
   }
    echo '<br><br>';
  }
  echo '</body></html>';
}

//--------------------------------------------------------------

function xtc_try_upload ($file = '', $destination = '',
                         $permissions = '777', $extensions = '')
{
  $file_object = new upload($file, $destination, $permissions, $extensions);
  if ($file_object->filename != '') return $file_object; else return false;
}

//--------------------------------------------------------------

require_once(DIR_FS_INC .'xtc_not_null.inc.php');

function clear_string($value)
{
        $string=str_replace("'",'',$value);
        $string=str_replace(')','',$string);
        $string=str_replace('(','',$string);
        $array=explode(',',$string);
        return $array;
}

//--------------------------------------------------------------

function xtc_RandomString($length)
{
        $chars = array( 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G', 'h', 'H', 'i', 'I', 'j', 'J',  'k', 'K', 'l', 'L', 'm', 'M', 'n','N', 'o', 'O', 'p', 'P', 'q', 'Q', 'r', 'R', 's', 'S', 't', 'T',  'u', 'U', 'v','V', 'w', 'W', 'x', 'X', 'y', 'Y', 'z', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0');

        $max_chars = count($chars) - 1;
        srand( (double) microtime()*1000000);

        $rand_str = '';
        for($i=0;$i<$length;$i++)
        {
          $rand_str = ( $i == 0 ) ? $chars[rand(0, $max_chars)] : $rand_str . $chars[rand(0, $max_chars)];
        }

  return $rand_str;
}

//--------------------------------------------------------------

function xtc_create_password($pass)
{
  return md5($pass);
}

//--------------------------------------------------------------

function xtc_remove_product($product_id)
{
//BOF - Dokuman - 2009-11-04 - fix typo customers_status_array -> customers_statuses_array
         //global $LangID, $customers_status_array;  //R Brym
         global $LangID, $customers_statuses_array;
//EOF - Dokuman - 2009-11-04 - fix typo customers_status_array -> customers_statuses_array
        $product_image_query = xtc_db_query("select products_image from " . TABLE_PRODUCTS . " where products_id = '" . xtc_db_input($product_id) . "'");
        $product_image = xtc_db_fetch_array($product_image_query);

        $duplicate_image_query = xtc_db_query("select count(*) as total from " . TABLE_PRODUCTS . " where products_image = '" . xtc_db_input($product_image['products_image']) . "'");
        $duplicate_image = xtc_db_fetch_array($duplicate_image_query);

        if ($duplicate_image['total'] < 2) {
          if (file_exists(DIR_FS_CATALOG_POPUP_IMAGES . $product_image['products_image'])) {
            @unlink(DIR_FS_CATALOG_POPUP_IMAGES . $product_image['products_image']);
          }
          // START CHANGES
          $image_subdir = BIG_IMAGE_SUBDIR;
          if (substr($image_subdir, -1) != '/') $image_subdir .= '/';
          if (file_exists(DIR_FS_CATALOG_IMAGES . $image_subdir . $product_image['products_image'])) {
            @unlink(DIR_FS_CATALOG_IMAGES . $image_subdir . $product_image['products_image']);
          }
          // END CHANGES
        }

        xtc_db_query("delete from " . TABLE_SPECIALS . " where products_id = '" . xtc_db_input($product_id) . "'");
        xtc_db_query("delete from " . TABLE_PRODUCTS . " where products_id = '" . xtc_db_input($product_id) . "'");
        xtc_db_query("delete from " . TABLE_PRODUCTS_TO_CATEGORIES . " where products_id = '" . xtc_db_input($product_id) . "'");
        xtc_db_query("delete from " . TABLE_PRODUCTS_DESCRIPTION . " where products_id = '" . xtc_db_input($product_id) . "'");
        xtc_db_query("delete from " . TABLE_PRODUCTS_ATTRIBUTES . " where products_id = '" . xtc_db_input($product_id) . "'");
        xtc_db_query("delete from " . TABLE_CUSTOMERS_BASKET . " where products_id = '" . xtc_db_input($product_id) . "'");
        xtc_db_query("delete from " . TABLE_CUSTOMERS_BASKET_ATTRIBUTES . " where products_id = '" . xtc_db_input($product_id) . "'");

        if (defined('TABLE_PRODUCTS_IMAGES'))
          {
            xtc_db_query("delete from " . TABLE_PRODUCTS_IMAGES . " where products_id = '" . xtc_db_input($product_id) . "'");
          }


        // get statuses
        $customers_statuses_array = array(array());

        $customers_statuses_query = xtc_db_query("select * from " . TABLE_CUSTOMERS_STATUS . " where language_id = '".$LangID."' order by customers_status_id");

          while ($customers_statuses = xtc_db_fetch_array($customers_statuses_query)) {
              $customers_statuses_array[] = array('id' => $customers_statuses['customers_status_id'],
                                                 'text' => $customers_statuses['customers_status_name']);

          }
//BOF - Dokuman - 2009-11-04 - fix typo customers_status_array -> customers_statuses_array
          //for ($i=0,$n=sizeof($customers_status_array);$i<$n;$i++) {
          for ($i=0,$n=sizeof($customers_statuses_array);$i<$n;$i++) {
//EOF - Dokuman - 2009-11-04 - fix typo customers_status_array -> customers_statuses_array
              xtc_db_query("delete from personal_offers_by_customers_status_" . $i . " where products_id = '" . xtc_db_input($product_id) . "'");
          }

          $product_reviews_query = xtc_db_query("select reviews_id from " . TABLE_REVIEWS . " where products_id = '" . xtc_db_input($product_id) . "'");
          while ($product_reviews = xtc_db_fetch_array($product_reviews_query)) {
            xtc_db_query("delete from " . TABLE_REVIEWS_DESCRIPTION . " where reviews_id = '" . $product_reviews['reviews_id'] . "'");
          }
          xtc_db_query("delete from " . TABLE_REVIEWS . " where products_id = '" . xtc_db_input($product_id) . "'");
}

//--------------------------------------------------------------

function ManufacturersImageUpload ()
{
  global $_GET, $_POST;

  if ($manufacturers_image = &xtc_try_upload('manufacturers_image',DIR_FS_CATALOG.DIR_WS_IMAGES,'777', '', true))
  {
    $code = 0;
    $message = 'OK';
  } else {
    $code = -1;
    $message = 'UPLOAD FAILED';
  }
  print_xml_status ($code, $_POST['action'], $message, '', 'OUTCOME', $manufacturers_image->filename);
}

//--------------------------------------------------------------

function CsvFileUpload()
{
  global $_GET, $_POST;

  $file_name=$_GET['file_name'];
  $file_size=$_GET['file_size'];

  $putdata=fopen("php://input", "r");
  $fp=fopen(DIR_FS_DOCUMENT_ROOT.'import/'.$file_name, "w");
  while($data=fread($putdata,1024))
  {
    fwrite($fp, $data);
  }

  fclose($fp);
  fclose($putdata);

  $fp_size=filesize(DIR_FS_DOCUMENT_ROOT.'import/'.$file_name);

  if($fp_size==$file_size)
  {
    $code = 0;
    $message = 'OK '.$file_size.":".$fp_size;
  } else {
    $code = -1;
    $message = 'FAILED '.$file_size.":".$fp_size;
  }

  print_xml_status ($code, $_POST['action'], $message, '', 'OUTCOME', $file_name);
}

//--------------------------------------------------------------

function ImageUpload()
{
  global $_GET, $_POST;

  $file_name=$_GET['file_name'];
  $file_size=$_GET['file_size'];

  $putdata=fopen("php://input", "r");
  $fp=fopen(DIR_FS_DOCUMENT_ROOT.'images/product_images/uploaded_images/'.$file_name, "w");
  while($data=fread($putdata,1024))
  {
    fwrite($fp, $data);
  }

  fclose($fp);
  fclose($putdata);

  $fp_size=filesize(DIR_FS_DOCUMENT_ROOT.'images/product_images/uploaded_images/'.$file_name);

  if($fp_size==$file_size) {
    $code=0;
    $message="OK ".$fp_size.":".$file_size;
  } else {
    $code=-1;
    $message="FAILED ".$fp_size.":".$file_size;
  }

  print_xml_status ($code, $_POST['action'], $message, '', 'OUTCOME', $file_name);
}

//--------------------------------------------------------------

function ValidateImage()
{
  global $_POST;

  $image_name=$_POST['image_name'];
  $query="SELECT products_image, products_model FROM products WHERE products_image LIKE '".$image_name."'";
  $result=xtc_db_fetch_array(xtc_db_query($query));

  if(isset($result))
  {
    print_xml_status ("0", '', '', '', 'OUTCOME', $image_name);
  }
  else
  {
    print_xml_status ("-1", '', '', '', 'OUTCOME', $image_name);
  }

}

//--------------------------------------------------------------

function ValidateImageFile()
{
  global $_POST;

  $image_name=$_POST['image_name'];

  if(file_exists(DIR_FS_DOCUMENT_ROOT.'images/product_images/original_images/'.$image_name))
  {
    print_xml_status ("0", '', '', '', 'OUTCOME', $image_name);
  }
  else
  {
    print_xml_status ("-1", '', '', '', 'OUTCOME', $image_name);
  }

}

//--------------------------------------------------------------

function CsvFileImport2()
{
  global $_GET, $_POST;
	$file_name=$_POST['file_name'];

  require('../admin/includes/classes/import.php');
  require(DIR_FS_INC.'xtc_format_filesize.inc.php');
  require(DIR_FS_INC.'xtc_get_customers_statuses.inc.php');

  define("CSV_SEPERATOR","|");
  define("CSV_TEXTSIGN","");
  define("CSV_CAT_DEPTH",4);
  define("CSV_CATEGORY_DEFAULT","Top");

  $handler = new xtcImport($file_name);
  $mapping=$handler->map_file($handler->generate_map());
  $import=$handler->import($mapping);

  $code=-1;
  $message='FAILED';
  $error='';

  if (isset($import)) 
  {
    if ($import[0]) 
    {
      $code=0;
      $message="OK";
    }

    if (isset($import[1]) && $import[1][0] != '')
    {
      $code=-1;
      for ($i=0;$i<count($import[1]);$i++) 
      {
        $error.$import[1][$i]."-";
      }
    }
  }

	$file = DIR_FS_DOCUMENT_ROOT.'import/'.$file_name;
  if (file_exists($file)) 
  {
		unlink($file);
  }
  
	print_xml_status ($code, $_POST['action'],$message, $error,'FILE_NAME',$file_name);
  return;
}

function CsvFileImport()
{
	global $_GET, $_POST;

	$user=$_POST['user'];
	$password=$_POST['password'];
	$file_name=$_POST['file_name'];
	$_url=$_POST['url'];

	$cookiefile = "cookies.txt";
	$agent     = "Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)";
	$url = $_url.'login.php?action=process';
	$data = 'email_address='.$user.'&password='.$password;

	$ch = curl_init();
	curl_setopt($ch, CURLOPT_COOKIEFILE, $cookiefile);
	curl_setopt($ch, CURLOPT_COOKIEJAR, $cookiefile);
	curl_setopt($ch, CURLOPT_USERAGENT, $agent);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
	if ($proxystatus == 'on') {
		curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE);
		curl_setopt($ch, CURLOPT_HTTPPROXYTUNNEL, TRUE);
		curl_setopt($ch, CURLOPT_PROXY, $proxy);
	}
	curl_setopt($ch, CURLOPT_URL, $url);
	curl_setopt($ch, CURLOPT_USERAGENT, $_SERVER['HTTP_USER_AGENT']);  
	curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
	curl_setopt($ch, CURLOPT_POST, 1); // set POST method
	curl_setopt($ch, CURLOPT_POSTFIELDS, $data);            
	$login_result = curl_exec($ch);

	$url  = $_url."admin/csv_backend.php?action=import";
    curl_setopt($ch, CURLOPT_COOKIEFILE, $cookiefile);
    curl_setopt($ch, CURLOPT_COOKIEJAR, $cookiefile);
    curl_setopt($ch, CURLOPT_USERAGENT, $agent);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    if ($proxystatus == 'on') {
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, FALSE);
        curl_setopt($ch, CURLOPT_HTTPPROXYTUNNEL, TRUE);
        curl_setopt($ch, CURLOPT_PROXY, $proxy);
    }
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_USERAGENT, $_SERVER['HTTP_USER_AGENT']);  
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
    curl_setopt($ch, CURLOPT_POST, 1);
	  curl_setopt($ch, CURLOPT_POSTFIELDS,"select_file=".$file_name."&select_content=products");

	$result=curl_exec($ch);

	$code=0;
	$message='OK';
	$ok=strpos($result,'Import--OK');
	if ($ok < 1) {
		$code=-1;
		$message='IMPORT FAILED';
	}
	
	print_xml_status ($code, $_POST['action'],$message, '','FILE_NAME',$file_name);
	curl_close($ch);
	
	$filename = DIR_FS_DOCUMENT_ROOT.'import/'.$file_name;
	if (file_exists($filename)) {
		unlink($filename);
	}
	
}

//--------------------------------------------------------------

function ImageProzess()
{
  global $_GET, $_POST;

  define(DIR_FS_CATALOG_ORIGINAL_IMAGES,DIR_FS_CATALOG.DIR_WS_ORIGINAL_IMAGES);
  define(DIR_FS_CATALOG_INFO_IMAGES,DIR_FS_CATALOG.DIR_WS_INFO_IMAGES);
  define(DIR_FS_CATALOG_POPUP_IMAGES,DIR_FS_CATALOG.DIR_WS_POPUP_IMAGES);
  define(DIR_FS_CATALOG_THUMBNAIL_IMAGES,DIR_FS_CATALOG.DIR_WS_THUMBNAIL_IMAGES);
  define(DIR_FS_CATALOG_IMAGES,DIR_FS_CATALOG.DIR_WS_IMAGES);
  define("DIR_FS_CATALOG_UPLOADED_IMAGES", DIR_FS_DOCUMENT_ROOT .'images/product_images/uploaded_images/');

  $original_images = array();
  $processed_images = array();
  $uploaded_images = array();

  if(!file_exists(DIR_FS_CATALOG_UPLOADED_IMAGES))
  {
    mkdir(DIR_FS_CATALOG_UPLOADED_IMAGES);
  }
  $dir=dir(DIR_FS_CATALOG_UPLOADED_IMAGES);
  $outcome="";

  while (false !== ($entry = $dir->read())) {
    if ($entry != "noimage.gif" && !is_dir($entry)) {
      $uploaded_images[]=$entry;
    }
  }

  $dir->close();

  $uploads=count($uploaded_images);
  $count=0;

  if($uploads >0 ) 
  {
    foreach ($uploaded_images as $picture) 
    {
      $image = img_box(imagecreatefromjpeg(DIR_FS_CATALOG_UPLOADED_IMAGES.$picture),800,600);
      if ($image !== null) {
        imagejpeg($image,DIR_FS_CATALOG_UPLOADED_IMAGES.$picture,100);
        rename(DIR_FS_CATALOG_UPLOADED_IMAGES.$picture, DIR_FS_CATALOG_ORIGINAL_IMAGES.$picture);

        $products_image_name=$picture;
        if(strlen($outcome)==0) {
          $outcome=$outcome.$products_image_name;
        } else {
          $outcome=$outcome.", ".$products_image_name;
        }

        include('../admin/includes/product_info_images.php');
        include('../admin/includes/product_popup_images.php');
        include('../admin/includes/product_thumbnail_images.php');
        $count++;
      }
    }

    if($count>0) 
    {
      $outcome=$outcome." erfolgreich.";
    } 
    else 
    {
      $outcome="keine Bilder gefunden.";
    }

  }

  print_xml_status ("0", $_POST['action'], $uploads, '', 'OUTCOME', $outcome);
}

function ImageProzess1()
{
  global $_GET, $_POST;

//  require_once(DIR_FS_DOCUMENT_ROOT.'includes/application_top.php');
//  require(DIR_FS_ADMIN.'image_manipulator_GD2_advanced.php');

  // init arrays
  
  $original_images = array();
  $processed_images = array();
  $uploaded_images = array();

  // neue images
  $d=dir(DIR_FS_CATALOG_UPLOADED_IMAGES);

  while ($entry = $d->read()) {
    if ($entry != "noimage.gif") $uploaded_images[]=$entry;
    echo $uploaded_images;
  }
  $d->close();

  $count=0;
  foreach ($uploaded_images as $value) {
    if (file_exists(DIR_FS_CATALOG_INFO_IMAGES.$value)) {
        unlink(DIR_FS_CATALOG_INFO_IMAGES.$value);
    }
    
    $i = imagecreatefromjpeg(DIR_FS_CATALOG_UPLOADED_IMAGES.$value);
    $thumb = img_box($i, 800, 600);
    imagedestroy($i);

    imagejpeg($thumb,DIR_FS_CATALOG_UPLOADED_IMAGES.$value,100);
    rename(DIR_FS_CATALOG_UPLOADED_IMAGES.$value, DIR_FS_CATALOG_ORIGINAL_IMAGES.$value);

    $count++;
    if ($count == '100') exit('again');

    $d = dir(DIR_FS_CATALOG_ORIGINAL_IMAGES);

    while (false !== ($entry = $d->read())) {
       if ($entry != "noimage.gif") $original_images[]=$entry;
    }
    $d->close();
     
    // product info images
    $d = dir(DIR_FS_CATALOG_INFO_IMAGES);
     
    while (false !== ($entry = $d->read())) {
       if ($entry != "noimage.gif") $processed_images[]=$entry;
    }
    $d->close();
     
    // compare arrays
    $dummy = array_diff($original_images, $processed_images);
    // if no difference, quit with statement complete
    
    $count1=0;
    foreach ($dummy as $products_image_name) {
        include('admin/includes/product_info_images.php');
        include('admin/includes/product_popup_images.php');
        include('admin/includes/product_thumbnail_images.php');
      $count1++;
      if ($count1 == '100') exit('again');
    }
  }
  echo "ok";

}

function img_box($img, $box_w, $box_h) {
  $new = imagecreatetruecolor($box_w, $box_h);
  if($new === false) {
  return null;
}

  $fill = imagecolorallocate($new, 255, 255, 255);
  imagefill($new, 0, 0, $fill);

  $hratio = $box_h / imagesy($img);
  $wratio = $box_w / imagesx($img);
  $ratio = min($hratio, $wratio);

  $sy = floor(imagesy($img) * $ratio);
  $sx = floor(imagesx($img) * $ratio);

  $m_y = floor(($box_h - $sy) / 2);
  $m_x = floor(($box_w - $sx) / 2);

  if(!imagecopyresampled($new, $img,
      $m_x, $m_y, //dest x, y (margins)
      0, 0, //src x, y (0,0 means top left)
      $sx, $sy,//dest w, h (resample to this size (computed above)
      imagesx($img), imagesy($img)) //src w, h (the full size of the original)
  ) {
      imagedestroy($new);
      return null;
  }
  return $new;
}

//--------------------------------------------------------------

function PostBieneLog()
{
  global $_GET, $_POST;

  $filename = DIR_FS_DOCUMENT_ROOT.'import/biene.chk';
  $message = $_POST['message'];
  
  if (is_writable($filename)) {
      if ($handle = fopen($filename, "a")) {
        if (!fwrite($handle, $message."\r\n")) {
          print_xml_status ('-1',$_POST['action'],"UNABLE TO WRITE LOG", '', 'OUTCOME', $message);
          exit;
        }
      } else {
        print_xml_status ('-1',$_POST['action'],"UNABLE TO OPEN LOG", '', 'OUTCOME', $message);
        exit;
      }
      fclose($handle);
  } else {
    print_xml_status ('-1',$_POST['action'],"FILE NOT WRITEABLE", '', 'OUTCOME', $message);
    exit;
  }
  print_xml_status ('0', $_POST['action'], 'OK', '', 'OUTCOME', $message);
}

//--------------------------------------------------------------

function CategoriesImageUpload ()
{
  global $_GET, $_POST;
  if ( $categories_image = &xtc_try_upload('categories_image',DIR_FS_CATALOG.DIR_WS_IMAGES.'categories/','777', '', true))
  {
    $code = 0;
    $message = 'OK';
  } else {
    $code = -1;
    $message = 'UPLOAD FAILED';
  }
  print_xml_status ($code, $_POST['action'], $message, '', 'OUTCOME', $categories_image->filename);
}

//--------------------------------------------------------------

function ProductsImageUpload ()
{
  global $_GET, $_POST;
  if ($products_image = &xtc_try_upload('products_image',DIR_FS_CATALOG.DIR_WS_ORIGINAL_IMAGES,'777', '', true))
  {
    $products_image_name = $products_image_name_process = $products_image->filename;
    
    // rewrite values to use resample classes
    define('DIR_FS_CATALOG_ORIGINAL_IMAGES',DIR_FS_CATALOG.DIR_WS_ORIGINAL_IMAGES);
    define('DIR_FS_CATALOG_INFO_IMAGES',DIR_FS_CATALOG.DIR_WS_INFO_IMAGES);
    define('DIR_FS_CATALOG_POPUP_IMAGES',DIR_FS_CATALOG.DIR_WS_POPUP_IMAGES);
    define('DIR_FS_CATALOG_THUMBNAIL_IMAGES',DIR_FS_CATALOG.DIR_WS_THUMBNAIL_IMAGES);
    define('DIR_FS_CATALOG_IMAGES',DIR_FS_CATALOG.DIR_WS_IMAGES);

    // generate resampled images
    require(DIR_FS_DOCUMENT_ROOT.(defined('DIR_ADMIN') ? DIR_ADMIN : 'admin/').'includes/product_thumbnail_images.php');
    require(DIR_FS_DOCUMENT_ROOT.(defined('DIR_ADMIN') ? DIR_ADMIN : 'admin/').'includes/product_info_images.php');
    require(DIR_FS_DOCUMENT_ROOT.(defined('DIR_ADMIN') ? DIR_ADMIN : 'admin/').'includes/product_popup_images.php');

    $code = 0;
    $message = 'OK';
  } else {
    $code = -1;
    $message = 'UPLOAD FAILED';
  }
  print_xml_status ($code, $_POST['action'], $message, '', 'FILE_NAME', $products_image->filename);
}

//--------------------------------------------------------------

function ProductsImageUploadMed ()
{
  ProductsImageUpload ();
}

//--------------------------------------------------------------

function ProductsImageUploadLarge ()
{
  ProductsImageUpload ();
}

//--------------------------------------------------------------

function CheckImages ($FileName)
{
  $products_image_name = $FileName;

  // rewrite values to use resample classes
  define('DIR_FS_CATALOG_ORIGINAL_IMAGES',DIR_FS_CATALOG.DIR_WS_ORIGINAL_IMAGES);
  define('DIR_FS_CATALOG_INFO_IMAGES',DIR_FS_CATALOG.DIR_WS_INFO_IMAGES);
  define('DIR_FS_CATALOG_POPUP_IMAGES',DIR_FS_CATALOG.DIR_WS_POPUP_IMAGES);
  define('DIR_FS_CATALOG_THUMBNAIL_IMAGES',DIR_FS_CATALOG.DIR_WS_THUMBNAIL_IMAGES);
  define('DIR_FS_CATALOG_IMAGES',DIR_FS_CATALOG.DIR_WS_IMAGES);

  // generate resampled images
  if  (file_exists (DIR_FS_CATALOG_ORIGINAL_IMAGES . $FileName))
  {

    if (!file_exists (DIR_FS_CATALOG_INFO_IMAGES . $FileName))
    {
      require(DIR_FS_DOCUMENT_ROOT.'admin/includes/product_info_images.php');
    }

    if (!file_exists (DIR_FS_CATALOG_THUMBNAIL_IMAGES . $FileName))
    {
      require(DIR_FS_DOCUMENT_ROOT.'admin/includes/product_thumbnail_images.php');
    }

    if (!file_exists (DIR_FS_CATALOG_POPUP_IMAGES . $FileName))
    {
      require(DIR_FS_DOCUMENT_ROOT.'admin/includes/product_popup_images.php');
    }
  }
}

//--------------------------------------------------------------

function ManufacturersUpdate ()
{
  global $_POST;

  $manufacturers_id = xtc_db_prepare_input($_POST['mID']);

  if (isset($manufacturers_id))
  {
    // Hersteller laden
    $count_query = xtc_db_query("select
                                  manufacturers_id,
                                 manufacturers_name,
                                 manufacturers_image,
                                 date_added,
                                 last_modified from " . TABLE_MANUFACTURERS . "
                                 where manufacturers_id='" . $manufacturers_id . "'");

   if ($manufacturer = xtc_db_fetch_array($count_query))
   {
      $exists = 1;
      // aktuelle Herstellerdaten laden
      $manufacturers_name  = $manufacturer['manufacturers_name'];
    $manufacturers_image = $manufacturer['manufacturers_image'];
    $date_added          = $manufacturer['date_added'];
    $last_modified       = $manufacturer['last_modified'];
    }
    else $exists = 0;

    // Variablen nur ueberschreiben wenn als Parameter vorhanden !!!
    if (isset($_POST['manufacturers_name'])) $manufacturers_name = xtc_db_prepare_input($_POST['manufacturers_name']);
    if (isset($_POST['manufacturers_image'])) $manufacturers_image = xtc_db_prepare_input($_POST['manufacturers_image']);

    $sql_data_array = array('manufacturers_id' => $manufacturers_id,
                           'manufacturers_name' => $manufacturers_name,
                           'manufacturers_image' => $manufacturers_image);

    if ($exists==0) // Neuanlage (ID wird von CAO virgegeben !!!)
    {
      $mode='APPEND';
      $insert_sql_data = array('date_added' => 'now()');
      $sql_data_array = array_merge($sql_data_array, $insert_sql_data);

      xtc_db_perform(TABLE_MANUFACTURERS, $sql_data_array);
      $products_id = xtc_db_insert_id();
    }
    elseif ($exists==1) //Update
    {
      $mode='UPDATE';
      $update_sql_data = array('last_modified' => 'now()');
      $sql_data_array = array_merge($sql_data_array, $update_sql_data);

      xtc_db_perform(TABLE_MANUFACTURERS, $sql_data_array, 'update', 'manufacturers_id = \'' . xtc_db_input($manufacturers_id) . '\'');
    }
    $languages_query = xtc_db_query("select languages_id, name, code, image, directory from " . TABLE_LANGUAGES . " order by sort_order");
    while ($languages = xtc_db_fetch_array($languages_query))
    {
      $languages_array[] = array('id' => $languages['languages_id'],
                                'name' => $languages['name'],
                                'code' => $languages['code'],
                                'image' => $languages['image'],
                                'directory' => $languages['directory']);
    }
    $languages = $languages_array;
    for ($i = 0, $n = sizeof($languages); $i < $n; $i++)
    {
      $language_id = $languages[$i]['id'];

      // Bestehende Daten laden
      $desc_query = xtc_db_query("select manufacturers_id,languages_id,manufacturers_url,url_clicked,date_last_click from " .
                               TABLE_MANUFACTURERS_INFO . " where manufacturers_id='" . $manufacturers_id . "' and languages_id='" . $language_id . "'");
      if ($desc = xtc_db_fetch_array($desc_query))
      {
        $manufacturers_url = $desc['manufacturers_url'];
        $url_clicked       = $desc['url_clicked'];
      $date_last_click   = $desc['date_last_click'];
    }

    // uebergebene Daten einsetzen
    if (isset($_POST['manufacturers_url'][$language_id])) $manufacturers_url=xtc_db_prepare_input($_POST['manufacturers_url'][$language_id]);
    if (isset($_POST['url_clicked'][$language_id]))       $url_clicked=xtc_db_prepare_input($_POST['url_clicked'][$language_id]);
    if (isset($_POST['date_last_click'][$language_id]))   $date_last_click=xtc_db_prepare_input($_POST['date_last_click'][$language_id]);

    $sql_data_array = array('manufacturers_url' => $manufacturers_url);

    if ($exists==0) // Insert
    {
      $insert_sql_data = array('manufacturers_id' => $products_id,
                               'languages_id' => $language_id);
      $sql_data_array = /*xtc_*/array_merge($sql_data_array, $insert_sql_data);
      xtc_db_perform(TABLE_MANUFACTURERS_INFO, $sql_data_array);
    }
    elseif ($exists==1) // Update
    {
      xtc_db_perform(TABLE_MANUFACTURERS_INFO, $sql_data_array, 'update', 'manufacturers_id = \'' . xtc_db_input($manufacturers_id) . '\' and languages_id = \'' . $language_id . '\'');
    }
    }
    print_xml_status (0, $_POST['action'], 'OK', $mode ,'MANUFACTURERS_ID', $mID);
  }
    else
  {
    print_xml_status (99, $_POST['action'], 'PARAMETER ERROR', '', '', '');
  }
}

//--------------------------------------------------------------

function ManufacturersErase ()
{
  global $_POST;

  $ManID  = xtc_db_prepare_input($_POST['mID']);

  if (isset($ManID))
  {
    // Hersteller loeschen
    xtc_db_query("delete from " . TABLE_MANUFACTURERS . " where manufacturers_id = '" . (int)$ManID . "'");
    xtc_db_query("delete from " . TABLE_MANUFACTURERS_INFO . " where manufacturers_id = '" . (int)$ManID . "'");
    // Herstellerverweis in den Artikeln loeschen
    xtc_db_query("update " . TABLE_PRODUCTS . " set manufacturers_id = '' where manufacturers_id = '" . (int)$ManID . "'");

    print_xml_status (0, $_POST['action'], 'OK', '', '', '');
  }
    else
  {
    print_xml_status (99, $_POST['action'], 'PARAMETER ERROR', '', '', '');
  }
}

//--------------------------------------------------------------

function ProductsUpdate ()
{
  global $_POST, $LangID;

  $languages_query = xtc_db_query("select languages_id, name, code, image, directory from " . TABLE_LANGUAGES . " order by sort_order");
  while ($languages = xtc_db_fetch_array($languages_query))
  {
    $languages_array[] = array('id' => $languages['languages_id'],
                               'name' => $languages['name'],
                               'code' => $languages['code'],
                               'image' => $languages['image'],
                               'directory' => $languages['directory']);
  }
  $products_id = xtc_db_prepare_input($_POST['pID']);


  //VPE JP20060130
  if ((defined('TABLE_PRODUCTS_VPE')) and (USE_VPE==true))
  {
    if (isset($_POST['products_me']))
    {
      $vpe_name = xtc_db_prepare_input($_POST['products_me']);

      $vpe_query = xtc_db_query("select products_vpe_id from " . TABLE_PRODUCTS_VPE .
                                " where products_vpe_name='" . $vpe_name . "' and " .
                                "language_id='" . $LangID ."'");
      if ($vpe_res = xtc_db_fetch_array($vpe_query))
      {
        //VPE existiert bereits
        $products_vpe_id = $vpe_res['products_vpe_id'];
      }
       else
      {
        //VPE neu anlegen
        $next_id_query = xtc_db_query("select max(products_vpe_id) as products_vpe_id from " . TABLE_PRODUCTS_VPE . "");
        $next_id = xtc_db_fetch_array($next_id_query);
        $products_vpe_id = $next_id['products_vpe_id'] + 1;


        $languages = $languages_array;
        for ($i = 0, $n = sizeof($languages); $i < $n; $i++)
        {
          $language_id = $languages[$i]['id'];

          $insert_sql_data = array('products_vpe_id' => $products_vpe_id,
                                   'language_id' => $language_id,
                                   'products_vpe_name' => $vpe_name);

          xtc_db_perform(TABLE_PRODUCTS_VPE, $insert_sql_data);
        }
      }
    }
  }



  // product laden
  $sql = "select products_quantity,
                products_model,
                products_image,
                products_price,
                products_date_available,
                products_weight,
                products_status,
                products_ean,
                products_fsk18,
                products_shippingtime,
                products_tax_class_id,
                manufacturers_id";

  if ((defined('TABLE_PRODUCTS_VPE')) and (USE_VPE==true))
  {
    $sql .= ",products_vpe";
  }

  $sql .= " from " . TABLE_PRODUCTS . " where products_id='" . $products_id . "'";


  $count_query = xtc_db_query($sql);

  if ($product = xtc_db_fetch_array($count_query))
  {
    $exists = 1;
    // aktuelle Produktdaten laden
    $products_quantity = $product['products_quantity'];
    $products_model = $product['products_model'];
    $products_image = $product['products_image'];
    $products_price = $product['products_price'];
   $products_date_available = $product['products_date_available'];
   $products_weight = $product['products_weight'];
   $products_status = $product['products_status'];
   $products_ean = $product['products_ean'];
   $products_fsk18 = $product['products_fsk18'];
   $products_shippingtime = $product['products_shippingtime'];
   $products_tax_class_id = $product['products_tax_class_id'];
   $manufacturers_id = $product['manufacturers_id'];
  }
  else $exists = 0;

  // Variablen nur ueberschreiben wenn als Parameter vorhanden !!!
  if (isset($_POST['products_quantity'])) $products_quantity = xtc_db_prepare_input($_POST['products_quantity']);
  if (isset($_POST['products_model'])) $products_model = xtc_db_prepare_input($_POST['products_model']);
  if (isset($_POST['products_image']))
  {
    $products_image = xtc_db_prepare_input($_POST['products_image']);
    CheckImages ($products_image);
  }
  if (isset($_POST['products_price'])) $products_price = xtc_db_prepare_input($_POST['products_price']);
  if (isset($_POST['products_date_available'])) $products_date_available = xtc_db_prepare_input($_POST['products_date_available']);
  if (isset($_POST['products_weight'])) $products_weight = xtc_db_prepare_input($_POST['products_weight']);
  if (isset($_POST['products_status'])) $products_status = xtc_db_prepare_input($_POST['products_status']);
  if (isset($_POST['products_ean'])) $products_ean = xtc_db_prepare_input($_POST['products_ean']);
  if (isset($_POST['products_fsk18'])) $products_fsk18 = xtc_db_prepare_input($_POST['products_fsk18']);
  if (isset($_POST['products_shippingtime'])) $products_shippingtime = xtc_db_prepare_input($_POST['products_shippingtime']);
  if (isset($_POST['products_me'])) $products_vpe = xtc_db_prepare_input($_POST['products_me']);
  if (isset($_POST['products_tax_class_id'])) $products_tax_class_id = xtc_db_prepare_input($_POST['products_tax_class_id']);

//VPE Frank
  if (isset($_POST['products_basis_factor'])) $products_vpe_value = xtc_db_prepare_input($_POST['products_basis_factor']);
// Wird von CAO noch fehlerhaft �bergeben  if (isset($_POST['products_basis_me'])) $products_vpe = xtc_db_prepare_input($_POST['products_basis_me']);

  if ($_POST['products_basis_factor'] == 1) {
	$products_vpe_status = 0;
  } else {
	$products_vpe_status = 1;
  }

//VPE Frank Ende

  if (file_exists('cao_produpd_1.php')) { include('cao_produpd_1.php'); }

  // Comment: SWITCH_MWST nun an der richtigen Var. ; TKI 2005-08-24
  if (SWITCH_MWST==true)
  {
    // switch IDs
    if ($products_tax_class_id==1)
    {
      $products_tax_class_id=2;
    }
      else
    {
      if ($products_tax_class_id==2)
      {
        $products_tax_class_id=1;
      }
    }
  }

  if (isset($_POST['manufacturers_id'])) $manufacturers_id = xtc_db_prepare_input($_POST['manufacturers_id']);

  $products_date_available = (date('Y-m-d') < $products_date_available) ? $products_date_available : 'null';

  $sql_data_array = array('products_id' => $products_id,
                         'products_quantity' => $products_quantity,
                         'products_model' => $products_model,
                         'products_image' => ($products_image == 'none') ? '' : $products_image,
                         'products_price' => $products_price,
                         'products_date_available' => $products_date_available,
                         'products_weight' => $products_weight,
                         'products_status' => $products_status,
                         'products_ean' => $products_ean,
                         'products_fsk18' => $products_fsk18,
                         'products_shippingtime' => $products_shippingtime,
                         'products_tax_class_id' => $products_tax_class_id,
                         'manufacturers_id' => $manufacturers_id,
                         //VPE Frank
                         'products_vpe_value' => $products_vpe_value,
                         'products_vpe_status' => $products_vpe_status);
                         //VPE Frank Ende

  if ((defined('TABLE_PRODUCTS_VPE')) and (USE_VPE==true))
  {
    $sql_data_array = array_merge($sql_data_array, array ('products_vpe' => $products_vpe_id));
  }

  if ($exists==0) // Neuanlage (ID wird an CAO zurueckgegeben !!!)
  {
    // set groupaccees

    $permission_sql = 'show columns from ' . TABLE_PRODUCTS . ' like "group_permission_%"';
    $permission_query = xtc_db_query ($permission_sql);

    if (xtc_db_num_rows($permission_query))
    {
      // ist XTC 3.0.4
      $permission_array = array ();
      while ($permissions = xtc_db_fetch_array($permission_query))
      {
        $permission_array = array_merge($permission_array, array ($permissions['Field'] => '1'));
      }

      $insert_sql_data = array('products_date_added' => 'now()',
                              'products_shippingtime'=>1);

      $insert_sql_data = array_merge($insert_sql_data, $permission_array);
    }
      else
    {
      // XTC bis 3.0.3
      $customers_statuses_array = array(array());
      $customers_statuses_query = xtc_db_query("select customers_status_id,
                                               customers_status_name
                                               from " . TABLE_CUSTOMERS_STATUS . "
                                               where language_id = '".$LangID."' order by
                                               customers_status_id");
      $i=1;        // this is changed from 0 to 1 in cs v1.2
      while ($customers_statuses = xtc_db_fetch_array($customers_statuses_query))
      {
        $i=$customers_statuses['customers_status_id'];
        $customers_statuses_array[$i] = array('id' => $customers_statuses['customers_status_id'],
                                             'text' => $customers_statuses['customers_status_name']);
      }

     $group_ids='c_all_group,';
      for ($i=0;$n=sizeof($customers_statuses_array),$i<$n;$i++)
      {
        $group_ids .='c_'.$customers_statuses_array[$i]['id'].'_group,';
      }

      $insert_sql_data = array('products_date_added' => 'now()',
                              'products_shippingtime'=>1,
                              'group_ids'=>$group_ids);
   }

    $mode='APPEND';

    $sql_data_array = array_merge($sql_data_array, $insert_sql_data);

    // insert data
    xtc_db_perform(TABLE_PRODUCTS, $sql_data_array);

    $products_id = xtc_db_insert_id();

  }
  elseif ($exists==1) //Update
  {
    $mode='UPDATE';
    $update_sql_data = array('products_last_modified' => 'now()');
    $sql_data_array = array_merge($sql_data_array, $update_sql_data);

    // update data
    xtc_db_perform(TABLE_PRODUCTS, $sql_data_array, 'update', 'products_id = \'' . xtc_db_input($products_id) . '\'');
  }

  $languages = $languages_array;
  for ($i = 0, $n = sizeof($languages); $i < $n; $i++)
  {
    $language_id = $languages[$i]['id'];

    // Bestehende Daten laden
    $desc_query = xtc_db_query("select
                               products_id,
                               products_name,
                               products_description,
                               products_short_description,
                               products_meta_title,
                               products_meta_description,
                               products_meta_keywords,
                               products_url,
                               products_viewed,
                               language_id
                               from " .
                               TABLE_PRODUCTS_DESCRIPTION . "
                               where products_id='" . $products_id . "'
                               and language_id='" . $language_id . "'");

    if ($desc = xtc_db_fetch_array($desc_query))
    {
      $products_name = $desc['products_name'];
      $products_description = $desc['products_description'];
      $products_short_description = $desc['products_short_description'];
      $products_meta_title = $desc['products_meta_title'];
      $products_meta_description = $desc['products_meta_description'];
      $products_meta_keywords = $desc['products_meta_keywords'];
      $products_url = $desc['products_url'];
    }

    // uebergebene Daten einsetzen
    if (isset($_POST['products_name'][$LangID]))              $products_name              = xtc_db_prepare_input($_POST['products_name'][$LangID]);
    if (isset($_POST['products_description'][$LangID]))       $products_description       = xtc_db_prepare_input($_POST['products_description'][$LangID]);
    if (isset($_POST['products_short_description'][$LangID])) $products_short_description = xtc_db_prepare_input($_POST['products_short_description'][$LangID]);
    if (isset($_POST['products_meta_title'][$LangID]))        $products_meta_title        = xtc_db_prepare_input($_POST['products_meta_title'][$LangID]);
    if (isset($_POST['products_meta_description'][$LangID]))  $products_meta_description  = xtc_db_prepare_input($_POST['products_meta_description'][$LangID]);
    if (isset($_POST['products_meta_keywords'][$LangID]))     $products_meta_keywords     = xtc_db_prepare_input($_POST['products_meta_keywords'][$LangID]);
    if (isset($_POST['products_url'][$LangID]))               $products_url               = xtc_db_prepare_input($_POST['products_url'][$LangID]);

    //NEU 20051004 JP
    if (isset($_POST['products_shop_long_description'][$LangID]))  $products_description       = xtc_db_prepare_input($_POST['products_shop_long_description'][$LangID]);
    if (isset($_POST['products_shop_short_description'][$LangID])) $products_short_description = xtc_db_prepare_input($_POST['products_shop_short_description'][$LangID]);

    $sql_data_array = array('products_name' => $products_name,
                            'products_description' => $products_description,
                            'products_short_description' => $products_short_description,
                            'products_meta_title' => $products_meta_title,
                            'products_meta_description' => $products_meta_description,
                            'products_meta_keywords' => $products_meta_keywords,
                            'products_url' => $products_url);

    if ($exists==0) // Insert
    {
      $insert_sql_data = array('products_id' => $products_id,
                               'language_id' => $language_id);

      $sql_data_array = array_merge($sql_data_array, $insert_sql_data);
      xtc_db_perform(TABLE_PRODUCTS_DESCRIPTION, $sql_data_array);
    }
    elseif (($exists==1)and($language_id==$LangID)) // Update
    {
      // Nur die Daten in der akt. Sprache aendern !
      xtc_db_perform(TABLE_PRODUCTS_DESCRIPTION, $sql_data_array, 'update', 'products_id = \'' . xtc_db_input($products_id) . '\' and language_id = \'' . $language_id . '\'');
    }
  }

  if (defined('TABLE_PRODUCTS_IMAGES'))
  {
    if (isset($_POST['products_image_med']))
    {
      $SQL = "delete from " .TABLE_PRODUCTS_IMAGES. " where products_id= '" . xtc_db_input($products_id) . "' and image_nr=1";

      xtc_db_query($SQL);

      if (strlen($_POST['products_image_med'])>0)
      {
        $sql_data_array = array('products_id' => $products_id,
                                'image_nr' => '1',
                                'image_name' => xtc_db_prepare_input($_POST['products_image_med']));

        xtc_db_perform(TABLE_PRODUCTS_IMAGES, $sql_data_array);
      }
    }

    if (isset($_POST['products_image_large']))
    {
      $SQL = "delete from " .TABLE_PRODUCTS_IMAGES. " where products_id= '" . xtc_db_input($products_id) . "' and image_nr=2";
      xtc_db_query($SQL);

      if (strlen($_POST['products_image_large'])>0)
      {
        $sql_data_array = array('products_id' => $products_id,
                                'image_nr' => '2',
                                'image_name' => xtc_db_prepare_input($_POST['products_image_large']));

        xtc_db_perform(TABLE_PRODUCTS_IMAGES, $sql_data_array);
      }
    }
  }

  //Burn VK1-4 an die jeweiligen Gruppen �bergeben

    if (isset($_POST['products_vk1']))               $products_vk1               = xtc_db_prepare_input($_POST['products_vk1']);
    if (isset($_POST['products_vk2']))               $products_vk2               = xtc_db_prepare_input($_POST['products_vk2']);
    if (isset($_POST['products_vk3']))               $products_vk3               = xtc_db_prepare_input($_POST['products_vk3']);
    if (isset($_POST['products_vk4']))               $products_vk4               = xtc_db_prepare_input($_POST['products_vk4']);
    if (isset($_POST['products_vk5']))               $products_vk5               = xtc_db_prepare_input($_POST['products_vk5']);

// Hier die Preise den Gruppen zuordnen ID => Aufsteigend, GRP => Gruppen ID aus dem Shop, PREIS => vk1-4
    $vk_array = array(
            1 => array('ID' => '1','GRP' => '0','PREIS' => $products_vk2),
            2 => array('ID' => '2','GRP' => '1','PREIS' => $products_vk2),
            3 => array('ID' => '3','GRP' => '2','PREIS' => $products_vk2),
            4 => array('ID' => '4','GRP' => '3','PREIS' => $products_vk2),
            //5 => array('ID' => '5','GRP' => '4','PREIS' => $products_vk3),
            //6 => array('ID' => '6','GRP' => '5','PREIS' => $products_vk4),
         );

   for ($i = 1; $i < count($vk_array)+1; $i++)
   {

         $sql_data_array = array('products_id' => $products_id,
                          'quantity' => '1',
                          'personal_offer' => $vk_array[$i]['PREIS']);


         $delete = xtc_db_query("delete from personal_offers_by_customers_status_" . $vk_array[$i]['GRP'] . " where products_id='" . $products_id . "'");
         $insert_sql_data = array('products_id' => $products_id);
         $sql_data_array = array_merge($sql_data_array, $insert_sql_data);
         xtc_db_perform(personal_offers_by_customers_status_ . $vk_array[$i]['GRP'], $sql_data_array);
   }

//Burn Ende

  if (file_exists('cao_produpd_2.php')) { include('cao_produpd_2.php'); }

  print_xml_status (0, $_POST['action'], 'OK', $mode, 'PRODUCTS_ID', $products_id);
}

//--------------------------------------------------------------

function ProductsErase ()
{
  global $_POST;

  $ProdID  = xtc_db_prepare_input($_POST['prodid']);
  if (isset($ProdID))
  {
    // ProductsToCategieries loeschen bei denen die products_id = ... ist
    $res1 = xtc_db_query("delete from " . TABLE_PRODUCTS_TO_CATEGORIES . " where products_id='" . $ProdID . "'");

    // Product loeschen
    xtc_remove_product($ProdID);
    $code = 0;
    $message = 'OK';
  }
    else
  {
    $code = 99;
    $message = 'FAILED';
  }
  print_xml_status (0, $_POST['action'], 'OK', '', 'SQL_RES1', $res1);
}

//--------------------------------------------------------------

function ProductsSpecialPriceUpdate ()
{
  global $_POST;

  $ProdID  = xtc_db_prepare_input($_POST['prodid']);

  $Price  = xtc_db_prepare_input($_POST['price']);
  $Status = xtc_db_prepare_input($_POST['status']);
  $Expire = xtc_db_prepare_input($_POST['expired']);

  if (isset($ProdID))
  {
    /*
    1. Ermitteln ob Produkt bereits einen Spezialpreis hat
    2. wenn JA -> Update / NEIN -> INSERT
    */
    $sp_sql = "select specials_id from " . TABLE_SPECIALS . " " .
              "where products_id='" . (int)$ProdID . "'";
    $sp_query = xtc_db_query($sql);

    if ($sp = xtc_db_fetch_array($sp_query))
    {
      // es existiert bereits ein Datensatz -> Update
      $SpecialID = $sp['specials_id'];

      xtc_db_query(
              "update " . TABLE_SPECIALS .
              " set specials_new_products_price = '" . $Price . "'," .
              " specials_last_modified = now()," .
              " expires_date = '" . $Expire .
              "' where specials_id = '" . (int)$SpecialID. "'");

      print_xml_status (0, $_POST['action'], 'OK', 'UPDATE', '', '');
    }
      else
    {
      // Neuanlage
      xtc_db_query(
              "insert into " . TABLE_SPECIALS .
              " (products_id, specials_new_products_price, specials_date_added, expires_date, status) " .
              " values ('" . (int)$ProdID . "', '" . $Price . "', now(), '" . $Expire . "', '1')");

      print_xml_status (0, $_POST['action'], 'OK', 'APPEND', '', '');
    }
  }
    else
  {
    print_xml_status (99, $_POST['action'], 'PARAMETER ERROR', '', '', '');
  }
}

//--------------------------------------------------------------

function ProductsSpecialPriceErase ()
{
  global $_POST;

  $ProdID  = xtc_db_prepare_input($_POST['prodid']);
  if (isset($ProdID))
  {
    xtc_db_query("delete from " . TABLE_SPECIALS . " where products_id = '" . (int)$ProdID . "'");
    print_xml_status (0, $_POST['action'], 'OK', '', '', '');
  }
    else
  {
    print_xml_status (99, $_POST['action'], 'PARAMETER ERROR', '', '', '');
  }
}

//--------------------------------------------------------------

function CategoriesUpdate ()
{
  global $_POST, $LangID;

  $CatID    = xtc_db_prepare_input($_POST['catid']);
  $ParentID = xtc_db_prepare_input($_POST['parentid']);

  if (isset($ParentID) && isset($CatID))
  {
    // product laden
    $SQL = "select categories_id, parent_id, date_added, sort_order, categories_image " .
           "from " . TABLE_CATEGORIES . " where categories_id='" . $CatID . "'";


    $count_query = xtc_db_query($SQL);
    if ($categorie = xtc_db_fetch_array($count_query))
    {
      $exists = 1;

      $ParentID = $categorie['parent_id'];
      $Sort     = $categorie['sort_order'];
      $Image    = $categorie['categories_image'];
    }
    else $exists = 0;

    // Variablen nur ueberschreiben wenn als Parameter vorhanden !!!
    if (isset($_POST['parentid'])) $ParentID = xtc_db_prepare_input($_POST['parentid']);
    if (isset($_POST['sort']))     $Sort     = xtc_db_prepare_input($_POST['sort']);
    if (isset($_POST['image']))    $Image    = xtc_db_prepare_input($_POST['image']);


    $sql_data_array = array('categories_id'    => $CatID,
                            'parent_id'        => $ParentID,
                            'sort_order'       => $Sort,
                            'categories_image' => $Image,
                            'last_modified'    => 'now()');

    if ($exists==0) // Neuanlage
    {
      $mode='APPEND';

      // set groupaccees
      $permission_sql = 'show columns from ' . TABLE_CATEGORIES . ' like "group_permission_%"';
      $permission_query = xtc_db_query ($permission_sql);

      if (xtc_db_num_rows($permission_query))
      {
        // ist XTC 3.0.4
        $permission_array = array ();
        while ($permissions = xtc_db_fetch_array($permission_query))
        {
          $permission_array = array_merge($permission_array, array ($permissions['Field'] => '1'));
        }

        $insert_sql_data = array('date_added' => 'now()');

        $insert_sql_data = array_merge($insert_sql_data, $permission_array);
      }
        else
      {
        // XTC bis 3.0.3
        $customers_statuses_array = array(array());
        $customers_statuses_query = xtc_db_query("select customers_status_id,
                                                 customers_status_name
                                                 from " . TABLE_CUSTOMERS_STATUS . "
                                                 where language_id = '".$LangID."' order by
                                                 customers_status_id");
        $i=1;        // this is changed from 0 to 1 in cs v1.2
        while ($customers_statuses = xtc_db_fetch_array($customers_statuses_query))
        {
          $i=$customers_statuses['customers_status_id'];
          $customers_statuses_array[$i] = array('id' => $customers_statuses['customers_status_id'],
                                                'text' => $customers_statuses['customers_status_name']);
        }

        $group_ids='c_all_group,';
        for ($i=0;$n=sizeof($customers_statuses_array),$i<$n;$i++)
        {
          $group_ids .='c_'.$customers_statuses_array[$i]['id'].'_group,';
        }
       $insert_sql_data = array('date_added' => 'now()',
                                 'group_ids'  => $group_ids);
      }

      $sql_data_array = /*xtc_*/array_merge($sql_data_array, $insert_sql_data);

      xtc_db_perform(TABLE_CATEGORIES, $sql_data_array);
    }
    elseif ($exists==1) //Update
    {
      $mode='UPDATE';

      xtc_db_perform(TABLE_CATEGORIES, $sql_data_array, 'update', 'categories_id = \'' . xtc_db_input($CatID) . '\'');
    }

    //$languages = xtc_get_languages();

    $languages_query = xtc_db_query("select languages_id, name, code, image, directory from " . TABLE_LANGUAGES . " order by sort_order");
    while ($languages = xtc_db_fetch_array($languages_query))
    {
      $languages_array[] = array('id' => $languages['languages_id'],
                                 'name' => $languages['name'],
                                 'code' => $languages['code'],
                                 'image' => $languages['image'],
                                 'directory' => $languages['directory']);
    }

    $languages = $languages_array;

    for ($i = 0, $n = sizeof($languages); $i < $n; $i++)
    {
      $language_id = $languages[$i]['id'];

      // Bestehende Daten laden
      $SQL = "select categories_id,language_id,categories_name,categories_description,categories_heading_title,".
             "categories_meta_title,categories_meta_description,categories_meta_keywords";

      $desc_query = xtc_db_query($SQL . " from " . TABLE_CATEGORIES_DESCRIPTION . " where categories_id='" . $CatID . "' and language_id='" . $language_id . "'");
      if ($desc = xtc_db_fetch_array($desc_query))
      {
        $categories_name             = $desc['categories_name'];
        $categories_description      = $desc['$categories_description'];
        $categories_heading_title    = $desc['categories_heading_title'];
        $categories_meta_title       = $desc['categories_meta_title'];
        $categories_meta_description = $desc['categories_meta_description'];
        $categories_meta_keywords    = $desc['categories_meta_keywords'];
      }

      // uebergebene Daten einsetzen
      if (isset($_POST['name']))                        $categories_name             = xtc_db_prepare_input(UrlDecode($_POST['name']));
      if (isset($_POST['descr']))                       $categories_description = xtc_db_prepare_input(UrlDecode($_POST['descr']));
      if (isset($_POST['categories_heading_title']))    $categories_heading_title    = xtc_db_prepare_input(UrlDecode($_POST['categories_heading_title']));
      if (isset($_POST['categories_meta_title']))       $categories_meta_title       = xtc_db_prepare_input(UrlDecode($_POST['categories_meta_title']));
     if (isset($_POST['categories_meta_description'])) $categories_meta_description = xtc_db_prepare_input(UrlDecode($_POST['categories_meta_description']));
     if (isset($_POST['categories_meta_keywords']))    $categories_meta_keywords    = xtc_db_prepare_input(UrlDecode($_POST['categories_meta_keywords']));

     $sql_data_array = array('categories_name'             => $categories_name,
                              'categories_description'      => $categories_description,
                             'categories_heading_title'    => $categories_heading_title,
                             'categories_meta_title'       => $categories_meta_title,
                             'categories_meta_description' => $categories_meta_description,
                             'categories_meta_keywords'    => $categories_meta_keywords);

    if ($exists==0) // Insert
      {
        $insert_sql_data = array('categories_id' => $CatID,
                                 'language_id' => $language_id);

        $sql_data_array = /*xtc_*/array_merge($sql_data_array, $insert_sql_data);
        xtc_db_perform(TABLE_CATEGORIES_DESCRIPTION, $sql_data_array);
      }
      elseif (($exists==1)and($language_id==$LangID)) // Update
      {
        // Nur 1 Sprache aktualisieren
        xtc_db_perform(TABLE_CATEGORIES_DESCRIPTION, $sql_data_array, 'update', 'categories_id = \'' . xtc_db_input($CatID) . '\' and language_id = \'' . $language_id . '\'');
      }
    }
    print_xml_status (0, $_POST['action'], 'OK', $mode, '', '');
  }
    else
  {
    print_xml_status (99, $_POST['action'], 'PARAMETER ERROR', '', '', '');
  }
}

//--------------------------------------------------------------

function CategoriesErase ()
{
  global $_POST;

  $CatID  = xtc_db_prepare_input($_POST['catid']);

  if (isset($CatID))
  {
    // Categorie loeschen
    $res1 = xtc_db_query("delete from " . TABLE_CATEGORIES . " where categories_id='" . $CatID . "'");
    // ProductsToCategieries loeschen bei denen die Categorie = ... ist
    $res2 = xtc_db_query("delete from " . TABLE_PRODUCTS_TO_CATEGORIES . " where categories_id='" . $CatID . "'");
    // CategieriesDescription loeschenm bei denen die Categorie = ... ist
    $res3 = xtc_db_query("delete from " . TABLE_CATEGORIES_DESCRIPTION . " where categories_id='" . $CatID . "'");

    print_xml_status (0, $_POST['action'], 'OK', '', 'SQL_RES1', $res1);
  }
    else
  {
    print_xml_status (99, $_POST['action'], 'PARAMETER ERROR', '', '', '');
  }
}

//--------------------------------------------------------------

function Prod2CatUpdate ()
{
  global $_POST;

  $ProdID = xtc_db_prepare_input($_POST['prodid']);
  $CatID  = xtc_db_prepare_input($_POST['catid']);

  if (isset($ProdID) && isset($CatID))
  {
    $res = xtc_db_query("replace into " . TABLE_PRODUCTS_TO_CATEGORIES . " (products_id, categories_id) Values ('" . $ProdID ."', '" . $CatID . "')");
    print_xml_status (0, $_POST['action'], 'OK', '', 'SQL_RES', $res);
  }
    else
  {
    print_xml_status (99, $_POST['action'], 'PARAMETER ERROR', '', '', '');
  }
}

//--------------------------------------------------------------

function Prod2CatErase ()
{
  global $_POST;

  $ProdID = xtc_db_prepare_input($_POST['prodid']);
  $CatID  = xtc_db_prepare_input($_POST['catid']);

  if (isset($ProdID) && isset($CatID))
  {
    $res = xtc_db_query("delete from " . TABLE_PRODUCTS_TO_CATEGORIES . " where products_id='" . $ProdID ."' and categories_id='" . $CatID . "'");
    print_xml_status (0, $_POST['action'], 'OK', '', 'SQL_RES', $res);
  }
    else
  {
    print_xml_status (99, $_POST['action'], 'PARAMETER ERROR', '', '', '');
  }
}

//--------------------------------------------------------------
function OrderUpdate ()
{
  global $_POST, $LangID;

  $schema = '<?xml version="1.0" encoding="' . CHARSET . '"?>' . "\n" . "\n";

  if ((isset($_POST['order_id'])) && (isset($_POST['status'])))
  {
    // Per Post �bergebene Variablen
    $oID = $_POST['order_id'];
    $status = $_POST['status'];
    $comments = xtc_db_prepare_input($_POST['comments']);

    //Status �berpr�fen
    $check_status_query = xtc_db_query("select * from " . TABLE_ORDERS . " where orders_id = '" . xtc_db_input($oID) . "'");
    if ($check_status = xtc_db_fetch_array($check_status_query))
    {
      if ($check_status['orders_status'] != $status || $comments != '')
      {
        xtc_db_query("update " . TABLE_ORDERS . " set orders_status = '" . xtc_db_input($status) . "', last_modified = now() where orders_id = '" . xtc_db_input($oID) . "'");
        $customer_notified = '0';
        if ($_POST['notify'] == 'on')
        {
          // Falls eine Sprach ID zur Order existiert die Emailbest�tigung in dieser Sprache ausf�hren
          if (isset($check_status['orders_language_id']) && $check_status['orders_language_id'] > 0 )
          {
            $orders_status_query = xtc_db_query("select orders_status_id, orders_status_name from " . TABLE_ORDERS_STATUS . " where language_id = '" . $check_status['orders_language_id'] . "'");
            if (xtc_db_num_rows($orders_status_query) == 0)
            {
              $orders_status_query = xtc_db_query("select orders_status_id, orders_status_name from " . TABLE_ORDERS_STATUS . " where language_id = '" . $languages_id . "'");
            }
          }
            else
          {
            $orders_status_query = xtc_db_query("select orders_status_id, orders_status_name from " . TABLE_ORDERS_STATUS . " where language_id = '" . $languages_id . "'");
          }
          $orders_statuses = array();
          $orders_status_array = array();
          while ($orders_status = xtc_db_fetch_array($orders_status_query))
          {
            $orders_statuses[] = array('id' => $orders_status['orders_status_id'],
                                       'text' => $orders_status['orders_status_name']);
            $orders_status_array[$orders_status['orders_status_id']] = $orders_status['orders_status_name'];
          }
          // status query
          $orders_status_query = xtc_db_query("select orders_status_name from " . TABLE_ORDERS_STATUS . " where language_id = '" . $LangID . "' and orders_status_id='".$status."'");
          $o_status=xtc_db_fetch_array($orders_status_query);
          $o_status=$o_status['orders_status_name'];

          //ok lets generate the html/txt mail from Template
          if ($_POST['notify_comments'] == 'on')
          {
            $notify_comments = sprintf(EMAIL_TEXT_COMMENTS_UPDATE, $comments) . "\n\n";
          }
            else
          {
            $comments='';
          }

          // require functionblock for mails
          require_once(DIR_FS_INC . 'xtc_add_tax.inc.php');
          require_once(DIR_FS_INC . 'xtc_not_null.inc.php');
          require_once(DIR_FS_INC . 'xtc_href_link.inc.php');
          require_once(DIR_FS_INC . 'xtc_date_long.inc.php');
          require_once(DIR_FS_INC . 'xtc_check_agent.inc.php');
          require_once(DIR_FS_INC . 'xtc_php_mail.inc.php');

          $smarty = new Smarty;

          $smarty->assign('language', $check_status['language']);
          $smarty->caching = false;
          $smarty->template_dir=DIR_FS_CATALOG.'templates';
          $smarty->compile_dir=DIR_FS_CATALOG.'templates_c';
          $smarty->config_dir=DIR_FS_CATALOG.'lang';
          $smarty->assign('tpl_path', HTTP_SERVER.DIR_WS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/');
          $smarty->assign('logo_path', HTTP_SERVER.DIR_WS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/img/');
          $smarty->assign('NAME',$check_status['customers_name']);
          $smarty->assign('ORDER_NR',$oID);
          $smarty->assign('ORDER_LINK',xtc_href_link(FILENAME_ACCOUNT_HISTORY_INFO, 'order_id=' . $oID, 'SSL'));
          $smarty->assign('ORDER_DATE',xtc_date_long($check_status['date_purchased']));
          $smarty->assign('NOTIFY_COMMENTS',$comments);
          $smarty->assign('ORDER_STATUS',$o_status);

          $html_mail=$smarty->fetch(CURRENT_TEMPLATE . '/admin/mail/'.$check_status['language'].'/change_order_mail.html');
          $txt_mail=$smarty->fetch(CURRENT_TEMPLATE . '/admin/mail/'.$check_status['language'].'/change_order_mail.txt');

          // send mail with html/txt template
          xtc_php_mail(EMAIL_BILLING_ADDRESS,
                       EMAIL_BILLING_NAME ,
                       $check_status['customers_email_address'],
                       $check_status['customers_name'],
                       '',
                       EMAIL_BILLING_REPLY_ADDRESS,
                       EMAIL_BILLING_REPLY_ADDRESS_NAME,
                       '',
                       '',
                       EMAIL_BILLING_SUBJECT,
                       $html_mail ,
                       $txt_mail);

          $customer_notified = '1';
        }
        xtc_db_query("insert into " . TABLE_ORDERS_STATUS_HISTORY . " (orders_id, orders_status_id, date_added, customer_notified, comments) values ('" . xtc_db_input($oID) . "', '" . xtc_db_input($status) . "', now(), '" . $customer_notified . "', '" . xtc_db_input($comments)  . "')");
        $schema .= '<STATUS>' . "\n" .
                   '<STATUS_DATA>' . "\n" .
                   '<ORDER_ID>' . $oID . '</ORDER_ID>' . "\n" .
                   '<ORDER_STATUS>' . $status . '</ORDER_STATUS>' . "\n" .
                   '<ACTION>' . $_POST['action'] . '</ACTION>' . "\n" .
                   '<CODE>' . '0' . '</CODE>' . "\n" .
                   '<MESSAGE>' . 'OK' . '</MESSAGE>' . "\n" .
                   '</STATUS_DATA>' . "\n" .
                   '</STATUS>' . "\n";
      }
      else if ($check_status['orders_status'] == $status)
      {
        // Status ist bereits gesetzt
        $schema .= '<STATUS>' . "\n" .
                   '<STATUS_DATA>' . "\n" .
                   '<ORDER_ID>' . $oID . '</ORDER_ID>' . "\n" .
                   '<ORDER_STATUS>' . $status . '</ORDER_STATUS>' . "\n" .
                   '<ACTION>' . $_POST['action'] . '</ACTION>' . "\n" .
                   '<CODE>' . '1' . '</CODE>' . "\n" .
                   '<MESSAGE>' . 'NO STATUS CHANGE' . '</MESSAGE>' . "\n" .
                   '</STATUS_DATA>' . "\n" .
                   '</STATUS>' . "\n";
      }
    }
      else
    {
      // Fehler Order existiert nicht
      $schema .= '<STATUS>' . "\n" .
                 '<STATUS_DATA>' . "\n" .
                 '<ORDER_ID>' . $oID . '</ORDER_ID>' . "\n" .
                 '<ACTION>' . $_POST['action'] . '</ACTION>' . "\n" .
                 '<CODE>' . '2' . '</CODE>' . "\n" .
                 '<MESSAGE>' . 'ORDER_ID NOT FOUND OR SET' . '</MESSAGE>' . "\n" .
                 '</STATUS_DATA>' . "\n" .
                 '</STATUS>' . "\n";
    }
  }
    else
  {
    $schema = '<?xml version="1.0" encoding="' . CHARSET . '"?>' . "\n" .
              '<STATUS>' . "\n" .
              '<STATUS_DATA>' . "\n" .
              '<ACTION>' . $_POST['action'] . '</ACTION>' . "\n" .
              '<CODE>' . '99' . '</CODE>' . "\n" .
              '<MESSAGE>' . 'PARAMETER ERROR' . '</MESSAGE>' . "\n" .
              '</STATUS_DATA>' . "\n" .
              '</STATUS>' . "\n\n";
  }
  echo $schema;
}

//--------------------------------------------------------------

function CustomersUpdate ()
{
  global $_POST, $Lang_folder;

  $customers_id = -1;
  // include PW function
  require_once(DIR_FS_INC . 'xtc_encrypt_password.inc.php');

  if (isset($_POST['cID'])) $customers_id = xtc_db_prepare_input($_POST['cID']);

  // security check, if user = admin, dont allow to perform changes
  if ($customers_id!=-1)
  {
    $sec_query=xtc_db_query("SELECT customers_status FROM ".TABLE_CUSTOMERS." where customers_id='".$customers_id."'");
    $sec_data=xtc_db_fetch_array($sec_query);
    if ($sec_data['customers_status']==0)
    {
      print_xml_status (120, $_POST['action'], 'CAN NOT CHANGE ADMIN USER!', '', '', '');
      return;
    }
  }
  $sql_customers_data_array = array();
  if (isset($_POST['customers_cid'])) $sql_customers_data_array['customers_cid'] = $_POST['customers_cid'];
  if (isset($_POST['customers_firstname'])) $sql_customers_data_array['customers_firstname'] = $_POST['customers_firstname'];
  if (isset($_POST['customers_lastname'])) $sql_customers_data_array['customers_lastname'] = $_POST['customers_lastname'];
  if (isset($_POST['customers_dob'])) $sql_customers_data_array['customers_dob'] = $_POST['customers_dob'];
  if (isset($_POST['customers_email'])) $sql_customers_data_array['customers_email_address'] = $_POST['customers_email'];
  if (isset($_POST['customers_tele'])) $sql_customers_data_array['customers_telephone'] = $_POST['customers_tele'];
  if (isset($_POST['customers_fax'])) $sql_customers_data_array['customers_fax'] = $_POST['customers_fax'];
  if (isset($_POST['customers_gender'])) $sql_customers_data_array['customers_gender'] = $_POST['customers_gender'];
  if ($_POST['customers_price_level'] == 5) $sql_customers_data_array['customers_status'] = 6;
  if ($_POST['customers_price_level'] == 4) $sql_customers_data_array['customers_status'] = 5;
  if ($_POST['customers_price_level'] == 3) $sql_customers_data_array['customers_status'] = 4;
  if ($_POST['customers_price_level'] == 2) $sql_customers_data_array['customers_status'] = 2;
  //if ($_POST['customers_price_level'] == 1) $sql_customers_data_array['customers_status'] = 0; // Tomcraft - 2018-08-18 - Beware that customers_status 0 is the admin customers group!

  if (file_exists('cao_custupd_1.php')) { include('cao_custupd_1.php'); }

  if (isset($_POST['customers_password']))
  {
    $sql_customers_data_array['customers_password'] = xtc_encrypt_password($_POST['customers_password']);
  }
  $sql_address_data_array =array();
  if (isset($_POST['customers_firstname'])) $sql_address_data_array['entry_firstname'] = $_POST['customers_firstname'];
  if (isset($_POST['customers_lastname'])) $sql_address_data_array['entry_lastname'] = $_POST['customers_lastname'];
  if (isset($_POST['customers_company'])) $sql_address_data_array['entry_company'] = $_POST['customers_company'];
  if (isset($_POST['customers_street'])) $sql_address_data_array['entry_street_address'] = $_POST['customers_street'];
  if (isset($_POST['customers_city'])) $sql_address_data_array['entry_city'] = $_POST['customers_city'];
  if (isset($_POST['customers_postcode'])) $sql_address_data_array['entry_postcode'] = $_POST['customers_postcode'];
  if (isset($_POST['customers_gender'])) $sql_address_data_array['entry_gender'] = $_POST['customers_gender'];
  if (isset($_POST['customers_country_id'])) $country_code = $_POST['customers_country_id'];

  $country_query = "SELECT countries_id FROM ".TABLE_COUNTRIES." WHERE countries_iso_code_2 = '".$country_code ."' LIMIT 1";
  $country_result = xtc_db_query($country_query);
  $row = xtc_db_fetch_array($country_result);
  $sql_address_data_array['entry_country_id'] = $row['countries_id'];

  $count_query = xtc_db_query("SELECT count(*) as count FROM " . TABLE_CUSTOMERS . " WHERE customers_id='" . (int)$customers_id . "' LIMIT 1");
  $check = xtc_db_fetch_array($count_query);

  if ($check['count'] > 0)
  {
    $mode = 'UPDATE';
    $address_book_result = xtc_db_query("SELECT customers_default_address_id FROM ".TABLE_CUSTOMERS." WHERE customers_id = '". (int)$customers_id ."' LIMIT 1");
    $customer = xtc_db_fetch_array($address_book_result);
    xtc_db_perform(TABLE_CUSTOMERS, $sql_customers_data_array, 'update', "customers_id = '" . xtc_db_input($customers_id) . "' LIMIT 1");
    xtc_db_perform(TABLE_ADDRESS_BOOK, $sql_address_data_array, 'update', "customers_id = '" . xtc_db_input($customers_id) . "' AND address_book_id = '".$customer['customers_default_address_id']."' LIMIT 1");
    xtc_db_query("update " . TABLE_CUSTOMERS_INFO . " set customers_info_date_account_last_modified = now() where customers_info_id = '" . (int)$customers_id . "'  LIMIT 1");
  }
    else
  {
    $mode= 'APPEND';
    if (strlen($_POST['customers_password'])==0)
    {
      // generate PW if empty
      $pw=xtc_RandomString(8);
      $sql_customers_data_array['customers_password']=xtc_create_password($pw);
    } else
    {
      $pw=$_POST['customers_password'];
    }
    xtc_db_perform(TABLE_CUSTOMERS, $sql_customers_data_array);
    $customers_id = xtc_db_insert_id();
    $sql_address_data_array['customers_id'] = $customers_id;
    xtc_db_perform(TABLE_ADDRESS_BOOK, $sql_address_data_array);
    $address_id = xtc_db_insert_id();
    xtc_db_query("update " . TABLE_CUSTOMERS . " set customers_default_address_id = '" . (int)$address_id . "' where customers_id = '" . (int)$customers_id . "'");
    //JP20080401
    if (!isset($_POST['customers_price_level']))
    {
      xtc_db_query("update " . TABLE_CUSTOMERS . " set customers_status = '" . STANDARD_GROUP . "' where customers_id = '" . (int)$customers_id . "'");
    }
    xtc_db_query("insert into " . TABLE_CUSTOMERS_INFO . " (customers_info_id, customers_info_number_of_logons, customers_info_date_account_created) values ('" . (int)$customers_id . "', '0', now())");
  }

  if (SEND_ACCOUNT_MAIL==true && $mode=='APPEND' && $sql_customers_data_array['customers_email_address']!='')
  {
    // generate mail for customer if customer=new
    require_once(DIR_FS_INC . 'xtc_add_tax.inc.php');
    require_once(DIR_FS_INC . 'xtc_not_null.inc.php');
    require_once(DIR_FS_INC . 'xtc_href_link.inc.php');
    require_once(DIR_FS_INC . 'xtc_date_long.inc.php');
    require_once(DIR_FS_INC . 'xtc_check_agent.inc.php');
    require_once(DIR_FS_INC . 'xtc_php_mail.inc.php');

    require_once(DIR_FS_LANGUAGES . $Lang_folder . '/admin/' . $Lang_folder . '.php');  //JP 20080102


    $smarty = new Smarty;

    //$smarty->assign('language', $check_status['language']);
    $smarty->assign('language', $Lang_folder);

    $smarty->caching = false;
    $smarty->template_dir=DIR_FS_CATALOG.'templates';
    $smarty->compile_dir=DIR_FS_CATALOG.'templates_c';
    $smarty->config_dir=DIR_FS_CATALOG.'lang';

    $smarty->assign('tpl_path', HTTP_SERVER.DIR_WS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/');
    $smarty->assign('logo_path', HTTP_SERVER.DIR_WS_CATALOG.'templates/'.CURRENT_TEMPLATE.'/img/');
    $smarty->assign('NAME',$sql_customers_data_array['customers_lastname'] . ' ' . $sql_customers_data_array['customers_firstname']);
    $smarty->assign('EMAIL',$sql_customers_data_array['customers_email_address']);
    $smarty->assign('PASSWORD',$pw);
    //$smarty->assign('language', $Lang_folder);
    $smarty->assign('content', $module_content);
    $smarty->caching = false;

    $html_mail=$smarty->fetch(CURRENT_TEMPLATE . '/admin/mail/'.$Lang_folder.'/create_account_mail.html');
    $txt_mail=$smarty->fetch(CURRENT_TEMPLATE . '/admin/mail/'.$Lang_folder.'/create_account_mail.txt');

    // send mail with html/txt template
    xtc_php_mail(EMAIL_SUPPORT_ADDRESS,
                 EMAIL_SUPPORT_NAME ,
                 $sql_customers_data_array['customers_email_address'],
                 $sql_customers_data_array['customers_lastname'] . ' ' . $sql_customers_data_array['customers_firstname'],
                 '',
                 EMAIL_SUPPORT_REPLY_ADDRESS,
                 EMAIL_SUPPORT_REPLY_ADDRESS_NAME,
                 '',
                 '',
                 EMAIL_SUPPORT_SUBJECT,
                 $html_mail ,
                 $txt_mail);
  }
  print_xml_status (0, $_POST['action'], 'OK', $mode, 'CUSTOMERS_ID', $customers_id);
}

//--------------------------------------------------------------

function CustomersErase ()
{
  global $_POST;

  $cID  = xtc_db_prepare_input($_POST['cID']);

  $sec_query=xtc_db_query("SELECT customers_status FROM ".TABLE_CUSTOMERS." where customers_id='".$cID."'");
  $sec_data=xtc_db_fetch_array($sec_query);
  if ($sec_data['customers_status']==0)
  {
    print_xml_status (120, $_POST['action'], 'CAN NOT CHANGE ADMIN USER!', '', '', '');
    return;
  }
  if (isset($cID))
  {
    xtc_db_query("update " . TABLE_REVIEWS . " set customers_id = null where customers_id = '" .  $cID . "'");
    xtc_db_query("delete from " . TABLE_ADDRESS_BOOK . " where customers_id = '" . $cID . "'");
    xtc_db_query("delete from " . TABLE_CUSTOMERS . " where customers_id = '" .$cID . "'");
    xtc_db_query("delete from " . TABLE_CUSTOMERS_INFO . " where customers_info_id = '" . $cID. "'");
    xtc_db_query("delete from " . TABLE_CUSTOMERS_BASKET . " where customers_id = '" . $cID . "'");
    xtc_db_query("delete from " . TABLE_CUSTOMERS_BASKET_ATTRIBUTES . " where customers_id = '" . $cID . "'");
    xtc_db_query("delete from " . TABLE_WHOS_ONLINE . " where customer_id = '" . $cID . "'");

    print_xml_status (0, $_POST['action'], 'OK', '', 'SQL_RES1', $res1);
  }
    else
  {
    print_xml_status (99, $_POST['action'], 'PARAMETER ERROR', '', '', '');
  }
}


//--------------------------------------------------------------

function XsellUpdate ()
{
  global $_POST;

  $ProdID = xtc_db_prepare_input($_POST['prodid']);
  $XsellID  = xtc_db_prepare_input($_POST['xsellid']);

  if (isset($ProdID) && isset($XsellID))
  {

      // ueberpruefen ob Daten schon vorhanden sind
    $SQL = "select products_id, xsell_id from " . TABLE_PRODUCTS_XSELL . " where products_id='"
            . $ProdID . "' and xsell_id='" . $XsellID . "'";


    $count_query = xtc_db_query($SQL);
    $exists = xtc_db_fetch_array($count_query);
    if ($exists > 0)
    {
      $exists = 1;
      // Eintrag bereits vorhanden, tue nichts
    }
    else
    {
      $exists = 0;
      $res = xtc_db_query("replace into " . TABLE_PRODUCTS_XSELL . " (products_id, xsell_id) Values ('" . $ProdID ."', '" . $XsellID . "')");
    }

    print_xml_status (0, $_POST['action'], 'OK', '', 'SQL_RES', $res);
  }
    else
  {
    print_xml_status (99, $_POST['action'], 'PARAMETER ERROR', '', '', '');
  }
}

//--------------------------------------------------------------

function XsellErase ()
{
  global $_POST;

  $ProdID = xtc_db_prepare_input($_POST['prodid']);
  $XsellID  = xtc_db_prepare_input($_POST['xsellid']);

  if (isset($ProdID) && isset($XsellID))
  {
    $res = xtc_db_query("delete from " . TABLE_PRODUCTS_XSELL . " where products_id='" . $ProdID ."' and xsell_id='" . $XsellID . "'");

    print_xml_status (0, $_POST['action'], 'OK', '', 'SQL_RES', $res);
  }
    else
  {
    print_xml_status (99, $_POST['action'], 'PARAMETER ERROR', '', '', '');
  }
}


//--------------------------------------------------------------

function SendLog ()
{
  global $version_nr, $version_datum, $logger;

  SendHTMLHeader();

  echo '<html><head></head><body>';
  echo '<h3>Shoptransfer XTC<->Winline</h3>';
  echo '<h4>Version ' . $version_nr . ' Stand : ' . $version_datum .'</h4>'.
       '<h4>Transfer-Log:</h4>';

  if (LOGGER==true)
  {
    $sql = 'select * from cao_log order by date desc limit 0,100';

    $res = xtc_db_query($sql);
    while ($log = xtc_db_fetch_array($res))
    {
      echo 'Date:' . $log['date'] . '<br>' . "\n";
      echo 'Method:' . $log['method'] . '<br>' . "\n";
      echo 'Action:' . $log['action'] . '<br>' . "\n";
      echo 'Post-Data:<br>' . nl2br($log['post_data']) . '<br>' . "\n";
      echo 'Get-Data:<br>' . nl2br($log['get_data']) . '<br>' . "\n";

      echo "<hr>" . "\n";
    }
  }
   else
  {
    echo '<br><br><br><hr><h5>Der Logger wurde im Script deaktiviert !</h5><hr>';

  }

  echo '<br><br></body></html>';

}

//--------------------------------------------------------------
//                     Ende Funktionen
//--------------------------------------------------------------




  $table_has_products_image_medium = false;
  $table_has_products_image_large = false;

  $images_query = xtc_db_query(' SHOW COLUMNS FROM '.TABLE_PRODUCTS);
  while($column = xtc_db_fetch_array($images_query)) {
        if ($column['Field'] == 'products_image_medium') {
          $table_has_products_image_medium = true;
        }
        if ($column['Field'] == 'products_image_large') {
          $table_has_products_image_large = true;
        }
  }
  if ($table_has_products_image_medium && $table_has_products_image_large) {
      define('DREI_PRODUKTBILDER', true);
  } else {
      define('DREI_PRODUKTBILDER', false);
  }


  if (LOGGER==true)
  {
    $pdata ='';
    foreach ($_POST as $key => $value)
    {
       if (is_array($value))
       {
         foreach ($value as $key1 => $value1)
         {
           $pdata .= xtc_db_input($key)."[" . xtc_db_input($key1)."] => ".xtc_db_input($value1)."\\r\\n";
         }
       }
         else
       {
         $pdata .= xtc_db_input($key)." => ".xtc_db_input($value)."\\r\\n";
       }
    }

    $gdata ='';
    foreach ($_GET as $key => $value)
    {
       $gdata .= xtc_db_input($key)." => ".xtc_db_input($value)."\\r\\n";
    }


     if ($_GET['action']!='send_log')
     {
        xtc_db_query("INSERT INTO cao_log
                  (date,user,pw,method,action,post_data,get_data) VALUES
                  (NOW(),'".xtc_db_input($user)."','".xtc_db_input($password)."','".$REQUEST_METHOD."','".xtc_db_input($_POST['action'])."','".$pdata."','".$gdata."')");
     }
  }




//-------------------------------------------------------------------------------------------------------
//
//-------------------------------------------------------------------------------------------------------

  require_once(DIR_FS_INC . 'xtc_not_null.inc.php');
  require_once(DIR_FS_INC . 'xtc_redirect.inc.php');
  require_once(DIR_FS_INC . 'xtc_rand.inc.php');

  //----------------------------------------------------------------------------
  class upload {
    var $file, $filename, $destination, $permissions, $extensions, $tmp_filename;

    function upload($file = '', $destination = '', $permissions = '777', $extensions = '') {

      $this->set_file($file);
      $this->set_destination($destination);
      $this->set_permissions($permissions);
      $this->set_extensions($extensions);

      if (xtc_not_null($this->file) && xtc_not_null($this->destination)) {
        if ( ($this->parse() == true) && ($this->save() == true) ) {
          return true;
        } else {
          return false;
        }
      }
    }
  //----------------------------------------------------------------------------
    function parse() {
      global $messageStack;
      if (isset($_FILES[$this->file])) {
        $file = array('name' => $_FILES[$this->file]['name'],
                      'type' => $_FILES[$this->file]['type'],
                      'size' => $_FILES[$this->file]['size'],
                      'tmp_name' => $_FILES[$this->file]['tmp_name']);
      } elseif (isset($_FILES[$this->file])) {

        $file = array('name' => $_FILES[$this->file]['name'],
                      'type' => $_FILES[$this->file]['type'],
                      'size' => $_FILES[$this->file]['size'],
                      'tmp_name' => $_FILES[$this->file]['tmp_name']);
      } else {
        $file = array('name' => $GLOBALS[$this->file . '_name'],
                      'type' => $GLOBALS[$this->file . '_type'],
                      'size' => $GLOBALS[$this->file . '_size'],
                      'tmp_name' => $GLOBALS[$this->file]);
      }

      if ( xtc_not_null($file['tmp_name']) && ($file['tmp_name'] != 'none') && is_uploaded_file($file['tmp_name']) ) {
        if (sizeof($this->extensions) > 0) {
          if (!in_array(strtolower(substr($file['name'], strrpos($file['name'], '.')+1)), $this->extensions)) {
            //$messageStack->add_session(ERROR_FILETYPE_NOT_ALLOWED, 'error');

            return false;
          }
        }

        $this->set_file($file);
        $this->set_filename($file['name']);
        $this->set_tmp_filename($file['tmp_name']);

        return $this->check_destination();
      } else {

             //if ($file['tmp_name']=='none') $messageStack->add_session(WARNING_NO_FILE_UPLOADED, 'warning');
        return false;
      }
    }
  //----------------------------------------------------------------------------
    function save() {
      global $messageStack;

      if (substr($this->destination, -1) != '/') $this->destination .= '/';

      // GDlib check
      if (!function_exists(imagecreatefromgif)) {

        // check if uploaded file = gif
        if ($this->destination==DIR_FS_CATALOG_ORIGINAL_IMAGES) {
            // check if merge image is defined .gif
            if (strstr(PRODUCT_IMAGE_THUMBNAIL_MERGE,'.gif') ||
                strstr(PRODUCT_IMAGE_INFO_MERGE,'.gif') ||
                strstr(PRODUCT_IMAGE_POPUP_MERGE,'.gif')) {

                //$messageStack->add_session(ERROR_GIF_MERGE, 'error');
                return false;

            }
            // check if uploaded image = .gif
            if (strstr($this->filename,'.gif')) {
             //$messageStack->add_session(ERROR_GIF_UPLOAD, 'error');
             return false;
            }

        }

      }

      if (move_uploaded_file($this->file['tmp_name'], $this->destination . $this->filename)) {
        chmod($this->destination . $this->filename, $this->permissions);

        //$messageStack->add_session(SUCCESS_FILE_SAVED_SUCCESSFULLY, 'success');

        return true;
      } else {
        //$messageStack->add_session(ERROR_FILE_NOT_SAVED, 'error');

        return false;
      }
    }
  //----------------------------------------------------------------------------
    function set_file($file) {
      $this->file = $file;
    }
  //----------------------------------------------------------------------------
    function set_destination($destination) {
      $this->destination = $destination;
    }
  //----------------------------------------------------------------------------
    function set_permissions($permissions) {
      $this->permissions = octdec($permissions);
    }
  //----------------------------------------------------------------------------
    function set_filename($filename) {
      $this->filename = $filename;
    }
  //----------------------------------------------------------------------------
    function set_tmp_filename($filename) {
      $this->tmp_filename = $filename;
    }
  //----------------------------------------------------------------------------
    function set_extensions($extensions) {
      if (xtc_not_null($extensions)) {
        if (is_array($extensions)) {
          $this->extensions = $extensions;
        } else {
          $this->extensions = array($extensions);
        }
      } else {
        $this->extensions = array();
      }
    }
  //----------------------------------------------------------------------------
    function check_destination() {
      global $messageStack;

      if (!is_writeable($this->destination)) {
        if (is_dir($this->destination)) {
          //$messageStack->add_session(sprintf(ERROR_DESTINATION_NOT_WRITEABLE, $this->destination), 'error');
        } else {
          //$messageStack->add_session(sprintf(ERROR_DESTINATION_DOES_NOT_EXIST, $this->destination), 'error');
        }

        return false;
      } else {
        return true;
      }
    }
  }
?>
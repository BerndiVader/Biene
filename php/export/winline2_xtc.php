<?php
ob_start();

enum Codes: int
{
  case OK=0;
  case VERSION=111;
  case FAILED=-1;
  case CONTINUE=1;
  case ERROR=-101;
  case RUNTIME_ERROR=-122;
  case PHP_EXCEPTION=-133;

  public function code(): int
  {
    return $this->value;
  }

  public static function contains(int $value): bool
  {
    foreach(self::cases() as $case)
    {
      if($case->value===$value)
      {
        return true;
      }
    }
    return false;
  }

}

class Wl2Exception extends Exception
{
    private Codes $codes;

    public function __construct(string $message="",?Codes $codes=null)
    {
      parent::__construct($message);
      $this->codes=$codes??Codes::FAILED;
    }

    public function codes(): Codes
    {
        return $this->codes;
    }

    public function __toString(): string
    {
      return __CLASS__ .": [{$this->codes->value}][{$this->code}]: {$this->message}\n";
    }

}

function print_xml_error(Codes $codes,string $action,string $msg,string $error_code="-999",string $trace="No further information.")
{
  $schema='<?xml version="1.0" encoding="'.CHARSET.'"?>'."\n".
            '<STATUS>'."\n".
            '<STATUS_DATA>'."\n".
            '<CODE>'.(string)$codes->value.'</CODE>'."\n" .
            '<ACTION>'.$action.'</ACTION>'."\n".
            '<MESSAGE>'.$msg.'</MESSAGE>'."\n".
            '<ERROR_CODE>'.$error_code.'</ERROR_CODE>'."\n".
            '<ERROR>'.$trace.'</ERROR>'."\n".
            '</STATUS_DATA>'."\n".'</STATUS>'."\n\n";

  header("Last-Modified: ".gmdate ("D, d M Y H:i:s")." GMT");
  header("Cache-Control: no-cache, must-revalidate");
  header("Pragma: no-cache");
  header("Content-type: text/xml");
  echo $schema;
  return;
}

function error_handler($errno,$errstr,$errfile,$errline)
{
  throw new ErrorException($errstr,0,$errno,$errfile,$errline);
}

function exception_handler($exception)
{
  if($exception instanceof Wl2Exception)
  {
    $codes=$exception->codes();
    $code=$codes->value;
  }
  else
  {
    $codes=Codes::PHP_EXCEPTION;
    $code=$exception->getCode();
  }

  $action=isset($_POST['action'])?(string)$_POST['action']:(isset($_GET['action'])?(string)$_GET['action']:'UNDEFINED');
  $message=(string)$exception->getMessage();

  $trace=$exception->getTraceAsString();
  if(empty($trace))
  {
    $trace="No Trace Available";
  }
  $trace="{$code}:".$trace;

  print_xml_error($codes,$action,$message,(string)$code,$trace);
  exit;
}

function shutdown_handler()
{
  $error=error_get_last();

  if($error!==null)
  {
    $buffer=ob_get_contents();
    ob_end_clean();

    $action=isset($_POST['action'])?(string)$_POST['action']:(isset($_GET['action'])?(string)$_GET['action']:'UNDEFINED');
    $errno=$error["type"];
    $message = $error["message"]??"Unknown error";
    $file=$error["file"]??"Unknown file";
    $line=$error["line"]??"Unknown line";
    $trace="Fatal Error in $file on line $line";
    print_xml_error(Codes::PHP_EXCEPTION,$action,$message,(string)$errno,$trace);
  }
  else
  {
    ob_end_flush();
  }

}

set_exception_handler("exception_handler");
set_error_handler("error_handler");
register_shutdown_function("shutdown_handler");

try
{
  require_once '../includes/application_top_export.php';
  require_once DIR_FS_INC.'xtc_try_upload.inc.php';
  
  $version_nr='1.06';
  $version_datum='2025.02.05';

  $LangID=2;
  $Lang_folder='german';
  
  define('SWITCH_MWST',false);
  define ('LOGGER',false);
  define('USE_3IMAGES',false);
  define('USE_VPE',false);
  define('SEND_ACCOUNT_MAIL',false);
  define ('_VALID_XTC',false);
    
  define('SET_TIME_LIMIT',1);
  define('CHARSET',(defined('DB_SERVER_CHARSET')&&strpos(DB_SERVER_CHARSET,'utf8')!==false)?'utf-8':'iso-8859-15');
  define('DIR_FS_LANGUAGES',DIR_WS_LANGUAGES);
  define('CONTENT_CONDITIONS','');
  define('DIR_FS_ADMIN',DIR_FS_DOCUMENT_ROOT.DIR_ADMIN);

  require_once 'winline2_xtc_functions.php';

  $user=$password="";
  $headers=getallheaders();

  if(isset($headers['user'])&&isset($headers['password']))
  {
    $user=$headers['user'];
    $password=$headers['password'];
  }

  if(isset($headers['X-Authorization']))
  {
    //print_xml_status("111","Bearer-Token-Test",$headers['X-Authorization']);
    //exit;
  }

  if($user===''||$password==='')
  {
    throw new Wl2Exception("MISSING LOGIN CREDENTIALS",Codes::RUNTIME_ERROR);
  }
  else
  {
    // security  1.check if admin user with this mailadress exits, and got access to xml-export
    //           2.check if password = true
  
    if(column_exists('admin_access','xml_export')===false)
    {
      xtc_db_query("ALTER TABLE admin_access ADD xml_export INT(1)  DEFAULT '0';");
    }
  
    $check_customer_query=xtc_db_query("select customers_id,
                                        customers_status,
                                        customers_password
                                        from customers where
                                        customers_email_address='".xtc_db_input($user)."'");
  
    if(xtc_db_num_rows($check_customer_query)===false)
    {
      throw new Wl2Exception("UNKNOWN LOGIN CREDENTIALS",Codes::RUNTIME_ERROR);
    }
    else
    {
      $check_customer=xtc_db_fetch_array($check_customer_query);
      // check if customer is Admin
      if($check_customer['customers_status']!='0') 
      {
        throw new Wl2Exception("INVALID LOGIN CREDENTIALS",Codes::RUNTIME_ERROR);
      }
      // check if Admin is allowed to access xml_export
      $access_query=xtc_db_query("SELECT
                                  xml_export
                                  from admin_access
                                  WHERE customers_id='".$check_customer['customers_id']."'");
      $access_data=xtc_db_fetch_array($access_query);
      if($access_data['xml_export']!=1) 
      {
        throw new Wl2Exception("INVALID LOGIN CREDENTIALS",Codes::RUNTIME_ERROR);
      }
  
      if(!( ($check_customer['customers_password']==$password) or
            ($check_customer['customers_password']==md5($password)) or
            ($check_customer['customers_password']==md5(substr($password,2,40)))
        ))
      {
        throw new Wl2Exception("WRONG LOGIN CREDENTIALS",Codes::RUNTIME_ERROR);
      }
    }
  }
  
  if($_SERVER['REQUEST_METHOD']==='GET') 
  {
    if(isset($_GET['action']))
    {
      $action=$_GET['action'];
      switch($action)
      {
        case'version':
          SendScriptVersion($action);
          exit;
        default:
          throw new Wl2Exception("UNKNOWN ACTION COMMAND",Codes::RUNTIME_ERROR);
      }
    }
  }
  else if($_SERVER['REQUEST_METHOD']==='POST')
  {
    if(isset($_GET['action']))
    {
      $action=$_GET['action'];
      switch($action)
      {
        case'img_upload':
          ImageUpload($action);
          exit;
        case'csv_upload':
          CsvFileUpload($action);
          exit;
      }
    }
    else if(isset($_POST['action']))
    {
      $action=$_POST['action'];
      switch($action) 
      {
        case'log':
          PostBieneLog($action);
          exit;
        case'img_upload':
          ImageUpload($action);
          exit;
        case'csv_upload':
          CsvFileUpload($action);
          exit;
        case'csv_import':
          CsvFileImport2($action);
          exit;
        case'img_process':
          ImageProzess($action);
          exit;
        case'img_validate':
          ValidateImage($action);
          exit;
        case'img_validate_file':
          ValidateImageFile($action);
          exit;
      }
    }
    throw new Wl2Exception("UNKNOWN ACTION COMMAND",Codes::RUNTIME_ERROR);
  }

}
catch(Exception $e)
{
  exception_handler($e);
}

?>
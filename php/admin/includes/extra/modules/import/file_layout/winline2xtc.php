<?PHP
defined('_VALID_XTC') or die('Direct Access to this location is not allowed.');

if(defined("WL2XTC")&&WL2XTC===true)
{
    $handler=fopen($this->ImportDir.$this->filename,'r');
    if($handler!==false)
    {
        $struct=fgetcsv($handler,0,$this->seperator);
        fclose($handler);

        if($struct!==false)
        {
            $wl2_layout=array_fill_keys($struct,"");
            foreach($file_layout as $key=>$value)
            {
                if(!array_key_exists($key,$wl2_layout))
                {
                    $wl2_layout[$key]=$value;
                }
            }
            $file_layout=$wl2_layout;

        }
        else
        {
            throw new Wl2Exception("CSV Struktur konnte nicht ermittelt werden.",Codes::RUNTIME_ERROR);
        }

    }
    else
    {
        throw new Wl2Exception("CSV File nicht gefunden.",Codes::RUNTIME_ERROR);
    }

}


?>
<?php

require DIR_FS_ADMIN.'/includes/classes/import.php';
require DIR_FS_INC.'xtc_format_filesize.inc.php';
require DIR_FS_INC.'xtc_get_customers_statuses.inc.php';

class Wl2Import extends xtcImport
{

    public function __construct($filename)
    {
        parent::__construct($filename);
    }

    function get_mfn() 
    {
        $mfn_array=[];
        $mfn_query=xtc_db_query("SELECT manufacturers_id,manufacturers_name FROM ".TABLE_MANUFACTURERS);
        if($mfn_query)
        {
            while($mfn=xtc_db_fetch_array($mfn_query))
            {
                $mfn_array[$mfn['manufacturers_name']]=['id'=>$mfn['manufacturers_id']];
            }
        }

        return $mfn_array;
    }

}

class Wl2Export extends xtcExport
{

}

?>
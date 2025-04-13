<?PHP
defined('_VALID_XTC') or die('Direct Access to this location is not allowed.');

if(defined("WL2XTC")&&WL2XTC===true&&$this->FileSheme["action"]=="Y")
{
    switch($dataArray["action"])
    {
        case "insert":
        {
            $query="SELECT products_status FROM products WHERE products_model ='".xtc_db_input($dataArray['p_model'])."'";
            $raw=xtc_db_query($query);
            if($result=xtc_db_fetch_array($raw))
            {
                $products_array["products_status"]=(int)$result["products_status"];
            }
            else
            {
                $products_array["products_status"]=1;
            }
            break;
        }
        case "update":
        {
            if(isset($products_array["products_status"]))
            {
                $query="SELECT products_status FROM products WHERE products_model ='".xtc_db_input($dataArray['p_model'])."'";
                $raw=xtc_db_query($query);
                if($result=xtc_db_fetch_array($raw))
                {
                    $products_array["products_status"]=(int)$result["products_status"];
                }
                else
                {
                    $products_array["products_status"]=1;
                }
            }
            break;
        }
    }

}

?>
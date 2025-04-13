<?PHP
defined('_VALID_XTC') or die('Direct Access to this location is not allowed.');

if(defined("WL2XTC")&&WL2XTC===true&&$this->FileSheme["action"]=="Y")
{
    switch($dataArray["action"])
    {
        case "delete":
        {
            $query=xtc_db_query("SELECT products_id FROM products WHERE products_model ='".xtc_db_input($dataArray['p_model'])."'");
            $data=xtc_db_fetch_array($query);
            if(isset($data))
            {
                xtc_db_query("DELETE FROM ".TABLE_PRODUCTS_TO_CATEGORIES." where products_id='".$data["products_id"]."'");
                xtc_remove_product($data["products_id"]);
            }
            break;
        }
    }

}

?>
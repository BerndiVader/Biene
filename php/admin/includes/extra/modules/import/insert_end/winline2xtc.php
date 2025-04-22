<?PHP
defined('_VALID_XTC') or die('Direct Access to this location is not allowed.');

if(defined("WL2XTC")&&WL2XTC===true&&$this->FileSheme["action"]=="Y")
{

    switch($dataArray["action"])
    {
        case "update":
        case "insert":
        {
            $query="
                SELECT pd.products_order_description FROM products AS p
                JOIN products_description AS pd 
                WHERE p.products_model='".xtc_db_input($dataArray['p_model'])."'
                AND p.products_id=pd.products_id;
            ";
            $raw=xtc_db_query($query);
            if($result=xtc_db_fetch_array($raw))
            {
                if(!isset($result['products_order_description'])||$result['products_order_description']===NULL)
                {
                    $query="
                        UPDATE products_description AS pd
                        JOIN products AS p 
                        ON p.products_id=pd.products_id
                        SET pd.products_order_description=''
                        WHERE p.products_model='".xtc_db_input($dataArray['p_model'])."';
                    ";
                    xtc_db_query($query);
                }
            }            
            break;
        }
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
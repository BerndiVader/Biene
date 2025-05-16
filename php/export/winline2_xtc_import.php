<?php

require DIR_FS_ADMIN.'/includes/classes/import.php';
require DIR_FS_INC.'xtc_format_filesize.inc.php';
require DIR_FS_INC.'xtc_get_customers_statuses.inc.php';

class Wl2Import extends xtcImport
{

    public function __construct($filename)
    {
        parent::__construct($filename);
        $this->seperator="|";
    }

    private function updateProducts2CatTable(int $pID,string $catTree)
    {
        $p_cats=explode("']['",trim($catTree,"[]'"));

        $query=
        "   SELECT cat_desc.categories_name,cat_desc.categories_id FROM categories AS cat
            JOIN categories_description AS cat_desc
            ON cat_desc.categories_id=cat.categories_id
            JOIN products_to_categories AS prod_cat
            ON prod_cat.categories_id=cat.categories_id
            WHERE prod_cat.products_id={$pID};
        ";

        $request=xtc_db_query($query);
        if($request)
        {
            while($data=xtc_db_fetch_array($request))
            {
                if(isset($data['categories_name']))
                {
                    if(!in_array($data['categories_name'],$p_cats))
                    {
                        $cID=$data['categories_id'];
                        xtc_db_query("  DELETE FROM products_to_categories
                                        WHERE products_id={$pID} AND categories_id={$cID};");
                    }
                }
            }
        }

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

    private function getStoredCatID(array $tree,string $catTree) 
    {
        $keys=explode("']['",trim($catTree,"[]'"));
        $current=$tree;
    
        foreach($keys as $key)
        {
            if(isset($current[$key]))
            {
                $current=$current[$key];
            }
            else
            {
                return null;
            }
        }
    
        return $current['ID']??null;
    }

    private function addCatToTree(string $catTree,int $catID)
    {
        $keys=explode("']['",trim($catTree,"[]'"));
        $current=&$this->CatTree;

        foreach($keys as $key)
        {
            if(!isset($current[$key]))
            {
                $current[$key]=[];
            }
            $current=&$current[$key];
        }

        $current['ID']=$catID;
    }
    
    function insertCategory(&$dataArray,$pID,$mode='insert') 
    {
        $cat=[];
        $catTree="";
        for($i=0;$i<$this->catDepth;$i++)
        {
            if(trim($dataArray["p_cat.{$i}"])!="")
            {
                $cat[$i]=xtc_db_prepare_input(trim($dataArray["p_cat.{$i}"]));
                $catTree.='[\''.xtc_db_input($cat[$i]).'\']';
            }
        }

        $dataCatTree=$catTree;

        $ID=$this->getStoredCatID($this->CatTree,$catTree);

        if(isset($ID)&&(is_int($ID)||$ID=='0'))
        {
            $this->insertPtoCconnection($pID,$ID);
        }
        else
        {
            $catTree="";
            $parTree="";
            $curr_ID=0;
            for($i=0,$cc=count($cat);$i<$cc;$i++)
            {
                $catTree.='[\''.xtc_db_input($cat[$i]).'\']';
                $ID=$this->getStoredCatID($this->CatTree,$catTree);

                if(isset($ID)&&(is_int($ID)||$ID=='0'))
                {
                    $curr_ID=$ID;
                }
                else
                {
                    $parent=$this->getStoredCatID($this->CatTree,$parTree);
                    $cat_query=xtc_db_query(
                        "SELECT c.categories_id
                        FROM ".TABLE_CATEGORIES." c
                        JOIN ".TABLE_CATEGORIES_DESCRIPTION." cd
                        ON cd.categories_id = c.categories_id
                        WHERE cd.categories_name = '".xtc_db_input($cat[$i])."'
                        AND cd.language_id = '".$this->languages[0]['id']."'
                        AND c.parent_id = '".$parent."'"
                    );
                    
                    if(!xtc_db_num_rows($cat_query))
                    {
                        $categorie_data=[
                            'parent_id'=>$parent,
                            'categories_status'=>1,
                            'date_added'=>'now()',
                            'last_modified'=>'now()'
                        ];

                        xtc_db_perform(TABLE_CATEGORIES,$categorie_data);
                        $cat_id=xtc_db_insert_id();
                        $this->counter['cat_new']++;

                        $this->addCatToTree($parTree.'[\''.xtc_db_input($cat[$i]).'\']',$cat_id);
                        $parent=$cat_id;

                        for ($i_insert=0;$i_insert<$this->sizeof_languages;$i_insert++)
                        {
                            $categorie_data=[
                                'language_id'=>$this->languages[$i_insert]['id'],
                                'categories_id'=>$cat_id,
                                'categories_name'=>$cat[$i]
                            ];
                            xtc_db_perform(TABLE_CATEGORIES_DESCRIPTION,$categorie_data);
                        }
                    } 
                    else 
                    {
                        $this->counter['cat_touched']++;
                        $cData=xtc_db_fetch_array($cat_query);
                        $cat_id=$cData['categories_id'];

                        $this->addCatToTree($parTree.'[\''.xtc_db_input($cat[$i]).'\']',$cat_id);
                    }
                }
                $parTree=$catTree;
            }
            $this->insertPtoCconnection($pID,$cat_id);
        }

        $this->updateProducts2CatTable($pID,$dataCatTree);
    }

}

class Wl2Export extends xtcExport
{

    public function __construct($filename)
    {
        parent::__construct($filename);
        if(empty($this->man))
        {
            /*
                This avoids php exception if manufacturers table is empty.
            */
            $this->man=[
                '0'=>''
            ];
        }
    }

    

}

?>
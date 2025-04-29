package com.gmail.berndivader.biene.db;

import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.enums.Tasks;

public class CatalogTree extends SimpleResultQuery {
	
	private static final String QUERY="SELECT c000 AS ID, c001 AS NAME,c002 AS RANK FROM t309 WHERE mesocomp='%s' AND mesoyear=%d;";
	public LinkedHashMap<String,Object>tree;

	public CatalogTree() {
		super(String.format(QUERY,Config.data.meso_client(),Config.data.meso_year()),Tasks.UNKOWN,5l);
	}

	@Override
	public void completed(ResultSet result) {
		tree=new LinkedHashMap<>();
		
	}

	@Override
	public void failed(Void error) {
		tree=null;
	}
	
	private void add(LinkedHashMap<String,Object>map,String[]parts) {
		LinkedHashMap<String,Object>current=map;
		for(String part:parts) {
			current.putIfAbsent(part,new LinkedHashMap<>(Map.of("name",part)));
            current=(LinkedHashMap<String,Object>)current.get(part);
        }
    }
}

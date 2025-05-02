package com.gmail.berndivader.biene.db;

import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

import com.gmail.berndivader.biene.Logger;
import com.gmail.berndivader.biene.config.Config;
import com.gmail.berndivader.biene.enums.Tasks;

public class CatalogTree extends SimpleResultQuery {
	
	private static final String QUERY="SELECT c000 AS ID, c001 AS NAME,c002 AS DEPTH FROM t309 WHERE mesocomp='%s' AND mesoyear=%d;";
	public LinkedHashMap<String,Object>tree;

	public CatalogTree() {
		super(String.format(QUERY,Config.data.meso_client(),(Config.data.meso_year()-1900)*12),Tasks.UNKOWN,5l);
	}

	@Override
	public void completed(ResultSet result) {
		tree=new LinkedHashMap<>();
		
		try {
			while(result.next()) {
				String id=result.getString("ID");
				String name=result.getString("NAME");
				int depth=result.getInt("DEPTH");
				add(tree,id.split("-"),name,depth,0);
			}
		} catch(Exception e) {
			Logger.$(e);
			tree=null;
		}
		
	}

	@Override
	public void failed(Void error) {
		tree=null;
	}
	
	@SuppressWarnings("unchecked")
	private void add(LinkedHashMap<String,Object>map,String[]parts,String name,int depth,int currentDepth) {
		if(currentDepth>=parts.length) return;
		
		String part=parts[currentDepth].equals("00000")?"INFO":parts[currentDepth];
		
		if(currentDepth==depth) {
			map.putIfAbsent(part,new LinkedHashMap<>(Map.of("NAME",name,"DEPTH",depth)));
		} else {
			map.putIfAbsent(part,new LinkedHashMap<>());
			Object child=map.get(part);
			if(child instanceof LinkedHashMap<?,?>) add((LinkedHashMap<String,Object>)child,parts,name,depth,currentDepth+1);
		}
		
	}
	
}

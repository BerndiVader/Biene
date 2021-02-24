package com.gmail.berndivader.biene.rtf2html;

import java.util.ArrayList;
import java.util.List;

public 
class 
RtfGroup 
extends 
RtfElement
{
	public RtfGroup parent;
	public List<RtfElement> children;

	public RtfGroup() {
		parent=null;
		children=new ArrayList<>();
	}

	public String getType() {
		if(children.isEmpty()) return "";
		RtfElement child=children.get(0);
		if(!(child instanceof RtfControlWord)) return "";
		return ((RtfControlWord)child).word;
	}

	public boolean isDestination() {
		if(children.isEmpty()) return false;
		RtfElement child=children.get(0);
		if(!(child instanceof RtfControlSymbol)) return false;
		return((RtfControlSymbol)child).symbol=='*';
	}

	public void dump() {
		dump(0);
	}

	@Override
	public void dump(int level) {
		System.out.println("<div>");
		indent(level);
		System.out.println("{");
		System.out.println("</div>");

		for(RtfElement child:children) {
			if(child instanceof RtfGroup) {
				RtfGroup group=(RtfGroup)child;

				if(group.getType().equals("fonttbl")) continue;
				if(group.getType().equals("colortbl")) continue;
				if(group.getType().equals("stylesheet")) continue;
				if(group.getType().equals("info")) continue;

				if(group.getType().length()>=4&&group.getType().substring(0,4).equals("pict")) continue;
				if(group.isDestination()) continue;
			}
			child.dump(level+2);
		}
		System.out.println("<div>");
		indent(level);
		System.out.println("}");
		System.out.println("</div>");
	}
}
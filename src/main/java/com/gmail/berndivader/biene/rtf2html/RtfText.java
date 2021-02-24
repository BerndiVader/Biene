package com.gmail.berndivader.biene.rtf2html;

public 
class 
RtfText
extends 
RtfElement 
{
	public String text;
	
	@Override
	public void dump(int level) {
		System.out.println("<div style='color:red'>");
		indent(level);
		System.out.println("TEXT " + text);
		System.out.println("</div>");
	}
}
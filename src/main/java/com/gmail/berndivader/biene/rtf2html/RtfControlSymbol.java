package com.gmail.berndivader.biene.rtf2html;

public 
class 
RtfControlSymbol 
extends
RtfElement 
{
	public char symbol;
	public int parameter=0;
	
	@Override
	public void dump(int level) {
		System.out.println("<div style='color:blue'>");
		indent(level);
		System.out.println("SYMBOL "+symbol+" ("+parameter+")");
		System.out.println("</div>");
	}
}
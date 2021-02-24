package com.gmail.berndivader.biene.rtf2html;

public 
abstract
class
RtfElement
{
	protected abstract void dump(int level);

	protected void indent(int level) {
		for (int i1=0;i1<level;i1++) {
			System.out.println("&nbsp;");
		}
	}
}
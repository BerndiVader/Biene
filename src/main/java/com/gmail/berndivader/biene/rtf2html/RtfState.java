package com.gmail.berndivader.biene.rtf2html;

public 
class
RtfState
implements
Cloneable 
{
	public boolean bold;
	public boolean italic;
	public boolean underline;
	public boolean strike;
	public boolean hidden;
	public int fontSize;
	public int textColor;
	public int background;
	
	public RtfState() {
		reset();
	}
	
	@Override
	public Object clone() {
		RtfState newState=new RtfState();
		newState.bold=this.bold;
		newState.italic=this.italic;
		newState.underline=this.underline;
		newState.strike=this.strike;
		newState.hidden=this.hidden;
		newState.fontSize=this.fontSize;
		newState.textColor=this.textColor;
		newState.background=this.background;
		return newState;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null) return false;
		if(!(obj instanceof RtfState)) return false;

		RtfState anotherState=(RtfState)obj;
		return this.bold==anotherState.bold&&this.italic==anotherState.italic
				&&this.underline==anotherState.underline&&this.strike==anotherState.strike
				&&this.hidden==anotherState.hidden&&this.fontSize==anotherState.fontSize
				&&this.textColor==anotherState.textColor&&this.background==anotherState.background;
	}

	public void reset() {
		bold=false;
		italic=false;
		underline=false;
		strike=false;
		hidden=false;
		fontSize=0;
		textColor=0;
		background=0;
	}
}
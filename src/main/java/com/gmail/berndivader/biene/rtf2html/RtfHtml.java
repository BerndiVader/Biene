package com.gmail.berndivader.biene.rtf2html;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public
class 
RtfHtml 
{
	private String output;
	private Stack<RtfState>states;
	private RtfState state;
	private RtfState previousState;
	private Map<String,Boolean>openedTags;
	private List<String>colortbl;

	public String format(RtfGroup root) {
		return format(root,false);
	}

	public String format(RtfGroup root,boolean page) {
		previousState=null;
		openedTags=new LinkedHashMap<>();
		openedTags.put("span",false);
		openedTags.put("p",true);

		states=new Stack<>();
		state=new RtfState();
		states.push(state);

		output="<p>";
		formatGroup(root);
		if (page) wrapTags();
		return output;
	}

	protected void extractColorTable(List<RtfElement>colorTblGrp) {
		List<String>colortbl=new ArrayList<>();
		colortbl.add(null);

		int c=colorTblGrp.size();
		String color="";

		for(int i=2;i<c;i++) {
			if(colorTblGrp.get(i) instanceof RtfControlWord) {
				int red=((RtfControlWord)colorTblGrp.get(i)).parameter;
				int green=((RtfControlWord)colorTblGrp.get(i+1)).parameter;
				int blue=((RtfControlWord)colorTblGrp.get(i+2)).parameter;

				color=String.format("#%02x%02x%02x",red,green,blue);
				i+=2;
			} else if(colorTblGrp.get(i) instanceof RtfText) {
				colortbl.add(color);
			}
		}

		this.colortbl=colortbl;
	}

	protected void formatGroup(RtfGroup group) {
		if(group==null) return;
		if(group.getType().equals("fonttbl")) return;
		if(group.getType().equals("colortbl")) {
			extractColorTable(group.children);
			return;
		}
		if(group.getType().equals("stylesheet")) return;
		if(group.getType().equals("info")) return;
		if(group.getType().length() >= 4 && group.getType().substring(0, 4).equals("pict")) return;
		if(group.isDestination()) return;
		
		state = (RtfState) state.clone();
		states.push(state);

		for(RtfElement child:group.children) {
			if (child instanceof RtfGroup) {
				formatGroup((RtfGroup)child);
			} else if(child instanceof RtfControlWord) {
				formatControlWord((RtfControlWord)child);
			} else if(child instanceof RtfControlSymbol) {
				formatControlSymbol((RtfControlSymbol)child);
			} else if(child instanceof RtfText) {
				formatText((RtfText)child);
			}
		}

		states.pop();
		state = states.peek();
	}

	protected void formatControlWord(RtfControlWord rtfWord) {
		if(rtfWord.word.equals("plain")||rtfWord.word.equals("pard")) {
			state.reset();
		}else if(rtfWord.word.equals("b")) {
			state.bold=rtfWord.parameter > 0;
		}else if(rtfWord.word.equals("i")) {
			state.italic=rtfWord.parameter > 0;
		}else if(rtfWord.word.equals("ul")) {
			state.underline=rtfWord.parameter > 0;
		}else if(rtfWord.word.equals("ulnone")) {
			state.underline=false;
		}else if(rtfWord.word.equals("strike")) {
			state.strike=rtfWord.parameter > 0;
		}else if(rtfWord.word.equals("v")) {
			state.hidden=rtfWord.parameter > 0;
		}else if(rtfWord.word.equals("fs")) {
			state.fontSize=(int)Math.ceil((rtfWord.parameter/24.0)*16.0);
		}else if(rtfWord.word.equals("cf")) {
			state.textColor=rtfWord.parameter;
		}else if(rtfWord.word.equals("cb")||rtfWord.word.equals("chcbpat")||rtfWord.word.equals("highlight")) {
			state.background=rtfWord.parameter;
		}else if(rtfWord.word.equals("lquote")) {
			output+="&lsquo;";
		}else if(rtfWord.word.equals("rquote")) {
			output+="&rsquo;";
		}else if(rtfWord.word.equals("ldblquote")) {
			output+="&ldquo;";
		}else if(rtfWord.word.equals("rdblquote")) {
			output+="&rdquo;";
		}else if(rtfWord.word.equals("emdash")) {
			output+="&mdash;";
		}else if(rtfWord.word.equals("endash")) {
			output+="&ndash;";
		}else if(rtfWord.word.equals("emspace")) {
			output+="&emsp;";
		}else if(rtfWord.word.equals("enspace")) {
			output+="&ensp;";
		}else if(rtfWord.word.equals("tab")) {
			output+="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		}else if(rtfWord.word.equals("line")) {
			output+="<br>";
		}else if(rtfWord.word.equals("bullet")) {
			output+="&bull;";
		}else if(rtfWord.word.equals("u")) {
			applyStyle("&#"+rtfWord.parameter+";");
		}else if(rtfWord.word.equals("par")||rtfWord.word.equals("row")) {
			closeTags();
			output+="<p>";
			openedTags.put("p",true);
		}
	}
	
	protected void applyStyle(String txt) {
		if (!state.equals(previousState)) {
			String span="";

			if(state.bold)span+="font-weight:bold;";
			if(state.italic)span+="font-style:italic;";
			if(state.underline)span+="text-decoration:underline;";
			if(state.strike)span+="text-decoration:strikethrough;";
			if(state.hidden)span+="display:none;";
			
			previousState=(RtfState)state.clone();
			closeTag("span");

			output+="<span style='"+span+"'>"+txt;
			openedTags.put("span",true);
		} else {
			output+=txt;
		}
	}

	protected String printColor(int index) {
		return index>=1&&index<colortbl.size()?colortbl.get(index):"";
	}

	protected void closeTag(String tag) {
		if(openedTags.get(tag)) {
			output+="</"+tag+">";
			openedTags.put(tag,false);
		}
	}

	protected void closeTags() {
		for(String tag:openedTags.keySet()) {
			closeTag(tag);
		}
	}

	protected void wrapTags() {
		StringBuilder source=new StringBuilder();
		source.append(output+"\n");
		output=source.toString();
	}

	protected void formatControlSymbol(RtfControlSymbol rtfSymbol) {
		if(rtfSymbol.symbol=='\'') applyStyle("&#"+rtfSymbol.parameter+";");
	}

	protected void formatText(RtfText rtfText) {
		applyStyle(rtfText.text);
	}
}
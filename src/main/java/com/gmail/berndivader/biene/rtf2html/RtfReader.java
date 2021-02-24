package com.gmail.berndivader.biene.rtf2html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public
class
RtfReader
{
	private String rtf;
	private int pos;
	private int len;
	private char tchar;
	private RtfGroup group;

	public RtfGroup root=null;

	protected void getChar() {
		if(pos<rtf.length()) tchar=rtf.charAt(pos++);
	}

	protected int hexdec(String s) {
		return Integer.parseInt(s,16);
	}

	protected boolean isDigit() {
		return tchar>=48&&tchar<=57;
	}

	protected boolean isLetter() {
		return((tchar>=65&&tchar<=90)||(tchar>=97&&tchar<=122));
	}

	protected void parseStartGroup() {
		RtfGroup newGroup=new RtfGroup();
		if(group!=null) newGroup.parent=group;
		if(root==null) {
			group=newGroup;
			root=newGroup;
		}else {
			group.children.add(newGroup);
			group=newGroup;
		}
	}

	protected void parseEndGroup() {
		group=group.parent;
	}

	protected void parseControlWord() {
		getChar();
		String word="";

		while (isLetter()) {
			word+=tchar;
			getChar();
		}

		int parameter=-1;
		boolean negative=false;
		if (tchar=='-') {
			getChar();
			negative=true;
		}

		while(isDigit()) {
			if(parameter==-1) parameter=0;
			parameter=parameter*10+Integer.parseInt(tchar+"");
			getChar();
		}

		if(parameter==-1) parameter=1;
		if(negative)parameter=-parameter;

		if(word.equals("u")) {
			if(tchar==' ') getChar();

			if(tchar=='\\'&&rtf.charAt(pos)=='\'') pos+=3;

			if(negative) parameter+=65536;
		}else {
			if(tchar!=' ') pos--;
		}

		RtfControlWord rtfWord=new RtfControlWord();
		rtfWord.word=word;
		rtfWord.parameter=parameter;
		group.children.add(rtfWord);
	}

	protected void parseControlSymbol() {
		getChar();
		char symbol=tchar;

		int parameter=0;
		if(symbol=='\'') {
			getChar();
			String firstChar=tchar+"";
			getChar();
			String secondChar=tchar+"";
			parameter=hexdec(firstChar+secondChar);
		}

		RtfControlSymbol rtfSymbol=new RtfControlSymbol();
		rtfSymbol.symbol=symbol;
		rtfSymbol.parameter=parameter;
		group.children.add(rtfSymbol);
	}
	
	protected void parseControl() {
		getChar();
		pos--;
		if(isLetter()) {
			parseControlWord();
		}else {
			parseControlSymbol();
		}
	}

	protected void parseText() throws RtfParseException {
		String text="";
		boolean terminate=false;

		do {
			terminate=false;

			if(tchar=='\\') {
				getChar();
				switch(tchar) {
				case'\\':
				case'{':
				case'}':
					break;
				default:
					pos-=2;
					terminate=true;
					break;
				}
			}else if(tchar=='{'||tchar=='}') {
				pos--;
				terminate=true;
			}

			if(!terminate) {
				text+=tchar;
				getChar();
			}
		} while(!terminate&&pos<len);

		RtfText rtfText=new RtfText();
		rtfText.text=text;

		if(group!=null) group.children.add(rtfText);
	}
	
	public boolean isValid(String rtf_string) {
		return rtf_string.contains("{\\rtf1");
	}

	public void parse(File rtfFile) throws RtfParseException {
		try {
			try (FileInputStream fis=new FileInputStream(rtfFile)) {
				parse(fis);
			}
		} catch (IOException e) {
			throw new RtfParseException(e.getMessage());
		}
	}

	public void parse(InputStream rtfStream) throws RtfParseException {
		String rtfSource=new BufferedReader(new InputStreamReader(rtfStream)).lines().collect(Collectors.joining("\n"));
		parse(rtfSource);
	}

	public void parse(String rtfSource) throws RtfParseException {
		rtf=rtfSource;
		pos=0;
		len=rtf.length();
		group=null;
		root=null;

		while(pos<len) {
			getChar();
			
			if(tchar=='\n'||tchar=='\r') continue;
			
			switch(tchar) {
			case'{':
				parseStartGroup();
				break;
			case'}':
				parseEndGroup();
				break;
			case'\\':
				parseControl();
				break;
			default:
				parseText();
				break;
			}
		}
	}
}
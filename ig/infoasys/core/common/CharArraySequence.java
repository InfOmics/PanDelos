package infoasys.core.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;


public class CharArraySequence {
	
	public char[] data;
	private char alpha_size;
	private char[] reverseMap;
	private char[] encodingMap;
	public static final char NULL_CODE = Character.MAX_VALUE;
	
	
	public CharArraySequence(char[] data, char alphabetSize, char[] reverseMap){
		this.data = data;
		this.alpha_size = alphabetSize;
		this.reverseMap = reverseMap;
	}
	
	
	public CharArraySequence(char[] data){
		this.data = data;
		reverseMap = new char[Character.MAX_VALUE + 1];
		encodingMap = new char[Character.MAX_VALUE + 1];
		alpha_size = toMinimalAlphabet(data, reverseMap, encodingMap);
	}
	
	public char alphabetSize(){
		return this.alpha_size;
	}
	public char[] reverseMap(){
		return this.reverseMap;
	}
	public void revert(){
		CharArraySequence.revert(data, reverseMap);
	}
	public char revert(char c){
		return reverseMap[c];
	}
	public char encode(char c){
		return encodingMap[c];
	}
	
	public int length(){
		return data.length;
	}
	
	
	public static char alphabetSize(char[] input){
		char[] counts = new char[Character.MAX_VALUE + 1];
		for(int i=0; i<input.length; i++){
			counts[ input[i] ]++;
		}
		char size = 0;
		for(int i = 0; i<counts.length; i++){
			if(counts[i] > 0) size++;
		}
		return size;
	}
	
	public static char toMinimalAlphabet(char[] input, char[] reverseMap, char[] mappedBack){
		Arrays.fill(reverseMap, Character.MAX_VALUE);
		
		int[] counts = new int[Character.MAX_VALUE + 1];
		for(int i=0; i<input.length; i++){
			counts[ input[i] ]++;
		}
		char size = 0;
		for(int i = 0; i<counts.length -1; i++){
			if(counts[i] > 0){
				size++;
			}
		}
		if(size < Character.MAX_VALUE -1){
			char new_code = 0;
			for(int i = 0; i<counts.length; i++){
				if(counts[i] > 0){
					counts[i] = new_code;
					reverseMap[new_code] = (char)i;
					mappedBack[ i ] = new_code;
					new_code++;
				}
			}
			for(int i=0; i<input.length; i++){
				input[i] = (char)counts[ input[i] ];
			}
		}
		else{
			for(int i=0; i<reverseMap.length; i++){
				reverseMap[i] = (char)i;
			}
		}
		reverseMap[Character.MAX_VALUE] = Character.MAX_VALUE;
		//return ret;
		return size;
	}
	
	public static void revert(char[] data, char[] reverseMap){
		for(int i=0; i<data.length; i++){
			data[i] = reverseMap[ data[i] ];
		}
	}
	
	
	public String toString(char[] data){
		String s = "";
		for(int i=0; i<data.length; i++){
			s += reverseMap[data[i]];
		}
		return s;
	}
	
	
	public static CharArraySequence load(String file) throws Exception{
		String s = FileUtils.readFileToString(new File(file), "UTF-8");
		System.out.println(s.toCharArray().length);
		return new CharArraySequence(s.toCharArray());
	}
}

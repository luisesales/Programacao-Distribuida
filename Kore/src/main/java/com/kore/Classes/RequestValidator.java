package com.kore.Classes;

import java.util.StringTokenizer;

public class RequestValidator{
    private static boolean ValidateOperation(String op){
		return !(op.equals("create") || op.equals("deposit") || op.equals("balance"));
	}

	public static boolean ValidateRequest(String request){
		String operation =null;
		int account = 0;
		int valor = 0;
		StringTokenizer tokenizer = new StringTokenizer(request, ";");
		if(!tokenizer.nextToken().equals("REQUEST"))
			return false;		
		while (tokenizer.hasMoreElements()) {
			try{			
			operation = tokenizer.nextToken();			
			account = Integer.parseInt(tokenizer.nextToken());			
			valor = Integer.parseInt(tokenizer.nextToken().trim());			
			if(ValidateOperation(operation)){
				return false;
			}
			} catch(NumberFormatException e){
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}
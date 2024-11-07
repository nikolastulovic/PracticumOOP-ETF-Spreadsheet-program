package sveKlase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CellFormatNumber extends CellFormat {
	
	private int decimals;
	
	private String calculate(String value)
	{
		Formula f=new Formula(value);
		return f.calculate();
	}
	
	public CellFormatNumber()
	{ 
		decimals=0;
	}
	public CellFormatNumber(int dec)
	{ 
		decimals=dec;
	}
	public int getNumberOfDecimals() { return decimals; }
	@Override
	public boolean appropriate(String value) {
		Pattern pattern = Pattern.compile("^(\\d+)(\\.\\d+)?$");
		Matcher matcher = pattern.matcher(value);

		return matcher.find();
	}

	@Override
	public String getFormat() {
		return "number";
	}

	@Override
	public String display(String value) {
		if(value.isEmpty()) return "0";
	    String ret = "";
	    if (value.charAt(0) == '=') {
	        ret = calculate(value);
	    } else if (appropriate(value)) {
	        ret = value;
	    }
	    
	    if (!ret.isEmpty()) {
	        Pattern pattern = Pattern.compile("^(\\d+)(\\.\\d+)?$");
	        Matcher matcher = pattern.matcher(ret);
	        
	        if (matcher.find()) {
	            if (decimals!=0 && matcher.group(2)==null) {
	                ret += ".";
	                for (int i = 0; i < decimals; i++) {
	                    ret += "0";
	                }
	                return ret;
	            }
	            else if(decimals!=0 && matcher.group(2)!=null && matcher.group(2).length()-1 < decimals)
	            {
	                for (int i = 0; i < decimals-matcher.group(2).length()+1; i++) {
	                    ret += "0";
	                }
	                return ret;
	            }
	            
	            return matcher.group(1) + (decimals!=0 && matcher.group(2)!=null ? "." : "") + (decimals!=0 && matcher.group(2)!=null ? matcher.group(2).substring(1, decimals + 1) : "");
	        }
	    }
	    
	    return "ERROR"; // error value
	}

}

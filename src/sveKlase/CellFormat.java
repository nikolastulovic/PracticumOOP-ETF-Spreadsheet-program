package sveKlase;

public abstract class CellFormat {
	
	public abstract boolean appropriate(String value);//checking if value that is tried to be entered is valid
	public abstract String getFormat();//format getter
	public abstract String display(String value);
}

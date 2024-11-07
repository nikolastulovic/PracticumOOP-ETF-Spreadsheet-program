package sveKlase;

public class Cell {

	//private
	private String value;//value of the cell
	CellFormat cf;//cell format
	int row;//cell identification
	int column;
	
	//protected
	protected Cell(String val, int r, int c)
	{
		CellFormat cellf = new CellFormatText();
		value=val;
		row=r;
		column=c;
		cf=cellf;
	}
	protected Cell(String val, int r, int c, CellFormat cellf)
	{
		value=val;
		row=r;
		column=c;
		cf=cellf;
	}
	protected String display() { return cf.display(value); }
	
	//public
	public Cell changeFormat(CellFormat cfnew)
	{
		cf = cfnew;
		return this;
	}
	public Cell changeValue(String value) 
	{ 
		this.value = value; 
		return this; 
	}
	public Cell changeDecimals(int decimals)
	{
		cf = new CellFormatNumber(decimals);
		return this;
	}
	public String getValue() { return value; }
	public int getRow() { return row; }
	public int getColumn() { return column; }
}

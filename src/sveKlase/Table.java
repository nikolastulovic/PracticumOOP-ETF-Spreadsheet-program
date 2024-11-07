package sveKlase;

import greske.*;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Program that implements Excel like table.
 * 
 * @author Nikola Stulovic
 * @version 1.0
 * @since 2022-06-27
 */
public class Table {
	
	static final int COLNUM = 'Z' - 'A' + 2; // fixed maximum number of columns
	
	//private
	private boolean saved=true;
	private static Table inst_;
	Vector<Cell> cells = new Vector<Cell>();
	int[] columnWidths;
	private Action actions=new Action();
	
	
	/**
	 * Constructor of the Table class, it sets necessary default values and fills header of the table
	 */
	private Table()
	{
		columnWidths = new int[COLNUM];
		columnWidths[0] = 3;//column that contains row index
		char ch = 'A';
		for (int i = 1; i < COLNUM; i++, ch++)
		{
			columnWidths[i] = 5;
			cells.add(new Cell(""+ch, 0, i));//filling the header
		}
	}
	/**
	* Method that clears all content of the table.
	*/
	public void clearTable()
	{
		cells.clear();
		
		char ch = 'A';
		for (int i = 1; i < COLNUM; i++, ch++)
		{
			columnWidths[i] = 5;
			cells.add(new Cell(""+ch, 0, i));//filling the header
		}
	}
	
	/**
	* Redo function gets last action that is undone over table and performs it again.
	* @exception GNemaRedo
	*/
	public void redo() throws GNemaRedo
	{
		String act = actions.getActionRedo();
		if (!act.isEmpty()) {
			Pattern pattern = Pattern.compile("^([a-zA-Z]+)-([^-]+)-([^-]+)-(-?\\d+)-(\\d+)$");
			Matcher matcher = pattern.matcher(act);
			matcher.find();
			
			String action = matcher.group(1);
			int row = Integer.parseInt(matcher.group(4));
			int col = Integer.parseInt(matcher.group(5));
			if (action.equals("edit"))
			{
				String newValue = matcher.group(3);
				Cell c = getCell(row, col);
				//changing value 
				c.changeValue(newValue);
			}
			else if (action.equals("format"))
			{
				String newFormat = matcher.group(3);
				if (row == -1)//indicator that format has been changed for whole column
				{
					for (Cell c : cells)//changing format for whole column
					{
						if (c.column == col && c.row != 0)
						{
							if (newFormat.equals("text")) c.changeFormat(new CellFormatText());
							else if (newFormat.equals("number")) c.changeFormat(new CellFormatNumber());
							else if (newFormat.equals("date")) c.changeFormat(new CellFormatDate());
						}
					}
				}
				else
				{
					Cell c = getCell(row, col);
					//changing format 
					if (newFormat.equals("text")) c.changeFormat(new CellFormatText());
					else if (newFormat.equals("number")) c.changeFormat(new CellFormatNumber());
					else if (newFormat.equals("date")) c.changeFormat(new CellFormatDate());
				}
			}
			else if (action.equals("decimals"))
			{
				int newDecimalPlaces = Integer.parseInt(matcher.group(3));
				Cell c = getCell(row, col);
				//changing decimal places
				c.changeDecimals(newDecimalPlaces);
			}
			//columnWidths[col] = widestElementInColumn(col);//adjusting column widths
		}
		else throw new GNemaRedo();//redo not available
	}
	/**
	* Undo function gets last action that is performed over table and undoes it.
	* 
	* @exception GNemaUndo
	*/
	public void undo() throws GNemaUndo
	{
		String act = actions.getActionUndo();
		if (!act.isEmpty()) {
			Pattern pattern = Pattern.compile("^([a-zA-Z]+)-([^-]+)-([^-]+)-(-?\\d+)-(\\d+)$");
			Matcher matcher = pattern.matcher(act);
			matcher.find();
			String action = matcher.group(1);
			int row = Integer.parseInt(matcher.group(4));
			int col = Integer.parseInt(matcher.group(5));
			if (action.equals("edit"))
			{
				String oldValue = matcher.group(2);
				if (oldValue.equals("`")) oldValue = "";
				Cell c = getCell(row, col);
				//changing value 
				c.changeValue(oldValue);
			}
			else if (action.equals("format"))
			{
				String oldFormat = matcher.group(2);
				if (row == -1)//indicator that format has been changed for whole column
				{
					for (Cell c : cells)//changing format for whole column
					{
						if (c.column == col && c.row != 0)
						{
							if (oldFormat.equals("text")) c.changeFormat(new CellFormatText());
							else if (oldFormat.equals("number")) c.changeFormat(new CellFormatNumber());
							else if (oldFormat.equals("date")) c.changeFormat(new CellFormatDate());
						}
					}
				}
				else
				{
					Cell c = getCell(row, col);
					//changing format 
					if (oldFormat.equals("text")) c.changeFormat(new CellFormatText());
					else if (oldFormat.equals("number")) c.changeFormat(new CellFormatNumber());
					else if (oldFormat.equals("date")) c.changeFormat(new CellFormatDate());
				}
			}
			else if (action.equals("decimals"))
			{
				int oldDecimalPlaces = Integer.parseInt(matcher.group(2));
				Cell c = getCell(row, col);
				//changing decimal places
				c.changeDecimals(oldDecimalPlaces);
			}
			//columnWidths[col] = widestElementInColumn(col);//adjusting column widths
		}
		else throw new GNemaUndo();//undo not available
	}
	/**
	* Function that returns a reference to cell at position (row,col)
	*
	* @param  row  numeric value of a row of a cell
	* @param  col  numeric value of a column of a cell
	* @return Cell reference to cell at position (row,col)
	*/
	public Cell getCell(int row, int col)
	{
		for (Cell c:cells) {
			if (c.getRow() == row && c.getColumn() == col) return c;
		}
		return null;
	}
	/**
	* Function that returns boolean that states if cell at (row, col) exists
	*
	* @param  row     numeric value of a row of a cell
	* @param  col  	  numeric value of a column of a cell
	* @return boolean value that states if cell at (row, col) exists
	*/
	public boolean cellExists(int row, int col)
	{
		for (Cell c : cells) if (c.getRow() == row && c.getColumn() == col) return true;
		return false;
	}
	/**
	* Function that returns boolean that states if cell at cell label exists
	*
	* @param cell     cell label, for example C5
	* @return boolean that states if cell at (row, col) exists
	*/
	public boolean cellExists(String cell)
	{
		cell=cell.toLowerCase();
		int col = cell.charAt(0) - 'a' + 1;
		int row = Integer.parseInt(cell.substring(1));
		return cellExists(row, col);
	}
	/**
	* Function that returns string value of a cell at (row, col)
	*
	* @param  row    numeric value of a row of a cell
	* @param  col    numeric value of a column of a cell
	* @return String value of a cell at (row, col)
	*/
	public String getCellValue(int row, int col)
	{
		for (Cell c : cells) {
			if (c.getRow() == row && c.getColumn() == col) return c.getValue();
		}
		return "";
	}
	/**
	* Function that returns numeric value of a cell at (row, col)
	*
	* @param  row    numeric value of a row of a cell
	* @param  col    numeric value of a column of a cell
	* @return double value of a cell at (row, col)
	*/
	public double getCellNumValue(String cell)
	{
		cell=cell.toLowerCase();
		int col = cell.charAt(0) - 'a' + 1;
		int row = Integer.parseInt(cell.substring(1));
		for (Cell c : cells) {
			if (c.getRow() == row && c.getColumn() == col) return Double.parseDouble(c.display());
		}
		return 0;
	}
//	public int widestElementInColumn(int col)
//	{
//		int max = 0;
//		for (Cell c : cells) {
//			if (c.getColumn() == col && c.cf.display(c.getValue())!=null && c.cf.display(c.getValue()).length() > max) max = (int)c.cf.display(c.getValue()).length();
//		}
//		return max;
//	}
	
	/**
	* Function that changes format of a cell at (row, col)
	*
	* @param row    numeric value of a row of a cell
	* @param col    numeric value of a column of a cell
	* @param format new format of a cell
	* @param oldValue old value of current cell
	*/
	public void changeFormat(int row, int col, String format, String oldValue)
	{
		if (row == -1)//if its -1 formating is done for the whole column
		{
			int actionDone = 0;
			for (Cell c : cells)
			{
				if (c.column == col && c.row != 0)
				{
					String oldFormat = c.cf.getFormat();
					if (format.equals("text")) c.changeFormat(new CellFormatText());
					else if (format.equals("number")) c.changeFormat(new CellFormatNumber());
					else if (format.equals("date")) c.changeFormat(new CellFormatDate());
					String newFormat = c.cf.getFormat();
					
					if (actionDone==0)//if its done for whole row its enough to get pushed once to undo actions stack
					{
						actionDone = 1;
						actions.pushAction("format", oldFormat, newFormat, -1, col);//saving which action is performed
					}
				}
			}
		}
		else//formating of a given cell
		{
			if (!cellExists(row, col))
			{
				Cell c = new Cell("", row, col);
				String oldFormat = "text";//default format is text, there is no need to call function to getFormat for newly created cell
				if (format.equals("text")) cells.add(c.changeFormat(new CellFormatText()));
				else if (format.equals("number")) cells.add(c.changeFormat(new CellFormatNumber()));
				else if (format.equals("date")) cells.add(c.changeFormat(new CellFormatDate()));
				String newFormat = c.cf.getFormat();
				actions.pushAction("format", oldFormat, newFormat, row, col);//saving which action is performed
			}
			else
			{
				Cell c = getCell(row, col);
				String oldFormat = c.cf.getFormat();
				if (format.equals("text")) c.changeFormat(new CellFormatText());
				else if (format.equals("number")) c.changeFormat(new CellFormatNumber());
				else if (format.equals("date")) c.changeFormat(new CellFormatDate());
				String newFormat = c.cf.getFormat();
				actions.pushAction("format", oldFormat, newFormat, row, col);//saving which action is performed
			}
		}
		//columnWidths[col] = widestElementInColumn(col);
	}
	/**
	* Function that changes format of a cell at (row, col)
	*
	* @param row    numeric value of a row of a cell
	* @param col    numeric value of a column of a cell
	* @param format new format of a cell
	*/
	public void changeFormat(int row, int col, String format)
	{
		if (row == -1)//if its -1 formating is done for the whole column
		{
			int actionDone = 0;
			for (Cell c : cells)
			{
				if (c.column == col && c.row != 0)
				{
					String oldFormat = c.cf.getFormat();
					if (format.equals("text")) c.changeFormat(new CellFormatText());
					else if (format.equals("number")) c.changeFormat(new CellFormatNumber());
					else if (format.equals("date")) c.changeFormat(new CellFormatDate());
					String newFormat = c.cf.getFormat();
					
					if (actionDone==0)//if its done for whole row its enough to get pushed once to undo actions stack
					{
						actionDone = 1;
						actions.pushAction("format", oldFormat, newFormat, -1, col);//saving which action is performed
					}
				}
			}
		}
		else//formating of a given cell
		{
			if (!cellExists(row, col))
			{
				Cell c = new Cell("", row, col);
				String oldFormat = "text";//default format is text, there is no need to call function to getFormat for newly created cell
				if (format.equals("text")) cells.add(c.changeFormat(new CellFormatText()));
				else if (format.equals("number")) cells.add(c.changeFormat(new CellFormatNumber()));
				else if (format.equals("date")) cells.add(c.changeFormat(new CellFormatDate()));
				String newFormat = c.cf.getFormat();
				actions.pushAction("format", oldFormat, newFormat, row, col);//saving which action is performed
			}
			else
			{
				Cell c = getCell(row, col);
				String oldFormat = c.cf.getFormat();
				if (format.equals("text")) c.changeFormat(new CellFormatText());
				else if (format.equals("number")) c.changeFormat(new CellFormatNumber());
				else if (format.equals("date")) c.changeFormat(new CellFormatDate());
				String newFormat = c.cf.getFormat();
				actions.pushAction("format", oldFormat, newFormat, row, col);//saving which action is performed
			}
		}
		//columnWidths[col] = widestElementInColumn(col);
	}
	/**
	* Function that changes value of a cell at (row, col)
	*
	* @param row      numeric value of a row of a cell
	* @param col      numeric value of a column of a cell
	* @param newValue new value of current cell
	* @exception GNeodgovarajucaVrednost
	*/
	public void changeValue(int row, int col, String newValue) throws GNeodgovarajucaVrednost
	{
		String oldValue = getCellValue(row, col);
		if (!cellExists(row, col)) cells.add(new Cell(newValue, row, col));
		else {
			Cell c = getCell(row, col);
			if (c.cf.appropriate(newValue) || newValue.charAt(0) == '=') c.changeValue(newValue);
			else throw new GNeodgovarajucaVrednost();//inappropriate value tried to be set for this format
		}
		actions.pushAction("edit", oldValue, newValue, row, col);//saving which action is performed
		//columnWidths[col] = widestElementInColumn(col);//adjusting column widths
	}
	/**
	* Function that gets proper display of a cell at (row, col)
	*
	* @param row	numeric value of a row of a cell
	* @param col	numeric value of a column of a cell
	*/
	public String getCellDisplay(int row, int col)
	{
		for (Cell c : cells) {
			if (c.getRow() == row && c.getColumn() == col) return c.cf.display(c.getValue());
		}
		return "";
	}
	/**
	* Function that changes number of decimal places of a cell at (row, col)
	*
	* @param row      numeric value of a row of a cell
	* @param col      numeric value of a column of a cell
	* @param decimals new number of decimal places
	* @exception GPostavljanjeDecimala
	*/
	public void changeDecimals(int row, int col, int decimals) throws GPostavljanjeDecimala
	{
		Cell c = getCell(row, col);
		String oldNumberOfDecimalPlaces;
		if (c!=null && c.cf.getFormat().equals("number"))
		{
			oldNumberOfDecimalPlaces = Integer.toString(((CellFormatNumber)(c.cf)).getNumberOfDecimals());
			c.changeDecimals(decimals);
		}
		else throw new GPostavljanjeDecimala();//decimals can be set only for number format
		

		actions.pushAction("decimals", oldNumberOfDecimalPlaces, Integer.toString(decimals), row, col);
		//columnWidths[col] = widestElementInColumn(col);
	}
	
	/**
	 * Method that get boolean value that states if table is saved
	 * 
	 * @return boolean value that states if table is saved
	 */
	public boolean isSaved()
	{
		return saved;
	}
	
	/**
	 * Method that set boolean value that states if table is saved
	 * 
	 * @param newState New state that should be set
	 */
	public void setSaved(boolean newState)
	{
		saved=newState;
	}
	/**
	 * Method that reads and executes commands over table.
	 * 
	 * @param command Command that says which action should be performed
	 * @throws GNepoznatFormat
	 * @throws GNemaRedo
	 * @throws GNemaUndo
	 * @throws GNeodgovarajucaVrednost
	 * @throws NumberFormatException
	 * @throws GPostavljanjeDecimala
	 * @throws GNeuspeloOtvaranje
	 * @throws GNepoznataKomanda
	 */
	public void perform(String command) throws GNepoznatFormat, GNemaRedo, GNemaUndo, GNeodgovarajucaVrednost, NumberFormatException, GPostavljanjeDecimala, GNeuspeloOtvaranje, GNepoznataKomanda
	{
		if (command.equals("redo"))
		{
			this.redo();
			saved = false;
			return;
		}
		if (command.equals("undo"))
		{
			this.undo();
			saved = false;
			return;
		}
		command=command.toLowerCase();
		//regex recognition
		Pattern pattern = Pattern.compile("^([a-zA-Z]+)-([^-]+)-(.+)$");
		Matcher matcher = pattern.matcher(command);
		
		if (matcher.find())
		{
			String nameOfAction = matcher.group(1);
			String ind = matcher.group(2);
			String value = matcher.group(3);
			int row = -1, col;
			col = ind.charAt(0) - 'a' + 1;
			//commands
			if (nameOfAction == "edit") {
				row = Integer.parseInt(ind.substring(1));
				changeValue(row, col, value);
				saved = false;
			}
			else if (nameOfAction.equals("decimals"))
			{
				row = Integer.parseInt(ind.substring(1));
				changeDecimals(row, col, Integer.parseInt(value));
				saved = false;
			}
			else if (nameOfAction.equals("width"))
			{
				columnWidths[col] = Integer.parseInt(value);
				saved = false;
			}
			else if (nameOfAction.equals("format"))
			{
				if (ind.length() > 1) row = Integer.parseInt(ind.substring(1));
				if (value == "text" || value == "number" || value == "date")
				{
					changeFormat(row, col, value);
					saved = false;
				}
				else throw new GNepoznatFormat();//format unknown
			}
			else if (nameOfAction.equals("save"))
			{
				if (value.equals("csv"))
				{
					Parser p = new CSVParser(ind + ".csv");
					p.save();
				}
				else if (value.equals("json"))
				{
					Parser p = new JSONParser(ind + ".json");
					p.save();
				}
				saved = true;
			}
			else if (nameOfAction.equals("load"))
			{
				if (value.equals("csv"))
				{
					Parser p = new CSVParser(ind + ".csv");
					p.read();
				}
				else if (value.equals("json"))
				{
					Parser p = new JSONParser(ind + ".json");
					p.read();
				}
				saved = true;
			}
			else throw new GNepoznataKomanda(); //command unknown
		}
		else throw new GNepoznataKomanda(); //command unknown
	}
	/**
	 * Method that ensures that Table is singleton class
	 * @return Table Singleton reference to the table
	 */
	public static Table getInstance() 
	{
		if (inst_==null) inst_ = new Table();
		return inst_;
	}
}

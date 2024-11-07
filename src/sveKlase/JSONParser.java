package sveKlase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import greske.GNeuspeloOtvaranje;

public class JSONParser extends Parser {

	static final int COLNUM = 'Z' - 'A' + 2; // fixed maximum number of columns
	
	public JSONParser(String file) {
		super(file);
	}

	@Override
	public void save() throws GNeuspeloOtvaranje {
		try {
			String workingDirectory = System.getProperty("user.dir");
			String filePath = workingDirectory + File.separator + filename;
		    FileWriter myFile = new FileWriter(filePath);
		    
		    //filling the json file
		    myFile.write("{\"Table\": [\n");
			String output="";
			boolean prvi = true;
			for (Cell c : table.cells)
			{
				if (c.getRow()!=0)//header cells doesn't need to be saved
				{
					if (!prvi) output += ",\n";
					output += "{\"row\":\"";
					output += Integer.toString(c.getRow()) + "\",\"column\":\"";
					output += Integer.toString(c.getColumn()) + "\",\"value\":\"";
					output += c.getValue() + "\",\"format\":\"";
					if (c.cf.getFormat().equals("text")) output += "text\",\"decimals\":\"0\"}";
					else if (c.cf.getFormat().equals("number"))
					{
						int brDec = ((CellFormatNumber)c.cf).getNumberOfDecimals();
						output += "number\",\"decimals\":\"";
						output += Integer.toString(brDec);
						output += "\"}";
					}
					else if (c.cf.getFormat().equals("date")) output += "date\",\"decimals\":\"0\"}";
					if (prvi) prvi = false;
					myFile.write(output);
					output = "";
				}
			}
			myFile.write("\n]}");

		    // closing the used file
		    myFile.close();
		} catch (IOException e) {
		    throw new GNeuspeloOtvaranje(); // file opening failed
		}
	}

	@Override
	public void read() throws GNeuspeloOtvaranje {
		//clearing old values from the table
				table.cells.clear();
				//filling the header
				char ch = 'A';
				for (int i = 1; i < COLNUM; i++, ch++)
				{
					table.columnWidths[i] = 5;
					table.cells.add(new Cell(""+ch, 0, i));//filling the header
				}
				//reading from json
				try 
				{
					String workingDirectory = System.getProperty("user.dir");
					String filePath = workingDirectory + File.separator + filename;
					BufferedReader myFile = new BufferedReader(new FileReader(filePath));
					
					String red;
					red=myFile.readLine();//skipping the first row
					int i = 1, j = 1;
					while (myFile!=null && (red=myFile.readLine())!=null && !red.isEmpty()) //getting row by row from json file
					{
						if (!red.equals("]}"))//last row in json, should be ignored
						{
							String regex = "\\{\\\"row\\\":\\\"([0-9]+)\\\",\\\"column\\\":\\\"([0-9]+)\\\",\\\"value\\\":\\\"(.+)\\\",\\\"format\\\":\\\"([a-zA-Z]+)\\\",\\\"decimals\\\":\\\"([0-9]+)\\\"\\}";
							Pattern pattern = Pattern.compile(regex);
							Matcher matcher = pattern.matcher(red);
							if (matcher.find()) 
							{
								Cell c = new Cell(matcher.group(3), Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
								if (matcher.group(4).equals("number")) c.changeFormat(new CellFormatNumber(Integer.parseInt(matcher.group(5))));
								else if(matcher.group(4).equals("date")) c.changeFormat(new CellFormatDate());
								table.cells.add(c);
								//table.columnWidths[c.column] = table.widestElementInColumn(c.column);
							}
						}
					}
					
					//closing the used file
					myFile.close();
				}
				catch (IOException e) {
					throw new GNeuspeloOtvaranje();//file opening failed
				}
	}

}

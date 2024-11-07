package sveKlase;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import greske.*;

public class CSVParser extends Parser {
	
	static final int COLNUM = 'Z' - 'A' + 2; // fixed maximum number of columns
	
	public CSVParser(String file) {
		super(file);
	}

	@Override
	public void save() throws GNeuspeloOtvaranje {
		try {
			String workingDirectory = System.getProperty("user.dir");
			String filePath = workingDirectory + File.separator + filename;
		    FileWriter myFile = new FileWriter(filePath);
		    
		    //getting number of rows used
		    int max = 0;
			for (Cell c : table.cells)
			{
				int row = c.getRow();
				if (row > max) max = row;
			}
			
			max+=1;

			//filling the csv file
			for (int i = 1; i < max; i++)
			{
				for (int j = 1; j < COLNUM; j++) myFile .write( table.getCellValue(i, j) + ";");
				myFile.write('\n');
			}

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
		//reading from csv
		try {
			String workingDirectory = System.getProperty("user.dir");
			String filePath = workingDirectory + File.separator + filename;
			BufferedReader myFile = new BufferedReader(new FileReader(filePath));

			String red;
			int i = 1, j = 1;
			while (myFile!=null && (red=myFile.readLine())!=null && !red.isEmpty())  //getting row by row from csv file
			{
				String rec = "";
				j = 1;
				String[] substrings = red.split("");
				for (String cha : substrings)
				{
					if (cha.equals(";"))
					{
						//pushing cells to the table vector
						if (!rec.isEmpty())
						{
							table.cells.add(new Cell(rec, i, j));
							//table.columnWidths[j] = table.widestElementInColumn(j);
							rec = "";
						}
						j++;
					}
					else rec += cha;
				}
				if (!rec.isEmpty())
				{
					table.cells.add(new Cell(rec, i, j));
					// table.columnWidths[j] = table.widestElementInColumn(j);
					rec = "";
				}
				i++;
			}
			//closing the used file
			myFile.close();
		}
		catch (IOException e) {
			throw new GNeuspeloOtvaranje();//file opening failed
		}
	}

}

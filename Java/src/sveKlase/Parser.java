package sveKlase;

import greske.GNeuspeloOtvaranje;

public abstract class Parser {
	
	protected String filename="savedCSV.csv";
	protected Table table=Table.getInstance();
	protected Parser(String file)
	{
		filename=file;
	}
	
	public abstract void save() throws GNeuspeloOtvaranje;
	public abstract void read() throws GNeuspeloOtvaranje;
}

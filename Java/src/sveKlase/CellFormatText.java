package sveKlase;

public class CellFormatText extends CellFormat {

	@Override
	public String display(String value) {
		return value;
	}

	@Override
	public boolean appropriate(String value) {
		return true;
	}

	@Override
	public String getFormat() {
		return "text";
	}

}

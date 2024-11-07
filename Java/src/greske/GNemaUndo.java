package greske;

public class GNemaUndo extends Exception {
	public GNemaUndo() {
		super("Ne postoji dostupna undo opcija!");
	}
}

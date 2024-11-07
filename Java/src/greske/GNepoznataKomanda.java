package greske;

public class GNepoznataKomanda extends Exception {
	public GNepoznataKomanda() {
		super("Trazena komanda ne postoji!");
	}
}

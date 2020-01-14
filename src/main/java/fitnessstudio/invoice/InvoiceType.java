package fitnessstudio.invoice;

/**
 * Enum to represent the {@link InvoiceEntry} type.
 *
 * @author Bill Kippe
 * @author Lea Häusler
 * @version 1.0
 */
public enum InvoiceType {
	DEPOSIT("AUFLADUNG"), WITHDRAW("KONTO ABBUCHUNG"), CASHPAYMENT("BAR BEZAHLT");

	private final String displayValue;

	InvoiceType(String displayValue) {this.displayValue = displayValue;}

	public String getDisplayValue() {
		return displayValue;
	}
}

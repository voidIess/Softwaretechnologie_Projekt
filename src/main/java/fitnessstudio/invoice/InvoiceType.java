package fitnessstudio.invoice;

public enum InvoiceType {
	DEPOSIT("AUFLADUNG"), WITHDRAW("KONTO ABBUCHUNG"), CASHPAYMENT("BAR BEZAHLT");

	private final String displayValue;

	InvoiceType(String displayValue) {this.displayValue = displayValue;}

	public String getDisplayValue() {
		return displayValue;
	}
}

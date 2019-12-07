package fitnessstudio.invoice;


import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class InvoiceEventListener implements ApplicationListener<InvoiceEvent> {

	private InvoiceManagement invoiceManagement;

	public InvoiceEventListener(InvoiceManagement invoiceManagement){
		Assert.notNull(invoiceManagement, "InvoiceManagement must not be null!");

		this.invoiceManagement = invoiceManagement;
	}

	@Override
	public void onApplicationEvent(@NotNull InvoiceEvent event) {
		invoiceManagement.createInvoiceEntry(event);
	}
}

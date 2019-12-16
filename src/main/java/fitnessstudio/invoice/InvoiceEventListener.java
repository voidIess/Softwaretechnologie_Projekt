package fitnessstudio.invoice;


import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class InvoiceEventListener implements ApplicationListener<InvoiceEvent> {
	private static final Logger LOG = LoggerFactory.getLogger(InvoiceEventListener.class);
	private InvoiceManagement invoiceManagement;

	public InvoiceEventListener(InvoiceManagement invoiceManagement) {
		Assert.notNull(invoiceManagement, "InvoiceManagement must not be null!");

		this.invoiceManagement = invoiceManagement;
	}

	@Override
	public void onApplicationEvent(@NotNull InvoiceEvent event) {
		LOG.info("Received InvoiceEvent: "
			+ event.getType() + " | " + event.getMember() + " | "
			+ event.getAmount() + " | " + event.getDescription());

		invoiceManagement.createInvoiceEntry(event);
	}
}

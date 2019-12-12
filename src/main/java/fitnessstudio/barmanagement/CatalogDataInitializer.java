package fitnessstudio.barmanagement;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.quantity.Metric;
import org.salespointframework.quantity.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.salespointframework.core.Currencies.EURO;

@Component
public class CatalogDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);

	private final ArticleCatalog catalog;

	public CatalogDataInitializer(ArticleCatalog catalog, DiscountRepository discountRepository) {
		this.catalog = catalog;
	}


	@Override
	public void initialize() {
		if (catalog.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default catalog entries.");
		LocalDate startDate = LocalDate.of(2019, 12, 1);
		LocalDate endDate = LocalDate.of(2020, 6, 15);
		Discount discount = new Discount(startDate, endDate, 0);
		discountRepository.save(discount);

		catalog.save(new Article("Hantel", Money.of(50.00, EURO), "Trainingsgerät",
			"10kg", discount, Quantity.of(50, Metric.UNIT)));
		catalog.save(new Article("Serious Mass", Money.of(13.37, EURO), "Essen",
			"gesund", discount, Quantity.of(10, Metric.UNIT)));
	}
}

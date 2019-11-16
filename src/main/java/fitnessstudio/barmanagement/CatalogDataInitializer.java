package gym.barmanagement;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDate;

import static org.salespointframework.core.Currencies.EURO;

@Component
public class CatalogDataInitializer implements DataInitializer {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);
	private final BarCatalog catalog;

	public CatalogDataInitializer(BarCatalog catalog) {
		Assert.notNull(catalog, "BarCatalog must not be null!");

		this.catalog = catalog;
	}

	@Override
	public void initialize() {
		if (catalog.findAll().iterator().hasNext()) {
			return;
		}

		LOG.info("Creating default catalog entries.");

		catalog.save(new Article("Hantelsalat", Money.of(13.37, EURO),"lul", "fein  fein, fein....", LocalDate.MIN));
		catalog.save(new Article("Kraueterschrauben", Money.of(13.37, EURO),"lul", "fein  fein, fein....", LocalDate.MIN));
	}
}

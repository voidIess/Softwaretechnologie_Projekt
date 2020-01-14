package fitnessstudio.barmanagement;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.quantity.Metric;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class InventoryDataInitializer implements DataInitializer {

	private final ArticleCatalog catalog;
	private final ExpiringInventory inventory;


	public InventoryDataInitializer(ExpiringInventory inventory, ArticleCatalog catalog) {
		this.inventory = inventory;
		this.catalog = catalog;
	}


	/**
	 * fill the repository with sample data, if its empty in the beginning
	 */
	@Override
	public void initialize() {
		catalog.findAll().forEach(article ->
		{
			InventoryItems<ExpiringInventoryItem> items = inventory.findByProduct(article);

			if (items.isEmpty()) {
				inventory.save(new ExpiringInventoryItem(article, Quantity.of(23, Metric.UNIT),
						LocalDate.of(2100, 10, 10)));
			}

		});
	}
}

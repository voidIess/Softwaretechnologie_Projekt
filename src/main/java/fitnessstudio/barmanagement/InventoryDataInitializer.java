package fitnessstudio.barmanagement;

import org.apache.juli.logging.Log;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


	@Override
	public void initialize() {
		catalog.findAll().forEach(article ->
			{
				InventoryItems<ExpiringInventoryItem> items = inventory.findByProduct(article);

				if(items.isEmpty())
				{
					inventory.save(new ExpiringInventoryItem(article, Quantity.of(23), LocalDate.of(2100, 10, 10)));
				}

			});
	}
}

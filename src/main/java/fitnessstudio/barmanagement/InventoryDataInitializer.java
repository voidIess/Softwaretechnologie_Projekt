package fitnessstudio.barmanagement;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Component;

@Component
public class InventoryDataInitializer implements DataInitializer {

	private final ArticleCatalog catalog;
	private final UniqueInventory<UniqueInventoryItem> inventory;

	public InventoryDataInitializer(UniqueInventory<UniqueInventoryItem> inventory, ArticleCatalog catalog) {
		this.inventory = inventory;
		this.catalog = catalog;
	}


	@Override
	public void initialize() {
		catalog.findAll().forEach(article -> inventory.findByProduct(article).orElseGet(() ->
			inventory.save(new UniqueInventoryItem(article, Quantity.of(23)))));
	}
}

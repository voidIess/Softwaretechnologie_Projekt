package fitnessstudio.barmanagement;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InventoryDataInitializer implements DataInitializer {

	@Autowired
	private ArticleCatalog catalog;
	@Autowired
	private UniqueInventory<UniqueInventoryItem> inventory;


	@Override
	public void initialize() {
		catalog.findAll().forEach(article -> inventory.findByProduct(article).orElseGet(() ->
			inventory.save(new UniqueInventoryItem(article, Quantity.of(23)))));
	}
}

package gym.barmanagement;

import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class InventoryDataInitializer implements DataInitializer {

	private final BarCatalog catalog;
	private final UniqueInventory<UniqueInventoryItem> inventory;


	public InventoryDataInitializer(BarCatalog catalog, UniqueInventory<UniqueInventoryItem> inventory) {
		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(catalog, "Catalog must not be null!");

		this.catalog = catalog;
		this.inventory = inventory;
	}

	@Override
	public void initialize() {
		catalog.findAll().forEach(x ->
		{
			inventory.findByProduct(x).orElseGet(() -> inventory.save(new UniqueInventoryItem(x, Quantity.of(23))));
		});
	}
}

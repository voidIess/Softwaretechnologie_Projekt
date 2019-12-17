package fitnessstudio.barmanagement;

import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.inventory.MultiInventory;
import org.springframework.data.util.Streamable;

import java.time.LocalDate;

public interface ExpiringInventory extends MultiInventory<ExpiringInventoryItem> {
	Streamable<ExpiringInventoryItem> findByExpirationDate(LocalDate localDate);

	InventoryItems<ExpiringInventoryItem> findByExpirationDateAfterOrderByExpirationDateAsc(LocalDate localDate);
	InventoryItems<ExpiringInventoryItem> findByProductAndExpirationDateAfter(Product product, LocalDate expirationDate);
	InventoryItems<ExpiringInventoryItem> findByProductAndExpirationDateAfterOrderByExpirationDateAsc(
			Product product, LocalDate expirationDate);
}

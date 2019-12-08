package fitnessstudio.barmanagement;

import org.salespointframework.inventory.InventoryItems;
import org.salespointframework.inventory.MultiInventory;
import org.springframework.data.util.Streamable;

import java.time.LocalDate;

public interface ExpiringInventory extends MultiInventory<ExpiringInventoryItem> {
	Streamable<ExpiringInventoryItem> findByExpirationDate(LocalDate localDate);

	InventoryItems<ExpiringInventoryItem> findByExpirationDateAfterOrderByExpirationDateAsc(LocalDate localDate);
	InventoryItems<ExpiringInventoryItem> findByProductAndExpirationDateAfter(Article article , LocalDate localDate);
}

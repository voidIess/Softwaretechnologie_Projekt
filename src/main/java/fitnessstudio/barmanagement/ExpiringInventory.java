package fitnessstudio.barmanagement;

import org.salespointframework.inventory.MultiInventory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Streamable;

import java.time.LocalDate;

public interface ExpiringInventory extends MultiInventory<ExpiringInventoryItem> {
	Streamable<ExpiringInventoryItem> findByExpirationDate(LocalDate localDate);

	Streamable<ExpiringInventoryItem> findByExpirationDateAfterOrderByExpirationDateAsc(LocalDate localDate);
}

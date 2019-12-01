package fitnessstudio.barmanagement;


import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;


@PreAuthorize("hasRole('STAFF')")
@Controller
public class CatalogController {

	private final ArticleCatalog catalog;
	private final UniqueInventory<UniqueInventoryItem> inventory;

	public CatalogController(ArticleCatalog catalog, UniqueInventory<UniqueInventoryItem> inventory) {
		this.catalog = catalog;
		this.inventory = inventory;
	}

	@GetMapping("/catalog")
	public String catalog(Model model) {
		model.addAttribute("catalog", catalog.findAll());
		return "bar/catalog";
	}

	@GetMapping("/article/{article}")
	public String detail(@PathVariable @ModelAttribute Article article, Model model) {

		var quantity = inventory.findByProductIdentifier(Objects.requireNonNull(article.getId()))
			.map(InventoryItem::getQuantity)
			.orElse(Quantity.of(0));
		model.addAttribute("article", article);
		model.addAttribute("quantity", quantity);
		model.addAttribute("orderable", quantity.isGreaterThan(Quantity.of(0)));

		return "bar/detail";

	}
}

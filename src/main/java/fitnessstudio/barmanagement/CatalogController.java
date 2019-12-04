package fitnessstudio.barmanagement;


import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;


@Controller
public class CatalogController {


	private final BarManager barManager;

	public CatalogController(BarManager barManager) {
		this.barManager = barManager;
	}


	@GetMapping("/catalog")
	public String catalog(Model model) {
		model.addAttribute("catalog", barManager.getAllArticles());
		return "catalog";
	}

	@GetMapping("/article/{article}")
	public String detail(@PathVariable @ModelAttribute Article article, Model model) {

		Quantity quantity = barManager.getArticleQuantity(article);
		model.addAttribute("article", article);
		model.addAttribute("quantity", quantity);
		model.addAttribute("orderable", quantity.isGreaterThan(Quantity.of(0)));

		return "detail";

	}
}

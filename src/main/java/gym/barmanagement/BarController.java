package gym.barmanagement;

import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class BarController {

	private final BarCatalog catalog;
	private final UniqueInventory<UniqueInventoryItem> inventory;

	public BarController(BarCatalog catalog, UniqueInventory<UniqueInventoryItem> inventory) {
		this.catalog = catalog;
		this.inventory = inventory;
	}


	@GetMapping("/sell_catalog")
	String SellingCatalog(Model model){

		model.addAttribute("catalog", catalog);
		model.addAttribute("name", "catalog.Article.title");

		return("sell_catalog");
	}

}

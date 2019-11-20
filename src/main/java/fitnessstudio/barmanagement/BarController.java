package fitnessstudio.barmanagement;

import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.quantity.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@SessionAttributes("cart")
public class BarController {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);
	private final ArticleCatalog catalog;
	private final UniqueInventory<UniqueInventoryItem> inventory;

	public BarController(ArticleCatalog catalog, UniqueInventory<UniqueInventoryItem> inventory) {
		this.catalog = catalog;
		this.inventory = inventory;
	}


	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/sell_catalog")
	String SellingCatalog(Model model) {

		model.addAttribute("inventory", inventory.findAll());
		return ("sell_catalog");
	}

	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@PostMapping("/addItemToCart")
	String addItem(@RequestParam("pid") Article article, @RequestParam("number") int number, @ModelAttribute Cart cart) {

		Quantity inventoryQuantity = inventory.findByProduct(article)
			.map(InventoryItem::getQuantity)
			.orElse(Quantity.NONE);

		Quantity allreadyOrderedQuantity = cart.stream().filter(x -> x.getProduct()
			.equals(article)).findFirst().map(CartItem::getQuantity).orElse(Quantity.NONE);

		if (!allreadyOrderedQuantity.add(Quantity.of(number)).isGreaterThan(inventoryQuantity)) {
			cart.addOrUpdateItem(article, Quantity.of(number));
		}

		LOG.info(cart.stream().map(x -> x.getProductName() + " " + x.getQuantity()).reduce("", ((x, y) -> x + y)));

		return ("redirect:sell_catalog");
	}

	@PreAuthorize("hasRole('STAFF') or hasRole('BOSS')")
	@GetMapping("/cart_items")
	String cartItems() {
		return ("cart_items");
	}

}

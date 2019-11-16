package fitnessstudio.barmanagement;

import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.inventory.UniqueInventory;
import org.salespointframework.inventory.UniqueInventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.quantity.Quantity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


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

		model.addAttribute("inventory", inventory.findAll());
		return("sell_catalog");
	}

	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	@PostMapping("/addItemToCart")
	String addItem(@RequestParam("pid") Article article, @RequestParam("number") int number, @ModelAttribute Cart cart) {

		Quantity inventoryQuantity = inventory.findByProduct(article)
			.map(InventoryItem::getQuantity)
			.orElse(Quantity.NONE);

		Quantity allreadyOrderedQuantity = cart.stream().filter(x -> x.getProduct()
			.equals(article)).findFirst().map(CartItem::getQuantity).orElse(Quantity.NONE);

		//if(!allreadyOrderedQuantity.add(Quantity.of(number)).isGreaterThan(inventoryQuantity)) {
			cart.addOrUpdateItem(article, Quantity.of(number));
		//}
		return ("redirect:cart_items");
	}

	@GetMapping("/cart_items")
	String cartItems() {
		//model.addAttribute("cart", cart);
		return ("cart_items");
	}

}

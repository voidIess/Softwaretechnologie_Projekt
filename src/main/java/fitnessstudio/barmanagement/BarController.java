package fitnessstudio.barmanagement;

import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.quantity.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;


@Controller
@SessionAttributes("cart")
public class BarController {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);
	private final OrderManager<Order> orderManager;
	private final BarManager barManager;

	public BarController(BarManager barManager, OrderManager<Order> orderManager) {
		this.barManager = barManager;
		this.orderManager = orderManager;
	}


	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/sell_catalog")
	public String SellingCatalog(Model model) {

		model.addAttribute("inventory", barManager.getAvailableArticles());
		return "bar/sell_catalog";
	}

	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/sell")
	public String sell(@ModelAttribute Cart cart, @Valid BarForm form, SessionStatus status) {

		// TODO: get member

		// TODO: choose cash or credit

		// TODO: check credit enough money

		// TODO: pay

		// TODO: update member( update credit and add invoice)

		// TODO: check if quantity = 0( add to "Nachbestelliste )

		// TODO: update stock

		status.setComplete();
		return "redirect:/";
	}

	// the cart is will stay in the controller, as it has an 1:1 relation to session(?)
	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/addItemToCart")
	String addItem(@RequestParam("pid") Article article, @RequestParam("number") int number, @ModelAttribute Cart cart) {
		barManager.addArticleToCart(article, Quantity.of(number), cart);
		return ("redirect:sell_catalog");
	}


	@GetMapping("/orders")
	@PreAuthorize("hasRole('STAFF')")
	public String orders(Model model) {
		model.addAttribute("ordersCompleted", orderManager.findBy(OrderStatus.COMPLETED));
		return "bar/orders";
	}

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/cart_items")
	public String cartItems() {
		return "bar/cart_items";
	}

}

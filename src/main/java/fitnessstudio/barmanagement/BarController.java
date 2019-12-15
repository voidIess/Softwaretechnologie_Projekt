package fitnessstudio.barmanagement;

import fitnessstudio.member.Member;
import fitnessstudio.member.MemberManagement;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.payment.Cash;
import org.salespointframework.payment.PaymentCard;
import org.salespointframework.payment.PaymentMethod;
import org.salespointframework.quantity.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;
import java.util.Optional;


@Controller
@SessionAttributes("cart")
public class BarController {

	private static final String ERROR = "error";
	private static final String STATUS = "status";

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);
	private final OrderManager<Order> orderManager;
	private final BarManager barManager;
	private final MemberManagement memberManagement;

	public BarController(BarManager barManager, OrderManager<Order> orderManager, MemberManagement memberManagement) {
		this.barManager = barManager;
		this.orderManager = orderManager;
		this.memberManagement = memberManagement;
	}


	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/sell_catalog")
	public String SellingCatalog(Model model) {

		model.addAttribute("inventory", barManager.getAvailableArticles());
		return "bar/sell_catalog";
	}

	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/checkout")
	public String checkout(@ModelAttribute Cart cart, CheckoutForm form, Model model){
		model.addAttribute("cart_price", cart.getPrice());
		model.addAttribute("form", form);
		return "bar/checkout";
	}

	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/checkout")
	public String postCheckout(@ModelAttribute Cart cart, @Valid CheckoutForm form, SessionStatus status, Model model) {
		long customerId = Long.parseLong(form.getCustomerId());

		Optional<Member> optionalCustomer = memberManagement.findById(customerId);
		if (optionalCustomer.isEmpty()){
			model.addAttribute(ERROR, "A Customer with this id couldn't be found");
			model.addAttribute(STATUS, 400);
			return ERROR;
		}
		Member customer = optionalCustomer.get();
		cart.clear();

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

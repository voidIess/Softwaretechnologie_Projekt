package fitnessstudio.barmanagement;

import fitnessstudio.invoice.InvoiceEvent;
import fitnessstudio.invoice.InvoiceType;
import fitnessstudio.member.Member;
import fitnessstudio.member.MemberManagement;
import org.javamoney.moneta.Money;
import org.salespointframework.order.*;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
public class BarController {

	private static final String ERROR = "error";
	private static final String STATUS = "status";

	private static final Logger LOG = LoggerFactory.getLogger(CatalogDataInitializer.class);
	private final OrderManager<Order> orderManager;
	private final BarManager barManager;
	private final MemberManagement memberManagement;

	@Autowired
	private
	ApplicationEventPublisher applicationEventPublisher;

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
	public String checkout(@ModelAttribute Cart cart, CheckoutForm form, Model model) {
		model.addAttribute("cart_price", cart.getPrice());
		model.addAttribute("form", form);
		return "bar/checkout";
	}

	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/checkout")
	public String postCheckout(@ModelAttribute Cart cart, @Valid CheckoutForm form, SessionStatus status, Model model) {
		long customerId = Long.parseLong(form.getCustomerId());

		boolean payCash = form.getPaymentMethod().equals("1");

		//check that selling is possible with the given parameters
		Optional<Member> optionalCustomer = memberManagement.findById(customerId);
		if (optionalCustomer.isEmpty()) {
			model.addAttribute(ERROR, "A Customer with this id couldn't be found");
			model.addAttribute(STATUS, 400);
			return ERROR;
		}

		if (!cart.stream().map(cartItem -> barManager.stockAvailable(cartItem.getProduct().getId(), cartItem.getQuantity())).reduce(true, (x, y) -> x && y)) {
			model.addAttribute(ERROR, "Not enough stock, to do this");
			model.addAttribute(STATUS, 400);
			return ERROR;
		}

		UserAccount account = optionalCustomer.get().getUserAccount();
		Money price = Money.from(cart.getPrice());

		// for Invoice or for statistic
		Iterator<CartItem> iterator = cart.iterator();
		List<CartItem> cartItemList = new ArrayList<>();
		while (iterator.hasNext()) {
			CartItem cartItem = iterator.next();
			cartItemList.add(cartItem);
		}

		Order order = new Order(account, Cash.CASH);
		cart.addItemsTo(order);
		orderManager.payOrder(order);
		orderManager.completeOrder(order);
		cart.clear();

		StringBuilder articles = new StringBuilder();
		try {
			for (CartItem cartItem : cartItemList) {
				articles.append(" ").append(cartItem.getQuantity().getAmount().intValue()).append("x ")
						.append(cartItem.getProductName());
			}
		} catch (Exception e) {
			// No CartItem given
		}
		if (payCash) {
			applicationEventPublisher.publishEvent(new InvoiceEvent(this, customerId, InvoiceType.CASHPAYMENT,
					price, articles.toString()));
		} else {
			Member customer = optionalCustomer.get();
			if (customer.getCredit().isLessThan(price)) {
				model.addAttribute(ERROR, "Kunde hat nicht genug Guthaben");
				model.addAttribute(STATUS, 400);
				return ERROR;
			}

			memberManagement.memberPayOut(customerId, price, articles.toString());
		}

		cart.forEach(cartItem -> barManager.removeStock(cartItem.getProduct().getId(), cartItem.getQuantity()));

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
	public String addItem(@RequestParam("pid") Article article, @RequestParam("number") int number, @ModelAttribute Cart cart) {
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

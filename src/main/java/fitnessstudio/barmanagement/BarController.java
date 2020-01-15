package fitnessstudio.barmanagement;

import fitnessstudio.invoice.InvoiceEvent;
import fitnessstudio.invoice.InvoiceType;
import fitnessstudio.member.Member;
import fitnessstudio.member.MemberManagement;
import org.javamoney.moneta.Money;
import org.jetbrains.annotations.NotNull;
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

	/**
	 * @param barManager       The bar manager is used as database backend for our stock
	 * @param orderManager     The order manager is used as database backend for the orders
	 * @param memberManagement This used for payment transactions
	 */
	public BarController(BarManager barManager, OrderManager<Order> orderManager, MemberManagement memberManagement) {
		this.barManager = barManager;
		this.orderManager = orderManager;
		this.memberManagement = memberManagement;
	}

	/**
	 * an overview about the articles wich can be than added to the cart
	 *
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/sell_catalog")
	public String SellingCatalog(Model model) {

		model.addAttribute("inventory", barManager.getAvailableArticles());
		return "bar/sell_catalog";
	}

	/**
	 * the final page of selling an {@link Article} before a completed {@link Order} is placed
	 *
	 * @param cart
	 * @param form
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/checkout")
	public String checkout(@ModelAttribute Cart cart, CheckoutForm form, Model model) {
		model.addAttribute("cart_price", cart.getPrice());
		model.addAttribute("form", form);
		return "bar/checkout";
	}

	/**
	 * this post mapping will place the order and do all necessary steps
	 *
	 * @param cart
	 * @param form
	 * @param status
	 * @param model
	 * @return
	 */
	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/checkout")
	public String postCheckout(@ModelAttribute Cart cart, @Valid CheckoutForm form, SessionStatus status, Model model) {
		long customerId = Long.parseLong(form.getCustomerId());

		boolean payCash = form.getPaymentMethod().equals("1");

		//check that selling is possible with the given parameters
		Optional<Member> optionalCustomer = memberManagement.findById(customerId);
		if (getErrors(cart, model, optionalCustomer) || optionalCustomer.isEmpty()) {
			return ERROR;
		}

		UserAccount account = optionalCustomer.get().getUserAccount();
		Money price = Money.from(cart.getPrice());

		// for Invoice or for statistic
		StringBuilder articles = getCartItemsStringBuilder(cart);

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
		Order order = new Order(account, Cash.CASH);
		cart.addItemsTo(order);
		orderManager.payOrder(order);
		orderManager.completeOrder(order);
		cart.forEach(cartItem -> barManager.removeStock(cartItem.getProduct().getId(), cartItem.getQuantity()));
		cart.clear();
		status.setComplete();
		return "redirect:/";
	}

	@NotNull
	private StringBuilder getCartItemsStringBuilder(@ModelAttribute Cart cart) {
		Iterator<CartItem> iterator = cart.iterator();
		List<CartItem> cartItemList = new ArrayList<>();
		while (iterator.hasNext()) {
			CartItem cartItem = iterator.next();
			cartItemList.add(cartItem);
		}

		StringBuilder articles = new StringBuilder();
		try {
			for (CartItem cartItem : cartItemList) {
				articles.append(" ").append(cartItem.getQuantity().getAmount().intValue()).append("x ")
						.append(cartItem.getProductName());
			}
		} catch (Exception e) {
			// No CartItem given
		}
		return articles;
	}

	private boolean getErrors(@ModelAttribute Cart cart, Model model, Optional<Member> optionalCustomer) {
		boolean hasError = false;
		if (optionalCustomer.isEmpty()) {
			model.addAttribute(ERROR, "A Customer with this id couldn't be found");
			model.addAttribute(STATUS, 400);
			hasError = true;
		}

		if (!cart.stream().map(cartItem -> barManager.stockAvailable(cartItem.getProduct().getId(),
				cartItem.getQuantity())).reduce(true, (x, y) -> x && y)) {
			model.addAttribute(ERROR, "Not enough stock, to do this");
			model.addAttribute(STATUS, 400);
			hasError = true;
		}

		return hasError;
	}

	// the cart is will stay in the controller, as it has an 1:1 relation to session(?)
	@ModelAttribute("cart")
	Cart initializeCart() {
		return new Cart();
	}

	/**
	 * the post mapping, which will add an {@link Article} to the {@link Cart}
	 *
	 * @param article
	 * @param number
	 * @param cart
	 * @return
	 */
	@PreAuthorize("hasRole('STAFF')")
	@PostMapping("/addItemToCart")
	public String addItem(@RequestParam("pid") Article article, @RequestParam("number")
			int number, @ModelAttribute Cart cart) {
		barManager.addArticleToCart(article, Quantity.of(number), cart);
		return "redirect:/sell_catalog";
	}


	/**
	 * get an overview about all completed orders
	 *
	 * @param model
	 * @return
	 */
	@GetMapping("/orders")
	@PreAuthorize("hasRole('STAFF')")
	public String orders(Model model) {
		model.addAttribute("ordersCompleted", orderManager.findBy(OrderStatus.COMPLETED));
		return "bar/orders";
	}

	/**
	 * get an overview about all items which are currently added to the cart
	 *
	 * @return
	 */
	@PreAuthorize("hasRole('STAFF')")
	@GetMapping("/cart_items")
	public String cartItems() {
		return "bar/cart_items";
	}

}

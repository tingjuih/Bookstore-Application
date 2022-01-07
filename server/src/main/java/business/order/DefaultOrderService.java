package business.order;

import api.ApiException;
import business.BookstoreDbException;
import business.JdbcUtils;
import business.book.Book;
import business.book.BookDao;
import business.cart.ShoppingCart;
import business.cart.ShoppingCartItem;
import business.customer.Customer;
import business.customer.CustomerDao;
import business.customer.CustomerForm;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {

	private BookDao bookDao;
	private CustomerDao customerDao;
	private OrderDao orderDao;
	private LineItemDao lineItemDao;

	public void setBookDao(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	public void setOrderDao(OrderDao orderDao) {
		this.orderDao = orderDao;
	}

	public void setLineItemDao(LineItemDao lineItemDao) {
		this.lineItemDao = lineItemDao;
	}

	@Override
	public OrderDetails getOrderDetails(long orderId) {
		Order order = orderDao.findByOrderId(orderId);
		Customer customer = customerDao.findByCustomerId(order.getCustomerId());
		List<LineItem> lineItems = lineItemDao.findByOrderId(orderId);
		List<Book> books = lineItems
				.stream()
				.map(lineItem -> bookDao.findByBookId(lineItem.getBookId()))
				.collect(Collectors.toList());
		return new OrderDetails(order, customer, lineItems, books);
	}

	@Override
    public long placeOrder(CustomerForm customerForm, ShoppingCart cart) {

		validateCustomer(customerForm);
		validateCart(cart);

		// NOTE: MORE CODE PROVIDED NEXT PROJECT
		try (Connection connection = JdbcUtils.getConnection()) {
			Date date = getDate(
					customerForm.getCcExpiryMonth(),
					customerForm.getCcExpiryYear());
			return performPlaceOrderTransaction(
					customerForm.getName(),
					customerForm.getAddress(),
					customerForm.getPhone(),
					customerForm.getEmail(),
					customerForm.getCcNumber(),
					date, cart, connection);
		} catch (SQLException e) {
			throw new BookstoreDbException("Error during close connection for customer order", e);
		}
	}

	private Date getDate(String monthString, String yearString) {
		return new Date(Integer.parseInt(yearString), Integer.parseInt(monthString) - 1, 0);
	}

	private long performPlaceOrderTransaction(
			String name, String address, String phone,
			String email, String ccNumber, Date date,
			ShoppingCart cart, Connection connection) {
		try {
			connection.setAutoCommit(false);
			long customerId = customerDao.create(
					connection, name, address, phone, email,
					ccNumber, date);
			long customerOrderId = orderDao.create(
					connection,
					cart.getComputedSubtotal() + cart.getSurcharge(),
					generateConfirmationNumber(), customerId);
			for (ShoppingCartItem item : cart.getItems()) {
				lineItemDao.create(connection, customerOrderId,
						item.getBookId(), item.getQuantity());
			}
			connection.commit();
			return customerOrderId;
		} catch (Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new BookstoreDbException("Failed to roll back transaction", e1);
			}
			return 0;
		}
	}

	private int generateConfirmationNumber() {
		return (int)(Math.random() * 10e10);
	}

	private void validateCustomer(CustomerForm customerForm) {

		if (!nameIsValid(customerForm.getName())) {
			throw new ApiException.InvalidParameter("Invalid name field");
		}

		if (!phoneIsValid(customerForm.getPhone())) {
			throw new ApiException.InvalidParameter("Invalid phone field");
		}

		if (!emailIsValid(customerForm.getEmail())) {
			throw new ApiException.InvalidParameter("Invalid email field");
		}

		if (!nameIsValid(customerForm.getAddress())) {
			throw new ApiException.InvalidParameter("Invalid address field");
		}

		if (!ccNumberIsValid(customerForm.getCcNumber())) {
			throw new ApiException.InvalidParameter("Invalid ccNumber field");
		}

		if (!expiryDateIsValid(customerForm.getCcExpiryMonth(), customerForm.getCcExpiryYear())) {
			throw new ApiException.InvalidParameter("Invalid expiry date");
		}
	}

	private boolean nameIsValid(String name) {
		return name != null && !name.isEmpty() && name.length() > 3 && name.length() <= 45;
	}

	private boolean phoneIsValid(String phone) {
		if (phone == null || phone.isEmpty()) {
			return false;
		}
		phone = phone.replaceAll("[()\\s+\\-]", "");
		return phone.length() == 10 && phone.matches("[\\d]+");
	}

	private boolean emailIsValid(String email) {
		if (email == null || email.isEmpty() || email.contains(" ")) {
			return false;
		}
		int idx = email.indexOf("@");
		return idx > -1 && email.indexOf("@", idx + 1) == -1 && email.charAt(email.length() - 1) != '.';
	}

	private boolean ccNumberIsValid(String ccNumber) {
		if (ccNumber == null || ccNumber.isEmpty()) {
			return false;
		}
		ccNumber = ccNumber.replaceAll("[\\s+\\-]", "");
		return ccNumber.length() >= 14
				&& ccNumber.length() <= 16
				&& ccNumber.matches("[\\d]+");
	}

	private boolean expiryDateIsValid(String ccExpiryMonth, String ccExpiryYear) {
		int month = Integer.parseInt(ccExpiryMonth);
		if (month < 1 || month > 12) {
			return false;
		}
		LocalDateTime time = LocalDateTime.now();
		int year = Integer.parseInt(ccExpiryYear);
		if (year > time.getYear()) {
			return true;
		}
		return year == time.getYear() && month >= time.getMonthValue();
	}

	private void validateCart(ShoppingCart cart) {

		if (cart.getItems().size() <= 0) {
			throw new ApiException.InvalidParameter("Cart is empty.");
		}

		cart.getItems().forEach(item-> {
			if (item.getQuantity() < 0 || item.getQuantity() > 99) {
				throw new ApiException.InvalidParameter("Invalid quantity");
			}
			Book databaseBook = bookDao.findByBookId(item.getBookId());
			if (item.getBookForm().getPrice() != databaseBook.getPrice()) {
				throw new ApiException.InvalidParameter("Invalid price");
			}
			if (item.getBookForm().getCategoryId() != databaseBook.getCategoryId()) {
				throw new ApiException.InvalidParameter("Invalid category ID");
			}
		});
	}
}

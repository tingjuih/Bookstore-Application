
package business;

import business.book.BookDao;
import business.book.BookDaoJdbc;
import business.category.CategoryDao;
import business.category.CategoryDaoJdbc;
import business.customer.CustomerDao;
import business.customer.CustomerDaoJdbc;
import business.order.*;

public class ApplicationContext {

    private final BookDao bookDao;
    private final CategoryDao categoryDao;
    private final OrderService orderService;
    private final OrderDao orderDao;
    private final LineItemDao lintItemDao;
    private final CustomerDao customerDao;

    public static ApplicationContext INSTANCE = new ApplicationContext();

    private ApplicationContext() {
        categoryDao = new CategoryDaoJdbc();
        bookDao = new BookDaoJdbc();
        orderService = new DefaultOrderService();
        ((DefaultOrderService)orderService).setBookDao(bookDao);
        orderDao = new OrderDaoJdbc();
        ((DefaultOrderService)orderService).setOrderDao(orderDao);
        lintItemDao = new LineItemDaoJdbc();
        ((DefaultOrderService)orderService).setLineItemDao(lintItemDao);
        customerDao = new CustomerDaoJdbc();
        ((DefaultOrderService)orderService).setCustomerDao(customerDao);
    }

    public CategoryDao getCategoryDao() {
        return categoryDao;
    }

    public BookDao getBookDao() {
        return bookDao;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public OrderDao getOrderDao() {
        return orderDao;
    }

    public LineItemDao getLintItemDao() {
        return lintItemDao;
    }

    public CustomerDao getCustomerDao() {
        return customerDao;
    }
}

package hibernate.core.quickstart;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CustomerTest {
    private static SessionFactory sessionFactory;

    @BeforeClass
    public static void beforeClass(){
        // SessionFactory is factory for Session
        // Configuration configures SessionFactory with properties
        // by default hibernate.cfg.xml is taken as name of configuration file
        // buildSessionFactory() builds sessionFactory with given configuration
        sessionFactory = new Configuration().configure()
                                            .buildSessionFactory();
    }

    @AfterClass
    public static void afterClass(){
        if(!sessionFactory.isClosed()){
            sessionFactory.close();
        }
    }

    @Test
    public void testCustomer(){
        // get Session from SessionFactory
        Session session = sessionFactory.openSession();
        //Tx start
        Transaction tx = session.beginTransaction();

        // Creating Customer object  to be saved
        Customer customer = new Customer();
        customer.setCustomerName("Fizz");
        customer.setCustomerEmail("Fizz@Buzz.com");
        customer.setCustomerPhone(123456789L);

        session.save(customer);

        //Tx commit
        session.getTransaction().commit();

        int custId = customer.getCustomerId();

        session.close();

        //Check saved data
        session = sessionFactory.openSession();
        Customer foundCustomer = session.get(Customer.class, custId);

        assertNotNull(foundCustomer);
        System.out.println("testCustomer : foundCustomer.Id = "+ foundCustomer.getCustomerId()
                                         + "\nfoundCustomer.customerName = " + foundCustomer.getCustomerName()
                                         + "\nfoundCustomer.customerEmail = " + foundCustomer.getCustomerEmail()
                                         + "\nfoundCustomer.customerPhone = " + foundCustomer.getCustomerPhone());
        assertEquals("Fizz", foundCustomer.getCustomerName());
    }
}

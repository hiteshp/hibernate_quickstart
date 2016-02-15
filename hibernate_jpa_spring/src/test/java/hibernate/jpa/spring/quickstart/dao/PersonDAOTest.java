package hibernate.jpa.spring.quickstart.dao;

import hibernate.jpa.spring.quickstart.model.Person;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PersonDAOTest {
    private static ClassPathXmlApplicationContext context;
    @BeforeClass
    public static void setUp(){
        context = new ClassPathXmlApplicationContext("hibernate-quickstart-beans.xml");
    }

    @AfterClass
    public static void tearDown(){
        if(context != null && context.isActive()){
            context.close();
        }
    }

    @Test
    public void testPersonDAO() {
        assertNotNull(context);
        assertTrue(context.isActive());

        PersonDAO personDAO = context.getBean(PersonDAO.class);

        Person person = new Person();
        person.setName("TestPerson");
        person.setCountry("MCRN");

        //write
        personDAO.save(person);

        //read
        List<Person> personList = personDAO.list();

        assertNotNull(personList);
        assertFalse(personList.isEmpty());

        for (Person p : personList) {
            assertNotNull(p);
            System.out.println("Person List::" + p);
        }
    }
}

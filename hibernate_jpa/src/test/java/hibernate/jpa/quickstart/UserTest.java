package hibernate.jpa.quickstart;

import org.hibernate.LazyInitializationException;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UserTest extends AbstractTest{
    private static EntityManager entityManager;

    @BeforeClass
    public static void beforeClass(){
        openEntityManager();
    }

    @AfterClass
    public static void afterClass(){
        if(entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

    @Before
    public void beforeTest(){
        openEntityManager();
    }

    @Test
    public void testNewUser() {
        //EntityManager provides access to data
        assertNotNull(entityManager);

        //persist must be performed in a transaction
        entityManager.getTransaction().begin();

        User user = new User();
        user.setName(Long.toString(new Date().getTime()));

        entityManager.persist(user);

        //write to DB
        entityManager.getTransaction().commit();

        System.out.println("user.name=" + user.getName() + ", user.id=" + user.getId());

        //check if user was saved
        User foundUser = entityManager.find(User.class, user.getId());

        //foundUser is a concrete class (not a proxy)
        System.out.println("foundUser.name=" + foundUser.getName() + ", foundUser.id=" + foundUser.getName());

        assertEquals(user.getName(), foundUser.getName());
    }

    @Test(expected = Exception.class)
    public void testNewUserWithTxn() throws Exception {
        assertNotNull(entityManager);

        entityManager.getTransaction().begin();
        User user = new User();
        user.setName(Long.toString(new Date().getTime()));
        try {
            entityManager.persist(user);
            if (true) {
                throw new Exception();
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        }

        //check if user was saved
        User foundUser = entityManager.find(User.class, user.getId());
        assertNull(foundUser);
    }

    //test role relationship
    @Test
    public void testNewUserAndAddRole() {
        assertNotNull(entityManager);
        entityManager.getTransaction().begin();

        User user = new User();
        user.setName(Long.toString(new Date().getTime()));

        Role role = new Role();
        role.setName(Long.toString(new Date().getTime()));

        entityManager.persist(user);
        entityManager.persist(role);
        entityManager.getTransaction().commit();

        assertEquals(0, user.getRoles().size());    //role is not saved in user yet
        Role foundRole = entityManager.find(Role.class, role.getId());
        System.out.println("testNewUserAndAddRole : foundRole.name=" + role.getName() + ", foundRole.id=" + role.getId());


        entityManager.getTransaction().begin();
        user.addRole(role);                         //create relation : add role to user
        entityManager.merge(user);                  //save changes to user
        entityManager.getTransaction().commit();

        assertEquals(1, user.getRoles().size());

        Role relatedRole = user.getRoles().iterator().next();
        System.out.println("testNewUserAndAddRole : relatedRole.name=" + relatedRole.getName() + ", relatedRole.id=" + relatedRole.getId());

        assertEquals(foundRole.getName(),relatedRole.getName());
    }

    //NamedQuery Test
    @Test
    public void testFindUserByNamedQuery() throws Exception {
        assertNotNull(entityManager);
        entityManager.getTransaction().begin();

        User user = new User();
        String name = Long.toString(new Date().getTime());
        user.setName(name);

        entityManager.persist(user);
        entityManager.getTransaction().commit();
        entityManager.close();                      //to force hibernate to read values from database

        entityManager = Persistence.createEntityManagerFactory("hibernateQSPersistenceUnit").createEntityManager();

        User foundUser = entityManager.createNamedQuery("User.findByName", User.class).setParameter("name", name)
                                      .getSingleResult();

        assertNotNull(foundUser);
        assertEquals(name, foundUser.getName());
        System.out.println("testFindUserByNamedQuery : foundUser.name = "+ foundUser.getName());
    }

    //Lazy Initialization
    @Test
    public void testFindUser_WithLazy() throws Exception {
        assertNotNull(entityManager);
        entityManager.getTransaction().begin();

        User user = new User();                     //Persistent state
        String name = Long.toString(new Date().getTime());
        user.setName(name);

        Role role = new Role();                     //Persistent state
        role.setName(name);
        user.addRole(role);

        entityManager.persist(role);
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        entityManager.close();                      //to force hibernate to read values from database

        entityManager = Persistence.createEntityManagerFactory("hibernateQSPersistenceUnit").createEntityManager();
        User foundUser = entityManager.find(User.class, user.getId());

        //User with Proxy for Role
        assertNotNull(foundUser);
        assertEquals(name, foundUser.getName());
        System.out.println("testFindUser_WithLazy : foundUser.name = "+ foundUser.getName());

        //Role is fetched from Database when used  -- loaded on use/lazily
        //Queries Role and Relationship table together
        assertEquals(1, foundUser.getRoles().size());
    }

    //LazyInitialization test
    @Test(expected = LazyInitializationException.class)
    public void testFindUser_WithLazyError() throws Exception {
        assertNotNull(entityManager);

        //Transient state - not associated with session
        User user = new User();
        String name = Long.toString(new Date().getTime());
        user.setName(name);

        assertNotNull(entityManager);
        entityManager.getTransaction().begin();

        Role role = new Role();
        role.setName(name);
        user.addRole(role);

        //Persistent state - associated with session and id is generated
        entityManager.persist(role);
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        entityManager.close();

        entityManager = Persistence.createEntityManagerFactory("hibernateQSPersistenceUnit").createEntityManager();
        User foundUser = entityManager.find(User.class, user.getId());

        entityManager.close();

        //Detached state - once associated, but not now
        //main object can be accessed
        assertNotNull(foundUser);
        assertEquals(name, foundUser.getName());

        //relationship are not loaded after session is closed
        //throws LazyInitializationException
        assertEquals(1, foundUser.getRoles().size());
    }

    /**
     * Open entity manager if close
     */
    private static void openEntityManager(){
        if(entityManager == null || !entityManager.isOpen()){
            entityManager = Persistence.createEntityManagerFactory("hibernateQSPersistenceUnit").createEntityManager();
        }
    }
}

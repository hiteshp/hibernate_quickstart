package hibernate.jpa.spring.quickstart.dao;

import hibernate.jpa.spring.quickstart.model.Person;

import java.util.List;

public interface PersonDAO {
    public void save(Person p);
    public List<Person> list();
}

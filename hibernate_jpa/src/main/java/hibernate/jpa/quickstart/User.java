package hibernate.jpa.quickstart;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usr_tb") // @Table is optional
@NamedQuery(name="User.findByName", query = "select u from User u where u.name = :name")
public class User {
    @Id // @Id indicates that this is a unique primary key
    @GeneratedValue // @GeneratedValue indicates that value is automatically generated by the server
    private Long id;

    @Column(length = 32, unique = true)
    // the optional @Column allows us makes sure that the name is limited to a suitable size and is unique
    private String name;

    // note that no setter for ID is provided, Hibernate will generate the ID for us

    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    //relationships
    //hibernate will automatically create third table for Many to Many with PKs of 2 tables
    @ManyToMany
    private Set<Role> roles = new HashSet<Role>();

    public boolean addRole(Role role) {
        return roles.add(role);
    }

    public Set<Role> getRoles() {
        return roles;
    }
}

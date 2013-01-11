package models;

/**
 *
 * @author willy
 */
import java.util.*;
import javax.persistence.*;
import play.data.validation.*;

import play.db.jpa.*;

@Entity
public class User extends Model {
    
    @Email
    @Required
    public String email;
    
    @Required
    public String password;
    
    public String fullname;
    public boolean isAdmin;

    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }

    public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }

    public String toString() {
        return fullname;
    }
}

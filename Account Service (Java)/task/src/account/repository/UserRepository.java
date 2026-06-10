package account.repository;

import account.model.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class UserRepository {

    public final HashMap<String, User> usersBase = new HashMap<String, User>();

    UserRepository() {

    }

    public void addUser(User user) {
        usersBase.put(user.getEmail(), user);
    }

    public User getUser(String email) {
        return usersBase.get(email);
    }

    public boolean isUserExist(String email) {
        return usersBase.containsKey(email);
    }

}

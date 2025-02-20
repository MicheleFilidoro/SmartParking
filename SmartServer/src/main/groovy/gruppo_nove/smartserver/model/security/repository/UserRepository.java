package gruppo_nove.smartserver.model.security.repository;

import gruppo_nove.smartserver.model.user.UserProfile;
import gruppo_nove.smartserver.repository.GlobalRepository;
import org.springframework.stereotype.Repository;

/**
 * The type User repository.
 *
 */
@Repository
public class UserRepository {

    private final GlobalRepository repository;


    /**
     * Instantiates a new User repository.
     *
     * @param repository
     * 		the repository
     */
    public UserRepository(GlobalRepository repository) {
        this.repository = repository;
    }
    /**
     * Save user.
     *
     * @param user
     * 		the user
     */
    public Integer saveUser(UserProfile user) {
        return this.repository.addUser(user);
    }
    /**
     * Find by username user profile.
     *
     * @param username
     * 		the username
     *
     * @return the user profile
     */
    public UserProfile findByUsername(String username) {
        return this.repository.getUserByUsername(username);
    }



}

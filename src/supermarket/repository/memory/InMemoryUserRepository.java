package supermarket.repository.memory;

import java.util.Optional;
import supermarket.model.User;
import supermarket.repository.UserRepository;

/** Repository akun yang menyimpan data di memori. */
public class InMemoryUserRepository extends InMemoryRepository<User> implements UserRepository {

    /** {@inheritDoc} */
    @Override
    public Optional<User> findByUsername(String username) {
        for (User user : storage.values()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}

package hoangdung.vn.projectSpring.database.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import hoangdung.vn.projectSpring.entity.User;
import hoangdung.vn.projectSpring.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'run'");

        if(isTableEmpty()) {
            String passwordEncoded = passwordEncoder.encode("123456789");
            User newUser = new User();
            newUser.setName("Hoàng Đình Dũng");
            newUser.setEmail("hoangdinhdung0205@gmail.com");
            newUser.setPassword(passwordEncoded);
            // newUser.setUserCatalogueId(1L);
            newUser.setPhone("0354870745");
            this.userRepository.save(newUser);
            logger.info("Inserted new user: " + newUser);
        }

    }

    //check table empty
    private boolean isTableEmpty() {
        Long count = (Long) this.entityManager.createQuery("select count(id) from User").getSingleResult();
        return count == 0;
    }
    
}

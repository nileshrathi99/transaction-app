package dev.transactionapp.service;

import dev.transactionapp.entity.User;
import dev.transactionapp.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartupService {

    @Autowired
    private UserRepository userRepository;

    /**
     * This method is automatically called by Spring after the bean is constructed.
     * It creates two User objects with initial balances and currencies and saves them to the UserRepository.
     *
     * **Note:** Just for development purpose.
     */
    @PostConstruct
    public void init(){
        User user1 = new User("USD", Double.parseDouble("200"));
        User user2 = new User("INR", Double.parseDouble("500"));
        userRepository.save(user1);
        userRepository.save(user2);
    }

}

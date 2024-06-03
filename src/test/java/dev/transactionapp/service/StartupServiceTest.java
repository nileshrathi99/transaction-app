package dev.transactionapp.service;

import dev.transactionapp.entity.User;
import dev.transactionapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StartupServiceTest {

    @InjectMocks
    private StartupService startupService;

    @Mock
    private UserRepository userRepository;

    @Test
    void init(){
        User user1 = new User("USD", Double.parseDouble("200"));
        User user2 = new User("INR", Double.parseDouble("500"));
        when(userRepository.save(ArgumentMatchers.any())).thenReturn(user1);
        when(userRepository.save(ArgumentMatchers.any())).thenReturn(user2);
        startupService.init();
    }

}
package tdf.ctf.orderedhashes.services;

import com.google.common.hash.Hashing;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import tdf.ctf.orderedhashes.models.User;
import tdf.ctf.orderedhashes.repositories.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean checkExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public void saveUser(User user) {
        String hashedPassword = Hashing.sha256().hashString(user.getPassword(), StandardCharsets.UTF_8).toString();
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(hashedPassword);
        userRepository.save(newUser);
    }

    public void changePassword(User user, String newPassword) {
        String hashedPassword = Hashing.sha256().hashString(newPassword, StandardCharsets.UTF_8).toString();
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public ArrayList<User> getUsers() {
        return userRepository.findAll();
    }

}

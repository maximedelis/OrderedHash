package tdf.ctf.orderedhashes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tdf.ctf.orderedhashes.models.User;

import java.util.ArrayList;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    boolean existsByUsername(String username);

    ArrayList<User> findAll();
}

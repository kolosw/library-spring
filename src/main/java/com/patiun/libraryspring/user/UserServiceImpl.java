package com.patiun.libraryspring.user;

import com.patiun.libraryspring.exception.ElementNotFoundException;
import com.patiun.libraryspring.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find a user by username '" + username + "'"));
    }

    @Override
    public void signUp(String login, String password, String firstName, String lastName) throws ServiceException {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new ServiceException("A user with such login already exists");
        }
        String encodedPassword = passwordEncoder.encode(password);

        userRepository.save(new User(null, login, encodedPassword, firstName, lastName, false, UserRole.READER));
    }

    @Override
    public List<User> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .toList();
    }

    @Override
    public User getUserById(Integer id) {
        return getExistingUserById(id);
    }

    @Override
    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new ElementNotFoundException("Could not find a user by login = " + login));
    }

    @Override
    public void updateUserById(Integer id, String firstName, String lastName, UserRole role) {
        User targetUser = getExistingUserById(id);

        targetUser.setFirstName(firstName);
        targetUser.setLastName(lastName);
        targetUser.setRole(role);

        userRepository.save(targetUser);
    }

    @Override
    public void switchUserBlockedById(Integer id) {
        User foundUser = getExistingUserById(id);

        Boolean currentUserBlocked = foundUser.getBlocked();
        foundUser.setBlocked(!currentUserBlocked);

        userRepository.save(foundUser);
    }

    private User getExistingUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException("Could not find a user by id = " + id));
    }

}

package tdf.ctf.orderedhashes.controllers;

import com.google.common.hash.Hashing;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tdf.ctf.orderedhashes.models.LoginForm;
import tdf.ctf.orderedhashes.models.PasswordChangeForm;
import tdf.ctf.orderedhashes.models.RegistrationForm;
import tdf.ctf.orderedhashes.models.User;
import tdf.ctf.orderedhashes.services.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;

@Controller
@RequestMapping("")
public class WebController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public WebController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if ((authentication.getPrincipal() instanceof User)) {
            model.addAttribute("authenticated", true);
        } else {
            model.addAttribute("authenticated", false);
        }
        return "index";
    }

    @GetMapping("/register")
    public String register(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if ((authentication.getPrincipal() instanceof User)) {
            return "redirect:/";
        }

        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid RegistrationForm registrationForm, BindingResult bindingResult, Model model) {

        if (userService.checkExists(registrationForm.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "An account already exists for this username.");
            return "register";
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.saveUser(registrationForm.getUsername(), registrationForm.getPassword());
        model.addAttribute("registered", true);
        return "register";
    }

    @GetMapping("/login")
    public String login(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User) {
            return "redirect:/";
        }
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid LoginForm loginForm, BindingResult bindingResult, Model model, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        String hashedPassword = Hashing.sha256().hashString(loginForm.getPassword(), StandardCharsets.UTF_8).toString();
        try {
            Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginForm.getUsername(), hashedPassword));
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid username or password.");
            return "login";
        }

    }

    @GetMapping("/searchusers/sort:User.{attribute}/direction:{direction}")
    public String searchUsers(Model model, @PathVariable String attribute, @PathVariable String direction) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            ArrayList<User> users = userService.getUsers();

            if ((!attribute.equals("username") && !attribute.equals("password")) || (!direction.equals("asc") && !direction.equals("desc"))) {
                users = new ArrayList<>();
            }

            if (!direction.equals("asc") && !direction.equals("desc")) {
                users = new ArrayList<>();
            }

            Comparator<User> comparator = switch (attribute.toLowerCase()) {
                case "username" ->
                        Comparator.comparing(User::getUsername, Comparator.nullsFirst(String::compareToIgnoreCase)); // this default lol
                case "password" ->
                        Comparator.comparing(User::getPassword, Comparator.nullsFirst(String::compareToIgnoreCase));
                default -> Comparator.comparing(User::getUsername, Comparator.nullsFirst(String::compareToIgnoreCase));
            };

            if ("desc".equalsIgnoreCase(direction)) {
                comparator = comparator.reversed();
            }

            users.sort(comparator);
            
            model.addAttribute("users", users);
            return "searchusers";
        }

        return "redirect:/";
    }

    @GetMapping("/changepassword")
    public String changePassword(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User) {
            model.addAttribute("passwordChangeForm", new PasswordChangeForm());
            return "changepassword";
        }

        return "redirect:/";
    }

    @PostMapping("/changepassword")
    public String changePassword(@Valid PasswordChangeForm passwordChangeForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "changepassword";
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            userService.changePassword(user, passwordChangeForm.getPassword());
        }
        return "redirect:/";
    }

}

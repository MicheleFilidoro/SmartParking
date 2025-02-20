package gruppo_nove.smartserver.model.security.services;

import gruppo_nove.smartserver.model.security.model.LoginRequestDTO;
import gruppo_nove.smartserver.model.security.model.LoginResponseDTO;
import gruppo_nove.smartserver.model.security.model.RegistrationDTO;
import gruppo_nove.smartserver.model.security.repository.UserRepository;
import gruppo_nove.smartserver.model.user.UserProfile;
import gruppo_nove.smartserver.model.user.UserRole;
import gruppo_nove.smartserver.repository.GlobalRepository;
import gruppo_nove.smartserver.repository.SaldoRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.client.RestTemplate;




/**
 * The type Authentication service.
 */
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RestTemplate restTemplate;
    private final GlobalRepository repository;

    /**
     * Instantiates a new Authentication service.
     *
     * @param userRepository
     * 		the user repository
     * @param authenticationManager
     * 		the authentication manager
     * @param restTemplate
     * 		the rest template
     */
    public AuthenticationService(UserRepository userRepository, PasswordEncoder encoder,
                                 AuthenticationManager authenticationManager, RestTemplate restTemplate,TokenService tokenService,
                                 GlobalRepository repository
    ) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.restTemplate = restTemplate;
        this.tokenService = tokenService;
        this.repository = repository;
    }

    /**
     * Register user login response dto.
     *
     * @param reg
     * 		the reg
     *
     * @return the login response dto
     */
    public LoginResponseDTO registerUser(RegistrationDTO reg) {
        UserRole role = null;

        switch (reg.getRole()) {
            case 0:
                role = UserRole.SYSTEMADMIN;
                break;
            case 1:
                role = UserRole.UTENTEPREMIUM;
                break;
            case 2:
                role = UserRole.UTENTEBASE;
            default:
                role = UserRole.UTENTEBASE;
                break;
        }

        UserProfile user = new UserProfile(
                reg.getNome(),
                reg.getCognome(),
                reg.getUsername(),
                reg.getEmail(),
                encoder.encode(reg.getPassword()),
                //reg.getPassword(),
                role,
                true
        );

        UserProfile check = this.userRepository.findByUsername(user.getUsername());
       // UserProfile checkMail = this.userRepository.findByMail(user.getMail());

        if (check == null  /*&& checkMail == null*/) {
            this.userRepository.saveUser(user);
            this.repository.insertSaldo(user.getId());

        } else {
            return new LoginResponseDTO(null, "USER EXISTS", false);
        }


        return this.loginUser(new LoginRequestDTO(user.getUsername(), reg.getPassword()));
    }


    /**
     * Login user login response dto.
     *
     * @param login
     * 		the login
     *
     * @return the login response dto
     */
    public LoginResponseDTO loginUser(LoginRequestDTO login) {
        UserProfile user = userRepository.findByUsername(login.getUsername());


        if (user != null) {
            if (login.getPassword().equals(user.getPassword())/*login.getPassword().equals(user.getPassword())*/) {
                String token = tokenService.generateJwt(user);

                user.setPassword("");

                return new LoginResponseDTO(
                        user,
                        token,
                        user.isEnabled()
                );
            } else {
                return new LoginResponseDTO(user, "WRONG PASSWORD", false);
            }
        } else {
            return null;
        }
    }
}

package main.service;

import main.api.response.LogoutResponse;
import main.api.response.login.UserLoginInfoResponse;
import main.api.response.login.UserLoginResponse;
import main.model.Users;
import main.model.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserLoginService {

    private HashMap<String, Integer> identifierMap = new HashMap<>();

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private HttpSession httpSession;

    public UserLoginResponse getUserLoginInfo(String email, String password) {
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        UserLoginInfoResponse userLoginInfoResponse = new UserLoginInfoResponse();
        Iterable<Users> usersIterable = usersRepository.findAll();
        String identifier = httpSession.getId();
        for (Users f : usersIterable) {
            Optional<Users> optionalUser = usersRepository.findById(f.getId());
            if (email.equals(optionalUser.get().getEmail()) & password.equals(optionalUser.get().getPassword())) {
                int id = optionalUser.get().getId();
                identifierMap.put(identifier, id);
                userLoginInfoResponse.setId(id);
                userLoginInfoResponse.setName(optionalUser.get().getName());
                userLoginInfoResponse.setPhoto(optionalUser.get().getPhoto());
                userLoginInfoResponse.setEmail(optionalUser.get().getEmail());
                int i = optionalUser.get().getIsModerator();
                userLoginInfoResponse.setModerationCount(i);
                if (i == 0) {
                    userLoginInfoResponse.setModeration(false);
                    userLoginInfoResponse.setSettings(false);
                } else if (i == 1) {
                    userLoginInfoResponse.setModeration(true);
                    userLoginInfoResponse.setSettings(true);
                }

                userLoginResponse.setUser(userLoginInfoResponse);
                userLoginResponse.setResult(true);
                return userLoginResponse;
            }
        }
        userLoginResponse.setResult(false);
        return userLoginResponse;
    }

    public UserLoginResponse getUserCheck() {
        UserLoginResponse checkUserLoginResponse = new UserLoginResponse();
        UserLoginInfoResponse checkUserLoginInfoResponse = new UserLoginInfoResponse();
        if (identifierMap.size() > 0) {
            String identifier = httpSession.getId();
            for (Map.Entry<String, Integer> f : identifierMap.entrySet()) {
                if (f.getKey().equals(identifier)) {
                    int q = identifierMap.get(identifier);
                    Optional<Users> optionalUser = usersRepository.findById(q);
                    int id = optionalUser.get().getId();
                    checkUserLoginInfoResponse.setId(id);
                    checkUserLoginInfoResponse.setName(optionalUser.get().getName());
                    checkUserLoginInfoResponse.setPhoto(optionalUser.get().getPhoto());
                    checkUserLoginInfoResponse.setEmail(optionalUser.get().getEmail());
                    int i = optionalUser.get().getIsModerator();
                    checkUserLoginInfoResponse.setModerationCount(i);
                    if (i == 0) {
                        checkUserLoginInfoResponse.setModeration(false);
                        checkUserLoginInfoResponse.setSettings(false);
                    } else if (i == 1) {
                        checkUserLoginInfoResponse.setModeration(true);
                        checkUserLoginInfoResponse.setSettings(true);
                    }
                    checkUserLoginResponse.setUser(checkUserLoginInfoResponse);
                    checkUserLoginResponse.setResult(true);
                    return checkUserLoginResponse;
                }
            }
        }
        checkUserLoginResponse.setResult(false);
        return checkUserLoginResponse;
    }

    public LogoutResponse logoutUser() {
        LogoutResponse logoutResponse = new LogoutResponse();
        String identifier = httpSession.getId();
        for (Map.Entry<String, Integer> f : identifierMap.entrySet()) {
            if (f.getKey().equals(identifier)) {
                identifierMap.remove(identifier);
                logoutResponse.setResult(true);
                return logoutResponse;
            }
        }
        logoutResponse.setResult(true);
        return logoutResponse;
    }

    public HashMap<String, Integer> getIdentifierMap() {
        return identifierMap;
    }

    public void setIdentifierMap(HashMap<String, Integer> identifierMap) {
        this.identifierMap = identifierMap;
    }

}

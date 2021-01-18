package main.service;

import main.PostsException;
import main.api.response.LogoutResponse;
import main.api.response.login.UserLoginInfoResponse;
import main.api.response.login.UserLoginResponse;
import main.model.User;
import main.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserLoginService {

    private HashMap<String, Integer> identifierMap = new HashMap<>();

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Autowired
    public UserLoginService(UserRepository userRepository, HttpSession httpSession) {
        this.userRepository = userRepository;
        this.httpSession = httpSession;
    }

    public UserLoginResponse getUserLoginInfo(String email, String password) {
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        String identifier = httpSession.getId();
        List<User> findUser = userRepository.findUser(email, password);
        if (findUser.size() != 1) {
            userLoginResponse.setResult(false);
            return userLoginResponse;
        }
        identifierMap.put(identifier, findUser.get(0).getId());
        return setUserInfo(findUser.get(0), userLoginResponse);
    }


    public UserLoginResponse checkUser() {
        UserLoginResponse checkUserLoginResponse = new UserLoginResponse();
        if (identifierMap.size() > 0) {
            for (Map.Entry<String, Integer> f : identifierMap.entrySet()) {
                if (f.getKey().equals(httpSession.getId())) {
                    User user = userRepository.findById(identifierMap.get(httpSession.getId())).orElseThrow(PostsException::new);
                    return setUserInfo(user, checkUserLoginResponse);
                }
            }
        }
        checkUserLoginResponse.setResult(false);
        return checkUserLoginResponse;
    }

    private UserLoginResponse setUserInfo(User user, UserLoginResponse userLoginResponse) {
        UserLoginInfoResponse userLoginInfoResponse = new UserLoginInfoResponse();
        userLoginInfoResponse.setId(user.getId());
        userLoginInfoResponse.setName(user.getName());
        userLoginInfoResponse.setPhoto(user.getPhoto());
        userLoginInfoResponse.setEmail(user.getEmail());
        int isModerator = user.getIsModerator();
        userLoginInfoResponse.setModerationCount(isModerator);
        if (isModerator == 1) {
            userLoginInfoResponse.setModeration(true);
            userLoginInfoResponse.setSettings(true);
        }
        userLoginResponse.setUser(userLoginInfoResponse);
        userLoginResponse.setResult(true);
        return userLoginResponse;
    }

    public LogoutResponse logoutUser() {
        LogoutResponse logoutResponse = new LogoutResponse();
        identifierMap.remove(httpSession.getId());
        logoutResponse.setResult(true);
        return logoutResponse;
    }

    public HashMap<String, Integer> getIdentifierMap() {
        return identifierMap;
    }

    public void setIdentifierMap(HashMap<String, Integer> identifierMap) {
        this.identifierMap = identifierMap;
    }

    public boolean idUserAuthorized(){
        return identifierMap.containsKey(httpSession.getId());
    }
}

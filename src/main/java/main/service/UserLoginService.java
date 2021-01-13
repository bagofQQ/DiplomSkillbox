package main.service;

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
import java.util.Optional;

@Service
public class UserLoginService {

    private HashMap<String, Integer> identifierMap = new HashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession httpSession;

    public UserLoginResponse getUserLoginInfo(String email, String password) {
        UserLoginResponse userLoginResponse = new UserLoginResponse();

        String identifier = httpSession.getId();
        List<User> findUser = userRepository.findUser(email, password);
        if(findUser.size() == 1){
            for(User user : findUser){
                identifierMap.put(identifier, user.getId());
                return setUserInfo(user, userLoginResponse);
            }
        }
        userLoginResponse.setResult(false);
        return userLoginResponse;

    }



    public UserLoginResponse getUserCheck() {
        UserLoginResponse checkUserLoginResponse = new UserLoginResponse();
        if (identifierMap.size() > 0) {
            String identifier = httpSession.getId();
            for (Map.Entry<String, Integer> f : identifierMap.entrySet()) {
                if (f.getKey().equals(identifier)) {
                    int q = identifierMap.get(identifier);
                    Optional<User> optionalUser = userRepository.findById(q);
                    return setUserInfo(optionalUser.get(), checkUserLoginResponse);
                }
            }
        }
        checkUserLoginResponse.setResult(false);
        return checkUserLoginResponse;
    }

    private UserLoginResponse setUserInfo(User user, UserLoginResponse userLoginResponse){
        UserLoginInfoResponse userLoginInfoResponse = new UserLoginInfoResponse();
        userLoginInfoResponse.setId(user.getId());
        userLoginInfoResponse.setName(user.getName());
        userLoginInfoResponse.setPhoto(user.getPhoto());
        userLoginInfoResponse.setEmail(user.getEmail());
        int i = user.getIsModerator();
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

    public LogoutResponse logoutUser() {
        LogoutResponse logoutResponse = new LogoutResponse();
        String identifier = httpSession.getId();
        //TODO изменен выход
//        for (Map.Entry<String, Integer> f : identifierMap.entrySet()) {
//            if (f.getKey().equals(identifier)) {
//                identifierMap.remove(identifier);
//                logoutResponse.setResult(true);
//                return logoutResponse;
//            }
//        }
        identifierMap.remove(identifier);
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

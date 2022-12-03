package com.quiz.service;

import com.quiz.dao.UserDao;
import com.quiz.dto.UserDto;
import com.quiz.entities.NotificationStatus;
import com.quiz.entities.User;
import com.quiz.exceptions.EmailExistException;
import com.quiz.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserService {
    @Value("client.app")
    private String urlPath;

    private final MailSender mailSender;

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        User userdb = userDao.findByEmail(email);
        if (userdb == null) {
            throw new NotFoundException("user", "email", email);
        }
        return userdb;
    }

    public UserDto addAdminUser(User user) {
        User userdb =userDao.findByEmail(user.getEmail());
        if(userdb != null){
            throw new EmailExistException("User with this email already exist");
        }
        user.setRole(user.getRole());
        user.setPassword(UUID.randomUUID().toString());
        userDao.insert(user);

        if(!StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
              "Dear,%s \n" +
                      "Welcome to Quizer. Visit:" + urlPath +"activate/%s",
                    user.getEmail(),
                    user.getPassword()
            );
            mailSender.send(user.getEmail(),"Activation code",message);
        }
        return new UserDto(user);
    }

    public User findById(int id) {
        return userDao.findById(id);
    }

    public User findProfileInfoByUserId(int id) {
        return userDao.findProfileInfoByUserId(id);
    }

    public List<User> findFriendByUserId(int id, String sort) {
        return userDao.findFriendByUserId(id, sort);
    }


    public boolean updateUser(User user) {
        return userDao.updateUser(user);
    }

    public boolean updatePasswordById(int id, String newPassword) {
        return userDao.updatePasswordById(id, passwordEncoder.encode(newPassword));
    }

    public boolean updateStatusById(int id) {
        return userDao.updateStatusById(id);
    }

    public int getUserIdByEmail(String email) {
        return userDao.getUserIdByEmail(email);
    }
    public String getUserRoleByEmail(String email){
        return userDao.getUserRoleByEmail(email);
    }

    public boolean updateProfileImage(String imageUrl, int userId) {
        return userDao.updateProfileImage(imageUrl, userId);
    }

    public String getImageByUserId(int userId) {
        return userDao.getUserImageByUserId(userId);
    }

    public boolean changeNotificationStatus(String status, int userId) {
        return userDao.updateNotificationStatus(status, userId);
    }
    public List<User> findAdminsUsers(int userId){
        return userDao.findAdminsUsers(userId);
    }
    public List<User> findUsersByRoleStatus(String role, String status, int userId) {

        if(status.equals("AllStatus") && role.equals("AllRole")){ return userDao.findAdminsUsers(userId);}
        if(status.equals("AllStatus") && !role.equals("AllRole")){ return userDao.getUsersByRole(role,userId);}
        if(!status.equals("AllStatus") && role.equals("AllRole")){ return userDao.getUsersByStatus(status,userId);}
        return userDao.getUsersByRoleStatus(role,status,userId);
    }
    public List<User> getUsersByFilter(String searchByUser, int userId) {
        return userDao.getUsersByFilter(searchByUser, userId);
    }

    public void deleteUserById(int id) { userDao.deleteUserById(id); }

    public NotificationStatus getNotificationStatus(int userId) {
        return userDao.getUserNotification(userId);
    }
    public Integer getRatingByUser(int userId) {
        return userDao.getRatingByUser(userId);
    }

    public List<User> getRating(int from, int to) {
        return userDao.getRating(from, to);
    }

    public List<User> getRatingInRange(int userId, int range) {
        return userDao.getRatingInRange(userId, range);
    }

    public List<User> filterFriendByUserId(String userSearch, int userId, String sort) {
        return userDao.filterFriendByUserId(userSearch, userId, sort);
    }

    public boolean activateUser(String code) {
        User user = userDao.findByActivationCode(code);
        if(user == null){
            return false;
        }
        user.setPassword(null);
        userDao.insert(user);
        return true;
    }

    public User findByPassword(String code) {
       return userDao.findByActivationCode(code);
    }
}


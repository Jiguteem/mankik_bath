
package com.manbath.bath.service;

import com.manbath.bath.DTO.UserDTO;
import com.manbath.bath.DTO.UserPostDTO;
import com.manbath.bath.entitiy.Bath;
import com.manbath.bath.entitiy.User;
import com.manbath.bath.repository.BathRepository;
import com.manbath.bath.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
public class UserService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BathRepository bathRepository;
    @Transactional
    public Object SaveUser(UserPostDTO userDTO){
        log.info("User saveUser");
        UserDTO result = new UserDTO();
        result.setResult(false);
        if(userDTO.getUserKey() != null && userDTO.getToken() != null&&!userDTO.getUserKey().equals("")&&!userDTO.getToken().equals("")){
            User tmpUser = userRepository.getfindByUserid(userDTO.getUserKey());
            if(tmpUser == null){
                tmpUser = new User();
                tmpUser.setUserid(userDTO.getUserKey());
                tmpUser.setToken(userDTO.getToken());
                tmpUser.setUsername(userDTO.getName());
                userRepository.save(tmpUser);
                result.setObject(tmpUser);
                result.setResult(true);
                return result;
            }else {
                if (userDTO.getToken().equals(tmpUser.getToken()) ) {
                    result.setObject(tmpUser.getBathid());
                    result.setResult(true);
                    return result;
                } else {
                    return result;
                }
            }
        }else {
            return result;
        }
    }

    @Transactional
    public Object SaveUserBath(String id,UserPostDTO userDTO){
        log.info("User SaveUserBath");
        UserDTO result = new UserDTO();
        result.setResult(false);
        if(userDTO.getUserKey() != null && userDTO.getToken() != null&&!userDTO.getUserKey().equals("")&&!userDTO.getToken().equals("")) {
            User tmpUser = userRepository.getfindByUserid(userDTO.getUserKey());
            if(tmpUser == null){
                return result;
            }else{

                Bath tmpBath = bathRepository.findByBathid(id);
                if(tmpBath==null){
                    Bath bath = new Bath();
                    bath.setClean_valve(0);
                    bath.setCap(0);
                    bath.setTemp(0);
                    bath.setState(0);
                    bath.setLevel(0);
                    bath.setC_valve(0);
                    bath.setBathid(id);
                    bathRepository.save(bath);
                    tmpBath = bathRepository.findByBathid(id);
                    System.out.println(bath);
                    System.out.println(tmpBath);



                    tmpUser.setBathid(tmpBath);
                    userRepository.save(tmpUser);
                    result.setResult(true);
                    result.setObject(tmpUser);
                    return result;
                }else{

                    tmpBath = bathRepository.findByBathid(id);
                    System.out.println("1111");
                    System.out.println(tmpBath);
                    System.out.println("1111");



                    tmpUser.setBathid(tmpBath);
                    userRepository.save(tmpUser);
                    result.setResult(true);
                    result.setObject(tmpUser);
                    return result;
                }

            }
        }
        return result;
    }
}

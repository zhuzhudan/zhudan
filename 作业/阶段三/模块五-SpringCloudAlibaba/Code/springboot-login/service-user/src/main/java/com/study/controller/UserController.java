package com.study.controller;

import com.service.CodeService;
import com.study.pojo.AuthCode;
import com.study.pojo.Token;
import com.study.pojo.User;
import com.study.service.TokenService;
import com.study.service.UserService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Reference
    private CodeService codeService;

    /**
     * 注册接⼝
     * @return true成功，false失败
     */
    @GetMapping("/register/{email}/{password}/{code}")
    public Boolean register(@PathVariable(name = "email") String email,
                            @PathVariable(name = "password") String password,
                            @PathVariable(name = "code") String code,
                            HttpSession session){
        // 判断code是否正确
        Integer status = 0;
        AuthCode authCode = codeService.findByEmail(email, code);
        if (authCode == null){
            status = 1;
        }
        Date now = new Date();
        Long diff = now.getTime() - authCode.getExpireTime().getTime();
        if (diff > 0){
            status = 2;
        }

        // 如果code正确
        if (status == 0){
            return userService.saveUser(email, password);
        } else {
            if (status == 1)
                session.setAttribute("message", "校验码错误");
            else if (status == 2)
                session.setAttribute("message", "已超时，请重新注册");
            return false;
        }

    }

    /**
     * 根据邮箱判断是否已注册
     * @return true代表已经注册过，false代表尚未注册
     */
    @GetMapping("/isRegistered/{email}")
    public Boolean isRegistered(@PathVariable(name = "email") String email){
        User user = userService.findUserByEmail(email);
        if (user == null){
            return false;
        }
        return true;
    }

    /**
     * 登录接⼝，验证⽤户名密码合法性
     * 根据⽤户名和密码⽣成token，token存⼊数据库，并写⼊cookie中
     * 登录成功返回邮箱地址
     */
    @GetMapping("/login/{email}/{password}")
    public String login(@PathVariable(name = "email") String email,
                        @PathVariable(name = "password") String password,
                        HttpServletResponse response){
        User user = userService.findUserByEmail(email);
        if (user == null){
            return null;
        }
        if (!user.getPassword().equals(password)){
            return null;
        }

        // 模拟生成token
        String uuid = UUID.randomUUID().toString();

        // 将token存入数据库中
        boolean isSaveToken = tokenService.saveToken(email, uuid);
        if (!isSaveToken){
            return null;
        }

        // 将token存入cookie中
        Cookie cookie = new Cookie("token",uuid);//创建新cookie
        cookie.setMaxAge(-1);// 设置存在时间为5分钟
        cookie.setPath("/");//设置作用域
        response.addCookie(cookie);
        // session.setAttribute("token", uuid);
        return email;
    }

    /**
     * 根据token查询⽤户登录邮箱接⼝
     */
    @GetMapping("/info/{token}")
    public String info(@PathVariable(name = "token") String token){
        Token tokenCode = tokenService.findTokenByCode(token);
        if (tokenCode == null){
            return null;
        }
        return tokenCode.getEmail();
    }


}

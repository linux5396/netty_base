package com.linxu.netty.controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;

/**
 * @author linxu
 * @date 2019/8/30
 * <tip>take care of yourself.everything is no in vain.</tip>
 * 在实际开发中，很多人会遇到一个问题，就是跨域。
 * 首先这么说，通常将页面放置网关项目的相同地方即可避免跨域，
 * 但是实际开发过程中的测试可能是前后端分离的本地测试，那么这个时候
 * 就需要允许跨域请求，否则请求失败。
 */
//maxAge为cookie的有效期
@CrossOrigin(origins = "http://localhost:10987 ", allowCredentials = "true", maxAge = 3600L)
@RestController
public class AllowCORSController {

    @GetMapping("/test")
    public String login(HttpServletRequest request) {
        request.getSession().setAttribute("User", "OK");
        return "login success!";
    }
    @GetMapping("/op")
    public String ops(HttpServletRequest request){
        if (request.getSession().getAttribute("User")!=null){
            return "OK";
        }else {
            return "ERROR";
        }
    }
}

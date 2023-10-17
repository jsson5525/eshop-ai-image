package com.kw.controllers;

import com.kw.beans.LoginBean;
import com.kw.constants.Constant;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login_v2";
    }

    @PostMapping("/login.do")
    public String doLogin(@RequestParam Map<String,String> allParams, HttpServletRequest request, Model model){
       // System.out.println("param are " + allParams.entrySet());
        return go2Index(model);
    }

    @GetMapping("/index")
    public String showIndexPage(Model model) {
        return go2Index(model);
    }

    public String go2Index(Model model){
        model.addAttribute(Constant.Model_Key_Menu_Light_Id,Constant.Menu_Dashboard);
        model.addAttribute(Constant.Menu_Active_Id,Constant.Menu_Dashboard_Act);
        model.addAttribute(Constant.Model_Key_Submenu_Light_Id,Constant.Submenu_DashboardV1);
        return "landing/index";
    }

}


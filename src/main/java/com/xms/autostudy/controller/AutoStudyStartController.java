package com.xms.autostudy.controller;

import com.xms.autostudy.analysis.Login;
import com.xms.autostudy.configuration.RuleConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * xumengsi
 */
@Controller
public class AutoStudyStartController {

    @Autowired
    private RuleConfiguration ruleConfiguration;

    @GetMapping(value="/autoStart")
    public void autoStart(HttpServletRequest request, HttpServletResponse response)throws IOException {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");
        Login login = new Login(ruleConfiguration);
        File file=login.getQRcode();
        InputStream is=new FileInputStream(file);
        BufferedImage bi= ImageIO.read(is);
        ImageIO.write(bi, "PNG", response.getOutputStream());
    }
}

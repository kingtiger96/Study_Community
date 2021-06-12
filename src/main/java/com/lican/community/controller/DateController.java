package com.lican.community.controller;

import com.lican.community.service.DateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
public class DateController {
    @Autowired
    private DateService dateService;

    @RequestMapping(path = "/data", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage(){
        return "/site/admin/data";
    }

    @RequestMapping(path = "/data/uv", method = RequestMethod.POST)
    public String getUV(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){
        long uv = dateService.calculateUV(start, end);
        model.addAttribute("uv",uv);
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate",end);

        return "forward:/data";
    }

    //统计活跃用户
    @RequestMapping(path = "/data/dau", method = RequestMethod.POST)
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date end, Model model){
        long uv = dateService.calculateDAU(start, end);
        model.addAttribute("dau",uv);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate",end);

        return "forward:/data";
    }
}

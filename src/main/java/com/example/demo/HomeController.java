package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    PotluckListRepository potluckListRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String PotluckList(Model model) {
        model.addAttribute("potluckLists", potluckListRepository.findAll());
        return "potluckList";
    }

    @GetMapping("/add")
    public String listForm(Model model) {
        model.addAttribute("potluckList", new PotluckList());
        return "listform";
    }

    @PostMapping("/process")
    public String processForm(@Valid PotluckList potluckList,
                              BindingResult result, @RequestParam("file") MultipartFile file) {
    if (result.hasErrors() || file.isEmpty()) {
        return "listform";
    }
    try {
            Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            String url = uploadResult.get("url").toString();
            int i = url.lastIndexOf('/');
            url=url.substring(i+1);
            url="http://res.cloudinary.com/ajkmonster/image/upload/w_200,h_200/"+url;
            potluckList.setPicture(url);
            potluckListRepository.save(potluckList);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/listform";
        }
        return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showToDoList(@PathVariable("id") long id, Model model) {
        model.addAttribute("potluckList", potluckListRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateToDoList(@PathVariable("id") long id, Model model) {
        model.addAttribute("potlucklist", potluckListRepository.findById(id).get());
        return "listform";
    }

    @RequestMapping("/delete/{id}")
    public String delToDoList(@PathVariable("id") long id) {
        potluckListRepository.deleteById(id);
        return "redirect:/";
    }
}

package com.munivel.Employee_Management_System.WebContorller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
public class Home {

  @GetMapping("/home")
  public String home(Model model) {
    model.addAttribute("message", "Hello world");
    return "Welocome";
  }
}

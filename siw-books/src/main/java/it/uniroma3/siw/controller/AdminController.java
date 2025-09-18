package it.uniroma3.siw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

	@GetMapping("/admin") 
	public String homePage() {
		return "admin/adminHomePage.html";
	}
	
}

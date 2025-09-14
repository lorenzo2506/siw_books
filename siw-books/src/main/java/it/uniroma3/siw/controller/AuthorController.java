package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.validator.AuthorValidator;
import jakarta.validation.Valid;

@Controller
public class AuthorController {

	@Autowired private AuthorService authorService;
	@Autowired private AuthorValidator authorValidator;
	
	
	@GetMapping("/authors")
	public String showAuthors(Model model) {
		model.addAttribute("authors", this.authorService.getAllAuthor());
		return "authors.html";
	}
	
	
	@GetMapping("/author/{id}")
	public String showAuthor(@PathVariable("id") Long id, Model model) {
		
		model.addAttribute("author", this.authorService.getAuthor(id));
		return "author.html";
	}
	
	
	@GetMapping("/authors/add")
	public String showFormNewAuthor(Model model) {
		
		model.addAttribute("author", new Author());
		return "formNewAuthor.html";
	}
	
	
	@PostMapping("/authors/add")
	public String addAuthor(@Valid @ModelAttribute("author") Author author, BindingResult bindingResult ,Model model ) {
		
		this.authorValidator.validate(author, bindingResult);
		if(bindingResult.hasErrors())
			return "formNewAuthor.html";
		
		this.authorService.addAuthor(author);
		return "redirect:/author/"+author.getId();
	}
	
	
}

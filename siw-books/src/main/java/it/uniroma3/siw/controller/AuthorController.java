package it.uniroma3.siw.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.ImageStorageService;
import it.uniroma3.siw.validator.AuthorValidator;
import jakarta.validation.Valid;

@Controller
public class AuthorController {

	@Autowired private AuthorService authorService;
	@Autowired private AuthorValidator authorValidator;
	@Autowired private ImageStorageService imageStorageService;
	
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
	public String addAuthor(@Valid @ModelAttribute("author") Author author, 
	                       BindingResult bindingResult,
	                       @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
	                       Model model) {
		
		this.authorValidator.validate(author, bindingResult);
		
		// Validazione immagine (opzionale per l'autore)
		if (imageFile != null && !imageFile.isEmpty()) {
			// Valida tipo file
			if (!imageStorageService.isValidImageType(imageFile.getContentType())) {
				bindingResult.rejectValue("name", "error.author.image", 
					"Tipo file non valido. Usa solo JPEG, PNG, GIF o WebP");
			}
			
			// Valida dimensione (max 5MB)
			if (imageFile.getSize() > 5 * 1024 * 1024) {
				bindingResult.rejectValue("name", "error.author.image", 
					"File troppo grande. Dimensione massima: 5MB");
			}
		}
		
		if(bindingResult.hasErrors()) {
			return "formNewAuthor.html";
		}
		
		try {
			// Salva l'immagine se presente
			if (imageFile != null && !imageFile.isEmpty()) {
				String imagePath = imageStorageService.saveImage(imageFile);
				author.setImagePath(imagePath);
			}
			
			this.authorService.addAuthor(author);
			return "redirect:/author/" + author.getId();
			
		} catch (IOException e) {
			bindingResult.rejectValue("name", "error.author.image", 
				"Errore nel caricamento immagine: " + e.getMessage());
			return "formNewAuthor.html";
		}
	}
}
package it.uniroma3.siw.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.validator.BookValidator;
import jakarta.validation.Valid;

@Controller
public class BookController {
	
	@Autowired private BookService bookService;
	@Autowired private AuthorService authorService;
	@Autowired private BookValidator bookValidator;
	
	@GetMapping("/books")
	public String showBooks(Model model) {
		model.addAttribute("books", bookService.getAllBooks());
		return "books.html";
	}
	
	@GetMapping("/book/{id}")
	public String showBook(@PathVariable("id") Long id, Model model) {
		model.addAttribute("book", bookService.getBook(id));
		model.addAttribute("authors", this.bookService.getBook(id).getAuthors());
		return "book.html";
	}
	
	@GetMapping("/books/add")
	public String showFormNewBook(Model model) {
		Book book = new Book();
		// Inizializza la lista di autori direttamente nel book
		List<Author> authors = new ArrayList<>();
		authors.add(new Author());
		book.setAuthors(authors);
		
		model.addAttribute("book", book);
		return "formNewBook.html";
	}
	
	@PostMapping("/books/add")
	public String addBook(@Valid @ModelAttribute("book") Book book, 
	                     BindingResult bindingResult, 
	                     @RequestParam(required = false) String aggiungiCampoAutore,
	                     @RequestParam(required = false) String rimuoviCampoAutore,
	                     Model model) {
	    
		this.bookValidator.validate(book, bindingResult);
		
	    // Assicurati che la lista di autori esista
	    if (book.getAuthors() == null) {
	        book.setAuthors(new ArrayList<>());
	    }
	    
	    List<Author> authors = book.getAuthors();
	    
	    // Se la lista è vuota, aggiungi un autore vuoto
	    if (authors.isEmpty()) {
	        authors.add(new Author());
	    }
	    
	    // Gestione aggiunta autore
	    if (aggiungiCampoAutore != null) {
	        authors.add(new Author());
	        model.addAttribute("book", book);
	        return "formNewBook.html";
	    }
	    
	    // Gestione rimozione autore
	    if (rimuoviCampoAutore != null) {
	        int index = Integer.parseInt(rimuoviCampoAutore);
	        if (index > 0 && authors.size() > 1) {
	            authors.remove(index);
	        }
	        model.addAttribute("book", book);
	        return "formNewBook.html";
	    }
	    
	    // Controllo errori di binding
	    if(bindingResult.hasErrors()) {    
	        return "formNewBook.html";
	    }
	    
	    // Validazione autori
	    for (int i = 0; i < authors.size(); i++) {
	        Author author = authors.get(i);
	        
	        // Controlla se i campi sono vuoti
	        if (author.getName() == null || author.getSurname() == null || author.getDateOfBirth()==null) {
	            bindingResult.rejectValue("authors[" + i + "].name", "author.empty", "Nome, cognome e data di nascita sono obbligatori");
	            continue;
	        }
	        
	        // Controlla se l'autore esiste nel database
	        if (!authorService.existsAuthor(author)) {
	            bindingResult.rejectValue("authors[" + i + "].name", "author.notInDatabase", "Questo autore non esiste nel database");
	        }
	    }
	    
	    if (bindingResult.hasErrors()) {
	        return "formNewBook.html";
	    }
	    
	    // Salva il libro con gli autori
	    this.bookService.addBook(book, authors);
	    return "redirect:/book/" + book.getId();
	}
}
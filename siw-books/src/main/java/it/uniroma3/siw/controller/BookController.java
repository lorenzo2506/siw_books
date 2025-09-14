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
import jakarta.validation.Valid;

@Controller
public class BookController {
	
	@Autowired private BookService bookService;
	@Autowired private AuthorService authorService;
	
	@GetMapping("/books")
	public String showBooks(Model model) {
		
		model.addAttribute("books", bookService.getAllBooks());
		return "books.html";
	}
	
	
	@GetMapping("/book/{id}")
	public String showBook(@PathVariable("id") Long id, Model model) {
		
		model.addAttribute("book", bookService.getBook(id));
		return "book.html";
	}
	
	
	@GetMapping("/books/add")
	public String showFormNewBook(Model model) {
		
		List<Author> authors = new ArrayList<Author>();
		authors.add(new Author());
		model.addAttribute("authors", authors);
		model.addAttribute("book", new Book());
		return "formNewBook.html";
	}
	
	
	@PostMapping("/books/add")
	public String addBook(@Valid @ModelAttribute("book") Book book, BindingResult bindingResult, @RequestParam(required = false) String aggiungiCampoAutore,
            @RequestParam(required = false) String rimuoviCampoAutore ,@ModelAttribute("authors") List<Author> authors, Model model) {
		
		if(bindingResult.hasErrors()) {	
	        model.addAttribute("authors", authors);
			return "formNewBook.html";
		}
		
		
		if (aggiungiCampoAutore != null) {
			
			authors.add(new Author());
			model.addAttribute("authors", authors);
	        model.addAttribute("book", book);
	        return "formNewBook.html";
	        
	    }
	    
	    
	    if (rimuoviCampoAutore != null) {
	        int index = Integer.parseInt(rimuoviCampoAutore);
	        if (index > 0 && authors.size()>1) {
	            authors.remove(index);
	        }
	        model.addAttribute("authors", authors);
	        model.addAttribute("book", book);
	        return "formNewBook.html";
	    }
	    
	    
        for (int i = 0; i < authors.size(); i++) {
            Author author = authors.get(i);
            
            if (author.getName() == null || author.getSurname() == null) {
                bindingResult.rejectValue("authors[" + i + "].name", "author.empty", "Nome e cognome sono obbligatori");
                continue;
            }
            
            if (!authorService.existsAuthor(author))
                bindingResult.rejectValue("authors[" + i + "].name", "author.notInDatabase", "Questo autore non esiste nel database");
        }
		
        if (bindingResult.hasErrors()) {
            model.addAttribute("authors", authors);
            return "formNewBook";
        }
        
        this.bookService.addBook(book, authors);
	    
		return "redirect:/book/" + book.getId();
	}

}

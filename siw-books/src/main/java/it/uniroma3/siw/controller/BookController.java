package it.uniroma3.siw.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.service.AuthorService;
import it.uniroma3.siw.service.BookService;
import it.uniroma3.siw.service.ImageStorageService;
import it.uniroma3.siw.validator.AdminValidator;
import it.uniroma3.siw.validator.BookValidator;
import jakarta.validation.Valid;

@Controller
public class BookController {
	
	@Autowired private BookService bookService;
	@Autowired private AuthorService authorService;
	@Autowired private BookValidator bookValidator;
	@Autowired private ImageStorageService imageStorageService;
	@Autowired private AdminValidator adminValidator;
	
	@Value("${app.image.upload.dir:uploads/images}")
	private String uploadDir;
	
	// Endpoint per servire le immagini
	@GetMapping("/images/{filename:.+}")
	public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
		try {
			Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			
			if (resource.exists() && resource.isReadable()) {
				String contentType = Files.probeContentType(filePath);
				if (contentType == null) {
					contentType = "application/octet-stream";
				}
				
				return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
					.body(resource);
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/books")
	public String showBooks(Model model) {
		model.addAttribute("books", bookService.getAllBooks());
		return "books.html";
	}
	
	@GetMapping("/book/{id}")
	public String showBook(@PathVariable("id") Long id, Model model) {
		Book book = bookService.getBook(id);
		
		model.addAttribute("book", book);
		model.addAttribute("authors", book.getAuthors());
		return "book.html";
	}
	
	@GetMapping("/admin/books/add")
	public String showFormNewBook(Model model) {
		Book book = new Book();
		// Inizializza la lista di autori direttamente nel book
		List<Author> authors = new ArrayList<>();
		authors.add(new Author());
		book.setAuthors(authors);
		
		model.addAttribute("book", book);
		return "admin/formNewBook.html";
	}
	
	@PostMapping("/admin/books/add")
	public String addBook(@Valid @ModelAttribute("book") Book book, 
	                     BindingResult bindingResult, 
	                     @RequestParam(required = false) String aggiungiCampoAutore,
	                     @RequestParam(required = false) String rimuoviCampoAutore,
	                     @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
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
	        return "admin/formNewBook.html";
	    }
	    
	    // Gestione rimozione autore
	    if (rimuoviCampoAutore != null) {
	        int index = Integer.parseInt(rimuoviCampoAutore);
	        if (index > 0 && authors.size() > 1) {
	            authors.remove(index);
	        }
	        model.addAttribute("book", book);
	        return "admin/formNewBook.html";
	    }
	    
	    // Validazione immagini
	    if (imageFiles != null && !imageStorageService.areAllFilesEmpty(imageFiles)) {
	        int nonEmptyFiles = imageStorageService.countNonEmptyFiles(imageFiles);
	        
	        if (nonEmptyFiles > 5) {
	            bindingResult.rejectValue("title", "error.book.images", "Puoi caricare massimo 5 immagini");
	        }
	        
	        if (!imageStorageService.areValidImageTypes(imageFiles)) {
	            bindingResult.rejectValue("title", "error.book.images", "Tipo file non valido. Usa solo JPEG, PNG, GIF o WebP");
	        }
	        
	        if (!imageStorageService.isValidTotalSize(imageFiles, 20 * 1024 * 1024)) {
	            bindingResult.rejectValue("title", "error.book.images", "Dimensione totale troppo grande. Massimo 20MB");
	        }
	        
	        for (MultipartFile file : imageFiles) {
	            if (!file.isEmpty() && file.getSize() > 5 * 1024 * 1024) {
	                bindingResult.rejectValue("title", "error.book.images", 
	                    "File troppo grande: " + file.getOriginalFilename() + ". Max 5MB per immagine");
	                break;
	            }
	        }
	    }
	    
	    // Controllo errori di binding
	    if(bindingResult.hasErrors()) {    
	        return "admin/formNewBook.html";
	    }
	    
	    // Validazione autori
	    for (int i = 0; i < authors.size(); i++) {
	        Author author = authors.get(i);
	        
	        if (author.getName() == null || author.getSurname() == null || author.getDateOfBirth() == null) {
	            bindingResult.rejectValue("authors[" + i + "].name", "author.empty", 
	                "Nome, cognome e data di nascita sono obbligatori");
	            continue;
	        }
	        
	        if (!authorService.existsAuthor(author)) {
	            bindingResult.rejectValue("authors[" + i + "].name", "author.notInDatabase", 
	                "Questo autore non esiste nel database");
	        }
	    }
	    
	    if (bindingResult.hasErrors()) {
	        return "admin/formNewBook.html";
	    }
	    
	    try {
	        // Salva le immagini se presenti
	        if (imageFiles != null && !imageStorageService.areAllFilesEmpty(imageFiles)) {
	            List<String> imagePaths = imageStorageService.saveImages(imageFiles);
	            book.setImages(imagePaths);
	        }
	        
	        // Salva il libro con gli autori
	        this.bookService.addBook(book, authors);
	        return "redirect:/book/" + book.getId();
	        
	    } catch (IOException e) {
	        bindingResult.rejectValue("title", "error.book.images", "Errore nel caricamento immagini: " + e.getMessage());
	        return "admin/formNewBook.html";
	    }
	}
	
	@PostMapping("/admin/book/{id}/remove")
	public String removeBook(@Valid @ModelAttribute("Credentials") Credentials credentials, BindingResult bindingResult, @PathVariable("id") Long id, Model model) {
		
		this.adminValidator.validate(credentials, bindingResult);
		if(bindingResult.hasErrors()) {
			model.addAttribute("book", this.bookService.getBook(id));
			model.addAttribute("authors", this.bookService.getBook(id).getAuthors());
			return "book.html";
		}
		
		this.bookService.deleteBook(id);
		
		return "redirect:/books";
	}
}
package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repository.BookRepository;

@Service
public class BookService {

	@Autowired private BookRepository bookRepo;
	@Autowired private AuthorService authorService;
	@Autowired private AuthenticationService authService;
	
	public List<Book> getAllBooks() {
		return bookRepo.findAll();
	}
	
	public void delete(Book book) {
		this.bookRepo.delete(book);
	}
	
	public Book getBook(Long id) {
		if(id==null)
			return null;
		return bookRepo.findById(id).get();
	}
	
	public boolean existsBook(Book book) {
		return bookRepo.existsByTitleAndYear(book.getTitle(), book.getYear());
	}
	
	public void save(Book book) {
		this.bookRepo.save(book);
	}
	
	@Transactional
	public void addBook(Book book, List<Author> authors) {
		
		if(book == null || authors.isEmpty())
			throw new IllegalArgumentException("libro vuoto o senza autori");
		
		List<Author> existingAuthors = new ArrayList<>();
	    for (Author author : authors) {
	        Author dbAuthor = authorService.getAuthorByNameAndSurnameAndDate(author);
	        existingAuthors.add(dbAuthor);
	    }
	    
	    book.setAuthors(existingAuthors);
	    
	    // IMPORTANTE: Salva prima il book (che include già le immagini)
	    this.save(book);
	    
	    // Poi aggiorna gli autori con la relazione
	    for (Author author : existingAuthors) {
	        author.getBooks().add(book);
	        authorService.save(author);
	    }
	}
	
	
	
	public void deleteBook(Long id) {
		
		if(id==null || this.getBook(id)==null)
			throw new IllegalArgumentException("id nullo oppure nessun libro nel sistema che ha quell id");
		
		Book book = this.getBook(id);
		for(Author author : book.getAuthors() ) {
			author.getBooks().remove(book);
			this.authorService.save(author);
		}
		
		this.delete(book);
	}
}
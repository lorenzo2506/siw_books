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
	
	public List<Book> getAllBooks() {
		return bookRepo.findAll();
	}
	
	
	public Book getBook(Long id) {
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
	    this.save(book);
	    
	    for (Author author : existingAuthors)
	        author.getBooks().add(book);	    
		
	    
	}
	
	
}

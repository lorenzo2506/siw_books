package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repository.AuthorRepository;
import it.uniroma3.siw.repository.BookRepository;
import jakarta.transaction.Transactional;

@Service
public class AuthorService {

	
	@Autowired private AuthorRepository authorRepo;
	@Autowired private BookRepository bookRepo;
	
	public List<Author> getAllAuthor() {
		return authorRepo.findAll();
	}
	
	
	public Author getAuthor(Long id) {
		return authorRepo.findById(id).get();
	}
	
	public void save(Author a) {
		this.authorRepo.save(a);
	}
	
	
	public void saveAll(List<Author> authors) {
		this.authorRepo.saveAll(authors);
	}
	
	
	public boolean existsAuthor(Author a) {
		if(a.getName()==null || a.getSurname()==null)
			return false;
		return this.authorRepo.existsByNameIgnoreCaseAndSurnameIgnoreCaseAndDateOfBirth(a.getName(), a.getSurname(), a.getDateOfBirth());
	}
	
	public Author getAuthorByNameAndSurnameAndDate(Author a) {
		if(a.getName()==null || a.getSurname()==null)
			throw new IllegalArgumentException("campi nome e cognome vuoti");
		return this.authorRepo.findByNameIgnoreCaseAndSurnameIgnoreCaseAndDateOfBirth(a.getName(), a.getSurname(), a.getDateOfBirth());
	
	}
	
	
	public void addAuthor(Author author) {
		this.save(author);
		
	}
	
	
	public void addBookToAuthorList(Book book) {
		
		List<Author> authors = book.getAuthors();
		
		for(Author author: authors) {
			author.getBooks().add(book);
			this.save(author);
		}
	}
	
	public void delete(Author a) {
		this.authorRepo.delete(a);
	}
	
	@Transactional
	public void removeAuthor(Long id) {
		
		if(id==null)
			throw new IllegalArgumentException("id nullo");
		
		Author author = this.getAuthor(id);
		if(author==null)
			throw new IllegalArgumentException("id non corrisponde a nessu autore nel sistema");
		
		
		for(Book book: author.getBooks()) {
			book.getAuthors().remove(author);
			
			if(book.getAuthors().size()==0)
				this.bookRepo.delete(book);
			else
				this.bookRepo.save(book);
		}
		
		this.delete(author);
		
		
	}
}

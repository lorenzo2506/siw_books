package it.uniroma3.siw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Author;
import it.uniroma3.siw.model.Book;
import it.uniroma3.siw.repository.AuthorRepository;

@Service
public class AuthorService {

	
	@Autowired private AuthorRepository authorRepo;
	
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
}

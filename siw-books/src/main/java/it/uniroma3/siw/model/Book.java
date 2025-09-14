package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Book {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	
	@ManyToMany()
	@JoinTable(
	        name = "author_book",
	        joinColumns = @JoinColumn(name = "book_id"),
	        inverseJoinColumns = @JoinColumn(name = "author_id"))
	private List<Author> authors;
	
	
	@NotBlank
	private String title;
	
	@NotNull
	private Integer year;
	
	private List<String> images;
	
	
	@Override
	public boolean equals(Object o) {
		
		if(o==null || o.getClass()!=this.getClass())
			return false;
		if(this==o)
			return true;
		
		Book book = (Book) o;
		
		return (this.getTitle() == book.getTitle()) && (this.getYear() == book.getYear());
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(title, year);
	}
}

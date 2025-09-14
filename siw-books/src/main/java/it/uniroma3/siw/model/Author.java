package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Author {

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToMany(mappedBy="authors")
	private List<Book> books;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String surname;
	
	@NotNull
	private LocalDate dateOfBirth;
	
	private LocalDate dateOfDeath;
	
	private String image;
	
	
	@Override
	public boolean equals(Object o) {
		
		if(o==null || o.getClass()!=this.getClass())
			return false;
		if(o==this)
			return true;
		
		Author author = (Author) o;
		return this.getName().equalsIgnoreCase(author.getName()) && this.getSurname().equalsIgnoreCase(author.getSurname()) 
				&& this.getDateOfBirth().equals(author.getDateOfBirth());
	}
	
	@Override
	public int hashCode() {
		return this.getName().hashCode() + this.getSurname().hashCode() + this.getDateOfBirth().hashCode();
	}
}

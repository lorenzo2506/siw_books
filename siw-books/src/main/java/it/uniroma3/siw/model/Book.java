package it.uniroma3.siw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
	
	// MODIFICA: Multiple immagini per il libro (massimo 5)
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "book_images", joinColumns = @JoinColumn(name = "book_id"))
	@Column(name = "image_path")
	private List<String> images = new ArrayList<>();
	
	public Book() {
		this.images = new ArrayList<>();
	}
	
	// Metodi di utilità per le immagini
	public void addImage(String imagePath) {
		if (this.images == null) {
			this.images = new ArrayList<>();
		}
		if (this.images.size() < 5) { // Massimo 5 immagini
			this.images.add(imagePath);
		}
	}
	
	public void removeImage(String imagePath) {
		if (this.images != null) {
			this.images.remove(imagePath);
		}
	}
	
	public String getMainImage() {
		return (images != null && !images.isEmpty()) ? images.get(0) : null;
	}
	
	public int getImageCount() {
		return (images != null) ? images.size() : 0;
	}
	
	public boolean hasImages() {
		return images != null && !images.isEmpty();
	}
	
	// DEBUG: Aggiungi questo metodo per controllare
	public void printImages() {
		System.out.println("Immagini nel book:");
		if (images != null) {
			for (int i = 0; i < images.size(); i++) {
				System.out.println(i + ": " + images.get(i));
			}
		} else {
			System.out.println("Lista immagini è null");
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if(o==null || o.getClass()!=this.getClass())
			return false;
		if(this==o)
			return true;
		
		Book book = (Book) o;
		return Objects.equals(this.getTitle(), book.getTitle()) && 
		       Objects.equals(this.getYear(), book.getYear());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(title, year);
	}
}
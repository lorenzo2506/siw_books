package it.uniroma3.siw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import it.uniroma3.siw.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{

	public boolean existsByTitleAndYear(String title, Integer year);
}

package it.uniroma3.siw.repository;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.uniroma3.siw.model.Author;


@Repository
public interface AuthorRepository extends JpaRepository<Author, Long>{

	@Query("SELECT CASE WHEN COUNT(a)>0 THEN true ELSE false END FROM Author a WHERE LOWER(a.name)= LOWER(:name)"
			+ " AND LOWER(a.surname) = LOWER(:surname) AND a.dateOfBirth = :date")
	public boolean existsByNameAndSurnameAndDate(@Param("name") String name,  @Param("surname") String surname, @Param("date") LocalDate dateOfBirth);
	
	
	public Author findByNameIgnoreCaseAndSurnameIgnoreCaseAndDateOfBirth(String name, String surname, LocalDate dateOfBirth);
	
	public Author findByNameIgnoreCaseAndSurnameIgnoreCase(String name, String surname);
	
	public boolean existsByNameIgnoreCaseAndSurnameIgnoreCase(String name, String surname);
	
	public boolean existsByNameIgnoreCaseAndSurnameIgnoreCaseAndDateOfBirth(String name, String surname, LocalDate dateOfBirth);

}

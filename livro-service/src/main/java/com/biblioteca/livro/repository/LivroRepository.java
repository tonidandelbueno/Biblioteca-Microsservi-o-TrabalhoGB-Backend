package com.biblioteca.livro.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.livro.model.Livro;

	@Repository
	public interface LivroRepository extends JpaRepository<Livro, Long> {
	
	    List<Livro> findByTituloContainingIgnoreCase(String titulo);
	// validacao adicional para garantir reserva unica
	    @Modifying
	    @Transactional
	    @Query("UPDATE Livro l SET l.disponivel = false, l.chaveReserva = :chave " +
	           "WHERE l.id = :id AND l.disponivel = true AND l.chaveReserva IS NULL")
	    int reservarSeDisponivel(
	            @Param("id") Long id,
	            @Param("chave") UUID chave);
	
	    @Modifying
	    @Transactional
	    @Query("UPDATE Livro l SET l.disponivel = true, l.chaveReserva = NULL " +
	           "WHERE l.id = :id AND l.disponivel = false AND l.chaveReserva = :chave")
	    int liberarSeReservado(
	            @Param("id") Long id,
	            @Param("chave") UUID chave);

		@Modifying
		@Transactional
		@Query("UPDATE Livro l SET l.titulo = :titulo, " +
		       "l.autor = :autor, l.isbn = :isbn " +
		       "WHERE l.id = :id")
		int atualizarDados(
		        @Param("id") Long id,
		        @Param("titulo") String titulo,
		        @Param("autor") String autor,
		        @Param("isbn") String isbn);
		//validação adicional para o delete
		@Modifying
		@Transactional
		@Query("DELETE FROM Livro l " +
		       "WHERE l.id = :id AND l.disponivel = true " +
		       "AND l.chaveReserva IS NULL")
		int excluirSeDisponivel(@Param("id") Long id);

	}

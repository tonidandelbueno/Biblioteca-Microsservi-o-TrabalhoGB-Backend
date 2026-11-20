
package com.biblioteca.emprestimo.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.biblioteca.emprestimo.model.Emprestimo;

@Repository
public interface EmprestimoRepository
        extends JpaRepository<Emprestimo, Long> {

    List<Emprestimo> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);

//agora com lock para bloquear multiplos acessos
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Emprestimo e WHERE e.id = :id")
    Optional<Emprestimo> buscarParaDevolucao(
            @Param("id") Long id);
}

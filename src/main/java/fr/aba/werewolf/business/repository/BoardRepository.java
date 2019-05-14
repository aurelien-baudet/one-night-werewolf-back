package fr.aba.werewolf.business.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.aba.werewolf.business.domain.state.Board;

public interface BoardRepository extends MongoRepository<Board, String> {

}

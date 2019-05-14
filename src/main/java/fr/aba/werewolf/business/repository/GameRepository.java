package fr.aba.werewolf.business.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.aba.werewolf.business.domain.Game;
import fr.aba.werewolf.business.repository.custom.CustomGameRepository;

public interface GameRepository extends MongoRepository<Game, String>, CustomGameRepository {
}

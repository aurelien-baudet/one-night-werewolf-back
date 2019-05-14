package fr.aba.werewolf.business.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.aba.werewolf.business.domain.Player;

public interface PlayerRepository extends MongoRepository<Player, String> {

}

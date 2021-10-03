package backend.claptrap.db.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import backend.claptrap.db.entity.Customer;
import reactor.core.publisher.Flux;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {

  @Query("SELECT * FROM customer WHERE last_name = :lastname")
  Flux<Customer> findByLastName(String lastName);

}

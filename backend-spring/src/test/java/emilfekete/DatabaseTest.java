package emilfekete;

import backend.claptrap.db.entity.Customer;
import backend.claptrap.db.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataR2dbcTest
public class DatabaseTest {

  @Autowired
  CustomerRepository customerRepo;

  @Test
  public void getCustomerById() {
    Customer testCustomer = new Customer(0L, "Patrik", "Aradi");
    Customer customer = customerRepo
      .findByLastName("Aradi").take(1).blockFirst();

    assertEquals(testCustomer, customer);
  }
}

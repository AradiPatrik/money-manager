package backend.claptrap.web.routes;

import com.claptrap.api.DummyApi;
import com.claptrap.api.TokenSignInApi;
import com.claptrap.model.DummyWire;
import backend.claptrap.db.entity.Customer;
import backend.claptrap.db.repository.CustomerRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
class Controller implements TokenSignInApi, DummyApi {

  @Autowired
  private CustomerRepository customerRepository;

  @Override
  public Flux<DummyWire> dummyGet(Mono<Jwt> jwt) {
    return null;
  }

  @Override
  public Mono<DummyWire> tokenSignIn(Mono<Jwt> jwt) {
    return null;
  }
}

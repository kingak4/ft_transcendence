package code.shared;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class HelloRestController {

  @GetMapping("user")
  @Operation(summary = "A test controller, remove in the future") // TODO
  public String helloUser() {
    return "Hello User";
  }
}

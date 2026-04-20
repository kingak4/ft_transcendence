package code.entrypoints.api;

<<<<<<< HEAD
import io.swagger.v3.oas.annotations.Operation;
=======
>>>>>>> main
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("hello")
public class HelloRestController {

<<<<<<< HEAD
  @GetMapping("user")
  @Operation(summary = "A test controller, remove in the future") // TODO
  public String helloUser() {
    return "Hello User";
  }
}
=======
    @GetMapping("user")
    public String helloUser() {
        return "Hello User";
    }
}
>>>>>>> main

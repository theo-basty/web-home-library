package space.basty.webhomelibrary;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import space.basty.webhomelibrary.service.MessageBagService;

@Configuration
public class ApplicationConfiguration extends WebMvcAutoConfiguration {

}


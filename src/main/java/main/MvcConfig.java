package main;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("/resources/", "classpath:/static/", "classpath:/templates/");

        File upload = new File("upload");
        File avatars = new File("avatars");

        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///" + upload.getAbsolutePath() + "/");
        registry.addResourceHandler("/post/upload/**")
                .addResourceLocations("file:///" + upload.getAbsolutePath() + "/");

        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:///" + avatars.getAbsolutePath() + "/");
        registry.addResourceHandler("/posts/avatars/**")
                .addResourceLocations("file:///" + avatars.getAbsolutePath() + "/");
        registry.addResourceHandler("/post/avatars/**")
                .addResourceLocations("file:///" + avatars.getAbsolutePath() + "/");
    }
}

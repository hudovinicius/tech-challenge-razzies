package br.com.razzie.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Boolean importOnStartup;
    private String filePath;
    private String encoString;
    private String columnDelimiter;
    private String elementDelimiter;
    private String regexElementDelimiter;
    private Boolean skipHeader;
    private String winnerValue;
}

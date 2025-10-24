package de.cookindustries.lib.spring.gui.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CiLibInputExtractionExceptionHandler.class)
public class CiLibExceptionConfig
{

}

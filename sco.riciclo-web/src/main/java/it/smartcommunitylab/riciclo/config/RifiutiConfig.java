/**
 *    Copyright 2015 Fondazione Bruno Kessler - Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package it.smartcommunitylab.riciclo.config;

import it.smartcommunitylab.riciclo.app.importer.converter.RifiutiConverter;
import it.smartcommunitylab.riciclo.app.importer.converter.RifiutiValidator;
import it.smartcommunitylab.riciclo.storage.NotificationManager;
import it.smartcommunitylab.riciclo.storage.RepositoryManager;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;

@Configuration
@ComponentScan("it.smartcommunitylab.riciclo")
@PropertySource("classpath:rifiuti.properties")
@EnableWebMvc
public class RifiutiConfig extends WebMvcConfigurerAdapter {

	@Autowired
	@Value("${db.draft}")
	private String draftDB;

	@Autowired
	@Value("${db.final}")
	private String finalDB;
	
	@Autowired
	@Value("${defaultLang}")
	private String defaultLang;

	public RifiutiConfig() {
	}

	@Bean(name = "draftDB")
	public MongoTemplate getDraftMongo() throws UnknownHostException, MongoException {
		return new MongoTemplate(new MongoClient(), draftDB);
	}

	@Bean(name = "finalDB")
	public MongoTemplate getFinalMongo() throws UnknownHostException, MongoException {
		return new MongoTemplate(new MongoClient(), finalDB);
	}

	@Bean
	RepositoryManager getRepositoryManager() throws UnknownHostException, MongoException {
		return new RepositoryManager(getDraftMongo(), getFinalMongo(), defaultLang);
	}
	
	@Bean NotificationManager getNotificationManager() throws UnknownHostException, MongoException {
		return new NotificationManager(getFinalMongo());
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	RifiutiConverter getRifiutiConverter() {
		return new RifiutiConverter(defaultLang);
	}
	
	@Bean
	RifiutiValidator getRifiutiValidator() {
		return new RifiutiValidator();
	}	

	@Bean
	public ViewResolver getViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/resources/");
		resolver.setSuffix(".html");
		return resolver;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/apps/**").addResourceLocations(
				"/apps/");
		registry.addResourceHandler("/resources/*").addResourceLocations(
				"/resources/");
		registry.addResourceHandler("/css/**").addResourceLocations(
				"/resources/css/");
		registry.addResourceHandler("/fonts/**").addResourceLocations(
				"/resources/fonts/");
		registry.addResourceHandler("/js/**").addResourceLocations(
				"/resources/js/");
		registry.addResourceHandler("/lib/**").addResourceLocations(
				"/resources/lib/");
		registry.addResourceHandler("/templates/**").addResourceLocations(
				"/resources/templates/");
		registry.addResourceHandler("/html/**").addResourceLocations(
				"/resources/html/");
		registry.addResourceHandler("/file/**").addResourceLocations(
				"/resources/file/");
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
	}
}

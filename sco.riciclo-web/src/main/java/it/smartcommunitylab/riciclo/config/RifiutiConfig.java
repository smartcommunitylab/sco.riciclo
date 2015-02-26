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

import it.smartcommunitylab.riciclo.app.giudicarie.converter.GiudicarieRifiutiConverter;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;

@Configuration
@ComponentScan("it.smartcommunitylab.riciclo")
@PropertySource("classpath:rifiuti.properties")
@EnableWebMvc
public class RifiutiConfig {

	@Autowired
	@Value("${db.draft}")
	private String draftDB;

	@Autowired
	@Value("${db.final}")
	private String finalDB;

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
		return new RepositoryManager(getDraftMongo(), getFinalMongo());
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	GiudicarieRifiutiConverter getGiudicarieRifiutiConverter() {
		return new GiudicarieRifiutiConverter();
	}

}

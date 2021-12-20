package com.jbisa.dopereads.dataloader;

import com.jbisa.dopereads.database.connection.DataStaxAstraProperties;
import com.jbisa.dopereads.dataloader.entities.author.Author;
import com.jbisa.dopereads.dataloader.entities.author.AuthorRepository;
import com.jbisa.dopereads.dataloader.entities.book.Book;
import com.jbisa.dopereads.dataloader.entities.book.BookRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class DataLoaderApplication {

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookRepository bookRepository;

	@Value("${datadump.location.authors}")
	private String authorsDataLocation;

	@Value("${datadump.location.works}")
	private String worksDataLocation;

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	public static void main(String[] args) {
		SpringApplication.run(DataLoaderApplication.class, args);
	}

	@PostConstruct
	public void start() {
		prepareAuthors();
		prepareBooks();
		System.out.println("All done!");
	}

	private void prepareAuthors() {
		System.out.println("Preparing authors data...");

		var path = Paths.get(authorsDataLocation);

		try (var lines = Files.lines(path)) {
			lines.forEach(line -> {
				// Read and parse the line
				var jsonString = line.substring(line.indexOf("{"));

				try {
					// Construct the author object
					var jsonObject = new JSONObject(jsonString);
					var author = new Author(
							jsonObject.optString("key").replace("/authors/", ""),
							jsonObject.optString("name"),
							jsonObject.optString("personal_name"));

					// Persist using repository
					System.out.println("Saving author: " + author.getName() + "...");
					authorRepository.save(author);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Done with authors data!");
	}

	private void prepareBooks() {
		System.out.println("Preparing books data...");

		var path = Paths.get(worksDataLocation);

		try (var lines = Files.lines(path)) {
			lines.forEach(line -> {
				// Read and parse the line
				var jsonString = line.substring(line.indexOf("{"));

				try {
					// Construct the author object
					var jsonObject = new JSONObject(jsonString);

					var descriptionObj = jsonObject.optJSONObject("description");
					var createdObj = jsonObject.optJSONObject("created");

					var coversArray = jsonObject.optJSONArray("covers");
					var covers = new ArrayList<String>();

					if (coversArray != null) {
						for (int i = 0; i < coversArray.length(); i++) {
							covers.add(coversArray.getString(i));
						}
					}

					var book = new Book(
							jsonObject.optString("key").replace("/works/", ""),
							jsonObject.optString("title"),
							descriptionObj != null ? descriptionObj.optString("value") : "",
							LocalDate.parse(createdObj != null ? createdObj.optString("value").substring(0, 10) : ""),
							covers
					);

					var authorsArray = jsonObject.optJSONArray("authors");

					if (authorsArray != null) {
						var authorIds = new ArrayList<String>();

						// Set up author IDs
						for (int i = 0; i < authorsArray.length(); i++) {
							authorIds.add(
									authorsArray.getJSONObject(i)
											.getJSONObject("author")
											.optString("key")
											.replace("/authors/", ""));
						}

						book.setAuthorIds(authorIds);

						// Set up author names from Cassandra
						var authorNames = authorIds.stream().map(id -> authorRepository.findById(id))
								.map(author -> author.isPresent() ? author.get().getName() : "")
								.collect(Collectors.toList());

						book.setAuthorNames(authorNames);
					}

					// Persist using repository
					System.out.println("Saving book: " + book.getTitle() + "...");
					bookRepository.save(book);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Done with books data!");
	}
}

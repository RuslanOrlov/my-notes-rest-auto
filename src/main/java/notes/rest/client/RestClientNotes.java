package notes.rest.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//import lombok.extern.slf4j.Slf4j;
import notes.models.Note;

//@Slf4j
@Component
public class RestClientNotes {
	
	private RestTemplate restTemplate;
	private String urlWithoutId;
	private String urlWithId;
	private String urlCount;
	private String urlCountWithQuery;
	private String urlQuery;
	private String urlPagingQuery;
	
	public RestClientNotes() {
		
		/*
		 * Из официальной документации на RestTemplate: 
		 * Note that the standard JDK HTTP library does not support 
		 * the HTTP PATCH method.Configure the Apache HttpComponents 
		 * or OkHttp request factory to enable PATCH.
		 * 
		 * Так как стандартный RestTemplate не поддерживает метод 
		 * PATCH напрямую, то для обхода этой проблемы требуется 
		 * добавить поддержку метода PATCH через библиотеку Apache 
		 * HttpComponents. Для этого в pom.xml была добавлена зави-
		 * симость "httpclient5" библиотеки "org.apache.httpcomponents.client5", 
		 * а здесь в RestTemplate выполнена настройка его RequestFactory с 
		 * использованием HttpComponentsClientHttpRequestFactory, которому 
		 * передан подходящий HttpClient. 
		 * 
		 * */
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpComponentsClientHttpRequestFactory requestFactory = 
				new HttpComponentsClientHttpRequestFactory(httpClient);
		this.restTemplate 		= new RestTemplate(requestFactory);
		
		this.urlWithoutId 		= "http://localhost:8080/notes";
		this.urlWithId 			= "http://localhost:8080/notes/{id}";
		this.urlCount 			= "http://localhost:8080/notes/search/countAll";
		this.urlCountWithQuery 	= "http://localhost:8080/notes/search/countAllWithQuery"
										+ "?value={value}";
		this.urlQuery 			= "http://localhost:8080/notes/search/readNotesWithQuery"
										+ "?value={value}";
		this.urlPagingQuery 	= "http://localhost:8080/notes/search/readNotesWithPagingQuery"
										+ "?value={value}&offset={offset}&limit={limit}";
	}
	
	public List<Note> getAllNotes() {
		List<Note> notes = new ArrayList<>();
		
		ResponseEntity<String> responseEntity = 
				this.restTemplate.getForEntity(this.urlWithoutId + "?sort=id", String.class);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			
			/*
			 * При десериализации данных возникает ошибка: 
			 * - "com.fasterxml.jackson.databind.exc.InvalidDefinitionException: 
			 *   Java 8 date/time type java.time.LocalDateTime not supported by default: 
			 *   add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" 
			 *   to enable handling". 
			 * Для устранения этой ошибки требуется следующее: 
			 * - В приложение необходимо добавить модуль "jackson-datatype-jsr310", 
			 *   требуемый для обработки типов даты/времени Java 8 в Jackson, который 
			 *   используется для сериализации и десериализации данных JSON. Однако в 
			 *   Spring Boot через стартер "spring-boot-starter-web" по умолчанию уже 
			 *   добавлен модуль "jackson-datatype-jsr310", то есть при наличии этого 
			 *   стартера этот шаг настройки ВЫПОЛНЯТЬ НЕ НУЖНО. 
			 * - Следующим шагом необходимо выполнить ниже указанную конфигурацию 
			 *   для ObjectMapper, который используется для сериализации/десериализации 
			 *   данных JSON: 
			 *     ------------------------------------------------------------
			 *     import com.fasterxml.jackson.databind.ObjectMapper;
			 *     import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
			 *   
			 *     ObjectMapper objectMapper = new ObjectMapper();
			 *     objectMapper.registerModule(new JavaTimeModule());
			 *     ------------------------------------------------------------
			 * */
			
			String responseBody = responseEntity.getBody();
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			
			try {
				notes = objectMapper.readValue(responseBody, NotesResponse.class).getNotes();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return notes;
	}
	
	public List<Note> getAllNotes(Integer curPage, Integer pageSize) {
		List<Note> notes = new ArrayList<>();
		
		String targetedUrl = this.urlWithoutId 
									+ "?page=" + curPage + "&size=" + pageSize + "&sort=id";
		ResponseEntity<String> responseEntity = 
				this.restTemplate.getForEntity(targetedUrl, String.class);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			String responseBody = responseEntity.getBody();
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			
			try {
				notes = objectMapper.readValue(responseBody, NotesResponse.class).getNotes();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return notes;
	}
	
	public List<Note> getAllNotes(String value) {
		List<Note> notes = new ArrayList<>();
		
		ResponseEntity<String> responseEntity = 
				this.restTemplate.getForEntity(this.urlQuery, String.class, value);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			String responseBody = responseEntity.getBody();
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			
			try {
				notes = objectMapper.readValue(responseBody, NotesResponse.class).getNotes();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return notes;
	}
	
	public List<Note> getAllNotes(Integer curPage, Integer pageSize, String value) {
		List<Note> notes = new ArrayList<>();
		
		ResponseEntity<String> responseEntity = 
				this.restTemplate.getForEntity(this.urlPagingQuery, String.class, value, 
												curPage*pageSize, pageSize);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			String responseBody = responseEntity.getBody();
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.registerModule(new JavaTimeModule());
			
			try {
				notes = objectMapper.readValue(responseBody, NotesResponse.class).getNotes();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return notes;
	}
	
	public Integer countAll(Boolean isFiltering, String value) {
		ResponseEntity<Integer> responseEntity = 
				ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		
		if (isFiltering)
			responseEntity = this.restTemplate.getForEntity(
									this.urlCountWithQuery, Integer.class, value);
		else
			responseEntity = this.restTemplate.getForEntity(
									this.urlCount, Integer.class);
		
		if (responseEntity.getStatusCode().is2xxSuccessful())
			return responseEntity.getBody();
		return 0;
	}
	
	public Note getNoteById(Long id) {
		ResponseEntity<Note> responseEntity = 
				this.restTemplate.getForEntity(this.urlWithId, Note.class, id);
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			return responseEntity.getBody();
		}
		return null;
	}
	
	public Note postNote(Note note) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Note> requestEntity = new HttpEntity<Note>(note, httpHeaders);
		
		ResponseEntity<Note> responseEntity = 
				this.restTemplate.postForEntity(this.urlWithoutId, requestEntity, Note.class);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			return responseEntity.getBody();
		}
		return null;
	}
	
	public Note putNote(Map<String, Object> note, Long id) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Map<String, Object>> requestEntity = 
				new HttpEntity<Map<String,Object>>(note, httpHeaders);
		
		ResponseEntity<Note> responseEntity = 
				this.restTemplate.exchange(this.urlWithId, HttpMethod.PUT, 
											requestEntity, Note.class, id);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			return responseEntity.getBody();
		}
		return null;
	}
	
	public Note patchNote(Map<String, Object> note, Long id) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<Map<String, Object>> requestEntity = 
				new HttpEntity<Map<String,Object>>(note, httpHeaders);
		
		ResponseEntity<Note> responseEntity = 
				this.restTemplate.exchange(this.urlWithId, HttpMethod.PATCH, 
						requestEntity, Note.class, id);
		
		if (responseEntity.getStatusCode().is2xxSuccessful()) {
			return responseEntity.getBody();
		}
		return null;
	}
	
	public void deleteNote(Long id) {
		this.restTemplate.delete(this.urlWithId, id);
	}
}

package br.com.razzie.controller;

import br.com.razzie.dtos.WinRangeResponseDTO;
import br.com.razzie.services.ProcessFileService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProducerControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ProcessFileService processFileService;

    private String endpoint() {
        return "http://localhost:" + port + "/v1/producers/intervals";
    }

    void clearDatabaseAndCache() {
        jdbcTemplate.execute("DELETE FROM movie_producer");
        jdbcTemplate.execute("DELETE FROM movie_studio");
        jdbcTemplate.execute("DELETE FROM movie");
        jdbcTemplate.execute("DELETE FROM producer");
        jdbcTemplate.execute("DELETE FROM studio");

        Objects.requireNonNull(cacheManager.getCache("winners-rangers")).clear();
    }

    @Test
    @Order(1)
    public void testFindConsecutiveWinners_ShouldProcessDefaultFileOnStartup() {

        var response = restTemplate.getForEntity(endpoint(), WinRangeResponseDTO.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

        Assertions.assertEquals(1, response.getBody().min().size());
        Assertions.assertEquals("Joel Silver", response.getBody().min().getFirst().producer());
        Assertions.assertEquals(1, response.getBody().min().getFirst().interval());
        Assertions.assertEquals(1990, response.getBody().min().getFirst().previousWin());
        Assertions.assertEquals(1991, response.getBody().min().getFirst().followingWin());

        Assertions.assertEquals(1, response.getBody().max().size());
        Assertions.assertEquals("Matthew Vaughn", response.getBody().max().getFirst().producer());
        Assertions.assertEquals(13, response.getBody().max().getFirst().interval());
        Assertions.assertEquals(2002, response.getBody().max().getFirst().previousWin());
        Assertions.assertEquals(2015, response.getBody().max().getFirst().followingWin());
    }

    @Test
    @Order(2)
    public void testFindConsecutiveWinners_EmptyData_ShouldReturnEmptyLists() {

        clearDatabaseAndCache();

        var response = restTemplate.getForEntity(endpoint(), WinRangeResponseDTO.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(0, response.getBody().min().size());
        Assertions.assertEquals(0, response.getBody().max().size());
    }

    @Test
    @Order(3)
    public void testFindConsecutiveWinners_Case1_ShouldReturnSameProducerInMinAndMaxIntervals() throws Exception {

        clearDatabaseAndCache();

        processFileService.processFile("classpath:csv/Case1.csv");

        var response = restTemplate.getForEntity(endpoint(), WinRangeResponseDTO.class);

        Assertions.assertEquals(1, response.getBody().min().size());
        Assertions.assertEquals("Producer A", response.getBody().min().getFirst().producer());
        Assertions.assertEquals(1, response.getBody().min().getFirst().interval());
        Assertions.assertEquals(2000, response.getBody().min().getFirst().previousWin());
        Assertions.assertEquals(2001, response.getBody().min().getFirst().followingWin());

        Assertions.assertEquals(1, response.getBody().max().size());
        Assertions.assertEquals("Producer A", response.getBody().max().getFirst().producer());
        Assertions.assertEquals(1, response.getBody().max().getFirst().interval());
        Assertions.assertEquals(2000, response.getBody().max().getFirst().previousWin());
        Assertions.assertEquals(2001, response.getBody().max().getFirst().followingWin());

    }

    @Test
    @Order(4)
    public void testFindConsecutiveWinners_Case2_ShouldReturnMultipleMinProducersWithTheSameInterval() throws Exception {

        clearDatabaseAndCache();

        processFileService.processFile("classpath:csv/Case2.csv");

        var response = restTemplate.getForEntity(endpoint(), WinRangeResponseDTO.class);

        Assertions.assertEquals(2, response.getBody().min().size());
        Assertions.assertEquals(1, response.getBody().max().size());
    }

    @Test
    @Order(5)
    public void testFindConsecutiveWinners_Case3_ShouldReturnMultipleMaxProducersWithTheSameInterval() throws Exception {

        clearDatabaseAndCache();

        processFileService.processFile("classpath:csv/Case3.csv");

        var response = restTemplate.getForEntity(endpoint(), WinRangeResponseDTO.class);

        Assertions.assertEquals(1, response.getBody().min().size());
        Assertions.assertEquals(2, response.getBody().max().size());
    }
}

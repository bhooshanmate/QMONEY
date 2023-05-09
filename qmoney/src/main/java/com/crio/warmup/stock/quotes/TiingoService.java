
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
    //  Implement getStockQuote method below that was also declared in the interface.

    // Note:
    // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
    // 2. Run the tests using command below and make sure it passes.
    //    ./gradlew test --tests TiingoServiceTest




  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
    if(from.compareTo(to) >= 0){
      throw new RuntimeException();
    }
    String url = buildUriForTiingo(symbol, from, to);
    String stocks = restTemplate.getForObject(url, String.class);
    ObjectMapper objectmapper = getObjectMapper();
    TiingoCandle[] stocksStartToEndDateArray = objectmapper.readValue(stocks,TiingoCandle[].class);
    if(stocksStartToEndDateArray == null){
      return new ArrayList<Candle>();
    }
    else{
      List<Candle> stock = Arrays.asList(stocksStartToEndDateArray);
      return stock;
    }
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }




  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR-- DONE
  //  Write a method to create appropriate url to call the Tiingo API.
  protected String buildUriForTiingo(String symbol, LocalDate startDate, LocalDate endDate) {
    String token = "27382580ac3f37ab8ab481d585dd6d765c5dd258";   
    String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
            + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
    String url = uriTemplate.replace("$APIKEY", token).replace("$SYMBOL", symbol).replace("$STARTDATE", startDate.toString()).replace("$ENDDATE", endDate.toString());
    return url;
  }

}

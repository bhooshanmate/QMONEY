
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.management.RuntimeErrorException;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private RestTemplate restTemplate;
  private StockQuotesService stockQuotesService;




  PortfolioManagerImpl(StockQuotesService stockQuotesService){
    this.stockQuotesService = stockQuotesService;
  }
  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        // if(from.compareTo(to) >= 0){
        //   throw new RuntimeException();
        // }
        // String url = buildUri(symbol, from, to);
        // TiingoCandle[] stocksStartToEndDate = restTemplate.getForObject(url,TiingoCandle[].class);
        // if(stocksStartToEndDate == null){
        //   return new ArrayList<Candle>();
        // }
        // else{
        //   List<Candle> stock = Arrays.asList(stocksStartToEndDate);
        //   return stock;
        // }
        return stockQuotesService.getStockQuote(symbol, from, to);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String token = "27382580ac3f37ab8ab481d585dd6d765c5dd258";   
    String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
            + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
    String url = uriTemplate.replace("$APIKEY", token).replace("$SYMBOL", symbol).replace("$STARTDATE", startDate.toString()).replace("$ENDDATE", endDate.toString());
    return url;
  }


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) {
    // TODO Auto-generated method stub
      AnnualizedReturn  annualizedReturn;
      List<AnnualizedReturn> annualizedReturns = new ArrayList<AnnualizedReturn>();
      for(int i=0;i< portfolioTrades.size();i++){
        annualizedReturn = getAnnualizedReturn(portfolioTrades.get(i),endDate);
        annualizedReturns.add(annualizedReturn);
      }
      Comparator<AnnualizedReturn> SortByAnnualReturn = Comparator.comparing(AnnualizedReturn :: getAnnualizedReturn).reversed();
      Collections.sort(annualizedReturns,SortByAnnualReturn);
      return annualizedReturns;
  }
  public AnnualizedReturn getAnnualizedReturn(PortfolioTrade trade,LocalDate endLocalDate){
    AnnualizedReturn annualizedReturn;
    String symbol = trade.getSymbol();
    LocalDate startLocalDate = trade.getPurchaseDate();
    try{
      List<Candle> stocksStartToEndDate;
      stocksStartToEndDate = getStockQuote(symbol, startLocalDate, endLocalDate);
      Candle stocksStartDate = stocksStartToEndDate.get(0);
      Candle stockLatest = stocksStartToEndDate.get(stocksStartToEndDate.size() - 1);
      Double buyPrice = stocksStartDate.getOpen();
      Double sellPrice = stockLatest.getClose();
      Double totalReturn = (sellPrice - buyPrice)/buyPrice;
      Double numYears = (double) ChronoUnit.DAYS.between(startLocalDate,endLocalDate)/365;
      Double annualizedReturns = Math.pow((1+totalReturn), (1/numYears))-1;
      annualizedReturn = new AnnualizedReturn(symbol, annualizedReturns, totalReturn);
    }catch(JsonProcessingException e){
      annualizedReturn = new AnnualizedReturn(symbol, Double.NaN, Double.NaN);
    }
    return annualizedReturn;
  }
}

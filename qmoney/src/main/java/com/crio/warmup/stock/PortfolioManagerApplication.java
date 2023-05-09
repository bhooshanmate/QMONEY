
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {
  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>

  private static PortfolioManager portfolioManager;


  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File file = resolveFileFromResources(args[0]);
    ObjectMapper om = getObjectMapper();
    PortfolioTrade[] trades = om.readValue(file,PortfolioTrade[].class);
    List<String> ans = new ArrayList<>();
    for(PortfolioTrade trade:trades){
      System.out.println(ans.add(trade.getSymbol()));
    }
    return ans;
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }
  public static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }
  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    // RestTemplate restTemplate = new RestTemplate();
    // String url = prepareUrl(trade,endDate,token);
	  // TiingoCandle[] tiingoCandleArray = getRestTemplate().getForObject(url,TiingoCandle[].class);
	  // return Arrays.asList(tiingoCandleArray);
    
    String localUrl = prepareUrl(trade, endDate, token);

    TiingoCandle[] tiingoCandles2 = getRestTemplate().getForObject(localUrl, TiingoCandle[].class);
    return Arrays.asList(tiingoCandles2);
  }

  private static RestTemplate getRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    return restTemplate;
  }
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {

    // step 1 - reading data from the JSON to portfolio objects
    List<PortfolioTrade> trades = readTradesFromJson(args[0]);    
    String token = "27382580ac3f37ab8ab481d585dd6d765c5dd258";
    // RestTemplate restTemplate = new RestTemplate();
    List<TotalReturnsDto> totalResturnsDtoList = new ArrayList<>();
    
    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-DD");
    LocalDate endDate = LocalDate.parse(args[1]);
    // Step 2
    for(PortfolioTrade trade:trades){
      // String url = prepareUrl(trade, endDate, token);
      // TiingoCandle[] responses = getRestTemplate.getForObject(url,TiingoCandle[].class);
      List<Candle> response = fetchCandles(trade, endDate, token);
      // for(TiingoCandle response : responses){
      //   TotalReturnsDto totalReturnsDtop = new TotalReturnsDto(trade.getSymbol(),response.getClose());
      //   totalResturnsDtoList.add(totalReturnsDtop);
      // }
      TotalReturnsDto trd = new TotalReturnsDto(trade.getSymbol(), response.get(response.size() - 1).getClose());
      totalResturnsDtoList.add(trd);
    }
    Collections.sort(totalResturnsDtoList,(arg0,arg1) -> (int)(arg0.getClosingPrice() - arg1.getClosingPrice()));
    List<String> result  =new ArrayList<>();
    totalResturnsDtoList.forEach(t -> result.add(t.getSymbol()));
    return result;
  }

  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    //  return Collections.emptyList();
    return Arrays.asList(getObjectMapper().readValue(resolveFileFromResources(filename),PortfolioTrade[].class));
  }


  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    String uri = "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol()+"/prices?startDate="+trade.getPurchaseDate().toString()+"&endDate="+endDate+"&token="+token;
    return uri;
  }

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/bhooshanmate-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@5542c4ed";
    String functionNameFromTestFileInStackTrace = "mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "29";


   return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
       toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
       lineNumberFromTestFileInStackTrace});
 }
 public static String getToken(){
  return "27382580ac3f37ab8ab481d585dd6d765c5dd258";
 }


  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.















  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    Double openingPrice = candles.get(0).getOpen();
    return openingPrice;
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    Double closingPrice = candles.get(candles.size()-1).getClose();
    return closingPrice;
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
    LocalDate endDate = LocalDate.parse(args[1]);
    List<AnnualizedReturn> annualizedReturns = new ArrayList<>();
    List<PortfolioTrade> trades = readTradesFromJson(args[0]);
    if(trades.isEmpty()) {
      return Collections.emptyList();
    }
    for(PortfolioTrade trade : trades) {
      List<Candle> candles = fetchCandles(trade,endDate,"27382580ac3f37ab8ab481d585dd6d765c5dd258");
      double buyPrice = getOpeningPriceOnStartDate(candles);
      double sellPrice = getClosingPriceOnEndDate(candles);

      AnnualizedReturn annRet = calculateAnnualizedReturns(endDate,trade,buyPrice,sellPrice);
      annualizedReturns.add(annRet);
    }
    // just for debugging
    // for(AnnualizedReturn i: annRetList){
    //   System.out.println(i.getTotalReturns());
    // }
    // Collections.sort(annRetList);
    // Collections.sort(annRetList, Collections.reverseOrder());
    return annualizedReturns.stream()
    .sorted((a1, a2) -> Double.compare(a2.getAnnualizedReturn(), a1.getAnnualizedReturn())) //descending order
    .collect(Collectors.toList());
    //  return Collections.emptyList();
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS -- DONE
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        double totalReturns = (sellPrice - buyPrice)/buyPrice;
        double total_num_years = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate)/365.24;
        double annualized_returns = Math.pow(1+totalReturns, (1/total_num_years)) - 1;
        return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturns);
  }




















  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  public static String readFileAsString(String filePath) throws java.io.IOException {
    StringBuffer fileData = new StringBuffer(1000);
    BufferedReader reader = new BufferedReader(new FileReader(filePath));
    char[] buf = new char[1024];
    int numRead = 0;
    while ((numRead = reader.read(buf)) != -1) {
        String readData = String.valueOf(buf, 0, numRead);
        fileData.append(readData);
        buf = new char[1024];
    }
    reader.close();
    return fileData.toString();
}
  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {;
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
       String contents = readFileAsString(file);
       ObjectMapper objectMapper = getObjectMapper();
       PortfolioTrade[] portfolioTrades = objectMapper.readValue(contents, PortfolioTrade[].class);
       
      return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

  }
}


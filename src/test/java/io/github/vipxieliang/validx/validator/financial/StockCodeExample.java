package io.github.vipxieliang.validx.validator.financial;

import io.github.vipxieliang.validx.annotations.StockCode;

public class StockCodeExample {
    
    // 默认支持所有交易所
    @StockCode
    private String stockCode1;
    
    // 只支持上海证券交易所
    @StockCode(exchanges = {StockCode.Exchange.SHANGHAI})
    private String stockCode2;
    
    // 支持上海证券交易所和纽约证券交易所
    @StockCode(exchanges = {StockCode.Exchange.SHANGHAI, StockCode.Exchange.NEW_YORK})
    private String stockCode3;
    
    // 只支持港股和美股
    @StockCode(exchanges = {StockCode.Exchange.HONG_KONG, StockCode.Exchange.NEW_YORK})
    private String stockCode4;
    
    // Getters and setters
    public String getStockCode1() {
        return stockCode1;
    }
    
    public void setStockCode1(String stockCode1) {
        this.stockCode1 = stockCode1;
    }
    
    public String getStockCode2() {
        return stockCode2;
    }
    
    public void setStockCode2(String stockCode2) {
        this.stockCode2 = stockCode2;
    }
    
    public String getStockCode3() {
        return stockCode3;
    }
    
    public void setStockCode3(String stockCode3) {
        this.stockCode3 = stockCode3;
    }
    
    public String getStockCode4() {
        return stockCode4;
    }
    
    public void setStockCode4(String stockCode4) {
        this.stockCode4 = stockCode4;
    }
}
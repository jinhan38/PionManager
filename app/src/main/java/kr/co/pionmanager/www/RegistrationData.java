package kr.co.pionmanager.www;

public class RegistrationData {
    private String Name;
    private String loanNum;
    private String status;
    private String auctionCompleteDate;
    private String auctionNum;
    private String color;
    private String hopeLoanPrice;
    private String maxPrice;
    private int auctionHouseCount;


    public RegistrationData(String loanNum,  String auctionNum, String status, String name, String auctionCompleteDate, String color
            , String hopeLoanPrice, String maxPrice, int auctionHouseCount) {
        this.loanNum = loanNum;
        this.auctionNum = auctionNum;
        this.status = status;
        this.Name = name;
        this.auctionCompleteDate = auctionCompleteDate;
        this.color = color;
        this.hopeLoanPrice = hopeLoanPrice;
        this.maxPrice = maxPrice;
        this.auctionHouseCount = auctionHouseCount;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLoanNum() {
        return loanNum;
    }

    public void setLoanNum(String loanNum) {
        this.loanNum = loanNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuctionCompleteDate() {
        return auctionCompleteDate;
    }

    public void setAuctionCompleteDate(String auctionCompleteDate) {
        this.auctionCompleteDate = auctionCompleteDate;
    }

    public String getAuctionNum() {
        return auctionNum;
    }

    public void setAuctionNum(String auctionNum) {
        this.auctionNum = auctionNum;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getHopeLoanPrice() {
        return hopeLoanPrice;
    }

    public void setHopeLoanPrice(String hopeLoanPrice) {
        this.hopeLoanPrice = hopeLoanPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getAuctionHouseCount() {
        return auctionHouseCount;
    }
}




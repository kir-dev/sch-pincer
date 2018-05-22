package hu.gerviba.webschop.dao;

public class OpeningEntityDao {
    
    private int timeIntervals = 4;
    private String dateStart;
    private String dateEnd;
    private String orderStart;
    private String orderEnd;
    private String feeling;
    private int maxOrder = 120;
    private int maxOrderPerHalfHour = 30;
    
    public OpeningEntityDao() {}

    public int getTimeIntervals() {
        return timeIntervals;
    }

    public void setTimeIntervals(int timeIntervals) {
        this.timeIntervals = timeIntervals;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public String getOrderStart() {
        return orderStart;
    }

    public void setOrderStart(String orderStart) {
        this.orderStart = orderStart;
    }

    public String getOrderEnd() {
        return orderEnd;
    }

    public void setOrderEnd(String orderEnd) {
        this.orderEnd = orderEnd;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public int getMaxOrder() {
        return maxOrder;
    }

    public void setMaxOrder(int maxOrder) {
        this.maxOrder = maxOrder;
    }

    public int getMaxOrderPerHalfHour() {
        return maxOrderPerHalfHour;
    }

    public void setMaxOrderPerHalfHour(int maxOrderPerHalfHour) {
        this.maxOrderPerHalfHour = maxOrderPerHalfHour;
    }
   
}

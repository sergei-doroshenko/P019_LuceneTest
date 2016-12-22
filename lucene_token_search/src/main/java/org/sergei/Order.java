package org.sergei;

/**
 * Created by Sergei_Doroshenko on 12/21/2016.
 */
public class Order {
    private Long orgerId;
    private String orderName;
    private String orderType;
    private String orderDesctiption;

    public Order () {
    }

    public Order ( Long orgerId, String orderName, String orderType, String orderDesctiption ) {
        this.orgerId = orgerId;
        this.orderName = orderName;
        this.orderType = orderType;
        this.orderDesctiption = orderDesctiption;
    }

    public Long getOrgerId () {
        return orgerId;
    }

    public void setOrgerId ( Long orgerId ) {
        this.orgerId = orgerId;
    }

    public String getOrderName () {
        return orderName;
    }

    public void setOrderName ( String orderName ) {
        this.orderName = orderName;
    }

    public String getOrderType () {
        return orderType;
    }

    public void setOrderType ( String orderType ) {
        this.orderType = orderType;
    }

    public String getOrderDesctiption () {
        return orderDesctiption;
    }

    public void setOrderDesctiption ( String orderDesctiption ) {
        this.orderDesctiption = orderDesctiption;
    }

    @Override
    public String toString () {
        return "Order{" +
                "orgerId=" + orgerId +
                ", orderName='" + orderName + '\'' +
                ", orderType='" + orderType + '\'' +
                ", orderDesctiption='" + orderDesctiption + '\'' +
                '}';
    }
}

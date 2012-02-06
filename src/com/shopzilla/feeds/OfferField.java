package com.shopzilla.feeds;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: shitalm
 * Date: 2/6/12
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */
public enum OfferField {
    Title("title", true, 1),
    Description("description", true, 2),
    URL("url", true, 3),
    Image60x60("image60x60", true, 4),
    SKU("sku", true, 5),
    Price("price", true, 6),
    ProductId("productId", 7),
    MerchantId("merchantId", 8),
    CategoryId("categoryId", 9),
    Id("id", 10),
    Manufacturer("manufacturer", 11),
    Image100x100("image100x100", 12),
    Image160x160("image160x160", 13),
    Image400x400("image400x400", 14),
    DetailURL("detailURL", 15),
    OriginalPrice("originalPrice", 16),
    MarkedDownPercent("markedDownPercent", 17),
    Bidded("bidded", 18),
    MerchantProductId("merchantProductId", 19),
    MerchantName("merchantName", 20),
    MerchantLogoURL("merchantLogoURL", 21),
    Condition("condition", 22),
    Stock("stock", 23),
    ShipAmount("shipAmount", 24),
    ShipType("shipType", 25),
    ShipWeight("shipWeight", 26);




    private String fieldName;
    private boolean mandatory;
    private int order;
    
    private static SortedSet<OfferField> fields = new TreeSet<OfferField>(new Comparator<OfferField>() {
        public int compare(OfferField offer1, OfferField offer2) {
            return offer1.order - offer2.order;
        }
    });

    /** Add all offers to a sorted set
     * Order in which they are inserted below is not important as order is determined by the order
     * field in the OfferField definition
     */
    static {

        fields.add(Title);
        fields.add(Description);
        fields.add(URL);
        fields.add(Image60x60);
        fields.add(SKU);
        fields.add(Price);
        fields.add(ProductId);
        fields.add(MerchantId);
        fields.add(CategoryId);
        fields.add(Id);
        fields.add(Manufacturer);
        fields.add(Image100x100);
        fields.add(Image160x160);
        fields.add(Image400x400);
        fields.add(DetailURL);
        fields.add(OriginalPrice);
        fields.add(MarkedDownPercent);
        fields.add(Bidded);
        fields.add(MerchantProductId);
        fields.add(MerchantName);
        fields.add(MerchantLogoURL);
        fields.add(Condition);
        fields.add(Stock);
        fields.add(ShipAmount);
        fields.add(ShipType);
        fields.add(ShipWeight);

    }

    public static SortedSet<OfferField> getfields() {
        return fields;
    }



    private OfferField(String fieldName, boolean mandatory, int order) {
        this.fieldName = fieldName;
        this.mandatory = mandatory;
        this.order = order;

    }

    private OfferField(String fieldName, int order) {
        this.fieldName = fieldName;
        this.mandatory = false;
        this.order = order;

    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(OfferField.getfields().toArray()));
    }

    public String getFieldName() {
        return fieldName;
    }

    public boolean isMandatory() {
        return mandatory;
    }


}

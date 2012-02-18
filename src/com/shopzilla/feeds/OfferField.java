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
    ProductId("productId", "id", true, 1),
    Title("title", "name", true, 2),
    Price("price", true, 3),
    Description("description", "desc_short", true, 4),
    URL("url", "URL", true, 5),
    Image60x60("image60x60", "imageURL_small", true, 6),
    SKU("sku", 7),
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




    private String nameInOffer, nameInProduct;
    private boolean mandatory;
    private int order;
    private static Set<String> mandatoryFieldNamesInOffer = new HashSet<String>();
    private static Set<String> mandatoryFieldNamesInProduct = new HashSet<String>();

    
    private static SortedSet<OfferField> fields = new TreeSet<OfferField>(new Comparator<OfferField>() {
        public int compare(OfferField offer1, OfferField offer2) {
            return offer1.order - offer2.order;
        }
    });

    private static SortedSet<OfferField> mandatoryFields = new TreeSet<OfferField>(new Comparator<OfferField>() {
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

        for(OfferField field : fields) {
            if(field.isMandatory()) {
                mandatoryFields.add(field);
                mandatoryFieldNamesInOffer.add(field.nameInOffer);
                mandatoryFieldNamesInProduct.add(field.nameInProduct);
            }

        }

    }

    public static SortedSet<OfferField> getfields() {
        return fields;
    }

    public static SortedSet<OfferField> getMandatoryFields() {
        return mandatoryFields;
    }


    private OfferField(String nameInOffer, String nameInProduct, boolean mandatory, int order) {
        this.nameInOffer = nameInOffer;
        this.nameInProduct = nameInProduct;
        this.mandatory = mandatory;
        this.order = order;

    }

    private OfferField(String fieldName, boolean mandatory, int order) {
        this(fieldName, fieldName, mandatory, order);
    }

    private OfferField(String fieldName, int order) {
        this(fieldName, false, order);
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(OfferField.getfields().toArray()));
    }

    public String getFieldName(ShopzillaFeedConvertor.OfferType offerType) {
        if(offerType == ShopzillaFeedConvertor.OfferType.OFFER) return nameInOffer;
        return nameInProduct;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public static boolean isMandatory(ShopzillaFeedConvertor.OfferType offerType, String name) {
        if(offerType == ShopzillaFeedConvertor.OfferType.PRODUCT) {
            return mandatoryFieldNamesInProduct.contains(name);
        } else {
            return mandatoryFieldNamesInOffer.contains(name);
        }
    }


}

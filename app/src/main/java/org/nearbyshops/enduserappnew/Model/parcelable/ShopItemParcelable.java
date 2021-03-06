package org.nearbyshops.enduserappnew.Model.parcelable;


import android.os.Parcel;
import android.os.Parcelable;

import org.nearbyshops.enduserappnew.Model.Item;
import org.nearbyshops.enduserappnew.Model.Shop;

import java.sql.Timestamp;

public class ShopItemParcelable implements Parcelable{
	
	public static final String UNIT_KG = "Kg.";
	public static final String UNIT_GRAMS = "Grams.";
	
	//int shopID;

	public ShopItemParcelable() {
		super();
		// TODO Auto-generated constructor stub
	}


	// Table Name
	public static final String TABLE_NAME = "SHOP_ITEM";

	// column Names
	public static final String SHOP_ID = "SHOP_ID";
	public static final String ITEM_ID = "ITEM_ID";
	public static final String AVAILABLE_ITEM_QUANTITY = "AVAILABLE_ITEM_QUANTITY";
	public static final String ITEM_PRICE = "ITEM_PRICE";

	//public static final String QUANTITY_UNIT = "QUANTITY_UNIT";
	//public static final String QUANTITY_MULTIPLE = "QUANTITY_MULTIPLE";

	public static final String MIN_QUANTITY_PER_ORDER = "MIN_QUANTITY_PER_ORDER";
	public static final String MAX_QUANTITY_PER_ORDER = "MAX_QUANTITY_PER_ORDER";

	public static final String DATE_TIME_ADDED = "DATE_TIME_ADDED";
	public static final String LAST_UPDATE_DATE_TIME = "LAST_UPDATE_DATE_TIME";
	public static final String EXTRA_DELIVERY_CHARGE = "EXTRA_DELIVERY_CHARGE";








	// holding shop and item reference for parsing JSON
	private Shop shop;
	//int itemID;
	private Item item;


	private int shopID;
	private int itemID;
	private int availableItemQuantity;
	private double itemPrice;

	
	// in certain cases the shop might take extra delivery charge for the particular item 
	// in most of the cases this charge would be zero, unless in some cases that item is so big that 
	// it requires special delivery. For example if you are buying some furniture. In that case the furniture
	
	
	// would require some special arrangement for delivery which might involve extra delivery cost
	//int extraDeliveryCharge = 0;
	
	// the minimum quantity that a end user - customer can buy 
	//int minQuantity;
	
	// the maximum quantity of this item that an end user can buy
	//int maxQuantity;




	// recently added variables
	private int extraDeliveryCharge;
	private Timestamp dateTimeAdded;
	private Timestamp lastUpdateDateTime;


	protected ShopItemParcelable(Parcel in) {
		shop = in.readParcelable(Shop.class.getClassLoader());
		item = in.readParcelable(Item.class.getClassLoader());
		shopID = in.readInt();
		itemID = in.readInt();
		availableItemQuantity = in.readInt();
		itemPrice = in.readDouble();
		extraDeliveryCharge = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeParcelable(shop, flags);
		dest.writeParcelable(item, flags);
		dest.writeInt(shopID);
		dest.writeInt(itemID);
		dest.writeInt(availableItemQuantity);
		dest.writeDouble(itemPrice);
		dest.writeInt(extraDeliveryCharge);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ShopItemParcelable> CREATOR = new Creator<ShopItemParcelable>() {
		@Override
		public ShopItemParcelable createFromParcel(Parcel in) {
			return new ShopItemParcelable(in);
		}

		@Override
		public ShopItemParcelable[] newArray(int size) {
			return new ShopItemParcelable[size];
		}
	};

	public int getExtraDeliveryCharge() {
		return extraDeliveryCharge;
	}

	public void setExtraDeliveryCharge(int extraDeliveryCharge) {
		this.extraDeliveryCharge = extraDeliveryCharge;
	}

	public Timestamp getDateTimeAdded() {
		return dateTimeAdded;
	}

	public void setDateTimeAdded(Timestamp dateTimeAdded) {
		this.dateTimeAdded = dateTimeAdded;
	}

	public Timestamp getLastUpdateDateTime() {
		return lastUpdateDateTime;
	}

	public void setLastUpdateDateTime(Timestamp lastUpdateDateTime) {
		this.lastUpdateDateTime = lastUpdateDateTime;
	}

	public double getItemPrice() {
		return itemPrice;
	}


	public void setItemPrice(double itemPrice) {
		this.itemPrice = itemPrice;
	}


	public int getShopID() {
		return shopID;
	}


	public void setShopID(int shopID) {
		this.shopID = shopID;
	}


	public int getItemID() {
		return itemID;
	}


	public void setItemID(int itemID) {
		this.itemID = itemID;
	}


	public int getAvailableItemQuantity() {
		return availableItemQuantity;
	}


	public void setAvailableItemQuantity(int availableItemQuantity) {
		this.availableItemQuantity = availableItemQuantity;
	}


	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
}
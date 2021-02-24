package com.gmail.berndivader.biene.enums;

public
enum
EventEnum
{
	HTTP_GET_VERSION("version"),
	HTTP_GET_PRODUCTS_PRINTOUT("products_export"),
	HTTP_GET_ORDERS_PRINTOUT("orders_export"),
	HTTP_GET_CUSTOMERS_PRINTOUT("customers"),
	
	HTTP_POST_VARIOUS(""),
	
	DB_GET_WINLINE("get_winline"),
	DB_VALIDATE_PICTURE("validate_picture"),
	
	DB_QUERY_OK("db_query_ok"),
	DB_QUERY_FAILED("db_query_failed"),
	
    UNKOWN("unkown");

	static final String get="&action=";
	private String command;
	
	private EventEnum(String command) {
		this.command=command;
	}
	
	public String command() {
		return get+command;
	}
}

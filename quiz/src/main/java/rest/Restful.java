//package rest;
//
//import java.util.List;
//
//public class Restful {
//	private String code;
//	private String type;
//	private String host;
//	private List<RestData> restDatas;
//	
//	
//
//	public Restful() {}
//
//	public Restful(String code, String type, String host) {
//		super();
//		this.code = code;
//		this.type = type;
//		this.host = host;
//	}
//	
//	public void dataAdd(String key,String value) {
//		restDatas.add(new RestData(key,value));
//	}
//	
//	@Override
//	public String toString() {
//		StringBuilder json = new StringBuilder();
//		
//		for(RestData rd : restDatas) {
//			
//		}
//	}
//	
//
//	public String getCode() {
//		return code;
//	}
//
//	public void setCode(String code) {
//		this.code = code;
//	}
//
//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}
//
//	public String getHost() {
//		return host;
//	}
//
//	public void setHost(String host) {
//		this.host = host;
//	}
//
//	
//	
//	
//	
//}
//class RestData {
//	String key;
//	String value;
//	public RestData(String key, String value) {
//		super();
//		this.key = key;
//		this.value = value;
//	}
//	
//}
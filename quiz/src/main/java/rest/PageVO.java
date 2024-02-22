package rest;

public class PageVO {
	private String title;
	private String url;
	
	public PageVO() {}
	
	
	
	public PageVO(String title, String url) {
		super();
		this.title = title;
		this.url = url;
	}



	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}

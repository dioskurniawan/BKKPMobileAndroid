package id.bkkp.general;

public class GetItem {
	private int id, masterId;
	private String name, code, image, price_sell, timeStamp, content, content2, content3, status;

	public GetItem() {
	}

	public GetItem(int id, int masterId, String name, String content, String content2, String content3, String code, String image,
                   String price_sell, String timeStamp, String status) {
		super();
		this.id = id;
		this.masterId = masterId;
		this.name = name;
		this.content = content;
		this.content2 = content2;
		this.content3 = content3;
		this.code = code;
		this.image = image;
		this.price_sell = price_sell;
		this.timeStamp = timeStamp;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMasterId() {
		return masterId;
	}

	public void setMasterId(int masterId) {
		this.masterId = masterId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent2() {
		return content2;
	}

	public void setContent2(String content2) {
		this.content2 = content2;
	}

	public String getContent3() {
		return content3;
	}

	public void setContent3(String content3) {
		this.content3 = content3;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getPrice_sell() {
		return price_sell;
	}

	public void setPrice_sell(String price_sell) {
		this.price_sell = price_sell;
	}
}

package gruppo_nove.smartserver.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePriceRequest {
    private String operationName;
    private double price;

    public UpdatePriceRequest() {}

    public UpdatePriceRequest(String operationName, double price) {
        this.operationName = operationName;
        this.price = price;
    }
    @Override
    public String toString() {
        return "UpdatePriceRequest{" +
                "operationName=" + operationName +
                ", price=" + price +
                '}';
    }

}

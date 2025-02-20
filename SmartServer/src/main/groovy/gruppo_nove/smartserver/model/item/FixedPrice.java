package gruppo_nove.smartserver.model.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FixedPrice {
    private int id;
    private String operationName;
    private double price;

    public FixedPrice() {}

    public FixedPrice(int id, String operationName, double price) {
        this.id = id;
        this.operationName = operationName;
        this.price = price;
    }

    @Override
    public String toString() {
        return "FixedPrice{" +
                "id=" + id +
                ", operationName='" + operationName + '\'' +
                ", price=" + price +
                '}';
    }
}

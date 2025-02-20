package gruppo_nove.smartserver.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaldoRequest {
    private int userId;
    private double amount;

    public SaldoRequest() {}

    public SaldoRequest(int userId, double amount) {
        this.userId = userId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "SaldoRequest{" +
                "userId=" + userId +
                ", amount=" + amount +
                '}';
    }
}

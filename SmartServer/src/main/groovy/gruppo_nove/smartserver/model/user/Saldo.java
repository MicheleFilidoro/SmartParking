package gruppo_nove.smartserver.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class Saldo {
    private int id;
    private double saldo;
    private int userId; // Riferimento all'ID dell'utente

    /**
     * Instantiates a new Saldo.
     *
     * @param id     the id
     * @param saldo  the initial saldo
     * @param userId the ID of the associated user
     */
    public Saldo(int id, double saldo, int userId) {
        this.id = id;
        this.saldo = saldo;
        this.userId = userId;
    }

    /**
     * Adds an amount to the saldo.
     *
     * @param amount the amount to add
     */
    public void aggiungiSaldo(double amount) {
        if (amount > 0) {
            this.saldo += amount;
        }
    }

    /**
     * Deducts an amount from the saldo if sufficient balance exists.
     *
     * @param amount the amount to deduct
     * @throws IllegalArgumentException if saldo is insufficient
     */
    public void rimuoviSaldo(double amount) {
        if (amount > this.saldo) {
            throw new IllegalArgumentException("Saldo insufficiente.");
        }
        this.saldo -= amount;
    }
}

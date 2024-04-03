package ar.com.plug.examen.app.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import javax.persistence.OneToMany;
import java.util.List;

@JsonRootName(value = "shoppingCart")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoppingCartApi {

    @JsonProperty
    private long orderId;
    @JsonProperty
    private int dni;
    @JsonProperty
    @OneToMany
    private List<ProductApi> items;

    public long getOrderId() {
        return orderId;
    }

    public int getDni() {
        return dni;
    }

    public List<ProductApi> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "{" +
                "orderId=" + orderId +
                ", dni=" + dni +
                ", items=" + items +
                '}';
    }
}

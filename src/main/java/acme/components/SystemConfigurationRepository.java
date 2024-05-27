
package acme.components;

import org.springframework.data.jpa.repository.Query;

import acme.client.data.datatypes.Money;
import acme.client.repositories.AbstractRepository;
import acme.entities.currency.Currency;
import acme.entities.systemConfiguration.SystemConfiguration;

public interface SystemConfigurationRepository extends AbstractRepository {

	@Query("select sc from SystemConfiguration sc")
	SystemConfiguration findSystemConfiguration();

	@Query("select c from Currency c where c.symbol=:symbol")
	Currency findCurrencyByName(String symbol);

	default Money convertToUsd(final Money money) {
		Currency actualCurrency = this.findCurrencyByName(money.getCurrency());
		Money res = new Money();
		res.setCurrency("USD");

		res.setAmount(money.getAmount() * actualCurrency.getValueAgainstDollar());

		return res;
	}

	default Money convertToCurrency(final Money moneyInUsd, final String symbol) {
		Currency actualCurrency = this.findCurrencyByName(symbol);
		Money res = new Money();
		res.setCurrency(symbol);

		res.setAmount(moneyInUsd.getAmount() / actualCurrency.getValueAgainstDollar());

		return res;
	}

	default Money convertFromCurrencyToAnother(final Money money, final String symbol) {
		Money moneyInUsd = this.convertToUsd(money);
		Money res = this.convertToCurrency(moneyInUsd, symbol);
		return res;
	}

	default boolean existsCurrency(final String symbol) {
		Currency c = this.findCurrencyByName(symbol);
		return c != null;
	}

}

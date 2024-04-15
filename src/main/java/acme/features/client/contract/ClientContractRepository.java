
package acme.features.client.contract;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.data.datatypes.Money;
import acme.client.repositories.AbstractRepository;
import acme.entities.contracts.Contract;
import acme.entities.projects.Project;
import acme.roles.Client;

@Repository
public interface ClientContractRepository extends AbstractRepository {

	@Query("select c from Contract c where c.client.id = :id")
	Collection<Contract> findContractsByClientId(int id);

	@Query("select c from Contract c where c.id = :id")
	Contract findContractById(int id);

	@Query("select c from Contract c where c.code = :code")
	Contract findContractByCode(String code);

	@Query("select p from Project p")
	Collection<Project> findlAllProjects();

	@Query("select c from Client c where c.id = :id")
	Client findClientById(int id);

	@Query("select p from Project p where p.id = :id")
	Project findProjectById(int id);

	//TODO ARREGLAR ERROR EN CONVERSOR ORIGINAL
	default double currencyTransformer(final Money initial, final String currency) {
		double res = initial.getAmount();
		ArrayList<Double> factor = new ArrayList<>();
		factor.add(1.0);
		factor.add(1.07);
		factor.add(0.83);//EURtoEUR, EURtoUSD, EURtoGBP

		factor.add(0.93);
		factor.add(1.00);
		factor.add(0.75);//USDtoEUR, USDtoUSD, USDtoGBP

		factor.add(1.17);
		factor.add(1.25);
		factor.add(1.0);//GBtoEUR, GBPtoUSD, GBPtoGBP

		ArrayList<String> lista = new ArrayList<>();
		lista.add("EUR");
		lista.add("USD");
		lista.add("GBP");

		for (int i = 0; i < lista.size(); i++)
			for (int j = 0; j < lista.size(); j++)
				if (lista.get(i).equals(initial.getCurrency()) && lista.get(j).equals(currency)) {
					System.out.println(factor.get(i + j));
					res = initial.getAmount() * factor.get(i + j);
				}
		return res;
	}

	default double currencyTransformerUsd(final Money initial) {
		double res = initial.getAmount();

		if (initial.getCurrency().equals("USD"))
			res = initial.getAmount();

		else if (initial.getCurrency().equals("EUR"))
			res = initial.getAmount() * 1.07;

		else
			res = initial.getAmount() * 1.25;

		return res;
	}
}

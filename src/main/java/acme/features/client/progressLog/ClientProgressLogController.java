
package acme.features.client.progressLog;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractController;
import acme.entities.progress_logs.ProgressLog;
import acme.roles.Client;

@Controller
public class ClientProgressLogController extends AbstractController<Client, ProgressLog> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private ClientProgressLogListService listService;

	//@Autowired
	//private ClientContractShowService	showService;

	//@Autowired
	//private ClientContractCreateService	createService;

	//@Autowired
	//private ClientContractUpdateService	updateService;

	//@Autowired
	//private ClientContractDeleteService	deleteService;


	// Constructors -----------------------------------------------------------
	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		//super.addBasicCommand("show", this.showService);
		//super.addBasicCommand("create", this.createService);
		//super.addBasicCommand("update", this.updateService);
		//super.addBasicCommand("delete", this.deleteService);
	}

}

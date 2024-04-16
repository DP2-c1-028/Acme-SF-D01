
package acme.features.developer.trainingModule;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractController;
import acme.entities.training_modules.TrainingModule;
import acme.roles.Developer;

@Controller
public class DeveloperTrainingModuleController extends AbstractController<Developer, TrainingModule> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private DeveloperTrainingModuleListService	listService;

	@Autowired
	private DeveloperTrainingModuleShowService	showService;

	@Autowired
	DeveloperTrainingModuleCreateService		createService;

	@Autowired
	DeveloperTrainingModuleUpdateService		updateService;

	@Autowired
	DeveloperTrainingModuleDeleteService		deleteService;

	@Autowired
	DeveloperTrainingModulePublishService		publishService;


	// Constructors -----------------------------------------------------------
	@PostConstruct
	protected void initialise() {
		super.addBasicCommand("list", this.listService);
		super.addBasicCommand("show", this.showService);
		super.addBasicCommand("create", this.createService);
		super.addBasicCommand("update", this.updateService);
		super.addBasicCommand("delete", this.deleteService);
		super.addCustomCommand("publish", "update", this.publishService);
	}
}


package acme.features.developer.trainingSession;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.client.controllers.AbstractController;
import acme.entities.training_modules.TrainingSession;
import acme.roles.Developer;

@Controller
public class DeveloperTrainingSessionController extends AbstractController<Developer, TrainingSession> {

	// Internal state ---------------------------------------------------------
	@Autowired
	private DeveloperTrainingSessionListService	listService;

	@Autowired
	private DeveloperTrainingSessionShowService	showService;

	@Autowired
	DeveloperTrainingSessionCreateService		createService;

	@Autowired
	DeveloperTrainingSessionUpdateService		updateService;

	@Autowired
	DeveloperTrainingSessionDeleteService		deleteService;

	@Autowired
	DeveloperTrainingSessionPublishService		publishService;


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

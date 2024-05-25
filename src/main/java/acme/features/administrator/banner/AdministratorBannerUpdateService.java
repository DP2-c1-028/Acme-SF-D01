
package acme.features.administrator.banner;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.client.data.accounts.Administrator;
import acme.client.data.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractService;
import acme.entities.banners.Banner;

@Service
public class AdministratorBannerUpdateService extends AbstractService<Administrator, Banner> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorBannerRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Banner object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneBannerById(id);

		Date instantiationMoment;
		instantiationMoment = MomentHelper.getCurrentMoment();
		object.setInstantiationMoment(instantiationMoment);

		super.getBuffer().addData(object);
	}

	@Override
	public void bind(final Banner object) {
		assert object != null;

		super.bind(object, "bannerStartTime", "bannerEndTime", "picture", "slogan", "link");
		Date instantiationMoment;
		Date currentMoment;

		currentMoment = MomentHelper.getCurrentMoment();
		instantiationMoment = new Date(currentMoment.getTime() - 1000);
		object.setInstantiationMoment(instantiationMoment);
	}

	@Override
	public void validate(final Banner object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("bannerStartTime") && object.getBannerStartTime() != null) {
			Date bannerStartTime;
			Date instantiationMoment;
			bannerStartTime = object.getBannerStartTime();
			instantiationMoment = object.getInstantiationMoment();

			if (bannerStartTime != null && instantiationMoment != null)
				super.state(bannerStartTime.after(instantiationMoment), "bannerStartTime", "administrator.banner.form.error.banner-start-time");
		}

		if (!super.getBuffer().getErrors().hasErrors("bannerEndTime") && object.getBannerEndTime() != null) {
			Date bannerStartTime;
			Date bannerEndTime;

			bannerStartTime = object.getBannerStartTime();
			bannerEndTime = object.getBannerEndTime();

			if (bannerStartTime != null && bannerEndTime != null)
				super.state(MomentHelper.isLongEnough(bannerStartTime, bannerEndTime, 1, ChronoUnit.WEEKS) && bannerEndTime.after(bannerStartTime), "bannerEndTime", "administrator.banner.form.error.banner-end-time");
		}

		if (!super.getBuffer().getErrors().hasErrors("bannerStartTime")) {

			Date bannerDate = object.getBannerStartTime();
			Date maximumDate = MomentHelper.parse("2100-01-01 00:00", "yyyy-MM-dd HH:mm");

			Boolean isBefore = bannerDate.before(maximumDate);
			super.state(isBefore, "bannerStartTime", "administrator.banner.form.error.banner-start-time-maximum");
		}
	}

	@Override
	public void perform(final Banner object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final Banner object) {
		assert object != null;
		Dataset dataset;
		dataset = super.unbind(object, "bannerStartTime", "bannerEndTime", "picture", "slogan", "link");
		dataset.put("instantiationMoment", MomentHelper.getCurrentMoment());

		super.getResponse().addData(dataset);
	}

}

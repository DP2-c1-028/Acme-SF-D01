
package acme.components;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.helpers.MomentHelper;
import acme.client.helpers.RandomHelper;
import acme.client.repositories.AbstractRepository;
import acme.entities.banners.Banner;

@Repository
public interface BannerRepository extends AbstractRepository {

	@Query("select count(a) from Banner a where a.bannerEndTime > :date and a.bannerStartTime < :date")
	int countValidBanners(Date date);

	@Query("select a from Banner a")
	List<Banner> findAllBanners(PageRequest pageRequest);

	@Query("select a from Banner a where a.bannerEndTime > :date and a.bannerStartTime < :date")
	List<Banner> findAllValidBannersByDate(PageRequest pageRequest, Date date);

	default Banner findRandomBanner() {
		Banner result;
		int count;
		int index;
		PageRequest page;
		List<Banner> list;

		count = this.countValidBanners(MomentHelper.getCurrentMoment());
		if (count == 0)
			result = null;
		else {
			index = RandomHelper.nextInt(0, count);

			page = PageRequest.of(index, 1, Sort.by(Direction.ASC, "id"));
			list = this.findAllValidBannersByDate(page, MomentHelper.getCurrentMoment());
			result = list.isEmpty() ? null : list.get(0);
		}

		return result;
	}
}

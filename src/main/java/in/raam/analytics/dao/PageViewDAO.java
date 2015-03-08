package in.raam.analytics.dao;

import in.raam.analytics.model.PageView;

import java.util.List;

/**
 * DAO interface to fetch PageView objects from underlying storage
 * @author ramasubramanian on 07/03/15.
 */
public interface PageViewDAO {
    List<PageView> fetchPageViewsByUser(String userId, int limit);
}
